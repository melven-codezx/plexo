package com.melven.plexo.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.melven.plexo.data.Pair
import com.melven.plexo.launch.LaunchMode
import com.melven.plexo.ui.theme.PlexoColors

/**
 * Ecrã principal do Plexo.
 * Estrutura: marca -> "ecrã" com os dois apps -> botao de abertura -> pares guardados.
 */
@Composable
fun PlexoRoot(vm: HomeViewModel = viewModel()) {
    var showPickerFor by remember { mutableStateOf<Int?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf<Pair?>(null) }

    Box(Modifier.fillMaxSize()) {
    AuroraBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(14.dp))
            TopBar()
            Spacer(Modifier.height(22.dp))
            HeroTitle()
            Spacer(Modifier.height(20.dp))
            StageCard(
                labelA = vm.labelOf(vm.pkgA),
                labelB = vm.labelOf(vm.pkgB),
                pkgA = vm.pkgA,
                pkgB = vm.pkgB,
                mode = vm.mode,
                onPickA = { showPickerFor = 0 },
                onPickB = { showPickerFor = 1 },
                onSwap = vm::swap,
                onMode = vm::updateMode
            )
            Spacer(Modifier.height(18.dp))
            AuroraButton(
                text = when {
                    vm.pkgA == null && vm.pkgB == null -> "Escolha as duas apps"
                    vm.pkgA == null -> "Falta a app de cima"
                    vm.pkgB == null -> "Falta a app de baixo"
                    else -> "Abrir em simultâneo"
                },
                icon = Icons.Rounded.Bolt,
                enabled = vm.canLaunch(),
                onClick = vm::launchSelection,
                modifier = Modifier.fillMaxWidth()
            )
            if (vm.canLaunch()) {
                Spacer(Modifier.height(11.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GhostButton(
                        text = "Guardar par",
                        icon = Icons.Rounded.AutoAwesome,
                        modifier = Modifier.weight(1f)
                    ) { showSaveDialog = true }
                    GhostButton(
                        text = "Trocar",
                        icon = Icons.Rounded.SwapVert,
                        modifier = Modifier.width(128.dp),
                        onClick = vm::swap
                    )
                }
            }
            Spacer(Modifier.height(26.dp))
            SavedSection(
                pairs = vm.pairs,
                labelOf = { vm.labelOf(it) },
                onLaunch = vm::launchSaved,
                onDelete = { confirmDelete = it }
            )
            Spacer(Modifier.height(18.dp))
            FeatureFooter()
            Spacer(Modifier.height(28.dp))
        }
    }

    vm.message?.let { msg ->
        Toast(msg.text, isError = msg.isError, onGone = vm::consumeMessage)
    }
    }

    AppPickerOverlay(
        visible = showPickerFor != null,
        apps = vm.apps,
        selectedPkg = if (showPickerFor == 0) vm.pkgA else vm.pkgB,
        otherPkg = if (showPickerFor == 0) vm.pkgB else vm.pkgA,
        title = if (showPickerFor == 0) "App de cima" else "App de baixo",
        onDismiss = { showPickerFor = null },
        onSelect = { pkg ->
            showPickerFor?.let { vm.select(it, pkg) }
            showPickerFor = null
        }
    )

    if (showSaveDialog) {
        SavePairDialog(
            suggestion = vm.suggestName(),
            onDismiss = { showSaveDialog = false },
            onSave = { name ->
                vm.savePair(name)
                showSaveDialog = false
            }
        )
    }

    confirmDelete?.let { pair ->
        ConfirmDialog(
            title = "Remover \u201c${pair.name}\u201d?",
            body = "O par desaparece da lista. Pode voltar a criá-lo quando quiser.",
            confirm = "Remover",
            onDismiss = { confirmDelete = null },
            onConfirm = {
                vm.deletePair(pair)
                confirmDelete = null
            }
        )
    }

}

@Composable
private fun TopBar() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        PlexoMark(size = 34.dp)
        Spacer(Modifier.width(10.dp))
        Text(
            "plexo",
            color = PlexoColors.TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.6).sp
        )
        Spacer(Modifier.weight(1f))
        TagChip(text = "v1.0", icon = Icons.Rounded.Check)
    }
}

