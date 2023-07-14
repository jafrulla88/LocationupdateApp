package com.example.interviewtask.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.realm.Realm
import java.util.*

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private var realm: Realm = Realm.getDefaultInstance()
    var number:Number?=null
    private val PREFS_NAME = "LoginPrefs"
    var userid: String?=null
    private val context = getApplication<Application>().applicationContext

    val locationlist:MutableLiveData<List<LocationModel>>
        get() = getLocationnew()


    fun addRegLocation(latitude: String, longitude: String,time:String,userid:String) {
        realm.executeTransaction { r: Realm ->
            val loction = r.createObject(LocationModel::class.java, UUID.randomUUID().toString())


            loction.latitude = latitude
            loction.longitude = longitude
            loction.time=time
            loction.userid=userid

            realm.insertOrUpdate(loction)
        }
    }

    private fun getLocation(): MutableLiveData<List<LocationModel>> {
        val list = MutableLiveData<List<LocationModel>>()
        val notes = realm.where(LocationModel::class.java).findAll()
        list.value = notes?.subList(0, notes.size)
        return list
    }

    private fun getLocationnew(): MutableLiveData<List<LocationModel>> {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, 0)
        userid = sharedPref.getString("Userid", "Default") ?: "Not Set"
        val list = MutableLiveData<List<LocationModel>>()
        val notes = realm.where(LocationModel::class.java).equalTo("userid",userid).findAll()
        list.value = notes?.subList(0, notes.size)
        return list
    }

    fun updateLocation(id: String, loginflag: Int,) {
       /* val locationmodel = realm.where(LocationModel::class.java)
            .equalTo("id", id)
            .findFirst()

        realm.executeTransaction {
            locationmodel!!.LoginFlag = loginflag
            realm.insertOrUpdate(LocationModel)*/
        }
    }



