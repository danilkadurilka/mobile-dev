package ci.nsu.moble.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import kotlin.system.exitProcess

@Composable
fun DepositNavigation(viewModel: DepositVM)
{
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsState()
    NavHost(navController, startDestination = "main")
    {
        composable("main")
        {
            Column(Modifier.fillMaxSize().padding(16.dp), Arrangement.Center, Alignment.CenterHorizontally)
            {
                Text("Расчет вкладов", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(30.dp))
                Button(onClick = {viewModel.reset(); navController.navigate("step1")}, Modifier.fillMaxWidth())
                {
                    Text("Рассчитать")
                }
                OutlinedButton(onClick = {navController.navigate("history")}, Modifier.fillMaxWidth())
                {
                    Text("История")
                }
                TextButton(onClick = {exitProcess(0)})
                {
                    Text("Выход")
                }
            }
        }
        composable("step1")
        {
            Column(Modifier.padding(16.dp))
            {
                Text("Этап 1: Ввод данных", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(state.amount, viewModel::onAmountChange, label = { Text("Сумма") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(state.months, viewModel::onMonthsChange, label = { Text("Срок (мес)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth().padding(top = 16.dp), Arrangement.SpaceBetween)
                {
                    Button(onClick = { navController.popBackStack() }) { Text("В начало") }
                    Button(onClick = { navController.navigate("step2") }, enabled = state.amount.isNotBlank() && state.months.isNotBlank()) { Text("Далее") }
                }
            }
        }

        composable("step2")
        {
            Column(Modifier.padding(16.dp))
            {
                Text("Этап 2: Доп. параметры", style = MaterialTheme.typography.titleLarge)
                Text("Ставка: ${state.selectedRate}%", Modifier.padding(vertical = 16.dp), style = MaterialTheme.typography.bodyLarge)
                OutlinedTextField(state.monthTopUp, viewModel::onTopUpChange, label = { Text("Пополнение (мес)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth().padding(top = 16.dp), Arrangement.SpaceBetween)
                {
                    Button(onClick = { navController.popBackStack() }) { Text("Назад") }
                    Button(onClick = { viewModel.calculation(); navController.navigate("result") }) { Text("Рассчитать") }
                }
            }
        }

        composable("result")
        {
            val res = state.result ?: return@composable
            Column(Modifier.padding(16.dp))
            {
                Card(Modifier.fillMaxWidth())
                {
                    Column(Modifier.padding(16.dp))
                    {
                        Text("Итог: ${String.format("%.2f", res.finalAmount)} ₽", style = MaterialTheme.typography.headlineSmall)
                        Text("Прибыль: ${String.format("%.2f", res.profit)} ₽")
                        Text("Ставка: ${res.rate}% на ${res.months} мес.")
                    }
                }
                Button(onClick = { viewModel.save(); navController.navigate("main") }, Modifier.fillMaxWidth().padding(top = 16.dp)) { Text("Сохранить и выйти") }
            }
        }

        composable("history")
        {
            LazyColumn(Modifier.padding(16.dp))
            {
                items(state.history) { item ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text(item.calculationDate, style = MaterialTheme.typography.labelSmall)
                            Text("Итого: ${String.format("%.2f", item.finalAmount)} (из ${item.initialAmount})")
                        }
                    }
                }
            }
        }
    }
}