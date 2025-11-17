package com.example.semana13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.semana13.ui.theme.Semana13Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Semana13Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimateDpAsStateExample(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimateDpAsStateExample(modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }

    // Animación de tamaño con tween
    val animatedSize by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else 100.dp,
        animationSpec = tween(durationMillis = 800),
        label = "Size Animation"
    )

    // Animación de offset con spring
    val animatedOffset by animateDpAsState(
        targetValue = if (isExpanded) 100.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 120f),
        label = "Offset Animation"
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cuadro animado: offset primero, luego size (orden importa)
        Box(
            modifier = Modifier
                .offset(x = animatedOffset, y = animatedOffset) // Primero offset
                .size(animatedSize) // Luego size
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF6200EE)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isExpanded) "Grande" else "Pequeño",
                color = Color.White,
                fontSize = if (isExpanded) 20.sp else 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Botón estilizado
        Button(
            onClick = { isExpanded = !isExpanded },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3700B3)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(width = 200.dp, height = 60.dp)
        ) {
            Text(
                text = "Mover y Cambiar Tamaño",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Orden: offset → size (afecta el resultado)",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimateDpAsStateExamplePreview() {
    Semana13Theme {
        AnimateDpAsStateExample()
    }
}
