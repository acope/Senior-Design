void setup() {
Serial.begin(115200);

}

void loop() {
    Serial.write("T");
    if(Serial.available() > 0){
      char data = Serial.read(); //Read byte of data
      Serial.write(data);
    }
    //Needed or else Ardulink has an overflow
    Serial.write(255);
}
