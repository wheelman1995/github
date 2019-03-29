package ru.wheelman.github.model.datasources.local

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.wheelman.github.model.entities.User

@Dao
interface UsersDao {

    @Query("select * from User order by id asc")
    fun getUsers(): DataSource.Factory<Int, User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Query("delete from User")
    suspend fun deleteAllUsers()

    @Query("select count(*) from User")
    suspend fun getUsersNumber(): Long
}