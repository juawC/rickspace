package com.app.juawcevada.rickspace.di

import android.content.Context
import androidx.room.Room
import com.app.juawcevada.rickspace.data.character.CharacterDao
import com.app.juawcevada.rickspace.data.shared.local.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * AppDatabase module
 */
@Module
class DBModule {

    @Singleton
    @Provides
    internal fun provideDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, "the_list")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Singleton
    @Provides
    internal fun provideProprietyDao(database: AppDatabase): CharacterDao {
        return database.characterDao()
    }
}