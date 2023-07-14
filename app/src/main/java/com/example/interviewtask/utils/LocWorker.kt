package com.example.interviewtask.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.interviewtask.R
import com.example.interviewtask.model.LocationViewModel
import com.example.interviewtask.view.Home
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class LocWorker(context: Context, workerParams: WorkerParameters) :
	Worker(context, workerParams) {
	/**
	 * The current location.
	 */
	private var mLocation: Location? = null

	/**
	 * Provides access to the Fused Location Provider API.
	 */
	private var mFusedLocationClient: FusedLocationProviderClient? = null
	lateinit var locationRequest: LocationRequest
	private val mContext: Context
	lateinit var locationviewmodel: LocationViewModel
	var userid: String?=null
	private val PREFS_NAME = "LoginPrefs"
 	var latitude:String?=null
	var longitude:String?=null

	/**
	 * Callback for changes in location.
	 */
	private var mLocationCallback: LocationCallback? = null
	@SuppressLint("MissingPermission")
	override fun doWork(): Result {
		val context = applicationContext



		val dateFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
		val c: Calendar = Calendar.getInstance()
		val date: Date = c.getTime()
		val formattedDate: String = dateFormat.format(date)
		try {

			mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
 			mLocationCallback = object : LocationCallback() {
				override fun onLocationResult(locationResult: LocationResult) {
					super.onLocationResult(locationResult)
					Log.e(
						"MYLocation",
						"Lat is: " + locationResult.lastLocation!!.latitude + "," + "Long is: " + locationResult.lastLocation!!
							.longitude
					)
					latitude = locationResult.lastLocation!!.latitude.toString()
					longitude = locationResult.lastLocation!!.longitude.toString()
					val sharedPref = mContext.getSharedPreferences(PREFS_NAME, 0) ?: return
					userid = sharedPref.getString("Userid", "Default") ?: "Not Set"

					val currentTime: Date = Calendar.getInstance().getTime()
					var locationviewmodel: LocationViewModel? = null
					locationviewmodel = LocationViewModel(context as Application)
					locationviewmodel!!.addRegLocation(
						locationResult.lastLocation!!.latitude.toString(),
						locationResult.lastLocation!!
							.longitude.toString()!!,
						currentTime.toString(),
						userid!!
					)

					Log.e("insideWorker", "Location saved")
				}

			}
			requestLoc()
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				val name: CharSequence = "Interviewtask"
				val description: String = "Interviewtask"
				val importance = NotificationManager.IMPORTANCE_DEFAULT
				val channel = NotificationChannel(
					"Interviewtask",
					name,
					importance
				)
				channel.description = description
				// Register the channel with the system; you can't change the importance
				// or other notification behaviors after this
				val notificationManager: NotificationManager =
					mContext.getSystemService(
						NotificationManager::class.java
					)




				notificationManager.createNotificationChannel(channel)
			}
			val builder: NotificationCompat.Builder =
				NotificationCompat.Builder(
					mContext,
					"Interviewtask"
				)
					.setSmallIcon(R.drawable.ic_launcher_background)
					.setContentTitle("New Location Update")

			var contentIntent:PendingIntent?=null
			var pendingIntent:PendingIntent?=null
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

				pendingIntent = PendingIntent.getActivity(
					context,
					0,
					Intent(context, Home::class.java), PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
				)
				NotificationManagerCompat.from(context).cancel(1001)

		} else {

				contentIntent= PendingIntent.getActivity(
					context, 0,
					Intent(context, Home::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

				NotificationManagerCompat.from(context).cancel(1001)
			}
 			builder.setContentIntent(contentIntent)
			builder.setContentIntent(pendingIntent)
			builder.setAutoCancel(true)


					.setPriority(NotificationCompat.PRIORITY_DEFAULT)

			val notificationManager = NotificationManagerCompat.from(mContext)


			// notificationId is a unique int for each notification that you must define
			notificationManager.notify(1001, builder.build())
			//	mFusedLocationClient!!.removeLocationUpdates(mLocationCallback as LocationCallback)

		} catch (ignored: ParseException) {
		}
		return Result.success()
	}

	init {
		mContext = context
	}
	@SuppressLint("MissingPermission")
	private fun requestLoc() {
		val locationRequest = com.google.android.gms.location.LocationRequest()
		//locationRequest.interval = 10000
		locationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
		mFusedLocationClient!!.requestLocationUpdates(
			locationRequest,
			mLocationCallback!!, Looper.getMainLooper()
		)


	}

}