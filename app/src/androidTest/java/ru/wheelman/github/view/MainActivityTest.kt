package ru.wheelman.github.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.wheelman.github.R

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule var activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun onCreate() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withText(R.string.app_name)).check(matches(withParent(withId(R.id.toolbar))))
    }
}