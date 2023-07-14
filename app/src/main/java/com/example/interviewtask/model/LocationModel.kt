package com.example.interviewtask.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
@RealmClass
open class LocationModel : RealmModel {

    @PrimaryKey
    var id:String = ""
    @Required
    var latitude:String=""
    @Required
    var longitude:String=""
    @Required
    var time:String=""
    @Required
    var userid:String=""
}