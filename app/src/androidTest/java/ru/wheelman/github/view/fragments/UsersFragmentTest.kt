package ru.wheelman.github.view.fragments

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.CATEGORY_BROWSABLE
import android.net.Uri
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.wheelman.github.App.Companion.appComponent
import ru.wheelman.github.R
import ru.wheelman.github.view.MainActivity
import ru.wheelman.github.view.fragments.UsersRvAdapter.VH

@RunWith(AndroidJUnit4::class)
class UsersFragmentTest {

    @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java, false, false)
    private lateinit var scenario: FragmentScenario<UsersFragment>
    private val navController: NavController = mockk()

    @Before
    fun setUp() {
        intentsTestRule.launchActivity(Intent())
        IdlingRegistry.getInstance().register(appComponent.countingIdlingResource())
        scenario =
            launchFragmentInContainer<UsersFragment>(themeResId = android.R.style.Theme_Material_NoActionBar)
                .apply {
                    onFragment {
                        Navigation.setViewNavController(it.requireView(), navController)
                    }
                }
        every { navController.navigate(ofType(NavDirections::class)) } just runs
    }

    @Test
    fun onCreateView() {
        onView(withId(R.id.rv_users)).check(matches(hasMinimumChildCount(5)))
    }

    @Test
    fun onAvatarClick() {
        onView(withId(R.id.rv_users)).perform(actionOnItemAtPosition<VH>(0, object : ViewAction {
            override fun getDescription() = "performing click"

            override fun getConstraints(): Matcher<View> =
                isAssignableFrom(RecyclerView::class.java)

            override fun perform(uiController: UiController?, view: View?) {
                click().perform(uiController, view?.findViewById(R.id.aciv_avatar))
            }
        }))
        verify(exactly = 1) { navController.navigate(ofType(NavDirections::class)) }
    }

    @Test
    fun onUsernameClick() {
        onView(withId(R.id.rv_users)).perform(actionOnItemAtPosition<VH>(0, object : ViewAction {
            override fun getDescription() = "performing click"

            override fun getConstraints(): Matcher<View> =
                isAssignableFrom(RecyclerView::class.java)

            override fun perform(uiController: UiController?, view: View?) {
                click().perform(uiController, view?.findViewById(R.id.tv_username))
            }
        }))
        intended(
            allOf(
                hasAction(ACTION_VIEW),
                hasCategories(setOf(CATEGORY_BROWSABLE)),
                hasData(notNullValue(Uri::class.java))
            )
        )
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(appComponent.countingIdlingResource())
        intentsTestRule.finishActivity()
    }
}