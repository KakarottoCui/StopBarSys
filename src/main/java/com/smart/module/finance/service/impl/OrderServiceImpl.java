package com.smart.module.finance.service.impl;

import cn.hutool.core.io.FileUtil;
import com.smart.common.constant.SystemConstant;
import com.smart.common.dynamicquery.DynamicQuery;
import com.smart.common.model.PageBean;
import com.smart.common.model.Result;
import com.smart.common.util.DateUtils;
import com.smart.common.util.ExcelExport;
import com.smart.common.util.OrderUtils;
import com.smart.common.util.ShiroUtils;
import com.smart.module.finance.entity.Order;
import com.smart.module.finance.repository.OrderRepository;
import com.smart.module.finance.service.OrderService;
import com.smart.module.sys.entity.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderRepository orderRepository;
    @Resource
    private DynamicQuery dynamicQuery;


    @Override
    @Transactional(rollbackFor=Exception.class)
    public Result save(Order entity) {
        entity.setGmtCreate(DateUtils.getTimestamp());
        entity.setOrderNo(OrderUtils.getOrderNo(entity.getParkManageId()));
        orderRepository.save(entity);
        return Result.ok();
    }

    @Override
    public Result list(Order entity) {
        String nativeSql = "SELECT COUNT(*) FROM app_order ";
        nativeSql += common(entity);
        Long count = dynamicQuery.nativeQueryCount(nativeSql);
        PageBean<Order> data = new PageBean<>();
        if(count>0){
            nativeSql = "SELECT * FROM app_order ";
            nativeSql += common(entity);
            nativeSql += " ORDER BY gmt_create desc";
            Pageable pageable = PageRequest.of(entity.getPageNo(),entity.getPageSize());
            List<Order> list =  dynamicQuery.nativeQueryPagingList(Order.class,pageable,nativeSql);
            data = new PageBean(list,count);
        }
        return Result.ok(data);
    }

    @Override
    @Transactional(readOnly = true)
    public ExcelExport exportData(Long orgId, Long parkManageId) throws IOException, InvalidFormatException {
        SysUser user = ShiroUtils.getUserEntity();
        Map<String, Integer> dataMap = new LinkedHashMap<>();
        dataMap.put("plate_number", ExcelExport.CELL_ALIGN_LEFT);
        dataMap.put("type", ExcelExport.CELL_ALIGN_LEFT);
        dataMap.put("total_Fee", ExcelExport.CELL_ALIGN_LEFT);
        dataMap.put("gmt_create", ExcelExport.CELL_ALIGN_LEFT);
        String nativeSql = "SELECT plate_number,(CASE TYPE WHEN 0 THEN '微信' WHEN 1 THEN '支付宝' WHEN 2 THEN 'APPLE PAY' WHEN 3 THEN 'HUAWEI PAY' ELSE '其它' END) type,total_Fee,gmt_create FROM app_order WHERE 1=1";
        List<Map<String, Object>> list;
        if(ShiroUtils.isHasRole(SystemConstant.ROLE_ADMIN)){
            if(orgId!=null){
                nativeSql +=" AND org_id="+orgId;
            }
        }else{
            nativeSql +=" AND org_id="+user.getOrgId();
        }
        if(parkManageId!=null){
            nativeSql +=" AND park_manage_id=?";
            list = dynamicQuery.nativeQueryListMap(nativeSql,parkManageId);
        }else{
            list = dynamicQuery.nativeQueryListMap(nativeSql);
        }
        /**
         * 开始生成模板、导出数据
         */
        InputStream stream = ClassUtils.getDefaultClassLoader()
                .getResourceAsStream("static/excelTemplate/orderExport.xls");
        ExcelExport excelExport = new ExcelExport(
                FileUtil.writeFromStream(stream, new File("excelTemplate/orderExport.xls")), 1);
        excelExport.setDataList(list, dataMap, false, "");
        return excelExport;
    }

    public String common(Order entity){
        String description = entity.getDescription();
        String commonSql = " WHERE 1=1";
        if(StringUtils.isNotBlank(description)){
            commonSql += " AND (body like '"+description+"%' OR plate_number like  '"+description+"%')";
        }
        if(ShiroUtils.isHasRole(SystemConstant.ROLE_ADMIN)){
            if(entity.getOrgId()!=null){
                commonSql +=" AND org_id="+entity.getOrgId();
            }
        }else{
            Long orgId = ShiroUtils.getUserEntity().getOrgId();
            commonSql +=" AND org_id="+orgId;
        }
        if(entity.getParkManageId()!=null){
            commonSql +=" AND park_manage_id="+entity.getParkManageId();
        }
        return commonSql;
    }
}
