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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

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
// 2. PANTALLA PRINCIPAL: HOJA DE PERSONAJE (DiceRollerScreen)
// =============================================================================

/**
 * @OptIn(ExperimentalMaterial3Api::class): Necesario para usar ciertos componentes
 * de Material 3 que aún están en fase experimental (como TopAppBar o Cards avanzados).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerScreen() {
    // =========================================================================
    // 1. ESTADO (State Hoisting)
    // =========================================================================

    // VALORES: Iniciamos en 0 para saber que no se han tirado
    var vitality by rememberSaveable { mutableIntStateOf(0) }
    var dexterity by rememberSaveable { mutableIntStateOf(0) }
    var wisdom by rememberSaveable { mutableIntStateOf(0) }

    // BLOQUEOS: Booleanos para saber si ya se usó ese botón (NUEVO)
    var vitLocked by rememberSaveable { mutableStateOf(false) }
    var dexLocked by rememberSaveable { mutableStateOf(false) }
    var wisLocked by rememberSaveable { mutableStateOf(false) }

    // ANIMACIÓN: Control visual
    var isRollingVit by remember { mutableStateOf(false) }
    var isRollingDex by remember { mutableStateOf(false) }
    var isRollingWis by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // =========================================================================
    // 2. LÓGICA DE NEGOCIO
    // =========================================================================

    val total = vitality + dexterity + wisdom

    // Lógica para saber si el juego terminó (todos bloqueados)
    val isGameFinished = vitLocked && dexLocked && wisLocked

    // Mensajes: Solo mostramos el juicio final si el juego terminó
    val (message, messageColor) = when {
        !isGameFinished -> "Tira todos los dados..." to Color.Gray
        total >= 50 -> "¡GODLIKE! 🌟" to Color(0xFFFFD700)
        total < 30 -> "Demasiado débil... 💀" to Color(0xFFDC143C)
        else -> "Personaje Equilibrado ✅" to Color(0xFF4CAF50) // Verde
    }

    /**
     * Función Helper Mejorada: Ahora recibe 'onLock' para bloquear al final
     */
    fun rollStat(
        updateValue: (Int) -> Unit,
        setRolling: (Boolean) -> Unit,
        lockStat: () -> Unit // Nueva función que pasaremos para bloquear
    ) {
        scope.launch {
            setRolling(true) // 1. Empieza animación

            repeat(15) {
                updateValue((1..20).random())
                delay(ANIMATION_DELAY_MS)
            }

            updateValue((1..20).random()) // 2. Valor final
            setRolling(false) // 3. Termina animación
            lockStat() // 4. BLOQUEO PERMANENTE (NUEVO)
        }
    }

    // Función para reiniciar todo (RESET)
    fun resetAll() {
        vitality = 0; dexterity = 0; wisdom = 0
        vitLocked = false; dexLocked = false; wisLocked = false
    }

    // =========================================================================
    // 3. UI (INTERFAZ VISUAL)
    // =========================================================================
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RPG Character Creator") },
                // Agregamos el botón de Reset en la barra superior
                actions = {
                    Button(onClick = { resetAll() }) {
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

            // Fila Vitality
            StatRow(
                label = "Vitality",
                value = vitality,
                // El botón se deshabilita si está rodando O si ya está bloqueado
                isEnabled = !isRollingVit && !vitLocked,
                onRoll = {
                    rollStat(
                        updateValue = { vitality = it },
                        setRolling = { isRollingVit = it },
                        lockStat = { vitLocked = true } // Al terminar, bloqueamos
                    )
                }
            )

            // Fila Dexterity
            StatRow(
                label = "Dexterity",
                value = dexterity,
                isEnabled = !isRollingDex && !dexLocked,
                onRoll = {
                    rollStat(
                        updateValue = { dexterity = it },
                        setRolling = { isRollingDex = it },
                        lockStat = { dexLocked = true }
                    )
                }
            )

            // Fila Wisdom
            StatRow(
                label = "Wisdom",
                value = wisdom,
                isEnabled = !isRollingWis && !wisLocked,
                onRoll = {
                    rollStat(
                        updateValue = { wisdom = it },
                        setRolling = { isRollingWis = it },
                        lockStat = { wisLocked = true }
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TARJETA DE RESULTADO TOTAL ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("TOTAL SCORE", style = MaterialTheme.typography.labelMedium)

                    Text(
                        text = total.toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = messageColor
                    )

                    // Solo mostramos mensaje final
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = messageColor
                    )
                }
            }
        }
    }
}

// =============================================================================
// 3. COMPONENTE REUTILIZABLE: StatRow
// =============================================================================
/**
 * StatRow: Componente personalizado para dibujar una fila de estadística.
 * Muestra: Nombre (Label) + Valor + Botón
 */
@Composable
fun StatRow(
    label: String,
    value: Int,
    isEnabled: Boolean,
    onRoll: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Si es 0, mostramos "--", si no, el número
                val displayValue = if (value == 0) "--" else value.toString()

                // LÓGICA DE COLOR ESTRICTA (RPG)
                val valueColor = when (value) {
                    20 -> Color(0xFFFFD700) // ¡Crítico Natural! (Dorado)
                    1 -> Color(0xFFDC143C)  // ¡Pifia! (Rojo)
                    else -> MaterialTheme.colorScheme.onSurface // Normal
                }

                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(end = 16.dp),
                    color = valueColor // Aplicamos el color calculado
                )

                Button(onClick = onRoll, enabled = isEnabled) {
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