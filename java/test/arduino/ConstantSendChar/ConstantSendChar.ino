void setup() {
Serial.begin(115200);

}

void loop() {
    Serial.write("Test");
    //Needed or else Ardulink has an overflow
    Serial.write(255);
    delay(500);
}
