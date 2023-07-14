package com.example.interviewtask.view

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.interviewtask.model.UserViewModel
import com.example.interviewtask.R
import com.example.interviewtask.model.RegisterModel
import io.realm.Realm
import io.realm.RealmResults
import kotlin.collections.ArrayList


class Login : Activity() {

    var editEmail: EditText? = null
    var editPassword: EditText? = null
    var buttonLogin: Button? = null
    var textsignup: TextView? = null
    var viewModel = UserViewModel()
    private val PREFS_NAME = "LoginPrefs"
    var login_data: SharedPreferences? = null
    var registermodel= RegisterModel()


    private val datalist: ArrayList<RegisterModel> = ArrayList<RegisterModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)



        editEmail = findViewById<View>(R.id.editEmail) as EditText
        editPassword = findViewById<View>(R.id.editPassword) as EditText
        buttonLogin = findViewById<View>(R.id.buttonLogin) as Button
        textsignup = findViewById<View>(R.id.textsignup) as TextView

        login_data = getSharedPreferences(PREFS_NAME, 0);
        if (login_data!!.getString("logged", "") == "logged") {
            val intent = Intent(this@Login, Home::class.java)
            startActivity(intent)
            finish()
        }



        buttonLogin!!.setOnClickListener {

            if (editEmail!!.text.toString().isEmpty() || editPassword!!.text.toString().isEmpty()

            ) {
                Toast.makeText(applicationContext,"Enter All Fileds",Toast.LENGTH_SHORT).show()
            } else {
                var email: String = editEmail!!.text.toString();
                var password: String = editPassword!!.text.toString()

                val realm: Realm
                realm = Realm.getDefaultInstance()
                val realmObjects: RealmResults<RegisterModel> =
                    realm.where(RegisterModel::class.java).findAll()


                if(checkuser(email,password)){

                    Toast.makeText(applicationContext,"Success",Toast.LENGTH_SHORT).show()

                    val realm: Realm
                    realm = Realm.getDefaultInstance()
                    registermodel= realm.where(RegisterModel::class.java).equalTo("email", email).findFirst()!!


                   var Userid:String=registermodel.id
                    var Username:String=registermodel.firstname
                   Log.e("Userid",Userid)

                    val settings = getSharedPreferences(PREFS_NAME, 0)
                    val editor = settings.edit()
                    editor.putString("logged", "logged")
                    editor.putString("Userid", Userid)
                    editor.putString("Username", Username)
                    editor.commit()


                    intent=Intent(this, Home::class.java)
                    intent.putExtra("Username",Username)
                     startActivity(intent)
                     finish()

                }
                else{
                    Toast.makeText(applicationContext,"Username/Password is incorrect",Toast.LENGTH_SHORT).show()
                }


            }
        }

        textsignup!!.setOnClickListener{
            intent=Intent(this, Register::class.java)
            startActivity(intent)
        }


    }

    fun checkuser(email: String, password: String): Boolean {
        val realm: Realm
        realm = Realm.getDefaultInstance()
        val realmObjects: RealmResults<RegisterModel> =
            realm.where(RegisterModel::class.java).findAll()
        for (registermodel: RegisterModel in realmObjects) {
            if (email.equals(registermodel.email) && password.equals(registermodel.password)) {

                return true
            }
        }

        return false
    }

}

