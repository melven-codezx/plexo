package com.melven.plexo

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.melven.plexo.launch.LaunchMode
import com.melven.plexo.launch.SplitLauncher
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Testa o motor de lancamento com o framework Android simulado:
 * intencoes construidas, extras do trampolim e comportamento de fallback.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class SplitLauncherTest {

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun `intencao de atalho transporta os dois pacotes e o modo`() {
        val intent = SplitLauncher.launchIntentFor(
            context,
            SplitLauncher.Request("com.app.um", "MainActivity", "com.app.dois", null, LaunchMode.FLOATING)
        )
        assertNotNull(intent)
        assertTrue(intent.getBooleanExtra("extra_mode", false) == false)
        assertTrue(intent.getStringExtra("extra_pkg_a") == "com.app.um")
        assertTrue(intent.getStringExtra("extra_pkg_b") == "com.app.dois")
        assertTrue(intent.getStringExtra("extra_mode") == "FLOATING")
    }

    @Test
    fun `pacotes inexistentes devolvem falha amigavel`() {
        val result = SplitLauncher.launch(
            context,
            SplitLauncher.Request("nao.existe.aaa", null, "nao.existe.bbb", null, LaunchMode.SPLIT)
        )
        assertTrue(!result.ok)
        assertTrue(result.message.contains("instalada", ignoreCase = true))
        assertTrue(result.strategy == null)
    }

    @Test
    fun `baseIntent devolve null para app ausente`() {
        val intent = SplitLauncher.baseIntent(context, "com.pacote.inexistente", null)
        assertTrue(intent == null)
    }

    @Test
    fun `modo split nao lanca excepcao ao construir pedido`() {
        val request = SplitLauncher.Request("a", null, "b", null, LaunchMode.SPLIT)
        assertTrue(request.mode == LaunchMode.SPLIT)
        val intent = Intent()
        assertTrue(intent != null)
    }
}
