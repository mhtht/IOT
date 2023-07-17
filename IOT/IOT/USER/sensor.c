#include "sensor.h"
#include "ADC.h"
#include "lcd.h"
#include "delay.h"
#include "IO.h"
#include "stm32f4xx_gpio.h"
#include "stm32f4xx.h"
#include "usart.h"
#include "stdio.h"
volatile uint32_t tim2_counter = 0;
short MQ_44,MQ_24,MQ_4,MQ_2,fire,ledd,beepp,receivedData,people;
char buffer[200],dataStr[10];
char message[100];
short temp; 
uint16_t adcY;


uint16_t receivedLength = 0;

uint16_t DATA;
uint16_t x = (30 + 10 * 8) + 50; // x坐标
uint16_t y = 600; // y坐标
uint8_t size = 16; // 字体大小
uint16_t i;


//得到温度值
//返回值:温度值(扩大了100倍,单位:℃.)






short Get_Temprate(void)
{
	u32 adcx;
	short result;
 	double temperate;
	adcx=Get_Adc_Average(ADC_Channel_16,10);	//读取通道16内部温度传感器通道,10次取平均
	temperate=(float)adcx*(3.3/4096);			//电压值
	temperate=(temperate-0.76)/0.0025 + 25; 	//转换为温度值 
	result=temperate*=100;						//扩大100倍.
	return result;
}




void data(void)
{
	adcY=Lsens_Get_Val();   //得到光照值
	temp=Get_Temprate();	//得到温度值
	MQ_44=Get_MQ4Value();    //得到甲烷值
	MQ_24=MQ2_GetPPM();      //得到烟雾值

	if(GPIO_ReadInputDataBit(GPIOA, GPIO_Pin_6) == Bit_SET)
	{
		LCD_ShowString((30+10*8)+50,340,200,16,24,"SOMEONE      ");
		people = 1;
	}
	else
	{
		
		LCD_ShowString((30+10*8)+50,340,200,16,24,"NO ONE      ");
		people = 0;
	}

	
	

	if(temp<0)
	{
		temp=-temp;
		LCD_ShowString(160,140,16,16,24,"-");	    //显示负号
	}else LCD_ShowString(160,140,16,16,24," ");		//无符号


	LCD_ShowxNum(150+10,140,(temp/100)-11,2,24,0);		//显示整数部分
	LCD_ShowxNum(185+10,140,temp%100,2,24,0);		//显示小数部分
	
	
	LCD_ShowxNum(150+10,180,(adcY*10),3,24,0);//显示ADC的值 	
	/*甲烷传感器*/
	if (MQ_44>2700)
	{
		LCD_ShowString((30+10*8)+50,220,200,16,24,"Methane overshoot!       ");
		//BEEP=1;
		MQ_4=0;
		LED1=0;
	}
	else
	{
		LCD_ShowString((30+10*8)+50,220,200,16,24,"No methane was detected");
		//BEEP=0;
		MQ_4=1;
		LED1=1;
		
	}
	/*烟雾传感器值*/
	if (MQ_24>666)
	{
		LCD_ShowString((30+10*8)+50,260,200,16,24,"smoky fire                ");
		LED1=0;
		BEEP=1;
		MQ_2=0;
	}
	else
	{
		LCD_ShowString((30+10*8)+50,260,200,16,24,"No smoke was detected");
		LED1=1;
		MQ_2=1;
		BEEP=0;
	}

	if(BEEP==0)
	{
		LCD_ShowString(105,380,200,16,24,"OFF      ");
		beepp = 0;
	}
	else
	{
		LCD_ShowString(105,380,200,16,24,"WARN!!!");
		beepp = 1;
	}


	if(LED1==1)
	{
		LCD_ShowString(250+70,380,200,16,24,"OFF   ");
		ledd = 0;
	}
	else
	{
		LCD_ShowString(250+70,380,200,16,24,"ON    ");
		ledd = 1;
	}
	

	if (GPIO_ReadInputDataBit(FLAME_SENSOR_GPIO_PORT, FLAME_SENSOR_PIN) != Bit_SET)
    {
        // 火焰传感器检测到火焰，执行报警处理
        LED1=0;
		BEEP=1;
		LCD_ShowString((30+10*8)+50,300,200,16,24,"Fire, please call the police");
		fire = 0;
    }
    else
    {
        // 火焰传感器未检测到火焰，取消报警状态
		LCD_ShowString((30+10*8)+50,300,200,16,24,"No FIRE                         ");
        LED1=1;
		BEEP=0;
		fire = 1;
    }


	if(receivedData==1)
	{
		LED1=0;
		BEEP=1;
		receivedData=0;
	}
	else
	{
		LED1=1;
		BEEP=0;
		receivedData=0;
	}

	
	if(buf[0]==0x55 && buf[1]==0x01 && buf[2]==0x02 && buf[3]==0x03 && buf[4]==0xbb)
	{
		sprintf(buffer, "light=%d,temp=%d,MQ_4=%d,MQ_2=%d\n,Fire=%d,people=%d,lamp=%d,bee=%d\n", adcY, temp/100-5-6, MQ_4, MQ_2,fire,people,ledd,beepp);
		USART_SendString(USART2, buffer);
	}

	if(buf[0]==0x55 && buf[1]==0x02 && buf[2]==0x21 && buf[3]==0x23 && buf[4]==0xbb)
		{
			GPIO_SetBits(GPIOC, GPIO_Pin_1);                  //拉高
		}
		if(buf[0]==0x55 && buf[1]==0x02 && buf[2]==0x20 && buf[3]==0x22 && buf[4]==0xbb)
		{
			GPIO_ResetBits(GPIOC, GPIO_Pin_1);                //拉低
		}

		if(buf[0]==0x55 && buf[1]==0x03 && buf[2]==0x21 && buf[3]==0x24 && buf[4]==0xbb)
		{
			GPIO_SetBits(GPIOC, GPIO_Pin_2);                  //拉高
		}
		if(buf[0]==0x55 && buf[1]==0x03 && buf[2]==0x20 && buf[3]==0x23 && buf[4]==0xbb)
		{
			GPIO_ResetBits(GPIOC, GPIO_Pin_2);                //拉低
		}

		if(buf[0]==0x55 && buf[1]==0x04 && buf[2]==0x21 && buf[3]==0x25 && buf[4]==0xbb)
		{
			GPIO_SetBits(GPIOC, GPIO_Pin_3);                  //拉高
		}
		if(buf[0]==0x55 && buf[1]==0x04 && buf[2]==0x20 && buf[3]==0x24 && buf[4]==0xbb)
		{
			GPIO_ResetBits(GPIOC, GPIO_Pin_3);                //拉低
		}

}




