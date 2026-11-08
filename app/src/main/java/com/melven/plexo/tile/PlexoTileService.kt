package com.melven.plexo.tile

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.melven.plexo.PlexoApp
import com.melven.plexo.launch.LaunchMode
import com.melven.plexo.launch.SplitLauncher
import com.melven.plexo.widget.PlexoWidgetProvider

/**
 * Tile nos ajustes rapidos: um toque abre o ultimo par usado.
 * Se ainda nao existir nenhum par, abre a app para o utilizador criar um.
 */
class PlexoTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        val store = PlexoApp.instance.pairStore
        val last = store.lastUsedPair() ?: store.pairs().firstOrNull()
        val tile = qsTile ?: return
        tile.state = if (last != null) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = last?.name ?: "Plexo"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = if (last != null) "Abrir ultimo par" else "Crie o primeiro par"
        }
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val store = PlexoApp.instance.pairStore
        val last = store.lastUsedPair() ?: store.pairs().firstOrNull()
        if (last == null) {
            startActivityAndCollapse(
                Intent(this, com.melven.plexo.MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            return
        }
        store.touch(last.id)
        PlexoWidgetProvider.refresh(this)
        val request = SplitLauncher.Request(
            pkgA = last.pkgA,
            actA = null,
            pkgB = last.pkgB,
            actB = null,
            mode = last.mode.takeIf { it != LaunchMode.SPLIT } ?: LaunchMode.SPLIT
        )
        startActivityAndCollapse(
            SplitLauncher.launchIntentFor(this, request)
        )
    }
}
