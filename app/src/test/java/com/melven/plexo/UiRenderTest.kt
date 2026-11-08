package com.melven.plexo

import android.graphics.Bitmap
import androidx.core.view.drawToBitmap
import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.melven.plexo.data.AppEntry
import com.melven.plexo.data.Pair
import com.melven.plexo.launch.LaunchMode
import com.melven.plexo.ui.AppPickerSheet
import com.melven.plexo.ui.HomeViewModel
import com.melven.plexo.ui.PlexoRoot
import com.melven.plexo.ui.SavePairDialog
import com.melven.plexo.ui.theme.PlexoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

/**
 * Testes de renderizacao: montam a UI Compose num ambiente Android simulado (Robolectric),
 * garantem que o ecra desenha sem excepcoes e guardam capturas em build/screenshots/.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class UiRenderTest {

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private fun save(bitmap: Bitmap, name: String) {
        val dir = File("build/screenshots").apply { mkdirs() }
        FileOutputStream(File(dir, "$name.png")).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        // O decorView inclui o conteudo Compose e quaisquer sobreposicoes (toast, seletor).
        save(compose.activity.window.decorView.drawToBitmap(), name)
    }

    private fun seedStore() {
        val store = ApplicationProvider.getApplicationContext<PlexoApp>().pairStore
        store.saveSelection("com.android.chrome", "com.google.android.dialer", LaunchMode.SPLIT)
        store.savePairs(
            listOf(
                Pair(
                    id = "p1", name = "Trabalho + Música",
                    pkgA = "com.android.chrome", pkgB = "com.google.android.youtube",
                    mode = LaunchMode.SPLIT, lastUsed = 2L
                ),
                Pair(
                    id = "p2", name = "Notas + Chat",
                    pkgA = "com.google.android.keep", pkgB = "com.whatsapp",
                    mode = LaunchMode.FLOATING, lastUsed = 1L
                ),
                Pair(
                    id = "p3", name = "Mapas + Chamadas",
                    pkgA = "com.google.android.dialer", pkgB = "com.android.chrome",
                    mode = LaunchMode.SPLIT, lastUsed = 0L
                )
            )
        )
    }

    @Test
    fun homeRenderizaSemErros() {
        com.melven.plexo.ui.Motion.reduceMotion = true
        seedStore()
        val vm = HomeViewModel(ApplicationProvider.getApplicationContext())
        compose.activity.setContent { PlexoTheme { PlexoRoot(vm) } }
        capture("01-home")
    }

    @Test
    fun pickerDeAppsRenderiza() {
        com.melven.plexo.ui.Motion.reduceMotion = true
        val apps = listOf(
            AppEntry("com.android.chrome", "Chrome", "Chrome", false),
            AppEntry("com.whatsapp", "WhatsApp", "WhatsApp", false),
            AppEntry("com.google.android.youtube", "YouTube", "YouTube", false),
            AppEntry("com.google.android.keep", "Keep notes", "Keep notes", false),
            AppEntry("com.google.android.dialer", "Phone", "Phone", true)
        )
        compose.activity.setContent {
            PlexoTheme {
                AppPickerSheet(
                    apps = apps,
                    selectedPkg = "com.android.chrome",
                    otherPkg = "com.whatsapp",
                    title = "App de cima",
                    onDismiss = {},
                    onSelect = {}
                )
            }
        }
        capture("02-seletor")
    }

    @Test
    fun cartaoDeAtivacaoRenderiza() {
        com.melven.plexo.ui.Motion.reduceMotion = true
        compose.activity.setContent {
            PlexoTheme {
                androidx.compose.foundation.layout.Column {
                    com.melven.plexo.ui.SplitPermissionCard(enabled = false, onActivate = {}, onHelp = {})
                    com.melven.plexo.ui.SplitPermissionCard(enabled = true, onActivate = {}, onHelp = {})
                }
            }
        }
        capture("04-ativacao")
    }

    @Test
    fun dialogoDeGuardarRenderiza() {
        com.melven.plexo.ui.Motion.reduceMotion = true
        compose.activity.setContent {
            PlexoTheme { SavePairDialog("Chrome + WhatsApp", onDismiss = {}, onSave = {}) }
        }
        capture("03-dialogo")
    }
}
