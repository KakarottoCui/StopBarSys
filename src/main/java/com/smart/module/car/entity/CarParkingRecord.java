package com.smart.module.car.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smart.common.config.BigDecimalSerialize;
import com.smart.common.model.PageBean;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * app_car_parking_record 实体类
 * Created by 小柒2012
 * Sun Oct 27 13:01:25 CST 2021
 */
@Data
@Entity 
@Table(name = "app_car_parking_record")
public class CarParkingRecord extends PageBean implements Serializable{
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, length = 20)
	private Long id;


    /**
     * 订单号
     */
    @Column(name = "order_no", length = 500)
    private String orderNo;

    /**
     * 所属单位
     */
    @Column(name = "org_id", nullable = false, length = 20)
    private Long orgId;

    /**
     * 所属单位
     */
    @Column(name = "org_name", length = 500)
    private String orgName;


    /**
     * 停车场
     */
    @Column(name = "park_manage_id", nullable = false, length = 20)
    private Long parkManageId;

    /**
     * 停车场
     */
    @Column(name = "park_manage_name", length = 500)
    private String parkManageName;

    /**
     * 车牌号
     */
    @Column(name = "plate_number", length = 100)
    private String plateNumber;

    /**
     * 类型 ，0：包月车  1：VIP免费车 2：临时车
     */
    @Column(name = "type", length = 4)
    private Short type;


   /**
    * 入场时间
    */ 
	@Column(name = "gmt_into")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp gmtInto;

   /**
    * 出场时间
    */ 
	@Column(name = "gmt_out")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp gmtOut;

    /**
     * 收费金额
     */
    @JsonSerialize(using = BigDecimalSerialize.class)
    @Column(name = "cost", scale = 2, precision = 18)
    private BigDecimal cost;
}

