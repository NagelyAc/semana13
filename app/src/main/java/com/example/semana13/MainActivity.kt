package com.example.semana13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

// -----------------------------
// MODELOS
// -----------------------------
data class Platform(val id: Int, val x: Float, var y: Float, val widthPx: Float, val heightPx: Float = 18f)

// -----------------------------
// PANTALLAS / ESTADOS
// -----------------------------
enum class ScreenState { MENU, GAME, GAME_OVER }

// -----------------------------
// ACTIVITY
// -----------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GameApp()
            }
        }
    }
}

// -----------------------------
// APP ROOT
// -----------------------------
@Composable
fun GameApp() {
    var screen by remember { mutableStateOf(ScreenState.MENU) }
    var lastScore by remember { mutableStateOf(0) }

    when (screen) {
        ScreenState.MENU -> MenuScreen(onStart = { screen = ScreenState.GAME })
        ScreenState.GAME -> GameScreen(
            onGameOver = { score ->
                lastScore = score
                screen = ScreenState.GAME_OVER
            }
        )
        ScreenState.GAME_OVER -> GameOverScreen(
            score = lastScore,
            onRestart = { screen = ScreenState.GAME },
            onMenu = { screen = ScreenState.MENU }
        )
    }
}

// -----------------------------
// PALETTE PASTEL CUTE
// -----------------------------
private val pastelBackgroundTop = Color(0xFFE0F7FA) // Light Cyan
private val pastelBackgroundBottom = Color(0xFFFFFDE7) // Pale Yellow
private val pastelAccent1 = Color(0xFFFFC1E3) // Soft Pink (Used for highlights)
private val pastelAccent2 = Color(0xFFFFE0B2) // Soft Orange/Peach (Used for buttons)
private val pastelAccent3 = Color(0xFFB3E5FC) // Soft Blue (Used for game over BG)
private val pastelPlatform = Color(0xFFC8E6C9) // Minty Green (Softer)
private val pastelCrystal = Color(0xFFFFCC80) // Brighter Orange/Yellow (Crystal)
private val pastelObstacle = Color(0xFFFF8A80) // Light Red/Coral (Danger)
private val uiText = Color(0xFF3B3A3A) // Dark Gray for text
private val playerColor = Color(0xFF90CAF9) // Soft light blue for player

// -----------------------------
// MENU BONITO PASTEL - Mejoras: Botón más visual, título animado
// -----------------------------
@Composable
fun MenuScreen(onStart: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "menuPulse")

    // 1. Efecto de pulso más sutil para el botón
    val pulseScale by infinite.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(animation = tween(1200, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "buttonScale"
    )

    // 2. Título con animación de "flotación" vertical
    val titleOffsetY by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(animation = tween(1800, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "titleOffset"
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(pastelBackgroundTop, pastelBackgroundBottom),
                    startY = 0f,
                    endY = 1000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Título mejorado: usa el offset animado
            Text(
                "Bola de Éter",
                fontSize = 52.sp,
                fontWeight = FontWeight.Black,
                color = uiText,
                modifier = Modifier
                    .offset { IntOffset(0, titleOffsetY.roundToInt()) }
                    .shadow(8.dp, RoundedCornerShape(8.dp), ambientColor = pastelAccent1)
            )
            Spacer(Modifier.height(40.dp))

            // Botón de jugar mejorado: usa el pulso y una forma más definida
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = pastelAccent2),
                modifier = Modifier
                    .scale(pulseScale)
                    .size(width = 240.dp, height = 70.dp)
                    .shadow(12.dp, RoundedCornerShape(25.dp), ambientColor = pastelAccent2.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("¡JUGAR!", color = uiText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }


            Spacer(Modifier.height(20.dp))
            Text("Toca izquierda/derecha para impulsar", color = uiText.copy(alpha = 0.8f))
        }
    }
}

// ... (El código anterior, incluyendo la paleta de colores y los modelos, se mantiene igual)

