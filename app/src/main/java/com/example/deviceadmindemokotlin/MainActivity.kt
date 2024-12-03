package com.example.deviceadmindemokotlin


import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
class MainActivity : Activity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(this, DeviceAdminReceiver::class.java)

        val btnEnableAdmin = findViewById<Button>(R.id.btn_enable_admin)
        val btnLockDevice = findViewById<Button>(R.id.btn_lock_device)
        val btnWipeData = findViewById<Button>(R.id.btn_wipe_data)

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
    }
}