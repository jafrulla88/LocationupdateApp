package com.example.interviewtask.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import java.util.*

@RealmClass
open class RegisterModel:RealmModel {

    @PrimaryKey
    var id:String = ""
 /*   @Required
    var rowID:Long=0*/
    @Required
    var firstname:String=""
    @Required
    var lastname:String=""
    @Required
    var password:String=""
    @Required
    var email:String=""
    @Required
    var Phone:String = ""

    var LoginFlag:Int = 0

}