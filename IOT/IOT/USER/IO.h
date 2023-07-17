#ifndef __IO_H
#define __IO_H
#include "sys.h"
#include "stdio.h"	
#include "stm32f4xx_conf.h"

extern char buf[100];
extern u8 USART2_RX_BUF_FLAG;
extern uint8_t voice_flag;
extern uint8_t USART2_RX_DATA;

#define USART2_RX_BUF_SIZE 64


//LED端口
#define LED0 PFout(9)	// DS0
#define LED1 PFout(10)	// DS1	 
void LED_Init(void);//初始化LED



/*按键端口设置*/
#define KEY0 		GPIO_ReadInputDataBit(GPIOE,GPIO_Pin_4) //PE4
#define KEY1 		GPIO_ReadInputDataBit(GPIOE,GPIO_Pin_3)	//PE3 
#define KEY2 		GPIO_ReadInputDataBit(GPIOE,GPIO_Pin_2) //PE2
#define WK_UP 	    GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_0)	//PA0


/*按键端口设置*/
/*
#define KEY0 		PEin(4)   	//PE4
#define KEY1 		PEin(3)		//PE3 
#define KEY2 		PEin(2)		//P32
#define WK_UP 	PAin(0)		//PA0
*/


#define KEY0_PRES 	1
#define KEY1_PRES	2
#define KEY2_PRES	3
#define WKUP_PRES   4
void KEY_Init(void);	//按键初始化
uint8_t KEY_Scan(u8);  		//按键检测


#define BEEP PFout(8)	// 蜂鸣器
void BEEP_Init(void);//初始化蜂鸣器	


void PC1_init(void);   //PA1初始化
void PC2_init(void);
void PC3_init(void);


void UART2_Init(void);
//void UART2_SendChar(char ch);
//void UART2_SendString(char* str);

void USART_SendString(USART_TypeDef* USARTx, char* str);
void USART2_ReceiveString(char* buffer, uint16_t bufferSize);
void USART2_ReceiveData(uint8_t *data, uint16_t *length);
uint8_t uart2_data(void);

void PA6_init(void);




#endif
