package com.curso.android.module1.dice

// =============================================================================
// IMPORTACIONES
// =============================================================================

// Organizamos las importaciones por categoría para mejor legibilidad.
// En Kotlin/Android, usamos import para traer clases y funciones externas.

// --- Android Core ---
// Bundle: Contenedor de datos que Android usa para pasar información entre componentes
// Log: Clase para imprimir mensajes de depuración en Logcat
import android.os.Bundle
import android.util.Log

// --- AndroidX Activity ---
// ComponentActivity: Activity base moderna que soporta Compose
// enableEdgeToEdge: Función para habilitar UI de borde a borde (sin barras negras)
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

// --- Jetpack Compose Core ---
// Herramientas fundamentales de diseño (Layouts)
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons

// --- Material 3 Components ---
// Componentes visuales modernos (Botones, Tarjetas, Textos)
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

// --- Compose Runtime (Estado y Efectos) ---
// APIs para manejar la lógica reactiva (Remember, State)
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

// --- Compose UI ---
// Utilidades de alineación, modificadores y previsualización
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Kotlin Coroutines ---
// Para la animación asíncrona (delay, launch)
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Imports para estilos ---
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.ButtonDefaults

// --- Imports para centrar el total score en su contenedor  ---
import androidx.compose.ui.text.style.TextAlign

// --- Imports para iconos  ---
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.PlayArrow

// --- Imports para reproducción de sonido  ---
import androidx.compose.ui.platform.LocalContext
import android.media.MediaPlayer

// =============================================================================
// 📚 CHEAT SHEET: CONCEPTOS TEÓRICOS DEL MÓDULO 1
// =============================================================================
/*
   1. KOTLIN BASICS
      - val: Variable inmutable (fija). | var: Variable mutable (cambiante).
      - Null Safety: String (nunca nulo) vs String? (puede ser nulo).
      - Lambdas: Funciones pasadas como variables -> { acciones() }.

   2. ANDROID ACTIVITY & LIFECYCLE
      - ComponentActivity: Base moderna para apps con Compose.
      - onCreate(): Donde "nace" la pantalla. Aquí configuramos la UI.
      - Log.d(TAG, msg): Para escribir en la consola (Logcat) y depurar.

   3. JETPACK COMPOSE (UI DECLARATIVA)
      - @Composable: Función que dibuja UI. Se "recompone" (actualiza) sola.
      - remember: Guarda un valor en memoria RAM mientras la vista está activa.
      - rememberSaveable: Igual que remember, pero sobrevive a rotaciones de pantalla.
      - mutableStateOf: Contenedor de dato que avisa a la UI cuando cambia.
      - Modifiers: Encadenables (.padding.fillMax) que alteran diseño y comportamiento.

   4. CORRUTINAS (ASINCRONÍA)
      - Hilo Principal (Main Thread): Dibuja la UI. ¡Nunca bloquearlo!
      - suspend fun: Función que puede pausarse sin congelar la app.
      - delay(ms): Pausa eficiente (no bloqueante).
      - rememberCoroutineScope: Necesario para lanzar corrutinas desde botones/UI.
      - launch { }: Inicia la tarea asíncrona ("fuego y olvido").
*/

// =============================================================================
// 📖 GLOSARIO TÉCNICO DETALLADO (Referencia del Curso)
// =============================================================================

