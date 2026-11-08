package com.melven.plexo.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import java.util.Locale

/**
 * Catalogo de apps instaladas com ecra de arranque.
 * Memoriza o resultado e recarrega quando o utilizador instala/desinstala apps.
 */
class AppCatalog(private val context: Context) {

    @Volatile
    private var cache: List<AppEntry>? = null

    fun apps(): List<AppEntry> = cache ?: synchronized(this) {
        cache ?: load().also { cache = it }
    }

    fun invalidate() {
        cache = null
    }

    fun find(packageName: String?): AppEntry? =
        packageName?.let { pkg -> apps().firstOrNull { it.packageName == pkg } }

    fun label(packageName: String?): String =
        find(packageName)?.label ?: packageName?.substringAfterLast('.') ?: "\u2014"

    fun isInstalled(packageName: String?): Boolean =
        packageName != null && find(packageName) != null

    private fun load(): List<AppEntry> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }
        val locale = Locale.getDefault()
        return resolved.asSequence()
            .mapNotNull { ri ->
                val info = ri.activityInfo ?: return@mapNotNull null
                AppEntry(
                    packageName = info.packageName,
                    activityName = info.name,
                    label = runCatching { ri.loadLabel(pm).toString() }.getOrDefault(info.packageName),
                    isSystem = (info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }
            .filter { it.packageName != context.packageName }
            .distinctBy { it.packageName }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label.lowercase(locale) })
            .toList()
    }
}
