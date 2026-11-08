package com.melven.plexo.data

import android.content.Context
import com.melven.plexo.launch.LaunchMode
import java.util.UUID

/** Preferencias persistentes: pares guardados + ultima seleccao. */
class PairStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("plexo_store", Context.MODE_PRIVATE)

    data class Selection(
        val pkgA: String? = null,
        val pkgB: String? = null,
        val mode: LaunchMode = LaunchMode.SPLIT
    )

    fun pairs(): List<Pair> = PairCodec.decode(prefs.getString(KEY_PAIRS, null))
        .sortedByDescending { it.lastUsed }

    fun savePairs(pairs: List<Pair>) {
        prefs.edit().putString(KEY_PAIRS, PairCodec.encode(pairs)).apply()
    }

    fun upsert(pair: Pair): List<Pair> {
        val current = pairs().toMutableList()
        val existing = current.indexOfFirst {
            it.pkgA == pair.pkgA && it.pkgB == pair.pkgB && it.mode == pair.mode
        }
        if (existing >= 0) {
            current[existing] = pair.copy(
                id = current[existing].id,
                createdAt = current[existing].createdAt
            )
        } else {
            current.add(0, pair)
        }
        val trimmed = current.take(MAX_PAIRS)
        savePairs(trimmed)
        return trimmed.sortedByDescending { it.lastUsed }
    }

    fun delete(id: String): List<Pair> {
        val remaining = pairs().filterNot { it.id == id }
        savePairs(remaining)
        return remaining
    }

    fun touch(id: String): List<Pair> {
        val updated = pairs().map {
            if (it.id == id) it.copy(lastUsed = System.currentTimeMillis()) else it
        }
        savePairs(updated)
        return updated.sortedByDescending { it.lastUsed }
    }

    fun lastUsedPair(): Pair? = pairs().maxByOrNull { it.lastUsed }

    fun selection(): Selection = Selection(
        pkgA = prefs.getString(KEY_SEL_A, null),
        pkgB = prefs.getString(KEY_SEL_B, null),
        mode = runCatching { LaunchMode.valueOf(prefs.getString(KEY_MODE, null) ?: "") }
            .getOrDefault(LaunchMode.SPLIT)
    )

    fun saveSelection(pkgA: String?, pkgB: String?, mode: LaunchMode) {
        prefs.edit()
            .putString(KEY_SEL_A, pkgA)
            .putString(KEY_SEL_B, pkgB)
            .putString(KEY_MODE, mode.name)
            .apply()
    }

    fun newId(): String = UUID.randomUUID().toString().take(8)

    companion object {
        private const val KEY_PAIRS = "pairs"
        private const val KEY_SEL_A = "sel_a"
        private const val KEY_SEL_B = "sel_b"
        private const val KEY_MODE = "mode"
        const val MAX_PAIRS = 12
    }
}
