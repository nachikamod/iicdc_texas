#define BLYNK_PRINT Serial
#include <SPI.h>
#include <WiFi.h>
#include <BlynkSimpleTI_CC3200_LaunchXL.h>
#include <Wire.h>

int timer_counter = 0;    //timer for storing for how much time an button is clicked
boolean enable_count = false;   //conditional boolean to enable and disssable counter

char auth[] = "KkhXYm9SEnhXZuRMIUeXJbqSApDM_3jZ"; //Auth token form blynk app

// Your WiFi credentials
char ssid[] = "Wifi name here";
char pass[] = "Password here"; 
int dataX = 0;
int dataY = 0;
int dataZ = 0;

int commands[1000];     //array to store commands
int delay_commands[1000];   //array to store commands execution timings
int comm_count = 0;         //number of commands counter

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

    
    commands[comm_count] = 1;   // 1 is conditional command for red led for automation
    delay_commands[comm_count] = timer_counter;     //get time of click from timer

    comm_count += 1;           // after updating command and delay in array append the index of array to save next command in next index
    
    timer_counter = 0;         //after storing the command clear the counter
    enable_count = false;      //turn off the counter
  }
}

//for yellow led
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

//for green led
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

//for automation led
BLYNK_WRITE(V4) {

  BLYNK_LOG("Got a value to AUTO: %s", param.asStr());
  int i = param.asInt(); 
  if(i == 1)
  {     
       int nxtComm = 0;   //command index to read from array
       while(commands[nxtComm] != 0)
       /*
       check if the command is empty at specific index, if we dont use this step then our
       any loop will have to iterate through whole array that is 1000 count.
        Hence by using this method we check if there is any command at current index if yes then execute forward else break
       */
       
       {   
         
         switch(commands[nxtComm]) { //Getting commands from array we previously stored
            case 1:digitalWrite(RED_LED,HIGH);
                   
                    //generating delay using for loop as you can see we also stored the time of button click and hold
                   for(int m = 0; m < delay_commands[nxtComm]; m++){
                    
                    Serial.println(m);
                    
                   }
                   
             //turning off the led after the delay ends
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
          nxtComm += 1; // after executing first case that is oth index of array increase the nxtComm by one to read next index for next command and delay 
       }
    
      //after successfull execution of all commands in array reset the index to zero again
       nxtComm = 0;
       
  }
  else if(i == 0)
  {
    //if automation button is turned off clear the commands and delay array
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
  
  //counter to count button click time
  if(enable_count == true) {
    timer_counter += 1; 
    Serial.println(timer_counter);
  }
  //Serial.println("1");
}
