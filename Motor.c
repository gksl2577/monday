#define MOTOR_PORT PORTC
#define MOTOR_DDR DDRC
#define PWM1_ON (MOTOR_PORT|=0x01)
#define PWM1_OFF (MOTOR_PORT&=0xFE)
#define DIR1_ON (MOTOR_PORT|=0x02)
#define DIR1_OFF (MOTOR_PORT&=0xFD)
#define ENABLE1_OFF (MOTOR_PORT|=0x04)
#define ENABLE1_ON (MOTOR_PORT&=0xFB)
#define BREAK1_ON (MOTOR_PORT|=0x08)
#define BREAK1_OFF (MOTOR_PORT&=0xF7)

void main (void)
{
    int i, d, count=1;
    init_devices();

    delay(1000);
    printf("DC 모터 구동 테스트₩n");
    delay(1000);

    MOTOR_DDR = 0xff; // 모터포트 초기화
    ENABLE1_ON;
    while(1){
        printf(" %d 정방향 회전₩n₩r", count++);
        LED_PORT = 0x01;
        DIR1_ON; // Ch1 정방향
        for(d=0; d<10; d++){ // 10 단계로 속도 조절
            for(i=0;i<1000;i++){ // 100 번 PWM 발생
                PWM1_ON; // PWM1 신호 1 
                delay(10-d);
                PWM1_OFF; // PWM1 신호 0
                delay(d);
            }
            if(d==2) BREAK1_ON; // 정지 (18200 Only)
        }
        BREAK1_OFF;
        printf(" %d 역방향 회전₩n₩r", count++);
        LED_PORT = 0x02;
        DIR1_OFF; // Ch1 역방향
        for(d=0; d<10; d++){ // 10 단계로 속도 조절
            for(i=0;i<1000;i++){ // 100 번 PWM 발생
                PWM1_ON; // PWM1 신호 1
                delay(10-d);
                PWM1_OFF; // PWM1 신호 0
                delay(d);
            }
        }
    }
}

// 시간 지연 함수
void delay(int n)
{
    volatile int i,j;
    for(i=0;i<n;i++)
    {
        for(j=0;j<100;j++);
    }
} 
