# AGENTS.md - Photosight Gallery Android App

This file guides agentic coding assistants working in this Kotlin/Android multi-module project.

## Build/Test/Lint Commands

### Building
- `./gradlew build` - Build and test all modules
- `./gradlew assembleDebug` - Build debug APK
- `./gradlew assembleRelease` - Build release APK (signed)

### Testing
- `./gradlew test` - Run all unit tests
- `./gradlew app:testDebugUnitTest` - Run app module unit tests
- `./gradlew parser:test` - Run parser module unit tests
- `./gradlew :app:test --tests "ds.photosight.utils.ReflectionDelegateTest"` - Run single test class
- `./gradlew :app:test --tests "ds.photosight.utils.ReflectionDelegateTest.test read"` - Run single test method

### Linting
- `./gradlew lint` - Run lint on all variants
- `./gradlew app:lintDebug` - Run lint on debug variant
- `./gradlew app:lintFix` - Auto-fix safe lint issues

### Module Structure
- `app/` - Main Android application (UI, ViewModels, Repositories)
- `parser/` - Pure Kotlin/JVM module (web scraping, data models, HTTP client)
- `compose/` - Jetpack Compose implementation (separate branch)

## Code Style Guidelines

### Imports
- Order: AndroidX imports, third-party libraries, project imports
- Group imports alphabetically within each section
- Use wildcard imports sparingly (only for kotlinx.android.synthetic)

### Formatting
- 4-space indentation (Kotlin standard)
- No trailing whitespace
- Max line length: ~120 characters (soft limit)
- Use meaningful line breaks for long expressions

### Types
- Prefer explicit types for public APIs
- Use type inference for local variables when type is obvious
- Use data classes for models with equals/hashCode/toString
- Use sealed classes for restricted hierarchies

### Naming Conventions
- Classes: PascalCase (`GalleryViewModel`, `PhotoInfo`)
- Functions/Properties: camelCase (`onMenuSelected`, `menuStateLiveData`)
- Constants: UPPER_SNAKE_CASE (`PAGE_SIZE`)
- Private properties: camelCase (no underscore prefix)
- Test names: backticks with spaces (`test read`, `auto evaluate field name`)

### Dependency Injection
- Use Hilt/Dagger for DI
- Inject dependencies via constructor: `@Inject constructor(...)`
- Mark ViewModels with `@HiltViewModel`
- Mark Fragments/Activities with `@AndroidEntryPoint`
- Use @Module classes for providing dependencies

### ViewModels
- Extend `BaseViewModel` which provides coroutineScope, prefs, log
- Use `viewModelScope` for coroutines (auto-cancelled onCleared)
- Expose state via LiveData or Flow
- Use `LiveEvent` for one-time events (navigation, snackbar)

### Fragments/Activities
- Use `@AndroidEntryPoint` annotation
- Inject ViewModels with `by viewModels()` or `by activityViewModels()`
- Use `kotlinx.android.synthetic.*` for view binding (deprecated but in use)
- Inflate layouts in `onCreateView` with `inflater.inflate(R.layout.xxx, container, false)`

### Coroutines
- Use `viewModelScope` in ViewModels
- Use `flow` for cold streams and `LiveData` transformation for UI
- Use `retry` operator for resilient network calls
- Use `Dispatchers.IO` for network, `Dispatchers.Main` for UI

### Testing
- Use JUnit 4 with `@Test` annotations
- Use Mockk for mocking
- Use descriptive test names in backticks: `fun \`test read\`()`
- Place tests in `app/src/test/` or `parser/src/test/`
- Use `@Before` for setup

### Error Handling
- Use try-catch for expected errors
- Log errors with Timber: `log.e("error message", exception)`
- Use `.retry {}` operator for Flow error recovery
- Show error states to users via LiveData/UI updates

### Logging
- Use Timber for logging
- Inject `Timber.Tree` as `log` in ViewModels
- Levels: `v()`, `d()`, `i()`, `w()`, `e()`

### Extensions
- Create extension functions for reusable utilities
- Place in `utils/` package
- Example: `fun Context.toast(text: String, duration: Int = Toast.LENGTH_LONG)`
- Use inline functions for lambdas

### Data Models
- Use data classes in `model/` or `parser/` package
- Implement Serializable for parcelable objects
- Mark deprecated fields with `@Deprecated`
- Use value classes (inline classes) for type safety

### Navigation
- Use AndroidX Navigation Component
- Navigate with directions: `navigate(GalleryFragmentDirections.openViewer(position))`
- Use `FragmentNavigatorExtras` for shared element transitions

### Comments
- Keep code self-documenting
- Add comments only for complex logic or workarounds
- Use TODO/FIXME comments sparingly

### ProGuard
- ProGuard enabled for release builds
- Rules in `app/proguard-rules.pro`
- Keep models with `@Keep` if needed

### Build Configuration
- Kotlin 1.7.20
- Gradle 8.0
- compileSdk 32, minSdk 21, targetSdk 32
- JVM target: 1.8
- Use kapt for annotation processing (Hilt, Glide)
- Android SDK location: `C:\dev\android-sdk-windows\`
- SDK Build-Tools: 30.0.3 (auto-installed during build)

## Known Issues & Warnings

### Critical Warnings (Build Time)
- `kotlin-android-extensions` plugin is deprecated - using `kotlinx.android.synthetic.*` throughout codebase
- Unsafe compiler argument `-XXLanguage:+InlineClasses` in use (not production-ready)
- Kotlinx coroutines experimental flags not supported by current compiler version
- Multiple API deprecations:
  - `Handler()` constructor deprecated in Java
  - `setHasOptionsMenu()` deprecated
  - `systemWindowInsetTop/Bottom` deprecated
  - `SYSTEM_UI_FLAG_*` constants deprecated
- Unused parameters in: `SharedElementsHelper.kt:95`, `GalleryAdapter.kt:94`, `GalleryFragment.kt:215`, `ViewerFragment.kt:151`
- Unnecessary non-null assertions in `Histogram.kt:78`
- Unchecked casts in `LiveDataExt.kt:29,41`

### Android SDK Setup
- adb command may not be in PATH - ensure `C:\dev\android-sdk-windows\platform-tools\` is in PATH
- Use Android Studio or SDK Manager to manage SDK updates

### Migration Priorities
1. **HIGH**: Migrate from `kotlinx.android.synthetic` to View Binding
2. **MEDIUM**: Remove deprecated API calls (Handler, systemWindowInsets, UI flags)
3. **LOW**: Remove unused parameters and unnecessary assertions

## Common Patterns

### Repository Pattern
```kotlin
class PhotosightRepo @Inject constructor(
    private val httpClient: HttpClient
) {
    suspend fun getCategories(): List<PhotoCategory> = ...
}
```

### Paging
- Use Paging 3 for large datasets
- Extend `PagingDataAdapter`
- Implement `PagingSource` in repository

### LiveEvent
- Use `LiveEvent` for one-time events (snackbar, navigation)
- Standard LiveData for state (observe multiple times)

### Extensions Usage
- Use `postDelayed()` for debug delays (GlobalScope)
- Use `View.snack()` for snackbars
- Use `toggle()` extension for visibility toggles
