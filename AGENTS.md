# AGENTS.md - Photosight Gallery (Kotlin Multiplatform)

Guide for agentic coding assistants working in this Kotlin Multiplatform project.

## Project Structure

- `app/` - **Android application** - Platform-specific Android code (minSdk 26, targetSdk 36, compileSdk 36)
- `shared/` - **Shared multiplatform module** - Common code with androidTarget() and jvm("desktop") targets
- `parser/` - **Pure Kotlin multiplatform** - Web scraping and data models (androidTarget + jvm)
- `gradle/libs.versions.toml` - Centralized dependency version catalog

## Build/Test/Lint Commands

### Building
```bash
./gradlew :app:assembleDebug                # Build debug APK
./gradlew :app:assembleRelease              # Build release APK
./gradlew build                             # Build all modules
./gradlew :shared:build                     # Build shared module only
```

### Testing
```bash
./gradlew test                              # Run all tests across all modules
./gradlew :parser:test                      # Run parser module tests
./gradlew :shared:test                      # Run shared module tests
./gradlew :app:testDebugUnitTest            # Run Android unit tests

# Running single tests (CRITICAL for KMP)
./gradlew :parser:testDebugUnitTest --tests "ClassName"           # Android target
./gradlew :parser:test --tests "ClassName"                        # All targets
./gradlew :parser:test --tests "ClassName.method"                 # Single method
./gradlew :shared:test --tests "ds.photosight.shared.util.HistogramUtilsTest"
```

### Linting
```bash
./gradlew :app:lintDebug                    # Run lint on Android app
./gradlew :shared:lint                      # Run lint on shared module
./gradlew :parser:lint                      # Run lint on parser module
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
- Use sealed classes for restricted hierarchies (UiState, PhotoCategory)

### Multiplatform Source Sets
```
shared/src/
  commonMain/kotlin/     # Shared business logic, UI components
  commonTest/kotlin/     # Shared tests (use kotlin.test)
  androidMain/kotlin/    # Android-specific implementations
  desktopMain/kotlin/    # Desktop JVM implementations
```

### Dependency Injection
- **Use Koin** (not Hilt/Dagger) for multiplatform DI
- Define modules in `di/` package
- Inject via constructor: `class ViewModel(private val repo: Repo)`
- Use `koinViewModel()` for ViewModels in Compose
- Use `koinInject()` for non-Android platforms

### Compose UI Patterns (Multiplatform)
- Use Compose Multiplatform (Material3) components
- State hoisting: state flows down, events flow up
- Use `remember` and `derivedStateOf` appropriately
- Use `koinViewModel()` for ViewModel access
- Place shared UI in `shared/src/commonMain/kotlin/`

### Coroutines
- Use `viewModelScope` in ViewModels (expect/actual for multiplatform)
- Use `Dispatchers.IO` for network, `Dispatchers.Main` for UI
- Collect flows with `collectAsStateWithLifecycle()` (Android) or `collectAsState()` (common)
- Handle errors with try-catch or Flow operators

### Error Handling
- Log errors with Napier: `Napier.e(exception, "message")` (multiplatform)
- For Android-only code: use Timber `Timber.e(exception, "message")`
- Show error states in UI (Snackbar, error screens)
- Use sealed classes for error states

### Logging (Multiplatform)
- **Use Napier** for common code: `Napier.d()`, `Napier.i()`, `Napier.e()`
- Use Timber for Android-specific code only
- Never log sensitive data

### Testing
- **Use kotlin.test** for common tests: `@Test`, `assertEquals()`, `runTest {}`
- Use JUnit 4 only for Android-specific tests
- Use `kotlinx.coroutines.test.runTest` for coroutine tests
- Test naming: descriptive with backticks in commonTest
- Use `@BeforeTest` for setup (common), `@Before` for Android

## Build Configuration

### Versions (managed in `gradle/libs.versions.toml`)
- Kotlin: 2.3.0
- AGP: 8.9.1
- Gradle: 8.0+
- JVM target: 17
- Compose Multiplatform: 1.11.0-alpha02

### Key Dependencies
- Compose Multiplatform: 1.11.0-alpha02
- Compose Material3: 1.4.0
- Koin: 4.1.1 (multiplatform)
- Coroutines: 1.10.2
- Ktor: 3.0.1 (multiplatform HTTP client)
- Coil 3: 3.0.0-alpha10 (multiplatform images)
- Napier: 2.7.1 (multiplatform logging)
- Paging (Cash App): 3.3.0-alpha02-0.5.1 (multiplatform)
- Navigation (JetBrains): 2.8.0-alpha10 (multiplatform)
- Ksoup: 0.2.0 (multiplatform HTML parsing)
- kotlinx-datetime: 0.6.1 (multiplatform dates)
- multiplatform-settings: 1.1.1 (key-value storage)

### Dependency Management
All versions are centralized in `gradle/libs.versions.toml`. Use version catalog aliases:
```kotlin
// Common multiplatform
implementation(libs.ktor.client.core)
implementation(libs.napier)
implementation(libs.ksoup)
implementation(libs.paging.common)
implementation(libs.multiplatform.settings)

// Android-only
implementation(libs.timber)
implementation(libs.androidx.core.ktx)
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

### ViewModel Pattern (Multiplatform)
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
                Napier.e(e, "Failed to load photos")
                _uiState.value = UiState.Error(e.message)
            }
        }
    }
}
```

## Platform Targets

### Current
- **Android** (androidTarget) - Primary platform, fully functional
- **Desktop** (jvm("desktop")) - In development

### Planned
- **iOS** (iosArm64, iosSimulatorArm64, iosX64) - Future target
- **Web/WASM** (wasmJs) - Potential future target

## Important Notes

- **Always use version catalog** - Never hardcode versions in build scripts
- **Koin for DI** - Not Hilt/Dagger (KMP compatible)
- **Napier for logging** in common code, Timber for Android-only
- **Ktor for networking** - Not OkHttp (multiplatform)
- **Coil 3** for multiplatform image loading
- **Kotlin 2.3.0 with JVM 17** - Modern toolchain
- **SDK Location**: `C:\dev\android-sdk-windows\`
- Use `@OptIn` annotations for experimental APIs (see compiler options in build.gradle.kts)

## Common Gradle Tasks

```bash
./gradlew clean                             # Clean build outputs
./gradlew dependencies                      # Show dependency tree
./gradlew :shared:dependencies              # Show shared module dependencies
./gradlew wrapper --version                 # Check Gradle version
./gradlew :app:installDebug                 # Install debug APK to device
```
