package com.smart.module.car.service.impl;

import com.smart.common.constant.SystemConstant;
import com.smart.common.dynamicquery.DynamicQuery;
import com.smart.common.model.PageBean;
import com.smart.common.model.Result;
import com.smart.common.util.DateUtils;
import com.smart.common.util.ShiroUtils;
import com.smart.module.car.entity.CarParkingRecord;
import com.smart.module.car.repository.CarParkingRecordRepository;
import com.smart.module.car.service.CarParkingRecordService;
import com.smart.module.finance.entity.Order;
import com.smart.module.finance.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CarParkingRecordServiceImpl implements CarParkingRecordService {

    @Resource
    private DynamicQuery dynamicQuery;
    @Resource
    private CarParkingRecordRepository carParkingRecordRepository;
    @Resource
    private OrderService orderService;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Result save(CarParkingRecord entity) {
        if(entity.getId()!=null){
            /**
             * 临时车 生成订单
             */
            if(entity.getType().shortValue() != SystemConstant.CAR_TYPE_TEMP){
                Order order = new Order();
                order.setOrgId(entity.getOrgId());
                order.setParkManageId(entity.getParkManageId());
                order.setUserCreate(-1L);
                order.setBody("临时车辆");
                order.setStatus(SystemConstant.PAY_STATUS_NO);
                order.setGmtCreate(DateUtils.getTimestamp());
                order.setPlateNumber(entity.getPlateNumber());
                order.setTotalFee(entity.getCost());
                orderService.save(order);
            }
        }
        carParkingRecordRepository.saveAndFlush(entity);
        return Result.ok("保存成功");
    }

    @Override
    public Result list(CarParkingRecord entity) {
        String nativeSql = "SELECT COUNT(*) FROM app_car_parking_record ";
        nativeSql += common(entity);
        Long count = dynamicQuery.nativeQueryCount(nativeSql);
        PageBean<CarParkingRecord> data = new PageBean<>();
        if(count>0){
            nativeSql = "SELECT * FROM app_car_parking_record ";
            nativeSql += common(entity);
            nativeSql += " ORDER BY gmt_into desc";
            Pageable pageable = PageRequest.of(entity.getPageNo(),entity.getPageSize());
            List<CarParkingRecord> list =
                    dynamicQuery.nativeQueryPagingList(CarParkingRecord.class,pageable,nativeSql);
            data = new PageBean(list,count);
        }
        return Result.ok(data);
    }

    @Override
    public CarParkingRecord getByPlateNumber(String plateNumber, Long parkManageId) {
        String nativeSql = "SELECT * FROM app_car_parking_record WHERE plate_number=? AND park_manage_id=? AND gmt_out is null";
        return dynamicQuery.nativeQuerySingleResult(CarParkingRecord.class,nativeSql,plateNumber,parkManageId);
    }

    public String common(CarParkingRecord entity){
        String description = entity.getDescription();
        String commonSql = " WHERE 1=1";
        if(StringUtils.isNotBlank(description)){
            commonSql += " AND plate_number like '"+description+"%' ";
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
