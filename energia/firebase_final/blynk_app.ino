/**************************************************************
 * Blynk is a platform with iOS and Android apps to control
 * Arduino, Raspberry Pi and the likes over the Internet.
 * You can easily build graphic interfaces for all your
 * projects by simply dragging and dropping widgets.
 *
 *   Downloads, docs, tutorials: http://www.blynk.cc
 *   Blynk community:            http://community.blynk.cc
 *   Social networks:            http://www.fb.com/blynkapp
 *                               http://twitter.com/blynk_app
 *
 * Blynk library is licensed under MIT license
 * This example code is in public domain.
 *
 **************************************************************
 * This example shows how to use TI CC3200-LaunchXL
 * to connect your project to Blynk.
 *
 * Feel free to apply it to any other example. It's simple!
 *
 **************************************************************/

#define BLYNK_PRINT Serial    // Comment this out to disable prints and save space
#include <SPI.h>
#include <WiFi.h>
#include <BlynkSimpleTI_CC3200_LaunchXL.h>
#include <Wire.h>

int timer_counter = 0;
boolean enable_count = false; 

// You should get Auth Token in the Blynk App.
// Go to the Project Settings (nut icon).
char auth[] = "KkhXYm9SEnhXZuRMIUeXJbqSApDM_3jZ";

// Your WiFi credentials
char ssid[] = "Soham";
char pass[] = "Nachidk99999*";        // Set to "" for open networks
int dataX = 0;
int dataY = 0;
int dataZ = 0;

int commands[1000];
int delay_commands[1000];
int comm_count = 0;

void setup()
{
  //Open a serial terminal with the PC
  Serial.begin(9600);   
  //Set up a blynk connection with your WiFi credentials
  Blynk.begin(auth, ssid, pass);  
  
  //Setup RED LED to be an output
  pinMode(RED_LED, OUTPUT);
  digitalWrite(RED_LED, LOW);

  pinMode(GREEN_LED, OUTPUT);
  digitalWrite(GREEN_LED, LOW);

  pinMode(YELLOW_LED, OUTPUT);
  digitalWrite(YELLOW_LED, LOW);
}

// Virtual Pin 1 - Toggles the LED high or low depending on the mobile app input
BLYNK_WRITE(V1)
{
 
  BLYNK_LOG("Got a value to red: %s", param.asStr());
  
  int i = param.asInt(); 
  if(i == 1)
  {
    digitalWrite(RED_LED, HIGH);
    enable_count = true; 
  }
  else if(i == 0)
  {
    digitalWrite(RED_LED, LOW);

    // 1 is conditional command for red led for automation
    commands[comm_count] = 1;
    delay_commands[comm_count] = timer_counter;

    comm_count += 1;
    
    timer_counter = 0;
    enable_count = false;
  }
}

BLYNK_WRITE(V2) {
  BLYNK_LOG("Got a value to Yellow: %s", param.asStr());
  
  int i = param.asInt(); 
  if(i == 1)
  {
    digitalWrite(YELLOW_LED, HIGH);
    enable_count = true; 
  }
  else if(i == 0)
  {
    digitalWrite(YELLOW_LED, LOW);

    commands[comm_count] = 2;
    delay_commands[comm_count] = timer_counter;

    comm_count += 1;
    
    timer_counter = 0;
    
    enable_count = false;
  }
}

BLYNK_WRITE(V3) {
  BLYNK_LOG("Got a value to GREEN: %s", param.asStr());
  
  int i = param.asInt(); 
  if(i == 1)
  {
    digitalWrite(GREEN_LED, HIGH);
    enable_count = true; 
  }
  else if(i == 0)
  {
    digitalWrite(GREEN_LED, LOW);

    commands[comm_count] = 3;
    delay_commands[comm_count] = timer_counter;

    comm_count += 1;
    
    timer_counter = 0;
    
    enable_count = false;
  }
}

BLYNK_WRITE(V4) {

  BLYNK_LOG("Got a value to AUTO: %s", param.asStr());
  int i = param.asInt(); 
  if(i == 1)
  {     
       int nxtComm = 0;
       while(commands[nxtComm] != 0) {
          switch(commands[nxtComm]) {
            case 1:digitalWrite(RED_LED,HIGH);
                   
                   for(int m = 0; m < delay_commands[nxtComm]; m++){
                    
                    Serial.println(m);
                    
                   }
                   
                   digitalWrite(RED_LED,LOW);
                   break;
            case 2:digitalWrite(YELLOW_LED,HIGH);
                   for(int m = 0; m < delay_commands[nxtComm]; m++){
                    Serial.println(m);
                    
                   }
                   digitalWrite(YELLOW_LED,LOW);
                   break;
            case 3:digitalWrite(GREEN_LED,HIGH);
                   for(int m = 0; m < delay_commands[nxtComm]; m++){
                    Serial.println(m);
                    
                   }
                   digitalWrite(GREEN_LED,LOW);
                   break;  
          }
          nxtComm += 1;
       }
       nxtComm = 0;
       
  }
  else if(i == 0)
  {
    int errase = 0;
    comm_count = 0;
    while(commands[errase] != 0) {
      commands[errase] = 0;
      delay_commands[errase] = 0;
      Serial.println(errase);
      errase += 1;  
    }
    errase = 0;
  }
  
}

// The main loop listens for commands from the mobile app
void loop()
{
  Blynk.run();
  if(enable_count == true) {
    timer_counter += 1; 
    Serial.println(timer_counter);
  }
  //Serial.println("1");
}
