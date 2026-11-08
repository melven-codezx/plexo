package com.melven.plexo.data

import com.melven.plexo.launch.LaunchMode

/** Um par de apps guardado pelo utilizador. */
data class Pair(
    val id: String,
    val name: String,
    val pkgA: String,
    val pkgB: String,
    val mode: LaunchMode = LaunchMode.SPLIT,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsed: Long = 0L
)
