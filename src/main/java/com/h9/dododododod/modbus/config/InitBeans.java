package com.h9.dododododod.modbus.config;

import com.h9.dododododod.modbus.master.ModbusMasterFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class InitBeans {
    @Value("${modbusTcp.port}")
    Integer port;

    @Value("${modbusTcp.host}")
    String host;
    @Bean
    public ModbusMaster getModbusMaster() throws ModbusInitException {
        ModbusMaster tcpMaster = ModbusMasterFactory.getTcpMaster(host, port, false, false);
        log.info("创建modbusMaster成功");
        return tcpMaster;
    }
}
