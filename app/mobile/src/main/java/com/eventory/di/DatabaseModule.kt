package com.eventory.di

import android.content.Context
import androidx.room.Room
import com.eventory.data.local.database.EventDao
import com.eventory.data.local.database.EventoryDatabase
import com.eventory.data.local.database.RsvpDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EventoryDatabase {
        return Room.databaseBuilder(
            context,
            EventoryDatabase::class.java,
            "eventory_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideEventDao(database: EventoryDatabase): EventDao {
        return database.eventDao()
    }

    @Provides
    @Singleton
    fun provideRsvpDao(database: EventoryDatabase): RsvpDao {
        return database.rsvpDao()
    }
}
