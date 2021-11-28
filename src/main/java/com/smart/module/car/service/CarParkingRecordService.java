package com.smart.module.car.service;

import com.smart.common.model.Result;
import com.smart.module.car.entity.CarManage;
import com.smart.module.car.entity.CarParkingRecord;

/**
 * 车辆管理
 * @author 小柒2012
 */
public interface CarParkingRecordService {

    /**
     * 保存
     * @param entity
     * @return
     */
    Result save(CarParkingRecord entity);

    /**
     * 列表
     * @param entity
     * @return
     */
    Result list(CarParkingRecord entity);

    /**
     * 根据车牌和停车场获取停车记录
     * @param plateNumber
     * @param parkManageId
     * @return
     */
    CarParkingRecord getByPlateNumber(String plateNumber,Long parkManageId);

}
