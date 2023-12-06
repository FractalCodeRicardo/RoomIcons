package com.thisisthetime.roomicons

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Entity(tableName = "colors")
data class Color(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val name: String
)

@Dao
interface ColorsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(color: Color): Long

    @Query("SELECT * FROM colors")
     suspend fun getAll(): List<Color>
}


@Database(entities = [Color::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun colorsDao(): ColorsDao
}