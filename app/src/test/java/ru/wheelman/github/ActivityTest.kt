package ru.wheelman.github

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import ru.wheelman.github.view.MainActivity


@RunWith(RobolectricTestRunner::class)
class ActivityTest {

    @Test
    fun test() {
        val activity = Robolectric.setupActivity(MainActivity::class.java)
        assertEquals("Github", activity.supportActionBar?.title)
    }
}