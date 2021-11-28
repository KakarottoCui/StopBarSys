package com.smart.module.car.service;

import com.smart.common.model.Result;
import com.smart.common.util.ExcelExport;
import com.smart.module.car.entity.CarManage;
import com.smart.module.finance.entity.Order;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;

/**
 * 车辆管理
 * @author 小柒2012
 */
public interface CarManageService {

    /**
     * 保存
     * @param entity
     * @return
     */
    Result save(CarManage entity);

    /**
     * 列表
     * @param entity
     * @return
     */
    Result list(CarManage entity);

    /**
     * 根据车牌号和停车场获取车辆信息
     * @param plateNumber
     * @param parkManageId
     * @return
     */
    CarManage getByPlateNumber(String plateNumber,Long parkManageId);

    /**
     * 续租
     * @param entity
     * @return
     */
    Result renew(Order entity);

    /**
     * 导出
     * @param orgId
     * @param parkManageId
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    ExcelExport exportData(Long orgId,Long parkManageId) throws IOException, InvalidFormatException;

}
