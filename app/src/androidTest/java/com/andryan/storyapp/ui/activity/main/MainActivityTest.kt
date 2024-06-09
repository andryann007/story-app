package com.andryan.storyapp.ui.activity.main

import androidx.paging.ExperimentalPagingApi
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.andryan.storyapp.R
import com.andryan.storyapp.ui.activity.splash.SplashActivity
import com.andryan.storyapp.utils.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@ExperimentalPagingApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get: Rule
    var hiltRule = HiltAndroidRule(this)

    @get: Rule
    val activity = ActivityScenarioRule(SplashActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun userLogout_Success() {
        intended(hasComponent(MainActivity::class.java.name))

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.button_logout_desc)).perform(click())
        onView(withText(R.string.logout_title)).inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
        onView(withText(R.string.positive_button_text)).perform(click())

        onView(withId(R.id.welcome)).check(matches(isDisplayed()))
    }
}