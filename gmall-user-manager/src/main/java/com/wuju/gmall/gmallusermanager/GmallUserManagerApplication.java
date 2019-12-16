package com.wuju.gmall.gmallusermanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("com.wuju.gmall.gmallusermanager.mapper")
@SpringBootApplication
@ComponentScan(basePackages = "com.wuju.gmall")
public class GmallUserManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallUserManagerApplication.class, args);
    }

}
