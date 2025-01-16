package com.example.arduino_bluetooth

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.arduinobluetooth.R
import java.util.UUID


        class dumy : AppCompatActivity() {

            private lateinit var bluetoothAdapter: BluetoothAdapter
            private var bluetoothSocket: BluetoothSocket? = null
            private val arduinoUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard UUID for SPP

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_dumy)

                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

                val btnConnect = findViewById<Button>(R.id.btnConnect)
                val btnSend = findViewById<Button>(R.id.btnSend)
                val tvStatus = findViewById<TextView>(R.id.tvStatus)

                btnConnect.setOnClickListener {
                    connectToArduino(tvStatus)
                }

                btnSend.setOnClickListener {
                    sendData("1")
                }
            }

            private fun connectToArduino(tvStatus: TextView) {
                if (!bluetoothAdapter.isEnabled) {
                    Toast.makeText(this, "Please enable Bluetooth", Toast.LENGTH_SHORT).show()
                    return
                }

                val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
                val device: BluetoothDevice? = pairedDevices.find { it.name == "Your_Arduino_Device_Name" }

                if (device == null) {
                    Toast.makeText(this, "Arduino device not found", Toast.LENGTH_SHORT).show()
                    return
                }

                try {
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
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(arduinoUUID)
                    bluetoothSocket?.connect()
                    tvStatus.text = "Status: Connected to Arduino"
                } catch (e: IOException) {
                    tvStatus.text = "Status: Connection Failed"
                    e.printStackTrace()
                }
            }

            private fun sendData(data: String) {
                if (bluetoothSocket == null) {
                    Toast.makeText(this, "Not connected to Arduino", Toast.LENGTH_SHORT).show()
                    return
                }

                try {
                    bluetoothSocket?.outputStream?.write(data.toByteArray())
                    Toast.makeText(this, "Data sent successfully", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(this, "Failed to send data", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }