void setup() {
Serial.begin(115200);

}

void loop() {
  while(1){
    Serial.write("T");
  }
}
