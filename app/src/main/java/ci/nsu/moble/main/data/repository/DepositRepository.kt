package ci.nsu.moble.main.data.repository

import ci.nsu.moble.main.data.database.DepositDAO
import ci.nsu.moble.main.data.database.DepositEntity
import kotlinx.coroutines.flow.Flow

class DepositRepository(private val dao: DepositDAO)
{
    fun getDeposits(userId: Long): Flow<List<DepositEntity>>
    {
        return dao.getDepositsByUserId(userId)
    }
    suspend fun saveDeposit(deposit: DepositEntity)
    {
        dao.insert(deposit)
    }
    suspend fun deleteDeposit(deposit: DepositEntity)
    {
        dao.delete(deposit)
    }
}
