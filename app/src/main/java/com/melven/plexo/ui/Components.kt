package com.melven.plexo.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.melven.plexo.ui.theme.AuroraBrush
import com.melven.plexo.ui.theme.PlexoColors
import com.melven.plexo.util.NameSuggester
import kotlin.math.cos
import kotlin.math.sin

/** Controlo global de animacao: respeita "remover animacoes" e poupa bateria. */
object Motion {
    @Volatile
    var reduceMotion: Boolean = false
}

/** Cache simples de icones de apps (bitmaps 192px). */
private val iconCache = object : LinkedHashMap<String, ImageBitmap>(32, 0.75f, true) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, ImageBitmap>?) = size > 64
}

@Composable
fun rememberAppIcon(pkg: String?): ImageBitmap? {
    if (pkg == null) return null
    val context = LocalContext.current
    return remember(pkg) {
        iconCache[pkg] ?: runCatching {
            val drawable = context.packageManager.getApplicationIcon(pkg)
            val size = 192
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, size, size)
            drawable.draw(canvas)
            bitmap.asImageBitmap().also { iconCache[pkg] = it }
        }.getOrNull()
    }
}

/** Painel de vidro fosco com gradiente subtil. */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    corner: Dp = 30.dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        PlexoColors.SurfaceHi.copy(alpha = 0.92f),
                        PlexoColors.Surface.copy(alpha = 0.78f)
                    )
                )
            )
            .border(1.dp, Brush.verticalGradient(
                listOf(Color.White.copy(alpha = 0.14f), Color.White.copy(alpha = 0.04f))
            ), shape)
    ) {
        content()
    }
}

/** Botao principal com gradiente aurora e brilho animado. */
@Composable
fun AuroraButton(
    text: String,
    icon: ImageVector?,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shift = if (Motion.reduceMotion) {
        0.5f
    } else {
        val transition = rememberInfiniteTransition(label = "aurora")
        val animated by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(4200), RepeatMode.Reverse),
            label = "shift"
        )
        animated
    }
    val alpha = if (enabled) 1f else 0.4f
    val brush = Brush.horizontalGradient(
        listOf(
            PlexoColors.Violet.copy(alpha = alpha),
            Color(0xFFB44BFF).copy(alpha = alpha),
            PlexoColors.Magenta.copy(alpha = alpha)
        ),
        startX = 900f * shift,
        endX = 900f * shift + 1100f
    )
    Box(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(brush)
            .clickable(enabled = enabled, onClick = onClick)
            .border(
                1.dp,
                Color.White.copy(alpha = 0.22f),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 22.dp)
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.2.sp
            )
            if (icon != null) {
                Spacer(Modifier.width(9.dp))
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

/** Botao secundario (contorno). */
@Composable
fun GhostButton(
    text: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(icon, null, tint = PlexoColors.TextSecondary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
        }
        Text(text, color = PlexoColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** Chip pequeno com icone. */
@Composable
fun TagChip(
    text: String,
    icon: ImageVector? = null,
    color: Color = PlexoColors.TextSecondary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, null, tint = color, modifier = Modifier.size(13.dp))
            Spacer(Modifier.width(5.dp))
        }
        Text(
            text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
            maxLines = 1
        )
    }
}

/** Icone de app com moldura; placeholder quando nao ha seleccao. */
@Composable
fun AppAvatar(
    pkg: String?,
    label: String?,
    size: Dp = 56.dp,
    corner: Dp = 18.dp,
    selected: Boolean = false,
    placeholder: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val icon = rememberAppIcon(pkg)
    val borderColor by animateColorAsState(
        if (selected) PlexoColors.Violet else Color.White.copy(alpha = 0.12f),
        label = "border"
    )
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(corner))
            .background(Color.White.copy(alpha = 0.07f))
            .border(if (selected) 2.dp else 1.dp, borderColor, RoundedCornerShape(corner)),
        contentAlignment = Alignment.Center
    ) {
        when {
            icon != null -> Image(
                bitmap = icon,
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size * 0.62f)
                    .clip(RoundedCornerShape(corner * 0.45f))
            )

            label != null -> Text(
                text = NameSuggester.initials(label),
                color = PlexoColors.TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = (size.value * 0.30f).sp
            )

            placeholder != null -> Icon(
                placeholder,
                contentDescription = null,
                tint = PlexoColors.TextMuted,
                modifier = Modifier.size(size * 0.4f)
            )
        }
    }
}

