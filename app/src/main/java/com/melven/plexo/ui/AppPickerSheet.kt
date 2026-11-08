package com.melven.plexo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.melven.plexo.data.AppEntry
import com.melven.plexo.ui.theme.PlexoColors

/**
 * Seletor de apps: sobreposicao de ecra inteiro (nao usa janela Dialog, para manter
 * transicoes suaves e ser totalmente testavel/capturavel).
 */
@Composable
fun AppPickerOverlay(
    visible: Boolean,
    apps: List<AppEntry>,
    selectedPkg: String?,
    otherPkg: String?,
    title: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(160)) + slideInVertically(tween(280)) { it / 3 },
        exit = fadeOut(tween(140)) + slideOutVertically(tween(220)) { it / 3 }
    ) {
        AppPickerSheet(
            apps = apps,
            selectedPkg = selectedPkg,
            otherPkg = otherPkg,
            title = title,
            onDismiss = onDismiss,
            onSelect = onSelect
        )
    }
}

@Composable
fun AppPickerSheet(
    apps: List<AppEntry>,
    selectedPkg: String?,
    otherPkg: String?,
    title: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(apps, query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) apps
        else apps.filter { it.label.lowercase().contains(q) || it.packageName.lowercase().contains(q) }
    }
    val shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)

    Box(Modifier.fillMaxSize()) {
        // Fundo escurecido
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.62f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )
        // Folha
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxSize(0.92f)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(Color(0xFF171428), Color(0xFF0B0915))))
                .border(1.dp, Color.White.copy(alpha = 0.10f), shape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(top = 10.dp)
                .navigationBarsPadding()
        ) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier.width(42.dp).height(4.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                )
            }
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        color = PlexoColors.TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.4).sp
                    )
                    Text(
                        "${apps.size} apps instaladas",
                        color = PlexoColors.TextMuted,
                        fontSize = 11.5.sp
                    )
                }
                Box(
                    Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.07f))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Close, "Fechar", tint = PlexoColors.TextSecondary, modifier = Modifier.size(18.dp))
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.09f), RoundedCornerShape(15.dp))
                    .padding(horizontal = 13.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Search, null, tint = PlexoColors.TextMuted, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(9.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(color = PlexoColors.TextPrimary, fontSize = 14.5.sp),
                    cursorBrush = SolidColor(PlexoColors.Cyan),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text("Procurar app\u2026", color = PlexoColors.TextMuted, fontSize = 14.5.sp)
                        }
                        inner()
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                    Text("Nada encontrado para \u201c$query\u201d", color = PlexoColors.TextMuted, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 14.dp, end = 14.dp, top = 4.dp, bottom = 24.dp
                    )
                ) {
                    items(filtered, key = { it.packageName }) { app ->
                        AppRow(
                            app = app,
                            selected = app.packageName == selectedPkg,
                            alreadyUsed = app.packageName == otherPkg,
                            onClick = { onSelect(app.packageName) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppRow(app: AppEntry, selected: Boolean, alreadyUsed: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) PlexoColors.Violet.copy(alpha = 0.16f) else Color.Transparent)
            .border(
                1.dp,
                if (selected) PlexoColors.Violet.copy(alpha = 0.5f) else Color.Transparent,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppAvatar(pkg = app.packageName, label = app.label, size = 42.dp, corner = 13.dp, selected = selected)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                app.label,
                color = PlexoColors.TextPrimary,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                app.packageName,
                color = PlexoColors.TextMuted,
                fontSize = 10.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        when {
            selected -> TagChip("nesta janela", color = PlexoColors.Violet)
            alreadyUsed -> TagChip("na outra", color = PlexoColors.Amber)
            else -> {}
        }
    }
}
