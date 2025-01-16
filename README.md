# Arduino Bluetooth App and HC-06 LED Controller

[![GitHub release](https://img.shields.io/github/v/release/yourusername/arduinobluetooth?style=flat-square)](https://github.com/yourusername/arduinobluetooth/releases)  
[![GitHub license](https://img.shields.io/github/license/yourusername/arduinobluetooth?style=flat-square)](LICENSE)  
[![Platform](https://img.shields.io/badge/platform-Android%20&%20Arduino-brightgreen?style=flat-square)](#)  
[![Status](https://img.shields.io/badge/status-Completed-blue?style=flat-square)](#)  

---

## 📱 Download APK

[![Download APK](https://img.shields.io/badge/Download-APK-blue?style=for-the-badge&logo=android)]([https://github.com/Shlok-Android-Dev/Arduino-Bluetooth/blob/main/app/release/Arduino%20Bluetooth.apk])

---

## 🎯 Features

### Android App:
- Display paired Bluetooth devices with names and addresses.
- Connect seamlessly to the HC-06 module.
- Control LED states using an intuitive interface.
  
### Arduino Code:
- Receives commands via the HC-06 module.
- Controls LED strip brightness and color.
- Easy to integrate with additional components.

---

## 📱 App Preview

| **Device List** | **LED Controller** |
|------------------|--------------------|
| ![Device List](path/to/device-list-image.png) | ![Controller](path/to/controller-image.png) |

---

## 🛠️ Tech Stack

### Android:
- **Language**: Kotlin  
- **Architecture**: MVVM  
- **UI Framework**: XML Layouts  
- **Other**: Bluetooth APIs, RecyclerView

### Arduino:
- **Microcontroller**: Arduino Uno/Nano  
- **Bluetooth Module**: HC-06  
- **LED Control**: Custom logic for brightness and color management.

---

## 📦 How to Use

### Prerequisites:
- Android device (API level 29+).
- Arduino Uno/Nano board.
- HC-06 Bluetooth module.
- LED strip and power supply.

### Steps:

#### 1. **Setup Arduino**:
1. Connect the HC-06 module to the Arduino:
   | HC-06 Pin | Arduino Pin |
   |-----------|-------------|
   | RX        | D10         |
   | TX        | D11         |
   | GND       | GND         |
   | VCC       | 5V          |

2. Upload the provided `arduino.ino` code to your Arduino board.

#### 2. **Run the Android App**:
1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/arduinobluetooth.git
