package com.smart.module.car.web;

import com.smart.common.model.Result;
import com.smart.common.util.ExcelExport;
import com.smart.module.car.entity.CarManage;
import com.smart.module.car.repository.CarManageRepository;
import com.smart.module.car.service.CarManageService;
import com.smart.module.finance.entity.Order;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 停车场管理
 * 爪哇笔记：https://blog.52itstyle.vip
 * @author 小柒2012
 */
@RestController
@RequestMapping("/car/manage")
public class CarManageController {

    @Autowired
    private CarManageService carManageService;
    @Autowired
    private CarManageRepository carManageRepository;

    /**
     * 列表
     */
    @PostMapping("list")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result list(CarManage entity){
        return carManageService.list(entity);
    }

    /**
     * 获取
     */
    @PostMapping("get")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result get(Long id){
        CarManage entity =
                carManageRepository.findById(id).orElse(new CarManage());
        return Result.ok(entity);
    }

    /**
     * 保存
     */
    @PostMapping("save")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result save(@RequestBody CarManage entity){
        return carManageService.save(entity);
    }

    /**
     * 删除
     */
    @PostMapping("delete")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result delete(Long id){
        carManageRepository.deleteById(id);
        return Result.ok();
    }

    /**
     * 续租
     */
    @PostMapping("renew")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public Result renew(@RequestBody Order entity){
        return carManageService.renew(entity);
    }


    /**
     * 导出
     */
    @PostMapping("export")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public void export(Long orgId,Long parkManageId,HttpServletRequest request, HttpServletResponse response){
        try{
            ExcelExport excelExport = carManageService.exportData(orgId,parkManageId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            excelExport.writeTemplate(response, request,
                    "车辆信息-" + sdf.format(new Date()) + ".xls");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
