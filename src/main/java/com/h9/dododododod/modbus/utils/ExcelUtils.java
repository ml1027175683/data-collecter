package com.h9.dododododod.modbus.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelUtils {

    /**
     * 判断单元格是否为空
     *
     * @param cell
     * @return
     */
    public static boolean cellIsNull(Cell cell) {
        if (null == cell || cell.getCellType().equals(CellType.BLANK)) {
            return true;
        } else if (cell.getCellType().equals(CellType.STRING)) {
            return StringUtils.isBlank(cell.getStringCellValue());
        }
        return false;
    }
}
