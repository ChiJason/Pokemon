package com.example.pokemon.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Database(
    entities = [
        Pokemon::class,
        Type::class,
        TypePokemonCrossRef::class,
        Species::class,
        Pocket::class
    ],
    version = 1
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
}

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time?.toLong()
}
