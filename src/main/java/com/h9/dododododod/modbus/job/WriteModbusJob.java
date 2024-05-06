package com.h9.dododododod.modbus.job;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.h9.dododododod.modbus.master.ModbusUtils;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Objects;

@Component
@Slf4j
public class WriteModbusJob {

    @Resource
    private RedisTemplate redisTemplateDB2;

    @Resource
    ModbusMaster master;
    @Value("${modbusTcp.pointSend}")
    String jsonFile;

   // @Scheduled(fixedRate = 1000 * 3)
    public void run() {
        try {
            File file = new File(jsonFile);

            JSON json = JSONUtil.readJSON(file, Charset.defaultCharset());

            JSONObject jsonObject = JSONUtil.parseObj(json);
            for (String key : jsonObject.keySet()) {
                Object obj = redisTemplateDB2.opsForValue().get(key);
                if (!Objects.isNull(obj)) {
                    Integer index = (Integer) jsonObject.get(key);
                    log.info("index:{}",index);
                    String str = String.valueOf(obj);
                    if (NumberUtil.isNumber(str)) {
                        if (NumberUtil.isDouble(str)) {
                            double v = new BigDecimal(str).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            ModbusUtils.writeHoldingRegister(master, 1, index, v, DataType.FOUR_BYTE_FLOAT);
                        }else {
                            ModbusUtils.writeHoldingRegister(master, 1, index, Integer.valueOf(str), DataType.TWO_BYTE_INT_UNSIGNED);

                        }
                    }
                }

            }


   /*     if (!Objects.isNull(body)) {
            JSONObject jsonObject = JSONUtil.parseObj(body);
            Object weight = jsonObject.get("weight");
            stringRedisTemplate.opsForValue().set("TtlCargo", String.valueOf(weight), Constants.CACHE_TIME, TimeUnit.SECONDS);


        }*/
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
