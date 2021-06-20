#include <SoftwareSerial.h>

#define BT_RXD 8
#define BT_TXD 7

SoftwareSerial bluetooth(BT_RXD, BT_TXD);

const int motorEnaPin = 10;
const int motorDirPin = 9;
const int motorPWMPin = 11;
const int encoderPinA = 2;
const int encoderPinB = 3;
const int pdlc = 12;

int encoderPos = 0;
int count = 0;
int automode = 0;
int rate = 5600;

String buffer0;

void doEncoderA(){ // 빨녹일 때
  encoderPos += (digitalRead(encoderPinA)==digitalRead(encoderPinB))?1:-1;
   if(encoderPos%rate==0){
   count ++;
   //Serial.print("A   ");
   //Serial.println(encoderPos/rate);
   }
   if(count==2){
    autoMotor(LOW, LOW);
  }
}

void doEncoderB(){ // 보파일 때
  encoderPos += (digitalRead(encoderPinA)==digitalRead(encoderPinB))?-1:1;
  if(encoderPos%rate==0){
    count ++;
    //Serial.print("B   ");
    //Serial.println(encoderPos/rate);
  }
  if(count==2){
    autoMotor(LOW, LOW);
  }
}

void autoMotor(bool dir, bool pwm){
  encoderPos = 0;
  count = 0;
  digitalWrite(motorDirPin, dir);
  digitalWrite(motorPWMPin, pwm);
  delay(5000);
}

void handMotor(bool dir, bool pwm){
  encoderPos = 0;
  count = 0;
  digitalWrite(motorDirPin, dir);
  digitalWrite(motorPWMPin, pwm);
}


void setup() {
  pinMode(motorDirPin, OUTPUT); // pinMode 설정은 digital만 필요하고 PWM을 사용할 analog는 필요 없음.
  pinMode(motorEnaPin, OUTPUT); // pinMode 설정은 digital만 필요하고 PWM을 사용할 analog는 필요 없음.
  pinMode(motorPWMPin, OUTPUT);
  pinMode(pdlc, OUTPUT);
  digitalWrite(motorEnaPin, HIGH);
  pinMode(encoderPinA, INPUT_PULLUP);
  attachInterrupt(0, doEncoderA, CHANGE);
 
  pinMode(encoderPinB, INPUT_PULLUP);
  attachInterrupt(1, doEncoderB, CHANGE);

  Serial.begin(9600);
  bluetooth.begin(9600);
}
void loop() {
  while(bluetooth.available()){
    char val = (char)bluetooth.read();
    buffer0 += val;
    if(val == '\n'){
      Serial.print(buffer0);
      Serial.print(rate);
      int inst = atoi(buffer0.c_str());
      buffer0 = "";
      if (inst == 1){ // auto open
        automode = 1; 
      }
      if (inst == 2){
        automode = 0;
      }
      if (automode == 1){
        if (inst == 3){ // auto open
        autoMotor(LOW, HIGH);
        }
        if (inst == 4){ // auto close
        autoMotor(HIGH, HIGH); 
        }
      }
      if (automode == 0){
        if (inst == 5){ // pdlc on
          for(int i = 0; i<255;i++){
          analogWrite(pdlc,i);
          }
        }
        if (inst == 6){ // pdlc off
          for(int i = 255; i>0;i--){
          analogWrite(pdlc,i);
          }
        }
        if (inst == 7){ // hand open
        handMotor(LOW,HIGH);
        }
        if (inst == 8){ // hand close
        handMotor(HIGH,HIGH);
        }
        if (inst == 9){ // hand stop
        handMotor(LOW,LOW);
        }
        if (inst > 5600){
          rate = inst;
        }
      } 
    }
  }
}
