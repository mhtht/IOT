#include "stm32f4xx.h"
#include "usart.h"
#include "delay.h"
#include "electrical_machinery.h"
#include "sys.h"
#include "led.h"
#include "IO.h"
#include "lcd.h"
#include "ADC.h"
#include "TIM.h"
#include "sensor.h"



uint8_t t;
uint8_t len;	
uint16_t times=0;  

void display_Init(void);
/*
void relay(void)
{
	short temp_value=Get_Temprate()/100;
	short light_value=Lsens_Get_Val();
	if(temp_value>26)
	{
		GPIO_SetBits(GPIOB,GPIO_Pin_0);
	}
	else
	{
		GPIO_ResetBits(GPIOB,GPIO_Pin_0);
	}
	if(light_value<60)
	{
		GPIO_SetBits(GPIOB,GPIO_Pin_2);
		delay_ms(1000);
		delay_ms(1000);
		delay_ms(1000);
		delay_ms(1000);
		GPIO_ResetBits(GPIOB,GPIO_Pin_2);
	}
	else if(light_value>60)
	{
		GPIO_ResetBits(GPIOB,GPIO_Pin_5);
		delay_ms(1000);
		delay_ms(1000);
		delay_ms(1000);
		delay_ms(1000);
		GPIO_SetBits(GPIOB,GPIO_Pin_5);
	}
	
}*/

void init()
{
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);//设置系统中断优先级分组2
	PC1_init();
	PC2_init();
	PC3_init();
	uart_init(115200);
	delay_init(84);
	LED_Init();
	LCD_Init();        		 //液晶初始化
	Adc_Init();         	 //内部温度传感器ADC初始化
	Adc3_Init();
	Lsens_Init();            //光照传感器 
	BEEP_Init();
	//TIM3_Init(1000,83);       //电机
	
	TIM4_Init(1000,83);
	GPIO_Configuration();
	PA6_init();
	UART2_Init();
  	display_Init();
}



void main(void)
{ 
	init();
    while(1)
    {
		
		
	}
		
	
}


void display_Init(void)
{
		POINT_COLOR=RED; 
		POINT_COLOR=BLUE;//设置字体为蓝色 
		LCD_ShowString(30,140,200,16,24,"TEMPERATE:");   LCD_ShowString(230,140,200,16,24,"Celsius");//先在固定位置显示小数点
		LCD_ShowString(30,180,200,16,24,"LSENS_VAL:");   LCD_ShowString(230,180,200,16,24,"lux");//光照传感器显示
		LCD_ShowString(30,220,200,16,24,"METHANE:");
		LCD_ShowString(30,260,200,16,24,"SMOKER:");
		LCD_ShowString(30,300,200,16,24,"FIRE:");
		LCD_ShowString(30,340,200,16,24,"PERSON:");
		LCD_ShowString(175+10,140,200,16,24,".");
		LCD_ShowString(30,380,200,16,24,"ALARM:");
		LCD_ShowString(250,380,200,16,24,"LAMP:");
		LCD_ShowString(30,420,200,16,24,"FAN:");
}

