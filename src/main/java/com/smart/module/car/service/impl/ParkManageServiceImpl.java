package com.smart.module.car.service.impl;

import com.smart.common.constant.SystemConstant;
import com.smart.common.dynamicquery.DynamicQuery;
import com.smart.common.model.PageBean;
import com.smart.common.model.Result;
import com.smart.common.util.DateUtils;
import com.smart.common.util.ShiroUtils;
import com.smart.module.car.entity.CarParkManage;
import com.smart.module.car.repository.ParkManageRepository;
import com.smart.module.car.service.ParkManageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ParkManageServiceImpl implements ParkManageService {

    @Resource
    private DynamicQuery dynamicQuery;
    @Resource
    private ParkManageRepository parkManageRepository;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Result save(CarParkManage entity) {
        if(entity.getId()==null){
            entity.setGmtCreate(DateUtils.getTimestamp());
            entity.setGmtModified(entity.getGmtCreate());
        }else{
            entity.setGmtModified(DateUtils.getTimestamp());
        }
        parkManageRepository.saveAndFlush(entity);
        return Result.ok("保存成功");
    }

    @Override
    public Result list(CarParkManage entity) {
        String nativeSql = "SELECT COUNT(*) FROM app_car_park_manage ";
        nativeSql += common(entity);
        Long count = dynamicQuery.nativeQueryCount(nativeSql);
        PageBean<CarParkManage> data = new PageBean<>();
        if(count>0){
            nativeSql = "SELECT * FROM app_car_park_manage ";
            nativeSql += common(entity);
            nativeSql += "ORDER BY gmt_create desc";
            Pageable pageable = PageRequest.of(entity.getPageNo(),entity.getPageSize());
            List<CarParkManage> list =  dynamicQuery.nativeQueryPagingList(CarParkManage.class,pageable,nativeSql);
            data = new PageBean(list,count);
        }
        return Result.ok(data);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String,Object>> select(CarParkManage entity) {
        String nativeSql = "SELECT id,name FROM app_car_park_manage WHERE 1=1";
        if(ShiroUtils.isHasRole(SystemConstant.ROLE_ADMIN)){
            if(entity.getOrgId()!=null){
                nativeSql +=" AND org_id="+entity.getOrgId();
            }
        }else{
            Long orgId = ShiroUtils.getUserEntity().getOrgId();
            nativeSql +=" AND org_id="+orgId;
        }
        return dynamicQuery.nativeQueryListMap(nativeSql);
    }

    public String common(CarParkManage entity){
        String description = entity.getDescription();
        String commonSql = "";
        if(StringUtils.isNotBlank(description)){
            commonSql += "WHERE name like '"+description+"%' ";
        }
        return commonSql;
    }

}
