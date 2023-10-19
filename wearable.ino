//librerias para utilizar el sensor MPU6050
#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>
#include <Wire.h>
#include <list>
//librerias para usar WiFi
#include <WiFi.h>
#include <HTTPClient.h>
//OFFSETS
#include <MPU6050.h>
MPU6050 mpuOF;
const int numSamples = 100;                         //numSamples Number of samples to average for offset calculation
int16_t offsetAccX, offsetAccY, offsetAccZ;
int16_t offsetGyroX, offsetGyroY, offsetGyroZ;

using namespace std;
//credenciales de la red
const char* ssid = "Nombre de la red";
const char* password = "Clave de la";
//variables
int res;
String encender = "0";
String idw = "w003";

std::list<float> lax;
std::list<float> lay;
std::list<float> laz;
std::list<float> lgx;
std::list<float> lgy;
std::list<float> lgz;

//creamos los iteradores de cada lista
list<float>::iterator it_lax = lax.begin();
list<float>::iterator it_lay = lay.begin();
list<float>::iterator it_laz = laz.begin();
list<float>::iterator it_lgx = lgx.begin();
list<float>::iterator it_lgy = lgy.begin();
list<float>::iterator it_lgz = lgz.begin();

String ax;
String ay;
String az;
String gx;
String gy;
String gz;

Adafruit_MPU6050 mpu;

void setup(void) {
  Serial.begin(115200);
  while (!Serial)
    delay(10);

  Serial.println("Sistema IoT-WBAN");

  //Configuracion WiFi
  WiFi.begin(ssid, password);
  Serial.print("Conectando");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("Conexion establecida");
  Serial.print("IP local: ");
  Serial.println(WiFi.localIP());
  //------------------------

  //-------mpuOF--------------------
  mpuOF.initialize();
  // Tiempo para que el sensor se estabilice.
  delay(1000);
  // Calcular offset del acelerometro 
  offsetAccX = offsetAccY = offsetAccZ = 0;
  for (int i = 0; i < numSamples; i++) {
    int16_t accX, accY, accZ;
    mpuOF.getAcceleration(&accX, &accY, &accZ);
    
    offsetAccX += accX;
    offsetAccY += accY;
    offsetAccZ += accZ;
    
    delay(10);
  }
  offsetAccX /= numSamples;
  offsetAccY /= numSamples;
  offsetAccZ /= numSamples;
  
  // Calcular offset del giroscopio
  offsetGyroX = offsetGyroY = offsetGyroZ = 0;
  for (int i = 0; i < numSamples; i++) {
    int16_t gyroX, gyroY, gyroZ;
    mpuOF.getRotation(&gyroX, &gyroY, &gyroZ);
    
    offsetGyroX += gyroX;
    offsetGyroY += gyroY;
    offsetGyroZ += gyroZ;
    
    delay(10);
  }
  offsetGyroX /= numSamples;
  offsetGyroY /= numSamples;
  offsetGyroZ /= numSamples;

  //--------------------------------
  // Inicializar el sensor MPU6050
  if (!mpu.begin()) {
    Serial.println(" Error al encontrar el sensor MPU6050");
    while (1) {
      delay(10);
    }
  }

  Serial.println("MPU6050 encontrado");

  mpu.setAccelerometerRange(MPU6050_RANGE_2_G);
  Serial.print("Accelerometer range set to: ");
  mpu.setGyroRange(MPU6050_RANGE_1000_DEG);
  Serial.print("Gyro range set to: ");
  mpu.setFilterBandwidth(MPU6050_BAND_260_HZ);
  Serial.print("Filter bandwidth set to: ");

  Serial.println("");
  delay(100);
}

void loop() {

  res = estado();
  int m = 600;

  if (res == 1) {
    do {
      //Leer datos con el sensor
      leerDatos();
      m = m - 1;
    } while (m > 0);
    cambiarEstado();

    it_lax = lax.begin();
    it_lay = lay.begin();
    it_laz = laz.begin();
    it_lgx = lgx.begin();
    it_lgy = lgy.begin();
    it_lgz = lgz.begin();

    ax = '@';
    ay = '@';
    az = '@';
    gx = '@';
    gy = '@';
    gz = '@';

    while (it_lax != lax.end()) {
      ax = ax + String(*it_lax++) + "@";
      ay = ay + String(*it_lay++) + "@";
      az = az + String(*it_laz++) + "@";
      gx = gx + String(*it_lgx++) + "@";
      gy = gy + String(*it_lgy++) + "@";
      gz = gz + String(*it_lgz++) + "@";
    }

    //enviar datos a servidor
    enviarDatos();

    //limpiar listas
    lax.clear();
    lay.clear();
    laz.clear();
    lgx.clear();
    lgy.clear();
    lgz.clear();
  } else {
    Serial.println("wearable apagado");
  }
}

