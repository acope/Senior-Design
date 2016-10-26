void setup() {
Serial.begin(115200);

}

void loop() {  
  //Echo back data recieved
  if(Serial.available() > 0){
    char data = Serial.read();
    Serial.write(data);
    Serial.write(255);
  }

}
