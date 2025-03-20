package miu.miu_03

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import miu.miu_03.ui.theme.AppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val isDarkTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isDarkTheme // jasne ikony na ciemnym tle i odwrotnie
        }

        setContent {
            AppTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NumeralSystemConverter()
                    AddAd()
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NumeralSystemConverter() {
    var value by remember { mutableStateOf("") }
    var showInfoDialog by remember { mutableStateOf(false) }
    var selectedSystem by remember { mutableStateOf<NumeralSystem?>(null) }
    var result by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("Informacje o aplikacji") },
            text = {
                Column {
                    Text("Aplikacja umożliwia przeliczenie liczby całkowitej w systemie dziesiątkowym na inne, wybrane systemy liczbowe.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Jak korzystać z konwertera:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Wprowadź liczbę całkowitą używając klawiatury numerycznej.\nZakres: -2 147 483 648 do 2 147 483 647.")
                    Text("2. Zaznacz system liczbowy, na który chcesz przeliczyć liczbę. Jednocześnie możesz zaznaczyć tylko jeden system liczbowy.")
                    Text("3. Kliknij przycisk 'Przelicz'")
                    Text("4. Wynik przeliczenia zostanie wyświetlony poniżej przycisku 'Przelicz'.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Liczby ujemne są przeliczane na liczby dodatnie w systemie docelowym z uwzględnieniem znaku minus.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Zamknij")
                }
            }
        )
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("Konwerter") },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Informacje o aplikacji"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Przelicznik systemów liczbowych",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            // Pole wprowadzania liczby całkowitej
            OutlinedTextField(
                value = value?.toString() ?: "",
                onValueChange = {
                    value = it
                },
                label = { Text("Wprowadź liczbę całkowitą") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { }
                ),
                modifier = Modifier.fillMaxWidth()
            )

// Wybór systemu liczbowego (radiobutton)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "System liczbowy:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                // First row - 3 systems
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val firstRowSystems = listOf(
                        NumeralSystem.BINARY,
                        NumeralSystem.BASE8,
                        NumeralSystem.DUODECIMAL
                    )

                    firstRowSystems.forEach { system ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .selectable(
                                    selected = selectedSystem == system,
                                    onClick = { selectedSystem = system }
                                )
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RadioButton(
                                selected = selectedSystem == system,
                                onClick = null // handled by selectable
                            )
                            Text(
                                text = when(system) {
                                    NumeralSystem.BINARY -> "BIN"
                                    NumeralSystem.BASE8 -> "OCT"
                                    NumeralSystem.DUODECIMAL -> "B12"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Second row - 3 systems
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val secondRowSystems = listOf(
                        NumeralSystem.HEXADECIMAL,
                        NumeralSystem.VIGESIMAL,
                        NumeralSystem.BASE36
                    )

                    secondRowSystems.forEach { system ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .selectable(
                                    selected = selectedSystem == system,
                                    onClick = { selectedSystem = system }
                                )
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RadioButton(
                                selected = selectedSystem == system,
                                onClick = null // handled by selectable
                            )
                            Text(
                                text = when(system) {
                                    NumeralSystem.HEXADECIMAL -> "HEX"
                                    NumeralSystem.VIGESIMAL -> "VIG"
                                    NumeralSystem.BASE36 -> "B36"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // Przycisk przeliczenia
            Button(
                onClick = {
                    if (selectedSystem != null) {
                        try {
                            result = intConverter(value.toInt(), selectedSystem!!)
                            errorMessage = null
                        } catch (_: NumberFormatException) {
                            errorMessage = "Wprowadź poprawną liczbę całkowitą"
                            result = ""
                        }
                    } else {
                        errorMessage = "Wybierz system liczbowy"
                        result = ""
                    }
                },
            ) {
                Text("Przelicz", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(2.dp))

            // Wynik
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column (
                    modifier = Modifier.padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Wynik przeliczenia:",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = result,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

        }




    }



}

@Preview
@Composable
fun AddAd() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight(),

        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Reklama",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Image(
                painter = painterResource(id = R.drawable.example_ad),
                contentDescription = "Advertisement Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


fun intConverter(value: Int, numeralSystem: NumeralSystem): String {
    val isNegative = value < 0
    val absValue = Math.abs(value.toLong()) // Używamy toLong() aby uniknąć problemu z Integer.MIN_VALUE

    val result = when (numeralSystem) {
        NumeralSystem.BINARY -> absValue.toString(2)
        NumeralSystem.BASE8 -> absValue.toString(8)
        NumeralSystem.DUODECIMAL -> absValue.toString(12)
        NumeralSystem.HEXADECIMAL -> absValue.toString(16)
        NumeralSystem.VIGESIMAL -> absValue.toString(20)
        NumeralSystem.BASE36 -> absValue.toString(36)
    }

    return if (isNegative) "-$result" else result
}

enum class NumeralSystem {
    BINARY, BASE8, DUODECIMAL, HEXADECIMAL, VIGESIMAL, BASE36
}