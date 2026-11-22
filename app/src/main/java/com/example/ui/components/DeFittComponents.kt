package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DeFittBlack
import com.example.ui.theme.DeFittBorder
import com.example.ui.theme.DeFittGold
import com.example.ui.theme.DeFittGoldContainer
import com.example.ui.theme.DeFittGoldDark
import com.example.ui.theme.DeFittSurfaceCard
import com.example.ui.theme.DeFittTextSecondary
import com.example.ui.theme.DeFittWhite
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonGreenContainer
import com.example.ui.theme.NeonGreenDim
import com.example.ui.theme.OutfitFontFamily

@Composable
fun DeFittOfficialLogo(
    size: Dp = 112.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(26.dp))
            .border(1.5.dp, NeonGreen.copy(alpha = 0.7f), RoundedCornerShape(26.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.defitt_official_logo),
            contentDescription = "Logo oficial de DeFitt",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DeFittCoinEmblem(
    size: Dp = 28.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = this.size.minDimension / 2f
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFEA70), DeFittGold, DeFittGoldDark)
                ),
                radius = radius
            )
            drawCircle(
                color = Color(0xFFFFF5B8),
                radius = radius * 0.86f,
                style = Stroke(width = radius * 0.09f)
            )
        }
        Text(
            text = "D",
            color = Color(0xFF473100),
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (size.value * 0.54f).sp
        )
    }
}

