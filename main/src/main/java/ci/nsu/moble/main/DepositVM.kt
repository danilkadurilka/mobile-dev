package ci.nsu.moble.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DepositUiState(
    val amount: String = "",
    val months: String = "",
    val monthTopUp: String = "",
    val selectedRate: Double = 0.0,
    val result: DepositEntity? = null,
    val profit: Double = 0.0,
    val history: List<DepositEntity> = emptyList()
    )

class DepositVM(private val dao: DepositDAO) : ViewModel()
{
    private val _uiState = MutableStateFlow(DepositUiState())
    val uiState = _uiState.asStateFlow()
    init
    {
        viewModelScope.launch{
            dao.getAllDepos().collect{
                list -> _uiState.update{
                    it.copy(history = list)
                }
            }
        }
    }
    fun onAmountChange(value: String)
    {
        _uiState.update{
            it.copy(amount = value)
        }
    }

    fun onMonthsChange(value: String)
    {
        val m = value.toIntOrNull() ?: 0
        val r = when {
            m <= 0 -> 0.0
            m < 6 -> 15.0
            m < 12 -> 10.0
            else -> 5.0
        }
        _uiState.update{
            it.copy(months = value, selectedRate = r)
        }
    }

    fun onTopUpChange(value: String)
    {
        _uiState.update {
            it.copy(monthTopUp = value)
        }
    }

    fun calculate()//calculate
    {
        val p = uiState.value.amount.toDoubleOrNull() ?: 0.0
        val n = uiState.value.months.toIntOrNull() ?: 0
        val r = uiState.value.selectedRate
        val m = uiState.value.monthTopUp.toDoubleOrNull() ?: 0.0
        var total = p
        val monthlyRate = (r/100)/12
        repeat(n)
        {
            total += total * monthlyRate
            if (m != null && m > 0) {
                total += m
            }
        }
        val totalDeposits = p + (m ?: 0.0) * n
        val profit = total - totalDeposits
        val dateToStr = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
        _uiState.update{
            it.copy(result= DepositEntity(
                initialAmount = p,
                months = n,
                rate = r,
                monthlyTopUp = m,
                finalAmount = total,
                profit = profit,
                calculationDate = dateToStr
            )
            )
        }
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState.value.result?.let { entity ->
                dao.insert(entity)
            }
        }
    }

    fun reset() {
        _uiState.update {
            DepositUiState(history = it.history)
        }
    }

    fun resetToMain() {
        _uiState.update {
            DepositUiState(history = it.history)
        }
    }

    class DepositVMFactory(private val dao: DepositDAO) : ViewModelProvider.Factory
    {
        override fun <T : ViewModel> create(modelClass: Class<T>): T
        {
            return DepositVM(dao) as T
        }
    }

}