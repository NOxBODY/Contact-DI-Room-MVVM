package com.vibeoncreation.contact.di

import android.app.Application
import androidx.room.Room
import com.vibeoncreation.contact.data.ContactDao
import com.vibeoncreation.contact.data.ContactDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContactDB(applicationContext: Application): ContactDatabase {
        return Room
            .databaseBuilder(
                applicationContext,
                ContactDatabase::class.java,
                "contacts.db"
            )
            .addMigrations(ContactDatabase.MIGRATION_1_2)
            .addMigrations(ContactDatabase.MIGRATION_3_4)
            .build()
    }

    @Provides
    @Singleton
    fun provideContactDao(db: ContactDatabase): ContactDao {
        return db.dao
    }
}