package com.example.interviewtask.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewtask.R
import com.example.interviewtask.model.UserViewModel
import com.example.interviewtask.model.RegisterModel
import com.example.interviewtask.view.Home
import com.example.interviewtask.view.Login
import io.realm.Realm

class SampleAdapter constructor(val context: Context,
                               private var datalistnew:List<RegisterModel>) :

    RecyclerView.Adapter<SampleAdapter.MyViewHolder>(){

    private val PREFS_NAME = "LoginPrefs"
    var userid:String?=""
    var viewModel = UserViewModel()
    var registermodel= RegisterModel()

    constructor(it:List<RegisterModel>?, context: Context) : this(context,datalistnew= it!!)


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var username: TextView = view.findViewById(R.id.textusername)
        var textsignout: TextView = view.findViewById(R.id.textSignout)
        var buttonSignin:Button=view.findViewById(R.id.buttonSignin)
     }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.switchdialog, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val datalist= datalistnew!!.get(position)
        datalist.email
        holder.username.text=datalist.email
        val sharedPref =context.getSharedPreferences(PREFS_NAME, 0)
        userid = sharedPref.getString("Userid", "Default") ?: "Not Set"
        Log.e("home_userid", userid!!)

        val realm: Realm
        realm = Realm.getDefaultInstance()
        registermodel= realm.where(RegisterModel::class.java).equalTo("id",userid).findFirst()!!

           if (datalist.LoginFlag==0) {

                holder.textsignout.visibility = View.GONE
                holder.buttonSignin.visibility = View.VISIBLE
           }  else {
                holder.textsignout.visibility = View.VISIBLE
                holder.buttonSignin.visibility = View.GONE
            }


            holder.buttonSignin.setOnClickListener{
                val settings = context.getSharedPreferences(PREFS_NAME, 0)
                val editor = settings.edit()
                editor.clear()
                editor.commit()
                val intent=Intent(context, Login::class.java)
                context.startActivity(intent)
            }


            holder.textsignout.setOnClickListener {
                holder.buttonSignin.visibility=View.VISIBLE
                holder.textsignout.visibility=View.GONE
                viewModel.updateNote(datalist.id,0)
                val settings = context.getSharedPreferences(PREFS_NAME, 0)
                val editor = settings.edit()
                editor.clear()
                editor.commit()
                val intent = Intent(context, Login::class.java)

                context.startActivity(intent)
                Log.e("sampleadpater", "clicked")

            }

        holder.username.setOnClickListener{

            if (datalist.LoginFlag==0) {
                val settings = context.getSharedPreferences(PREFS_NAME, 0)
                val editor = settings.edit()
                editor.clear()
                editor.commit()
                val intent=Intent(context, Login::class.java)
                context.startActivity(intent)
            }
            else {

                val settings =context.getSharedPreferences(PREFS_NAME, 0)
                val editor = settings.edit()
                editor.putString("logged", "logged")
                editor.putString("Userid", datalist.id)
                editor.putString("Username", datalist.firstname)
                editor.commit()

                val intent = Intent(context, Home::class.java)
                context.startActivity(intent)

            }



        }
     }
     override fun getItemCount(): Int {
        return datalistnew!!.size
    }
}