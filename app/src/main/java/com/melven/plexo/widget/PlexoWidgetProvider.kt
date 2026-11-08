package com.melven.plexo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.melven.plexo.MainActivity
import com.melven.plexo.PlexoApp
import com.melven.plexo.R
import com.melven.plexo.launch.SplitLauncher

/**
 * Widget 2x2: mostra o ultimo par usado e abre-o com um toque.
 * Um segundo toque no canto inferior direito abre a app para escolher outro par.
 */
class PlexoWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id ->
            appWidgetManager.updateAppWidget(id, buildViews(context))
        }
    }

    companion object {

        fun buildViews(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_plexo)
            val store = PlexoApp.instance.pairStore
            val catalog = PlexoApp.instance.catalog
            val pair = store.lastUsedPair() ?: store.pairs().firstOrNull()

            if (pair == null) {
                views.setTextViewText(R.id.widget_title, context.getString(R.string.widget_empty_title))
                views.setTextViewText(R.id.widget_subtitle, context.getString(R.string.widget_empty_sub))
                views.setImageViewResource(R.id.widget_icon_top, R.drawable.ic_widget_placeholder)
                views.setImageViewResource(R.id.widget_icon_bottom, R.drawable.ic_widget_placeholder)
                views.setOnClickPendingIntent(R.id.widget_root, openAppIntent(context))
            } else {
                val labelA = catalog.label(pair.pkgA)
                val labelB = catalog.label(pair.pkgB)
                views.setTextViewText(R.id.widget_title, pair.name)
                views.setTextViewText(R.id.widget_subtitle, "$labelA  \u00b7  $labelB")
                val iconA = iconFor(context, pair.pkgA, store, catalog)
                val iconB = iconFor(context, pair.pkgB, store, catalog)
                if (iconA != null) views.setImageViewBitmap(R.id.widget_icon_top, iconA)
                if (iconB != null) views.setImageViewBitmap(R.id.widget_icon_bottom, iconB)
                views.setOnClickPendingIntent(R.id.widget_root, launchPairIntent(context, pair))
            }
            views.setOnClickPendingIntent(R.id.widget_settings, openAppIntent(context))
            return views
        }

        private fun iconFor(
            context: Context,
            pkg: String,
            store: com.melven.plexo.data.PairStore,
            catalog: com.melven.plexo.data.AppCatalog
        ) = runCatching {
            catalog.apps().firstOrNull { it.packageName == pkg }?.let { entry ->
                // Resize suave para o widget (evita bitmaps gigantes em RemoteViews).
                val drawable = context.packageManager.getApplicationIcon(pkg)
                val bmp = android.graphics.Bitmap.createBitmap(144, 144, android.graphics.Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bmp)
                drawable.setBounds(0, 0, 144, 144)
                drawable.draw(canvas)
                bmp
            }
        }.getOrNull()

        private fun launchPairIntent(context: Context, pair: com.melven.plexo.data.Pair): PendingIntent {
            val intent = SplitLauncher.launchIntentFor(
                context,
                SplitLauncher.Request(pair.pkgA, null, pair.pkgB, null, pair.mode)
            )
            return PendingIntent.getActivity(
                context,
                1000 + pair.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun openAppIntent(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java)
                .setData(Uri.parse("plexo://open"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return PendingIntent.getActivity(
                context,
                999,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun refresh(context: Context) {
            runCatching {
                val manager = AppWidgetManager.getInstance(context)
                val ids = manager.getAppWidgetIds(ComponentName(context, PlexoWidgetProvider::class.java))
                if (ids.isNotEmpty()) {
                    ids.forEach { manager.updateAppWidget(it, buildViews(context)) }
                }
            }
        }
    }
}
