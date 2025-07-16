package com.h9.dododododod.tcp;




import com.h9.dododododod.config.TcpPortsConfig;
import com.h9.dododododod.tcp.service.TcpStartService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
@Slf4j
public class MplsStartUp implements CommandLineRunner {

    @Resource
    TcpPortsConfig tcpPortsConfig;
    @Resource
    TcpStartService tcpStartService;

    @Override
    public void run(String... args) {
        ArrayList<String> ports = tcpPortsConfig.getPorts();
        for (String port : ports) {
            tcpStartService.startThread(port);
        }

    }
}
