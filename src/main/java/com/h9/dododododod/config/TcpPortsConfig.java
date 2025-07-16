package com.h9.dododododod.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Data
@Configuration
@ConfigurationProperties(prefix = "tcp")
public class TcpPortsConfig {
    ArrayList<String> ports;
}