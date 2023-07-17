#ifndef __TIM_H
#define __TIM_H	
#include "sys.h" 
#include "sensor.h"
#include "ADC.h"
#include "lcd.h"
#include "delay.h"
#include "IO.h"
	
void TIM4_Init(uint32_t arr, uint32_t psc);
void TIM2_Init(void);                                         //定时器2初始化
void GPIO_Configuration(void);
void TIM2_Init(void);
#endif 

