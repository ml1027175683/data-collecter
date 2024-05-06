package com.h9.dododododod.modbus;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.locator.BaseLocator;


public class ModbusExample {
    public static void main(String[] args) {

        double i = 999.9D;
        String format = String.format("%02d", i);
        System.out.println(format);

    }
}
