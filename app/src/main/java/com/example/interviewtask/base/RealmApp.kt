package com.example.interviewtask.base

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class RealmApp :Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val configurstion =RealmConfiguration.Builder()
            .name("Location.db")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(0)
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

        Realm.setDefaultConfiguration(configurstion)

    }
}