@Composable
fun DeFittCoinsPill(
    balance: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = DeFittGoldContainer,
        border = BorderStroke(1.dp, DeFittGold.copy(alpha = 0.65f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DeFittCoinEmblem(size = 20.dp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = balance.toString(),
                color = DeFittGold,
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun DeFittMedalBadge(
    tier: String,
    unlocked: Boolean,
    size: Dp = 52.dp,
    modifier: Modifier = Modifier
) {
    val primaryColor = when {
        !unlocked -> Color(0xFF2B312D)
        tier == "GOLD" -> DeFittGold
        else -> NeonGreen
    }
    val secondaryColor = when {
        !unlocked -> Color(0xFF1A1E1B)
        tier == "GOLD" -> DeFittGoldDark
        else -> NeonGreenDim
    }
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val r = this.size.minDimension * 0.42f

            // Ribbon tail accents
            val ribbonPath = Path().apply {
                moveTo(cx - r * 0.45f, cy + r * 0.55f)
                lineTo(cx - r * 0.62f, this@Canvas.size.height * 0.97f)
                lineTo(cx - r * 0.12f, this@Canvas.size.height * 0.86f)
                close()
                moveTo(cx + r * 0.45f, cy + r * 0.55f)
                lineTo(cx + r * 0.62f, this@Canvas.size.height * 0.97f)
                lineTo(cx + r * 0.12f, this@Canvas.size.height * 0.86f)
                close()
            }
            drawPath(ribbonPath, color = secondaryColor)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primaryColor, secondaryColor),
                    center = Offset(cx - r * 0.2f, cy - r * 0.2f),
                    radius = r * 1.3f
                ),
                radius = r,
                center = Offset(cx, cy * 0.92f)
            )
            drawCircle(
                color = if (unlocked) Color.White.copy(alpha = 0.55f) else Color(0xFF3E4540),
                radius = r * 0.78f,
                center = Offset(cx, cy * 0.92f),
                style = Stroke(width = 2.5f)
            )
        }
        Text(
            text = if (unlocked) "D" else "?",
            color = if (unlocked) DeFittBlack else Color(0xFF6B766E),
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (size.value * 0.36f).sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

@Composable
fun StepProgressRing(
    steps: Int,
    goal: Int,
    distanceKmFormatted: String,
    activeTimeFormatted: String,
    modifier: Modifier = Modifier
) {
    val rawProgress = if (goal > 0) (steps.toFloat() / goal.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = spring(stiffness = 220f),
        label = "step_ring_progress"
    )
    val percent = (rawProgress * 100).toInt()

    Box(
        modifier = modifier.size(228.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 18.dp.toPx()
            val inset = strokeWidth / 2f + 6.dp.toPx()
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            val topLeft = Offset(inset, inset)

            // Background track
            drawArc(
                color = Color(0xFF1D2620),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Outer subtle glow
            if (animatedProgress > 0.01f) {
                drawArc(
                    color = NeonGreen.copy(alpha = 0.22f),
                    startAngle = 135f,
                    sweepAngle = 270f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth + 10.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Neon green arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(NeonGreenDim, NeonGreen, NeonGreenBright, NeonGreen)
                ),
                startAngle = 135f,
                sweepAngle = (270f * animatedProgress).coerceAtLeast(2f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "PASOS HOY",
                style = MaterialTheme.typography.labelMedium,
                color = NeonGreen
            )
            Text(
                text = steps.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = DeFittWhite,
                fontFamily = OutfitFontFamily
            )
            Text(
                text = "Meta: $goal · $percent%",
                style = MaterialTheme.typography.bodyMedium,
                color = DeFittTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = distanceKmFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = DeFittWhite
                )
                Text(
                    text = "•",
                    color = NeonGreen
                )
                Text(
                    text = activeTimeFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = DeFittWhite
                )
            }
        }
    }
}

@Composable
fun ExerciseIllustration(
    illustrationKey: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF101812),
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.3f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val stroke = Stroke(width = 5.5f, cap = StrokeCap.Round)

            // Subtle floor line
            drawLine(
                color = DeFittBorder,
                start = Offset(w * 0.1f, h * 0.88f),
                end = Offset(w * 0.9f, h * 0.88f),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            when (illustrationKey) {
                "pushup", "plank" -> {
                    // Horizontal athlete body
                    drawCircle(color = NeonGreen, radius = w * 0.085f, center = Offset(w * 0.78f, h * 0.45f))
                    drawLine(NeonGreenBright, Offset(w * 0.70f, h * 0.52f), Offset(w * 0.22f, h * 0.76f), strokeWidth = 6f, cap = StrokeCap.Round)
                    drawLine(DeFittWhite, Offset(w * 0.66f, h * 0.55f), Offset(w * 0.68f, h * 0.85f), strokeWidth = 5f, cap = StrokeCap.Round)
                    drawLine(DeFittWhite, Offset(w * 0.22f, h * 0.76f), Offset(w * 0.16f, h * 0.86f), strokeWidth = 5f, cap = StrokeCap.Round)
                }
                "squat" -> {
                    // Squatting silhouette
                    drawCircle(color = NeonGreen, radius = w * 0.09f, center = Offset(cx + w * 0.05f, h * 0.24f))
                    drawLine(NeonGreenBright, Offset(cx + w * 0.03f, h * 0.34f), Offset(cx - w * 0.12f, h * 0.58f), strokeWidth = 6f, cap = StrokeCap.Round)
                    drawLine(DeFittWhite, Offset(cx - w * 0.12f, h * 0.58f), Offset(cx + w * 0.10f, h * 0.62f), strokeWidth = 5.5f, cap = StrokeCap.Round)
                    drawLine(DeFittWhite, Offset(cx + w * 0.10f, h * 0.62f), Offset(cx + w * 0.08f, h * 0.86f), strokeWidth = 5.5f, cap = StrokeCap.Round)
                    drawLine(NeonGreen, Offset(cx + w * 0.03f, h * 0.40f), Offset(cx + w * 0.26f, h * 0.42f), strokeWidth = 4.5f, cap = StrokeCap.Round)
                }
                "cardio", "fullbody" -> {
                    // Dynamic runner / jumping figure
                    drawCircle(color = NeonGreen, radius = w * 0.085f, center = Offset(cx + w * 0.06f, h * 0.20f))
                    drawLine(NeonGreenBright, Offset(cx + w * 0.04f, h * 0.30f), Offset(cx - w * 0.04f, h * 0.56f), strokeWidth = 6f, cap = StrokeCap.Round)
                    drawLine(DeFittWhite, Offset(cx - w * 0.04f, h * 0.56f), Offset(cx + w * 0.18f, h * 0.72f), strokeWidth = 5.5f, cap = StrokeCap.Round)
                    drawLine(DeFittWhite, Offset(cx + w * 0.18f, h * 0.72f), Offset(cx + w * 0.12f, h * 0.86f), strokeWidth = 5f, cap = StrokeCap.Round)
                    drawLine(DeFittWhite, Offset(cx - w * 0.04f, h * 0.56f), Offset(cx - w * 0.24f, h * 0.78f), strokeWidth = 5f, cap = StrokeCap.Round)
                    drawLine(NeonGreen, Offset(cx + w * 0.02f, h * 0.36f), Offset(cx + w * 0.24f, h * 0.28f), strokeWidth = 4.5f, cap = StrokeCap.Round)
                    drawLine(NeonGreen, Offset(cx + w * 0.02f, h * 0.36f), Offset(cx - w * 0.20f, h * 0.44f), strokeWidth = 4.5f, cap = StrokeCap.Round)
                }
                "core", "back" -> {
                    // Supine/prone core arch
                    drawCircle(color = NeonGreen, radius = w * 0.08f, center = Offset(w * 0.24f, h * 0.52f))
                    drawLine(NeonGreenBright, Offset(w * 0.32f, h * 0.58f), Offset(w * 0.62f, h * 0.72f), strokeWidth = 6f, cap = StrokeCap.Round)
                    drawLine(DeFittWhite, Offset(w * 0.62f, h * 0.72f), Offset(w * 0.80f, h * 0.56f), strokeWidth = 5f, cap = StrokeCap.Round)
                    drawLine(DeFittGold, Offset(w * 0.36f, h * 0.56f), Offset(w * 0.42f, h * 0.32f), strokeWidth = 4.5f, cap = StrokeCap.Round)
                }
                else -> {
                    // Standing mobility / arms / warmup reach
                    drawCircle(color = NeonGreen, radius = w * 0.085f, center = Offset(cx, h * 0.22f))
                    drawLine(NeonGreenBright, Offset(cx, h * 0.32f), Offset(cx, h * 0.60f), strokeWidth = 6f, cap = StrokeCap.Round)
                    drawLine(DeFittWhite, Offset(cx, h * 0.60f), Offset(cx - w * 0.14f, h * 0.86f), strokeWidth = 5.5f, cap = StrokeCap.Round)
                    drawLine(DeFittWhite, Offset(cx, h * 0.60f), Offset(cx + w * 0.14f, h * 0.86f), strokeWidth = 5.5f, cap = StrokeCap.Round)
                    drawArc(
                        color = NeonGreen,
                        startAngle = 200f,
                        sweepAngle = 140f,
                        useCenter = false,
                        topLeft = Offset(cx - w * 0.26f, h * 0.16f),
                        size = Size(w * 0.52f, h * 0.38f),
                        style = stroke
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyStepsBarChart(
    labels: List<String>,
    values: List<Int>,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val maxVal = maxOf(values.maxOrNull() ?: 0, goal, 1000)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DeFittSurfaceCard, RoundedCornerShape(18.dp))
            .border(1.dp, DeFittBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PASOS POR DÍA",
                style = MaterialTheme.typography.labelLarge,
                color = DeFittWhite
            )
            Text(
                text = "Meta: $goal",
                style = MaterialTheme.typography.labelSmall,
                color = NeonGreen
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
        ) {
            val count = values.size.coerceAtLeast(1)
            val slotW = size.width / count
            val barW = (slotW * 0.52f).coerceAtMost(28.dp.toPx())

            // Goal reference dashed-like subtle line
            val goalY = size.height - (goal.toFloat() / maxVal.toFloat()) * (size.height - 8.dp.toPx())
            drawLine(
                color = NeonGreen.copy(alpha = 0.35f),
                start = Offset(0f, goalY),
                end = Offset(size.width, goalY),
                strokeWidth = 2f
            )

            values.forEachIndexed { idx, value ->
                val ratio = (value.toFloat() / maxVal.toFloat()).coerceIn(0f, 1f)
                val barH = (ratio * (size.height - 8.dp.toPx())).coerceAtLeast(4.dp.toPx())
                val left = idx * slotW + (slotW - barW) / 2f
                val top = size.height - barH
                drawRoundRect(
                    brush = if (value >= goal) {
                        Brush.verticalGradient(listOf(NeonGreenBright, NeonGreenDim))
                    } else if (value > 0) {
                        Brush.verticalGradient(listOf(NeonGreen.copy(alpha = 0.7f), NeonGreenContainer))
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFF28302A), Color(0xFF1A201C)))
                    },
                    topLeft = Offset(left, top),
                    size = Size(barW, barH),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = DeFittTextSecondary
                )
            }
        }
    }
}
