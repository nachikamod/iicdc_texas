

#include <SPI.h>
#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiServer.h>
#define temp_in 2

int dat = 0;

// your network name also called SSID
char ssid[] = "v3n0m";
// your network password
char password[] = "10050963";
// your network key Index number (needed only for WEP)
int keyIndex = 0;

int count = 0;
char snum[5];

WiFiServer server(80);

void setup() {
  Serial.begin(115200);      // initialize serial communication
  pinMode(RED_LED, OUTPUT);      // set the LED pin mode
  pinMode(temp_in,INPUT);
  // attempt to connect to Wifi network:
  Serial.print("Attempting to connect to Network named: ");
  // print the network name (SSID);
  Serial.println(ssid); 
  // Connect to WPA/WPA2 network. Change this line if using open or WEP network:
  WiFi.begin(ssid, password);
  while ( WiFi.status() != WL_CONNECTED) {
    // print dots while we wait to connect
    Serial.print(".");
    delay(300);
  }
  
  Serial.println("\nYou're connected to the network");
  Serial.println("Waiting for an ip address");
  
  while (WiFi.localIP() == INADDR_NONE) {
    // print dots while we wait for an ip addresss
    Serial.print(".");
    delay(300);
  }

  // you're connected now, so print out the status  
  printWifiStatus();
  
  Serial.println("Starting webserver on port 80");
  server.begin();                           // start the web server on port 80
  Serial.println("Webserver started!");

}


void loop() {
  // listen for incoming clients
  WiFiClient client = server.available();
  if (client) {
    Serial.println("new client");
    // an http request ends with a blank line
    boolean currentLineIsBlank = true;
    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        Serial.write(c);
        // if you've gotten to the end of the line (received a newline
        // character) and the line is blank, the http request has ended,
        // so you can send a reply
        if (c == '\n' && currentLineIsBlank) {
          // send a standard http response header
          client.println("HTTP/1.1 200 OK");
          client.println("Content-Type: text/html");
          client.println("Connection: close");  // the connection will be closed after completion of the response
          //client.println("Refresh: 5");  // refresh the page automatically every 5 sec
          client.println();
          client.println("<!DOCTYPE HTML>");
          client.println("<html>");
          client.println("<head>");
          //Intigration of firebase to website see the firebase web documentation
          client.println("<script src='https://www.gstatic.com/firebasejs/7.11.0/firebase-app.js'></script>");
          client.println("<script src='https://www.gstatic.com/firebasejs/7.11.0/firebase-analytics.js'></script>");
          client.println("<script src='https://www.gstatic.com/firebasejs/7.11.0/firebase-database.js'></script>");
          client.println("<script>var firebaseConfig = {apiKey: 'AIzaSyC4MoPYm4SM_6HQEKSj_DvJMIjIkEru7Uk',authDomain: 'iicdc-texas.firebaseapp.com',databaseURL: 'https://iicdc-texas.firebaseio.com',projectId: 'iicdc-texas',storageBucket: 'iicdc-texas.appspot.com',messagingSenderId: '70340898763',appId: '1:70340898763:web:e82926a61a5b0499ea104e',measurementId: 'G-0D1L5YFE16'};firebase.initializeApp(firebaseConfig);firebase.analytics();</script>");
          client.println("</head>");
          client.println("<body>");
          client.println("<script>");
          client.println("function sendData(){");

          while(count != 20) {
            count = count + 1;
            dat = analogRead(temp_in) / 27.3;
            itoa(dat, snum, 10);
            String daat = snum; 
            client.println("firebase.database().ref().push().child(\"data\").set('" + daat + "')");  
          }
          
          /*while(true){
            count = count + 1;
            itoa(count, snum, 10);
            String daat = snum;
            client.println("firebase.database().ref().child('data').set('"+ daat +"');console.log('"+ daat +"');");  
          }*/
          client.println("}");
          client.println("</script>");
          client.println("<button onclick=\"sendData()\">Click Me!</button>");          
          client.println("</body>");
          client.println("</html>");
          break;
        }
        if (c == '\n') {
          // you're starting a new line
          currentLineIsBlank = true;
        }
        else if (c != '\r') {
          // you've gotten a character on the current line
          currentLineIsBlank = false;
        }
      }
    }
    // give the web browser time to receive the data
    delay(1);

    // close the connection:
    client.stop();
    Serial.println("client disonnected");
  }
}


void printWifiStatus() {
  // print the SSID of the network you're attached to:
  Serial.print("Network Name: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}
