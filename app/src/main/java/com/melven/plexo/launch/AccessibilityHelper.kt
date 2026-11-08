package com.melven.plexo.launch

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

/** Ajuda a ligar/verificar o serviço de acessibilidade do Plexo. */
object AccessibilityHelper {

    /** true se o serviço do Plexo está ativo nas definições do sistema. */
    fun isEnabled(context: Context): Boolean {
        if (PlexoAccessibilityService.isConnected) return true
        val expected = "${context.packageName}/${PlexoAccessibilityService::class.java.name}"
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            ?: return false
        return runCatching {
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
                .any { info ->
                    val id = info.id ?: return@any false
                    id.equals(expected, ignoreCase = true) ||
                        id.startsWith(context.packageName)
                }
        }.getOrDefault(false)
    }

    /** Abre as definições de acessibilidade (o utilizador tem de ligar manualmente). */
    fun openSettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }
}
