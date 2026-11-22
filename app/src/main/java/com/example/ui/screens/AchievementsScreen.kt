package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.dao.AchievementWithStatus
import com.example.data.local.entity.CoinTransactionEntity
import com.example.ui.components.DeFittCoinEmblem
import com.example.ui.components.DeFittMedalBadge
import com.example.ui.theme.DeFittBlack
import com.example.ui.theme.DeFittBorder
import com.example.ui.theme.DeFittGold
import com.example.ui.theme.DeFittGoldContainer
import com.example.ui.theme.DeFittSurfaceCard
import com.example.ui.theme.DeFittTextSecondary
import com.example.ui.theme.DeFittWhite
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NeonGreen
import com.example.util.DateUtils

@Composable
fun AchievementsScreen(
    achievements: List<AchievementWithStatus>,
    coinBalance: Int,
    todayCoins: Int,
    coinHistory: List<CoinTransactionEntity>
) {
    val unlockedCount = achievements.count { it.isUnlocked }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeFittBlack),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "LOGROS Y DEFITT COINS",
                    style = MaterialTheme.typography.headlineLarge,
                    color = DeFittWhite
                )
                Text(
                    text = "$unlockedCount de ${achievements.size} medallas desbloqueadas automáticamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeFittTextSecondary
                )
            }
        }

        // DeFitt Coins Wallet Card
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = DeFittGoldContainer,
                border = BorderStroke(1.dp, DeFittGold.copy(alpha = 0.65f)),
                modifier = Modifier.fillMaxWidth().testTag("defitt_coins_wallet_card")
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(DeFittGoldContainer, DeFittSurfaceCard)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "SALDO DEFITT COINS",
                                style = MaterialTheme.typography.labelLarge,
                                color = DeFittGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = coinBalance.toString(),
                                style = MaterialTheme.typography.displayMedium,
                                color = DeFittWhite,
                                fontFamily = JetBrainsMonoFamily
                            )
                            Text(
                                text = "Ganadas hoy: +$todayCoins coins",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeonGreen
                            )
                        }
                        DeFittCoinEmblem(size = 64.dp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Las DeFitt Coins son puntos virtuales internos de motivación por hábitos saludables (con tope diario saludable; no representan dinero real).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeFittTextSecondary
                    )
                }
            }
        }

        item {
            Text(
                text = "MEDALLAS DE PROGRESO",
                style = MaterialTheme.typography.labelLarge,
                color = DeFittTextSecondary
            )
        }

        items(achievements, key = { it.achievement.code }) { item ->
            val a = item.achievement
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = DeFittSurfaceCard,
                border = BorderStroke(
                    1.dp,
                    when {
                        !item.isUnlocked -> DeFittBorder
                        a.badgeTier == "GOLD" -> DeFittGold.copy(alpha = 0.6f)
                        else -> NeonGreen.copy(alpha = 0.6f)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("achievement_card_${a.code}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DeFittMedalBadge(
                        tier = a.badgeTier,
                        unlocked = item.isUnlocked,
                        size = 54.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = a.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (item.isUnlocked) DeFittWhite else DeFittTextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+${a.coinsReward} D",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (item.isUnlocked) DeFittGold else DeFittTextSecondary,
                                fontFamily = JetBrainsMonoFamily
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = a.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DeFittTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (item.isUnlocked && item.unlockedAt != null) {
                                "Desbloqueado el ${DateUtils.formatDisplayDate(item.unlockedAt)}"
                            } else {
                                "Bloqueado · Completa la meta para desbloquear"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (item.isUnlocked) NeonGreen else DeFittTextSecondary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "HISTORIAL DE RECOMPENSAS",
                style = MaterialTheme.typography.labelLarge,
                color = DeFittTextSecondary
            )
        }

        if (coinHistory.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DeFittSurfaceCard,
                    border = BorderStroke(1.dp, DeFittBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Camina, registra tus comidas o completa un ejercicio para ganar tus primeras DeFitt Coins.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeFittTextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(coinHistory.take(25), key = { it.id }) { tx ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DeFittSurfaceCard,
                    border = BorderStroke(1.dp, DeFittBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DeFittCoinEmblem(size = 24.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.reason,
                                style = MaterialTheme.typography.bodyLarge,
                                color = DeFittWhite
                            )
                            Text(
                                text = "${DateUtils.formatDisplayDateFromKey(tx.dateKey)} · ${DateUtils.formatTime(tx.createdAt)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DeFittTextSecondary
                            )
                        }
                        Text(
                            text = "+${tx.amount}",
                            style = MaterialTheme.typography.titleMedium,
                            color = DeFittGold,
                            fontFamily = JetBrainsMonoFamily
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
