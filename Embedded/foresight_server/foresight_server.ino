#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <EEPROM.h>

//Variables
int i = 0;
int statusCode;

/* Put your SSID & Password */
const char* APssid = "A64.foresight.1";  // Enter SSID here
const char* APpassword = "thereisnospoon";  //Enter Password here
String st;
String content;


//Function Decalration
bool testWifi(String, String);
void launchWeb(void);
void setupAP(void);

//Establishing Local server at port 80 whenever required
ESP8266WebServer server(80);


void(* resetFunc) (void) = 0;

void setup()
{

  Serial.begin(115200); //Initialising if(DEBUG)Serial Monitor
  Serial.println();
  Serial.println("Disconnecting previously connected WiFi");
  WiFi.disconnect();

  WiFi.mode(WIFI_AP_STA);
  
  EEPROM.begin(512);
  
  delay(10);
  pinMode(LED_BUILTIN, OUTPUT);
  Serial.println();
  Serial.println();
  Serial.println("Startup");

  String essid;
  for (int i = 0; i < 32; ++i)
  {
    essid += char(EEPROM.read(i));
  }
  
  Serial.println();
  Serial.print("SSID: ");
  Serial.println(essid);
  Serial.println("Reading EEPROM pass");

  String epass = "";
  for (int i = 32; i < 96; ++i)
  {
    epass += char(EEPROM.read(i));
  }
  Serial.print("PASS: ");
  Serial.println(epass);

  WiFi.softAP(APssid, APpassword);
  if (testWifi(essid, epass ))
  {
    Serial.println("Succesfully Connected!!!");
    Serial.println("Creating IP Broadcast channel");

    Serial.println("Set Mode to: WIFI_AP_STA");
//    WiFi.disconnect();

    IPBroadcast();
    server.begin();
    return;
  }
  else
  {
    Serial.println("Turning the HotSpot On");
    setupAP();// Setup HotSpot
    server.begin();
  }

  Serial.println();
  Serial.println("Waiting.");
  
  while ((WiFi.status() != WL_CONNECTED))
  {
//    Serial.print(".");
    delay(100);
    server.handleClient();
  }

}
void loop() {
  
  server.handleClient();
  
  if ((WiFi.status() == WL_CONNECTED))
  {

    for (int i = 0; i < 10; i++)
    {
      digitalWrite(LED_BUILTIN, HIGH);
      delay(1000);
      digitalWrite(LED_BUILTIN, LOW);
      delay(1000);
    }
  }
  else
  {
    for (int i = 0; i < 10; i++)
    {
      digitalWrite(LED_BUILTIN, HIGH);
      delay(500);
      digitalWrite(LED_BUILTIN, LOW);
      delay(500);
    }
    resetFunc();
  }
  Serial.println(WiFi.status());
}

bool testWifi(String essid,String epass)
{
  int c = 0;
  Serial.println("Waiting for Wifi to connect");
  WiFi.begin(essid.c_str(), epass.c_str());
  while ( c < 20 ) {
    if (WiFi.status() == WL_CONNECTED)
    {
      Serial.println("IP : ");
      Serial.print(WiFi.localIP()); 
      return true;
    }
    delay(500);
    Serial.print("*");
    c++;
  }
  Serial.println("");
  Serial.println("Connect timed out, opening AP");
  return false;
}


void setupAP(void)
{
//  WiFi.disconnect();
//  delay(100);
//  int n = WiFi.scanNetworks();
//  Serial.println("scan done");
//  if (n == 0)
//    Serial.println("no networks found");
//  else
//  {
//    Serial.print(n);
//    Serial.println(" networks found");
//    for (int i = 0; i < n; ++i)
//    {
//      // Print SSID and RSSI for each network found
//      Serial.print(i + 1);
//      Serial.print(": ");
//      Serial.print(WiFi.SSID(i));
//      Serial.print(" (");
//      Serial.print(WiFi.RSSI(i));
//      Serial.print(")");
//      Serial.println((WiFi.encryptionType(i) == ENC_TYPE_NONE) ? " " : "*");
//      delay(10);
//    }
//  }
//  Serial.println("");
//  st = "<ol>";
//  for (int i = 0; i < n; ++i)
//  {
//    // Print SSID and RSSI for each network found
//    st += "<li>";
//    st += WiFi.SSID(i);
//    st += " (";
//    st += WiFi.RSSI(i);
//
//    st += ")";
//    st += (WiFi.encryptionType(i) == ENC_TYPE_NONE) ? " " : "*";
//    st += "</li>";
//  }
//  st += "</ol>";

  delay(100);
//  WiFi.softAP(APssid, APpassword);
  createWebServer();

}

void createWebServer()
{
  Serial.println("Entered createWebServer function");
    server.on("/", []() {
      Serial.println("createWebServer/");
      IPAddress ip = WiFi.softAPIP();
      String ipStr = String(ip[0]) + '.' + String(ip[1]) + '.' + String(ip[2]) + '.' + String(ip[3]);
      content = "<!DOCTYPE HTML>\r\n<html>Foresight Server ";
      content += "</p><form method='get' action='setting'><label>SSID: </label><input name='ssid' length=32><input name='pass' length=64><input type='submit'></form>";
      content += "</html>";
      server.send(200, "text/html", content);
    });


    server.on("/setting", []() {
      String qssid = server.arg("ssid");
      String qpass = server.arg("pass");
      if (qssid.length() > 0 && qpass.length() > 0) {
        Serial.println("clearing eeprom");
        for (int i = 0; i < 96; ++i) {
          EEPROM.write(i, 0);
        }
        Serial.println(qssid);
        Serial.println("");
        Serial.println(qpass);
        Serial.println("");

        Serial.println("writing eeprom ssid:");
        for (int i = 0; i < qssid.length(); ++i)
        {
          EEPROM.write(i, qssid[i]);
          Serial.print("Wrote: ");
          Serial.println(qssid[i]);
        }
        Serial.println("writing eeprom pass:");
        for (int i = 0; i < qpass.length(); ++i)
        {
          EEPROM.write(32 + i, qpass[i]);
          Serial.print("Wrote: ");
          Serial.println(qpass[i]);
        }
        EEPROM.commit();
        testWifi(qssid, qpass);
        
        
        content = "{\"Success\":\"200\"}";
        statusCode = 200;
//        ESP.reset();
      } else {
        content = "{\"Error\":\"404 not found\"}";
        statusCode = 404;
        Serial.println("Sending 404");
      }
      server.sendHeader("Access-Control-Allow-Origin", "*");
      server.send(statusCode, "application/json", content);
      delay(2000);
      Serial.println("resetting");
      resetFunc();  //call reset
    });
}

void IPBroadcast() {
  Serial.println("IP Broadcast called");
  server.on("/", []() {
      IPAddress ip = WiFi.localIP();
      Serial.println("IP Broadcast called, end point hit");
      String ipStr = String(ip[0]) + '.' + String(ip[1]) + '.' + String(ip[2]) + '.' + String(ip[3]);
      content = "{\"ip\":\"";
      content += ipStr;
      content += "\"}";
      server.sendHeader("Access-Control-Allow-Origin", "*");
      server.send(200, "text/html", content);
      delay(1000);
    });
}
