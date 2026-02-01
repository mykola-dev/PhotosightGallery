package ds.photosight.shared.usecase

import android.content.Context
import android.content.pm.PackageManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class AppNameUseCase : KoinComponent {
    private val packageManager: PackageManager by inject()
    private val packageName: String by inject()

    actual operator fun invoke(): String = try {
        packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0)).toString()
    } catch (e: Exception) {
        "Photosight Gallery"
    }
}
