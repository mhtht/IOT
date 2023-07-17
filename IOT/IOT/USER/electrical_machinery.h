#ifndef _electrical_machinery_h
#define _electrical_machinery_h


void TIM3_Init(uint32_t arr,uint32_t psc);
void TIM3_IRQHandler(uint8_t dir);
//void Electrical_Machinery_Control(uint8_t duty_cycle);
void Electrical_Machinery_Control(void);
void Electrical_Machinery_Control_fan(void);
void FAN(void);

#endif

