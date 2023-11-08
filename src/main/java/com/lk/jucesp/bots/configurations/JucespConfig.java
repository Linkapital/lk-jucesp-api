package com.lk.jucesp.bots.configurations;

import com.lk.captcha.CaptchaSolver;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({CaptchaSolver.class})
@ComponentScan(value = "com.lk.jucesp.bots.components")
@Configuration
public class JucespConfig {

}
