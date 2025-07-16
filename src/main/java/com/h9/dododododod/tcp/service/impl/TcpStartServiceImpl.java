package com.h9.dododododod.tcp.service.impl;


import com.h9.dododododod.tcp.service.TcpStartService;
import com.h9.dododododod.tcp.task.TcpCollectTask;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class TcpStartServiceImpl implements TcpStartService {
    @Resource
    private ThreadPoolTaskExecutor tcpTaskExecutor;
    @Resource
    RedisTemplate redisTemplateDB2;

    @Override
    public void startThread(String port) {
        TcpCollectTask task = new TcpCollectTask(port,redisTemplateDB2);
        tcpTaskExecutor.execute(task);
    }
}