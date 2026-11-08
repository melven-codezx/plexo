package com.melven.plexo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.melven.plexo.ui.theme.PlexoColors

/**
 * Cartão que pede a ativação do serviço de acessibilidade.
 * Sem isto, o Android 12+ ignora o pedido de ecrã dividido e abre só uma app.
 */
@Composable
fun SplitPermissionCard(
    enabled: Boolean,
    onActivate: () -> Unit,
    onHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Quando já está ligado, mostra apenas um selo discreto.
    AnimatedVisibility(
        visible = enabled,
        enter = fadeIn(tween(180)) + expandVertically(tween(220)),
        exit = fadeOut(tween(150)) + shrinkVertically(tween(200))
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(PlexoColors.Mint.copy(alpha = 0.10f))
                .border(1.dp, PlexoColors.Mint.copy(alpha = 0.32f), RoundedCornerShape(18.dp))
                .padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.CheckCircle, null, tint = PlexoColors.Mint, modifier = Modifier.size(17.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "Ecrã dividido real ativo",
                color = PlexoColors.Mint,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.weight(1f))
            Text(
                "alterar",
                color = PlexoColors.TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.clickable(onClick = onHelp)
            )
        }
    }

    AnimatedVisibility(
        visible = !enabled,
        enter = fadeIn(tween(180)) + expandVertically(tween(220)),
        exit = fadeOut(tween(150)) + shrinkVertically(tween(200))
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            PlexoColors.Violet.copy(alpha = 0.22f),
                            PlexoColors.Magenta.copy(alpha = 0.14f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(PlexoColors.Violet.copy(alpha = 0.65f), PlexoColors.Magenta.copy(alpha = 0.5f))
                    ),
                    RoundedCornerShape(24.dp)
                )
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Bolt, null, tint = Color.White, modifier = Modifier.size(17.dp))
                    }
                    Spacer(Modifier.width(9.dp))
                    Text(
                        "Falta 1 passo para dividir o ecrã",
                        color = PlexoColors.TextPrimary,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "O Android 12+ bloqueia o ecrã dividido pedido por apps normais — é por isso que " +
                        "só uma app abria em ecrã inteiro. Ative o serviço do Plexo para o sistema " +
                        "dividir o ecrã automaticamente.",
                    color = PlexoColors.TextSecondary,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(13.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Row(
                        Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Brush.horizontalGradient(PlexoColors.Aurora))
                            .clickable(onClick = onActivate),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(7.dp))
                        Text(
                            "Ativar agora",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        Modifier
                            .height(46.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White.copy(alpha = 0.07f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(15.dp))
                            .clickable(onClick = onHelp)
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Info, null, tint = PlexoColors.TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Como", color = PlexoColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
