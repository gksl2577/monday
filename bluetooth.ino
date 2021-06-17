#include <SoftwareSerial.h>

#define BT_RXD 8
#define BT_TXD 7

SoftwareSerial bluetooth(BT_RXD, BT_TXD);

const int motorEnaPin = 10;
const int motorDirPin = 9;
const int motorPWMPin = 11;

const int encoderPinA = 2;
const int encoderPinB = 3;

int encoderPos = 0;
int count = 0;

void doEncoderA(){ // 빨녹일 때
  encoderPos += (digitalRead(encoderPinA)==digitalRead(encoderPinB))?1:-1;
   if(encoderPos%7800==0){
   count ++;
   Serial.print("A   ");
   Serial.println(encoderPos/7800);
   }
   if(count==9){
    doMotor(LOW, LOW);
  }
}

void doEncoderB(){ // 보파일 때
  encoderPos += (digitalRead(encoderPinA)==digitalRead(encoderPinB))?-1:1;
  if(encoderPos%7800==0){
    count ++;
    Serial.print("B   ");
    Serial.println(encoderPos/7800);
  }
  if(count==9){
    doMotor(LOW, LOW);
  }
}

void doMotor(bool dir, bool pwm){
  encoderPos = 0;
  count = 0;
  digitalWrite(motorDirPin, dir);
  digitalWrite(motorPWMPin, pwm);
}


void setup() {
  pinMode(motorDirPin, OUTPUT); // pinMode 설정은 digital만 필요하고 PWM을 사용할 analog는 필요 없음.
  pinMode(motorEnaPin, OUTPUT); // pinMode 설정은 digital만 필요하고 PWM을 사용할 analog는 필요 없음.
  pinMode(motorPWMPin, OUTPUT);
  digitalWrite(motorEnaPin, HIGH);
  pinMode(encoderPinA, INPUT_PULLUP);
  attachInterrupt(0, doEncoderA, CHANGE);
 
  pinMode(encoderPinB, INPUT_PULLUP);
  attachInterrupt(1, doEncoderB, CHANGE);

  Serial.begin(9600);
  bluetooth.begin(9600);
}

void loop() {
  if(bluetooth.available()){
    char val = bluetooth.read();
    if (val == '1'){
      doMotor(HIGH, HIGH); 
    }
    if (val == '2'){
      doMotor(LOW, HIGH); 
    }
  }
}
