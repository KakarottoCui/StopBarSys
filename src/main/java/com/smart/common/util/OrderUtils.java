package com.smart.common.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 订单编码码生成器，生成26位数字编码，
 * 生成规则 1位支付类型+17位时间戳+8位随机
 */
public class OrderUtils {

    /**
     * 生成时间戳
     */
    private static String getDateTime() {
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date());
    }

    /**
     * 生成订单单号编码
     */
    public static String getOrderNo() {
        return getDateTime() + RandomStringUtils.randomNumeric(8);
    }


    /**
     * 生成订单单号编码
     */
    public static String getOrderNo(Long parkManageId) {
        return parkManageId + getDateTime() + RandomStringUtils.randomNumeric(8);
    }

}