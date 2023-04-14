#if defined(ESP32)

#include <WiFi.h>

#elif defined(ESP8266)

#include <ESP8266WiFi.h>

#endif

#include <DHT.h>
#include <Firebase_ESP_Client.h>

#define DHTPIN 15     // Pin de lectura de datos del DHT11
#define DHTTYPE DHT11 // Tipo de sensor DHT

#define WIFI_SSID "DESKTOP-CHULG57 3746"

#define WIFI_PASSWORD "8&n0A103"


#define FIREBASE_HOST "compose-13e83-default-rtdb.firebaseio.com"

#define FIREBASE_PROJECT_ID "compose-13e83"

#define FIREBASE_CLIENT_EMAIL "firebase-adminsdk-skur7@compose-13e83.iam.gserviceaccount.com";

const char PRIVATE_KEY[] PROGMEM = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDmMDBrT5/xdQpA\nB0bfRIO2yeKTwMh5tTyrVA/yi6++g6MiJLHbnsvVhnwe8/QgHhi2pQmFeAeKe1jQ\nNDiqbivOwQG1tbg0o3+iPyS4ql7pe7bLXwOSbOgMNBYh0D32pbHJjNuKwKqQeRr5\nAGRsDkRXPOqAYBrxSmA7Z/Jj1TRZZt+8eLA6N7kr3M42AWXdKLyXCAPO8q1RhUoA\nmr94XBhum+/sJ/bMGuvmFmqucNul539AlImGSxekLFMYe5eFb6SqIpIllooxFm4n\n4FGVXMlhXmRSKM+2NGtWuP8l2SGNvtU8CYUrgEwSVfCL2cLLADEg2iC09rk/UtW5\nA5B3IbtrAgMBAAECggEAPsVSVtkaTR51ApvZZEYLQvvPuSn/oUzec/wS8znLXYEJ\nKeTmyXSGsj4ft9MftehvJdNggtCGAen8AJ7U+wjbEmEIhdQ9nMEfK4/bhC4VfscZ\nRGEvGK1xI+7KQENIumtZzZQHRFGg0Y/s3o0QVXUYhs+aRRKwFi+JiXmhnD7GLUB/\ninHKf9jf7nLfh7QrsoOZPN3JL7yC4Dwp3rEQ/lrBShaoJh0xsQ3w3krlPCXwUVQH\nARqvpwIIPl6OQLKJRaFugEydPAsntwdg3H12ACdFXSS7DlJqMm3UqNTC9+UpOqxZ\nz7eGcwDTH2pnES6TkdNnNOaK22sFYCGR+R58eLJHqQKBgQDy31QFWcE/PthJLm3q\ntXJKzo5jINHJp4iyJ409LCtqppdo0Ur/3ze11UHQW4S2qFOLb+2eewauyaqgJ8m5\n0W8YV54NOGi7Epw+eBbPoZNPpaA1AYpzSGQvWUHWX+OHwNuejX5Ls3UcTWbYKzFq\nKc7KAzPvaEVl1dNYcWS18HKH4wKBgQDyoVkcN6ELbeetd20K8Q9/BPlmuuywdSZ4\nIrIjOibt0GJK3ARH574/exJGyGx2tThUB9zunX0o2mjD9hOft/0UaKW3O1eRgb6f\ngWZcYKYxrrTOfOG1FAjJP6j35y7jyBDzr2uDzi/tVga7GjyD2u2ebsA3aqzyC+GR\nT1INNe8E2QKBgGggu+M3YhT9mrl1gIa6mG/eM0nozkkI3SCUuIdbopmtJwk3glJF\nn5sD/Z2ZP0MY1AjaSiRCVtElgVP8+w4B7wGevxKn9q8OAZL+5bjLa18ggZl4OaXH\nibyLJiEFJ4YSd5Z4z4chP7qMrOVNT3hWyGPwD5o7OFW64UvwAd/HYTNFAoGAHwct\nOyOOD8UdVJyqMDUFs0uyjUWoUSSaJ9DgEQs1wUHd4A7k6UEknj+h5bVp0YB5VJ2w\nzj8Lq3bK4QyOE+XWko6TaqxHFY+PDhzhNz0Q49egvatQvfRy7zGZ7SH+aBjEujUb\nvb5XeLWApNrFFGOh8ZneMPgOZ+HHFesb5h6Yk3kCgYEAlnP+JNwHbpoS4kvY2mzq\nCzNsimPtFNRU334TSFKgdjGDhejVziqTl+JL3qSEipS0qbRtJQfg7lYfxQpkspAw\njOK2CErxpGk8BPLMwF0VKpuBnlkm5if3PCaoTrPOfDmp5qTRHQi4Aps1Gybfjf8M\nwEXbQXnW3NabEbkHEQpC0XM=\n-----END PRIVATE KEY-----\n";
  
