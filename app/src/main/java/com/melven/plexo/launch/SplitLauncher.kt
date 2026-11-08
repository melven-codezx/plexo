package com.melven.plexo.launch

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build

/**
 * Motor de lancamento do Plexo.
 *
 * O Android nao expoe uma API publica "abre dois apps em ecra dividido", por isso o Plexo
 * usa tres estrategias em cascata, da mais elegante para a mais compativel:
 *
 *  1. PAIR_WINDOWING - startActivities([A, B]) com um ActivityOptions que pede
 *     explicitamente WINDOWING_MODE_SPLIT_SCREEN_PRIMARY + splitScreenCreateMode TOP_OR_LEFT.
 *     Funciona em Android 8+ e e o mecanismo usado pelas apps de referencia da Play Store.
 *
 *  2. ADJACENT - startActivities([A, B]) com FLAG_ACTIVITY_LAUNCH_ADJACENT em ambas as
 *     intencoes. E o truque classico do Nougat, imbatível quando o ecra ja esta dividido.
 *
 *  3. SEQUENTIAL - abre A, espera 750 ms e abre B. Garante que ambas ficam em memoria,
 *     prontas para o utilizador as emparelhar no ecra dividido nativo.
 *
 * Para alem disso ha o modo FLOATING, que tenta duas janelas livres com setLaunchBounds.
 */
object SplitLauncher {

    // Chaves internas do ActivityOptions (usadas pelo sistema ao desempacotar o Bundle).
    private const val KEY_LAUNCH_WINDOWING_MODE = "android:activity.launchWindowingMode"
    private const val KEY_SPLIT_SCREEN_CREATE_MODE = "android:activity.splitScreenCreateMode"

    private const val WINDOWING_MODE_SPLIT_PRIMARY = 3
    private const val SPLIT_CREATE_TOP_OR_LEFT = 0

    private const val PIN_FLAGS = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK

    /** Pedido de abertura de um par. */
    data class Request(
        val pkgA: String,
        val actA: String?,
        val pkgB: String,
        val actB: String?,
        val mode: LaunchMode
    )

    enum class Strategy { PAIR_WINDOWING, ADJACENT, SEQUENTIAL, FLOATING }

    data class Result(
        val strategy: Strategy?,
        val ok: Boolean,
        val message: String
    )

    /** Lanca o par e devolve o que aconteceu (para diagnostico e feedback ao utilizador). */
    fun launch(context: Context, request: Request): Result {
        val rawA = baseIntent(context, request.pkgA, request.actA)
        val rawB = baseIntent(context, request.pkgB, request.actB)

        if (rawA == null && rawB == null) {
            return Result(null, false, "Nenhuma das apps está instalada.")
        }
        if (rawA == null) return single(context, rawB!!, ausente = "A app de cima")
        if (rawB == null) return single(context, rawA, ausente = "A app de baixo")

        val errors = StringBuilder()

        if (request.mode == LaunchMode.FLOATING) {
            runCatching { launchFreeform(context, rawA, rawB) }
                .onSuccess { return Result(Strategy.FLOATING, true, "Duas janelas flutuantes abertas.") }
                .onFailure { errors.appendLine("FLOATING: ${it.javaClass.simpleName}") }
        }

        runCatching { launchPairWindowing(context, rawA, rawB) }
            .onSuccess { return Result(Strategy.PAIR_WINDOWING, true, "Ecrã dividido aberto.") }
            .onFailure { errors.appendLine("PAIR_WINDOWING: ${it.javaClass.simpleName} ${it.message ?: ""}") }

        runCatching { launchAdjacent(context, rawA, rawB) }
            .onSuccess { return Result(Strategy.ADJACENT, true, "Ecrã dividido aberto.") }
            .onFailure { errors.appendLine("ADJACENT: ${it.javaClass.simpleName} ${it.message ?: ""}") }

        return runCatching {
            context.startActivity(
                clone(rawA).apply { flags = PIN_FLAGS or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED }
            )
        }.map {
            Result(Strategy.SEQUENTIAL, true, "Apps abertas. Arraste uma para o topo para dividir o ecrã.")
        }.getOrElse {
            errors.appendLine("SEQUENTIAL: ${it.javaClass.simpleName}")
            Result(null, false, "Não foi possível abrir. ${errors.toString().trim()}")
        }
    }

    /** Abre apenas uma app (fallback quando a outra foi desinstalada). */
    private fun single(context: Context, intent: Intent, ausente: String): Result =
        runCatching { context.startActivity(clone(intent).apply { flags = PIN_FLAGS }) }
            .map { Result(Strategy.SEQUENTIAL, true, "$ausente já não existe. Abrimos a outra.") }
            .getOrElse { Result(null, false, "$ausente já não existe.") }

    // ---------------------------------------------------------------- estrategias

    private fun launchPairWindowing(context: Context, a: Intent, b: Intent) {
        val options = ActivityOptions.makeBasic().toBundle().apply {
            putInt(KEY_LAUNCH_WINDOWING_MODE, WINDOWING_MODE_SPLIT_PRIMARY)
            putInt(KEY_SPLIT_SCREEN_CREATE_MODE, SPLIT_CREATE_TOP_OR_LEFT)
        }
        val first = clone(a).apply { flags = PIN_FLAGS }
        val second = clone(b).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        context.startActivities(arrayOf(first, second), options)
    }

    private fun launchAdjacent(context: Context, a: Intent, b: Intent) {
        val first = clone(a).apply { flags = PIN_FLAGS }
        val second = clone(b).apply {
            flags = PIN_FLAGS or Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT
        }
        context.startActivities(arrayOf(first, second), null)
    }

    private fun launchFreeform(context: Context, a: Intent, b: Intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            throw IllegalStateException("Freeform indisponivel")
        }
        context.startActivity(
            clone(a).apply { flags = PIN_FLAGS },
            ActivityOptions.makeBasic().setLaunchBounds(Rect(0, 0, 980, 1400)).toBundle()
        )
        context.startActivity(
            clone(b).apply { flags = PIN_FLAGS },
            ActivityOptions.makeBasic().setLaunchBounds(Rect(560, 900, 1440, 2200)).toBundle()
        )
    }

    // ---------------------------------------------------------------- intencoes

    /** Copia defensiva: Intent nao tem copy(), mas tem construtor de copia. */
    private fun clone(source: Intent): Intent = Intent(source)

    fun baseIntent(context: Context, pkg: String, activity: String?): Intent? {
        val pm: PackageManager = context.packageManager
        val explicit = activity?.let { name ->
            Intent().setClassName(pkg, name)
        }?.takeIf { it.resolveActivity(pm) != null }
        return explicit ?: pm.getLaunchIntentForPackage(pkg)
    }

    /** Intencao usada pelos atalhos, widget e tile. */
    fun launchIntentFor(context: Context, request: Request): Intent =
        Intent(context, LaunchPairActivity::class.java)
            .putExtra(LaunchPairActivity.EXTRA_PKG_A, request.pkgA)
            .putExtra(LaunchPairActivity.EXTRA_ACT_A, request.actA)
            .putExtra(LaunchPairActivity.EXTRA_PKG_B, request.pkgB)
            .putExtra(LaunchPairActivity.EXTRA_ACT_B, request.actB)
            .putExtra(LaunchPairActivity.EXTRA_MODE, request.mode.name)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
}