/*
   -----------------------------------------------------------------------------
   1. CICLO DE VIDA DE UNA ACTIVITY (Android Core)
   -----------------------------------------------------------------------------
   Una Activity pasa por varios estados durante su vida. Es crucial entenderlo
   para saber cuándo guardar datos o detener animaciones.

   onCreate() → onStart() → onResume() → [RUNNING] → onPause() → onStop() → onDestroy()
       ↑                                                              ↓
       └──────────────────────────────────────────────────────────────┘

   - onCreate(): Se llama UNA vez. Inicializamos la UI y configuraciones.
   - onStart(): La Activity se vuelve visible.
   - onResume(): La Activity está en primer plano e interactiva.
   - onPause(): Pierde el foco (ej. sale un diálogo o el usuario desliza para salir).
   - onStop(): Ya no es visible.
   - onDestroy(): Se destruye (por rotación de pantalla o cierre total).

   -----------------------------------------------------------------------------
   2. GESTIÓN DE ESTADO: REMEMBER vs REMEMBER SAVEABLE
   -----------------------------------------------------------------------------
   En Jetpack Compose, el "estado" determina qué muestra la UI.
   Cuando una variable de estado cambia, ocurre la "Recomposición" (redibujado).

   ¿Cuál usar?
   A) remember { ... }
      - El valor se guarda en memoria RAM mientras el Composable esté en pantalla.
      - PROBLEMA: Si rotas la pantalla (destruye la Activity), el valor se PIERDE.
      - USO: Animaciones, estados transitorios (hover, scroll) que no importa reiniciar.

   B) rememberSaveable { ... }
      - El valor se GUARDA en un "Bundle" del sistema.
      - VENTAJA: Sobrevive a rotaciones de pantalla, modo oscuro, cambios de idioma.
      - USO: Datos del usuario (inputs, contadores, resultados finales).
      - LIMITACIÓN: Solo guarda tipos simples (Int, String, Boolean).

   -----------------------------------------------------------------------------
   3. CORRUTINAS (KOTLIN COROUTINES)
   -----------------------------------------------------------------------------
   Las corrutinas nos permiten hacer cosas "en segundo plano" sin congelar la pantalla.

   - CoroutineScope: Es el "ámbito" de vida. Si el scope muere, la corrutina se cancela.
     En Compose usamos `rememberCoroutineScope()` para que las tareas mueran si
     el usuario cierra la pantalla (evitando fugas de memoria).

   - launch { }: Lanza una corrutina de "fuego y olvido". El código sigue ejecutándose
     abajo sin esperar a que launch termine.

   - delay(ms): Es una función de SUSPENSIÓN. A diferencia de Thread.sleep() (que congela),
     delay() "libera" el hilo para que la UI siga respondiendo mientras espera.

   -----------------------------------------------------------------------------
   4. LAYOUTS Y MODIFIERS (COMPOSE UI)
   -----------------------------------------------------------------------------
   - Column: Organiza verticalmente (como LinearLayout vertical).
   - Row: Organiza horizontalmente.
   - Box: Apila elementos uno encima de otro (Z-Index).
   - Scaffold: Esqueleto estándar de Material Design (TopBar, FAB, BottomBar).
   - Modifier: Instrucciones encadenadas. ¡El orden importa!
     .padding(10.dp).background(Red) NO es lo mismo que .background(Red).padding(10.dp).
*/

// =============================================================================
// CONSTANTES GLOBALES
// =============================================================================
/**
 * TAG para mensajes de Log en Logcat.
 */
private const val TAG = "MainActivity"

/**
 * Velocidad de la animación (milisegundos entre cada cambio de número).
 */
private const val ANIMATION_DELAY_MS = 80L

// =============================================================================
// 1. MAIN ACTIVITY (PUNTO DE ENTRADA)
// =============================================================================
/**
 * MainActivity: El contenedor principal de la aplicación.
 * Hereda de ComponentActivity para soportar funcionalidades modernas.
 */
class MainActivity : ComponentActivity() {

    // Se llama cuando la Activity se crea por primera vez.
    override fun onCreate(savedInstanceState: Bundle?) {
        // PASO 1: Inicialización obligatoria de Android
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Inicializando App...")

        // PASO 2: Habilitar Edge-to-Edge (Pantalla completa moderna)
        enableEdgeToEdge()

        // PASO 3: Definir el contenido de la UI con Compose
        setContent {
            MaterialTheme {
                // Surface proporciona el fondo base según el tema (Claro/Oscuro)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos a nuestro Composable principal
                    DiceRollerScreen()
                }
            }
        }
    }
}

