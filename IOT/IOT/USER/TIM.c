#include "TIM.h"
#include "stm32f4xx.h"
#include "sensor.h"

#define SERVO_GPIO_PORT     GPIOA
#define SERVO_GPIO_PIN      GPIO_Pin_15
#define SERVO_GPIO_PIN_SRC  GPIO_PinSource5

#define TIM_SERVO           TIM2
#define TIM_SERVO_CLK       RCC_APB1Periph_TIM2
#define TIM_SERVO_PRESCALER 83
#define TIM_SERVO_PERIOD    19999

uint8_t dataReceived = 0;


void TIM4_Init(uint32_t arr, uint32_t psc)
{
    TIM_TimeBaseInitTypeDef TIM_TimeBaseStructure;
    NVIC_InitTypeDef NVIC_InitStructure;
    GPIO_InitTypeDef GPIO_InitStructure;

    /* 继电器配置 */
    RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOB, ENABLE);

    GPIO_InitStructure.GPIO_Pin = GPIO_Pin_0 | GPIO_Pin_2 | GPIO_Pin_5;
    GPIO_InitStructure.GPIO_Mode = GPIO_Mode_OUT;
    GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
    GPIO_InitStructure.GPIO_OType = GPIO_OType_PP;
    GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_UP;
    GPIO_Init(GPIOB, &GPIO_InitStructure);

    /* TIM4的配置 */
    RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM4, ENABLE);

    TIM_TimeBaseStructure.TIM_Period = arr - 1;
    TIM_TimeBaseStructure.TIM_Prescaler = psc - 1;
    TIM_TimeBaseStructure.TIM_ClockDivision = 0;
    TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up;
    TIM_TimeBaseInit(TIM4, &TIM_TimeBaseStructure);

    NVIC_InitStructure.NVIC_IRQChannel = TIM4_IRQn;
    NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 1;
    NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;
    NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
    NVIC_Init(&NVIC_InitStructure);

    TIM_ITConfig(TIM4, TIM_IT_Update, ENABLE);

    TIM_Cmd(TIM4, ENABLE);
}

void GPIO_Configuration(void)
{
    GPIO_InitTypeDef GPIO_InitStructure;

    /* 设置舵机引脚为复用推挽输出 */
    GPIO_InitStructure.GPIO_Pin = SERVO_GPIO_PIN;
    GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF;
    GPIO_InitStructure.GPIO_OType = GPIO_OType_PP;
    GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_NOPULL;
    GPIO_InitStructure.GPIO_Speed = GPIO_Speed_100MHz;
    GPIO_Init(SERVO_GPIO_PORT, &GPIO_InitStructure);

    /* 将引脚与定时器功能映射 */
    GPIO_PinAFConfig(SERVO_GPIO_PORT, SERVO_GPIO_PIN_SRC, GPIO_AF_TIM2);
}

void TIM4_IRQHandler(void)
{
    if (TIM_GetITStatus(TIM4, TIM_IT_Update) != RESET)
    {
        data();
        
        TIM_ClearITPendingBit(TIM4, TIM_IT_Update);  // 清除中断标志位
    }
}





void TIM2_Init(void)
{
    TIM_TimeBaseInitTypeDef TIM_TimeBaseStructure;
    TIM_ICInitTypeDef TIM_ICInitStructure;
    NVIC_InitTypeDef NVIC_InitStructure;
    GPIO_InitTypeDef GPIO_InitStructure;

    RCC_APB1PeriphClockCmd(TIM_SERVO_CLK, ENABLE);
    RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOA, ENABLE);

    /* 配置PA3为输入模式 */
    GPIO_InitStructure.GPIO_Pin = GPIO_Pin_3;
    GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN;
    GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_NOPULL;
    GPIO_Init(GPIOA, &GPIO_InitStructure);

    /* 设置定时器2的基本参数 */
    TIM_TimeBaseStructure.TIM_Prescaler = TIM_SERVO_PRESCALER - 1;
    TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up;
    TIM_TimeBaseStructure.TIM_Period = TIM_SERVO_PERIOD - 1;
    TIM_TimeBaseStructure.TIM_ClockDivision = TIM_CKD_DIV1;
    TIM_TimeBaseInit(TIM_SERVO, &TIM_TimeBaseStructure);

    /* 配置定时器2的输入捕获通道 */
    TIM_ICInitStructure.TIM_Channel = TIM_Channel_4;
    TIM_ICInitStructure.TIM_ICPolarity = TIM_ICPolarity_Falling;
    TIM_ICInitStructure.TIM_ICSelection = TIM_ICSelection_DirectTI;
    TIM_ICInitStructure.TIM_ICPrescaler = TIM_ICPSC_DIV1;
    TIM_ICInitStructure.TIM_ICFilter = 0x0;
    TIM_ICInit(TIM_SERVO, &TIM_ICInitStructure);

    /* 配置定时器2的中断 */
    NVIC_InitStructure.NVIC_IRQChannel = TIM2_IRQn;
    NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 2;
    NVIC_InitStructure.NVIC_IRQChannelSubPriority = 3;
    NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
    NVIC_Init(&NVIC_InitStructure);

    /* 使能定时器2的更新中断 */
    TIM_ITConfig(TIM_SERVO, TIM_IT_Update, ENABLE);

    /* 使能定时器2的输入捕获中断 */
    TIM_ITConfig(TIM_SERVO, TIM_IT_CC4, DISABLE);

    /* 启动定时器2 */
    TIM_Cmd(TIM_SERVO, ENABLE);
}

void TIM2_IRQHandler(void)
{
    if (TIM_GetITStatus(TIM_SERVO, TIM_IT_Update) != RESET)
    {
        // 处理定时器溢出中断
        TIM_ClearITPendingBit(TIM_SERVO, TIM_IT_Update);  // 清除中断标志位
    }

    if (TIM_GetITStatus(TIM_SERVO, TIM_IT_CC4) != RESET)
    {
        // 处理输入捕获中断
        TIM_ClearITPendingBit(TIM_SERVO, TIM_IT_CC4);  // 清除中断标志位
    }
}

