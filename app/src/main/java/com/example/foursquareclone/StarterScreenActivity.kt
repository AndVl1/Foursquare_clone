package com.example.foursquareclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.parse.*
import kotlinx.android.synthetic.main.activity_starter_screen.*

class StarterScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter_screen)

        ParseAnalytics.trackAppOpenedInBackground(intent)



    }

    fun signIn(view: View){
        ParseUser.logInInBackground (usernameText.text.toString(), pwdText.text.toString()) { _, e ->
            if (e != null) {
                e.printStackTrace()
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Welcome, " + usernameText.text.toString(), Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, LocationsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun signUp(view: View){
        val user = ParseUser()
        user.username = usernameText.text.toString()
        user.setPassword(pwdText.text.toString())
        user.signUpInBackground { e ->
            if (e != null) {
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } else {
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, LocationsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}