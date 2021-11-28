package com.smart.module.car.service;

import com.smart.common.model.Result;
import com.smart.module.car.entity.CarParkManage;

import java.util.List;
import java.util.Map;

/**
 * 停车场管理
 * @author 小柒2012
 */
public interface ParkManageService {

    /**
     * 保存
     * @param entity
     * @return
     */
    Result save(CarParkManage entity);

    /**
     * 列表
     * @param entity
     * @return
     */
    Result list(CarParkManage entity);

    /**
     * 列表
     * @param entity
     * @return
     */
    List<Map<String,Object>> select(CarParkManage entity);
}
