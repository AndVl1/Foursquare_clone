package com.example.foursquareclone

import android.app.Application
import com.parse.Parse

class ParseHelper : Application() {
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(Parse.Configuration.Builder(this)
            .applicationId("58c2df0c516aa1967bf066b8851bc9925a527c7e")
            .clientKey("384baad1a7c0ed5861b4fca791dcfa9b123dacbf")
            .server("http://52.14.194.168:80/parse")
            .build())
    }
}