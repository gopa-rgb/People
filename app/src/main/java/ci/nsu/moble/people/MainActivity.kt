package ci.nsu.moble.people

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ci.nsu.moble.people.ui.theme.PeopleTheme

// Глобальная мапа для текстовых названий цветов (в формате ARGB int)
val colorMap = mapOf(
    "red" to 0xFFFF0000.toInt(),
    "blue" to 0xFF0000FF.toInt(),
    "green" to 0xFF00FF00.toInt(),
    "yellow" to 0xFFFFFF00.toInt(),
    "purple" to 0xFF800080.toInt(), // #800080
    "orange" to 0xFFFFA500.toInt(),
    "pink" to 0xFFFFC0CB.toInt(),
    "black" to 0xFF000000.toInt(),
    "white" to 0xFFFFFFFF.toInt(),
    "gray" to 0xFF808080.toInt(),
    "cyan" to 0xFF00FFFF.toInt(),
    "magenta" to 0xFFFF00FF.toInt(),
    // Новые цвета
    "brown" to 0xFFA52A2A.toInt(),
    "teal" to 0xFF008080.toInt(),
    "navy" to 0xFF000080.toInt(),
    "maroon" to 0xFF800000.toInt(),
    "olive" to 0xFF808000.toInt(),
    "silver" to 0xFFC0C0C0.toInt(),
    "gold" to 0xFFFFD700.toInt(),
    "lime" to 0xFF32CD32.toInt(), // Яркий зелёный
    "indigo" to 0xFF4B0082.toInt()
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PeopleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ColorChangerApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ColorChangerApp(modifier: Modifier = Modifier) {
    var inputText by remember { mutableStateOf("") }  // Текущий ввод в TextField
    var buttonColor by remember { mutableStateOf(Color.Gray) }  // Цвет кнопки
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Функция для парсинга цвета
    fun parseColor(input: String): Color? {
        val trimmed = input.trim().lowercase()

        try {
            val hex = if (trimmed.startsWith("#")) trimmed else "#$trimmed"
            val colorInt = AndroidColor.parseColor(hex)
            return Color(colorInt)
        } catch (e: IllegalArgumentException) {
            // Игнорируем, попробуем текстовое название ниже
        }

        // Проверяем на текстовое название
        colorMap[trimmed]?.let { colorInt ->
            return Color(colorInt)
        }

        return null // Не распарсилось
    }

    // Функция для изменения цвета при нажатии кнопки
    fun changeColor() {
        val parsedColor = parseColor(inputText)
        if (parsedColor != null) {
            buttonColor = parsedColor
            errorMessage = null
        } else {
            errorMessage = if (inputText.isNotEmpty()) {
                "Невалидный цвет. Выберите из списка ниже:"
            } else {
                null
            }
        }
    }

    // Функция для выбора цвета из списка
    fun selectColor(colorName: String) {
        colorMap[colorName.lowercase()]?.let { colorInt ->
            buttonColor = Color(colorInt)
            errorMessage = null  // Скрываем ошибку после выбора
            inputText = ""  // Очищаем поле ввода
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,  // Изменено на Top для размещения списка внизу
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Введите цвет (hex или название, например #FF0000 или red)") },
            modifier = Modifier.padding(bottom = 16.dp),
            singleLine = true
        )
        Button(
            onClick = { changeColor() },  // При нажатии кнопки меняем цвет
            modifier = Modifier.padding(bottom = 16.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = buttonColor
            )
        ) {
            Text("Изменить цвет", color = Color.White)
        }

        // Показываем ошибку, если есть
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Список доступных цветов (всегда видим)
        Text(
            text = "Или выберите цвет из списка:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(0.6f)  // Ограничиваем высоту списка
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(colorMap.keys.toList().sorted()) { colorName ->  // Сортируем для удобства
                Text(
                    text = colorName.replaceFirstChar { it.uppercase() },  // Капитализируем
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .clickable { selectColor(colorName) }
                        .padding(8.dp)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorChangerAppPreview() {
    PeopleTheme {
        ColorChangerApp()
    }
}
