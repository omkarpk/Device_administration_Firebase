package com.example.deviceadmindemokotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : Activity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName
    private lateinit var firebaseDatabase: FirebaseDatabase // Declare late initialization

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContentView(R.layout.activity_main)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(this, DeviceAdminReceiver::class.java)

        val btnEnableAdmin = findViewById<Button>(R.id.btn_enable_admin)
        val btnLockDevice = findViewById<Button>(R.id.btn_lock_device)
        val btnWipeData = findViewById<Button>(R.id.btn_wipe_data)
        val btnEnableKioskMode = findViewById<Button>(R.id.btn_enable_kiosk_mode)
        val btnDisableKioskMode = findViewById<Button>(R.id.btn_disable_kiosk_mode)

        btnEnableAdmin.setOnClickListener {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable admin to access device features.")
            startActivity(intent)
        }

        btnLockDevice.setOnClickListener {
            if (devicePolicyManager.isAdminActive(componentName)) {
                devicePolicyManager.lockNow()
                Toast.makeText(this, "Device Locked", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Admin privileges required!", Toast.LENGTH_SHORT).show()
            }
        }

        btnWipeData.setOnClickListener {
            if (devicePolicyManager.isAdminActive(componentName)) {
                devicePolicyManager.wipeData(0)
            } else {
                Toast.makeText(this, "Admin privileges required!", Toast.LENGTH_SHORT).show()
            }
        }

        btnEnableKioskMode.setOnClickListener {
            toggleKioskMode(true)
        }

        btnDisableKioskMode.setOnClickListener {
            toggleKioskMode(false)

        }

        observeKioskMode()
    }

    private fun toggleKioskMode(enable: Boolean) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            if (enable) {
                startLockTask()
                Toast.makeText(this, "Kiosk Mode Enabled", Toast.LENGTH_SHORT).show()
            } else {
                stopLockTask()
                Toast.makeText(this, "Kiosk Mode Disabled", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Admin privileges required!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeKioskMode() {
        val kioskModeRef = firebaseDatabase.getReference("kiosk_mode")

        kioskModeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isKioskModeEnabled = snapshot.getValue(Boolean::class.java) ?: false
                toggleKioskMode(isKioskModeEnabled)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to read from Firebase", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
