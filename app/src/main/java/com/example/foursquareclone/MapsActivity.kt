package com.example.foursquareclone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import java.io.ByteArrayOutputStream
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var locationManager : LocationManager? = null
    var locationListener : LocationListener? = null
    var lat : Double = 0.0
    var long : Double = 0.0
    private val clickListener = GoogleMap.OnMapLongClickListener { p0 ->
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0!!).title(globalName))

        lat = p0.latitude
        long = p0.longitude

        Toast.makeText(applicationContext, "Ready", Toast.LENGTH_LONG).show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(clickListener)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location?) {
                println("here")
                if (p0 != null) {
                    println(p0)
                    val userLocation = LatLng(p0.latitude, p0.longitude)
                    mMap.clear()
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Your location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17f))
                }
            }

            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

            override fun onProviderEnabled(p0: String?) {}

            override fun onProviderDisabled(p0: String?) {}

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }else{
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2f, locationListener!!)
            mMap.clear()
            val lastLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastUserLocation = LatLng(lastLocation!!.latitude, lastLocation.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 17f))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty()){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2f, locationListener!!)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.map_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_place){
            saveToServer()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveToServer() : Boolean {
        println("hereSave")
        val byteArrayOS =  ByteArrayOutputStream()
        globalImage!!.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOS)
        val bytes = byteArrayOS.toByteArray()

        val parseFile = ParseFile("image.png", bytes)

        val parseObj = ParseObject("Locations")
        parseObj.put("name", globalName)
        parseObj.put("type", globalType)
        parseObj.put("atmosphere", globalAtm)
        parseObj.put("latitude", lat)
        parseObj.put("longitude", long)
        parseObj.put("username", ParseUser.getCurrentUser().username.toString())
        parseObj.put("image", parseFile)

        parseObj.saveInBackground { e ->
            if (e != null) {
                e.printStackTrace()
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, LocationsActivity::class.java))
            }
        }

        return true
    }
}
