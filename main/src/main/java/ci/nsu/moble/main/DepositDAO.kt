package ci.nsu.moble.main

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDAO
{
    @Insert
    fun insert (deposit: DepositEntity)
    @Query("SELECT * FROM deposits ORDER BY calculationDate DESC")
    fun getAllDepos(): Flow<List<DepositEntity>>
}
@Database(entities = [DepositEntity::class], version = 1)
abstract class DepositAppDatabase : RoomDatabase()
{
    abstract fun dao() : DepositDAO
    companion object
    {
        @Volatile private var INSTANCE: DepositAppDatabase? = null
        fun getDb (context: Context): DepositAppDatabase
        {
            return INSTANCE ?: synchronized(this)
            {
                Room.databaseBuilder(context.applicationContext, DepositAppDatabase::class.java, "deposits_database").build().also{INSTANCE = it}
            }
        }
    }
}