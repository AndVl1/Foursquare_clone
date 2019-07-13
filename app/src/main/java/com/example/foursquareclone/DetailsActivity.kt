package com.example.foursquareclone

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap : GoogleMap
    var chosenName : String? = ""

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!

        val query = ParseQuery<ParseObject>("Locations")
        query.whereEqualTo("name", chosenName)
        query.findInBackground { objects, e ->
            if (e != null){
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (objects.isNotEmpty()){
                    for (parseObj in objects){
                        val image = parseObj["image"] as ParseFile
                        image.getDataInBackground { data, e2 ->
                            if (e2 !== null) {
                                Toast.makeText(applicationContext, e2.localizedMessage, Toast.LENGTH_SHORT).show()
                            } else {
                                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                                imageView.setImageBitmap(bitmap)
                                val name = parseObj["name"] as String
                                val type = parseObj["type"] as String
                                val atm = parseObj["atmosphere"] as String
                                val lat = parseObj["latitude"] as Double
                                val long = parseObj["longitude"] as Double
                                val location = LatLng(lat, long)

                                nameView.text = name
                                typeView.text = type
                                atmView.text = atm
                                mMap.addMarker(MarkerOptions().position(location).title(name))
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val intent = intent
        chosenName = intent.getStringExtra("name")
        nameView.text = chosenName

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
}
