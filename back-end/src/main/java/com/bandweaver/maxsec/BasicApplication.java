package com.bandweaver.maxsec;

import com.bandweaver.maxsec.service.MeasObjectValService;
import com.bandweaver.maxsec.service.VideoManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BasicApplication implements CommandLineRunner {

    @Autowired
    private VideoManagerService videoManagerService;
    @Autowired
    private MeasObjectValService measObjectValService;

    public static void main(String[] args) {
        SpringApplication.run(BasicApplication.class, args);
    }

    @Override
    public void run(String... args) {

//        videoManagerService.start();
        measObjectValService.selectAll();
    }
}
