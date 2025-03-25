package com.example.pokemon.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pokemon(
    @PrimaryKey val id: Long,
    val name: String,
    val image: String
)
