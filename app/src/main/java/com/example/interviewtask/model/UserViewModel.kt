package com.example.interviewtask.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import java.util.*

class UserViewModel:ViewModel() {

    private var realm: Realm = Realm.getDefaultInstance()
    var number:Number?=null

    val registerlist:MutableLiveData<List<RegisterModel>>
        get() = getRegister()

    fun addReg(Fname: String, Lname: String,Password:String,Email:String,Mobile:String) {
        realm.executeTransaction { r: Realm ->
            val register = r.createObject(RegisterModel::class.java, UUID.randomUUID().toString())
             register.firstname = Fname
            register.lastname = Lname
            register.password=Password
            register.email=Email
            register.Phone=Mobile

            realm.insertOrUpdate(register)
        }
    }

    private fun getRegister(): MutableLiveData<List<RegisterModel>> {
        val list = MutableLiveData<List<RegisterModel>>()
        val notes = realm.where(RegisterModel::class.java).findAll()
        list.value = notes?.subList(0, notes.size)
        return list
    }

    fun updateNote(id: String, loginflag: Int) {
        val registerModel = realm.where(RegisterModel::class.java)
            .equalTo("id", id)
            .findFirst()

        realm.executeTransaction {
            registerModel!!.LoginFlag = loginflag
             realm.insertOrUpdate(registerModel)
        }
    }

}

