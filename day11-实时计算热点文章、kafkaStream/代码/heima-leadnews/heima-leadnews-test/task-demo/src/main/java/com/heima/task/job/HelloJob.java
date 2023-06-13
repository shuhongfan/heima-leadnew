package com.heima.task.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class HelloJob {

    @Scheduled(cron = "0/5 5/10 * * * ?") //每5秒执行一次
    public void sing(){
        System.out.println("当前执行时间："+new Date());
    }
}
