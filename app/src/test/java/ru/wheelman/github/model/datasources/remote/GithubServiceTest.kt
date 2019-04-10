package ru.wheelman.github.model.datasources.remote

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import okhttp3.Headers
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import ru.wheelman.github.model.entities.User

class GithubServiceTest {

    private val githubApi: GithubApi = mockk()
    private val githubService = GithubService(githubApi)
    private var onErrorInvoked = false
    private var onSuccessInvoked = false

    @Before
    fun setUp() {

    }

    @Test
    fun `getUsers should invoke onError with per page error`() = runBlocking {
        githubService.getUsers(
            0,
            1,
            { },
            { onErrorInvoked = true }
        )
        if (!onErrorInvoked) throw Throwable("onError has not been invoked!")
    }

    @Test
    fun `getUsers should invoke onError when page key is null`() = runBlocking {
        githubService.getUsers(
            pageKey = null,
            onSuccess = { },
            onError = { onErrorInvoked = true }
        )
        if (!onErrorInvoked) throw Throwable("onError has not been invoked!")
    }

    @Test
    fun `getUsers should invoke onError when githubApi throws an exception`() = runBlocking {
        every { githubApi.getUsers(any(), any()) } throws Exception("error")
        githubService.getUsers(
            onSuccess = { },
            onError = {
                assertEquals("error", it)
                onErrorInvoked = true
            }
        )
        if (!onErrorInvoked) throw Throwable("onError has not been invoked!")
    }

    @Test
    fun `getUsers should invoke onError when response is not successful`() = runBlocking {
        val unsuccessfulResponse =
            Response.error<List<GithubUser>>(404, ResponseBody.create(null, ""))
        val deferredResponse = CompletableDeferred(unsuccessfulResponse)
        every { githubApi.getUsers(any(), any()) } returns deferredResponse
        githubService.getUsers(
            onSuccess = { },
            onError = {
                assertEquals(unsuccessfulResponse.errorBody()?.string(), it)
                onErrorInvoked = true
            }
        )
        if (!onErrorInvoked) throw Throwable("onError has not been invoked!")
    }

    @Test
    fun `getUsers should invoke onError when body is null`() = runBlocking {
        val successfulResponse = Response.success<List<GithubUser>>(null)
        val deferredResponse = CompletableDeferred(successfulResponse)
        every { githubApi.getUsers(any(), any()) } returns deferredResponse
        githubService.getUsers(
            onSuccess = { },
            onError = {
                assertEquals(successfulResponse.errorBody()?.string() ?: "Unknown error", it)
                onErrorInvoked = true
            }
        )
        if (!onErrorInvoked) throw Throwable("onError has not been invoked!")
    }

    @Test
    fun `getUsers should invoke onError when body is empty`() = runBlocking {
        val successfulResponse = Response.success<List<GithubUser>>(emptyList())
        val deferredResponse = CompletableDeferred(successfulResponse)
        every { githubApi.getUsers(any(), any()) } returns deferredResponse
        githubService.getUsers(
            onSuccess = { },
            onError = { onErrorInvoked = true }
        )
        if (!onErrorInvoked) throw Throwable("onError has not been invoked!")
    }

    @Test
    fun `successful getUsers invocation`() = runBlocking {
        val gitUsers = listOf(
            GithubUser(1, "user1", "url1", 1.0f),
            GithubUser(2, "user2", "url2", 2.0f),
            GithubUser(3, "user3", "url3", 3.0f)
        )
        val users = listOf(
            User(1, "user1", "url1", 1.0f, 1),
            User(2, "user2", "url2", 2.0f, 2),
            User(3, "user3", "url3", 3.0f, 3)
        )
        val successfulResponse = Response.success(
            gitUsers,
            Headers.of(
                "Link",
                "<https://api.github.com/users?per_page=3&since=3>; rel=\"next\", <https://api.github.com/users{?since}>; rel=\"first\""
            )
        )
        val deferredResponse = CompletableDeferred(successfulResponse)
        every { githubApi.getUsers(any(), any()) } returns deferredResponse
        val onSuccess: (suspend (List<User>) -> Unit) = {
            assertEquals(users, it)
            onSuccessInvoked = true
        }
        githubService.getUsers(
            onSuccess = onSuccess,
            onError = { }
        )
        if (!onSuccessInvoked) throw Throwable("onSuccess has not been invoked!")
    }

    @Test
    fun `findUsers should invoke onError with per page error`() = runBlocking {
        var onErrorInvoked = false
        githubService.findUsers(
            0,
            query = "123",
            onSuccess = { },
            onError = { onErrorInvoked = true }
        )
        if (!onErrorInvoked) throw Throwable("onError has not been invoked!")
    }

    @Test
    fun `findUsers should invoke onError if deferred throws an exception`() = runBlocking {
        val deferred: Deferred<SearchResult> = mockk()
        coEvery { deferred.await() } throws Exception("error")
        every { githubApi.searchUsers(any(), any(), any()) } returns deferred
        githubService.findUsers(
            query = "123",
            onSuccess = { },
            onError = {
                assertEquals("error", it)
                onErrorInvoked = true
            }
        )
        if (!onErrorInvoked) throw Throwable("onError has not been invoked!")
    }

    @Test
    fun `successful findUsers invocation`() = runBlocking {
        val timesCancelled: AtomicInt = atomic(0)
        val timesCompletedSuccessfully = atomic(0)
        val repeat = 100_000
        every { githubApi.searchUsers(any(), any(), any()) } answers {
            val query = firstArg<String>()
            val gitUsers = listOf(
                GithubUser(1, "user$query", "url1", 1.0f),
                GithubUser(2, "user$query", "url2", 2.0f),
                GithubUser(3, "user$query", "url3", 3.0f)
            )
            val searchResult = SearchResult(gitUsers, 123L)
            async {
                delay(2000L)
                searchResult
            }
        }
        launch(newFixedThreadPoolContext(8, "")) {
            for (i in 1..repeat) {
                launch {
                    var onSuccessInvoked = false
                    try {
                        githubService.findUsers(
                            query = "$i",
                            onSuccess = {
                                val users = listOf(
                                    User(1, "user$i", "url1", 1.0f, null),
                                    User(2, "user$i", "url2", 2.0f, null),
                                    User(3, "user$i", "url3", 3.0f, null)
                                )
                                assertEquals(users, it)
                                onSuccessInvoked = true
                            },
                            onError = { }
                        )
                    } catch (e: CancellationException) {
                        assertFalse(onSuccessInvoked)
                        timesCancelled.incrementAndGet()
                        throw e
                    }
                    timesCompletedSuccessfully.incrementAndGet()
                    assertTrue(onSuccessInvoked)
                }
            }
        }.join()
        assertEquals(repeat, timesCancelled.value + timesCompletedSuccessfully.value)
        println(
            "timesCancelled: ${timesCancelled.value}\n" +
                    "timesCompletedSuccessfully: ${timesCompletedSuccessfully.value}"
        )
    }

    @After
    fun tearDown() {
        onErrorInvoked = false
        onSuccessInvoked = false
    }
}
