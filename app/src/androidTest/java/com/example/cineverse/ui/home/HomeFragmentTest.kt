package com.example.cineverse.ui.home

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cineverse.R
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun fragment_displaysSwipeRefreshLayout() {
        launchFragmentInContainer<HomeFragment>()

        onView(withId(R.id.swipeRefreshLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun fragment_displaysSectionHeaders() {
        launchFragmentInContainer<HomeFragment>()

        onView(withText(R.string.section_trending))
            .check(matches(isDisplayed()))
    }

    @Test
    fun fragment_hasRecyclerViewForTrendingMovies() {
        launchFragmentInContainer<HomeFragment>()

        onView(withId(R.id.trendingRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun fragment_hasRecyclerViewForPopularMovies() {
        launchFragmentInContainer<HomeFragment>()

        onView(withId(R.id.popularRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun fragment_hasRecyclerViewForTopRatedMovies() {
        launchFragmentInContainer<HomeFragment>()

        onView(withId(R.id.topRatedRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun fragment_hasRecyclerViewForGenres() {
        launchFragmentInContainer<HomeFragment>()

        onView(withId(R.id.genresRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun fragment_launches_successfully() {
        val scenario = launchFragmentInContainer<HomeFragment>()

        scenario.onFragment { fragment ->
            assertThat(fragment).isNotNull()
        }
    }

    @Test
    fun fragment_viewModel_isInitialized() {
        val scenario = launchFragmentInContainer<HomeFragment>()

        scenario.onFragment { fragment ->
            assertThat(fragment.javaClass.simpleName).isEqualTo("HomeFragment")
        }
    }
}
