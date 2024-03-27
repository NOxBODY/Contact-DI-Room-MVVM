This is a sample Contact application for demonstrating examples of -
* MVVM project architecture
* Jetpack Compose declarative UI
* Android Room local database
* Dagger-Hilt dependency injection
* SavedStateHandle process death survival

Initially, the database used a single table to store a name and a corresponding phone number.

In a later commit, manual migration for Room Database has been used to update schema for adding the multiple phone number feature. It helped the devices running the single phone number version with some data migrate seamlessly to the new version without losing any data.
