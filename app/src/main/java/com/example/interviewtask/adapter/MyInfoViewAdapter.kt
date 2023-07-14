package com.example.interviewtask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.interviewtask.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MyInfoViewAdapter(context: Context) :
    GoogleMap.InfoWindowAdapter {
    var context: Context
    init {
        this.context = context

    }

    override fun getInfoContents(marker: Marker): View? {
        val inflater = LayoutInflater.from(context)
        val v: View = inflater.inflate(R.layout.custom_info_contents, null)
        var position:TextView=v.findViewById(R.id.position)
        var title:TextView=v.findViewById(R.id.title)
        var snippet:TextView=v.findViewById(R.id.snippet)
        snippet.text=marker.snippet
        title.text=marker.title
        position.text=marker.position.toString()

        return v
    }

    override fun getInfoWindow(marker: Marker): View? {

        return null
    }


}
