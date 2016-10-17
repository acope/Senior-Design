void setup() {
  //Serial.begin(115200);
  
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }  
  establishContact();  // send a byte to establish contact until receiver responds
}

void loop() {

}

void establishContact() {
  boolean a = false;
  
  while(a == false){
    while (Serial.available() <= 0) {
      Serial.print('T');   // send a capital T
      delay(300);
    }
    
    
    if(Serial.read() == 'A'){
      a = true;
    }
  }
}
