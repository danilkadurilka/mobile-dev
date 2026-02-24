package ci.nsu.moble.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

data class ColorItem(
    val name: String,
    val color: Color
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ColorFinderScreen()
                }
            }
        }
    }
}

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

@Composable
fun ColorFinderScreen() {
    val availableColors = remember {
        listOf(
            ColorItem("Red", Color.Red),
            ColorItem("Orange", Color(0xFFFFA500)),
            ColorItem("Yellow", Color.Yellow),
            ColorItem("Green", Color.Green),
            ColorItem("Blue", Color.Blue),
            ColorItem("Indigo", Color(0xFF4B0082)),
            ColorItem("Violet", Color(0xFF8F00FF))
        )
    }

    var searchText by remember { mutableStateOf("") }
    var buttonColor by remember { mutableStateOf(Color.Gray) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Color Finder",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Введите название цвета") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val foundColor = availableColors.find {
                    it.name.equals(searchText, ignoreCase = true)
                }

                if (foundColor != null) {
                    buttonColor = foundColor.color
                    Log.d("ColorFinder", "Цвет найден: ${foundColor.name}")
                } else {
                    buttonColor = Color.Gray
                    Log.d("ColorFinder", "Пользовательский цвет '$searchText' не найден")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            )
        ) {
            Text(
                text = "Применить цвет",
                color = if (isColorDark(buttonColor)) Color.White else Color.Black
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Палитра цветов:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableColors) { colorItem ->
                ColorPaletteItem(colorItem)
            }
        }
    }
}

fun isColorDark(color: Color): Boolean {
    return color == Color.Blue ||
            color == Color(0xFF4B0082) ||
            color == Color(0xFF8F00FF) ||
            color == Color.Gray
}

@Composable
fun ColorPaletteItem(colorItem: ColorItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorItem.color
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = colorItem.name,
                color = if (isColorDark(colorItem.color)) Color.White else Color.Black
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyAppTheme {
        ColorFinderScreen()
    }
}