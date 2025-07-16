package com.h9.dododododod.tcp.task;


import cn.hutool.core.thread.ThreadUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TcpCollectTask implements Runnable {

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    RedisTemplate redisTemplateDB2;

    int filedRed = 0;
    String port = null;

    public TcpCollectTask(String port, RedisTemplate redisTemplateDB2) {
        this.port = port;
        this.redisTemplateDB2 = redisTemplateDB2;
    }


    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            filedRed = 0;
            int hostPort = Integer.parseInt(port);
            String perfix = "Tcp" + port;
            while (true) {
                try {
                    serverSocket = new ServerSocket(hostPort);
                    socket = serverSocket.accept();
                    socket .setSoTimeout(5000);
                    break;
                } catch (Exception e) {
                    socketclose();
                    log.info("", e);
                    ThreadUtil.safeSleep(2000);
                }
            }
            log.info("TCP连接成功:开启端口:{}", hostPort);
            InputStream in = null;
            try {
                in = socket.getInputStream();
            } catch (Exception e) {
                socketclose();
            }
            while (true) {
                filedRed++;
                if (filedRed>10) {
                    socketclose();
                    log.info("未接收到数据"+filedRed+"次,"+perfix+"关闭连接");
                    break;
                }
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {

                        int totalPackageCount = bytesRead / 4;
                        for (int i = 0; i < totalPackageCount; i++) {

                            byte[] group = new byte[4];
                            int startIndex = i * 4;
                            System.arraycopy(buffer, startIndex, group, 0, 4);
                            StringBuilder hexBuilder = getHexString(group);
                            log.info(perfix+"获取到totalPackageCount:{} 处理第"+i+"组 处理报文: [{}]",totalPackageCount, hexBuilder.toString().trim());
                            float floatValue = bytesToFloat(group);
                            redisTemplateDB2.opsForValue().set( perfix+ "_" + i, floatValue, Duration.ofMinutes(5));
                        }


                    }

                } catch (Exception e) {
                    ThreadUtil.safeSleep(500);
                    socketclose();
                    break;
                }
            }
        }


    }

    private static StringBuilder getHexString(byte[] group) {
        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : group) {
            hexBuilder.append(String.format("%02X ", b));
        }
        return hexBuilder;
    }


    // 大端序字节数组转float (IEEE 754)
    private static float bytesToFloat(byte[] bytes) {
        // 将4个字节组合成32位整数
        int intValue = ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);

        // 将整数按IEEE 754标准解释为float
        return Float.intBitsToFloat(intValue);
    }

    // 如果需要小端序版本
    private static float littleEndianBytesToFloat(byte[] bytes) {
        int intValue = (bytes[3] & 0xFF) << 24 |
                (bytes[2] & 0xFF) << 16 |
                (bytes[1] & 0xFF) << 8 |
                (bytes[0] & 0xFF);
        return Float.intBitsToFloat(intValue);
    }


    private void handler(String port, String binaryString) {
        //TODO  报文解析
        log.info("端口:{}  报文:{}", port, binaryString);

        int segmentLength = 32;
        List<Float> decimalNumbers = new ArrayList<>();
        try {
            int totalSegments = binaryString.length() / segmentLength;

            for (int i = 0; i < totalSegments; i++) {
                int start = i * segmentLength;
                int end = start + segmentLength;
                String segment = binaryString.substring(start, end);
                float aFloat = binaryStringToFloat(segment);
                decimalNumbers.add(aFloat);
            }
        } catch (Exception e) {
            log.info("解析出错端口:{}  报文:{} 错误信息:{}", port, binaryString, e.getMessage());
        }
        log.info("端口:{}  报文:{}  解析数据:{}", port, binaryString, Arrays.toString(decimalNumbers.toArray()));
        for (int i = 0; i < decimalNumbers.size(); i++) {
            redisTemplateDB2.opsForValue().set("Tcp" + port + "_" + i, decimalNumbers.get(i));
        }
    }

    public static float binaryStringToFloat(String binary) {
        if (binary.length() != 32) {
            throw new IllegalArgumentException("二进制字符串长度必须为32位");
        }

        // 提取符号位、指数位和尾数位
        char signBit = binary.charAt(0);
        String exponentBits = binary.substring(1, 9);
        String mantissaBits = binary.substring(9, 32);

        // 计算符号
        float sign = (signBit == '1') ? -1.0f : 1.0f;

        // 解析指数位（8位无符号整数）
        int exponent = Integer.parseInt(exponentBits, 2);
        // 解析尾数位（23位）
        int mantissaInt = mantissaBits.equals("0".repeat(23)) ? 0 : Integer.parseInt(mantissaBits, 2);

        // 处理特殊值
        if (exponent == 0xFF) { // 指数全1
            if (mantissaInt == 0) {
                return sign * Float.POSITIVE_INFINITY;
            } else {
                return Float.NaN;
            }
        }

        // 计算尾数值（二进制小数部分）
        float mantissaValue = mantissaInt / (float) (1 << 23); // 2^23 = 8388608

        // 规格化数（指数不全0）
        if (exponent > 0) {
            return sign * (1.0f + mantissaValue) * (float) Math.pow(2, exponent - 127);
        }
        // 非规格化数（指数全0）或零
        else {
            if (mantissaInt == 0) {
                return sign * 0.0f; // 正零或负零
            }
            return sign * mantissaValue * (float) Math.pow(2, -126);
        }
    }

    public static void main(String[] args) {
        // 测试用例
        String[] testCases = {
                "00111110001000000000000000000000", // 0.15625
                "11000000101000000000000000000000", // -5.0
                "00000000000000000000000000000000", // 正零
                "10000000000000000000000000000000", // 负零
                "01111111100000000000000000000000", // 正无穷
                "11111111100000000000000000000000", // 负无穷
                "01111111100000000000000000000001"  // NaN
        };

        for (String binary : testCases) {
            float result = binaryStringToFloat(binary);
            System.out.println("二进制: " + binary + " -> Float: " + result);
        }
    }


    private void socketclose() {
        if (null != socket) {
            try {
                socket.close();
                serverSocket.close();
                log.info("关闭连接端口号:{}", port);
            } catch (Exception e) {
                socket=null;
                serverSocket=null;
            }
        }
    }
}