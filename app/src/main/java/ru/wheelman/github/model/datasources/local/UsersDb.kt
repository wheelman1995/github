package ru.wheelman.github.model.datasources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.wheelman.github.model.entities.User

private const val NAME = "users"
fun getUsersDb(context: Context) =
    Room.databaseBuilder(context, UsersDb::class.java, NAME).build()

@Database(entities = [User::class], version = 1)
abstract class UsersDb : RoomDatabase() {

    abstract fun usersDao(): UsersDao
}