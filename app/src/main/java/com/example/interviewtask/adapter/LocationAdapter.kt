package com.example.interviewtask.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewtask.R
import com.example.interviewtask.view.InfoCustomActivity
import com.example.interviewtask.model.LocationModel

class LocationAdapter constructor(val context: Context,
                                  private var datalistnew:List<LocationModel>) :


    RecyclerView.Adapter<LocationAdapter.MyViewHolder>(){


    constructor(it:List<LocationModel>?, context: Context) : this(context,datalistnew= it!!)


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textlatitude: TextView = view.findViewById(R.id.textLatitude)
        var textLongitude: TextView = view.findViewById(R.id.textLongitude)
        var texttime: TextView =view.findViewById(R.id.textTime)
        var mainlayout:ConstraintLayout=view.findViewById(R.id.mainlayout)
    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.locationitem, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val datalist= datalistnew!!.get(position)


        holder.textlatitude.text=datalist.latitude
        holder.textLongitude.text=datalist.longitude
        holder.texttime.text=datalist.time


        holder.mainlayout.setOnClickListener{

            val intent=Intent(context, InfoCustomActivity::class.java)
            intent.putExtra("Latitude",datalist.latitude)
            intent.putExtra("Longitude",datalist.longitude)
            context.startActivity(intent)

        }

    }


    override fun getItemCount(): Int {
        return datalistnew!!.size
    }
}