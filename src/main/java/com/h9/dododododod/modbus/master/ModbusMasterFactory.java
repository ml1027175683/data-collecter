package com.h9.dododododod.modbus.master;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.IpParameters;


public class ModbusMasterFactory {

    public static ModbusFactory modbusFactory = new ModbusFactory();


    public static ModbusMaster getTcpMaster(String host, int port, boolean encapsulated, boolean keepAlive) throws ModbusInitException {
        IpParameters params = new IpParameters();
        params.setHost(host);
        params.setPort(port);
        params.setEncapsulated(encapsulated);
        ModbusMaster master = modbusFactory.createTcpMaster(params, keepAlive);
        master.setTimeout(1000);
        master.setRetries(3);
        master.init();
        return master;
    }

    public static ModbusSlaveSet createTcpSlave(boolean encapsulated) {
        ModbusSlaveSet slave = modbusFactory.createTcpSlave(encapsulated);
        return slave;
    }






}
