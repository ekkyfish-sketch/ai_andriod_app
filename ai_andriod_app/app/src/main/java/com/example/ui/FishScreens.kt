package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.DialogProperties
import com.example.data.FishItem
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.StockGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getOptimizedDisplayName(name: String?, email: String?): String {
    val trimmedName = name?.trim().orEmpty()
    if (trimmedName.isNotBlank() && 
        trimmedName != "Registered Member" && 
        trimmedName != "Registered Seafood Enthusiast") {
        return trimmedName
    }
    val trimmedEmail = email?.trim().orEmpty()
    if (trimmedEmail.isNotBlank()) {
        val partBeforeAt = trimmedEmail.substringBefore("@")
        val cleanPrefix = partBeforeAt.replace(Regex("[._-]"), " ")
        val parts = cleanPrefix.split(" ").filter { it.isNotBlank() }
        if (parts.isNotEmpty()) {
            return parts.joinToString(" ") { part ->
                part.lowercase().replaceFirstChar { it.uppercase() }
            }
        }
    }
    return "Registered Member"
}

private fun saveBitmapToCache(context: android.content.Context, bitmap: Bitmap): Uri? {
    val file = File(context.cacheDir, "camera_fish_${System.currentTimeMillis()}.jpg")
    return try {
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Beautiful, custom self-contained wave illustration component to replace missing system Waves icon
@Composable
fun NauticalWavesIcon(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(0f, h * 0.4f)
            cubicTo(w * 0.25f, h * 0.15f, w * 0.25f, h * 0.65f, w * 0.5f, h * 0.4f)
            cubicTo(w * 0.75f, h * 0.15f, w * 0.75f, h * 0.65f, w, h * 0.4f)

            moveTo(0f, h * 0.7f)
            cubicTo(w * 0.25f, h * 0.45f, w * 0.25f, h * 0.95f, w * 0.5f, h * 0.7f)
            cubicTo(w * 0.75f, h * 0.45f, w * 0.75f, h * 0.95f, w, h * 0.7f)
        }
        drawPath(path = path, color = color, style = Stroke(width = 5f))
    }
}

// Custom subtraction/minus icon to replace missing system Remove icon
@Composable
fun CustomMinusIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Black
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawLine(
            color = color,
            start = Offset(w * 0.15f, h * 0.5f),
            end = Offset(w * 0.85f, h * 0.5f),
            strokeWidth = 6f
        )
    }
}

