package com.myprog.sportislife

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.myprog.sportislife.other.Constants

class WelcomeActivity: AppCompatActivity() {

    lateinit var buttonPermission: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startNewActivity();
        }

        buttonPermission = findViewById(R.id.welcome_activity_permission)
        buttonPermission.setOnClickListener {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.FINE_LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun startNewActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            Constants.FINE_LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startNewActivity()
                } else {
                    Toast.makeText(this, "Вы не дали разрешения, повторите попытку", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}