package com.melven.plexo.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.melven.plexo.PlexoApp
import com.melven.plexo.data.AppEntry
import com.melven.plexo.data.Pair
import com.melven.plexo.launch.LaunchMode
import com.melven.plexo.launch.SplitLauncher
import com.melven.plexo.util.NameSuggester

/** Estado e acoes do ecra principal. */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app get() = getApplication<Application>()
    private val store get() = PlexoApp.instance.pairStore
    private val catalog get() = PlexoApp.instance.catalog

    var apps by mutableStateOf<List<AppEntry>>(emptyList())
        private set

    var pairs by mutableStateOf<List<Pair>>(emptyList())
        private set

    var pkgA by mutableStateOf<String?>(null)
        private set

    var pkgB by mutableStateOf<String?>(null)
        private set

    var mode by mutableStateOf(LaunchMode.SPLIT)
        private set

    var message by mutableStateOf<UiMessage?>(null)
        private set

    data class UiMessage(val text: String, val isError: Boolean = false)

    init {
        refreshCatalog()
        val selection = store.selection()
        pkgA = selection.pkgA.takeIf { catalog.isInstalled(it) }
        pkgB = selection.pkgB.takeIf { catalog.isInstalled(it) }
        mode = selection.mode
        pairs = store.pairs()
    }

    fun refreshCatalog() {
        catalog.invalidate()
        apps = catalog.apps()
        // Se uma app seleccionada foi desinstalada, limpa a seleccao.
        if (pkgA != null && !catalog.isInstalled(pkgA)) pkgA = null
        if (pkgB != null && !catalog.isInstalled(pkgB)) pkgB = null
    }

    fun labelOf(pkg: String?): String? = pkg?.let { catalog.label(it) }

    fun entryOf(pkg: String?): AppEntry? = pkg?.let { catalog.find(it) }

    fun select(slot: Int, pkg: String) {
        when (slot) {
            0 -> pkgA = pkg
            1 -> pkgB = pkg
        }
        persistSelection()
    }

    fun clear(slot: Int) {
        when (slot) {
            0 -> pkgA = null
            1 -> pkgB = null
        }
        persistSelection()
    }

    fun swap() {
        val a = pkgA
        pkgA = pkgB
        pkgB = a
        persistSelection()
    }

    fun updateMode(value: LaunchMode) {
        mode = value
        persistSelection()
    }

    fun suggestName(): String {
        val a = labelOf(pkgA) ?: "App"
        val b = labelOf(pkgB) ?: "App"
        return NameSuggester.suggest(a, b)
    }

    fun canLaunch(): Boolean = pkgA != null && pkgB != null

    fun launchSelection() {
        if (!canLaunch()) {
            message = UiMessage("Escolha as duas apps primeiro.", isError = true)
            return
        }
        launchPair(pkgA!!, pkgB!!, mode, setAsLastUsed = null)
    }

    fun launchSaved(pair: Pair) {
        store.touch(pair.id)
        pairs = store.pairs()
        launchPair(pair.pkgA, pair.pkgB, pair.mode, setAsLastUsed = pair.id)
    }

    private fun launchPair(a: String, b: String, launchMode: LaunchMode, setAsLastUsed: String?) {
        val request = SplitLauncher.Request(
            pkgA = a,
            actA = entryOf(a)?.activityName,
            pkgB = b,
            actB = entryOf(b)?.activityName,
            mode = launchMode
        )
        val result = SplitLauncher.launch(app, request)
        message = UiMessage(result.message, isError = !result.ok)
        if (result.ok && setAsLastUsed != null) {
            com.melven.plexo.widget.PlexoWidgetProvider.refresh(app)
        }
    }

    fun savePair(name: String) {
        if (!canLaunch()) return
        val pair = Pair(
            id = store.newId(),
            name = name.trim().ifBlank { suggestName() },
            pkgA = pkgA!!,
            pkgB = pkgB!!,
            mode = mode,
            lastUsed = System.currentTimeMillis()
        )
        pairs = store.upsert(pair)
        message = UiMessage("Par \u201c${pair.name}\u201d guardado.")
        com.melven.plexo.widget.PlexoWidgetProvider.refresh(app)
    }

    fun deletePair(pair: Pair) {
        pairs = store.delete(pair.id)
        message = UiMessage("Par removido.")
        com.melven.plexo.widget.PlexoWidgetProvider.refresh(app)
    }

    fun openNativeSplitHelp() {
        message = UiMessage("Dica: no ecrã de recentes, mantenha uma app premida e escolha \u201cEcrã dividido\u201d.")
    }

    fun consumeMessage() {
        message = null
    }

    private fun persistSelection() {
        store.saveSelection(pkgA, pkgB, mode)
    }
}
