# CineVerse Test Cases Documentation

This document provides a comprehensive overview of all test cases written for the CineVerse Android application across the develop branch.

## Test Suite Overview

The test suite is organized into two categories:
- **Unit Tests** (`app/src/test/java`) - Tests that run on the JVM without Android dependencies
- **Instrumented Tests** (`app/src/androidTest/java`) - Tests that run on an Android device/emulator

## Unit Tests

### 1. Resource Utility Tests (`ResourceTest.kt`)
**Location:** `app/src/test/java/com/example/cineverse/util/ResourceTest.kt`

Tests for the `Resource<T>` sealed class that wraps data from the repository layer.

| Test Case | Description |
|-----------|-------------|
| `success_createsSuccessResource()` | Verifies Success resource is created correctly |
| `error_createsErrorResource()` | Verifies Error resource with throwable is created |
| `error_createsErrorWithoutThrowable()` | Verifies Error resource without throwable |
| `loading_createsLoadingResource()` | Verifies Loading resource is created |
| `multipleLoadingInstances_areSameObject()` | Verifies Loading is a singleton object |

**Coverage:** Success, Error, Loading states

---

### 2. Movie Model Tests (`MovieTest.kt`)
**Location:** `app/src/test/java/com/example/cineverse/domain/model/MovieTest.kt`

Tests for the Movie data class ensuring proper structure and equality.

| Test Case | Description |
|-----------|-------------|
| `movie_createsMovieWithAllFields()` | Verifies Movie creation with all fields |
| `movie_withNullPosterUrl()` | Verifies Movie handles null poster URL |
| `movie_equality()` | Verifies equality for identical movies |
| `movie_inequality_differentId()` | Verifies inequality when ID differs |
| `movie_inequality_differentTitle()` | Verifies inequality when title differs |
| `movie_inequality_differentRating()` | Verifies inequality when rating differs |

**Coverage:** Movie data class, field validation, equality

---

### 3. Genre Model Tests (`GenreTest.kt`)
**Location:** `app/src/test/java/com/example/cineverse/domain/model/GenreTest.kt`

Tests for the Genre data class.

| Test Case | Description |
|-----------|-------------|
| `genre_createsGenreWithAllFields()` | Verifies Genre creation |
| `genre_equality()` | Verifies equality for identical genres |
| `genre_inequality_differentId()` | Verifies inequality when ID differs |
| `genre_inequality_differentName()` | Verifies inequality when name differs |

**Coverage:** Genre data class, equality

---

### 4. HomeUiState Tests (`HomeUiStateTest.kt`)
**Location:** `app/src/test/java/com/example/cineverse/ui/home/HomeUiStateTest.kt`

Tests for the HomeUiState that represents the home screen UI state.

| Test Case | Description |
|-----------|-------------|
| `homeUiState_isEmpty_whenAllListsEmpty()` | Verifies isEmpty property when all lists are empty |
| `homeUiState_notEmpty_whenTrendingMoviesNotEmpty()` | Verifies isEmpty returns false with trending movies |
| `homeUiState_notEmpty_whenPopularMoviesNotEmpty()` | Verifies isEmpty returns false with popular movies |
| `homeUiState_notEmpty_whenTopRatedMoviesNotEmpty()` | Verifies isEmpty returns false with top rated movies |
| `homeUiState_containsAllMovieCategories()` | Verifies all movie categories are stored |
| `homeUiState_containsGenres()` | Verifies genres are stored correctly |
| `homeUiState_equality()` | Verifies equality for identical states |

**Coverage:** HomeUiState properties, isEmpty logic

---

### 5. HomeMockData Tests (`HomeMockDataTest.kt`)
**Location:** `app/src/test/java/com/example/cineverse/ui/home/HomeMockDataTest.kt`

Tests for the mock data used before real API integration.

| Test Case | Description |
|-----------|-------------|
| `homeMockData_hasTrendingMovies()` | Verifies trending movies list is not empty |
| `homeMockData_trendingMovies_count()` | Verifies correct count of trending movies (5) |
| `homeMockData_hasPopularMovies()` | Verifies popular movies list is not empty |
| `homeMockData_popularMovies_count()` | Verifies correct count of popular movies (5) |
| `homeMockData_hasTopRatedMovies()` | Verifies top rated movies list is not empty |
| `homeMockData_topRatedMovies_count()` | Verifies correct count of top rated movies (5) |
| `homeMockData_hasGenres()` | Verifies genres list is not empty |
| `homeMockData_genres_count()` | Verifies correct count of genres (8) |
| `homeMockData_allMoviesHaveValidFields()` | Verifies all movies have non-empty valid fields |
| `homeMockData_allGenresHaveValidFields()` | Verifies all genres have valid fields |
| `homeMockData_toUiState_convertsSuccessfully()` | Verifies conversion to HomeUiState |
| `homeMockData_toUiState_notEmpty()` | Verifies converted state is not empty |
| `homeMockData_trendingMovies_containsDarkKnight()` | Verifies specific movie exists with correct data |
| `homeMockData_genres_containsAllMainGenres()` | Verifies main genre types are present |
| `homeMockData_posterUrlsAreNull()` | Verifies all poster URLs are null (placeholder) |

