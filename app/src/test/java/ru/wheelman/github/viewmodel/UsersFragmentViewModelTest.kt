package ru.wheelman.github.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import ru.wheelman.github.App
import ru.wheelman.github.di.components.DaggerAppTestComponent
import ru.wheelman.github.model.entities.Result
import ru.wheelman.github.model.entities.User
import ru.wheelman.github.model.repositories.IGithubUsersRepo

class UsersFragmentViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @MockK lateinit var githubUsersRepo: IGithubUsersRepo
    @MockK lateinit var result: Result
    @MockK(relaxed = true) lateinit var livePagedList: LiveData<PagedList<User>>
    val errors = MutableLiveData<String>()
    lateinit var viewModel: UsersFragmentViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        result = Result(errors, livePagedList)
        coEvery { githubUsersRepo.getUsers(any()) } answers { result }
        val appTestComponent = DaggerAppTestComponent.builder()
            .githubRepo(githubUsersRepo)
            .context(mockk())
            .build()
        mockkObject(App.Companion)
        every { App.Companion.appComponent } returns appTestComponent
        viewModel = spyk<UsersFragmentViewModel>(recordPrivateCalls = true)
    }

    @Test
    fun initDagger() {
        assertEquals(viewModel.githubUsersRepo, githubUsersRepo)
    }

    @Test
    fun loadUsers() {
        errors.value = "error"
        viewModel.errors.observeForever {
            assertTrue(it == "error")
        }
        viewModel.allUsersLivePagedList.observeForever {
            assertTrue(it != null)
        }
    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun before() {
            Dispatchers.setMain(Dispatchers.Unconfined)
        }


        @AfterClass
        @JvmStatic
        fun after() {
            Dispatchers.resetMain()
        }
    }
}