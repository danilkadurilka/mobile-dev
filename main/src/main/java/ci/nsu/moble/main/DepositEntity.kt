package ci.nsu.moble.main

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deposits")
data class DepositEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val initialAmount: Double,
    val months: Int,
    val rate: Double,
    val monthlyTopUp: Double?,
    val finalAmount: Double,
    val profit: Double,
    val calculationDate: String
)