package com.melven.plexo.util

import java.util.Locale

/** Gera nomes curtos e legiveis para um par de apps. */
object NameSuggester {

    fun suggest(labelA: String, labelB: String): String {
        val a = short(labelA)
        val b = short(labelB)
        return if (a.equals(b, ignoreCase = true)) "$a \u00d7 2" else "$a + $b"
    }

    fun short(label: String, max: Int = 14): String {
        val clean = label.trim()
            .replace(Regex("\\s+"), " ")
            .substringBefore("(")
            .trim()
        if (clean.length <= max) return clean.ifBlank { "App" }
        val cut = clean.take(max - 1).trimEnd()
        return cut.replace(Regex("[\\s\\-_:]+$"), "") + "\u2026"
    }

    fun initials(label: String): String = label.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1).uppercase(Locale.getDefault()) }
        .ifBlank { "P" }
}
