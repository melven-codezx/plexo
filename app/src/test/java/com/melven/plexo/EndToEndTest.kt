package com.melven.plexo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.melven.plexo.data.Pair
import com.melven.plexo.launch.LaunchMode
import com.melven.plexo.launch.SplitLauncher
import com.melven.plexo.widget.PlexoWidgetProvider
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Teste ponta-a-ponta com apps reais do ambiente simulado: usa a propria app como
 * "app A" e "app B" (tem activity LAUNCHER valida) e verifica que o motor percorre
 * as estrategias sem explodir.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class EndToEndTest {

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun `motor abre par usando atividades resolviveis`() {
        val pkg = context.packageName
        val launchable = context.packageManager.getLaunchIntentForPackage(pkg)
        assertNotNull("A app deve ter activity de arranque", launchable)
        val activity = launchable!!.component?.className!!

        val result = SplitLauncher.launch(
            context,
            SplitLauncher.Request(pkg, activity, pkg, activity, LaunchMode.SPLIT)
        )
        assertTrue("Esperava sucesso, veio: ${result.message}", result.ok)
        println("[Plexo] estrategia escolhida: ${result.strategy} -> ${result.message}")
    }

    @Test
    fun `persistencia de pares sobrevive a releitura`() {
        val app = ApplicationProvider.getApplicationContext<PlexoApp>()
        val store = app.pairStore
        val pair = Pair(
            id = "zz1", name = "Teste + Persistência",
            pkgA = "com.a", pkgB = "com.b", mode = LaunchMode.FLOATING,
            lastUsed = 42L
        )
        store.savePairs(listOf(pair))
        val reloaded = store.pairs()
        assertTrue(reloaded.any { it.name == pair.name && it.mode == LaunchMode.FLOATING })

        // upsert do mesmo par nao duplica
        store.upsert(pair.copy(id = "zz2"))
        assertTrue(store.pairs().count { it.pkgA == "com.a" && it.pkgB == "com.b" } == 1)

        // apagar
        store.delete(reloaded.first().id)
        assertTrue(store.pairs().none { it.pkgA == "com.a" })
    }

    @Test
    fun `widget constroi vistas sem rebentar`() {
        val views = PlexoWidgetProvider.buildViews(context)
        assertNotNull(views)
        println("[Plexo] RemoteViews do widget construidas com sucesso")
    }
}