@Composable
private fun HeroTitle() {
    Column {
        Text(
            "DOIS APPS.",
            color = PlexoColors.TextPrimary,
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 42.sp,
            letterSpacing = (-1.6).sp
        )
        Text(
            text = "Um ecrã.",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 46.sp,
            letterSpacing = (-1.6).sp,
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.horizontalGradient(PlexoColors.Aurora)
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Escolha duas apps e o Plexo abre-as lado a lado. Sem anúncios, sem contas, sem complicações.",
            color = PlexoColors.TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

/** O "ecrã" central: duas mini-janelas empilhadas com um gesto de divisão no meio. */
@Composable
private fun StageCard(
    labelA: String?,
    labelB: String?,
    pkgA: String?,
    pkgB: String?,
    mode: LaunchMode,
    onPickA: () -> Unit,
    onPickB: () -> Unit,
    onSwap: () -> Unit,
    onMode: (LaunchMode) -> Unit
) {
    GlassPanel(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(start = 6.dp, end = 4.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PulseDot(PlexoColors.Mint)
                Spacer(Modifier.width(7.dp))
                Text(
                    "ECRÃ DO PLEXO",
                    color = PlexoColors.TextMuted,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp
                )
                Spacer(Modifier.weight(1f))
                if (pkgA != null && pkgB != null) {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.06f))
                            .clickable(onClick = onSwap)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.SwapVert, null, tint = PlexoColors.Cyan, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("inverter", color = PlexoColors.Cyan, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            MiniWindow(pkg = pkgA, label = labelA, accent = PlexoColors.Violet, modifier = Modifier.fillMaxWidth().clickable(onClick = onPickA))

            // Gesto de divisão
            Row(
                Modifier.fillMaxWidth().padding(vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.weight(1f).height(1.dp)
                        .background(Brush.horizontalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.16f))))
                )
                Box(
                    Modifier
                        .padding(horizontal = 10.dp)
                        .width(38.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(PlexoColors.Aurora))
                )
                Box(
                    Modifier.weight(1f).height(1.dp)
                        .background(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Transparent)))
                )
            }

            MiniWindow(pkg = pkgB, label = labelB, accent = PlexoColors.Magenta, modifier = Modifier.fillMaxWidth().clickable(onClick = onPickB))

            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Tune, null, tint = PlexoColors.TextMuted, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                ModeSegment(
                    splitSelected = mode == LaunchMode.SPLIT,
                    onSplit = { onMode(LaunchMode.SPLIT) },
                    onFloating = { onMode(LaunchMode.FLOATING) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SavedSection(
    pairs: List<Pair>,
    labelOf: (String?) -> String?,
    onLaunch: (Pair) -> Unit,
    onDelete: (Pair) -> Unit
) {
    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Pares guardados", color = PlexoColors.TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text(
                "${pairs.size}/12",
                color = PlexoColors.TextMuted,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(Modifier.height(10.dp))
        if (pairs.isEmpty()) {
            GlassPanel(Modifier.fillMaxWidth(), corner = 22.dp) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.06f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, null, tint = PlexoColors.Violet, modifier = Modifier.size(19.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Ainda não guardou nenhum par", color = PlexoColors.TextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                        Text("Escolha duas apps e toque em \u201cGuardar par\u201d.", color = PlexoColors.TextMuted, fontSize = 11.5.sp)
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                pairs.forEach { pair -> PairRow(pair, labelOf, onLaunch, onDelete) }
            }
        }
    }
}

@Composable
private fun PairRow(pair: Pair, labelOf: (String?) -> String?, onLaunch: (Pair) -> Unit, onDelete: (Pair) -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.985f else 1f, tween(120), label = "press")
    GlassPanel(
        Modifier.fillMaxWidth().scale(scale).clickable {
            pressed = true
            onLaunch(pair)
            pressed = false
        },
        corner = 22.dp
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box {
                AppAvatar(pkg = pair.pkgA, label = labelOf(pair.pkgA), size = 40.dp, corner = 13.dp)
                Box(Modifier.padding(start = 18.dp, top = 18.dp)) {
                    AppAvatar(pkg = pair.pkgB, label = labelOf(pair.pkgB), size = 28.dp, corner = 9.dp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    pair.name,
                    color = PlexoColors.TextPrimary,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${labelOf(pair.pkgA)}  ·  ${labelOf(pair.pkgB)}",
                    color = PlexoColors.TextMuted,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TagChip(
                text = if (pair.mode == LaunchMode.SPLIT) "dividido" else "flutuante",
                color = if (pair.mode == LaunchMode.SPLIT) PlexoColors.Cyan else PlexoColors.Amber
            )
            Spacer(Modifier.width(4.dp))
            Box(
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .clickable { onDelete(pair) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.DeleteOutline, "Remover", tint = PlexoColors.TextMuted, modifier = Modifier.size(17.dp))
            }
            Spacer(Modifier.width(6.dp))
            Box(
                Modifier.size(34.dp).clip(RoundedCornerShape(11.dp))
                    .background(Brush.horizontalGradient(PlexoColors.AuroraSoft)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.ArrowOutward, "Abrir", tint = Color.White, modifier = Modifier.size(17.dp))
            }
        }
    }
}

@Composable
private fun FeatureFooter() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        TagChip("offline", icon = Icons.Rounded.Check, color = PlexoColors.Mint, modifier = Modifier.weight(1f, false))
        TagChip("sem anúncios", icon = Icons.Rounded.Check, color = PlexoColors.Mint, modifier = Modifier.weight(1f, false))
        TagChip("atalho na barra", icon = Icons.Rounded.Check, color = PlexoColors.Mint, modifier = Modifier.weight(1f, false))
    }
}

/** Aviso flutuante minimalista. */
@Composable
fun Toast(text: String, isError: Boolean, onGone: () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(text) {
        kotlinx.coroutines.delay(3200)
        onGone()
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Row(
            Modifier
                .padding(bottom = 34.dp, start = 20.dp, end = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PlexoColors.SurfaceHi)
                .border(
                    1.dp,
                    if (isError) PlexoColors.Magenta.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.12f),
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(6.dp).clip(CircleShape)
                    .background(if (isError) PlexoColors.Magenta else PlexoColors.Mint)
            )
            Spacer(Modifier.width(9.dp))
            Text(text, color = PlexoColors.TextPrimary, fontSize = 12.5.sp)
        }
    }
}