/** Cartao-miniatura que representa um app dentro do "ecra" do Plexo. */
@Composable
fun MiniWindow(
    pkg: String?,
    label: String?,
    height: Dp = 96.dp,
    accent: Color = PlexoColors.Violet,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.16f), Color.White.copy(alpha = 0.03f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(22.dp))
            .padding(12.dp)
    ) {
        Column(Modifier.fillMaxWidth().height(height - 24.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.20f)))
                Box(Modifier.size(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.13f)))
                Box(Modifier.size(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.09f)))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppAvatar(pkg = pkg, label = label, size = 34.dp, corner = 11.dp, placeholder = Icons.Rounded.Add)
                Spacer(Modifier.width(9.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = label ?: "Escolher app",
                        color = if (label != null) PlexoColors.TextPrimary else PlexoColors.TextMuted,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (label != null) "pronto" else "toque para escolher",
                        color = PlexoColors.TextMuted,
                        fontSize = 10.5.sp
                    )
                }
            }
        }
    }
}

/** Marca do Plexo: dois aneis entrelacados. Parametrico para reutilizar no ecra e no splash. */
@Composable
fun PlexoMark(size: Dp = 40.dp, modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(size)) {
        val r = this.size.minDimension * 0.30f
        val d = this.size.minDimension * 0.16f
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val anglesA = listOf(-50f, 0f, 50f).map { Math.toRadians(it.toDouble()) }
        val anglesB = listOf(130f, 180f, 230f).map { Math.toRadians(it.toDouble()) }

        // Aneis
        val stroke = this.size.minDimension * 0.075f
        drawCircle(
            brush = Brush.sweepGradient(PlexoColors.Aurora),
            radius = r,
            center = androidx.compose.ui.geometry.Offset(cx - d, cy),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
        drawCircle(
            brush = Brush.sweepGradient(listOf(PlexoColors.Cyan, PlexoColors.Violet, PlexoColors.Cyan)),
            radius = r,
            center = androidx.compose.ui.geometry.Offset(cx + d, cy),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )

        // Pontos orbitais
        val dot = this.size.minDimension * 0.052f
        anglesA.forEachIndexed { i, a ->
            drawCircle(
                color = if (i == 1) Color.White else PlexoColors.Magenta,
                radius = dot,
                center = androidx.compose.ui.geometry.Offset(
                    (cx - d + r * cos(a)).toFloat(),
                    (cy + r * sin(a)).toFloat()
                )
            )
        }
        anglesB.forEachIndexed { i, a ->
            drawCircle(
                color = if (i == 1) Color.White else PlexoColors.Cyan,
                radius = dot,
                center = androidx.compose.ui.geometry.Offset(
                    (cx + d + r * cos(a)).toFloat(),
                    (cy + r * sin(a)).toFloat()
                )
            )
        }
    }
}

/** Fundo aurora com duas manchas radiais. */
@Composable
fun AuroraBackground(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(PlexoColors.Ink, PlexoColors.InkSoft, Color(0xFF0A0714))
                )
            )
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(520.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PlexoColors.Violet.copy(alpha = 0.30f), Color.Transparent),
                        radius = 900f
                    )
                )
        )
        content()
    }
}

/** Indicador de modo tipo pill segmentado. */
@Composable
fun ModeSegment(
    splitSelected: Boolean,
    onSplit: () -> Unit,
    onFloating: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {
        SegmentItem("Ecrã dividido", splitSelected, onSplit, Modifier.weight(1f))
        SegmentItem("Flutuante", !splitSelected, onFloating, Modifier.weight(1f))
    }
}

@Composable
private fun SegmentItem(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (selected) Modifier.background(
                    Brush.horizontalGradient(
                        listOf(PlexoColors.Violet.copy(alpha = 0.90f), PlexoColors.Magenta.copy(alpha = 0.72f))
                    )
                ) else Modifier
            )
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = if (selected) Color.White else PlexoColors.TextSecondary,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

/** Ponto pulsante de estado. */
@Composable
fun PulseDot(color: Color, modifier: Modifier = Modifier) {
    val scale = if (Motion.reduceMotion) {
        1f
    } else {
        val transition = rememberInfiniteTransition(label = "pulse")
        val animated by transition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.25f,
            animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
            label = "scale"
        )
        animated
    }
    Box(modifier.size(8.dp).scale(scale).clip(CircleShape).background(color))
}