// -----------------------------
// GAME OVER PASTEL (CORREGIDO Y UNIFORME)
// -----------------------------
@Composable
fun GameOverScreen(score: Int, onRestart: () -> Unit, onMenu: () -> Unit) {
    // Título parpadeante
    val infinite = rememberInfiniteTransition(label = "gameOverAlpha")
    val alpha by infinite.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(600), repeatMode = RepeatMode.Reverse),
        label = "alphaPulse"
    )

    // Dimensiones fijas
    val buttonWidth = 160.dp
    val buttonHeight = 60.dp
    val buttonShape = RoundedCornerShape(20.dp)

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(pastelAccent1.copy(alpha = 0.9f), pastelAccent3.copy(alpha = 0.9f)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .shadow(20.dp, buttonShape, ambientColor = uiText.copy(alpha = 0.3f))
                .clip(buttonShape)
                .background(Color.White)
                .padding(vertical = 40.dp, horizontal = 50.dp)
        ) {
            Text(
                "¡Juego Terminado!",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = uiText.copy(alpha = alpha),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(18.dp))

            Text(
                "Puntaje Final: $score",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = uiText,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(35.dp))

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                // Botón Reintentar
                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(containerColor = pastelAccent2),
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(width = buttonWidth, height = buttonHeight)
                        .shadow(8.dp, buttonShape),
                    shape = buttonShape,
                    // Eliminamos el padding automático interno de Button
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "Reintentar",
                        color = uiText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium, // CAMBIO: Texto más ligero para uniformidad
                        // Aseguramos que el texto ocupe todo el ancho disponible y esté centrado
                        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }

                // Botón Menú
                Button(
                    onClick = onMenu,
                    colors = ButtonDefaults.buttonColors(containerColor = pastelPlatform),
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(width = buttonWidth, height = buttonHeight)
                        .shadow(8.dp, buttonShape),
                    shape = buttonShape,
                    // Eliminamos el padding automático interno de Button
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "Menú",
                        color = uiText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium, // CAMBIO: Texto más ligero para uniformidad
                        // Aseguramos que el texto ocupe todo el ancho disponible y esté centrado
                        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}
// ... (El resto del código GameApp, MenuScreen y GameScreen se mantiene igual)
// ... (El resto del código GameApp, MenuScreen y GameScreen se mantiene igual)

