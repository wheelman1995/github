package ru.wheelman.github.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.*
import ru.wheelman.github.App
import ru.wheelman.github.di.components.DaggerAppTestComponent
import ru.wheelman.github.model.entities.Result
import ru.wheelman.github.model.entities.User
import ru.wheelman.github.model.repositories.IGithubUsersRepo

class UsersFragmentViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @MockK lateinit var githubUsersRepo: IGithubUsersRepo
    private lateinit var loading: ObservableBoolean
    private lateinit var viewModel: UsersFragmentViewModel
    private lateinit var allUsersResult: MockResult

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        allUsersResult = MockResult()
        coEvery { githubUsersRepo.getUsers(any()) } returns allUsersResult.result
        loading = spyk(ObservableBoolean(false))
        val appTestComponent = DaggerAppTestComponent.builder()
            .githubRepo(githubUsersRepo)
            .context(mockk())
            .build()
        mockkObject(App.Companion)
        every { App.Companion.appComponent } returns appTestComponent
        viewModel = spyk<UsersFragmentViewModel>(recordPrivateCalls = true)
        every { viewModel.loading } returns loading
    }

    @Test
    fun initDagger() {
        assertEquals(viewModel.githubUsersRepo, githubUsersRepo)
    }

    @Test
    fun loadUsers() {
        allUsersResult.livePagedList.value = allUsersResult.pagedList
        allUsersResult.errors.value = "error"
        viewModel.errors.observeForever {
            println(it)
            assertEquals("error", it)
        }
        viewModel.allUsersLivePagedList.observeForever {
            println(it)
            assertEquals(allUsersResult.pagedList, it)
        }
    }

    @Test
    fun onRefreshAllUsersError() {
        coEvery {
            githubUsersRepo.tryFetchingUsersFromNetwork(
                any(),
                captureCoroutine()
            )
        } answers {
            coroutine<(suspend (String) -> Unit)>().coInvoke("error")
        }
        viewModel.onRefresh()
        verifyOrder {
            loading.set(true)
            loading.set(false)
        }
        viewModel.errors.observeForever {
            println(it)
            assertEquals("error", it)
        }
    }

    @Test
    fun onRefreshAllUsersSuccess() {
        viewModel.allUsersLivePagedList.observeForever { }
        allUsersResult.livePagedList.value = allUsersResult.pagedList
        coEvery {
            githubUsersRepo.tryFetchingUsersFromNetwork(
                coInvoke<(suspend () -> Unit), Unit>(),
                any()
            )
        } just runs
        viewModel.onRefresh()
        verifyOrder {
            loading.set(true)
            allUsersResult.dataSource.invalidate()
            loading.set(false)
        }
    }

    @Test
    fun onRefreshFoundUsers() {
        val foundUsersResult = MockResult()
        coEvery { githubUsersRepo.findUsers(any(), any()) } returns foundUsersResult.result
        viewModel.foundUsersLivePagedList.observeForever { }
        foundUsersResult.livePagedList.value = foundUsersResult.pagedList
        viewModel.onQueryTextChange("123")
        assertFalse(viewModel.showAllUsers.value!!)
        viewModel.onRefresh()
        verify { foundUsersResult.dataSource.invalidate() }
    }

    @Test
    fun onQueryTextChangeEmptyText() {
        viewModel.onQueryTextChange("")
        assertTrue(viewModel.showAllUsers.value!!)
    }

    @Test
    fun onQueryTextChange() {
        val foundUsersResult = MockResult()
        val foundUsersResult2 = MockResult()
        coEvery {
            githubUsersRepo.findUsers(
                any(),
                any()
            )
        } returnsMany listOf(foundUsersResult.result, foundUsersResult2.result)
        viewModel.errors.observeForever { }
        viewModel.foundUsersLivePagedList.observeForever { }
        viewModel.onQueryTextChange("123")
        assertFalse(viewModel.showAllUsers.value!!)
        foundUsersResult.errors.value = "error"
        foundUsersResult.livePagedList.value = foundUsersResult.pagedList
        assertEquals("error", viewModel.errors.value)
        assertEquals(foundUsersResult.pagedList, viewModel.foundUsersLivePagedList.value)
        viewModel.onQueryTextChange("123")
        foundUsersResult2.errors.value = "error2"
        foundUsersResult2.livePagedList.value = foundUsersResult2.pagedList
        assertEquals("error2", viewModel.errors.value)
        assertEquals(foundUsersResult2.pagedList, viewModel.foundUsersLivePagedList.value)
        foundUsersResult.errors.value = "error3"
        foundUsersResult.livePagedList.value = foundUsersResult.pagedList
        assertEquals("error2", viewModel.errors.value)
        assertEquals(foundUsersResult2.pagedList, viewModel.foundUsersLivePagedList.value)
        verify(exactly = 2) { viewModel["removePreviousSearchSource"]() }
    }

    companion object {

        @BeforeClass @JvmStatic
        fun before() {
            Dispatchers.setMain(Dispatchers.Unconfined)
        }


        @AfterClass @JvmStatic
        fun after() {
            Dispatchers.resetMain()
        }
    }

    private class MockResult {

        val dataSource: DataSource<Long, User> = mockk()
        val pagedList: PagedList<User> = mockk()
        val livePagedList: MutableLiveData<PagedList<User>> = MutableLiveData()
        val errors: MutableLiveData<String> = MutableLiveData()
        val result: Result = Result(errors, livePagedList)

        init {
            every { dataSource.invalidate() } just runs
            every { pagedList.dataSource } returns dataSource
        }
    }
}