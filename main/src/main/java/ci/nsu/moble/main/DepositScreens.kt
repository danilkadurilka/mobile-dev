package ci.nsu.moble.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.compose.ui.unit.sp
import kotlin.system.exitProcess
@Composable
fun MainScreen(
    onNavigateToStep1: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Расчет вкладов",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onNavigateToStep1,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Рассчитать")
        }

        OutlinedButton(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("История")
        }

        TextButton(
            onClick = { exitProcess(0) }
        ) {
            Text("Выход")
        }
    }
}

// Экран ввода данных
@Composable
fun Step1Screen(
    state: DepositUiState,
    onAmountChange: (String) -> Unit,
    onMonthsChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToStep2: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Этап 1: Ввод данных",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.amount,
            onValueChange = onAmountChange,
            label = { Text("Сумма") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.months,
            onValueChange = onMonthsChange,
            label = { Text("Срок (мес)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onNavigateBack) {
                Text("В начало")
            }

            Button(
                onClick = onNavigateToStep2,
                enabled = state.amount.isNotBlank() && state.months.isNotBlank()
            ) {
                Text("Далее")
            }
        }
    }
}

// Экран дополнительных параметров
@Composable
fun Step2Screen(
    state: DepositUiState,
    onTopUpChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onCalculate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Этап 2: Доп. параметры",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ставка: ${state.selectedRate}%",
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = state.monthTopUp,
            onValueChange = onTopUpChange,
            label = { Text("Пополнение (мес)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onNavigateBack) {
                Text("Назад")
            }

            Button(onClick = onCalculate) {
                Text("Рассчитать")
            }
        }
    }
}

@Composable
fun ResultScreen(
    result: DepositEntity?,
    onSaveAndExit: () -> Unit
) {
    if (result == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Итог: ${String.format("%.2f", result.finalAmount)} ₽",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Прибыль: ${String.format("%.2f", result.profit)} ₽"
                )

                Text(
                    text = "Ставка: ${result.rate}% на ${result.months} мес."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSaveAndExit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить и выйти")
        }
    }
}

// Экран истории
@Composable
fun HistoryScreen(
    history: List<DepositEntity>,
    onNavigateToMain: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Button(
                onClick = onNavigateToMain,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("На основную страницу")
            }
        }

        items(history) { item ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = item.calculationDate,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = "Итого: ${String.format("%.2f", item.finalAmount)} (из ${item.initialAmount})"
                    )
                }
            }
        }
    }
}