@Composable
fun SeafoodCanvasIcon(
    imageResName: String,
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.primary
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val drawableId = remember(imageResName) {
        if (imageResName.isNotBlank() && !imageResName.startsWith("content:") && 
            !imageResName.startsWith("file:") && !imageResName.startsWith("http")) {
            val resId = context.resources.getIdentifier(imageResName, "drawable", context.packageName)
            if (resId != 0) resId else {
                val prefixedId = context.resources.getIdentifier("img_$imageResName", "drawable", context.packageName)
                if (prefixedId != 0) prefixedId else 0
            }
        } else {
            0
        }
    }

    val isCustomImage = imageResName.startsWith("content:") || 
                        imageResName.startsWith("file:") || 
                        imageResName.startsWith("http:") || 
                        imageResName.startsWith("https:") ||
                        imageResName.contains("/")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(baseColor.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        if (isCustomImage) {
            AsyncImage(
                model = imageResName,
                contentDescription = "Custom Fish Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (drawableId != 0) {
            Image(
                painter = androidx.compose.ui.res.painterResource(id = drawableId),
                contentDescription = imageResName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                when (imageResName.lowercase()) {
                    "salmon" -> drawSalmon(w, h, baseColor)
                    "tuna" -> drawTuna(w, h, baseColor)
                    "snapper" -> drawSnapper(w, h, baseColor)
                    "prawn" -> drawPrawn(w, h, baseColor)
                    "lobster" -> drawLobster(w, h, baseColor)
                    "trout" -> drawTrout(w, h, baseColor)
                    "octopus" -> drawOctopus(w, h, baseColor)
                    else -> drawGenericFish(w, h, baseColor)
                }
            }
        }
    }
}

// Vector drawings on Canvas for flawless UI presentation

private fun DrawScope.drawSalmon(w: Float, h: Float, color: Color) {
    val salmonPink = Color(0xFFFF8B7D)
    val body = Path().apply {
        moveTo(w * 0.15f, h * 0.5f)
        cubicTo(w * 0.3f, h * 0.25f, w * 0.65f, h * 0.35f, w * 0.8f, h * 0.5f)
        cubicTo(w * 0.65f, h * 0.65f, w * 0.3f, h * 0.75f, w * 0.15f, h * 0.5f)
        close()
    }
    drawPath(path = body, color = salmonPink)

    val tail = Path().apply {
        moveTo(w * 0.15f, h * 0.5f)
        lineTo(w * 0.05f, h * 0.35f)
        lineTo(w * 0.08f, h * 0.5f)
        lineTo(w * 0.05f, h * 0.65f)
        close()
    }
    drawPath(path = tail, color = color)

    val fin = Path().apply {
        moveTo(w * 0.45f, h * 0.33f)
        cubicTo(w * 0.48f, h * 0.2f, w * 0.55f, h * 0.22f, w * 0.58f, h * 0.34f)
    }
    drawPath(path = fin, color = color)

    drawCircle(color = Color.White, radius = h * 0.04f, center = Offset(w * 0.72f, h * 0.46f))
    drawCircle(color = Color.Black, radius = h * 0.02f, center = Offset(w * 0.73f, h * 0.46f))

    drawLine(
        color = color.copy(alpha = 0.5f),
        start = Offset(w * 0.25f, h * 0.52f),
        end = Offset(w * 0.68f, h * 0.5f),
        strokeWidth = 3f
    )
}

private fun DrawScope.drawTuna(w: Float, h: Float, color: Color) {
    val oceanBlue = Color(0xFF1E3A8A)
    val yellowFin = Color(0xFFFBBF24)

    val body = Path().apply {
        moveTo(w * 0.18f, h * 0.5f)
        cubicTo(w * 0.35f, h * 0.23f, w * 0.65f, h * 0.3f, w * 0.82f, h * 0.5f)
        cubicTo(w * 0.65f, h * 0.7f, w * 0.35f, h * 0.77f, w * 0.18f, h * 0.5f)
        close()
    }
    drawPath(path = body, color = oceanBlue)

    val topFin = Path().apply {
        moveTo(w * 0.48f, h * 0.31f)
        lineTo(w * 0.6f, h * 0.12f)
        lineTo(w * 0.58f, h * 0.32f)
        close()
    }
    drawPath(path = topFin, color = yellowFin)

    val bottomFin = Path().apply {
        moveTo(w * 0.5f, h * 0.68f)
        lineTo(w * 0.58f, h * 0.84f)
        lineTo(w * 0.56f, h * 0.67f)
        close()
    }
    drawPath(path = bottomFin, color = yellowFin)

    val tail = Path().apply {
        moveTo(w * 0.18f, h * 0.5f)
        lineTo(w * 0.05f, h * 0.28f)
        lineTo(w * 0.1f, h * 0.5f)
        lineTo(w * 0.05f, h * 0.72f)
        close()
    }
    drawPath(path = tail, color = oceanBlue)

    drawCircle(color = Color.White, radius = h * 0.035f, center = Offset(w * 0.74f, h * 0.45f))
    drawCircle(color = Color.Black, radius = h * 0.018f, center = Offset(w * 0.75f, h * 0.45f))
}

private fun DrawScope.drawSnapper(w: Float, h: Float, color: Color) {
    val snapperRed = Color(0xFFEF4444)

    val body = Path().apply {
        moveTo(w * 0.16f, h * 0.52f)
        cubicTo(w * 0.32f, h * 0.22f, w * 0.66f, h * 0.28f, w * 0.82f, h * 0.52f)
        cubicTo(w * 0.66f, h * 0.76f, w * 0.32f, h * 0.82f, w * 0.16f, h * 0.52f)
        close()
    }
    drawPath(path = body, color = snapperRed)

    val tail = Path().apply {
        moveTo(w * 0.16f, h * 0.52f)
        lineTo(w * 0.06f, h * 0.32f)
        lineTo(w * 0.09f, h * 0.52f)
        lineTo(w * 0.06f, h * 0.72f)
        close()
    }
    drawPath(path = tail, color = snapperRed)

    val spiky = Path().apply {
        moveTo(w * 0.35f, h * 0.34f)
        lineTo(w * 0.42f, h * 0.2f)
        lineTo(w * 0.48f, h * 0.33f)
        lineTo(w * 0.54f, h * 0.18f)
        lineTo(w * 0.62f, h * 0.34f)
    }
    drawPath(path = spiky, color = color, style = Stroke(width = 6f))

    drawCircle(color = Color.White, radius = h * 0.04f, center = Offset(w * 0.73f, h * 0.44f))
    drawCircle(color = Color.Black, radius = h * 0.02f, center = Offset(w * 0.74f, h * 0.44f))
}

private fun DrawScope.drawPrawn(w: Float, h: Float, color: Color) {
    val shrimpCoral = Color(0xFFF97316)

    for (i in 0..4) {
        val offsetFactor = i * 0.1f
        drawCircle(
            color = shrimpCoral.copy(alpha = 1f - i * 0.12f),
            radius = w * (0.18f - i * 0.02f),
            center = Offset(w * (0.35f + offsetFactor), h * (0.4f + i * 0.08f))
        )
    }

    drawCircle(
        color = shrimpCoral,
        radius = w * 0.18f,
        center = Offset(w * 0.32f, h * 0.38f)
    )

    drawLine(color = shrimpCoral, start = Offset(w * 0.22f, h * 0.32f), end = Offset(w * 0.05f, h * 0.15f), strokeWidth = 3f)
    drawLine(color = shrimpCoral, start = Offset(w * 0.22f, h * 0.32f), end = Offset(w * 0.03f, h * 0.3f), strokeWidth = 3f)

    drawCircle(color = Color.Black, radius = h * 0.02f, center = Offset(w * 0.28f, h * 0.34f))
}

private fun DrawScope.drawLobster(w: Float, h: Float, color: Color) {
    val lobsterDarkRed = Color(0xFFB91C1C)

    drawRoundRect(
        color = lobsterDarkRed,
        topLeft = Offset(w * 0.35f, h * 0.4f),
        size = Size(w * 0.35f, h * 0.2f),
        cornerRadius = CornerRadius(15f, 15f)
    )

    val tailPath = Path().apply {
        moveTo(w * 0.7f, h * 0.5f)
        lineTo(w * 0.85f, h * 0.4f)
        lineTo(w * 0.80f, h * 0.5f)
        lineTo(w * 0.85f, h * 0.6f)
        close()
    }
    drawPath(path = tailPath, color = lobsterDarkRed)

    drawCircle(color = lobsterDarkRed, radius = w * 0.12f, center = Offset(w * 0.25f, h * 0.32f))
    drawCircle(color = lobsterDarkRed, radius = w * 0.12f, center = Offset(w * 0.25f, h * 0.68f))

    drawLine(color = lobsterDarkRed, start = Offset(w * 0.38f, h * 0.45f), end = Offset(w * 0.25f, h * 0.35f), strokeWidth = 10f)
    drawLine(color = lobsterDarkRed, start = Offset(w * 0.38f, h * 0.55f), end = Offset(w * 0.25f, h * 0.65f), strokeWidth = 10f)

    drawLine(color = Color.LightGray, start = Offset(w * 0.33f, h * 0.48f), end = Offset(w * 0.15f, h * 0.45f), strokeWidth = 3f)
    drawLine(color = Color.LightGray, start = Offset(w * 0.33f, h * 0.52f), end = Offset(w * 0.15f, h * 0.55f), strokeWidth = 3f)
}

private fun DrawScope.drawTrout(w: Float, h: Float, color: Color) {
    val troutGreen = Color(0xFF10B981)
    val goldenStreak = Color(0xFFFBBF24)

    val body = Path().apply {
        moveTo(w * 0.12f, h * 0.5f)
        cubicTo(w * 0.28f, h * 0.28f, w * 0.68f, h * 0.34f, w * 0.86f, h * 0.5f)
        cubicTo(w * 0.68f, h * 0.66f, w * 0.28f, h * 0.72f, w * 0.12f, h * 0.5f)
        close()
    }
    drawPath(path = body, color = troutGreen)

    val streak = Path().apply {
        moveTo(w * 0.22f, h * 0.51f)
        cubicTo(w * 0.4f, h * 0.53f, w * 0.6f, h * 0.48f, w * 0.78f, h * 0.5f)
    }
    drawPath(path = streak, color = goldenStreak, style = Stroke(width = 8f))

    val tail = Path().apply {
        moveTo(w * 0.12f, h * 0.5f)
        lineTo(w * 0.04f, h * 0.33f)
        lineTo(w * 0.07f, h * 0.5f)
        lineTo(w * 0.04f, h * 0.67f)
        close()
    }
    drawPath(path = tail, color = troutGreen)

    drawCircle(color = Color.White, radius = h * 0.035f, center = Offset(w * 0.75f, h * 0.46f))
    drawCircle(color = Color.Black, radius = h * 0.016f, center = Offset(w * 0.76f, h * 0.46f))
}

private fun DrawScope.drawOctopus(w: Float, h: Float, color: Color) {
    val krakenViolet = Color(0xFF8B5CF6)

    drawCircle(color = krakenViolet, radius = w * 0.24f, center = Offset(w * 0.5f, h * 0.36f))

    for (i in 0..5) {
        val startX = w * (0.35f + i * 0.06f)
        val endX = w * (0.2f + i * 0.12f)
        val tentacle = Path().apply {
            moveTo(startX, h * 0.5f)
            cubicTo(w * (0.4f + i * 0.04f), h * 0.65f, w * (0.3f - i * 0.02f), h * 0.8f, endX, h * 0.88f)
        }
        drawPath(path = tentacle, color = krakenViolet, style = Stroke(width = 8f))
    }

    drawCircle(color = Color.White, radius = h * 0.04f, center = Offset(w * 0.42f, h * 0.42f))
    drawCircle(color = Color.Black, radius = h * 0.02f, center = Offset(w * 0.41f, h * 0.42f))

    drawCircle(color = Color.White, radius = h * 0.04f, center = Offset(w * 0.58f, h * 0.42f))
    drawCircle(color = Color.Black, radius = h * 0.02f, center = Offset(w * 0.59f, h * 0.42f))
}

private fun DrawScope.drawGenericFish(w: Float, h: Float, color: Color) {
    val body = Path().apply {
        moveTo(w * 0.15f, h * 0.5f)
        cubicTo(w * 0.35f, h * 0.25f, w * 0.65f, h * 0.35f, w * 0.82f, h * 0.5f)
        cubicTo(w * 0.65f, h * 0.65f, w * 0.35f, h * 0.75f, w * 0.15f, h * 0.5f)
        close()
    }
    drawPath(path = body, color = color)

    val tail = Path().apply {
        moveTo(w * 0.15f, h * 0.5f)
        lineTo(w * 0.05f, h * 0.32f)
        lineTo(w * 0.09f, h * 0.5f)
        lineTo(w * 0.05f, h * 0.68f)
        close()
    }
    drawPath(path = tail, color = color)

    drawCircle(color = Color.White, radius = h * 0.035f, center = Offset(w * 0.72f, h * 0.45f))
    drawCircle(color = Color.Black, radius = h * 0.016f, center = Offset(w * 0.73f, h * 0.45f))
}


// PRIMARY STOREFRONT INTERFACE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorefrontScreen(
    viewModel: FishViewModel,
    modifier: Modifier = Modifier
) {
    val rawFishItems by viewModel.filteredFishItems.collectAsState()
    val fishItems = remember(rawFishItems) {
        rawFishItems.sortedWith { f1, f2 ->
            val f1Sold = f1.availableQuantity <= 0 || f1.availabilityStatus == "Sold Out"
            val f2Sold = f2.availableQuantity <= 0 || f2.availabilityStatus == "Sold Out"
            when {
                f1Sold && !f2Sold -> 1
                !f1Sold && f2Sold -> -1
                else -> 0
            }
        }
    }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val loggedInUser by viewModel.loggedInUser.collectAsState()

    var detailDialogItem by remember { mutableStateOf<FishItem?>(null) }
    var showLoginInterceptDialog by remember { mutableStateOf(false) }
    val categories = listOf("All", "Salmon", "Tuna", "Snapper", "Prawns", "Lobster", "Trout", "Octopus")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcoming Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Column (adaptive)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = com.example.R.drawable.img_ekkyfish_logo),
                        contentDescription = "EkkyFish Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EkkyFish",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.SansSerif
                    )
                }
                
                val userGreeting = if (loggedInUser != null) {
                    val displayName = if (loggedInUser?.role == UserRole.ADMIN) {
                        "Admin"
                    } else {
                        val userPart = loggedInUser?.email?.substringBefore("@") ?: "Customer"
                        userPart.replaceFirstChar { it.uppercase() }
                    }
                    "Hello, $displayName 👋"
                } else {
                    "Ocean To Home Fresh Meat"
                }

                Text(
                    text = userGreeting,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right Row (compact controllers)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Quick floating cart helper (FOR ALL USERS)
                val totalInCart = cart.size
                if (totalInCart > 0) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .clickable { viewModel.navigateTo(NavigationScreen.CART) }
                            .testTag("nav_quick_cart_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .background(CoralAlert, CircleShape)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "$totalInCart",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (loggedInUser == null) {
                    Button(
                        onClick = { viewModel.navigateTo(NavigationScreen.LOGIN) },
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("store_sign_in_header_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock icon",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sign In",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.logout()
                            viewModel.navigateTo(NavigationScreen.STORE)
                        },
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("store_logout_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Sign Out",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sign Out",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Advanced Search Panel
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("store_search_input"),
            placeholder = { Text("Search by species or class...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon") },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Wipe query")
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Horizontal Selector Strip (Scrollable Species Quick Search)
        LazyRow(
            modifier = Modifier.fillMaxWidth().testTag("store_category_strip"),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .clickable { viewModel.setSelectedCategory(category) }
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    tonalElevation = if (isSelected) 4.dp else 0.dp
                ) {
                    Text(
                        text = category,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Grid catalog
        if (fishItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                NauticalWavesIcon(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Seafood matches your inputs.",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = "Try searching another products.",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(fishItems) { fish ->
                    ProductCard(
                        fish = fish,
                        quantityInCart = cart[fish.id] ?: 0,
                        onAdd = { qty -> viewModel.addToCartWithQuantity(fish, qty) },
                        onViewDetails = { detailDialogItem = fish }
                    )
                }
            }
        }
    }

    // Detail Popups
    detailDialogItem?.let { fish ->
        ProductDetailDialog(
            fish = fish,
            quantityInCart = cart[fish.id] ?: 0,
            onDismiss = { detailDialogItem = null },
            onAdd = { qty -> viewModel.addToCartWithQuantity(fish, qty) }
        )
    }
}

// CATALOG PRODUCT CARD COMPONENT
@Composable
fun ProductCard(
    fish: FishItem,
    quantityInCart: Int,
    onAdd: (Int) -> Unit,
    onViewDetails: () -> Unit
) {
    var selectedWeight by remember(quantityInCart) { mutableStateOf(if (quantityInCart > 0) quantityInCart else 2) }
    var weightInputText by remember(selectedWeight) { mutableStateOf(String.format(Locale.US, "%.1f", selectedWeight * 0.5)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onViewDetails)
            .testTag("fish_card_${fish.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                Color.Transparent
                             )
                        )
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                SeafoodCanvasIcon(
                    imageResName = fish.imageResName,
                    modifier = Modifier
                        .size(110.dp)
                        .padding(8.dp)
                )

                // Category tag label
                Text(
                    text = fish.category,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )

                // Sold Out Circular Stamp Overlay
                if (fish.availableQuantity <= 0 || fish.availabilityStatus == "Sold Out") {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.45f))
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedSoldOutStamp(
                            sizeDp = 92.dp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = fish.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = fish.tamilName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "₹${fish.price.toInt()}/kg",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive buying thresholds (Touch target >48dp)
                if (fish.availableQuantity > 0 && fish.availabilityStatus != "Sold Out") {
                    // Spacer & Title
                    Text(
                        text = "Weight to Order:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Row with minus / weightInput / plus buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Decrease button
                        IconButton(
                            onClick = {
                                if (selectedWeight > 2) {
                                    selectedWeight--
                                    if (quantityInCart > 0) {
                                        onAdd(selectedWeight)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), CircleShape)
                        ) {
                            CustomMinusIcon(
                                modifier = Modifier.size(8.dp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }

                        // Text Field showing weight value
                        androidx.compose.foundation.text.BasicTextField(
                            value = weightInputText,
                            onValueChange = { newValue ->
                                weightInputText = newValue
                                val parsed = newValue.toDoubleOrNull()
                                if (parsed != null && parsed > 0.0) {
                                    val roundedUnits = kotlin.math.round(parsed / 0.5).toInt()
                                    if (roundedUnits in 2..fish.availableQuantity) {
                                        selectedWeight = roundedUnits
                                        if (quantityInCart > 0) {
                                            onAdd(roundedUnits)
                                        }
                                    }
                                }
                            },
                            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                    RoundedCornerShape(6.dp)
                                )
                                .background(
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.02f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(vertical = 4.dp),
                            singleLine = true
                        )

                        // Increase button
                        IconButton(
                            onClick = {
                                if (selectedWeight < fish.availableQuantity) {
                                    selectedWeight++
                                    if (quantityInCart > 0) {
                                        onAdd(selectedWeight)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (quantityInCart > 0) {
                        Button(
                            onClick = { onAdd(0) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("remove_from_cart_store_btn_${fish.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = CoralAlert),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove From Cart",
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Remove", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    } else {
                        Button(
                            onClick = { onAdd(selectedWeight) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("add_to_cart_btn_${fish.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Add to Cart",
                                    tint = Color.Black,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add to Cart", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = { /* disabled */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = false
                    ) {
                        Text("Unavailable", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
fun FullProductImageDialog(
    fish: FishItem,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
        ) {
            // Immersive Image Preview Container with Gestures
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            if (scale > 1f) {
                                offset += pan
                            } else {
                                offset = Offset.Zero
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                SeafoodCanvasIcon(
                    imageResName = fish.imageResName,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1f)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                )
            }

            // Top Header Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fish.name,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = fish.tamilName,
                        color = Color.LightGray.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close full image view",
                        tint = Color.White
                    )
                }
            }

            // Bottom Caption/Description Details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                        )
                    )
                    .padding(bottom = 48.dp, start = 24.dp, end = 24.dp, top = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Subtle pinch or drag gestures can zoom/pan the picture.",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = fish.description,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// DETAILED INFO DIALOG SHEET
@Composable
fun ProductDetailDialog(
    fish: FishItem,
    quantityInCart: Int,
    onDismiss: () -> Unit,
    onAdd: (Int) -> Unit
) {
    var selectedWeight by remember(quantityInCart) { mutableStateOf(if (quantityInCart > 0) quantityInCart else 2) }
    var weightInputText by remember(selectedWeight) { mutableStateOf(String.format(Locale.US, "%.1f", selectedWeight * 0.5)) }
    var showFullImageDialog by remember { mutableStateOf(false) }

    if (showFullImageDialog) {
        FullProductImageDialog(
            fish = fish,
            onDismiss = { showFullImageDialog = false }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header dismiss row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Species Dossier",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close description panel"
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier
                        .padding(12.dp)
                        .clickable { showFullImageDialog = true }
                        .testTag("product_image_detail_container_${fish.id}")
                ) {
                    SeafoodCanvasIcon(
                        imageResName = fish.imageResName,
                        modifier = Modifier.size(130.dp)
                    )
                    Text(
                        text = "🔍 Tap to Zoom",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .align(Alignment.BottomCenter)
                    )
                    if (fish.availableQuantity <= 0 || fish.availabilityStatus == "Sold Out") {
                        AnimatedSoldOutStamp(
                            sizeDp = 105.dp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Text(
                    text = fish.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = fish.tamilName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Technical habitat matrices
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Category", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        Text(fish.category, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Price", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        Text("₹${fish.price.toInt()}/kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Description",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = fish.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    lineHeight = 18.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (fish.availableQuantity > 0 && fish.availabilityStatus != "Sold Out") {
                    Text(
                        text = "Specify Order Quantity (kg)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (selectedWeight > 2) {
                                    selectedWeight--
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), CircleShape)
                        ) {
                            CustomMinusIcon(
                                modifier = Modifier.size(12.dp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }

                        androidx.compose.foundation.text.BasicTextField(
                            value = weightInputText,
                            onValueChange = { newValue ->
                                weightInputText = newValue
                                val parsed = newValue.toDoubleOrNull()
                                if (parsed != null && parsed > 0.0) {
                                    val roundedUnits = kotlin.math.round(parsed / 0.5).toInt()
                                    if (roundedUnits in 2..fish.availableQuantity) {
                                        selectedWeight = roundedUnits
                                    }
                                }
                            },
                            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                                .background(
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.02f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                if (selectedWeight < fish.availableQuantity) {
                                    selectedWeight++
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (quantityInCart > 0) {
                        Button(
                            onClick = {
                                onAdd(0)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralAlert),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove From Cart",
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Remove", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                onAdd(selectedWeight)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Add to Cart",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Add to Cart",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = { /* disabled */ },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = false
                    ) {
                        Text("Sold Out / Out of Stock", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

// OUTSTANDING SHOPPING CART LISTING SCREEN
@Composable
fun CartScreen(
    viewModel: FishViewModel,
    modifier: Modifier = Modifier
) {
    val fishItems by viewModel.allFishItems.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val transactionState by viewModel.transactionStatus.collectAsState()
    val paymentDetails by viewModel.paymentDetails.collectAsState()

    val lastPlacedOrderMessage by viewModel.lastPlacedOrderMessage.collectAsState()
    val adminPhoneState by viewModel.adminPhone.collectAsState()
    val whatsappNotifyEnabled by viewModel.whatsappNotifyEnabled.collectAsState()
    val context = LocalContext.current

    var savedWhatsappMsg by remember { mutableStateOf<String?>(null) }

    androidx.compose.runtime.LaunchedEffect(lastPlacedOrderMessage) {
        lastPlacedOrderMessage?.let { msg ->
            savedWhatsappMsg = msg
            viewModel.clearLastPlacedOrderMessage()
        }
    }

    val cartItemsList = remember(cart, fishItems) {
        cart.mapNotNull { (id, count) ->
            val match = fishItems.find { it.id == id }
            if (match != null) match to count else null
        }
    }

    val subtotal = remember(cartItemsList) {
        cartItemsList.sumOf { (fish, count) -> fish.price * (count * 0.5) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Seafood Cart",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Review live stock, select weight parameters, and checkout",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (cartItemsList.isEmpty() && transactionState !is ViewState.Success) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Empty shopping cart",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Your Cart is empty.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = { viewModel.navigateTo(NavigationScreen.STORE) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Browse Seafood Selection", fontWeight = FontWeight.Bold)
                }
            }
        } else if (transactionState is ViewState.Success) {
            val liveAdminPhone by viewModel.adminPhone.collectAsState()
            SuccessReceiptView(
                message = (transactionState as ViewState.Success).message,
                whatsappMsg = savedWhatsappMsg,
                adminPhone = liveAdminPhone,
                isAutomatedNotify = viewModel.isWhatsAppCloudApiConfigured,
                viewModel = viewModel,
                onDismiss = {
                    savedWhatsappMsg = null
                    viewModel.clearWhatsappStatus()
                    viewModel.navigateTo(NavigationScreen.STORE)
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cartItemsList) { (fish, count) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SeafoodCanvasIcon(
                                imageResName = fish.imageResName,
                                modifier = Modifier.size(54.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(fish.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("₹${fish.price}/kg", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                Text(
                                    text = "Subtotal: ₹${String.format(Locale.US, "%.2f", fish.price * (count * 0.5))}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Dynamic weight editors
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.removeFromCart(fish) },
                                    modifier = Modifier.size(36.dp).testTag("cart_remove_btn_${fish.id}")
                                ) {
                                    CustomMinusIcon(
                                        modifier = Modifier.size(16.dp),
                                        color = CoralAccent
                                    )
                                }

                                Text(
                                    text = "${String.format(Locale.US, "%.1f", count * 0.5)} kg",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )

                                IconButton(
                                    onClick = { viewModel.addToCart(fish) },
                                    modifier = Modifier.size(36.dp).testTag("cart_add_btn_${fish.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Increase species weight in cart",
                                        tint = StockGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.removeProductFromCart(fish.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete item",
                                        tint = CoralAlert,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CheckoutPortal(
                        viewModel = viewModel,
                        subtotal = subtotal,
                        transactionState = transactionState,
                        onSubmit = { email, name, phone, address, method, upi ->
                            viewModel.executeCheckout(
                                customerEmail = email,
                                customerName = name,
                                customerPhone = phone,
                                shippingAddress = address,
                                paymentMethod = method,
                                upiId = upi
                            )
                        }
                    )
                }
            }
        }
    }
}

// THE GATEWAY PORTAL CONTAINER
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutPortal(
    viewModel: FishViewModel,
    subtotal: Double,
    transactionState: ViewState,
    onSubmit: (String, String, String, String, String, String) -> Unit
) {
    val loggedInUser by viewModel.loggedInUser.collectAsState()
    val user = loggedInUser
    val customerAccount by viewModel.loggedInCustomerAccount.collectAsState()

    var customerEmail by remember(customerAccount, user) { mutableStateOf(customerAccount?.email ?: user?.email ?: "") }
    var customerName by remember(customerAccount, user) {
        val activeName = customerAccount?.name?.trim().orEmpty()
        val initName = if (activeName.isNotBlank() && 
            activeName != "Registered Member" && 
            activeName != "Registered Seafood Enthusiast") {
            activeName
        } else {
            ""
        }
        mutableStateOf(initName)
    }
    var customerPhone by remember(customerAccount) { mutableStateOf(customerAccount?.phone ?: "") }
    // No popup showing required in Checkout portal anymore
    var shippingAddress by remember(customerAccount) { mutableStateOf(customerAccount?.address ?: "") }
    var pinCode by remember(customerAccount) {
        val addr = customerAccount?.address.orEmpty()
        val match = Regex("\\b\\d{6}\\b").find(addr)
        mutableStateOf(match?.value ?: "")
    }

    // UPI/COD selection state
    var selectedMethod by remember { mutableStateOf("COD") } // "UPI" or "COD"
    var upiId by remember { mutableStateOf("") }

    val adminPincodeConfig by viewModel.deliveryPincode.collectAsState()

    val isPincodeValid = remember(pinCode, adminPincodeConfig) {
        val cleanUser = pinCode.trim()
        if (cleanUser.length != 6 || !cleanUser.all { it.isDigit() }) {
            false
        } else {
            val cleanAdmin = adminPincodeConfig.trim()
            if (cleanAdmin.isBlank()) {
                true
            } else {
                val allowedPincodes = cleanAdmin.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                allowedPincodes.any { allowed ->
                    cleanUser == allowed || (allowed.length < 6 && cleanUser.startsWith(allowed))
                }
            }
        }
    }

    val isPhoneValid = remember(customerPhone) {
        val digits = customerPhone.replace(Regex("[^0-9]"), "")
        digits.length == 10
    }

    // Dynamic Validation Check
    val isFormValid = remember(user, customerEmail, customerName, customerPhone, shippingAddress, selectedMethod, upiId, isPincodeValid, isPhoneValid) {
        val emailValid = customerEmail.contains("@") && customerEmail.contains(".")
        val infoValid = customerName.isNotBlank() && isPhoneValid && shippingAddress.isNotBlank()
        val paymentValid = if (selectedMethod == "UPI") upiId.isNotBlank() && upiId.length >= 3 else true
        emailValid && infoValid && paymentValid && isPincodeValid
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Checkout Portal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selectable Payment Channel Cards
            Text(
                text = "Select Payment Channel *",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cash on Delivery Option (Default)
                Card(
                    onClick = { selectedMethod = "COD" },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMethod == "COD") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = if (selectedMethod == "COD") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .testTag("payment_method_cod")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "COD Home icon",
                                    tint = if (selectedMethod == "COD") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Pay on Delivery",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (selectedMethod == "COD") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Cash on Delivery (COD)",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // UPI Option
                Card(
                    onClick = { selectedMethod = "UPI" },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMethod == "UPI") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = if (selectedMethod == "UPI") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .testTag("payment_method_upi")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "UPI Transfer symbol",
                                    tint = if (selectedMethod == "UPI") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "UPI Transfer",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (selectedMethod == "UPI") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "GPay / PhonePe / Paytm",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // UPI ID input form
            if (selectedMethod == "UPI") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("Your UPI Address *") },
                    placeholder = { Text("e.g. seafood@okhdfcbank") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_upi_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery coordinates block
            Text(
                text = "Recipient & Shipping Contact Details *",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text("Delivery Full Name *") },
                placeholder = { Text("e.g. Admiral John Doe") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_guest_name"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            val isPhoneError = customerPhone.isNotBlank() && !isPhoneValid
            OutlinedTextField(
                value = customerPhone,
                onValueChange = { input ->
                    var cleaned = input.filter { it.isDigit() }
                    if (cleaned.startsWith("91") && cleaned.length > 10) {
                        cleaned = cleaned.substring(2)
                    }
                    if (cleaned.length > 10) {
                        cleaned = cleaned.take(10)
                    }
                    customerPhone = cleaned
                },
                label = { Text("Contact Phone Number *") },
                placeholder = { Text("e.g. 9884958545") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_guest_phone"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                isError = isPhoneError,
                supportingText = {
                    if (isPhoneError) {
                        Text("Strictly enter a valid 10 digit number", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    } else {
                        Text("Enter exactly 10 digits (system handles +91/91 behind screen)", fontSize = 10.sp)
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = shippingAddress,
                onValueChange = { shippingAddress = it },
                label = { Text("Shipping Street Address *") },
                placeholder = { Text("e.g. No. 12, Anna Salai, Chennai, Tamil Nadu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_guest_address"),
                shape = RoundedCornerShape(10.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = pinCode,
                onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) pinCode = it },
                label = { Text("Chennai PIN Code (6 digits) *") },
                placeholder = { Text("e.g. 600001") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_guest_pincode"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                isError = pinCode.isNotEmpty() && !isPincodeValid
            )
            if (pinCode.isNotEmpty() && !isPincodeValid) {
                Text(
                    text = if (adminPincodeConfig.isNotBlank()) "Delivery is restricted to allowed PINs: $adminPincodeConfig" else "Please enter a valid 6-digit PIN code",
                    color = CoralAlert,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Correspondence Email
            OutlinedTextField(
                value = customerEmail,
                onValueChange = { customerEmail = it },
                label = { Text(if (user == null) "Email Address * (For shipment invoice updates)" else "Correspondence Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_email_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                enabled = user == null // Disable if user is already logged in securely
            )

            if (user == null) {
                // Direct inline selection chips for quick completion
                EmailExtensionChips(
                    email = customerEmail,
                    onEmailUpdated = { customerEmail = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)))
            Spacer(modifier = Modifier.height(12.dp))

            // Price visuals summary
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Items Price Subtotal:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text("₹${String.format(Locale.US, "%.2f", subtotal)}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Delivery Charges:", fontSize = 13.sp, color = CoralAlert, fontWeight = FontWeight.Medium)
                Text("+₹50.00", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CoralAlert)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Grand Total to Pay:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "₹${String.format(Locale.US, "%.2f", subtotal + 50.0)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))



            Spacer(modifier = Modifier.height(14.dp))

            when (transactionState) {
                is ViewState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        val loadingText = if (selectedMethod == "UPI") "Authenticating instant UPI transaction webhook..." else "Transmitting cash order dispatch log..."
                        Text(loadingText, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    }
                }
                is ViewState.Error -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CoralAlert.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Error logo", tint = CoralAlert)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(transactionState.error, color = CoralAlert, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                else -> {}
            }

            if (transactionState !is ViewState.Loading) {
                val buttonText = if (selectedMethod == "UPI") {
                    "Pay ₹${String.format(Locale.US, "%.2f", subtotal + 50.0)} via UPI"
                } else {
                    "Place On Delivery Order (₹${String.format(Locale.US, "%.2f", subtotal + 50.0)})"
                }
                Button(
                    onClick = { onSubmit(customerEmail, customerName, customerPhone, "$shippingAddress, Chennai (PIN: $pinCode)", selectedMethod, upiId) },
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("pay_secure_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StockGreen,
                        disabledContainerColor = StockGreen.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(buttonText, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                if (!isFormValid) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val helpText = if (selectedMethod == "UPI") {
                        "Please fill all fields, UPI address & a 6-digit Chennai PIN (starting with 600)."
                    } else {
                        "Please fill all fields & a 6-digit Chennai PIN (starting with 600)."
                    }
                    Text(
                        text = helpText,
                        fontSize = 11.sp,
                        color = CoralAlert,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// INVOICE SUMMARY ELEMENT
@Composable
fun SuccessReceiptView(
    message: String,
    whatsappMsg: String?,
    adminPhone: String,
    isAutomatedNotify: Boolean,
    viewModel: FishViewModel,
    onDismiss: () -> Unit
) {
    val whatsappStatus by viewModel.whatsappStatus.collectAsState()
    val tokenVal = com.example.BuildConfig.WHATSAPP_API_TOKEN
    val phoneIdVal = com.example.BuildConfig.WHATSAPP_PHONE_NUMBER_ID
    val tokenInfo = if (tokenVal.isBlank() || tokenVal == "YOUR_WHATSAPP_API_TOKEN") "None" else "Loaded (Length: ${tokenVal.length})"
    val phoneIdInfo = if (phoneIdVal.isBlank() || phoneIdVal == "YOUR_WHATSAPP_PHONE_NUMBER_ID") "None" else "Loaded ($phoneIdVal)"

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Audit success token",
                tint = StockGreen,
                modifier = Modifier.size(68.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Order Succeeded!",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = StockGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            whatsappMsg?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAutomatedNotify) Icons.Default.CheckCircle else Icons.Filled.Info,
                                contentDescription = "Cloud API status",
                                tint = if (isAutomatedNotify) StockGreen else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WhatsApp Cloud Notification 📱",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = if (isAutomatedNotify) {
                                val currentStatus = whatsappStatus ?: "Initiating background API dispatch..."
                                "Alert Status: $currentStatus\n\n" +
                                "🔒 Diagnostics:\n" +
                                "• Phone Number ID: $phoneIdInfo\n" +
                                "• Access Token: $tokenInfo\n\n" +
                                "ℹ️ Sandbox Tip: If you are using a WhatsApp Test Number inside Meta Sandbox, make sure you have added the recipient number (+${adminPhone}) to your \"To\" dropdown in Meta App Settings! Free-form messages also require the customer to have sent a message to the business in the last 24 hours."
                            } else {
                                "WhatsApp Cloud API is not configured.\n\n" +
                                "🔒 Diagnostics:\n" +
                                "• Phone Number ID: $phoneIdInfo\n" +
                                "• Access Token: $tokenInfo\n\n" +
                                "Please add WHATSAPP_API_TOKEN and WHATSAPP_PHONE_NUMBER_ID to the Secrets panel in the Google AI Studio UI, then restart/recompile the applet."
                            },
                            fontSize = 11.sp,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                            lineHeight = 16.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Continue shopping fish", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HorizontalOrderStatChip(label: String, value: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

// HISTORIC GENERAL LEDGER SCREEN
@Composable
fun OrdersHistoryScreen(
    viewModel: FishViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.allOrders.collectAsState()
    val loggedInUser by viewModel.loggedInUser.collectAsState()
    var searchRefOrEmail by remember { mutableStateOf("") }

    val isAdmin = loggedInUser?.role == UserRole.ADMIN
    val isCustomer = loggedInUser?.role == UserRole.CUSTOMER

    val filteredOrders = remember(orders, searchRefOrEmail, loggedInUser) {
        when {
            isAdmin -> {
                if (searchRefOrEmail.isBlank()) {
                    orders
                } else {
                    orders.filter { order ->
                        order.customerEmail.contains(searchRefOrEmail, ignoreCase = true) ||
                        order.transactionRef.contains(searchRefOrEmail, ignoreCase = true) ||
                        order.customerName.contains(searchRefOrEmail, ignoreCase = true)
                    }
                }
            }
            isCustomer -> {
                val userEmail = loggedInUser?.email.orEmpty().trim().lowercase()
                val userOrders = orders.filter { it.customerEmail.trim().lowercase() == userEmail }
                if (searchRefOrEmail.isBlank()) {
                    userOrders
                } else {
                    userOrders.filter { order ->
                        order.transactionRef.contains(searchRefOrEmail, ignoreCase = true) ||
                        order.fishName.contains(searchRefOrEmail, ignoreCase = true)
                    }
                }
            }
            else -> {
                // Guest Customer: must input search transaction reference.
                if (searchRefOrEmail.isBlank()) {
                    emptyList()
                } else {
                    orders.filter { order ->
                        order.transactionRef.equals(searchRefOrEmail.trim(), ignoreCase = true)
                    }
                }
            }
        }
    }

    val titleText = when {
        isAdmin -> "Audit & Order Ledger"
        isCustomer -> "My Order History"
        else -> "Track Guest Order"
    }

    val subtitleText = when {
        isAdmin -> "Trace customer invoice orders with security cryptographic tokens and shipping logs"
        isCustomer -> "Review status and delivery history of your fresh marine catches selection"
        else -> "Search and track your order dispatch logs using your unique transaction identifier (TXN-xxxx)"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = titleText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = subtitleText,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        val customerAccount by viewModel.loggedInCustomerAccount.collectAsState()

        if (isCustomer) {
            val userEmail = loggedInUser?.email.orEmpty().trim().lowercase()
            val userOrders = orders.filter { it.customerEmail.trim().lowercase() == userEmail }
            val totalOrdersPlaced = userOrders.groupBy { it.transactionRef }.size
            val totalAmountSpent = userOrders.sumOf { it.totalPrice }
            var showEditProfileDialog by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = getOptimizedDisplayName(customerAccount?.name, userEmail),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Registered Email: $userEmail",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.testTag("edit_profile_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile Details",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Default Contact", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = customerAccount?.phone?.ifBlank { "Not provided yet" } ?: "Not provided yet", fontSize = 13.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Delivery Destination", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = customerAccount?.address?.ifBlank { "Not provided yet" } ?: "Not provided yet", fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HorizontalOrderStatChip(label = "Total Orders", value = "$totalOrdersPlaced")
                            HorizontalOrderStatChip(label = "Paid Seafood", value = "₹${String.format(Locale.US, "%.2f", totalAmountSpent)}")
                        }
                        Text(
                            text = "PREMIUM MEMBER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (showEditProfileDialog) {
                var tempName by remember {
                    val activeName = customerAccount?.name?.trim().orEmpty()
                    val initName = if (activeName.isNotBlank() && 
                        activeName != "Registered Member" && 
                        activeName != "Registered Seafood Enthusiast") {
                        activeName
                    } else {
                        ""
                    }
                    mutableStateOf(initName)
                }
                var tempPhone by remember { mutableStateOf(customerAccount?.phone ?: "") }
                var tempAddress by remember { mutableStateOf(customerAccount?.address ?: "") }

                AlertDialog(
                    onDismissRequest = { showEditProfileDialog = false },
                    title = { Text("Update Registered Profile") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = tempName,
                                onValueChange = { tempName = it },
                                label = { Text("Your Full Name") },
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_name"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = tempPhone,
                                onValueChange = { tempPhone = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_phone"),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = tempAddress,
                                onValueChange = { tempAddress = it },
                                label = { Text("Delivery Address (including PIN Code)") },
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_address"),
                                maxLines = 3
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.updateCustomerProfile(tempName.trim(), tempPhone.trim(), tempAddress.trim())
                                showEditProfileDialog = false
                            },
                            modifier = Modifier.testTag("save_profile_btn")
                        ) {
                            Text("Save Changes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditProfileDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Hook for Guest & Members
        OutlinedTextField(
            value = searchRefOrEmail,
            onValueChange = { searchRefOrEmail = it },
            placeholder = { 
                Text(
                    text = when {
                        isAdmin -> "Filter by email, name or transaction ref..."
                        isCustomer -> "Filter by species name or transaction ID..."
                        else -> "Enter Transaction ID (e.g., TXN-XXXXXX)"
                    }
                )
            },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Order lookup tracking tool") },
            trailingIcon = if (searchRefOrEmail.isNotEmpty()) {
                {
                    IconButton(onClick = { searchRefOrEmail = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search filter")
                    }
                }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("orders_tracking_lookup_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchRefOrEmail.isBlank() && loggedInUser == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = "Search Order Illustration",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Track Your Shipment Logs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Please enter your unique Transaction Reference ID above (e.g. TXN-XXXXXX) to monitor dispatch status, payment verification and cold-chain shipping details.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else if (filteredOrders.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "No receipts found",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (searchRefOrEmail.isNotEmpty()) "No matching orders found." else "No transactions logged yet.",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = if (searchRefOrEmail.isNotEmpty()) "Double check your transaction code details." else "Settle a storefront cart, and check your logs here.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        } else {
            val groupedOrders = remember(filteredOrders) {
                filteredOrders
                    .groupBy { it.transactionRef }
                    .toList()
                    .sortedByDescending { (_, list) -> list.firstOrNull()?.timestamp ?: 0L }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(groupedOrders) { (txnRef, ordersInGroup) ->
                    val firstOrder = ordersInGroup.first()
                    // Compute pseudo-live shipping status
                    val orderAgeMs = System.currentTimeMillis() - firstOrder.timestamp
                    val statusText = when {
                        orderAgeMs < 45000 -> "Preparation Dock"
                        orderAgeMs < 120000 -> "Cold Logistic Transit"
                        else -> "Delivered"
                    }
                    val statusColor = when {
                        orderAgeMs < 45000 -> MaterialTheme.colorScheme.primary
                        orderAgeMs < 120000 -> CoralAccent
                        else -> StockGreen
                    }
                    val statusDetails = when {
                        orderAgeMs < 45000 -> "🐟 Preparing Catches (Water tank dispatch)"
                        orderAgeMs < 120000 -> "❄️ In Cold-chain Transit (Oxygenated logistic truck)"
                        else -> "✅ Delivered (Signature verified at dock)"
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = txnRef,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = FontFamily.Monospace
                                )

                                Text(
                                    text = statusText.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(statusColor, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Consolidated Purchased Items list inside grouped card
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Purchased Seafood Items:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                ordersInGroup.forEach { orderItem ->
                                    val actualWeight = orderItem.quantity * 0.5
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "• ${orderItem.fishName}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${String.format(Locale.US, "%.1f", actualWeight)} kg (₹${String.format(Locale.US, "%.2f", orderItem.totalPrice)})",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val format = remember { SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.US) }
                            val dateString = remember(firstOrder.timestamp) { format.format(Date(firstOrder.timestamp)) }

                            Text(
                                text = "Purchased on: $dateString",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )

                            Text(
                                text = "Email: ${firstOrder.customerEmail}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )

                            // Unified Shipping & Delivery tracking status block
                            Spacer(modifier = Modifier.height(8.dp))
                            Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)))
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Live Tracking: $statusDetails",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = statusColor,
                                fontSize = 12.sp
                            )

                            // Show guest checkout logistics meta values (drawn from first order metadata)
                            if (firstOrder.customerName.isNotBlank() || firstOrder.shippingAddress.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "📋 Recipient Logistics:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    val displayName = getOptimizedDisplayName(firstOrder.customerName, firstOrder.customerEmail)
                                    if (displayName.isNotBlank() && displayName != "Registered Member") {
                                        Text("Full Name: $displayName", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                                    }
                                    if (firstOrder.customerPhone.isNotBlank()) {
                                        Text("Contact Phone: ${firstOrder.customerPhone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                                    }
                                    if (firstOrder.shippingAddress.isNotBlank()) {
                                        Text("Delivery Address: ${firstOrder.shippingAddress}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Settled Amount (Invoiced):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                val grandTotal = ordersInGroup.sumOf { it.totalPrice }
                                Text(
                                    text = "₹${String.format(Locale.US, "%.2f", grandTotal)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// LIVE ADMIN MODULE WITH INSTANT FIELD SYNCS
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: FishViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "EkkyAdmin Console",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = CoralAccent
            )
            Text(
                text = "Configure store operations, notifications, and delivery restrictions",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // WhatsApp notification setting controls inside Admin Console
        val adminPhone by viewModel.adminPhone.collectAsState()
        val whatsappNotifyEnabled by viewModel.whatsappNotifyEnabled.collectAsState()
        val whatsappStatus by viewModel.whatsappStatus.collectAsState()
        val whatsappTokenOverride by viewModel.whatsappTokenOverride.collectAsState()
        val whatsappPhoneIdOverride by viewModel.whatsappPhoneIdOverride.collectAsState()
        val whatsappWabaIdOverride by viewModel.whatsappWabaIdOverride.collectAsState()
        val whatsappDatasetIdOverride by viewModel.whatsappDatasetIdOverride.collectAsState()
        val whatsappApiVersion by viewModel.whatsappApiVersion.collectAsState()
        val whatsappTemplateName by viewModel.whatsappTemplateName.collectAsState()
        var editPhoneStr by remember(adminPhone) { mutableStateOf(adminPhone) }
        var isEditingPhone by remember { mutableStateOf(false) }
        var editTokenStr by remember(whatsappTokenOverride) { mutableStateOf(whatsappTokenOverride) }
        var editPhoneIdStr by remember(whatsappPhoneIdOverride) { mutableStateOf(whatsappPhoneIdOverride) }
        var editWabaIdStr by remember(whatsappWabaIdOverride) { mutableStateOf(whatsappWabaIdOverride) }
        var editDatasetIdStr by remember(whatsappDatasetIdOverride) { mutableStateOf(whatsappDatasetIdOverride) }
        var editApiVersionStr by remember(whatsappApiVersion) { mutableStateOf(whatsappApiVersion) }
        var editTemplateNameStr by remember(whatsappTemplateName) { mutableStateOf(whatsappTemplateName) }
        var showOverridesConfig by remember { mutableStateOf(false) }
        var testRecipientPhoneStr by remember(adminPhone) { mutableStateOf(adminPhone.ifBlank { "919884958545" }) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .shadow(1.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "WhatsApp Admin Notifications 📱",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Configure which phone receives the automated customer checkout orders.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Admin WhatsApp Number",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        if (isEditingPhone) {
                            OutlinedTextField(
                                value = editPhoneStr,
                                onValueChange = { input ->
                                    var cleaned = input.filter { it.isDigit() }
                                    if (cleaned.startsWith("91") && cleaned.length > 10) {
                                        cleaned = cleaned.substring(2)
                                    }
                                    if (cleaned.length > 10) {
                                        cleaned = cleaned.take(10)
                                    }
                                    editPhoneStr = cleaned
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .padding(vertical = 2.dp)
                                    .testTag("admin_phone_input_field"),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                placeholder = { Text("e.g. 9840761653") },
                                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                                )
                            )
                        } else {
                            Text(
                                text = if (adminPhone.isBlank()) "Not Set" else "+$adminPhone",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isEditingPhone) {
                            Button(
                                onClick = {
                                    viewModel.updateAdminPhone(editPhoneStr)
                                    isEditingPhone = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StockGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier.height(34.dp).testTag("save_admin_phone_btn")
                            ) {
                                Text("Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    editPhoneStr = adminPhone
                                    isEditingPhone = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CoralAlert),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier.height(34.dp).testTag("cancel_admin_phone_btn")
                            ) {
                                Text("Cancel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { isEditingPhone = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier.height(34.dp).testTag("edit_admin_phone_btn")
                            ) {
                                Text("Change Contact", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Automated WhatsApp Alerts",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Trigger automated WhatsApp Cloud API notifications to Admin on order placement.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }

                    androidx.compose.material3.Switch(
                        checked = whatsappNotifyEnabled,
                        onCheckedChange = { viewModel.setWhatsappNotifyEnabled(it) },
                        modifier = Modifier.testTag("admin_whatsapp_switch")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Advanced credentials accordion
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showOverridesConfig = !showOverridesConfig }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🛠️ Advanced Cloud API Credentials (Optional Overrides)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = if (showOverridesConfig) "▲" else "▼",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                if (showOverridesConfig) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "If you configured WhatsApp credentials in Meta Developers Console, paste them here. No app rebuild required!",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Access Token Input
                            Text(
                                text = "WhatsApp Access Token (Bearer Key Override)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = editTokenStr,
                                onValueChange = { editTokenStr = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("override_token_input"),
                                textStyle = MaterialTheme.typography.bodySmall,
                                placeholder = { Text("EAA...", fontSize = 11.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Phone ID Input
                            Text(
                                text = "WhatsApp Phone Number ID Override (15-digit ID)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = editPhoneIdStr,
                                onValueChange = { editPhoneIdStr = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("override_phoneid_input"),
                                textStyle = MaterialTheme.typography.bodySmall,
                                placeholder = { Text("e.g. 102938475610293", fontSize = 11.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // WABA ID Input
                            Text(
                                text = "WhatsApp Business Account ID Override (WABA ID)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = editWabaIdStr,
                                onValueChange = { editWabaIdStr = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("override_waba_id_input"),
                                textStyle = MaterialTheme.typography.bodySmall,
                                placeholder = { Text("e.g. 293847561029348", fontSize = 11.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Dataset/Pixel ID Input
                            Text(
                                text = "Conversions API Dataset / Pixel ID Override",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = editDatasetIdStr,
                                onValueChange = { editDatasetIdStr = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("override_dataset_id_input"),
                                textStyle = MaterialTheme.typography.bodySmall,
                                placeholder = { Text("e.g. 847561029348576", fontSize = 11.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // API Version Input
                            Text(
                                text = "WhatsApp Cloud API Version (e.g. v25.0, v21.0, v19.0)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = editApiVersionStr,
                                onValueChange = { editApiVersionStr = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("override_api_version_input"),
                                textStyle = MaterialTheme.typography.bodySmall,
                                placeholder = { Text("v25.0", fontSize = 11.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Template Name Input
                            Text(
                                text = "WhatsApp Template Name (Approved Code Template)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = editTemplateNameStr,
                                onValueChange = { editTemplateNameStr = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("override_template_name_input"),
                                textStyle = MaterialTheme.typography.bodySmall,
                                placeholder = { Text("order_confirmed_ekky_fish", fontSize = 11.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.updateWhatsAppOverrides(editTokenStr, editPhoneIdStr, editWabaIdStr, editDatasetIdStr, editApiVersionStr, editTemplateNameStr)
                                    },
                                    enabled = editTokenStr.isNotBlank() || editPhoneIdStr.isNotBlank() || editWabaIdStr.isNotBlank() || editDatasetIdStr.isNotBlank() || editApiVersionStr.isNotBlank() || editTemplateNameStr.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("save_override_credentials_btn")
                                ) {
                                    Text("Apply Override", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                if (whatsappTokenOverride.isNotBlank() || whatsappPhoneIdOverride.isNotBlank() || whatsappWabaIdOverride.isNotBlank() || whatsappDatasetIdOverride.isNotBlank() || whatsappApiVersion != "v25.0" || whatsappTemplateName != "order_confirmed_ekky_fish") {
                                    Button(
                                        onClick = {
                                            viewModel.updateWhatsAppOverrides("", "", "", "", "v25.0", "order_confirmed_ekky_fish")
                                            editTokenStr = ""
                                            editPhoneIdStr = ""
                                            editWabaIdStr = ""
                                            editDatasetIdStr = ""
                                            editApiVersionStr = "v25.0"
                                            editTemplateNameStr = "order_confirmed_ekky_fish"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CoralAlert),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp).testTag("clear_override_credentials_btn")
                                    ) {
                                        Text("Clear & use Defaults", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (editWabaIdStr.isNotBlank() && editDatasetIdStr.isNotBlank()) {
                                    Button(
                                        onClick = {
                                            viewModel.configureEventActivitySharing(editWabaIdStr, editDatasetIdStr)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp).testTag("link_dataset_activity_api_btn")
                                    ) {
                                        Text("Link Dataset (API)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WhatsApp Cloud API Status",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (viewModel.isWhatsAppCloudApiConfigured) {
                                    "Active 🟢 (Credentials detected)"
                                } else {
                                    "Inactive 🔴 (Configure credentials in AI Studio Secrets)"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (viewModel.isWhatsAppCloudApiConfigured) {
                                    Color(0xFF2E7D32)
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                            Text(
                                text = "Test Recipient Phone Number (to) 📱",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            OutlinedTextField(
                                value = testRecipientPhoneStr,
                                onValueChange = { testRecipientPhoneStr = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("test_recipient_phone_field"),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                placeholder = { Text("e.g. 919884958545", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone),
                                singleLine = true,
                                shape = RoundedCornerShape(6.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.sendWhatsAppHelloWorldTemplate(testRecipientPhoneStr)
                                    },
                                    enabled = testRecipientPhoneStr.isNotBlank(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                    modifier = Modifier.height(30.dp).testTag("send_template_whatsapp_btn")
                                ) {
                                    Text("Send Template", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        viewModel.sendWhatsAppNotification(
                                            testRecipientPhoneStr,
                                            "📦 *EkkyFish - ORDER RECEIVED SUCCESSFULLY!* 🐟🚀\n" +
                                            "---------------------------------------------\n" +
                                            "*Order Reference:* EF-TEST${(100000..999999).random()}\n" +
                                            "*Status:* Confirmed (Processing for Dispatch)\n\n" +
                                            "*Delivery Estimate:* Same Day Delivery (Within 3 hours) 🚚💨\n" +
                                            "*Shipping Address:* 12, Marine Drive, Scheme West, Chennai - 600028\n" +
                                            "---------------------------------------------\n" +
                                             "*Items Ordered:*\n" +
                                             "• Fresh Atlantic Salmon (Premium Cut) - 1 kg x 1\n" +
                                             "• Wild Caught Tiger Prawns - 500g x 2\n\n" +
                                             "*Subtotal:* ₹1,450.00\n" +
                                             "*Delivery Fee:* ₹50.00\n" +
                                             "*Total Amount Paid:* *₹1,500.00* (via UPI)\n" +
                                             "---------------------------------------------\n" +
                                             "Thank you for ordering your fresh catch from EkkyFish! 🌊❄\n" +
                                             "For support/tracking, contact +91 98407 61653"
                                        )
                                    },
                                    enabled = testRecipientPhoneStr.isNotBlank(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                    modifier = Modifier.height(30.dp).testTag("send_test_whatsapp_btn")
                                ) {
                                    Text("Send Text", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 *Why do I get the welcome message?*\n" +
                                        "• *Send Template*: Facebook Cloud API dictates the template layout. Since 'hello_world' is a static preset on Facebook's servers, its greeting cannot be customized from our Android code.\n" +
                                        "• *Send Text*: Sends our custom 'Order Received' billing structure directly. (Per Meta policies, free-form text requires the recipient to have messaged your business in the last 24 hours to open a customer session).",
                                fontSize = 9.sp,
                                lineHeight = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Credential Verification Details & Diagnostics Card
                    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                    var curlCopiedMsg by remember { mutableStateOf("") }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Loaded Runtime Configuration",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Active Phone ID:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(viewModel.activePhoneIdDisplay.toString(), fontSize = 9.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Active Auth Token:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(viewModel.activeTokenMasked.toString(), fontSize = 9.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Active API Version:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(whatsappApiVersion, fontSize = 9.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Active Template Name:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(whatsappTemplateName, fontSize = 9.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Diagnostics:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )

                                TextButton(
                                    onClick = {
                                        val token = viewModel.getActiveWhatsAppToken()
                                        val phoneId = viewModel.getActiveWhatsAppPhoneId()
                                        val cleanToPhone = testRecipientPhoneStr.replace(Regex("[^0-9]"), "")
                                        var formattedToPhone = cleanToPhone
                                        if (formattedToPhone.length == 10 && (formattedToPhone.startsWith("6") || formattedToPhone.startsWith("7") || formattedToPhone.startsWith("8") || formattedToPhone.startsWith("9"))) {
                                            formattedToPhone = "91$formattedToPhone"
                                        }
                                        val curl = "curl --request POST \\\n" +
                                                "  --url https://graph.facebook.com/$whatsappApiVersion/$phoneId/messages \\\n" +
                                                "  --header \"Authorization: Bearer $token\" \\\n" +
                                                "  --header \"Content-Type: application/json\" \\\n" +
                                                "  --data '{\n" +
                                                "  \"messaging_product\": \"whatsapp\",\n" +
                                                "  \"to\": \"$formattedToPhone\",\n" +
                                                "  \"type\": \"template\",\n" +
                                                "  \"template\": {\n" +
                                                "    \"name\": \"$whatsappTemplateName\",\n" +
                                                "    \"language\": {\n" +
                                                "      \"code\": \"en_US\"\n" +
                                                "    },\n" +
                                                "    \"components\": [\n" +
                                                "      {\n" +
                                                "        \"type\": \"body\",\n" +
                                                "        \"parameters\": [\n" +
                                                "          {\n" +
                                                "            \"type\": \"text\",\n" +
                                                "            \"text\": \"John Doe\"\n" +
                                                "          },\n" +
                                                "          {\n" +
                                                "            \"type\": \"text\",\n" +
                                                "            \"text\": \"EF-TEST987412\"\n" +
                                                "          }\n" +
                                                "        ]\n" +
                                                "      }\n" +
                                                "    ]\n" +
                                                "  }\n" +
                                                "}'"
                                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(curl))
                                        curlCopiedMsg = "📋 Template curl copied to clipboard!"
                                    },
                                    modifier = Modifier.height(24.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                ) {
                                    Text("Copy Curl (Template)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                TextButton(
                                    onClick = {
                                        val token = viewModel.getActiveWhatsAppToken()
                                        val phoneId = viewModel.getActiveWhatsAppPhoneId()
                                        val cleanToPhone = testRecipientPhoneStr.replace(Regex("[^0-9]"), "")
                                        var formattedToPhone = cleanToPhone
                                        if (formattedToPhone.length == 10 && (formattedToPhone.startsWith("6") || formattedToPhone.startsWith("7") || formattedToPhone.startsWith("8") || formattedToPhone.startsWith("9"))) {
                                            formattedToPhone = "91$formattedToPhone"
                                        }
                                        val curl = "curl --request POST \\\n" +
                                                "  --url https://graph.facebook.com/$whatsappApiVersion/$phoneId/messages \\\n" +
                                                "  --header \"Authorization: Bearer $token\" \\\n" +
                                                "  --header \"Content-Type: application/json\" \\\n" +
                                                "  --data '{\n" +
                                                "  \"messaging_product\": \"whatsapp\",\n" +
                                                "  \"to\": \"$formattedToPhone\",\n" +
                                                "  \"type\": \"text\",\n" +
                                                "  \"text\": {\n" +
                                                "    \"body\": \"📦 *EkkyFish - ORDER RECEIVED SUCCESSFULLY!* 🐟🚀\\n---------------------------------------------\\n*Order Reference:* EF-TEST984218\\n*Status:* Confirmed (Processing for Dispatch)\\n\\n*Delivery Estimate:* Same Day Delivery (Within 3 hours) 🚚💨\\n*Shipping Address:* 12, Marine Drive, Scheme West, Chennai - 600028\\n---------------------------------------------\\n*Items Ordered:*\\n• Fresh Atlantic Salmon (Premium Cut) - 1 kg x 1\\n• Wild Caught Tiger Prawns - 500g x 2\\n\\n*Subtotal:* ₹1,450.00\\n*Delivery Fee:* ₹50.00\\n*Total Amount Paid:* *₹1,500.00* (via UPI)\\n---------------------------------------------\\nThank you for ordering your fresh catch from EkkyFish! 🌊❄\\nFor support/tracking, contact +91 98407 61653\"\n" +
                                                "  }\n" +
                                                "}'"
                                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(curl))
                                        curlCopiedMsg = "📋 Text message curl copied!"
                                    },
                                    modifier = Modifier.height(24.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                ) {
                                    Text("Copy Curl (Text Message)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (curlCopiedMsg.isNotBlank()) {
                                Text(
                                    text = curlCopiedMsg,
                                    color = Color(0xFF1B5E20),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                androidx.compose.runtime.LaunchedEffect(curlCopiedMsg) {
                                    kotlinx.coroutines.delay(3500)
                                    curlCopiedMsg = ""
                                }
                            }
                        }
                    }

                    whatsappStatus?.let { status ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (status.contains("successfully", ignoreCase = true)) {
                                    Color(0xFFE8F5E9)
                                } else {
                                    Color(0xFFFFEBEE)
                                }
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "API Output:\n$status",
                                fontSize = 10.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = if (status.contains("successfully", ignoreCase = true)) {
                                    Color(0xFF1B5E20)
                                } else {
                                    Color(0xFFB71C1C)
                                },
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        // Helpful troubleshooting tip widget
                        val tipText = when {
                            status.contains("401", ignoreCase = true) || status.contains("Bearer", ignoreCase = true) -> {
                                "💡 TIP: HTTP 401 indicates your WhatsApp Access Token is invalid or expired. Remember, developer Temporary Access Tokens expire in 24 hours. Copy a fresh one from Meta Developers console, or configure a Permanent System User Token."
                            }
                            status.contains("400", ignoreCase = true) && (status.contains("131030") || status.contains("131047") || status.contains("24 hours")) -> {
                                "💡 TIP: WhatsApp rules prevent sending freeform Text messages unless the receiver has messaged your number first in the past 24 hours. To resolve this:\n1. Send 'Hi' from your personal phone (+$adminPhone) to your Meta Sandboxed WhatsApp Sender number.\n2. Or, click \"Send Template\" above, which sends the pre-approved template and always works!"
                            }
                            status.contains("400", ignoreCase = true) && (status.contains("recipient", ignoreCase = true) || status.contains("allowed", ignoreCase = true)) -> {
                                "💡 TIP: In Meta Sandbox accounts, you can only send messages to verified recipient numbers. Ensure +$adminPhone is listed in your Sandbox 'To' list on the Meta Developer Dashboard."
                            }
                            status.contains("404", ignoreCase = true) -> {
                                "💡 TIP: HTTP 404 means your WhatsApp Phone Number ID is invalid or incorrect. Check the Phone Number ID on Meta's WhatsApp Getting Started dashboard (it's a 15-digit ID, not the phone number itself)."
                            }
                            status.contains("successfully", ignoreCase = true) -> {
                                "✅ Integration Success! Delivery configurations are correct."
                            }
                            else -> {
                                "💡 TIP: Check your WHATSAPP_API_TOKEN, WHATSAPP_PHONE_NUMBER_ID configs, and confirm the recipient phone number is correct."
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tipText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (status.contains("successfully", ignoreCase = true)) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val adminPincodeConfig by viewModel.deliveryPincode.collectAsState()
        var editPincodeStr by remember(adminPincodeConfig) { mutableStateOf(adminPincodeConfig) }
        var isEditingPincode by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .shadow(1.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Delivery Pincode Control 📍",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Specify the exact PIN code(s) or PIN prefix(es) allowed for customer orders. Use a comma-separated list to support multiple (e.g. '600001, 600002, 600028'). Leave empty to allow any PIN.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Serviceable Pincode(s)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        if (isEditingPincode) {
                            OutlinedTextField(
                                value = editPincodeStr,
                                onValueChange = { editPincodeStr = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .padding(vertical = 2.dp)
                                    .testTag("admin_pincode_config_input"),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                placeholder = { Text("e.g. 600001, 600") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                                )
                            )
                        } else {
                            Text(
                                text = if (adminPincodeConfig.isBlank()) "Any (No limits)" else adminPincodeConfig,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isEditingPincode) {
                            Button(
                                onClick = {
                                    viewModel.setDeliveryPincode(editPincodeStr)
                                    isEditingPincode = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StockGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier.height(34.dp).testTag("save_admin_pincode_btn")
                            ) {
                                Text("Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    editPincodeStr = adminPincodeConfig
                                    isEditingPincode = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CoralAlert),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier.height(34.dp).testTag("cancel_admin_pincode_btn")
                            ) {
                                Text("Cancel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { isEditingPincode = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier.height(34.dp).testTag("edit_admin_pincode_btn")
                            ) {
                                Text("Change Limit", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// COMPASS_STUDIO CATALOGUE MANAGEMENT SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    viewModel: FishViewModel,
    modifier: Modifier = Modifier
) {
    val fishItems by viewModel.allFishItems.collectAsState()
    var isAddOpen by remember { mutableStateOf(false) }
    var showResetConfirmation by remember { mutableStateOf(false) }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = {
                Text(text = "Reset Database Catalog?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            },
            text = {
                Text(text = "Are you sure you want to reset all fish items and stock levels back to default catalog settings? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetToDefaults()
                        showResetConfirmation = false
                    }
                ) {
                    Text("Yes, Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetConfirmation = false }
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Product Catalog Editor",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoralAccent
                )
                Text(
                    text = "Instantly modify prices, stocks and live products",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            // Restore defaults
            IconButton(
                onClick = { showResetConfirmation = true },
                modifier = Modifier
                    .shadow(1.dp, CircleShape)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Restore initial catalog lists",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Open quick add
        Button(
            onClick = { isAddOpen = true },
            colors = ButtonDefaults.buttonColors(containerColor = CoralAccent),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("admin_add_species_trigger"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Insert custom species",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add new Marine Catch Species",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(fishItems, key = { it.id }) { fish ->
                AdminProductRow(
                    fish = fish,
                    onUpdate = { updatedItem -> viewModel.updateFishDetails(updatedItem) },
                    onDelete = { viewModel.deleteProduct(fish) }
                )
            }
        }
    }

    if (isAddOpen) {
        AddNewProductDialog(
            onDismiss = { isAddOpen = false },
            onAdd = { n, t, c, d, p, s, img, status ->
                viewModel.addNewProduct(n, t, c, d, p, s, img, status)
                isAddOpen = false
            }
        )
    }
}

// INSTANT EDITOR ROW FOR CLASSIFIED SPECIES
@Composable
fun AdminProductRow(
    fish: FishItem,
    onUpdate: (FishItem) -> Unit,
    onDelete: () -> Unit
) {
    var editMode by remember { mutableStateOf(false) }

    var tempName by remember(fish) { mutableStateOf(fish.name) }
    var tempTamilName by remember(fish) { mutableStateOf(fish.tamilName) }
    var tempPriceStr by remember(fish) { mutableStateOf(fish.price.toInt().toString()) }
    var tempStockStr by remember(fish) { mutableStateOf(String.format(Locale.US, "%.1f", fish.availableQuantity * 0.5)) }
    var tempStatus by remember(fish) { mutableStateOf(fish.availabilityStatus) }
    var tempImageResName by remember(fish) { mutableStateOf(fish.imageResName) }
    var tempDesc by remember(fish) { mutableStateOf(fish.description) }
    var tempCategory by remember(fish) { mutableStateOf(fish.category) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            tempImageResName = uri.toString()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val uri = saveBitmapToCache(context, bitmap)
            if (uri != null) {
                tempImageResName = uri.toString()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("admin_product_row_${fish.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (!editMode) {
                // Static read-only view of product
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SeafoodCanvasIcon(
                        imageResName = fish.imageResName,
                        modifier = Modifier.size(46.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(fish.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "Category: ${fish.category} | ${fish.tamilName}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "₹${fish.price.toInt()}/kg",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Stock: ${String.format(Locale.US, "%.1f", fish.availableQuantity * 0.5)} kg",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            val statusColor = when (fish.availabilityStatus) {
                                "Available" -> StockGreen
                                "Sold Out" -> CoralAccent
                                else -> CoralAlert
                            }
                            Text(
                                text = fish.availabilityStatus,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier
                                    .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { editMode = true },
                        modifier = Modifier.size(36.dp).testTag("edit_mode_btn_${fish.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit product details",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp).testTag("delete_mode_btn_${fish.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete from inventory shelf",
                            tint = CoralAlert,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                // Interactive extended edit form inside card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Edit Species Details 🐟",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = { editMode = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel edit mode", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Name & Scientific Name fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Product Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        val isNameError = tempName.isBlank()
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("name_edit_${fish.id}"),
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            isError = isNameError,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tamil Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = tempTamilName,
                            onValueChange = { tempTamilName = it },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("tamil_name_edit_${fish.id}"),
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Price & Stock fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unit Price (₹)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = tempPriceStr,
                            onValueChange = { tempPriceStr = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("price_edit_${fish.id}"),
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Inventory (kg)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = tempStockStr,
                            onValueChange = { tempStockStr = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("stock_edit_${fish.id}"),
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category row selection
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val cats = listOf("Marine", "Freshwater", "Shellfish")
                        cats.forEach { cat ->
                            val isCatSelected = tempCategory == cat
                            val bgColor = if (isCatSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                            val txtColor = if (isCatSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = bgColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { tempCategory = cat }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(cat, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = txtColor)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status row selection
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Availability Status",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val statusOptions = listOf("Available", "Sold Out", "Not Available")
                        statusOptions.forEach { status ->
                            val isSelected = tempStatus == status
                            val chipColor = if (isSelected) {
                                when (status) {
                                    "Available" -> StockGreen
                                    "Sold Out" -> CoralAccent
                                    "Not Available" -> CoralAlert
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            } else {
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                            }
                            val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            val label = if (status == "Available") "Available" else status

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = chipColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        tempStatus = status
                                        if (status == "Sold Out") {
                                            tempStockStr = "0"
                                        } else if (status == "Available" && (tempStockStr.toIntOrNull() ?: 0) <= 0) {
                                            tempStockStr = "12"
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Modify Product Image
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Modify Product Image", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeafoodCanvasIcon(
                            imageResName = tempImageResName,
                            modifier = Modifier.size(36.dp)
                        )

                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("📁 Storage", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                if (hasPermission) {
                                    cameraLauncher.launch(null)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("📸 Camera", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Catch Description field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Catch Description", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = tempDesc,
                        onValueChange = { tempDesc = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        textStyle = MaterialTheme.typography.bodySmall,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save & Cancel CTA Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { editMode = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    val isValid = tempName.isNotBlank() && tempPriceStr.toDoubleOrNull() != null && tempStockStr.toDoubleOrNull() != null
                    Button(
                        onClick = {
                            if (isValid) {
                                val finalPrice = tempPriceStr.toDoubleOrNull() ?: fish.price
                                val finalStock = ((tempStockStr.toDoubleOrNull() ?: (fish.availableQuantity * 0.5)) * 2).toInt()
                                onUpdate(
                                    fish.copy(
                                        name = tempName.trim(),
                                        tamilName = tempTamilName.trim(),
                                        price = finalPrice,
                                        availableQuantity = finalStock,
                                        category = tempCategory,
                                        availabilityStatus = tempStatus,
                                        imageResName = tempImageResName,
                                        description = tempDesc.trim(),
                                        isAvailable = finalStock > 0 && tempStatus == "Available"
                                    )
                                )
                                editMode = false
                            }
                        },
                        enabled = isValid,
                        colors = ButtonDefaults.buttonColors(containerColor = StockGreen),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// QUICK DIALOG PANEL FOR CREATING NOVEL ENTRIES
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewProductDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, Double, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var tamilName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Marine") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var stockStr by remember { mutableStateOf("") }
    var imageKey by remember { mutableStateOf("salmon") }
    var availabilityStatus by remember { mutableStateOf("Available") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageKey = uri.toString()
        }
    }

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val uri = saveBitmapToCache(context, bitmap)
            if (uri != null) {
                imageKey = uri.toString()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    val categories = listOf("Marine", "Freshwater", "Shellfish")
    val iconsKeys = listOf("salmon", "tuna", "snapper", "prawn", "lobster", "trout", "octopus")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(10.dp, RoundedCornerShape(20.dp))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Add Marine Species", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close insert panel")
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Species Commercial Name") },
                        modifier = Modifier.fillMaxWidth().testTag("add_fish_name_field"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = tamilName,
                        onValueChange = { tamilName = it },
                        label = { Text("Species Tamil Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                item {
                    Text("Habitat Classification", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val selected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .clickable { selectedCategory = cat }
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            ) {
                                Text(
                                    text = cat,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Product Image", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("📁 Storage", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                if (hasPermission) {
                                    cameraLauncher.launch(null)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("📸 Camera", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Preview Box
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            SeafoodCanvasIcon(
                                imageResName = imageKey,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    if (imageKey.startsWith("content://") || imageKey.startsWith("file://") || imageKey.startsWith("http")) {
                        Text(
                            text = "✓ Custom image uploaded & active",
                            color = StockGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Text(
                            text = "Or choose a preset illustrator profile below:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(iconsKeys) { key ->
                            val selected = imageKey == key
                            Surface(
                                shape = CircleShape,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable { imageKey = key }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = key.take(2).uppercase(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price per kg (₹)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_fish_price_field"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it },
                        label = { Text("Available Stock (kg)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_fish_stock_field"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                item {
                    Text("Initial Availability", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val statusOptions = listOf("Available", "Sold Out", "Not Available")
                        statusOptions.forEach { status ->
                            val selected = availabilityStatus == status
                            val chipColor = if (selected) {
                                when (status) {
                                    "Available" -> StockGreen
                                    "Sold Out" -> CoralAccent
                                    "Not Available" -> CoralAlert
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            } else {
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                            }
                            val textColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                            val label = if (status == "Available") "Available (Add Catch)" else status

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = chipColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { 
                                        availabilityStatus = status 
                                        if (status == "Sold Out") {
                                            stockStr = "0"
                                        } else if (status == "Available" && (stockStr.toDoubleOrNull() ?: 0.0) <= 0.0) {
                                            stockStr = "10"
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Catch Notes & Gourmet Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 3
                    )
                }

                item {
                    Button(
                        onClick = {
                            val priceVal = priceStr.toDoubleOrNull() ?: 10.0
                            val stockValInKg = stockStr.toDoubleOrNull() ?: 5.0
                            val stockVal = (stockValInKg * 2).toInt()
                            if (name.isNotBlank()) {
                                onAdd(name, tamilName, selectedCategory, description, priceVal, stockVal, imageKey, availabilityStatus)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("add_new_fish_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = StockGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add species icon",
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Catch Species to Shelf", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: FishViewModel,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var passcodeVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var isRegisterMode by remember { mutableStateOf(false) }
    var showGoogleSSODialog by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
// No popup showing required in screen anymore
    
    val loginError by viewModel.loginError.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // Minimalist circular Back button targeting native feel
        IconButton(
            onClick = { viewModel.navigateTo(NavigationScreen.STORE) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .size(40.dp)
                .shadow(2.dp, CircleShape)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .testTag("login_back_btn")
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back to Home",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        // App Identity Brand
        Image(
            painter = painterResource(id = com.example.R.drawable.img_ekkyfish_logo),
            contentDescription = "EkkyFish Logo",
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "EkkyFish Store",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Ocean To Home Fresh Meat",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (selectedRole == UserRole.ADMIN) "Admin Sign In" else if (isRegisterMode) "Register Customer Account" else "Customer Sign In",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Beautiful role switch bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isCustomer = selectedRole == UserRole.CUSTOMER
                    
                    // Customer select button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { 
                                selectedRole = UserRole.CUSTOMER 
                            }
                            .testTag("role_customer_tab"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCustomer) MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Customer",
                                color = if (isCustomer) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    
                    // Admin select button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { 
                                selectedRole = UserRole.ADMIN
                                isRegisterMode = false
                            }
                            .testTag("role_admin_tab"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isCustomer) MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Merchant Admin",
                                color = if (!isCustomer) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Beautiful Customer Sign In / Register Tab
                if (selectedRole == UserRole.CUSTOMER) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val isLogin = !isRegisterMode
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isRegisterMode = false },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isLogin) MaterialTheme.colorScheme.primary.copy(alpha = 0.85f) else Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Sign In",
                                    color = if (isLogin) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isRegisterMode = true },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isRegisterMode) MaterialTheme.colorScheme.primary.copy(alpha = 0.85f) else Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Register",
                                    color = if (isRegisterMode) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Customer Register Fields (Name and Phone)
                if (selectedRole == UserRole.CUSTOMER && isRegisterMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        placeholder = { Text("John Doe") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val isRegisterPhoneError = phone.isNotBlank() && phone.length != 10
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { input ->
                            var cleaned = input.filter { it.isDigit() }
                            if (cleaned.startsWith("91") && cleaned.length > 10) {
                                cleaned = cleaned.substring(2)
                            }
                            if (cleaned.length > 10) {
                                cleaned = cleaned.take(10)
                            }
                            phone = cleaned
                        },
                        label = { Text("Mobile No") },
                        placeholder = { Text("e.g. 9876543210") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_phone_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = isRegisterPhoneError,
                        supportingText = if (isRegisterPhoneError) {
                            { Text("Strictly enter a 10 digit number", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (isRegisterMode && name.trim().isBlank()) {
                            val suggested = getOptimizedDisplayName("", it)
                            if (suggested != "Registered Member") {
                                name = suggested
                            }
                        }
                    },
                    label = { Text("Email ID") },
                    placeholder = { Text("buyer@ekkyfish.com") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )

                // Direct inline selection chips for quick completion
                if (selectedRole != UserRole.ADMIN) {
                    EmailExtensionChips(
                        email = email,
                        onEmailUpdated = {
                            email = it
                            if (isRegisterMode && name.trim().isBlank()) {
                                val suggested = getOptimizedDisplayName("", it)
                                if (suggested != "Registered Member") {
                                    name = suggested
                                }
                            }
                        }
                    )
                }

                if (selectedRole == UserRole.ADMIN) {
                    val isAdminEmailCorrect = email.trim().lowercase() == "ekkyfish@gmail.com" || 
                                              email.trim().lowercase() == "admin@ekkyfish.com" || 
                                              email.trim().lowercase() == "merchant@ekkyfish.com" || 
                                              (email.trim().lowercase().contains("admin") && email.trim().contains("@"))
                    if (!isAdminEmailCorrect) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Security Gateway",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Merchant Verification Required",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "To protect administration portal access, please type the verified Merchant Admin Email ID to reveal the secure password input screen.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                        .clickable { 
                                            email = "ekkyfish@gmail.com"
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "💡 Autofill registered: ekkyfish@gmail.com",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                
                val showPasswordFields = if (selectedRole != UserRole.ADMIN) true else {
                    val clean = email.trim().lowercase()
                    clean == "ekkyfish@gmail.com" || clean == "admin@ekkyfish.com" || clean == "merchant@ekkyfish.com" || (clean.isNotBlank() && clean.contains("admin") && clean.contains("@"))
                }

                AnimatedVisibility(
                    visible = showPasswordFields,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Password entry fields
                        OutlinedTextField(
                            value = passcode,
                            onValueChange = { passcode = it },
                            label = { Text(if (isRegisterMode) "Choose Password" else "Password") },
                            placeholder = { Text(if (selectedRole == UserRole.ADMIN) "Hint: admin123" else "Enter password") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Password") },
                            visualTransformation = if (passcodeVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passcodeVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                val description = if (passcodeVisible) "Hide password" else "Show password"
                                IconButton(onClick = { passcodeVisible = !passcodeVisible }) {
                                    Icon(imageVector = image, contentDescription = description)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )
                        )

                        // Forgot Password link option (only for customer login mode)
                        if (selectedRole == UserRole.CUSTOMER && !isRegisterMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { showForgotPasswordDialog = true },
                                    modifier = Modifier.testTag("forgot_passcode_btn")
                                ) {
                                    Text(
                                        text = "Forgot Password?",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        if (selectedRole == UserRole.ADMIN) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 Password Hint: 'admin123' for verification",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Error warning box
                        loginError?.let { err ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CoralAlert.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Warning error icon",
                                    tint = CoralAlert,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = err,
                                    color = CoralAlert,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        // Proceed / Submit button
                        Button(
                            onClick = {
                                if (selectedRole == UserRole.CUSTOMER && isRegisterMode) {
                                    viewModel.registerCustomer(name, email, phone, passcode) { success ->
                                        if (success) {
                                            name = ""
                                            phone = ""
                                            email = ""
                                            passcode = ""
                                        }
                                    }
                                } else {
                                    viewModel.login(email.trim(), passcode, selectedRole)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isRegisterMode) {
                                    "Register & Sign In"
                                } else {
                                    "Sign In"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                if (selectedRole == UserRole.CUSTOMER) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                        Text(
                            text = if (isRegisterMode) "Already have an account? Sign In" else "New customer? Create registered account",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (selectedRole == UserRole.CUSTOMER && !isRegisterMode) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f).height(1.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        ) {}
                        Text(
                            text = "OR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Surface(
                            modifier = Modifier.weight(1f).height(1.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        ) {}
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Google Sign-In SSO Button
                    Button(
                        onClick = {
                            showGoogleSSODialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("google_sso_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            GoogleIcon(modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Continue with Google SSO",
                                color = Color(0xFF1F1F1F),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
    }

    // Removed EmailExtensionPopup call since it is now inline

    if (showGoogleSSODialog) {
        GoogleSSODialog(
            onDismiss = { showGoogleSSODialog = false },
            onAccountSelected = { gmailAddress, displayName ->
                showGoogleSSODialog = false
                viewModel.loginWithGoogle(gmailAddress, displayName)
            }
        )
    }

    if (showForgotPasswordDialog) {
        var forgotEmail by remember { mutableStateOf("") }
        val forgotResult by viewModel.forgotPasswordResult.collectAsState()

        AlertDialog(
            onDismissRequest = { 
                showForgotPasswordDialog = false 
                viewModel.clearForgotPasswordResult()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Forgot Password",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Forgot Password Recovery", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter your registered email address. We will look up your seafood subscriber account and simulate securely transmitting an invoice recovery mail.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text("Registered Email Address") },
                        placeholder = { Text("customer@gmail.com") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
                        modifier = Modifier.fillMaxWidth().testTag("forgot_email_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Inline directly selectable email extension chips
                    EmailExtensionChips(
                        email = forgotEmail,
                        onEmailUpdated = { forgotEmail = it }
                    )

                    forgotResult?.let { res ->
                        val isSuccess = res.startsWith("SUCCESS")
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSuccess) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color(0xFFFFEBEE),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = "Status Icon",
                                        tint = if (isSuccess) MaterialTheme.colorScheme.primary else Color(0xFFD32F2F),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isSuccess) "Recovery Dispatch Sent!" else "Recovery Failed",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSuccess) MaterialTheme.colorScheme.primary else Color(0xFFD32F2F)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = res,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                val isSuccess = forgotResult?.startsWith("SUCCESS") == true
                if (isSuccess) {
                    Button(
                        onClick = {
                            showForgotPasswordDialog = false
                            viewModel.clearForgotPasswordResult()
                        },
                        modifier = Modifier.testTag("forgot_close_btn")
                    ) {
                        Text("Done")
                    }
                } else {
                    Button(
                        onClick = {
                            if (forgotEmail.isNotBlank()) {
                                viewModel.forgotPassword(forgotEmail)
                            }
                        },
                        enabled = forgotEmail.isNotBlank(),
                        modifier = Modifier.testTag("forgot_send_mail_btn")
                    ) {
                        Text("Send Password Recovery Mail")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showForgotPasswordDialog = false
                        viewModel.clearForgotPasswordResult()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun GoogleIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val sizePx = size.width
        val strokeWidth = sizePx * 0.22f
        val center = Offset(sizePx / 2f, sizePx / 2f)
        
        // Red segment (top-left)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 180f,
            sweepAngle = 100f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Yellow segment (bottom-left)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 110f,
            sweepAngle = 75f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Green segment (bottom-right)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 0f,
            sweepAngle = 115f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Blue segment (top-right)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 275f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Horizontal bar of 'G'
        val barLength = sizePx * 0.45f
        val barThickness = strokeWidth
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = Offset(center.x, center.y - barThickness / 2f),
            size = Size(barLength, barThickness)
        )
    }
}

@Composable
fun AnimatedSoldOutStamp(
    sizeDp: androidx.compose.ui.unit.Dp = 100.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sold_out_pulse_loop")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = -9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotationAnim
            }
            .size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        // Red dynamic rubber stamp circle borders matching reference image
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthOuter = 3.dp.toPx()
            val strokeWidthInner = 1.2.dp.toPx()
            val center = Offset(size.width / 2f, size.height / 2f)
            val minSize = size.minDimension

            // Outer circle stamp border
            drawCircle(
                color = CoralAlert,
                radius = (minSize / 2f) - (strokeWidthOuter / 2f),
                style = Stroke(width = strokeWidthOuter)
            )

            // Inner circle stamp border
            drawCircle(
                color = CoralAlert,
                radius = (minSize / 2f) * 0.72f,
                style = Stroke(width = strokeWidthInner)
            )
        }

        // Circular aligned text and stars inside the stamp
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "OUT OF",
                color = CoralAlert,
                fontSize = (sizeDp.value * 0.08f).sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (sizeDp.value * 0.12f).dp)
            )
            Text(
                text = "STOCK",
                color = CoralAlert,
                fontSize = (sizeDp.value * 0.08f).sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -(sizeDp.value * 0.12f).dp)
            )

            // Distressed rubber stamp stars decoration on the sides
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (sizeDp.value * 0.18f).dp)
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("★", color = CoralAlert, fontSize = (sizeDp.value * 0.09f).sp, fontWeight = FontWeight.Bold)
                Text("★", color = CoralAlert, fontSize = (sizeDp.value * 0.09f).sp, fontWeight = FontWeight.Bold)
            }
        }

        // Bold off-axis diagonal rectangular plate overlay: "SOLD OUT"
        Box(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .height((sizeDp.value * 0.28f).dp)
                .graphicsLayer {
                    rotationZ = -6f
                }
                .background(CoralAlert, RoundedCornerShape(2.dp))
                .border(2.dp, Color.White, RoundedCornerShape(2.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SOLD OUT",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = (sizeDp.value * 0.14f).sp,
                letterSpacing = 1.5.sp,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.3f),
                        offset = Offset(1.5f, 1.5f),
                        blurRadius = 1.5f
                    )
                )
            )
        }
    }
}

@Composable
fun EmailExtensionChips(
    email: String,
    onEmailUpdated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val extensions = listOf("@gmail.com", "@yahoo.com", "@outlook.com", "@hotmail.com", "@icloud.com")
    val baseUsername = remember(email) {
        if (email.contains("@")) email.substringBefore("@") else email
    }

    if (baseUsername.isNotBlank()) {
        Column(modifier = modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                extensions.forEach { ext ->
                    val isApplied = email.endsWith(ext)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isApplied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isApplied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .clickable {
                                onEmailUpdated(baseUsername + ext)
                            }
                            .testTag("email_ext_chip_${ext.replace("@", "").replace(".", "_")}")
                    ) {
                        Text(
                            text = ext,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isApplied) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleSSODialog(
    onDismiss: () -> Unit,
    onAccountSelected: (String, String) -> Unit
) {
    var isAddingAccount by remember { mutableStateOf(false) }
    var customEmail by remember { mutableStateOf("") }
    var customName by remember { mutableStateOf("") }
    var isAuthenticating by remember { mutableStateOf(false) }
    var selectedEmailState by remember { mutableStateOf("") }
    var selectedNameState by remember { mutableStateOf("") }

    androidx.compose.runtime.LaunchedEffect(isAuthenticating) {
        if (isAuthenticating) {
            kotlinx.coroutines.delay(1200)
            onAccountSelected(selectedEmailState, selectedNameState)
        }
    }

    Dialog(onDismissRequest = { if (!isAuthenticating) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Google Logo
                GoogleIcon(modifier = Modifier.size(32.dp))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isAuthenticating) {
                    Text(
                        text = "Signing you in...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Establishing secure workspace handshake for $selectedEmailState...",
                        fontSize = 13.sp,
                        color = Color(0xFF5F6368),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    CircularProgressIndicator(
                        color = Color(0xFF4285F4),
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else if (!isAddingAccount) {
                    Text(
                        text = "Choose an account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F1F1F)
                    )
                    Text(
                        text = "to continue to EkkyFish Store",
                        fontSize = 14.sp,
                        color = Color(0xFF5F6368)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Account option 1: ekkyfish@gmail.com
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedEmailState = "ekkyfish@gmail.com"
                                selectedNameState = "Ekky Fish"
                                isAuthenticating = true
                            }
                            .testTag("google_acct_ekkyfish"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar circle representing the account
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = Color(0xFF0F9D58) // Nice emerald green
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "E",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Ekky Fish",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF3C4043)
                                )
                                Text(
                                    text = "ekkyfish@gmail.com",
                                    fontSize = 12.sp,
                                    color = Color(0xFF5F6368)
                                )
                            }
                        }
                    }
                    
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = Color(0xFFE8EAED)
                    ) {}
                    
                    // Add another account
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isAddingAccount = true }
                            .testTag("google_acct_add_new"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add account",
                                tint = Color(0xFF1A73E8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Use another account",
                                fontSize = 14.sp,
                                color = Color(0xFF1A73E8),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "To configure a production-ready Google Workspace integration, register your debug keystore's SHA-1 fingerprint inside Google Cloud Console.",
                        fontSize = 10.sp,
                        color = Color(0xFF7F868F),
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Custom Google account entry
                    Text(
                        text = "Add Google Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Profile Name", color = Color(0xFF5F6368)) },
                        placeholder = { Text("e.g. Jean Dupont") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = customEmail,
                        onValueChange = { customEmail = it },
                        label = { Text("Google Gmail Address", color = Color(0xFF5F6368)) },
                        placeholder = { Text("example@gmail.com") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { isAddingAccount = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFF5F6368))
                        ) {
                            Text("Back", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (customEmail.isNotBlank() && customEmail.contains("@")) {
                                    selectedEmailState = customEmail.trim()
                                    selectedNameState = if (customName.isBlank()) "Google User" else customName.trim()
                                    isAuthenticating = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                            shape = RoundedCornerShape(8.dp),
                            enabled = customEmail.contains("@")
                        ) {
                            Text("Continue", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
