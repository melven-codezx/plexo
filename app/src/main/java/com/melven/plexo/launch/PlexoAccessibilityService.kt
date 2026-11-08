package com.melven.plexo.launch

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo

/**
 * Serviço de acessibilidade do Plexo — é o que torna o ecrã dividido REAL em Android 12+.
 *
 * Porque é necessário:
 *  - Desde o Android 12 (e sobretudo no 12L/13/14), o sistema ignora pedidos de
 *    `WINDOWING_MODE_SPLIT_SCREEN_PRIMARY` feitos por apps normais, e bloqueia também o
 *    arranque de activities a partir do background. O resultado é o que o utilizador vê:
 *    só a última app abre, em ecrã inteiro.
 *
 * O que este serviço faz:
 *  1. Recebe um comando do Plexo (broadcast interno, protegido por assinatura de pacote).
 *  2. Abre a app A (fica em ecrã inteiro).
 *  3. Usa `GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN` para o sistema dividir o ecrã
 *     (esta ação é a mesma do botão dos recentes — permitida a serviços de acessibilidade).
 *  4. Espera o divisor aparecer e abre a app B com `FLAG_ACTIVITY_LAUNCH_ADJACENT`,
 *     que o sistema coloca na janela adjacente.
 *
 * O serviço não lê conteúdo: `canRetrieveWindowContent=false`, `flagDefault`, sem
 * `flagRequestTouchExplorationMode`. Apenas usa ações globais e observa a lista de janelas.
 */
class PlexoAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var pendingJob: Runnable? = null
    private var waitingForDivider = false
    private var attemptsWithDividerCheck = 0

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i(TAG, "Serviço de acessibilidade ligado")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Só nos interessa quando estamos à espera do divisor.
        if (!waitingForDivider) return
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOWS_CHANGED) return
        if (hasSplitDivider()) {
            waitingForDivider = false
            val job = pendingJob ?: return
            handler.postDelayed(job, SPLIT_SETTLE_MS)
        }
    }

    override fun onInterrupt() {
        cancel()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        cancel()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        instance = null
        cancel()
        super.onDestroy()
    }

    private fun cancel() {
        pendingJob?.let { handler.removeCallbacks(it) }
        pendingJob = null
        waitingForDivider = false
    }

    /** true se existir uma janela do tipo divisor de ecrã dividido. */
    fun hasSplitDivider(): Boolean = runCatching {
        windows.orEmpty().any { it.type == AccessibilityWindowInfo.TYPE_SPLIT_SCREEN_DIVIDER }
    }.getOrDefault(false)

    /**
     * Executa: abre A -> divide -> abre B na metade adjacente.
     */
    fun launchPair(pkgA: String, actA: String?, pkgB: String, actB: String?) {
        cancel()
        attemptsWithDividerCheck = 0

        val intentA = SplitLauncher.baseIntent(this, pkgA, actA)
        val intentB = SplitLauncher.baseIntent(this, pkgB, actB)
        if (intentA == null || intentB == null) {
            Log.w(TAG, "App em falta (A=${intentA != null}, B=${intentB != null})")
            return
        }

        // 1) App A em ecrã inteiro.
        runCatching {
            startActivity(
                intentA.apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    )
                }
            )
        }.onFailure { Log.w(TAG, "Falha ao abrir A", it) }

        // 2) Dividir o ecrã passado um instante (a app A tem de estar visível).
        handler.postDelayed({ enterSplitAndOpenSecond(intentA, intentB) }, STAGE_DELAY_MS)
    }

    private fun enterSplitAndOpenSecond(intentA: Intent, intentB: Intent) {
        if (hasSplitDivider()) {
            openAdjacent(intentB)
            return
        }

        waitingForDivider = true
        attemptsWithDividerCheck++

        val toggled = performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
        Log.i(TAG, "TOGGLE_SPLIT_SCREEN enviado: $toggled (tentativa $attemptsWithDividerCheck)")

        // 3) Plano A: assim que o divisor aparecer, abre B ao lado (ver onAccessibilityEvent).
        //    Plano B: se não houver evento, tenta de qualquer forma após um atraso.
        pendingJob = Runnable {
            waitingForDivider = false
            openAdjacent(intentB)
        }.also { handler.postDelayed(it, DIVIDER_TIMEOUT_MS) }
    }

    private fun openAdjacent(intentB: Intent) {
        val flags = Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
            Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT
        val opened = runCatching {
            startActivity(Intent(intentB).apply { addFlags(flags) })
        }.isSuccess

        Log.i(TAG, "App B adjacente aberta: $opened")

        // 4) Segunda passagem: se o aparelho aceitou o split mas a app B caiu na mesma
        //    janela, tenta mais uma vez (alguns fabricantes precisam de 2 chamadas).
        if (!opened && attemptsWithDividerCheck < 2) {
            handler.postDelayed({
                if (hasSplitDivider()) openAdjacent(intentB)
            }, RETRY_DELAY_MS)
        }
    }

    companion object {
        private const val TAG = "PlexoA11y"

        /** Atraso antes de pedir a divisão (deixar a app A desenhar). */
        private const val STAGE_DELAY_MS = 900L

        /** Espera máxima pelo divisor após o toggle. */
        private const val DIVIDER_TIMEOUT_MS = 1800L

        /** Tempo de acomodação depois de o divisor aparecer. */
        private const val SPLIT_SETTLE_MS = 450L

        private const val RETRY_DELAY_MS = 700L

        @Volatile
        var instance: PlexoAccessibilityService? = null
            private set

        val isConnected: Boolean
            get() = instance != null

        /** Ação usada pelo Android 12+ (constante pública desde a API 24). */
        const val GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN = 7

        fun splitSupportedOnThisDevice(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }
}
