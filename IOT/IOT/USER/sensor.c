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
uint16_t x = (30 + 10 * 8) + 50; // x����
uint16_t y = 600; // y����
uint8_t size = 16; // �����С
uint16_t i;


//�õ��¶�ֵ
//����ֵ:�¶�ֵ(������100��,��λ:��.)






short Get_Temprate(void)
{
	u32 adcx;
	short result;
 	double temperate;
	adcx=Get_Adc_Average(ADC_Channel_16,10);	//��ȡͨ��16�ڲ��¶ȴ�����ͨ��,10��ȡƽ��
	temperate=(float)adcx*(3.3/4096);			//��ѹֵ
	temperate=(temperate-0.76)/0.0025 + 25; 	//ת��Ϊ�¶�ֵ 
	result=temperate*=100;						//����100��.
	return result;
}




void data(void)
{
	adcY=Lsens_Get_Val();   //�õ�����ֵ
	temp=Get_Temprate();	//�õ��¶�ֵ
	MQ_44=Get_MQ4Value();    //�õ�����ֵ
	MQ_24=MQ2_GetPPM();      //�õ�����ֵ

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
		LCD_ShowString(160,140,16,16,24,"-");	    //��ʾ����
	}else LCD_ShowString(160,140,16,16,24," ");		//�޷���


	LCD_ShowxNum(150+10,140,(temp/100)-11,2,24,0);		//��ʾ��������
	LCD_ShowxNum(185+10,140,temp%100,2,24,0);		//��ʾС������
	
	
	LCD_ShowxNum(150+10,180,(adcY*10),3,24,0);//��ʾADC��ֵ 	
	/*���鴫����*/
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
	/*��������ֵ*/
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
        // ���洫������⵽���棬ִ�б�������
        LED1=0;
		BEEP=1;
		LCD_ShowString((30+10*8)+50,300,200,16,24,"Fire, please call the police");
		fire = 0;
    }
    else
    {
        // ���洫����δ��⵽���棬ȡ������״̬
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
			GPIO_SetBits(GPIOC, GPIO_Pin_1);                  //����
		}
		if(buf[0]==0x55 && buf[1]==0x02 && buf[2]==0x20 && buf[3]==0x22 && buf[4]==0xbb)
		{
			GPIO_ResetBits(GPIOC, GPIO_Pin_1);                //����
		}

		if(buf[0]==0x55 && buf[1]==0x03 && buf[2]==0x21 && buf[3]==0x24 && buf[4]==0xbb)
		{
			GPIO_SetBits(GPIOC, GPIO_Pin_2);                  //����
		}
		if(buf[0]==0x55 && buf[1]==0x03 && buf[2]==0x20 && buf[3]==0x23 && buf[4]==0xbb)
		{
			GPIO_ResetBits(GPIOC, GPIO_Pin_2);                //����
		}

		if(buf[0]==0x55 && buf[1]==0x04 && buf[2]==0x21 && buf[3]==0x25 && buf[4]==0xbb)
		{
			GPIO_SetBits(GPIOC, GPIO_Pin_3);                  //����
		}
		if(buf[0]==0x55 && buf[1]==0x04 && buf[2]==0x20 && buf[3]==0x24 && buf[4]==0xbb)
		{
			GPIO_ResetBits(GPIOC, GPIO_Pin_3);                //����
		}

}




uint16_t USART2_Receive(void)
{
 	while (!(USART2->SR & USART_SR_RXNE)); // �ȴ����ջ������ǿ�
    return USART2->DR; // �ӽ��ռĴ����ж�ȡ����
	
}




/*��ȡ���մ�����*/
void Lsens_Init(void)
{
  GPIO_InitTypeDef  GPIO_InitStructure;
  RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOF, ENABLE);//ʹ��GPIOFʱ��
	
  //�ȳ�ʼ��ADC3ͨ��7IO��
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_7;		//PA7 ͨ��7
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AN;	//ģ������
  GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_NOPULL ;//����������
  GPIO_Init(GPIOF, &GPIO_InitStructure);//��ʼ��  
  //Adc3_Init();//��ʼ��ADC3
}




//��ȡLight Sens��ֵ
//0~100:0,�;100,���� 
u8 Lsens_Get_Val(void)
{
	u32 temp_val=0;
	u8 t;
	for(t=0;t<LSENS_READ_TIMES;t++)
	{
		temp_val+=Get_Adc3(ADC_Channel_5);	//��ȡADCֵ,ͨ��5
		delay_ms(5);
	}
	temp_val/=LSENS_READ_TIMES;//�õ�ƽ��ֵ 
	if(temp_val>4000)temp_val=4000;
	return (u8)(100-(temp_val/40));
}


/*��ȡ��������*/
float Get_MQ4Value(void)
{
	uint16_t ADCVal;
	float Voltage;
	float ppm;
	ADCVal = Get_Adc_Average(ADC_Channel_0,10);
	Voltage = ADCVal * 3.3 / 4096;
	//����Ȼ���Ļ����£�ʵ��AOUT�˵ĵ�ѹΪ0.5V������⵽��Ȼ��ʱ����ѹÿ����0.1V,ʵ�ʱ�������Ũ������200ppm
	ppm = (Voltage - 0.5) / 0.1 * 200;
	return ppm;
			//return ADCVal;
}
 

/*��ȡ����ֵ*/
float MQ2_GetPPM(void)
{   
	float adc_voltage, mq2_voltage, mq2_ppm;
// ��ȡADC����ֵ
	uint16_t adc_value;
	adc_value=Get_Adc_Average(ADC_Channel_4,10);
// ����ADC�����ѹ
	adc_voltage = adc_value * (3.3 / 4095.0);
// ����MQ-2�����ѹ
	mq2_voltage = adc_voltage * 5 / 3.3;
// ����MQ-2��PPMֵ
	mq2_ppm = (mq2_voltage - 0.4) * 10000 / 3.6;
	return mq2_ppm;
}

