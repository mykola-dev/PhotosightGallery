# AGENTS.md - Photosight Gallery Android App

Guide for agentic coding assistants working in this Kotlin/Android project.

## Project Structure

- `app/` - **Main application** - Jetpack Compose UI (minSdk 26, targetSdk 36, compileSdk 36)
- `parser/` - Pure Kotlin/JVM module for web scraping and data models
- `gradle/libs.versions.toml` - Centralized dependency version catalog

## Build/Test/Lint Commands

### Building
```bash
./gradlew :app:assembleDebug      # Build debug APK
./gradlew :app:assembleRelease    # Build release APK
./gradlew build                   # Build all modules
```

### Testing
```bash
./gradlew test                                    # Run all tests
./gradlew :app:testDebugUnitTest                  # Run app unit tests
./gradlew :parser:test                            # Run parser tests
./gradlew :parser:test --tests "ClassName"        # Run single test class
./gradlew :parser:test --tests "ClassName.method" # Run single test method
```

### Linting
```bash
./gradlew :app:lintDebug      # Run lint on app
./gradlew :parser:lint        # Run lint on parser
```

## Code Style Guidelines

### Formatting
- 4-space indentation
- Max line length: 120 characters (soft limit)
- No trailing whitespace
- Use meaningful line breaks for long expressions

### Naming Conventions
- Classes: PascalCase (`GalleryViewModel`, `PhotoInfo`)
- Functions/Properties: camelCase (`onMenuSelected`, `isLoading`)
- Constants: UPPER_SNAKE_CASE (`PAGE_SIZE`)
- Test names: backticks with spaces (`fun \`loads photos successfully\`()`)

### Types & Classes
- Prefer explicit types for public APIs
- Use type inference for local variables when obvious
- Use data classes for models
- Use sealed classes for restricted hierarchies

### Dependency Injection
- **Use Koin** (not Hilt) for DI
- Define modules in `di/` package
- Inject via constructor: `class ViewModel(private val repo: Repo)`
- Use `koinViewModel()` for ViewModels in Compose

### Compose UI Patterns
- Use Compose Material3 components
- State hoisting: state flows down, events flow up
- Use `remember` and `derivedStateOf` appropriately
- Use `viewModel()` or `koinViewModel()` for ViewModel access

### Coroutines
- Use `viewModelScope` in ViewModels
- Use `Dispatchers.IO` for network, `Dispatchers.Main` for UI
- Collect flows with `collectAsStateWithLifecycle()`
- Handle errors with try-catch or Flow operators

### Error Handling
- Log errors with Timber: `Timber.e(exception, "message")`
- Show error states in UI (Snackbar, error screens)
- Use sealed classes for error states

### Logging
- Use Timber for all logging
- Levels: `Timber.d()`, `Timber.i()`, `Timber.e()`
- Never log sensitive data

### Testing
- Use JUnit 4 with `@Test` annotations
- Use Mockk for mocking: `mockk()`, `every {}`, `verify {}`
- Test naming: descriptive with backticks
- Use `@Before` for setup

## Build Configuration

### Versions (managed in `gradle/libs.versions.toml`)
- Kotlin: 2.3.0
- AGP: 9.0.0
- Gradle: 8.0+
- JVM target: 17

### Key Dependencies
- Jetpack Compose: 1.10.2
- Compose Material3: 1.4.0
- Koin: 4.1.1
- Coroutines: 1.10.2
- OkHttp: 5.3.2
- Coil: 2.7.0

### Dependency Management
All versions are centralized in `gradle/libs.versions.toml`. Use version catalog aliases:
```kotlin
implementation(libs.bundles.compose)
implementation(libs.androidx.core.ktx)
testImplementation(libs.junit)
```

## Architecture Patterns

### Repository Pattern
```kotlin
class PhotosightRepo(
    private val httpClient: HttpClient
) {
    suspend fun getCategories(): List<PhotoCategory> = ...
}
```

### ViewModel Pattern
```kotlin
class GalleryViewModel(
    private val repo: PhotosightRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    fun loadPhotos() {
        viewModelScope.launch {
            try {
                val photos = repo.getPhotos()
                _uiState.value = UiState.Success(photos)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message)
            }
        }
    }
}
```

## Important Notes

- **Always use version catalog** - Never hardcode versions in build scripts
- **Koin for DI** - Not Hilt/Dagger
- **Compose Material3** - Primary UI toolkit
- **Kotlin 2.3.0 with JVM 17** - Modern toolchain
- **SDK Location**: `C:\dev\android-sdk-windows\`

## Common Gradle Tasks

```bash
./gradlew clean                 # Clean build outputs
./gradlew dependencies          # Show dependency tree
./gradlew :app:dependencies     # Show app module dependencies
./gradlew wrapper --version     # Check Gradle version
```
