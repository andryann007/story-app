package com.andryan.storyapp.ui.fragment.login

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.paging.ExperimentalPagingApi
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.andryan.storyapp.R
import com.andryan.storyapp.data.remote.retrofit.ApiConfig
import com.andryan.storyapp.utils.EspressoIdlingResource
import com.andryan.storyapp.utils.JsonConverter
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@ExperimentalPagingApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    @get: Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun userLogin_Success() {
        launchFragmentInContainer<LoginFragment>()

        onView(withId(R.id.ed_login_email)).perform(typeText("abc@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password)).perform(typeText("12345678"), closeSoftKeyboard())
        onView(withId(R.id.button_login)).perform(click())

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_login_response"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.main)).check(matches(isDisplayed()))
    }
}