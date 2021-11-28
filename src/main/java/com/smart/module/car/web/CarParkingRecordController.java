package com.smart.module.car.web;

import com.smart.common.model.Result;
import com.smart.module.car.entity.CarParkingRecord;
import com.smart.module.car.repository.CarParkingRecordRepository;
import com.smart.module.car.service.CarParkingRecordService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 车辆进出记录
 * 爪哇笔记：https://blog.52itstyle.vip
 * @author 小柒2012
 */
@RestController
@RequestMapping("/car/parkingRecord")
public class CarParkingRecordController {

    @Autowired
    private CarParkingRecordService carParkingRecordService;
    @Autowired
    private CarParkingRecordRepository carParkingRecordRepository;

    /**
     * 列表
     */
    @PostMapping("list")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result list(CarParkingRecord entity){
        return carParkingRecordService.list(entity);
    }

    /**
     * 获取
     */
    @PostMapping("get")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result get(Long id){
        CarParkingRecord entity =
                carParkingRecordRepository.findById(id).orElse(new CarParkingRecord());
        return Result.ok(entity);
    }

}
