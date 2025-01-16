package com.example.arduino_bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.dreamcastbluethoot.R
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {



    private lateinit var enableDisableButton: Button
    private lateinit var receiveButton: Button
    private lateinit var enableDiscoverableButton: Button
    private lateinit var discoverButton: Button
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var lvNewDevices: ListView
    private lateinit var showAllDevicesSwitch: SwitchMaterial

    private val btDevices = ArrayList<BluetoothDevice>()
    private var deviceListAdapter: DeviceListAdapter? = null

    private var deviceAddress: String? = null

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
        private const val TAG = "MainActivity"
        const val EXTRA_ADDRESS = "device_address"
    }

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> Log.d(TAG, "onReceive: STATE OFF")
                    BluetoothAdapter.STATE_TURNING_OFF -> Log.d(TAG, "onReceive: STATE TURNING OFF")
                    BluetoothAdapter.STATE_ON -> Log.d(TAG, "onReceive: STATE ON")
                    BluetoothAdapter.STATE_TURNING_ON -> Log.d(TAG, "onReceive: STATE TURNING ON")
                }
            }
        }
    }

    private val discoverabilityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_SCAN_MODE_CHANGED) {
                val mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR)
                when (mode) {
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> Log.d(TAG, "Discoverability Enabled")
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> Log.d(TAG, "Discoverability Disabled. Able to receive connections")
                    BluetoothAdapter.SCAN_MODE_NONE -> Log.d(TAG, "Discoverability Disabled. Not able to receive connections")
                }
            }
        }
    }

    private val discoveryReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothDevice.ACTION_FOUND) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    if (showAllDevicesSwitch.isChecked || it.name != null) {
                        btDevices.add(it)
                        Log.d(TAG, "onReceive: ${it.name} - ${it.address}")
                        deviceListAdapter = DeviceListAdapter(context, R.layout.activity_discover, btDevices)
                        lvNewDevices.adapter = deviceListAdapter
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enableDisableButton = findViewById(R.id.btnOnOff)
        receiveButton = findViewById(R.id.btnReceive)
        enableDiscoverableButton = findViewById(R.id.btnEnableDiscoverable)
        discoverButton = findViewById(R.id.btnDiscover)
        showAllDevicesSwitch = findViewById(R.id.switchShowAllDevices)

        lvNewDevices = findViewById(R.id.rvBluetoothDevices)


        lvNewDevices.setOnItemClickListener(this)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()


        receiveButton.visibility = Button.INVISIBLE
        enableDiscoverableButton.visibility = Button.INVISIBLE
        discoverButton.visibility = Button.INVISIBLE


        enableDisableButton.setOnClickListener { toggleBluetooth()
            enableDiscoverableButton.visibility = Button.VISIBLE
            enableDisableButton.setBackgroundColor(getColor(R.color.green))
        }
        enableDiscoverableButton.setOnClickListener { enableDiscoverability()
            discoverButton.visibility = Button.VISIBLE

        }
        discoverButton.setOnClickListener { discoverDevices() }
        receiveButton.setOnClickListener {
            val connectionIntent = Intent(this, Connection::class.java)
            connectionIntent.putExtra(EXTRA_ADDRESS, deviceAddress)
            startActivity(connectionIntent)
        }


    }

    private fun toggleBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(enableBTIntent)

            val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            registerReceiver(bluetoothStateReceiver, filter)
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            bluetoothAdapter.disable()
            Log.d(TAG, "Bluetooth Disabled")
        }
    }

    private fun enableDiscoverability() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADMIN),
                    REQUEST_CODE_PERMISSIONS
                )
                return
            }
        }

        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            enableDiscoverableButton.setBackgroundColor(getColor(R.color.green))
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION , 300)
        }
        startActivity(discoverableIntent)
    }


    private fun discoverDevices() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_PERMISSIONS
            )
            return
        }

        if (bluetoothAdapter.isDiscovering) {
            discoverButton.setBackgroundColor(getColor(R.color.gray))

            bluetoothAdapter.cancelDiscovery()
            Log.d(TAG, "Discovery Canceled")
        }

        bluetoothAdapter.startDiscovery()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(discoveryReceiver, filter)
        discoverButton.setBackgroundColor(getColor(R.color.green))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                enableDiscoverability()
            } else {
                Log.d(TAG, "Permissions denied")
                Toast.makeText(this, "Permissions are required for Bluetooth functionality", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothStateReceiver)
        unregisterReceiver(discoverabilityReceiver)
        unregisterReceiver(discoveryReceiver)
    }
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    REQUEST_CODE_PERMISSIONS
                )
            }
            return
        }

        bluetoothAdapter.cancelDiscovery()

        val device = btDevices[position]
        deviceAddress = device.address
        val deviceName = device.name ?: "Unknown Device"

        device.createBond()

        val connectionIntent = Intent(this, Connection::class.java)
        connectionIntent.putExtra(EXTRA_ADDRESS, deviceAddress)
        connectionIntent.putExtra("device_name", deviceName)
        startActivity(connectionIntent)
    }

}