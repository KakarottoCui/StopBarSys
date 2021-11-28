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
 * car_park_manage 实体类
 * Created by 小柒2012
 * Sun Oct 27 13:01:25 CST 2021
 */
@Data
@Entity 
@Table(name = "app_car_park_manage")
public class CarParkManage extends PageBean implements Serializable{
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, length = 20)
	private Long id;

    /**
     * 名称
     */
    @Column(name = "name", length = 500)
    private String name;

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
    * 状态，0：隐藏   1：显示 
    */ 
	@Column(name = "status", length = 4)
	private Short status;


    /**
     * 停车位数量
     */
    @Column(name = "parking_space_number")
    private Integer parkingSpaceNumber;

    /**
     * 免费时长
     */
    @Column(name = "free_time")
    private Integer freeTime;

    /**
     * 计时单元
     */
    @Column(name = "time_unit")
    private Integer timeUnit;


    /**
     * 单元费用
     */
    @JsonSerialize(using = BigDecimalSerialize.class)
    @Column(name = "unit_cost",scale = 2, precision = 18)
    private BigDecimal unitCost;

    /**
     * 最大收费金额
     */
    @JsonSerialize(using = BigDecimalSerialize.class)
    @Column(name = "max_money",scale = 2, precision = 18)
    private BigDecimal maxMoney;

   /**
    * 创建时间 
    */ 
	@Column(name = "gmt_create")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp gmtCreate;

   /**
    * 修改时间 
    */ 
	@Column(name = "gmt_modified")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp gmtModified;

    /**
     * 创建用户id
     */
    @Column(name = "user_id_create")
    private Long userIdCreate;

}

