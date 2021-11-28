package com.smart.module.finance.service;

import com.smart.common.model.Result;
import com.smart.common.util.ExcelExport;
import com.smart.module.finance.entity.Order;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;

public interface OrderService {

    /**
     * 保存
     * @param entity
     * @return
     */
    Result save(Order entity);

    /**
     * 列表
     * @param entity
     * @return
     */
    Result list(Order entity);

    /**
     * 导出
     * @param orgId
     * @param parkManageId
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    ExcelExport exportData(Long orgId, Long parkManageId) throws IOException, InvalidFormatException;

}
