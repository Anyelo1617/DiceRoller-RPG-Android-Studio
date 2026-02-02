package com.example.traffic_light

// --- Imports ---
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TrafficLightScreen()
                }
            }
        }
    }
}

// 1. ENUM DEFINED
enum class TrafficLightState {
    Red, Yellow, Green
}

@Composable
fun TrafficLightScreen() {
    // Estado que guarda qué luz está encendida
    var currentState by remember { mutableStateOf(TrafficLightState.Red) }

    // 2. LAUNCHED EFFECT
    LaunchedEffect(Unit) {
        while (true) {
            // Ciclo: Rojo -> Verde -> Amarillo
            currentState = TrafficLightState.Red
            delay(2000)

            currentState = TrafficLightState.Green
            delay(2000)

            currentState = TrafficLightState.Yellow
            delay(1000)
        }
    }

    // 3. UI: CENTRADO EN PANTALLA
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Contenedor negro del semáforo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- TÍTULO ---
            // Columna principal que contiene Textos + Semáforo
            Text(
                text = "TRAFFIC LIGHT SIMULATOR",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // --- FRASE / SUBTÍTULO ---
            Text(
                text = "Keep your eyes open! \uD83D\uDC40", // Frase + Emoji de ojos
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp) // Sangría generosa antes del semáforo
            )

            // --- CAJA DEL SEMÁFORO ---
            Column(
                modifier = Modifier
                    .width(150.dp)
                    .background(Color.DarkGray, shape = CircleShape.copy(all = CornerSize(16.dp)))
                    .padding(vertical = 24.dp), // Padding interno del semáforo
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp) // Espacio entre luces
            ) {
                // LUZ ROJA
                TrafficLightCircle(
                    color = Color.Red,
                    isOn = currentState == TrafficLightState.Red
                )

                // LUZ AMARILLA (Orden visual corregido: Rojo-Amarillo-Verde)
                TrafficLightCircle(
                    color = Color.Yellow,
                    isOn = currentState == TrafficLightState.Yellow
                )

                // LUZ VERDE
                TrafficLightCircle(
                    color = Color.Green,
                    isOn = currentState == TrafficLightState.Green
                )
            }
        }
    }
}

// Componente reutilizable para cada círculo
@Composable
fun TrafficLightCircle(color: Color, isOn: Boolean) {
    Box(
        modifier = Modifier
            .size(100.dp) // Tamaño del círculo
            .clip(CircleShape)
            .background(if (isOn) color else Color.Gray.copy(alpha = 0.3f)) // 4. COLOR STATES
            .border(4.dp, if (isOn) color.copy(alpha = 0.5f) else Color.Transparent, CircleShape) // Brillo extra si está encendido
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TrafficLightScreen()
}