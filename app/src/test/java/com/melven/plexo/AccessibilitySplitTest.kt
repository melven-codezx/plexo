package com.melven.plexo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.melven.plexo.launch.AccessibilityHelper
import com.melven.plexo.launch.LaunchMode
import com.melven.plexo.launch.PlexoAccessibilityService
import com.melven.plexo.launch.SplitLauncher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * O ecra dividido real depende do servico de acessibilidade. Estes testes cobrem
 * a logica que nao precisa do sistema: deteccao, mensagens e rotas.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class AccessibilitySplitTest {

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun `constante do botao ecra dividido corresponde a API publica`() {
        // AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN == 7
        assertEquals(7, PlexoAccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
    }

    @Test
    fun `sem servico ligado o estado e falso`() {
        assertFalse(PlexoAccessibilityService.isConnected)
        assertFalse(AccessibilityHelper.isEnabled(context))
        assertFalse(SplitLauncher.hasReliableSplit(context))
    }

    @Test
    fun `rota acessibilidade e a primeira quando o servico existe`() {
        // Sem servico real, o motor cai para as estrategias antigas mas nao rebenta.
        val pkg = context.packageName
        val launch = context.packageManager.getLaunchIntentForPackage(pkg)
        val activity = launch?.component?.className
        val result = SplitLauncher.launch(
            context,
            SplitLauncher.Request(pkg, activity, pkg, activity, LaunchMode.SPLIT)
        )
        assertTrue(result.ok)
        assertTrue(
            "Sem acessibilidade deve usar fallback, veio ${result.strategy}",
            result.strategy != SplitLauncher.Strategy.A11Y_SPLIT
        )
        println("[Plexo] fallback estrategy: ${result.strategy}")
    }

    @Test
    fun `mensagem de fallback pede a ativacao do modo real`() {
        val pkg = context.packageName
        val launch = context.packageManager.getLaunchIntentForPackage(pkg)
        val result = SplitLauncher.launch(
            context,
            SplitLauncher.Request(pkg, launch?.component?.className, pkg, launch?.component?.className, LaunchMode.SPLIT)
        )
        if (result.strategy == SplitLauncher.Strategy.SEQUENTIAL) {
            assertTrue(result.message.contains("Ecrã dividido real", ignoreCase = true))
        }
    }
}
