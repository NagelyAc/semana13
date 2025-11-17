package com.example.semana13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.CircularProgressIndicator
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
                    AnimatedContentExample(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

sealed class ScreenState {
    object Loading : ScreenState()
    object Content : ScreenState()
    object Error : ScreenState()
}

@Composable
fun AnimatedContentExample(modifier: Modifier = Modifier) {
    var currentState by remember { mutableStateOf<ScreenState>(ScreenState.Loading) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // AnimatedContent para transiciones entre estados
        AnimatedContent(
            targetState = currentState,
            transitionSpec = {
                fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "State Transition"
        ) { state ->
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        when (state) {
                            ScreenState.Loading -> Color(0xFFFFC107) // Amarillo
                            ScreenState.Content -> Color(0xFF4CAF50) // Verde
                            ScreenState.Error -> Color(0xFFF44336) // Rojo
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    ScreenState.Loading -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando...",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    ScreenState.Content -> {
                        Text(
                            text = "¡Contenido Cargado!",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    ScreenState.Error -> {
                        Text(
                            text = "Error de Carga",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Botones para cambiar estado
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { currentState = ScreenState.Loading },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(width = 160.dp, height = 50.dp)
            ) {
                Text("Cargando", color = Color.Black, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { currentState = ScreenState.Content },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(width = 160.dp, height = 50.dp)
            ) {
                Text("Contenido", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { currentState = ScreenState.Error },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(width = 160.dp, height = 50.dp)
            ) {
                Text("Error", color = Color.White, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Estado actual: ${when (currentState) {
                ScreenState.Loading -> "Cargando"
                ScreenState.Content -> "Contenido"
                ScreenState.Error -> "Error"
            }}",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedContentExamplePreview() {
    Semana13Theme {
        AnimatedContentExample()
    }
}
