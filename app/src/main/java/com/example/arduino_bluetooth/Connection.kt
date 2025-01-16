package com.example.arduino_bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.arduinobluetooth.R
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class Connection : AppCompatActivity() {



    companion object {
        private const val TAG = "Connection"
        private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 100
        private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        const val EXTRA_ADDRESS = "device_address"
        const val EXTRA_NAME = "device_name"


    }

    private var address: String? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var isConnected = false
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var isBtConnected = false

    private lateinit var startConnectionButton: Button
    private lateinit var sendSampleButton: Button
    private lateinit var receiveButton: Button
    private lateinit var incomingDataTextView: TextView
    private lateinit var sendData: TextView
    private lateinit var deviceNameTV: TextView
    private lateinit var disconnectButton: Button


    private var inputStream: InputStream? = null
    private val messages = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)

        address = intent.getStringExtra(EXTRA_ADDRESS)
        val deviceName = intent.getStringExtra(EXTRA_NAME ) ?: "Unknown Device"

        startConnectionButton = findViewById(R.id.btnStartConnection)
        deviceNameTV = findViewById(R.id.deviceNameTV)
        incomingDataTextView = findViewById(R.id.logsReceive)
        sendData = findViewById(R.id.sendData)
        sendSampleButton = findViewById(R.id.btnSendSample)
        receiveButton = findViewById(R.id.btnReceive)
        disconnectButton = findViewById(R.id.btnDisconnect)

        deviceNameTV.text = "$deviceName"

        startConnectionButton.visibility = Button.VISIBLE
        sendSampleButton.visibility = Button.INVISIBLE
        receiveButton.visibility = Button.INVISIBLE
        disconnectButton.visibility = Button.INVISIBLE
        deviceNameTV.visibility = Button.INVISIBLE

        startConnectionButton.setOnClickListener {
            ConnectBluetoothTask().execute()
            startConnectionButton.setBackgroundColor(getColor(R.color.green))
            sendSampleButton.visibility = Button.VISIBLE
            deviceNameTV.visibility = Button.VISIBLE
        }
        disconnectButton.setOnClickListener { disconnect() }
        receiveButton.setOnClickListener { startReceivingData() }
        sendSampleButton.setOnClickListener {
            sendData("2")
        }

    }


    private fun disconnect() {
        try {
            bluetoothSocket?.close()
            finish()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startReceivingData() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                listenForInput()
                incomingDataTextView.text = messages.toString()
                handler.postDelayed(this, 200)
            }
        }
        handler.post(runnable)
    }
/*
    private fun sendSampleData() {
        if (btSocket != null && isBtConnected) {
            try {
                sendSampleButton.setBackgroundColor(getColor(R.color.green))

                val outputStream = btSocket!!.outputStream
                val sampleData = "1"
                outputStream.write(sampleData.toByteArray())
                Log.d(TAG, "Sent sample data: $sampleData")
                Toast.makeText(this, "Sample Data Sent", Toast.LENGTH_SHORT).show()
                incomingDataTextView.text = "Sample Data Sent: $sampleData"
            } catch (e: IOException) {
                Log.e(TAG, "Error sending sample data", e)
                incomingDataTextView.text = "Error Sending Data"
                sendSampleButton.setBackgroundColor(getColor(R.color.gray))

            }
        } else {
            Toast.makeText(this, "Not connected to a device", Toast.LENGTH_SHORT).show()
        }
    }
*/

    private fun sendData(data: String) {
        if (isConnected && bluetoothSocket != null) {
            Thread {
                try {
                    val outputStream: OutputStream = bluetoothSocket!!.outputStream
                    val inputStream: InputStream = bluetoothSocket!!.inputStream

                    outputStream.write(data.toByteArray())
                    outputStream.flush()
                    Log.d(TAG, "Data sent: $data")

                    val buffer = ByteArray(1024)
                    val bytesRead = inputStream.read(buffer)
                    val response = String(buffer, 0, bytesRead).trim()

                    runOnUiThread {
                        incomingDataTextView.text = "Sent: $data ||| Arduino Response: $response"
                        Toast.makeText(this, "Response: $response", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Response from Arduino: $response")
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Error sending data", e)
                    Toast.makeText(this, "Error sending data", Toast.LENGTH_SHORT).show()

                    runOnUiThread {
                        Toast.makeText(this, "Error sending data", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        } else {
            runOnUiThread {
                Toast.makeText(this, "Not connected to a device", Toast.LENGTH_SHORT).show()
            }
        }
    }








    private fun listenForInput() {
        inputStream = bluetoothSocket?.inputStream
        val buffer = ByteArray(1024)

        try {
            val bytes = inputStream?.read(buffer) ?: return
            val incomingMessage = String(buffer, 0, bytes)
            messages.append(incomingMessage)
            Log.d(TAG, "Received data: $incomingMessage")
            Toast.makeText(this, incomingMessage, Toast.LENGTH_SHORT).show()
            sendData.setText(" , Received data: ${incomingMessage}")

        } catch (e: IOException) {
            Log.e(TAG, "Error reading input stream", e)
        }
    }

    private inner class ConnectBluetoothTask : AsyncTask<Void, Void, Boolean>() {

        override fun onPostExecute(success: Boolean) {
            if (success) {
                isBtConnected = true
                isConnected = true
                Toast.makeText(this@Connection, "Connection Successful", Toast.LENGTH_SHORT).show()

                // Enable send and receive buttons
                sendSampleButton.visibility = Button.VISIBLE
                receiveButton.visibility = Button.VISIBLE
                disconnectButton.visibility = Button.VISIBLE
            } else {
                isBtConnected = false
                Toast.makeText(this@Connection, "Connection Failed", Toast.LENGTH_SHORT).show()
                finish()
            }
        }


        override fun doInBackground(vararg params: Void?): Boolean {
            return try {
                if (bluetoothSocket == null || !isBtConnected) {
                    if (ActivityCompat.checkSelfPermission(
                            this@Connection,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@Connection,
                            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                            BLUETOOTH_PERMISSION_REQUEST_CODE
                        )
                        return false
                    }

                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device = bluetoothAdapter?.getRemoteDevice(address)
                    bluetoothSocket = device?.createInsecureRfcommSocketToServiceRecord(MY_UUID)

                    bluetoothAdapter?.cancelDiscovery()

                    bluetoothSocket?.connect()
                }
                isConnected = true
                true
            } catch (e: IOException) {
                Log.e(TAG, "Connection failed", e)
                closeConnection()
                false
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        closeConnection()
    }

    private fun closeConnection() {
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing connection", e)
        }
    }

}
