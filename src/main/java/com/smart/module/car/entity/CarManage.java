package com.smart.module.car.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.common.model.PageBean;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * app_car 实体类
 * Created by 小柒2012
 * Sun Oct 27 13:01:25 CST 2021
 */
@Data
@Entity 
@Table(name = "app_car_manage")
public class CarManage extends PageBean implements Serializable{
    
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
     * 性别
     */
    @Column(name = "gender", length = 1)
    private String gender;

    /**
     * 车主姓名
     */
    @Column(name = "nickname", length = 100)
    private String nickname;

    /**
     * 手机号
     */
    @Column(name = "mobile", length = 20)
    private String mobile;


    /**
     * 已购车位
     */
    @Column(name = "parking_lot", length = 100)
    private String parkingLot;

    /**
     * 类型  0：包月车  1：VIP免费车 2：临时
     */
    @Column(name = "type", length = 4)
    private Short type;

   /**
    * 状态，0：禁用   1：显示
    */ 
	@Column(name = "status", length = 4)
	private Short status;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;

    /**
     * 有效期至
     */
    @Column(name = "validity_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp validityTime;

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