DHT dht(DHTPIN, DHTTYPE);
FirebaseData fbdo;


FirebaseAuth auth;


FirebaseConfig config;

unsigned long lastTime = 0;

int count = 0;

int contador = 0;

void setup() {

    pinMode(2,OUTPUT);
  
    Serial.begin(115200);
  
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  
    Serial.print("Connecting to Wi-Fi");
  
    while (WiFi.status() != WL_CONNECTED) {
  
      Serial.print(".");
  
      delay(300);
  
    }

    Serial.println();

    Serial.print("Connected with IP: ");
    
    Serial.println(WiFi.localIP());
    
    Serial.println();

    
    /* Assign the project host (required) */
    
    config.host = FIREBASE_HOST;
    
    /* Assign the sevice account credentials and private key (required) */
    
    config.service_account.data.client_email = FIREBASE_CLIENT_EMAIL;
    
    config.service_account.data.project_id = FIREBASE_PROJECT_ID;
    
    config.service_account.data.private_key = PRIVATE_KEY;
    
    dht.begin();
    Firebase.begin(&config, &auth);
    
    Firebase.reconnectWiFi(true);
  
    #if defined(ESP8266)
    
    //Set the size of WiFi rx/tx buffers in the case where we want to work with large data.
    
    fbdo.setBSSLBufferSize(1024, 1024);
    
    #endif

  
    //Set the size of HTTP response buffers in the case where we want to work with large data.
    
    fbdo.setResponseSize(1024);
}


void loop(){




//Verificar que la conexión a firebase exista







    if(Firebase.ready()){
 
 
        //Obtener un valor entero desde Firebase
        if(Firebase.RTDB.getString(&fbdo,"/esp/led_state")){  //Colección
    
            Serial.print("Obteniendo Valor Entero, int = ");
            
            Serial.println(fbdo.stringData());
            
            
            if(1 == fbdo.stringData().toInt()) {
            
                digitalWrite(2,HIGH);//CONECTAR UN LED EN D4
            }
          
            else {
                digitalWrite(2,LOW);
            }
    
        }
        
        else{
              Serial.print("Error: ");
              Serial.println(fbdo.errorReason());
        }


        float temperatura = dht.readTemperature();
        float humedad = dht.readHumidity();
      
        // Verificar si se pudo leer el sensor
        if (isnan(temperatura) || isnan(humedad)) {
          Serial.println("Error al leer el sensor");
          return;
        }
        Serial.print("Temperatura: ");
        Serial.print(temperatura);
        Serial.print(" °C, Humedad: ");
        Serial.print(humedad);
        Serial.println(" %");
    

/******************Subir datos desde el ESP8266***************************/

        /*if(Firebase.RTDB.setInt(&fbdo,"/esp/temperature",contador)){        
            contador = contador +=1;            
            Serial.println(contador);            
            Serial.println("Valor guardado en Firebase");
        }
        
        else{        
            Serial.print("Error subiendo datos: ");        
            Serial.println(fbdo.errorReason());        
        }        
        delay(3000);*/
        // Enviar los datos a Firebase
        if (Firebase.RTDB.setFloat(&fbdo,"/esp/temperature", temperatura) && Firebase.RTDB.setFloat(&fbdo,"/esp/humidity", humedad)) {
          Serial.println("Datos enviados a Firebase");
        } else {
          Serial.println("Error al enviar los datos a Firebase");
        }
        delay(3000);
        
    }

    if (millis() - lastTime > 60 * 500)  { 
        lastTime = millis();
    }

}
