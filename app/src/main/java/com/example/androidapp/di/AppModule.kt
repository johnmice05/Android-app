package com.example.androidapp.di

import android.content.Context
import androidx.room.Room
import com.example.androidapp.data.db.AppDatabase
import com.example.androidapp.data.repository.BibleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBibleRepository(
        @ApplicationContext context: Context
    ): BibleRepository {
        return BibleRepository(context)
    }
}