/** Dialogo de confirmacao generico. */
@Composable
fun ConfirmDialog(
    title: String,
    body: String,
    confirm: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        GlassPanel(Modifier.fillMaxWidth(), corner = 26.dp) {
            Column(Modifier.padding(20.dp)) {
                Text(title, color = PlexoColors.TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(body, color = PlexoColors.TextSecondary, fontSize = 13.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    GhostButton("Cancelar", modifier = Modifier.weight(1f), onClick = onDismiss)
                    Box(
                        Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(PlexoColors.Magenta, Color(0xFFFF5E5B))))
                            .clickable(onClick = onConfirm),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(confirm, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/** Dialogo para dar nome ao par. */
@Composable
fun SavePairDialog(suggestion: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf(suggestion) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.62f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        GlassPanel(Modifier.fillMaxWidth().padding(24.dp).clickable(enabled = false) {}, corner = 26.dp) {
            Column(Modifier.padding(20.dp)) {
                Text("Guardar par", color = PlexoColors.TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Dê um nome curto. Sugerimos um baseado nas apps escolhidas.",
                    color = PlexoColors.TextSecondary,
                    fontSize = 12.5.sp
                )
                Spacer(Modifier.height(14.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = name,
                    onValueChange = { if (it.length <= 28) name = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = PlexoColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(PlexoColors.Cyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .padding(vertical = 14.dp, horizontal = 12.dp)
                )
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text("${name.length}/28", color = PlexoColors.TextMuted, fontSize = 10.5.sp)
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    GhostButton("Cancelar", modifier = Modifier.weight(1f), onClick = onDismiss)
                    Box(
                        Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(PlexoColors.Aurora))
                            .clickable { onSave(name) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Guardar", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/** Placeholder usado por pré-visualizações. */
@Composable
fun EmptyState(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = PlexoColors.TextMuted, fontSize = 13.sp)
    }
}

@Composable
private fun Unused() {
    Icon(Icons.Rounded.Close, null, modifier = Modifier.alpha(0f))
}