uint16_t USART2_Receive(void)
{
 	while (!(USART2->SR & USART_SR_RXNE)); // 等待接收缓冲区非空
    return USART2->DR; // 从接收寄存器中读取数据
	
}




/*获取光照传感器*/
void Lsens_Init(void)
{
  GPIO_InitTypeDef  GPIO_InitStructure;
  RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOF, ENABLE);//使能GPIOF时钟
	
  //先初始化ADC3通道7IO口
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_7;		//PA7 通道7
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AN;	//模拟输入
  GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_NOPULL ;//不带上下拉
  GPIO_Init(GPIOF, &GPIO_InitStructure);//初始化  
  //Adc3_Init();//初始化ADC3
}




//读取Light Sens的值
//0~100:0,最暗;100,最亮 
u8 Lsens_Get_Val(void)
{
	u32 temp_val=0;
	u8 t;
	for(t=0;t<LSENS_READ_TIMES;t++)
	{
		temp_val+=Get_Adc3(ADC_Channel_5);	//读取ADC值,通道5
		delay_ms(5);
	}
	temp_val/=LSENS_READ_TIMES;//得到平均值 
	if(temp_val>4000)temp_val=4000;
	return (u8)(100-(temp_val/40));
}


/*获取甲烷数据*/
float Get_MQ4Value(void)
{
	uint16_t ADCVal;
	float Voltage;
	float ppm;
	ADCVal = Get_Adc_Average(ADC_Channel_0,10);
	Voltage = ADCVal * 3.3 / 4096;
	//无天然气的环境下，实测AOUT端的电压为0.5V，当检测到天然气时，电压每升高0.1V,实际被测气体浓度增加200ppm
	ppm = (Voltage - 0.5) / 0.1 * 200;
	return ppm;
			//return ADCVal;
}
 

/*获取烟雾值*/
float MQ2_GetPPM(void)
{   
	float adc_voltage, mq2_voltage, mq2_ppm;
// 读取ADC采样值
	uint16_t adc_value;
	adc_value=Get_Adc_Average(ADC_Channel_4,10);
// 计算ADC输入电压
	adc_voltage = adc_value * (3.3 / 4095.0);
// 计算MQ-2输出电压
	mq2_voltage = adc_voltage * 5 / 3.3;
// 计算MQ-2的PPM值
	mq2_ppm = (mq2_voltage - 0.4) * 10000 / 3.6;
	return mq2_ppm;
}

