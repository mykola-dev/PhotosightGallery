package ds.photosight.shared.usecase

actual class CheckVersionUseCase {
    actual fun shouldShowAboutDialog(): Boolean {
        // Desktop version - always return false
        return false
    }
}
