#ifndef __sensor_H
#define __sensor_H	
#include "sys.h" 

short Get_Temprate(void);
void display_temp(void);
extern short MQ_4,MQ_2,fire,ledd,beepp,receivedData,people;
extern char buffer[200],dataStr[10];
extern char message[100];
extern short temp; 
extern uint16_t adcY;

// 定义定时器2的周期和分频系数，以产生1ms的定时中断
#define TIM2_PERIOD     8399
#define TIM2_PRESCALER  9    // 84MHz / (TIM2_PERIOD+1) / (TIM2_PRESCALER+1) = 1kHz
// 定义检测火焰的阈值
#define FLAME_THRESHOLD 2000


#define LSENS_READ_TIMES	10		//定义光敏传感器读取次数,读这么多次,然后取平均值
 
void Lsens_Init(void); 				//初始化光敏传感器
u8 Lsens_Get_Val(void);				//读取光敏传感器的值


void fire_value_control(void);              //火焰传感器控制
float Get_MQ4Value(void);                   //甲烷传感器
float MQ2_GetPPM(void);                     //获取烟雾传感器

uint16_t USART2_Receive(void);              //串口接收
void data(void);


#endif