**Coverage:** Mock data integrity, data conversion

---

### 6. HomeViewModel Tests (`HomeViewModelTest.kt`)
**Location:** `app/src/test/java/com/example/cineverse/ui/home/HomeViewModelTest.kt`

Tests for the HomeViewModel state management logic using Turbine for Flow testing.

| Test Case | Description |
|-----------|-------------|
| `viewModel_initializesWithLoading()` | Verifies ViewModel starts in Loading state |
| `viewModel_emitsSuccessState()` | Verifies Loading → Success state transition |
| `viewModel_successStateHasNonEmptyData()` | Verifies loaded state contains data |
| `viewModel_retry_reloadsData()` | Verifies retry() function reloads data |
| `viewModel_uiStateFlowIsReadOnly()` | Verifies uiState is a read-only StateFlow |
| `viewModel_loadedDataContainsMockData()` | Verifies correct mock data is loaded |

**Coverage:** ViewModel lifecycle, state management, retry logic, Flow emissions

---

### 7. Extensions Tests (`ExtensionsTest.kt`)
**Location:** `app/src/test/java/com/example/cineverse/util/ExtensionsTest.kt`

Tests for Kotlin extension functions.

| Test Case | Description |
|-----------|-------------|
| `capitalizeWords_singleWord()` | Verifies single word capitalization |
| `capitalizeWords_multipleWords()` | Verifies multiple word capitalization |
| `capitalizeWords_emptyString()` | Verifies empty string handling |
| `capitalizeWords_alreadyCapitalized()` | Verifies already capitalized string |
| `capitalizeWords_singleCharacter()` | Verifies single character capitalization |
| `capitalizeWords_withNumbers()` | Verifies string with numbers |
| `capitalizeWords_withSpecialCharacters()` | Verifies special character handling |
| `capitalizeWords_onlySpaces()` | Verifies space-only string |

**Coverage:** String extension functions

---

### 8. BaseListAdapter Tests (`BaseListAdapterTest.kt`)
**Location:** `app/src/test/java/com/example/cineverse/ui/common/adapter/BaseListAdapterTest.kt`

Tests for the generic BaseListAdapter base class.

| Test Case | Description |
|-----------|-------------|
| `adapter_initiallyEmpty()` | Verifies adapter starts empty |
| `adapter_submitList_updatesItemCount()` | Verifies submitList updates item count |
| `adapter_submitList_emptyList()` | Verifies submitList with empty list |
| `adapter_getItem_returnsCorrectItem()` | Verifies getItem returns correct item |

**Coverage:** Adapter initialization, list submission, item retrieval

---

## Instrumented Tests

Instrumented tests run on an Android device or emulator and have access to Android framework components.

### 1. MovieAdapter Tests (`MovieAdapterTest.kt`)
**Location:** `app/src/androidTest/java/com/example/cineverse/ui/common/adapter/MovieAdapterTest.kt`

Tests for the MovieAdapter that displays movies in a horizontal RecyclerView.

| Test Case | Description |
|-----------|-------------|
| `adapter_initiallyEmpty()` | Verifies adapter starts empty |
| `adapter_submitList_updatesItemCount()` | Verifies item count updates correctly |
| `adapter_submitEmptyList()` | Verifies clearing the adapter |
| `adapter_getItem_returnsCorrectMovie()` | Verifies correct movie retrieval |
| `adapter_multipleSubmissions_replacesList()` | Verifies list replacement |
| `adapter_diffCallback_detectsSameItem()` | Verifies DiffUtil same item detection |
| `adapter_diffCallback_detectsDifferentItems()` | Verifies DiffUtil different item detection |
| `adapter_handlesNullPosterUrl()` | Verifies null poster URL handling |
| `adapter_handlesValidPosterUrl()` | Verifies valid poster URL handling |

**Coverage:** MovieAdapter functionality, DiffUtil callbacks, URL handling

---

### 2. GenreAdapter Tests (`GenreAdapterTest.kt`)
**Location:** `app/src/androidTest/java/com/example/cineverse/ui/common/adapter/GenreAdapterTest.kt`

Tests for the GenreAdapter that displays genre chips.

| Test Case | Description |
|-----------|-------------|
| `adapter_initiallyEmpty()` | Verifies adapter starts empty |
| `adapter_submitList_updatesItemCount()` | Verifies item count updates |
| `adapter_submitEmptyList()` | Verifies clearing the adapter |
| `adapter_getItem_returnsCorrectGenre()` | Verifies correct genre retrieval |
| `adapter_getItem_withMultipleGenres()` | Verifies multiple genre retrieval |
| `adapter_multipleSubmissions_replacesList()` | Verifies list replacement |
| `adapter_diffCallback_detectsSameGenre()` | Verifies DiffUtil same genre detection |
| `adapter_diffCallback_detectsDifferentGenres()` | Verifies DiffUtil different genre detection |
| `adapter_maintainsGenreOrder()` | Verifies genre order is maintained |
| `adapter_handlesLargeGenreList()` | Verifies handling of large lists (100 items) |

