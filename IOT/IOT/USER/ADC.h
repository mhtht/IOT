#ifndef __ADC_H
#define __ADC_H	
#include "sys.h" 

	
#define ADC_CH5  		5 		 	//ͨ��5	   	    
	   									   
void Adc_Init(void); 				//ADCͨ����ʼ��
u16  Get_Adc(u8 ch); 				//���ĳ��ͨ��ֵ 
u16 Get_Adc_Average(u8 ch,u8 times);//�õ�ĳ��ͨ����������������ƽ��ֵ  



void  Adc3_Init(void);
u16  Get_Adc3(u8 ch); 				//���ĳ��ͨ��ֵ 
u16 Get_Adc_Average(u8 ch,u8 times);//�õ�ĳ��ͨ����������������ƽ��ֵ 

#define FLAME_SENSOR_PIN GPIO_Pin_6
#define FLAME_SENSOR_GPIO_PORT GPIOB

#endif 
