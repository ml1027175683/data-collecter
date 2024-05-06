package com.h9.dododododod.modbus.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.h9.dododododod.modbus.master.ModbusUtils;
import com.h9.dododododod.modbus.utils.ExcelUtils;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;


import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Resource
    ModbusMaster master;

    @Value("${modbusTcp.pointSend}")
    String jsonFile;

    @ResponseBody
    @GetMapping("/sendData")
    public void sendData(Number value, int offset) {
        try {
            //    ModbusMaster tcpMaster = ModbusMasterFactory.getTcpMaster("127.0.0.1", 502, false, false);

            ModbusUtils.writeHoldingRegister(master, 1, offset, value, DataType.TWO_BYTE_INT_UNSIGNED);
        } catch (Exception e) {
            log.info("==============================");
            log.info(e.getMessage());
            log.info("==============================");
        }
    }

    @PostMapping("/import")
    public void importData(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            Workbook workbook = null;
            if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else {
                workbook = new XSSFWorkbook(file.getInputStream());
            }
            Sheet planSheet = workbook.getSheetAt(0);
            int rows = planSheet.getPhysicalNumberOfRows();
            HashMap<String, Integer> map = new HashMap<>();
            for (int i = 1; i <= rows; i++) {
                Row row = planSheet.getRow(i);
                if (row != null) {
                    if (!ExcelUtils.cellIsNull(row.getCell(0))) {

                        //index[2] somsKey
                        String key = row.getCell(2).getStringCellValue();
                        //index[4] modbus addr
                        int index = (int) row.getCell(4).getNumericCellValue();
                        map.put(key, index);
                    }

                }
            }
            String jsonStr = JSONUtil.toJsonStr(map);
            File file1 = new File(jsonFile);
            FileUtil.writeString(jsonStr, file1, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
