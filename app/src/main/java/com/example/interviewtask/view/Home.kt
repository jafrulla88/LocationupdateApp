package com.example.interviewtask.view

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.interviewtask.*
import com.example.interviewtask.adapter.LocationAdapter
import com.example.interviewtask.adapter.SampleAdapter
import com.example.interviewtask.model.LocationModel
import com.example.interviewtask.model.LocationViewModel
import com.example.interviewtask.model.UserViewModel
import com.example.interviewtask.model.RegisterModel
import com.example.interviewtask.utils.LocWorker
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


class Home :AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks {


    var username:TextView?=null
    var user:TextView?=null
    var viewModel = UserViewModel()
     lateinit var locationviewmodel: LocationViewModel
    var locationmodel= LocationModel()
    var registermodel= RegisterModel()
    private val PREFS_NAME = "LoginPrefs"
    var userid: String?=null
    var userName: String?=null
    var locationManager: LocationManager? = null
    private val REQUEST_CODE_ASK_PERMISSIONS = 123
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val PERMISSION_REQUEST_CODE = 112
    var login_data: SharedPreferences? = null
    var recyclerView:RecyclerView?=null
    var swiferefreshlayout:SwipeRefreshLayout?=null
    var status:Boolean?=null

    lateinit var list: List<LocationModel>
    private lateinit var sampleadapter: SampleAdapter
    private lateinit var locationadapter: LocationAdapter
    val JOB_ID = 1005;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.home)
        username=findViewById<View>(R.id.textusername) as TextView
        user=findViewById<View>(R.id.textuser) as TextView
        recyclerView=findViewById<View>(R.id.recyclerview)as RecyclerView
        swiferefreshlayout=findViewById<View>(R.id.swiperefresh)as SwipeRefreshLayout


        val MyVersion = Build.VERSION.SDK_INT

        if (MyVersion >= Build.VERSION_CODES.M) {
            checkPermissionsnew()
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                workmanage()
            }

        }

        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale("112")){
                getNotificationPermission();
            }
        }

        val sharedPref = getSharedPreferences(PREFS_NAME, 0) ?: return
        userid = sharedPref.getString("Userid", "Default") ?: "Not Set"
        userName= sharedPref.getString("Username", "Default") ?: "Not Set"
        Log.e("home_userid", userid!!)
        user!!.setText(userName)


         viewModel.updateNote(userid!!,1)


        locationviewmodel = ViewModelProvider(this).get(LocationViewModel::class.java)
        locationviewmodel.locationlist.observe(this, {

            list=it
            locationadapter = LocationAdapter(this, it)
            val mLayoutManager = LinearLayoutManager(applicationContext)

            recyclerView!!.layoutManager = mLayoutManager
            recyclerView!!.itemAnimator = DefaultItemAnimator()
            val dividerItemDecoration = DividerItemDecoration(
                recyclerView!!.context,
                mLayoutManager.orientation
            )
            recyclerView!!.addItemDecoration(dividerItemDecoration)
            recyclerView!!.adapter = locationadapter
            locationadapter.notifyDataSetChanged()

        })

        swiferefreshlayout!!.setOnRefreshListener(OnRefreshListener {
            swiferefreshlayout!!.setRefreshing(false)
            locationadapter.notifyDataSetChanged()

        })


        username!!.setOnClickListener{

            var dialog:Dialog=Dialog(this)
            dialog.setContentView(R.layout.dialoglist)
            val recyclerView:RecyclerView
            val addaccount:TextView
            recyclerView=dialog.findViewById<View>(R.id.recyclerview) as RecyclerView
            addaccount=dialog.findViewById<View>(R.id.textAddacc) as TextView

            val window: Window? = dialog.window
            if (window != null) {
            val wlp: WindowManager.LayoutParams = window.getAttributes()
            wlp.gravity = Gravity.TOP or Gravity.RIGHT
                 window.setAttributes(wlp)
            }
            viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
            viewModel.registerlist.observe(this, {

                sampleadapter= SampleAdapter(this,it)
                val layoutManager = LinearLayoutManager(applicationContext)

                recyclerView.layoutManager = layoutManager
                recyclerView.itemAnimator = DefaultItemAnimator()
                val dividerItemDecoration = DividerItemDecoration(
                    recyclerView.context,
                    layoutManager.orientation
                )
                recyclerView.addItemDecoration(dividerItemDecoration)
                recyclerView.adapter = sampleadapter



            })
            addaccount.setOnClickListener{

                intent= Intent(this, Register::class.java)
                startActivity(intent)
            }

            dialog.show()

        }
    }



    protected fun checkPermissionsnew() {
        val missingPermissions: MutableList<String> = ArrayList()
        // check all required dynamic permissions
        for (permission in  REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions
                .toTypedArray()
            ActivityCompat.requestPermissions(
                this,
                permissions,
                REQUEST_CODE_ASK_PERMISSIONS
            )
        } else {
            val grantResults =
                IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(
                REQUEST_CODE_ASK_PERMISSIONS,
                REQUIRED_SDK_PERMISSIONS,
                grantResults
            )
        }
    }

    fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
        }
    }


        /*val i = Intent(applicationContext, LocationServiceNew::class.java)
        startService(i)
        buildAlertMessageNoGps()*/


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
             REQUEST_CODE_ASK_PERMISSIONS -> {
                var index = permissions.size - 1
                while (index >= 0) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(
                            this,
                            "Required permission '" + permissions[index] + "' not granted, exiting",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                        return
                    }
                    --index
                }
              // startService()
                // scheduleJob()
                // startRecivers()
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                     workmanage()
                 }
             }
            PERMISSION_REQUEST_CODE -> {

                if (grantResults.size > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // allow

                }  else {
                    //deny
                }
                return;
            }
        }

    }


    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun workmanage(){
        try {
            if (isWorkScheduled(WorkManager.getInstance().getWorkInfosByTag("Location").get())) {
                   Log.e("Home","Running")
            }
            else{
                Log.e("Home","Not Running")
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

         Gpsstatus()
        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale("112")){
                getNotificationPermission();
            }
        }


        Log.e("workmanager","inside")


        val request1: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
            LocWorker::class.java, 15, TimeUnit.MINUTES,
        )
             .addTag("Location").setInitialDelay(15,TimeUnit.MINUTES)
             .build()


         WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, request1)

        Log.e("Location work started", request1.id.toString())
    }


    private fun isWorkScheduled(workInfos: List<WorkInfo>?): Boolean {
        var running = false
        if (workInfos == null || workInfos.size == 0) return false
        for (workStatus in workInfos) {
            running =
                workStatus.state == WorkInfo.State.RUNNING
        }
        return running
    }

    private fun Gpsstatus(){

        var googleApiClient:GoogleApiClient?=null

        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()
            googleApiClient.connect()
            val locationRequest: com.google.android.gms.location.LocationRequest = com.google.android.gms.location.LocationRequest()
            com.google.android.gms.location.LocationRequest.create()
            locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
            locationRequest.setInterval(30 * 1000)
            locationRequest.setFastestInterval(5 * 1000)
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            // **************************
            builder.setAlwaysShow(true) // this is the key ingredient
            // **************************
            val result: PendingResult<LocationSettingsResult> = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback(object : ResultCallback<LocationSettingsResult?> {
                override fun onResult(result: LocationSettingsResult) {
                    val status: Status = result.status
                    val state = result
                        .locationSettingsStates
                    when (status.getStatusCode()) {
                        LocationSettingsStatusCodes.SUCCESS -> {}
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                 // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(this@Home, 1000)
                            } catch (e: SendIntentException) {
                                // Ignore the error.
                            }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                    }
                }
            })
        }
    }



    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {
     }

    override fun onConnectionFailed(p0: ConnectionResult) {
     }

    private fun storedata(list: List<LocationModel>) {

        locationadapter = LocationAdapter(this, list)
        val mLayoutManager = LinearLayoutManager(applicationContext)

        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView!!.context,
            mLayoutManager.orientation
        )
        recyclerView!!.addItemDecoration(dividerItemDecoration)
        recyclerView!!.adapter = locationadapter
        locationadapter.notifyDataSetChanged()

    }


}


