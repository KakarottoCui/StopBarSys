package com.smart.module.car.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.smart.common.util.DateUtils;
import com.smart.module.car.entity.CarParkManage;
import com.smart.module.car.entity.CarParkingRecord;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 计算费用
 */
public class CostUtils {

    /**
     * 计算
     * @param record
     * @param carParkManage
     * @return
     */
    public static BigDecimal calculate(CarParkingRecord record, CarParkManage carParkManage){
        long minutes = DateUtil.between(record.getGmtInto(),
                DateUtils.getTimestamp(), DateUnit.MINUTE);
        minutes = minutes - carParkManage.getFreeTime();
        if(minutes>0){
            BigDecimal count =
                    NumberUtil.div(new BigDecimal(minutes),carParkManage.getTimeUnit());
            count = NumberUtil.round(count,0);
            BigDecimal cost = NumberUtil.mul(count,carParkManage.getUnitCost());
            cost = NumberUtil.round(cost,0);
            if(cost.compareTo(carParkManage.getMaxMoney())>1){
                return carParkManage.getMaxMoney();
            }
            return cost;
        }else{
            return new BigDecimal(BigInteger.ZERO);
        }
    }

    public static void main(String[] args) {
        System.out.println(NumberUtil.div(4,2));
    }
}
