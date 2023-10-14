package com.team42.NHPS.api.pharmacy;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NhpsAppApiPharmacyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NhpsAppApiPharmacyApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
