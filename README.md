# Arduino Ultrasonic Distance Measurement App

This project is an **Arduino-based distance measurement system** using an **Ultrasonic Sensor (HC-SR04)**. The app transmits real-time distance data via **Bluetooth (HC-05)**, allowing for remote monitoring of distance measurements. The data is sent continuously at fixed intervals, providing users with up-to-date information about the distance to objects in front of the sensor.

## Features

- **Real-Time Distance Measurement**: Measures the distance using the ultrasonic sensor (HC-SR04) and sends the data over Bluetooth.
- **Bluetooth Communication**: Utilizes the **HC-05 Bluetooth module** to transmit distance data to a paired device.
- **Error Handling**: The system checks for valid measurements and handles errors when no echo is received.
- **Interval Transmission**: The app sends the distance data every 500ms for real-time monitoring.

## Application Workflow

1. The **HC-SR04 ultrasonic sensor** measures the distance to an object using sound waves. The **Trig Pin** sends a pulse, and the **Echo Pin** listens for the return pulse, calculating the distance based on the time delay.
2. The distance value is transmitted to a paired device via the **HC-05 Bluetooth module**.
3. The data is sent to the Bluetooth device in the following format:  
   `Distance: <distance_value> cm`
4. The app continuously sends distance updates every 500ms for live monitoring.

## Installation

1. Clone this repository or download the project files.
2. Open the Arduino IDE and upload the provided code to your Arduino board (e.g., Arduino Uno).
3. Ensure that the HC-SR04 ultrasonic sensor and HC-05 Bluetooth module are connected to the appropriate pins.
4. Pair your Bluetooth device with the HC-05 module.
5. Open the Serial Monitor to debug the data or connect your Bluetooth device to an Android app for further use.

## Hardware Requirements

- **Arduino Board** (e.g., Arduino Uno)
- **HC-06 Bluetooth Module**
- Jumper wires and a breadboard for connections

## Software Requirements

- **Arduino IDE** for uploading the code to the Arduino board.
- **Bluetooth-enabled device** to receive and display the distance data.

## Preview

Below is a preview of how the system works:

![Preview Image](./device-list.jpg)  
*Example of the distance measurement interface (or image from your app)*


## 📱 Download APK

[![Download APK](https://img.shields.io/badge/Download-APK-blue?style=for-the-badge&logo=android)](https://your-apk-link.com)


## License

This project is open-source and available under the [MIT License](LICENSE).

---

Feel free to modify the hardware, add more features, or integrate this system into different projects!

## Support

For any issues or questions, feel free to open an issue on the repository or contact me at shlok0531@gmail.com .

