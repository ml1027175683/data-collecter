package com.h9.dododododod;

import com.h9.dododododod.modbus.master.ModbusMasterFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class H9DodododododApplication {

    public static void main(String[] args) {
        SpringApplication.run(H9DodododododApplication.class, args);
    }


}
