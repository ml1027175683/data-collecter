package com.h9.dododododod.modbus.master;

import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusUtils {
    public static void writeHoldingRegister(ModbusMaster tcpMaster, int slaveId, int offset, Number value, int dataType) throws ModbusTransportException, ErrorResponseException, ModbusInitException {
        // 类型
        BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, offset, dataType);
        tcpMaster.setValue(locator, value);
    }
}
