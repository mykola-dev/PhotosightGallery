package ds.photosight.compose.di

import android.content.Context
import android.content.res.Resources
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Provides
    @Singleton
    fun timber(): Timber.Tree {
        println("timber init")
        return Timber.DebugTree()
    }

    @Provides
    @Singleton
    fun resources(@ApplicationContext context: Context): Resources = context.resources
}