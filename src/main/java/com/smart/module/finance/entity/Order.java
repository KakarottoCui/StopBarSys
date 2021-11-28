package com.smart.module.finance.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.smart.common.config.BigDecimalSerialize;
import com.smart.common.model.PageBean;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "app_order")
public class Order extends PageBean implements Serializable {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 20)
    private Long id;

    /**
     * 商品描述
     */
    @Column(name = "body")
    private String body;

    /**
     * 订单号
     */
    @Column(name = "order_no", length = 200)
    private String orderNo;

    /**
     * 付款金额
     */
    @JsonSerialize(using = BigDecimalSerialize.class)
    @Column(name = "total_Fee",scale = 2, precision = 18)
    private BigDecimal totalFee;

    /**
     * 支付类型 0：微信  1：支付宝  2: Apple Pay  3: HUAWEI pay
     */
    @Column(name = "type", length = 4)
    private Short type;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "gmt_create")
    private Timestamp gmtCreate;

    /**
     * 创建用户
     */
    @Column(name = "user_create")
    private Long userCreate;

    /**
     * 停车场ID
     */
    @Column(name = "park_manage_id", nullable = false, length = 20)
    private Long parkManageId;

    /**
     * 机构ID
     */
    @Column(name = "org_Id", nullable = false, length = 20)
    private Long orgId;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 车牌号
     */
    @Column(name = "plate_number", length = 100)
    private String plateNumber;

    /**
     * 有效期至
     */
    @Column(name = "validity_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp validityTime;

    /**
     * 订单支付状态 0：未支付 1：支付
     */
    @Column(name = "status", length = 4)
    private Short status;

    /**
     * 车辆主键
     */
    @Transient
    private Long carId;

}
