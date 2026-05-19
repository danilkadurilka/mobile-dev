package ci.nsu.moble.main.ui.deposit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.moble.main.data.database.DepositEntity
import ci.nsu.moble.main.data.repository.DepositRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DepositUiState(
    val amount: String = "",
    val months: String = "",
    val monthTopUp: String = "",
    val selectedRate: Double = 0.0,
    val result: DepositEntity? = null,
    val profit: Double = 0.0,
    val history: List<DepositEntity> = emptyList()
)

class DepositVM(
    userId: Long,
    private val repository: DepositRepository
) : ViewModel()
{
    private val _uiState = MutableStateFlow(DepositUiState())
    val uiState = _uiState.asStateFlow()

    private var currentUserId: Long = userId

    init
    {
        if (userId > 0)
        {
            loadDeposits()
        }
    }
    private fun loadDeposits()
    {
        viewModelScope.launch {
            repository.getDeposits(currentUserId).collect { list ->
                _uiState.update { it.copy(history = list) }
            }
        }
    }
    fun updateUserId(newUserId: Long)
    {
        if (newUserId > 0 && newUserId != currentUserId)
        {
            currentUserId = newUserId
            loadDeposits()
        }
    }
    fun onAmountChange(value: String)
    {
        _uiState.update { it.copy(amount = value) }
    }
    fun onMonthsChange(value: String)
    {
        val m = value.toIntOrNull() ?: 0
        val r = when
        {
            m <= 0 -> 0.0
            m < 6 -> 15.0
            m < 12 -> 10.0
            else -> 5.0
        }
        _uiState.update { it.copy(months = value, selectedRate = r) }
    }
    fun onTopUpChange(value: String)
    {
        _uiState.update { it.copy(monthTopUp = value) }
    }
    fun calculate()
    {
        val p = _uiState.value.amount.toDoubleOrNull() ?: 0.0
        val n = _uiState.value.months.toIntOrNull() ?: 0
        val r = _uiState.value.selectedRate
        val m = _uiState.value.monthTopUp.toDoubleOrNull() ?: 0.0
        var total = p
        val monthlyRate = (r / 100) / 12
        repeat(n)
        {
            total += total * monthlyRate
            if (m > 0)
                total += m
        }
        val totalDeposits = p + m * n
        val profit = total - totalDeposits
        val dateToStr = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
        _uiState.update{
            it.copy(
                result = DepositEntity(
                    userId = currentUserId,
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
            _uiState.value.result?.let { entity ->
                repository.saveDeposit(entity)
            }
        }
    }
    fun reset() {
        _uiState.update {
            DepositUiState(history = it.history)
        }
    }
    fun delete(deposit: DepositEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDeposit(deposit)
        }
    }
}