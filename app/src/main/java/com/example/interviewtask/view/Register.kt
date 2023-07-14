package com.example.interviewtask.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.interviewtask.model.UserViewModel
import com.example.interviewtask.R

class Register : AppCompatActivity() {

    var editFname: EditText? = null
    var editLname: EditText? = null
    var editPassword: EditText? = null
    var editEmail: EditText? =null
    var editMobile:EditText?=null
    var buttonReg:Button?=null
     var viewModel = UserViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        editFname=findViewById<View>(R.id.editFname) as EditText
        editLname=findViewById<View>(R.id.editLname) as EditText
        editPassword=findViewById<View>(R.id.editPassword) as EditText
        editEmail=findViewById<View>(R.id.editEmail) as EditText
        editMobile=findViewById<View>(R.id.editMobile) as EditText
        buttonReg=findViewById<View>(R.id.buttonReg)as Button

          buttonReg!!.setOnClickListener{

              if (editFname!!.text.toString()
                      .isEmpty() || editLname!!.text.toString().isEmpty()
                  || editPassword!!.text.toString().isEmpty()|| editEmail!!.text.toString().isEmpty()
                  || editMobile!!.text.toString().isEmpty()
              ) {
                  Toast.makeText(applicationContext,"Enter All Fileds",Toast.LENGTH_SHORT).show()
              } else {
                  viewModel!!.addReg(
                      editFname!!.text.toString(),editLname!!.text.toString(),
                      editPassword!!.text.toString(),editEmail!!.text.toString(),
                      editMobile!!.text.toString()
                  )

                  editFname!!.text.clear()
                  editLname!!.text.clear()
                  editPassword!!.text.clear()
                  editEmail!!.text.clear()
                  editMobile!!.text.clear()

                  Toast.makeText(applicationContext,"Regsitered Successfully",Toast.LENGTH_SHORT).show()
                  intent= Intent(this, Login::class.java)
                  startActivity(intent)
                  finish()

              }


          }

    }
}