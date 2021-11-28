package com.smart.module.car.service.impl;

import cn.hutool.core.io.FileUtil;
import com.smart.common.constant.SystemConstant;
import com.smart.common.dynamicquery.DynamicQuery;
import com.smart.common.model.PageBean;
import com.smart.common.model.Result;
import com.smart.common.util.DateUtils;
import com.smart.common.util.ExcelExport;
import com.smart.common.util.ShiroUtils;
import com.smart.module.car.entity.CarManage;
import com.smart.module.car.repository.CarManageRepository;
import com.smart.module.car.service.CarManageService;
import com.smart.module.finance.entity.Order;
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
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarManageServiceImpl implements CarManageService {

    @Resource
    private DynamicQuery dynamicQuery;
    @Resource
    private CarManageRepository carManageRepository;
    @Resource
    private OrderService orderService;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Result save(CarManage entity) {
        if(entity.getId()==null){
            entity.setGmtCreate(DateUtils.getTimestamp());
            entity.setGmtModified(entity.getGmtCreate());
        }else{
            entity.setGmtModified(DateUtils.getTimestamp());
        }
        carManageRepository.saveAndFlush(entity);
        return Result.ok("保存成功");
    }

    @Override
    public Result list(CarManage entity) {
        String nativeSql = "SELECT COUNT(*) FROM app_car_manage ";
        nativeSql += common(entity);
        Long count = dynamicQuery.nativeQueryCount(nativeSql);
        PageBean<CarManage> data = new PageBean<>();
        if(count>0){
            nativeSql = "SELECT * FROM app_car_manage ";
            nativeSql += common(entity);
            nativeSql += " ORDER BY gmt_create desc";
            Pageable pageable = PageRequest.of(entity.getPageNo(),entity.getPageSize());
            List<CarManage> list =  dynamicQuery.nativeQueryPagingList(CarManage.class,pageable,nativeSql);
            data = new PageBean(list,count);
        }
        return Result.ok(data);
    }

    @Override
    public CarManage getByPlateNumber(String plateNumber, Long parkManageId) {
        String nativeSql = "SELECT * FROM app_car_manage WHERE plate_number=? AND park_manage_id=? ";
        CarManage carManage =
                dynamicQuery.nativeQuerySingleResult(CarManage.class,nativeSql,plateNumber,parkManageId);
        return carManage;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Result renew(Order entity) {
        SysUser user = ShiroUtils.getUserEntity();
        entity.setOrgId(user.getOrgId());
        entity.setUserCreate(user.getUserId());
        entity.setBody("车位续租");
        entity.setStatus(SystemConstant.PAY_STATUS_YES);
        orderService.save(entity);
        Timestamp validityTime = entity.getValidityTime();
        String nativeSql = "UPDATE app_car_manage SET validity_time=? WHERE id=?";
        dynamicQuery.nativeExecuteUpdate(nativeSql,validityTime,entity.getCarId());
        return Result.ok();
    }

    @Override
    @Transactional(readOnly = true)
    public ExcelExport exportData(Long orgId,Long parkManageId) throws IOException, InvalidFormatException {
        SysUser user = ShiroUtils.getUserEntity();
        Map<String, Integer> dataMap = new LinkedHashMap<>();
        dataMap.put("org_name", ExcelExport.CELL_ALIGN_LEFT);
        dataMap.put("park_manage_name", ExcelExport.CELL_ALIGN_LEFT);
        dataMap.put("plate_number", ExcelExport.CELL_ALIGN_LEFT);
        dataMap.put("nickname", ExcelExport.CELL_ALIGN_LEFT);
        dataMap.put("type", ExcelExport.CELL_ALIGN_LEFT);
        dataMap.put("status", ExcelExport.CELL_ALIGN_LEFT);
        dataMap.put("validity_time", ExcelExport.CELL_ALIGN_LEFT);
        String nativeSql = "SELECT org_name,park_manage_name,plate_number,nickname,(CASE TYPE WHEN 0 THEN '包月车' ELSE '免费车' END) type ,(CASE STATUS WHEN 0 THEN '禁用' ELSE '正常' END) status,validity_time FROM app_car_manage WHERE 1=1";
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
                .getResourceAsStream("static/excelTemplate/carManageExport.xls");
        ExcelExport excelExport = new ExcelExport(
                FileUtil.writeFromStream(stream, new File("excelTemplate/carManageExport.xls")), 1);
        excelExport.setDataList(list, dataMap, false, "");
        return excelExport;
    }

    public String common(CarManage entity){
        String description = entity.getDescription();
        String commonSql = " WHERE 1=1 ";
        if(StringUtils.isNotBlank(description)){
            commonSql += " AND name like '"+description+"%' ";
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
