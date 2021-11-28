package com.smart.module.finance.web;

import com.smart.common.model.Result;
import com.smart.common.util.ExcelExport;
import com.smart.module.finance.entity.Order;
import com.smart.module.finance.service.OrderService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/finance/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 列表
     */
    @PostMapping("list")
    public Result list(Order entity){
        return orderService.list(entity);
    }

    /**
     * 导出
     */
    @PostMapping("export")
    @RequiresRoles(value={"admin","orgAdmin"},logical = Logical.OR)
    public void export(Long orgId, Long parkManageId, HttpServletRequest request, HttpServletResponse response){
        try{
            ExcelExport excelExport = orderService.exportData(orgId,parkManageId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            excelExport.writeTemplate(response, request,
                    "订单信息-" + sdf.format(new Date()) + ".xls");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
