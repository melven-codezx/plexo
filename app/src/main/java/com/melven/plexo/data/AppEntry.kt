package com.melven.plexo.data

/** Uma aplicacao instalada que pode ser lancada pelo utilizador. */
data class AppEntry(
    val packageName: String,
    val activityName: String?,
    val label: String,
    val isSystem: Boolean
) {
    val initial: String
        get() = label.trim().take(1).ifBlank { "?" }.uppercase()
}
