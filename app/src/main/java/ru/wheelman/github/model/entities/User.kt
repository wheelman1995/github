package ru.wheelman.github.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @field:PrimaryKey val id: Long,
    val name: String,
    val avatarUrl: String,
    val score: Float,
    val nextPageKey: Long?,
    val htmlUrl: String
)