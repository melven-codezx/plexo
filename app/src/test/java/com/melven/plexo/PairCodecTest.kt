package com.melven.plexo

import com.melven.plexo.data.Pair
import com.melven.plexo.data.PairCodec
import com.melven.plexo.launch.LaunchMode
import com.melven.plexo.util.NameSuggester
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Testes de logica pura: correm sem emulador (gradle test). */
class PairCodecTest {

    private val sample = listOf(
        Pair(id = "ab12", name = "Maps + Música", pkgA = "com.google.maps", pkgB = "com.spotify.music", mode = LaunchMode.SPLIT, createdAt = 10L, lastUsed = 20L),
        Pair(id = "cd34", name = "Chat\tcom\ttabs", pkgA = "org.telegram.messenger", pkgB = "com.whatsapp", mode = LaunchMode.FLOATING, createdAt = 30L, lastUsed = 0L)
    )

    @Test
    fun `ida e volta preserva todos os campos`() {
        val decoded = PairCodec.decode(PairCodec.encode(sample))
        assertEquals(sample.size, decoded.size)
        assertEquals(sample[0].name, decoded[0].name)
        assertEquals(LaunchMode.FLOATING, decoded[1].mode)
        assertEquals(20L, decoded[0].lastUsed)
        assertEquals("Chat\tcom\ttabs", decoded[1].name)
    }

    @Test
    fun `texto corrompido devolve lista vazia`() {
        assertTrue(PairCodec.decode(null).isEmpty())
        assertTrue(PairCodec.decode("").isEmpty())
        assertTrue(PairCodec.decode("lixo\nabc").isEmpty())
    }

    @Test
    fun `linhas invalidas sao ignoradas`() {
        val raw = PairCodec.encode(sample) + "linha-má\n"
        assertEquals(2, PairCodec.decode(raw).size)
    }

    @Test
    fun `sugestao de nome e curta e legivel`() {
        assertEquals("Maps + Spotify", NameSuggester.suggest("Maps", "Spotify"))
        assertEquals("Maps × 2", NameSuggester.suggest("Maps", "Maps"))
        val long = NameSuggester.suggest("Google Maps Navegação", "YouTube Music Premium")
        assertTrue(long.length <= 32)
        assertTrue(long.contains("+"))
    }
}
