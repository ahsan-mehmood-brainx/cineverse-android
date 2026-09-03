# CineVerse Testing Guide

Quick reference for running and understanding the test suite.

## Test Files Created

### Unit Tests (JVM - no Android emulator needed)
- `ResourceTest.kt` - 5 tests for Resource sealed class
- `MovieTest.kt` - 6 tests for Movie data model
- `GenreTest.kt` - 4 tests for Genre data model
- `HomeUiStateTest.kt` - 7 tests for HomeUiState
- `HomeMockDataTest.kt` - 15 tests for mock data validation
- `HomeViewModelTest.kt` - 6 tests for ViewModel state management
- `ExtensionsTest.kt` - 8 tests for Kotlin extensions
- `BaseListAdapterTest.kt` - 4 tests for base adapter

**Total Unit Tests: 55**

### Instrumented Tests (Requires Android emulator/device)
- `MovieAdapterTest.kt` - 9 tests for MovieAdapter
- `GenreAdapterTest.kt` - 10 tests for GenreAdapter
- `HomeFragmentTest.kt` - 10 tests for HomeFragment UI

**Total Instrumented Tests: 29**

**Grand Total: 84 test cases**

## Quick Start

### Run All Tests
```bash
cd /Users/brainxiosdev/Desktop/Projects/Android/CineVerse
./gradlew test                    # Unit tests (fast, no emulator)
./gradlew connectedAndroidTest    # Instrumented tests (requires emulator)
./gradlew build                   # Build and run all tests
```

### Run Specific Test Files
```bash
# Run a single unit test file
./gradlew test --tests com.example.cineverse.ui.home.HomeViewModelTest

# Run a single instrumented test file
./gradlew connectedAndroidTest --tests com.example.cineverse.ui.home.HomeFragmentTest

# Run all tests in a package
./gradlew test --tests com.example.cineverse.util.*
```

### Run with Output
```bash
# Show test results in console
./gradlew test --info

# Run tests and show coverage
./gradlew testDebugCoverage
```

## Test Organization

```
app/src/
├── test/                          # Unit Tests (JVM)
│   └── java/com/example/cineverse/
│       ├── domain/model/
│       │   ├── MovieTest.kt
│       │   └── GenreTest.kt
│       ├── ui/
│       │   ├── common/adapter/
│       │   │   └── BaseListAdapterTest.kt
│       │   └── home/
│       │       ├── HomeViewModelTest.kt
│       │       ├── HomeUiStateTest.kt
│       │       └── HomeMockDataTest.kt
│       └── util/
│           ├── ResourceTest.kt
│           └── ExtensionsTest.kt
└── androidTest/                   # Instrumented Tests
    └── java/com/example/cineverse/
        └── ui/
            ├── common/adapter/
            │   ├── MovieAdapterTest.kt
            │   └── GenreAdapterTest.kt
            └── home/
                └── HomeFragmentTest.kt
```

## Features Tested

### Home Feed Feature
- ✅ ViewModel initialization and state management
- ✅ Mock data loading and conversion
- ✅ UI state rendering (empty, loading, success)
- ✅ Fragment lifecycle and view setup
- ✅ Movie list adapter functionality
- ✅ Genre chip list adapter functionality
- ✅ Retry functionality

### Data Models
- ✅ Movie model creation and equality
- ✅ Genre model creation and equality
- ✅ HomeUiState properties and isEmpty logic

### Utilities
- ✅ Resource sealed class (Loading, Success, Error states)
- ✅ String extension functions (capitalizeWords)
- ✅ BaseListAdapter generic implementation

## Test Coverage Breakdown

| Component | Covered | Tests |
|-----------|---------|-------|
| UI Models | Yes | 13 |
| ViewModels | Yes | 6 |
| Adapters | Yes | 23 |
| Fragments | Yes | 10 |
| Utilities | Yes | 13 |
| Data Models | Yes | 10 |
| Mock Data | Yes | 15 |

## Dependencies Added

The following testing libraries were added to support the test suite:

```gradle
// Unit Testing
testImplementation(libs.junit)
testImplementation(libs.mockito.core)
testImplementation(libs.mockito.kotlin)
testImplementation(libs.google.truth)
testImplementation(libs.kotlinx.coroutines.test)
testImplementation(libs.androidx.lifecycle.runtime.testing)
testImplementation(libs.turbine)

// Instrumented Testing
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
androidTestImplementation(libs.androidx.test.runner)
androidTestImplementation(libs.mockito.android)
androidTestImplementation(libs.google.truth)
androidTestImplementation(libs.hilt.testing)
```

## Writing New Tests

Follow these conventions when adding new tests:

### Unit Test Template
```kotlin
@Test
fun component_scenario_expectedOutcome() {
    // Arrange - Set up test data
    
    // Act - Execute the code under test
    
    // Assert - Verify the results
    assertThat(result).isEqualTo(expected)
}
```

### Test Naming Pattern
- Format: `component_scenario_expectedOutcome()`
- Examples:
  - `viewModel_initializesWithLoading()`
  - `adapter_submitList_updatesItemCount()`
  - `fragment_displaysLoadingState()`

### Assertions
Use Google Truth for readable assertions:
```kotlin
assertThat(value).isEqualTo(expected)
assertThat(list).hasSize(5)
assertThat(string).contains("text")
assertThat(object).isInstanceOf(ClassName::class.java)
```

## CI/CD Integration

These tests are designed to run in CI/CD pipelines:

### GitHub Actions
```yaml
- name: Run Unit Tests
  run: ./gradlew test

- name: Run Instrumented Tests
  run: ./gradlew connectedAndroidTest
```

### Jenkins/GitLab CI
```bash
./gradlew testDebugUnitTest              # Unit tests
./gradlew connectedAndroidTestDebug      # Instrumented tests
```

## Troubleshooting

### Test Compilation Issues
```bash
# Clean build
./gradlew clean build

# Verify dependencies
./gradlew dependencyInsight --dependency mockito-core
```

### Emulator Issues for Instrumented Tests
```bash
# List available emulators
emulator -list-avds

# Start emulator
emulator -avd emulator_name

# Check emulator status
adb devices
```

### Import Issues in IDE
```bash
# Sync Gradle files
./gradlew --sync

# Rebuild project
./gradlew clean build
```

## Performance Notes

- Unit tests typically run in <30 seconds
- Instrumented tests take 2-5 minutes depending on device/emulator
- Tests use mock data (HomeMockData) for speed
- No network calls in these tests (API integration pending)

## Next Steps

Recommended areas for expanding the test suite:

1. **Repository/DataSource Tests**
   - TMDB API mock tests
   - Database DAO tests
   - Mapper tests (DTO → domain model)

2. **UseCase/Domain Logic Tests**
   - Business logic validation
   - Error handling scenarios

3. **Navigation Tests**
   - Fragment navigation paths
   - Deep linking scenarios

4. **Integration Tests**
   - Full feature flow testing
   - API integration scenarios

5. **UI/Screenshot Tests**
   - Visual regression testing
   - Material Design compliance

## Resources

- [Android Testing Guide](https://developer.android.com/training/testing)
- [Espresso Documentation](https://developer.android.com/training/testing/espresso)
- [Hilt Testing Guide](https://developer.android.com/training/dependency-injection/hilt-testing)
- [Turbine (Flow Testing)](https://github.com/cashapp/turbine)
- [Google Truth Assertions](https://truth.dev/)