**Coverage:** GenreAdapter functionality, order preservation, scalability

---

### 3. HomeFragment Tests (`HomeFragmentTest.kt`)
**Location:** `app/src/androidTest/java/com/example/cineverse/ui/home/HomeFragmentTest.kt`

Tests for the HomeFragment UI using Hilt for dependency injection.

| Test Case | Description |
|-----------|-------------|
| `fragment_displaysLoadingState()` | Verifies loading state UI |
| `fragment_displaysSwipeRefreshLayout()` | Verifies SwipeRefreshLayout is visible |
| `fragment_displaysSectionHeaders()` | Verifies section headers are displayed |
| `fragment_hasRecyclerViewForTrendingMovies()` | Verifies trending movies RecyclerView exists |
| `fragment_hasRecyclerViewForPopularMovies()` | Verifies popular movies RecyclerView exists |
| `fragment_hasRecyclerViewForTopRatedMovies()` | Verifies top rated movies RecyclerView exists |
| `fragment_hasRecyclerViewForGenres()` | Verifies genres RecyclerView exists |
| `fragment_displays_retryButton_onError()` | Verifies retry button on error state |
| `fragment_launches_successfully()` | Verifies fragment launch and initialization |
| `fragment_viewModel_isInitialized()` | Verifies ViewModel is initialized |

**Coverage:** Fragment UI, navigation, state rendering, Espresso assertions

---

## Test Dependencies

The following testing libraries are used:

### Unit Testing
- **JUnit 4** - Test framework
- **Mockito + Mockito-Kotlin** - Mocking framework
- **Google Truth** - Fluent assertions
- **Kotlin Coroutines Test** - Coroutine testing utilities
- **Turbine** - Flow testing library
- **Lifecycle Runtime Testing** - ViewModel testing utilities

### Instrumented Testing
- **Androidx Test** - Android testing framework
- **Espresso** - UI testing framework
- **Hilt Testing** - Dependency injection testing
- **Mockito Android** - Android-specific mocking

## Running the Tests

### Run All Tests
```bash
./gradlew test                    # Unit tests only
./gradlew connectedAndroidTest    # Instrumented tests only
./gradlew testDebug               # All tests
```

### Run Specific Test Class
```bash
./gradlew test --tests com.example.cineverse.ui.home.HomeViewModelTest
./gradlew connectedAndroidTest --tests com.example.cineverse.ui.home.HomeFragmentTest
```

### Run with Coverage
```bash
./gradlew testDebugCoverage
```

## Test Coverage Summary

| Component | Type | Tests | Status |
|-----------|------|-------|--------|
| Resource | Unit | 5 | ✓ Complete |
| Movie Model | Unit | 6 | ✓ Complete |
| Genre Model | Unit | 4 | ✓ Complete |
| HomeUiState | Unit | 7 | ✓ Complete |
| HomeMockData | Unit | 15 | ✓ Complete |
| HomeViewModel | Unit | 6 | ✓ Complete |
| Extensions | Unit | 8 | ✓ Complete |
| BaseListAdapter | Unit | 4 | ✓ Complete |
| MovieAdapter | Instrumented | 9 | ✓ Complete |
| GenreAdapter | Instrumented | 10 | ✓ Complete |
| HomeFragment | Instrumented | 10 | ✓ Complete |

**Total Test Cases: 84**

## Test Coverage by Feature

### Home Feed Feature
- HomeViewModel: 6 tests
- HomeUiState: 7 tests
- HomeMockData: 15 tests
- HomeFragment: 10 tests
- MovieAdapter: 9 tests
- GenreAdapter: 10 tests

### Data Models
- Movie: 6 tests
- Genre: 4 tests

### Utilities
- Resource: 5 tests
- Extensions: 8 tests
- BaseListAdapter: 4 tests

## CI/CD Integration

These tests are designed to run in CI/CD pipelines:
- Unit tests run on JVM (fast, no emulator needed)
- Instrumented tests require Android emulator/device
- Tests use Hilt for dependency injection
- All tests include assertions using Google Truth for clarity

## Future Test Additions

Recommended areas for future test additions:
1. Repository layer tests (when API integration lands)
2. Mapper tests (data → domain model conversion)
3. Use case tests
4. Integration tests for full feature flows
5. UI screenshot tests for visual regression
6. Performance tests for large data sets
7. Database tests for Room DAO operations
8. API interceptor tests

## Notes

- All tests follow AAA pattern (Arrange, Act, Assert)
- Tests use descriptive naming following: `component_scenario_expectedOutcome()`
- HomeMockData-driven tests ensure UI works with actual data before API integration
- Fragment tests use Hilt for proper dependency injection in Android context
