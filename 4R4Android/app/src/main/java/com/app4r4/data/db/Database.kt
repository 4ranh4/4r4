package com.app4r4.data.db

import androidx.room.*
import com.app4r4.data.model.ActivityType
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "activity_records")
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val description: String,
    val carbonKg: Double,
    val date: Long = System.currentTimeMillis()
)

@Dao
interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ActivityRecord): Long

    @Query("SELECT * FROM activity_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE date >= :startOfMonth ORDER BY date DESC")
    fun getRecordsThisMonth(startOfMonth: Long): Flow<List<ActivityRecord>>

    @Query("SELECT SUM(carbonKg) FROM activity_records WHERE date >= :startOfMonth")
    fun getTotalCarbonThisMonth(startOfMonth: Long): Flow<Double?>

    @Query("SELECT SUM(carbonKg) FROM activity_records WHERE date >= :start AND date < :end")
    suspend fun getTotalCarbonBetween(start: Long, end: Long): Double?

    @Delete
    suspend fun delete(record: ActivityRecord)
}

@Database(entities = [ActivityRecord::class], version = 1, exportSchema = false)
abstract class App4R4Database : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
}
