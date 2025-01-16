// This is the code of Arduino which is used to control the LED strip using Bluetooth.

#include <SoftwareSerial.h>
#include <FastLED.h>
#define NUM_LEDS 70
#define DATA_PIN 3
CRGB leds[NUM_LEDS];
SoftwareSerial mySerial(4, 5);  // RX, TX
int brightness = 0;      // LED brightness level
int fadeAmount = 5;      // Amount to change brightness each cycle
int data = 0;            // Data from Bluetooth
void setup() {
    Serial.begin(9600);         // Start Serial Monitor for debugging
    mySerial.begin(9600);       // Start Bluetooth serial communication
    FastLED.addLeds<WS2815, DATA_PIN, RGB>(leds, NUM_LEDS).setCorrection(TypicalLEDStrip);
    FastLED.setMaxPowerInVoltsAndMilliamps(5, 2000);
    FastLED.clear();
    FastLED.setBrightness(255); // Start with max brightness
    FastLED.show();
    Serial.println("System Initialized. Waiting for data...");
    data=3;
}
void loop() {
    // Read and print incoming Bluetooth data as hex
    if (mySerial.available()) {
        while (mySerial.available()) {
            int incomingByte = mySerial.read();  // Read each byte
            Serial.print("Data Received (Hex): ");
            Serial.println(incomingByte, HEX);   // Print as hexadecimal
            // Check for ASCII control characters 1, 2, and 3
            if (incomingByte == 1) {        // ASCII SOH (Start of Heading)
                data = 1;
            } else if (incomingByte == 2) { // ASCII STX (Start of Text)
                data = 2;
            } else if (incomingByte == 3) { // ASCII ETX (End of Text)
                data = 3;
            }
        }
    }
    // Perform the LED breathing animation based on the value of 'data'
    if (data == 1) {
        breathingAnimation(CRGB::Green);  // Green LED animation for '1'
    } else if (data == 2) {
        breathingAnimation(CRGB::Red);    // Red LED animation for '2'
    } else if (data == 3) {
        breathingAnimation(CRGB::Blue);   // Blue LED animation for '3'
    }
}
// Function to create a breathing animation with the given color
void breathingAnimation(CRGB color) {
    fill_solid(leds, NUM_LEDS, color);      // Set all LEDs to the given color
    FastLED.setBrightness(brightness);      // Adjust brightness
    FastLED.show();
    // Update brightness for breathing effect
    brightness += fadeAmount;
    // Reverse the direction of the fade at the ends of the fade
    if (brightness <= 0 || brightness >= 255) {
        fadeAmount = -fadeAmount;
    }
    delay(30);  // Adjust delay to control breathing speed (faster or slower breathing)
}

// old version of code
/*

*/
/*

Any kind of data can be sent using the similar code.
Here, we are using an ultrasonic sensor and sending the data obtained by it.

Connections
Vcc- 5V
Gnd- Gnd
Trig-12
Echo-11

*//*

#include<SoftwareSerial.h>
SoftwareSerial BTserial(0,1);
const int trigPin=12;
const int echoPin=11;

void setup() {
    pinMode(trigPin,OUTPUT);
    pinMode(echoPin,INPUT);
    pinMode(13,OUTPUT);
    BTserial.begin(9600);
    */
/* while(BTserial.available()==0)
    {BTserial.print(5);
    delay(2000);}*//*


}

void loop() {

    float duration,distance;
    digitalWrite(trigPin,LOW);
    delayMicroseconds(100);
    digitalWrite(trigPin,LOW);
    digitalWrite(trigPin,HIGH);
    delayMicroseconds(5000);
    digitalWrite(trigPin,LOW);
    duration=pulseIn(echoPin,HIGH);
    distance=duration/29/2;


    if(BTserial.available()==0)
    {BTserial.print(distance);
        BTserial.print("\n");
        delay(500);
    }

}
*/
