package ru.wheelman.github.view.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import ru.wheelman.github.R
import ru.wheelman.github.view.fragments.UsersFragmentDirections.Companion.actionUsersFragmentToAvatarFragment

@RunWith(AndroidJUnit4::class)
class AvatarFragmentTest {

    @Test
    fun onCreateView() {
        val scenario = launchFragmentInContainer<AvatarFragment>(
            actionUsersFragmentToAvatarFragment("https://developer.android.com/images/training/testing/espresso-cheatsheet.png").arguments,
            android.R.style.Theme_Material_NoActionBar
        )
        onView(withId(R.id.aciv_avatar)).check(matches(isCompletelyDisplayed()))
    }
}