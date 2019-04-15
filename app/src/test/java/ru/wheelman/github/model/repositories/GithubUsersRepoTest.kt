package ru.wheelman.github.model.repositories

import androidx.paging.DataSource
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.assertNotNull
import ru.wheelman.github.model.datasources.local.UsersDb
import ru.wheelman.github.model.datasources.remote.GithubService
import ru.wheelman.github.model.entities.User

class GithubUsersRepoTest {

    private val usersDb: UsersDb = mockk()
    private val githubService: GithubService = mockk()
    private val githubUsersRepo = GithubUsersRepo(usersDb, githubService)

    @Before
    fun setUp() {

    }

    @Test
    fun getUsers() = runBlocking {
        val dataSource: DataSource.Factory<Int, User> = mockk()
        every { usersDb.usersDao().getUsers() } returns dataSource
        val result = githubUsersRepo.getUsers(CoroutineScope(coroutineContext))
        assertNotNull(result)
    }

    @Test
    fun tryFetchingUsersFromNetwork() = runBlocking {
        var onSuccessInvoked = false
        var onErrorInvoked = false
        val user = User(1, "name", "url", 2.1f, 1, "url1")
        val userList = listOf(user)
        val onSuccess: (suspend () -> Unit) = { onSuccessInvoked = true }
        val onError: (suspend (String) -> Unit) = { onErrorInvoked = true }
        coEvery {
            githubService.getUsers(
                any(),
                any(),
                onSuccess = coInvoke(userList),
                onError = coInvoke("error")
            )
        } just runs
        coEvery { usersDb.usersDao().deleteAllUsers() } just runs
        coEvery { usersDb.usersDao().insertUsers(any()) } just runs
        githubUsersRepo.tryFetchingUsersFromNetwork(
            onSuccess,
            onError
        )
        coVerifyOrder {
            usersDb.usersDao().deleteAllUsers()
            usersDb.usersDao().insertUsers(userList)
        }
        if (!onSuccessInvoked || !onErrorInvoked) throw Throwable("one or more functions have not been invoked!")
    }

    @Test
    fun findUsers() = runBlocking {
        val result = githubUsersRepo.findUsers("123", CoroutineScope(coroutineContext))
        assertNotNull(result)
    }

    @After
    fun tearDown() {

    }

    companion object {

        @BeforeClass @JvmStatic
        fun before() {
            mockkStatic(Dispatchers::class)
            every { Dispatchers.IO } returns Dispatchers.Unconfined
        }


        @AfterClass @JvmStatic
        fun after() {
            unmockkAll()
        }
    }
}