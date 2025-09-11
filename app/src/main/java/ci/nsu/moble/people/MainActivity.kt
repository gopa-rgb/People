package ci.nsu.moble.people

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ci.nsu.moble.people.ui.theme.PeopleTheme
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PeopleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InputScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun InputScreen(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }

    // состояние выбранного цвета кнопки
    var buttonColor by remember { mutableStateOf(Color(0xFF6200EE)) }

    // список доступных цветов
    val colorsList = listOf(
        "red", "green", "blue", "yellow", "black", "white",
        "gray", "grey", "cyan", "magenta", "orange", "purple", "brown"
    )

    // преобразователь имени цвета в Color или null, если имя не распознано
    fun tryParseColor(name: String): Color? {
        return when (name.trim().lowercase()) {
            "red" -> Color.Red
            "green" -> Color.Green
            "blue" -> Color.Blue
            "yellow" -> Color.Yellow
            "black" -> Color.Black
            "white" -> Color.White
            "gray", "grey" -> Color.Gray
            "cyan" -> Color.Cyan
            "magenta" -> Color.Magenta
            "orange" -> Color(0xFFFF9800)
            "purple" -> Color(0xFF9C27B0)
            "brown" -> Color(0xFF795548)
            else -> null
        }
    }

    // Вспомогательная функция для отображения превью цвета (возвращает Color — если null, вернёт дефолт)
    fun previewColor(name: String): Color = tryParseColor(name) ?: Color.LightGray

    // вычисляем яркость цвета (для читаемости текста)
    fun computeLuminance(color: Color): Float {
        fun linearize(c: Float): Float =
            if (c <= 0.04045f) c / 12.92f
            else (((c + 0.055f) / 1.055f).toDouble().pow(2.4)).toFloat()

        val r = linearize(color.red)
        val g = linearize(color.green)
        val b = linearize(color.blue)
        return 0.2126f * r + 0.7152f * g + 0.0722f * b
    }

    val contentColorForButton =
        if (computeLuminance(buttonColor) < 0.5f) Color.White else Color.Black

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Введите цвет (например: red, blue...)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val parsed = tryParseColor(text)
                if (parsed != null) {
                    buttonColor = parsed
                } else {
                    // запись ошибки в лог — цвет не распознан
                    Log.e("ColorInput", "Unrecognized color: '$text'")
                    // при желании: можно показать Snackbar/Toast; но по задаче — логирование
                }
            }, // изменяем цвет только при нажатии (или логируем ошибку)
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = contentColorForButton
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отправить")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Список доступных цветов (нажмите, чтобы вставить в поле ввода):",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Список внизу — занимает оставшееся место и скроллится
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(colorsList) { colorName ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            text = colorName // вставляем имя цвета в поле ввода
                        }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // превью цвета (если цвет не распознан — показываем LightGray)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 12.dp)
                            .background(previewColor(colorName))
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = colorName, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "Нажмите, чтобы вставить",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Divider()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Распознанный цвет (после нажатия): ${text.ifBlank { "дефолт" }}",
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InputScreenPreview() {
    PeopleTheme {
        InputScreen()
    }
}