// =============================================================================
// PANTALLA PRINCIPAL: DiceRollerScreen (RPG EDITION ⚔️)
// =============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerScreen() {
    // 1. ESTADO
    var vitality by rememberSaveable { mutableIntStateOf(0) }
    var dexterity by rememberSaveable { mutableIntStateOf(0) }
    var wisdom by rememberSaveable { mutableIntStateOf(0) }

    var vitLocked by rememberSaveable { mutableStateOf(false) }
    var dexLocked by rememberSaveable { mutableStateOf(false) }
    var wisLocked by rememberSaveable { mutableStateOf(false) }

    var isRollingVit by remember { mutableStateOf(false) }
    var isRollingDex by remember { mutableStateOf(false) }
    var isRollingWis by remember { mutableStateOf(false) }


    val context = LocalContext.current

    // 2. FUNCIÓN HELPER PARA SONIDO
    fun playDiceSound() {
        // Asegúrate de que tu archivo se llame 'dice_roll' en la carpeta raw
        // Si se llama distinto, cambia R.raw.dice_roll por R.raw.tu_nombre
        val mediaPlayer = MediaPlayer.create(context, R.raw.diceroll)
        mediaPlayer.start()
        // Liberar memoria cuando termine el sonido (Buena práctica)
        mediaPlayer.setOnCompletionListener { mp -> mp.release() }
    }

    // --- ESTADO PARA LAS FRASES RPG ---
    // Definimos la variable UNA SOLA VEZ aquí:
    val rpgQuotes = listOf(
        "Fortune favors the brave.",
        "The dice determine your destiny.",
        "A true hero makes their own luck.",
        "Legends are born from critical rolls.",
        "Your adventure begins now!",
        "May your hits be critical and your failures few."
    )
    var selectedQuote by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // 2. LÓGICA
    val total = vitality + dexterity + wisdom
    val isGameFinished = vitLocked && dexLocked && wisLocked

    // Lógica para seleccionar frase (solo si terminó y aún no hay frase)
    if (isGameFinished && selectedQuote.isEmpty()) {
        selectedQuote = rpgQuotes.random()
    }

    val (message, messageColor) = when {
        !isGameFinished -> "" to Color.Transparent
        total >= 50 -> "Godlike!" to Color(0xFFB8860B) // Dorado
        total < 30 -> "Re-roll recommended!" to Color(0xFFDC143C) // Rojo
        else -> "Good stats" to Color.DarkGray
    }

    fun rollStat(updateValue: (Int) -> Unit, setRolling: (Boolean) -> Unit, lockStat: () -> Unit) {
        scope.launch {
            setRolling(true)
            repeat(15) {
                updateValue((1..20).random())
                delay(ANIMATION_DELAY_MS)
            }
            updateValue((1..20).random())
            setRolling(false)
            lockStat()
        }
    }

    fun resetAll() {
        vitality = 0; dexterity = 0; wisdom = 0
        vitLocked = false; dexLocked = false; wisLocked = false
        selectedQuote = ""
    }

    // 3. UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "⚔️ Character Creator",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    Button(
                        onClick = { resetAll() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh, // Ícono del Core
                            contentDescription = null,
                            modifier = Modifier.size(18.dp) // Un poco más pequeño para que se vea fino
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // Espacio entre ícono y texto
                        Text("RESET")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- VITALITY ---
            StatRow(
                label = "Vitality",
                emoji = "❤️",
                themeColor = Color(0xFFD32F2F),
                value = vitality,
                isEnabled = !isRollingVit && !vitLocked,
                onRoll = {
                    playDiceSound()
                    rollStat({ vitality = it }, { isRollingVit = it }, { vitLocked = true }) }
            )

            // --- DEXTERITY ---
            StatRow(
                label = "Dexterity",
                emoji = "🏹",
                themeColor = Color(0xFF388E3C),
                value = dexterity,
                isEnabled = !isRollingDex && !dexLocked,
                onRoll = {
                    playDiceSound()
                    rollStat({ dexterity = it }, { isRollingDex = it }, { dexLocked = true }) }
            )

            // --- WISDOM ---
            StatRow(
                label = "Wisdom",
                emoji = "🔮",
                themeColor = Color(0xFF512DA8),
                value = wisdom,
                isEnabled = !isRollingWis && !wisLocked,
                onRoll = {
                    playDiceSound()
                    rollStat({ wisdom = it }, { isRollingWis = it }, { wisLocked = true }) }
            )

            // --- TOTAL SCORE ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "TOTAL SCORE",
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = total.toString(),
                        fontSize = 56.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = messageColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (message.isNotEmpty()) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = messageColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }

            // --- RPG QUOTE (Solo visible al final) ---
            if (isGameFinished) {
                Spacer(modifier = Modifier.height(24.dp)) // Espacio superior

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Un poco más estrecho
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "YOUR QUOTE:",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "\"$selectedQuote\"",
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily.Serif,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// COMPONENTE PERSONALIZADO: StatRow
// =============================================================================
@Composable
fun StatRow(
    label: String,
    emoji: String,     // Emoji
    themeColor: Color, // Color del botón
    value: Int,
    isEnabled: Boolean,
    onRoll: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LADO IZQUIERDO: Emoji + Texto
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = emoji,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            }

            // LADO DERECHO: Valor + Botón Coloreado
            Row(verticalAlignment = Alignment.CenterVertically) {
                val displayValue = if (value == 0) "--" else value.toString()

                val valueColor = when (value) {
                    20 -> Color(0xFFB8860B) // Oro
                    1 -> Color(0xFFDC143C)  // Rojo
                    else -> MaterialTheme.colorScheme.onSurface
                }

                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 16.dp),
                    color = valueColor
                )

                Button(
                    onClick = onRoll,
                    enabled = isEnabled,
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                    shape = CircleShape, // Opcional: hacerlo redondito si te gusta
                    contentPadding = PaddingValues(horizontal = 12.dp) // Ajustar relleno
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Roll")
                }
            }
        }
    }
}

// =============================================================================
// 4. PREVIEW
// =============================================================================
/**
 * @Preview: Permite ver el diseño en Android Studio sin ejecutar la app en el celular.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DiceRollerScreenPreview() {
    MaterialTheme {
        DiceRollerScreen()
    }
}