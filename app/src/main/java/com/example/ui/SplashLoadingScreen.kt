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
        delay(1500L) // Reduced load timing for a faster, snappier launch
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
        targetValue = -85f,
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
                color = Color(0xFF38BDF8),
                start = Offset(currentDashX, dashY),
                end = Offset(currentDashX + dashLength, dashY),
                strokeWidth = 3.5f
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

        // 1. Dynamic Under-Scooter Shadow
        val shadowWidth = 145f + (bikeBobbing * 3f)
        drawOval(
            color = Color(0x3B0F172A),
            topLeft = Offset(bikeCenterX - 75f, roadY - 4f),
            size = androidx.compose.ui.geometry.Size(shadowWidth, 8f)
        )

        // 2. Rear Exhaust Muffler Smoke Puff
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

        // 3. Realistic Dual Wheels (Tyres, Rims, ventilated brake discs, and hubs)
        listOf(rearWheelX, frontWheelX).forEach { wX ->
            // Thick Outer Tyre (Charcoal)
            drawCircle(
                color = Color(0xFF1E293B),
                radius = wheelRadius,
                center = Offset(wX, wheelY),
                style = Stroke(width = 7.5f)
            )
            // Tyre Inner Tread Contour (Deep Black)
            drawCircle(
                color = Color(0xFF0F172A),
                radius = wheelRadius - 2f,
                center = Offset(wX, wheelY),
                style = Stroke(width = 2f)
            )
            // Steel Alloy Rim Bed (Silver Grey Accent)
            drawCircle(
                color = Color(0xFF94A3B8),
                radius = wheelRadius - 5f,
                center = Offset(wX, wheelY),
                style = Stroke(width = 1.5f)
            )
            // High-Performance Ventilated Brake Disc/Rotor (Metal Silver)
            drawCircle(
                color = Color(0xFFCBD5E1),
                radius = wheelRadius - 10f,
                center = Offset(wX, wheelY),
                style = Stroke(width = 3f)
            )
            // Central Axle Metal Hub Wheel Bolt
            drawCircle(
                color = Color(0xFF475569),
                radius = 4f,
                center = Offset(wX, wheelY)
            )
            drawCircle(
                color = Color(0xFFF1F5F9),
                radius = 1.5f,
                center = Offset(wX, wheelY)
            )
        }

        // 4. Spinning 5-Spoke Premium Alloy Mag Spokes
        val rad = Math.toRadians(wheelRotation.toDouble())
        val spokeInnerRad = 5f
        val spokeOuterRad = wheelRadius - 5.5f
        for (i in 0 until 5) {
            val angle = rad + (i * 2 * Math.PI / 5)
            val cos = Math.cos(angle).toFloat()
            val sin = Math.sin(angle).toFloat()

            // Rear wheel sporty alloy spokes
            drawLine(
                color = Color(0xFFF1F5F9),
                start = Offset(rearWheelX + spokeInnerRad * cos, wheelY + spokeInnerRad * sin),
                end = Offset(rearWheelX + spokeOuterRad * cos, wheelY + spokeOuterRad * sin),
                strokeWidth = 2.2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFF475569),
                start = Offset(rearWheelX + spokeInnerRad * cos + 1f, wheelY + spokeInnerRad * sin + 1f),
                end = Offset(rearWheelX + (spokeOuterRad - 2f) * cos + 1f, wheelY + (spokeOuterRad - 2f) * sin + 1f),
                strokeWidth = 1f
            )

            // Front wheel sporty alloy spokes
            drawLine(
                color = Color(0xFFF1F5F9),
                start = Offset(frontWheelX + spokeInnerRad * cos, wheelY + spokeInnerRad * sin),
                end = Offset(frontWheelX + spokeOuterRad * cos, wheelY + spokeOuterRad * sin),
                strokeWidth = 2.2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFF475569),
                start = Offset(frontWheelX + spokeInnerRad * cos + 1f, wheelY + spokeInnerRad * sin + 1f),
                end = Offset(frontWheelX + (spokeOuterRad - 2f) * cos + 1f, wheelY + (spokeOuterRad - 2f) * sin + 1f),
                strokeWidth = 1f
            )
        }

        // 5. Front Hydraulic Telescopic Suspension Fork
        val forkTopX = frontWheelX - 16f
        val forkTopY = wheelY - 32f + bikeYOffset
        // Outer sheath tube (Dark Slate)
        drawLine(
            color = Color(0xFF475569),
            start = Offset(frontWheelX, wheelY),
            end = Offset(frontWheelX - 8f, wheelY - 16f + bikeYOffset),
            strokeWidth = 4.5f,
            cap = StrokeCap.Round
        )
        // Chrome inner sliding stanchion
        drawLine(
            color = Color(0xFFE2E8F0),
            start = Offset(frontWheelX - 8f, wheelY - 16f + bikeYOffset),
            end = Offset(forkTopX, forkTopY),
            strokeWidth = 3f,
            cap = StrokeCap.Square
        )
        // Orange safety reflector on lower front fork
        drawRect(
            color = Color(0xFFF97316),
            topLeft = Offset(frontWheelX - 6f, wheelY - 12f + bikeYOffset),
            size = androidx.compose.ui.geometry.Size(3f, 6f)
        )

        // 6. Rear Dual Sport Shock Absorber Coil-Spring
        val shockStartX = rearWheelX + 6f
        val shockStartY = wheelY
        val shockEndX = bikeCenterX - 24f
        val shockEndY = wheelY - 28f + bikeYOffset
        // Chrome inner damper tube
        drawLine(
            color = Color(0xFFCBD5E1),
            start = Offset(shockStartX, shockStartY),
            end = Offset(shockEndX, shockEndY),
            strokeWidth = 3f
        )
        // Red Coil spring spiral rings
        val coils = 6
        for (c in 0..coils) {
            val ratio = c.toFloat() / coils
            val pX = shockStartX + (shockEndX - shockStartX) * ratio
            val pY = shockStartY + (shockEndY - shockStartY) * ratio
            drawLine(
                color = Color(0xFFEF4444), // Vibrant sports coil spring
                start = Offset(pX - 4f, pY - 1f),
                end = Offset(pX + 4f, pY + 1f),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
        }
        // Gold Nitrogen/Fluid Piggyback Reservoir
        drawRoundRect(
            color = Color(0xFFF59E0B),
            topLeft = Offset(shockEndX - 5f, shockEndY + 4f),
            size = androidx.compose.ui.geometry.Size(4f, 8f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f, 1f)
        )

        // 7. Engine Cylinder Block, Casing and Chrome Exhaust Heat-Shield
        val engineX = rearWheelX + 16f
        val engineY = wheelY - 12f + bikeYOffset
        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = Offset(engineX, engineY),
            size = androidx.compose.ui.geometry.Size(26f, 16f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )
        for (f in 0..3) {
            drawLine(
                color = Color(0xFF1E293B),
                start = Offset(engineX + 4f, engineY + 3f + (f * 3f)),
                end = Offset(engineX + 22f, engineY + 3f + (f * 3f)),
                strokeWidth = 1.5f
            )
        }
        // Exhaust Pipe Pipeline & Chrome Heat-Shielded Muffler
        val pipeMidX = rearWheelX - 4f
        val pipeMidY = wheelY - 2f + bikeYOffset
        val pipeEndX = rearWheelX - 25f
        val pipeEndY = wheelY - 14f + bikeYOffset
        // Dark exhaust barrel
        drawLine(
            color = Color(0xFF475569),
            start = Offset(pipeMidX, pipeMidY),
            end = Offset(pipeEndX, pipeEndY),
            strokeWidth = 7.5f,
            cap = StrokeCap.Round
        )
        // Metal Exhaust Tip
        drawLine(
            color = Color(0xFFCBD5E1),
            start = Offset(pipeEndX, pipeEndY),
            end = Offset(pipeEndX - 5f, pipeEndY - 2f),
            strokeWidth = 6.5f,
            cap = StrokeCap.Round
        )
        // Silver-Chrome heat guard plate
        drawLine(
            color = Color(0xFFF1F5F9),
            start = Offset(pipeMidX - 6f, pipeMidY - 1.5f),
            end = Offset(pipeEndX + 6f, pipeEndY + 1.5f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        // 8. Scooter Platform Floorboard and Tail Fairing Panel
        val floorX = bikeCenterX - 18f
        val floorY = wheelY - 10f + bikeYOffset
        val floorWidth = 42f
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(floorX, floorY),
            size = androidx.compose.ui.geometry.Size(floorWidth, 5f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5f, 1.5f)
        )
        // Deep blue chassis sweeping body panels
        val bodyPath = Path().apply {
            moveTo(bikeCenterX - 20f, floorY)
            lineTo(bikeCenterX - 35f, wheelY - 24f + bikeYOffset)
            lineTo(bikeCenterX - 15f, wheelY - 36f + bikeYOffset)
            lineTo(bikeCenterX + 10f, wheelY - 24f + bikeYOffset)
            close()
        }
        drawPath(path = bodyPath, color = Color(0xFF2563EB))
        // Dynamic racing stripe highlight decal
        val bodyDecal = Path().apply {
            moveTo(bikeCenterX - 22f, floorY - 3f)
            lineTo(bikeCenterX - 31f, wheelY - 23f + bikeYOffset)
            lineTo(bikeCenterX - 26f, wheelY - 26f + bikeYOffset)
            close()
        }
        drawPath(path = bodyDecal, color = Color(0x99FFFFFF))

        // 9. Premium Ergonomic Caramel-Brown Leather Seat
        val seatX = bikeCenterX - 32f
        val seatY = wheelY - 38f + bikeYOffset
        val seatPath = Path().apply {
            moveTo(seatX, seatY)
            quadraticTo(seatX + 15f, seatY - 4f, seatX + 32f, seatY - 1f)
            lineTo(seatX + 28f, seatY + 5f)
            lineTo(seatX + 2f, seatY + 4f)
            close()
        }
        drawPath(path = seatPath, color = Color(0xFFB45309)) // Amber/Caramel Leather Accent
        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(seatX + 1f, seatY + 4f),
            end = Offset(seatX + 29f, seatY + 5f),
            strokeWidth = 2f
        )

        // 10. Heavy Duty Back Carrier Rack & Blue Delivery Box
        // Silver metallic luggage rack stays
        drawLine(
            color = Color(0xFF64748B),
            start = Offset(rearWheelX - 4f, wheelY - 24f + bikeYOffset),
            end = Offset(rearWheelX + 16f, wheelY - 36f + bikeYOffset),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        val boxWidth = 38f
        val boxHeight = 38f
        val boxX = rearWheelX - 12f
        val boxY = wheelY - 52f + bikeYOffset
        drawRoundRect(
            color = Color(0xFF1E40AF),
            topLeft = Offset(boxX, boxY),
            size = androidx.compose.ui.geometry.Size(boxWidth, boxHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRect(
            color = Color(0xFF38BDF8),
            topLeft = Offset(boxX, boxY),
            size = androidx.compose.ui.geometry.Size(boxWidth, 6f)
        )
        // White fish logo sticker on box
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

        // 11. Steering stem, Rear-view safety mirrors, Handlebars
        val handlebarX = bikeCenterX + 35f
        val handlebarY = wheelY - 50f + bikeYOffset
        drawLine(
            color = Color(0xFF64748B),
            start = Offset(forkTopX, forkTopY),
            end = Offset(handlebarX, handlebarY),
            strokeWidth = 4f
        )
        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(handlebarX - 6f, handlebarY - 1f),
            end = Offset(handlebarX + 2f, handlebarY + 1f),
            strokeWidth = 3.5f,
            cap = StrokeCap.Round
        )
        // Mirror Stem & Oval reflecting glass
        drawLine(
            color = Color(0xFF94A3B8),
            start = Offset(handlebarX - 2f, handlebarY - 1f),
            end = Offset(handlebarX - 9f, handlebarY - 12f),
            strokeWidth = 1.5f
        )
        drawOval(
            color = Color(0xFF1E293B),
            topLeft = Offset(handlebarX - 14f, handlebarY - 15f),
            size = androidx.compose.ui.geometry.Size(9f, 6f)
        )
        drawOval(
            color = Color(0xE0E2E8F0),
            topLeft = Offset(handlebarX - 13f, handlebarY - 14f),
            size = androidx.compose.ui.geometry.Size(7f, 4f)
        )

        // 12. Front Nose fairing aerodynamic styling & sport windshield
        val nosePath = Path().apply {
            moveTo(handlebarX, handlebarY - 3f)
            lineTo(handlebarX + 14f, handlebarY + 13f)
            lineTo(handlebarX + 8f, handlebarY + 28f)
            lineTo(handlebarX - 6f, handlebarY + 13f)
            close()
        }
        drawPath(path = nosePath, color = Color(0xFF2563EB))
        val windshieldPath = Path().apply {
            moveTo(handlebarX, handlebarY - 3f)
            lineTo(handlebarX - 4f, handlebarY - 12f)
            lineTo(handlebarX + 6f, handlebarY - 8f)
            close()
        }
        drawPath(path = windshieldPath, color = Color(0xCC1E293B))

        // 13. High-intensity LED Headlight bulb and Volumetric Glow
        val bulbCenterX = handlebarX + 11f
        val bulbCenterY = handlebarY + 18f
        drawCircle(
            color = Color(0xFFFFEB3B),
            radius = 4f,
            center = Offset(bulbCenterX, bulbCenterY)
        )
        drawCircle(
            color = Color(0xFFCBD5E1),
            radius = 4.5f,
            center = Offset(bulbCenterX, bulbCenterY),
            style = Stroke(width = 1f)
        )
        val beamPath = Path().apply {
            moveTo(bulbCenterX, bulbCenterY)
            lineTo(w, wheelY - 30f)
            lineTo(w, roadY + 50f)
            lineTo(bulbCenterX, bulbCenterY)
            close()
        }
        drawPath(
            path = beamPath,
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0x60FEF08A), Color(0x35FEF08A), Color.Transparent)
            )
        )
        // Volumetric dust specks gleaming in headlight beam
        drawCircle(color = Color(0xCCFFFFFF), radius = 1f, center = Offset(bulbCenterX + 45f, bulbCenterY + 4f))
        drawCircle(color = Color(0x88FFFFFF), radius = 1.2f, center = Offset(bulbCenterX + 85f, bulbCenterY + 11f))
        drawCircle(color = Color(0x55FFFFFF), radius = 0.8f, center = Offset(bulbCenterX + 125f, bulbCenterY + 15f))

        // 14. Professional Delivery Rider & Gear
        val riderSeatX = bikeCenterX - 6f
        val riderSeatY = wheelY - 28f + bikeYOffset
        val shoulderX = bikeCenterX + 16f
        val shoulderY = wheelY - 54f + bikeYOffset
        // Wind-breaker blue active jacket torso
        val riderTorsoPath = Path().apply {
            moveTo(riderSeatX, riderSeatY)
            lineTo(riderSeatX - 6f, riderSeatY - 16f)
            lineTo(shoulderX, shoulderY)
            lineTo(shoulderX - 14f, shoulderY + 22f)
            close()
        }
        drawPath(path = riderTorsoPath, color = Color(0xFF2563EB))
        // Leaning Rider comfortable Slate Pants
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
        // Riding boots
        drawLine(
            color = Color(0xFF0F172A),
            start = Offset(footX, footY),
            end = Offset(footX + 9f, footY),
            strokeWidth = 5f,
            cap = StrokeCap.Round
        )
        // Sleek blue sleeve arm reaching out to grips
        drawLine(
            color = Color(0xFF2563EB),
            start = Offset(shoulderX, shoulderY + 4f),
            end = Offset(handlebarX, handlebarY),
            strokeWidth = 5.2f,
            cap = StrokeCap.Round
        )
        // Rider Helmet with Neon Accent Stripes
        val headX = shoulderX - 4f
        val headY = shoulderY - 15f
        val helmetRadius = 12f
        drawCircle(
            color = Color(0xFF0EA5E9),
            radius = helmetRadius,
            center = Offset(headX, headY)
        )
        // Visor glass shield
        val visorPath = Path().apply {
            moveTo(headX + 2f, headY - helmetRadius + 2f)
            quadraticTo(headX + helmetRadius + 1.5f, headY - 2f, headX + 4f, headY + helmetRadius - 2f)
            lineTo(headX - 1f, headY + helmetRadius - 4f)
            quadraticTo(headX + helmetRadius - 3.5f, headY - 1f, headX + 1f, headY - helmetRadius + 3f)
            close()
        }
        drawPath(path = visorPath, color = Color(0xFF0F172A))
        // Glare shell highlight on visor
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = 3.5f,
            center = Offset(headX - 5f, headY - 5f)
        )
    }
}

