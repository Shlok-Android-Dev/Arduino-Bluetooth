package com.example.arduino_bluetooth

import android.Manifest
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.arduinobluetooth.R

class DeviceListAdapter(
    context: Context,
    private val resource: Int,
    private val devices: List<BluetoothDevice>
) : ArrayAdapter<BluetoothDevice>(context, resource, devices) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(resource, parent, false)

        val device = devices[position]
        val deviceNameTextView = view.findViewById<TextView>(R.id.tvDeviceName)
        val deviceAddressTextView = view.findViewById<TextView>(R.id.tvDeviceAddress)

        // Use the adapter's context to check permissions
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions are not granted; handle appropriately (optional)
            deviceNameTextView.text = "Permission Required"
            deviceAddressTextView.text = "N/A"
            return view
        }

        deviceNameTextView.text = device.name ?: "Unknown Device"
        deviceAddressTextView.text = device.address

        return view
    }
}







// version 1
/*
package com.example.arduino_bluetooth

import android.R
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class DeviceListAdapter(
    context: Context,
    private val mViewResourceId: Int,
    private val mDevices: ArrayList<BluetoothDevice>
) :
    ArrayAdapter<BluetoothDevice?>(context, mViewResourceId, mDevices) {
    private val mLayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        convertView = mLayoutInflater.inflate(mViewResourceId, null)

        val device = mDevices[position]

        if (device != null) {
            val deviceName = convertView.findViewById<View>(R.id.tvDeviceName) as TextView
            val deviceAdress = convertView.findViewById<View>(R.id.tvDeviceAddress) as TextView

            if (deviceName != null) {
                deviceName.text = device.name
            }
            if (deviceAdress != null) {
                deviceAdress.text = device.address
            }
        }

        return convertView
    }
}*/








