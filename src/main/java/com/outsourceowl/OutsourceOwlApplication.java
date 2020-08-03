package com.outsourceowl;

import com.outsourceowl.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class OutsourceOwlApplication {

  public static void main(String[] args) {
    SpringApplication.run(OutsourceOwlApplication.class, args);
  }
}