//verificamos si esta encendido o no el wearable
int estado() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;  //creo objeto
    //creo la estructura para enviar los datos
    String datosEnviar = "idw=" + idw;

    http.begin("http://192.168.18.68/sistemaIoTWBAN/estado.php");//direccion del servidor
    http.addHeader("Content-Type", "application/x-www-form-urlencoded");  //defino la estructura en la que se enviaran los datos

    int respuesta = http.POST(datosEnviar);  //enviar con metodo POST y capturar respuesta

    if (respuesta > 0) {
      Serial.println("codigo http: " + String(respuesta));
      if (respuesta == 200) {
        String cuerpoRespuesta = http.getString();
        return cuerpoRespuesta.toInt();
      }
    } else {
      Serial.print("error en estado, codigo: ");
      Serial.println(respuesta);
    }
    http.end();
  } else {
    Serial.println("error conexion WiFi");
  }
  delay(10);
  return 0;
}

//apagamos el wearable
void cambiarEstado() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;  //creo objeto
    //creo la estructura para enviar los datos
    String datosEnviar = "idw=" + idw + "&encender=" + encender;

    http.begin("http://192.168.18.68/sistemaIoTWBAN/cambiarEstado.php");//direccion del servidor
    http.addHeader("Content-Type", "application/x-www-form-urlencoded");  //defino la estructura en la que se enviaran los datos

    int respuesta = http.POST(datosEnviar);  //enviar con metodo POST y capturar respuesta

    if (respuesta > 0) {
      Serial.println("codigo http: " + String(respuesta));
      if (respuesta == 200) {
        String cuerpoRespuesta = http.getString();
      }
    } else {
      Serial.print("error en cambiarEstado, codigo: ");
      Serial.println(respuesta);
    }
    http.end();
  } else {
    Serial.println("error conexion WiFi");
  }
  delay(10);
}

//tomar los datos
void leerDatos() {
  int16_t accX, accY, accZ;
  int16_t gyroX, gyroY, gyroZ;
  //Obtenga nuevos eventos del sensor con las lecturas
  sensors_event_t a, g, temp;
  mpu.getEvent(&a, &g, &temp);

  accX =a.acceleration.x;
  accY =a.acceleration.y;
  accZ =a.acceleration.z;
  gyroX = g.gyro.x;
  gyroY = g.gyro.y;
  gyroZ = g.gyro.z;

  accX -= offsetAccX;
  accY -= offsetAccY;
  accZ -= offsetAccZ;
  gyroX -= offsetGyroX;
  gyroY -= offsetGyroY;
  gyroZ -= offsetGyroZ;

  lax.push_back(accX);
  lay.push_back(accY);
  laz.push_back(accZ);
  lgx.push_back(gyroX);
  lgy.push_back(gyroY);
  lgz.push_back(gyroZ);

  //Imprime los valores
  Serial.print("Aceleracion X: ");
  Serial.print(accX);
  Serial.print(", Y: ");
  Serial.print(accY);
  Serial.print(", Z: ");
  Serial.print(accZ);
  Serial.println(" m/s^2");

  Serial.print("Rotacion X: ");
  Serial.print(gyroX);
  Serial.print(", Y: ");
  Serial.print(gyroY);
  Serial.print(", Z: ");
  Serial.print(gyroZ);
  Serial.println(" rad/s");

  Serial.println("");
  delay(15);
}

//se envian las listas con los datos
void enviarDatos() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;  //creo objeto
    //creo la estructura para enviar los datos
    String datosEnviar = "idw=" + idw + "&ax=" + ax + "&ay=" + ay + "&az=" + az + "&gx=" + gx + "&gy=" + gy + "&gz=" + gz;

    http.begin("http://192.168.18.68/sistemaIoTWBAN/guardar.php");//direccion del servidor
    http.addHeader("Content-Type", "application/x-www-form-urlencoded");  //defino la estructura en la que se enviaran los datos

    int respuesta = http.POST(datosEnviar);  //enviar con metodo POST y capturar respuesta

    if (respuesta > 0) {
      Serial.println("codigo http: " + String(respuesta));
      if (respuesta == 200) {
        String cuerpoRespuesta = http.getString();
        Serial.println("respuesta del servidor: ");
        Serial.println(cuerpoRespuesta);
      }
    } else {
      Serial.print("error en guardar, codigo: ");
      Serial.println(respuesta);
    }
    http.end();
  } else {
    Serial.println("error conexion WiFi");
  }
  delay(10);
}
