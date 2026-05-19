package ci.nsu.moble.domain.models

import java.util.Date

data class DepositCalculation(
    val id: Long = 0,
    val userId: Long,
    val initialAmount: Double,
    val months: Int,
    val rate: Double,
    val monthlyTopUp: Double?,
    val finalAmount: Double,
    val profit: Double,
    val calculationDate: Date
)