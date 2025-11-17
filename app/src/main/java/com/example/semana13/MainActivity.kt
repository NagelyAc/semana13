package com.example.semana13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
                    AnimateColorAsStateExample(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimateColorAsStateExample(modifier: Modifier = Modifier) {
    var isBlue by remember { mutableStateOf(true) }

    // Animación con tween para el color principal
    val animatedColor by animateColorAsState(
        targetValue = if (isBlue) Color(0xFF2196F3) else Color(0xFF4CAF50), // Azul a Verde
        animationSpec = tween(durationMillis = 1000), // Animación suave de 1 segundo
        label = "Color Animation"
    )

    // Animación con spring para el botón
    val buttonColor by animateColorAsState(
        targetValue = if (isBlue) Color(0xFF1976D2) else Color(0xFF388E3C),
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 100f), // Spring bouncy
        label = "Button Color Animation"
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cuadro grande con animación de color
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(animatedColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isBlue) "Azul" else "Verde",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón estilizado con animación
        Button(
            onClick = { isBlue = !isBlue },
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(width = 180.dp, height = 60.dp)
        ) {
            Text(
                text = "Cambiar Color",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Animación: ${if (isBlue) "Tween (Suave)" else "Spring (Elástico)"}",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimateColorAsStateExamplePreview() {
    Semana13Theme {
        AnimateColorAsStateExample()
    }
}
