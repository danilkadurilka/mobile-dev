package ci.nsu.moble.main.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DepositEntity::class], version = 2)
abstract class DepositAppDatabase : RoomDatabase()
{
    abstract fun dao(): DepositDAO
    companion object
    {
        @Volatile
        private var INSTANCE: DepositAppDatabase? = null
        fun getDb(context: Context): DepositAppDatabase
        {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DepositAppDatabase::class.java,
                    "deposits_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}