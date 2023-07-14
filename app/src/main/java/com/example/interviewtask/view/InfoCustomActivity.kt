package com.example.interviewtask.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.interviewtask.utils.LatLngInterpolator.Spherical
import com.example.interviewtask.model.LocationViewModel
import com.example.interviewtask.utils.MarkerAnimation.animateMarkerToICS
import com.example.interviewtask.adapter.MyInfoViewAdapter
import com.example.interviewtask.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


class InfoCustomActivity :FragmentActivity() ,
    GoogleMap.OnInfoWindowClickListener,GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    private var markerPerth: Marker? = null
    private var markerSydney: Marker? = null
    private var markerBrisbane: Marker? = null
    lateinit var locationviewmodel: LocationViewModel

    var ourGlobalMarker: Marker? = null

      var markerHome: Marker? = null
    var markerOptions=MarkerOptions()

    private val PERTH = LatLng(-31.952854, 115.857342)
    private val SYDNEY = LatLng(-33.87365, 151.20689)
    private val BRISBANE = LatLng(-27.47093, 153.0235)

    var Latitude:String?=null
    var Longitude:String?=null
    var buttonplay:Button?=null
    var context:Context?=null

      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mapactivity)

        buttonplay=findViewById<View>(R.id.buttonplay)as Button
        val mapFragment = supportFragmentManager.findFragmentById(R.id.location_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        Latitude = intent.getStringExtra("Latitude").toString()
        Longitude = intent.getStringExtra("Longitude").toString()

        Log.e("Latitude", Latitude!!)
        Log.e("Longitude", Longitude!!)

        buttonplay!!.setOnClickListener{

            locationviewmodel = ViewModelProvider(this).get(LocationViewModel::class.java)
            locationviewmodel.locationlist.observe(this, {
                var latitude:String?=null
                var longitude:String?=null
                for (i in 0.. it.size-1) {


                    latitude=it.get(i).latitude
                    longitude=it.get(i).longitude
                    Log.e("latitude",latitude)
                    Log.e("longitude",longitude)
                    updateMarkerPosition(latitude,longitude,mMap)
                }


            })



        }

    }


    override fun onMapReady(gmMap: GoogleMap) {
        mMap=gmMap
        val home=LatLng(Latitude!!.toDouble(),Longitude!!.toDouble())
        val currentTime: Date = Calendar.getInstance().getTime()
        markerHome = mMap.addMarker(
            markerOptions
                .position(home)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            .title("Home")
                .snippet(currentTime.toString()) )

        mMap.moveCamera(CameraUpdateFactory.newLatLng(home))
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                home, 12.0f
            )
        )
        val adapter = MyInfoViewAdapter(this)
        mMap.setInfoWindowAdapter(adapter)
        mMap.addMarker(markerOptions)!!.showInfoWindow()

        mMap.setOnInfoWindowClickListener(this)
        mMap.setOnMarkerClickListener (this)



     }


    override fun onInfoWindowClick(marker: Marker) {

        var myinfoviewadater= MyInfoViewAdapter(this)
        myinfoviewadater.getInfoWindow(marker)
       // myinfoviewadater
        Toast.makeText(
            this, "Info window clicked",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateMarkerPosition(latitude:String,longitude:String,mMap:GoogleMap) {
        val home=LatLng(latitude!!.toDouble(),longitude!!.toDouble())
        val newLatLng = home
        if (ourGlobalMarker == null) { // First time adding marker to map
            ourGlobalMarker = mMap.addMarker(MarkerOptions().position(newLatLng))
        } else {
            animateMarkerToICS(ourGlobalMarker!!, newLatLng, Spherical())
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        Log.e("MArker","clicker")

        val adapter = MyInfoViewAdapter(this)
        mMap.setInfoWindowAdapter(adapter)
        mMap.addMarker(markerOptions)!!.showInfoWindow()

        return true
    }

}