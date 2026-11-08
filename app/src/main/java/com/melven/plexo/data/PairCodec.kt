package com.melven.plexo.data

import com.melven.plexo.launch.LaunchMode
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Serializacao compacta e independente do Android (testavel em JVM pura).
 * Formato: v1 \n id \t nome \t pkgA \t pkgB \t modo \t criado \t usado \n ...
 */
object PairCodec {

    private const val VERSION = 1
    private const val HEADER = "v$VERSION"
    private const val SEP = '\t'

    fun encode(pairs: List<Pair>): String = buildString {
        append(HEADER).append('\n')
        pairs.forEach { p ->
            append(enc(p.id)).append(SEP)
            append(enc(p.name)).append(SEP)
            append(enc(p.pkgA)).append(SEP)
            append(enc(p.pkgB)).append(SEP)
            append(p.mode.name).append(SEP)
            append(p.createdAt).append(SEP)
            append(p.lastUsed).append('\n')
        }
    }

    fun decode(raw: String?): List<Pair> {
        if (raw.isNullOrBlank()) return emptyList()
        val lines = raw.split('\n')
        if (lines.isEmpty() || lines.first() != HEADER) return emptyList()
        return lines.drop(1).mapNotNull { line ->
            if (line.isBlank()) return@mapNotNull null
            val f = line.split(SEP)
            if (f.size < 7) return@mapNotNull null
            runCatching {
                Pair(
                    id = dec(f[0]),
                    name = dec(f[1]),
                    pkgA = dec(f[2]),
                    pkgB = dec(f[3]),
                    mode = runCatching { LaunchMode.valueOf(f[4]) }.getOrDefault(LaunchMode.SPLIT),
                    createdAt = f[5].toLongOrNull() ?: 0L,
                    lastUsed = f[6].toLongOrNull() ?: 0L
                )
            }.getOrNull()
        }
    }

    fun encodeSafe(s: String): String = enc(s)
    fun decodeSafe(s: String): String = dec(s)

    private fun enc(value: String): String = URLEncoder.encode(value, "UTF-8")
    private fun dec(value: String): String = URLDecoder.decode(value, "UTF-8")
}
