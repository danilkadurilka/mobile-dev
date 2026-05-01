package ci.nsu.moble.main.ui.deposit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun DepositFlowScreen(
    viewModel: DepositVM,
    onFinish: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var step by remember { mutableStateOf(1) }
    LaunchedEffect(Unit) {
        viewModel.reset()
        step = 1
    }
    when (step)
    {
        1 -> Step1Screen(
            state = state,
            onAmountChange = viewModel::onAmountChange,
            onMonthsChange = viewModel::onMonthsChange,
            onNavigateBack = onFinish,
            onNavigateToStep2 = { step = 2 }
        )
        2 -> Step2Screen(
            state = state,
            onTopUpChange = viewModel::onTopUpChange,
            onNavigateBack = { step = 1 },
            onCalculate = {
                viewModel.calculate()
                step = 3
            }
        )
        3 -> ResultScreen(
            result = state.result,
            onSaveAndExit = {
                viewModel.save()
                onFinish()
            }
        )
    }
}