// -----------------------------
// GAME PRINCIPAL - Mejoras: Elementos visuales del juego (Jugador, Plataforma, Cristal, Obstáculo, HUD)
// -----------------------------
@Composable
fun GameScreen(onGameOver: (Int) -> Unit) {
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val screenW = remember { mutableStateOf(0f) } // px
    val screenH = remember { mutableStateOf(0f) } // px

    val playerSizePx = 56f
    val gravity = 2.2f
    val jumpImpulse = 32f
    val moveSpeed = 9f
    val horizontalFriction = 0.87f

    var playerX by remember { mutableStateOf(0f) }
    var playerY by remember { mutableStateOf(0f) }
    var velY by remember { mutableStateOf(0f) }
    var velX by remember { mutableStateOf(0f) }

    var cameraYOffset by remember { mutableStateOf(0f) }
    val platforms = remember { mutableStateListOf<Platform>() }
    var nextPlatformId by remember { mutableStateOf(0) }

    // Usaremos un data class para cristales para gestionar el estado de "desaparición"
    data class CrystalState(val x: Float, val y: Float, val id: Int, var scale: Float = 1f, var alpha: Float = 1f)
    val crystals = remember { mutableStateListOf<CrystalState>() }
    val obstacles = remember { mutableStateListOf<Triple<Float, Float, Float>>() }

    var score by remember { mutableStateOf(0) }
    val scorePulse = remember { Animatable(1f) } // Animatable para el pulso del puntaje

    // Animaciones existentes
    val animatedX by animateFloatAsState(targetValue = playerX, animationSpec = spring(dampingRatio = 0.6f, stiffness = 120f), label = "playerXSpring")
    val animatedY by animateFloatAsState(targetValue = playerY - cameraYOffset, animationSpec = spring(dampingRatio = 0.6f, stiffness = 120f), label = "playerYSpring")
    val scope = rememberCoroutineScope()
    var isHit by remember { mutableStateOf(false) }
    val shake = remember { Animatable(0f) }

    // 2. Animación de Impacto (Color del Jugador)
    val playerHitColor = remember { Animatable(1f) } // 1f = Color normal, 0f = Color de hit (rojo/pastelObstacle)

    // 3. Animación de escala para el cristal (para dar un efecto de brillo/vida)
    val infiniteCrystal = rememberInfiniteTransition(label = "crystalScale")
    val crystalScaleInfinite by infiniteCrystal.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse),
        label = "crystalScalePulse"
    )

    // Variables para el feedback de toque
    var leftControlPressed by remember { mutableStateOf(false) }
    var rightControlPressed by remember { mutableStateOf(false) }

    // extra soft floating background blobs (positions)
    val blobs = remember {
        List(6) {
            Pair(Random.nextFloat(), Random.nextFloat())
        }
    }

    // Animación para el movimiento de los blobs
    val infiniteBlob = rememberInfiniteTransition(label = "blobMove")
    val blobMoveY by infiniteBlob.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(animation = tween(15000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "blobMoveY"
    )
    val blobMoveX by infiniteBlob.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(animation = tween(10000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "blobMoveX"
    )

    // Función para manejar el pulso del puntaje
    fun triggerScorePulse(newScore: Int) {
        score = newScore
        scope.launch {
            scorePulse.animateTo(1.2f, tween(100))
            scorePulse.animateTo(1.0f, spring(dampingRatio = 0.5f, stiffness = 150f))
        }
    }


    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(pastelBackgroundTop, pastelBackgroundBottom))
            )
            .onSizeChanged { size ->
                val w = size.width.toFloat()
                val h = size.height.toFloat()
                screenW.value = w
                screenH.value = h
                if (platforms.isEmpty()) {
                    playerX = w / 2f - playerSizePx / 2f
                    playerY = h - 180f
                    cameraYOffset = 0f
                    nextPlatformId = 0
                    platforms.clear()

                    val baseY = playerY + 120f
                    platforms.add(Platform(nextPlatformId++, w / 2f - 120f, baseY, 240f))
                    platforms.add(Platform(nextPlatformId++, w * 0.25f - 80f, baseY - 320f, 160f))
                    platforms.add(Platform(nextPlatformId++, w * 0.75f - 60f, baseY - 600f, 120f))

                    crystals.clear()
                    obstacles.clear()
                    score = 0
                }
            }
            // Eliminamos el pointerInput global y lo movemos a los Box de control
            .offset { IntOffset(shake.value.roundToInt(), 0) }
    ) {
        // Soft decorative blobs
        // ... (Código de Blobs se mantiene igual)
        blobs.forEachIndexed { idx, pair ->
            val cx = pair.first * (screenW.value.coerceAtLeast(600f))
            val cy = pair.second * (screenH.value.coerceAtLeast(800f))
            Box(
                Modifier
                    .offset { IntOffset((cx + blobMoveX).roundToInt(), (cy + blobMoveY - cameraYOffset / (10f + idx)).roundToInt()) }
                    .size((50 + idx * 12).dp)
                    .blur(radius = (10 + idx).dp)
                    .clip(RoundedCornerShape(50))
                    .background(pastelAccent1.copy(alpha = 0.2f))
            )
        }


        // ... GAME LOOP (solo se ajusta la lógica de Score y Hit) ...
        LaunchedEffect(key1 = true) {
            while (true) {
                delay(16L)
                val w = screenW.value
                val h = screenH.value
                if (w == 0f || h == 0f) continue

                velY += gravity
                if (velY > 96f) velY = 96f
                playerY += velY
                playerX += velX
                velX *= horizontalFriction

                if (playerX < -playerSizePx / 2f) playerX = w - playerSizePx / 2f
                if (playerX > w - playerSizePx / 2f) playerX = -playerSizePx / 2f

                val followThreshold = h * 0.40f
                if (playerY < cameraYOffset + followThreshold) {
                    val diff = (cameraYOffset + followThreshold) - playerY
                    cameraYOffset -= diff.coerceAtMost(48f)
                    val pointsGained = (diff.roundToInt() / 10).coerceAtLeast(0)
                    if (pointsGained > 0) {
                        triggerScorePulse(score + pointsGained) // Pulso por subir
                    }
                }

                if (-cameraYOffset > 180f * nextPlatformId) {
                    val newY = cameraYOffset - 200f - Random.nextFloat() * 120f
                    val newW = 90f + Random.nextFloat() * 140f
                    val newX = Random.nextFloat() * (w - newW)
                    platforms.add(Platform(nextPlatformId++, newX, newY, newW))
                    if (Random.nextFloat() < 0.6f) crystals.add(CrystalState(newX + newW / 2f, newY - 28f, Random.nextInt())) // Usa CrystalState
                    if (Random.nextFloat() < 0.25f && nextPlatformId > 4) obstacles.add(Triple(newX + Random.nextFloat() * newW, newY - 30f, 22f))
                }

                platforms.removeAll { it.y - cameraYOffset > h + 400f }
                crystals.removeAll { it.y - cameraYOffset > h + 400f || it.alpha < 0.1f } // Remueve cristales si desaparecen
                obstacles.removeAll { (_, oy, _) -> oy - cameraYOffset > h + 400f }

                var landedThisFrame = false
                for (p in platforms) {
                    val pVisibleY = p.y - cameraYOffset
                    val horizontalOverlap = (playerX + playerSizePx * 0.5f) > p.x && (playerX + playerSizePx * 0.5f) < (p.x + p.widthPx)
                    val playerVisibleY = playerY - cameraYOffset
                    val touchingTop = (playerVisibleY + playerSizePx) >= pVisibleY &&
                            (playerVisibleY + playerSizePx) - velY < pVisibleY + p.heightPx
                    if (velY > 0f && horizontalOverlap && touchingTop) {
                        playerY = p.y - playerSizePx
                        velY = 0f
                        landedThisFrame = true
                        triggerScorePulse(score + 1) // Pulso por aterrizar
                        break
                    }
                }

                val collected = crystals.filter { crystal ->
                    val cyVisible = crystal.y - cameraYOffset
                    abs((playerX + playerSizePx / 2f) - crystal.x) < (playerSizePx / 2f + 16f) &&
                            abs((playerY + playerSizePx / 2f) - cyVisible) < (playerSizePx / 2f + 16f)
                }
                if (collected.isNotEmpty()) {
                    collected.forEach { crystal ->
                        triggerScorePulse(score + 10) // Pulso por cristal
                        scope.launch {
                            // Animación de desaparición del cristal
                            crystal.scale = 2.0f
                            crystal.alpha = 0.0f
                            crystals.remove(crystal)
                        }
                    }
                }

                val hit = obstacles.filter { (ox, oy, size) ->
                    val oyVisible = oy - cameraYOffset
                    abs((playerX + playerSizePx / 2f) - ox) < (playerSizePx / 2f + size / 2f) &&
                            abs((playerY + playerSizePx / 2f) - oyVisible) < (playerSizePx / 2f + size / 2f)
                }
                if (hit.isNotEmpty()) {
                    if (!isHit) {
                        isHit = true
                        scope.launch {
                            // Feedback de impacto del jugador (Color)
                            playerHitColor.animateTo(0f, tween(50)) // Pasa a color rojo
                            shake.animateTo(14f, spring(dampingRatio = 0.6f, stiffness = 120f))
                            shake.animateTo(0f, tween(300))
                            playerHitColor.animateTo(1f, tween(300)) // Vuelve al color normal
                            isHit = false
                        }
                        velY = 18f
                        triggerScorePulse((score - 5).coerceAtLeast(0)) // Pulso por daño
                    }
                    hit.forEach { h -> obstacles.remove(h) }
                }

                if (playerY - cameraYOffset > screenH.value + 240f) {
                    onGameOver(score)
                    break
                }
            }
        }

        // RENDER: platforms
        for (p in platforms) {
            Box(
                Modifier
                    .offset { IntOffset(p.x.roundToInt(), (p.y - cameraYOffset).roundToInt()) }
                    .width((with(LocalDensity.current) { p.widthPx.toDp() }))
                    .height((with(LocalDensity.current) { p.heightPx.toDp() }))
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(listOf(pastelPlatform.copy(alpha = 0.9f), pastelPlatform))
                    )
                    .shadow(4.dp, RoundedCornerShape(10.dp), ambientColor = pastelPlatform.copy(alpha = 0.8f))
            )
        }

        // RENDER: crystals (usando CrystalState y animación de desaparición)
        for (crystal in crystals.toList()) { // toList para evitar ConcurrentModificationException
            // Necesitamos usar animateFloatAsState para el efecto de desaparición
            val animatedCrystalScale by animateFloatAsState(targetValue = crystal.scale, label = "crystalScaleAnim")
            val animatedCrystalAlpha by animateFloatAsState(targetValue = crystal.alpha, label = "crystalAlphaAnim")

            Box(
                Modifier
                    .offset { IntOffset(crystal.x.roundToInt() - 10, (crystal.y - cameraYOffset).roundToInt() - 10) }
                    .size(20.dp)
                    .scale(crystalScaleInfinite * animatedCrystalScale) // Combina pulso infinito con desaparición
                    .alpha(animatedCrystalAlpha) // Aplica la transparencia de desaparición
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White, pastelCrystal),
                            center = Offset(10f, 10f)
                        )
                    )
                    .shadow(6.dp, CircleShape, ambientColor = pastelCrystal)
            )
        }

        // RENDER: obstacles
        for ((ox, oy, size) in obstacles) {
            Box(
                Modifier
                    .offset { IntOffset(ox.roundToInt() - size.roundToInt() / 2, (oy - cameraYOffset).roundToInt() - size.roundToInt() / 2) }
                    .size((with(LocalDensity.current) { size.toDp() }))
                    .clip(CircleShape)
                    .background(pastelObstacle)
                    .shadow(4.dp, CircleShape, ambientColor = pastelObstacle.copy(alpha = 0.8f))
            )
        }

        // Player with soft glow (Color Animado)
        val interpolatedPlayerColor = lerp(pastelObstacle, playerColor, playerHitColor.value)
        Box(
            Modifier
                .offset {
                    IntOffset(animatedX.roundToInt(), animatedY.roundToInt())
                }
                .size(with(LocalDensity.current) { playerSizePx.toDp() })
                .clip(CircleShape)
                .background(interpolatedPlayerColor) // Usa el color interpolado
                .shadow(12.dp, CircleShape, ambientColor = interpolatedPlayerColor.copy(alpha = 0.8f))
        )

        // HUD: translucent rounded panel (Puntaje con Pulso)
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.4f))
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    "Puntos: $score",
                    color = uiText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.scale(scorePulse.value) // Aplica el pulso del puntaje
                )
            }
        }

        // Controles invisibles: pantalla dividida en dos mitades (con feedback visual sutil)
        Row(Modifier.fillMaxSize()) {
            // Control Izquierdo
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    // 1. Feedback Visual de Control
                    .alpha(if (leftControlPressed) 0.1f else 0f) // Sutil color al presionar
                    .background(pastelAccent3)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                leftControlPressed = true
                                velY = -jumpImpulse
                                velX = -moveSpeed
                                tryAwaitRelease()
                                leftControlPressed = false
                            }
                        )
                    }
            )
            // Control Derecho
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    // 1. Feedback Visual de Control
                    .alpha(if (rightControlPressed) 0.1f else 0f) // Sutil color al presionar
                    .background(pastelAccent3)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                rightControlPressed = true
                                velY = -jumpImpulse
                                velX = moveSpeed
                                tryAwaitRelease()
                                rightControlPressed = false
                            }
                        )
                    }
            )
        }
    }
}

// Función auxiliar para interpolar color (similar a lerp en Compose, pero más simple)
private fun lerp(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}