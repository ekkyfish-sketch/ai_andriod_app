package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.DeepOceanBg
import com.example.ui.theme.OceanPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashLoadingScreen(
    onFinished: () -> Unit
) {
    // Launch a delayed trigger to signal completion after the animations complete
    LaunchedEffect(Unit) {
        delay(3200L) // Beautiful 3.2 seconds load time to enjoy the animation
        onFinished()
    }

    // Infinite transition for continuous dynamic elements
    val infiniteTransition = rememberInfiniteTransition(label = "SplashAnimation")

    // Pulse/Scale effect for the background glowing halo around the logo
    val haloPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloScale"
    )

    // Fade transition for text elements
    var textVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        textVisible = true
    }
    val textAlpha by animateFloatAsState(
        targetValue = if (textVisible) 1f else 0f,
        animationSpec = tween(800, easing = EaseOutQuad),
        label = "TextAlpha"
    )

    // Deep immersive Navy background gradient matching the blue branding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepOceanBg,
                        Color(0xFF0C192E),
                        DeepOceanBg
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Brand Round Logo with glowing neon halo
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                // Expanding background radiant halo ring
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(haloPulseScale)
                        .alpha(0.2f)
                ) {
                    val r = size.width * 0.45f
                    drawCircle(
                        color = Color(0xFF38BDF8), // Bright Sky Blue
                        radius = r,
                        center = center
                    )
                }

                // Inner static glowing ambient container
                Box(
                    modifier = Modifier
                        .size(125.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = com.example.R.drawable.img_ekkyfish_logo),
                        contentDescription = "Ekkyfish Logo Emblem",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Brand Typography Block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha)
            ) {
                Text(
                    text = "EkkyFish",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Ocean To Home Delivery",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFF38BDF8),
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // The centerpiece: Glorious running delivery bike animation
            RunningBikeAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Premium animated progress indicator bar
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                val infiniteProgress = rememberInfiniteTransition(label = "Progress")
                val loaderProgressOffset by infiniteProgress.animateFloat(
                    initialValue = -50f,
                    targetValue = 150f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1400, easing = EaseInOutQuad),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "ProgressOffset"
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(40.dp)
                        .offset(x = loaderProgressOffset.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    OceanPrimary,
                                    Color(0xFF0EA5E9),
                                    OceanPrimary
                                )
                            ),
                            CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun RunningBikeAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BikeAnimation")

    // Bobbing/vibration of the entire bike (feels like engine/road bump)
    val bikeBobbing by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(75, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BikeBobbing"
    )

    // Wheel rotation for spokes
    val wheelRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(220, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WheelRotation"
    )

    // Speed lines offsets in the background
    val speedLinesOffset1 by infiniteTransition.animateFloat(
        initialValue = 400f,
        targetValue = -150f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SpeedLines1"
    )

    val speedLinesOffset2 by infiniteTransition.animateFloat(
        initialValue = 450f,
        targetValue = -200f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SpeedLines2"
    )

    val roadOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -90f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RoadOffset"
    )

    // Subtle puff of speed dust/exhaust smoke from the muffler
    val smokeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "SmokeAlpha"
    )
    val smokeSize by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "SmokeSize"
    )

    Canvas(
        modifier = modifier
    ) {
        val w = size.width
        val h = size.height

        // 1. Draw Background Speed Lines
        // Line 1 (Upper)
        val line1Y = h * 0.20f
        drawLine(
            color = Color(0x6060A5FA),
            start = Offset(speedLinesOffset1, line1Y),
            end = Offset(speedLinesOffset1 + 90f, line1Y),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        // Line 2 (Mid-Upper)
        val line2Y = h * 0.40f
        drawLine(
            color = Color(0x8038BDF8),
            start = Offset(speedLinesOffset2, line2Y),
            end = Offset(speedLinesOffset2 + 130f, line2Y),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )

        // Line 3 (Mid-Lower)
        val line3Y = h * 0.58f
        drawLine(
            color = Color(0x5060A5FA),
            start = Offset(speedLinesOffset1 - 120f, line3Y),
            end = Offset(speedLinesOffset1 - 30f, line3Y),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        // Line 4 (Lower near ground)
        val line4Y = h * 0.76f
        drawLine(
            color = Color(0x4038BDF8),
            start = Offset(speedLinesOffset2 + 80f, line4Y),
            end = Offset(speedLinesOffset2 + 210f, line4Y),
            strokeWidth = 3.5f,
            cap = StrokeCap.Round
        )

        // 2. Draw Moving Road / Lane Marks
        val roadY = h * 0.84f
        // Solid dark road outline
        drawLine(
            color = Color(0xFF334155),
            start = Offset(0f, roadY),
            end = Offset(w, roadY),
            strokeWidth = 4.5f
        )
        // Scrolling divider dashes
        val dashY = h * 0.92f
        val dashLength = 40f
        val dashGap = 45f
        var currentDashX = roadOffset
        while (currentDashX < w + dashLength) {
            drawLine(
                color = Color(0x4594A3B8),
                start = Offset(currentDashX, dashY),
                end = Offset(currentDashX + dashLength, dashY),
                strokeWidth = 3f
            )
            currentDashX += dashLength + dashGap
        }

        // 3. Draw the Bike (incorporating vertical vibration rumble offset)
        val bikeYOffset = bikeBobbing

        val bikeCenterX = w * 0.5f
        val wheelRadius = 22f
        val rearWheelX = bikeCenterX - 50f
        val frontWheelX = bikeCenterX + 50f
        val wheelY = roadY - wheelRadius

        // A. Rear Exhaust Muffler Smoke Puff
        drawCircle(
            color = Color(0x3594A3B8).copy(alpha = smokeAlpha),
            radius = smokeSize,
            center = Offset(rearWheelX - 38f, wheelY - 6f)
        )
        drawCircle(
            color = Color(0x1F38BDF8).copy(alpha = smokeAlpha),
            radius = smokeSize * 1.4f,
            center = Offset(rearWheelX - 48f, wheelY - 12f)
        )

        // B. Wheel Tyres
        drawCircle(
            color = Color(0xFF1E293B),
            radius = wheelRadius,
            center = Offset(rearWheelX, wheelY),
            style = Stroke(width = 6.5f)
        )
        drawCircle(
            color = Color(0xFF1E293B),
            radius = wheelRadius,
            center = Offset(frontWheelX, wheelY),
            style = Stroke(width = 6.5f)
        )
        // alloy rims
        drawCircle(
            color = Color(0xFF64748B),
            radius = wheelRadius - 3.5f,
            center = Offset(rearWheelX, wheelY),
            style = Stroke(width = 2.5f)
        )
        drawCircle(
            color = Color(0xFF64748B),
            radius = wheelRadius - 3.5f,
            center = Offset(frontWheelX, wheelY),
            style = Stroke(width = 2.5f)
        )

        // Spinning Spokes
        val rad = Math.toRadians(wheelRotation.toDouble())
        val spokeLength = wheelRadius - 3.5f
        for (i in 0 until 4) {
            val angle = rad + (i * Math.PI / 2)
            val dx = (spokeLength * Math.cos(angle)).toFloat()
            val dy = (spokeLength * Math.sin(angle)).toFloat()

            // Rear wheel sport spokes
            drawLine(
                color = Color(0xFF94A3B8),
                start = Offset(rearWheelX, wheelY),
                end = Offset(rearWheelX + dx, wheelY + dy),
                strokeWidth = 2.5f
            )
            // Front wheel sport spokes
            drawLine(
                color = Color(0xFF94A3B8),
                start = Offset(frontWheelX, wheelY),
                end = Offset(frontWheelX + dx, wheelY + dy),
                strokeWidth = 2.5f
            )
        }

        // C. Steel Frame Components
        val framePath = Path().apply {
            moveTo(rearWheelX, wheelY)
            lineTo(bikeCenterX - 18f, wheelY - 14f)
            lineTo(bikeCenterX + 16f, wheelY - 8f)
            lineTo(frontWheelX, wheelY)
            lineTo(frontWheelX - 12f, wheelY - 38f)
            lineTo(bikeCenterX + 12f, wheelY - 26f)
            lineTo(bikeCenterX - 22f, wheelY - 24f)
            close()
        }
        drawPath(path = framePath, color = Color(0xFF0F172A))

        // D. Sleek Blue Scooter Mudguards
        // Rear Mudguard
        drawArc(
            color = Color(0xFF2563EB),
            startAngle = 180f,
            sweepAngle = 110f,
            useCenter = false,
            topLeft = Offset(rearWheelX - wheelRadius - 4f, wheelY - wheelRadius - 4f),
            size = androidx.compose.ui.geometry.Size(wheelRadius * 2 + 8f, wheelRadius * 2 + 8f),
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )
        // Front Mudguard
        drawArc(
            color = Color(0xFF2563EB),
            startAngle = 240f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(frontWheelX - wheelRadius - 4f, wheelY - wheelRadius - 4f),
            size = androidx.compose.ui.geometry.Size(wheelRadius * 2 + 8f, wheelRadius * 2 + 8f),
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        // E. Muffler / Exhaust Pipe (Angled chrome cylinder)
        drawLine(
            color = Color(0xFF64748B),
            start = Offset(bikeCenterX - 22f, wheelY - 4f),
            end = Offset(rearWheelX - 22f, wheelY - 7f),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFFE2E8F0),
            start = Offset(rearWheelX - 22f, wheelY - 7f),
            end = Offset(rearWheelX - 35f, wheelY - 15f),
            strokeWidth = 5f,
            cap = StrokeCap.Round
        )

        // F. The Delivery Box (Ekkyfish blue backbox)
        val boxWidth = 38f
        val boxHeight = 38f
        val boxX = rearWheelX - 12f
        val boxY = wheelY - 52f + bikeYOffset

        drawRoundRect(
            color = Color(0xFF1E40AF), // Dark Royal Blue Box
            topLeft = Offset(boxX, boxY),
            size = androidx.compose.ui.geometry.Size(boxWidth, boxHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRect(
            color = Color(0xFF38BDF8), // Light Sky Blue top rim
            topLeft = Offset(boxX, boxY),
            size = androidx.compose.ui.geometry.Size(boxWidth, 6f)
        )
        // Tiny fish decal on delivery box
        val fishDecalPath = Path().apply {
            moveTo(boxX + 11f, boxY + 21f)
            quadraticTo(boxX + 19f, boxY + 13f, boxX + 27f, boxY + 21f)
            quadraticTo(boxX + 19f, boxY + 29f, boxX + 11f, boxY + 21f)
            close()
        }
        drawPath(path = fishDecalPath, color = Color.White)
        drawPath(
            path = Path().apply {
                moveTo(boxX + 11f, boxY + 21f)
                lineTo(boxX + 7f, boxY + 17f)
                lineTo(boxX + 7f, boxY + 25f)
                close()
            },
            color = Color.White
        )

        // G. The Delivery Rider (positioned with dynamic bobbing)
        val riderSeatX = bikeCenterX - 6f
        val riderSeatY = wheelY - 28f + bikeYOffset
        val shoulderX = bikeCenterX + 16f
        val shoulderY = wheelY - 54f + bikeYOffset

        // Leaning Royal Blue Jacket Torso
        val riderTorsoPath = Path().apply {
            moveTo(riderSeatX, riderSeatY)
            lineTo(riderSeatX - 6f, riderSeatY - 16f)
            lineTo(shoulderX, shoulderY)
            lineTo(shoulderX - 14f, shoulderY + 22f)
            close()
        }
        drawPath(path = riderTorsoPath, color = Color(0xFF2563EB))

        // Leaning Rider Pants / Legs in dark navy slate
        val kneeX = bikeCenterX + 19f
        val kneeY = wheelY - 24f + bikeYOffset
        val footX = bikeCenterX + 11f
        val footY = wheelY - 9f + bikeYOffset

        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(riderSeatX, riderSeatY - 2f),
            end = Offset(kneeX, kneeY),
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(kneeX, kneeY),
            end = Offset(footX, footY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF0F172A),
            start = Offset(footX, footY),
            end = Offset(footX + 9f, footY),
            strokeWidth = 5f,
            cap = StrokeCap.Round
        )

        // Arms reaching to handle grip
        val handlebarX = bikeCenterX + 35f
        val handlebarY = wheelY - 50f + bikeYOffset

        drawLine(
            color = Color(0xFF2563EB), // Sleek blue arm sleeve
            start = Offset(shoulderX, shoulderY + 4f),
            end = Offset(handlebarX, handlebarY),
            strokeWidth = 5.2f,
            cap = StrokeCap.Round
        )

        // H. Delivery Rider Helmet & Visor
        val headX = shoulderX - 4f
        val headY = shoulderY - 15f
        val helmetRadius = 12f
        drawCircle(
            color = Color(0xFF0EA5E9), // Bright Blue Helmet matches logo
            radius = helmetRadius,
            center = Offset(headX, headY)
        )
        // Dark Visor facing right
        val visorPath = Path().apply {
            moveTo(headX + 2f, headY - helmetRadius + 2f)
            quadraticTo(headX + helmetRadius + 1.5f, headY - 2f, headX + 4f, headY + helmetRadius - 2f)
            lineTo(headX - 1f, headY + helmetRadius - 4f)
            quadraticTo(headX + helmetRadius - 3.5f, headY - 1f, headX + 1f, headY - helmetRadius + 3f)
            close()
        }
        drawPath(path = visorPath, color = Color(0xFF0F172A))

        // Helmet highlight shine
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = 3.5f,
            center = Offset(headX - 5f, headY - 5f)
        )

        // I. Motorcycle Front Fairing Assembly & Yellow Headlight Beam
        drawLine(
            color = Color(0xFF475569),
            start = Offset(frontWheelX - 4f, wheelY - 8f),
            end = Offset(handlebarX, handlebarY),
            strokeWidth = 4f
        )
        val fairingPath = Path().apply {
            moveTo(handlebarX - 4f, handlebarY - 4f)
            lineTo(handlebarX + 8f, handlebarY + 6f)
            lineTo(handlebarX + 2f, handlebarY + 23f)
            lineTo(handlebarX - 10f, handlebarY + 13f)
            close()
        }
        drawPath(path = fairingPath, color = Color(0xFF2563EB))

        // Bulbing Light
        drawCircle(
            color = Color(0xFFFEF08A),
            radius = 4.5f,
            center = Offset(handlebarX + 8f, handlebarY + 15f)
        )
        // Light Beam Projection
        val beamPath = Path().apply {
            moveTo(handlebarX + 8f, handlebarY + 15f)
            lineTo(w, wheelY - 15f)
            lineTo(w, roadY)
            lineTo(handlebarX + 8f, handlebarY + 15f)
            close()
        }
        drawPath(
            path = beamPath,
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0x55FEF08A), Color.Transparent)
            )
        )
    }
}

