package com.smart.module.car.web;

import com.smart.common.model.Result;
import com.smart.common.util.OrderUtils;
import com.smart.module.car.entity.CarParkManage;
import com.smart.module.car.repository.ParkManageRepository;
import com.smart.module.car.service.ParkManageService;
import com.smart.module.pay.entity.AppPayConfig;
import com.smart.module.pay.repository.PayConfigRepository;
import com.yungouos.pay.merge.MergePay;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 停车场管理
 * 爪哇笔记：https://blog.52itstyle.vip
 * @author 小柒2012
 */
@RestController
@RequestMapping("/car/parkManage")
public class ParkManageController {

    @Autowired
    private ParkManageService parkManageService;
    @Autowired
    private ParkManageRepository parkManageRepository;
    @Autowired
    private PayConfigRepository payConfigRepository;

    /**
     * 列表
     */
    @PostMapping("list")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result list(CarParkManage entity){
        return parkManageService.list(entity);
    }

    /**
     * 获取
     */
    @PostMapping("get")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result get(Long id){
        CarParkManage carParkManage =
                parkManageRepository.findById(id).orElse(new CarParkManage());
        return Result.ok(carParkManage);
    }

    /**
     * 保存
     */
    @PostMapping("save")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result save(@RequestBody CarParkManage entity){
        return parkManageService.save(entity);
    }

    /**
     * 删除
     */
    @PostMapping("delete")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result delete(Long id){
        parkManageRepository.deleteById(id);
        return Result.ok();
    }

    /**
     * 列表
     */
    @PostMapping("/select")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result select(CarParkManage entity){
        List<Map<String,Object>> list = parkManageService.select(entity);
        return Result.ok(list);
    }

    /**
     * 生成支付码
     */
    @PostMapping("/createPay")
    public Result createPay(Long carParkId){
        AppPayConfig config = payConfigRepository.findByCarParkId(carParkId);
        MergePay.nativePay(OrderUtils.getOrderNo(),"100",
                config.getMchId(),"停车收费","","","","",
                "","","",config.getSecretKey());
        return Result.ok();
    }
}
