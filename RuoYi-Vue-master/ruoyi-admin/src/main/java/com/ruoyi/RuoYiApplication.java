package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RuoYiApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  扬子石化体系三基融合工作平台启动成功  ლ(´ڡ`ლ)ﾞ  \n" +
                "\\        /   ZZZZZZZZ\n" +
                " \\      /           Z\n" +
                "  \\    /           Z\n" +
                "   \\  /           Z\n" +
                "    \\/           Z\n" +
                "    ||          Z\n" +
                "    ||         Z\n" +
                "    ||         ZZZZZZZZ");
    }
}
