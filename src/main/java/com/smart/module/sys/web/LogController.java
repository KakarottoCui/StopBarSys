package com.smart.module.sys.web;

import com.smart.common.model.Result;
import com.smart.module.sys.entity.SysLog;
import com.smart.module.sys.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志管理
 * 爪哇笔记：https://blog.52itstyle.vip
 * @author 小柒2012
 */
@RestController
@RequestMapping("/sys/log")
public class LogController {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 日志列表
     */
    @PostMapping("/list")
    public Result list(SysLog log){
        return sysLogService.list(log);
    }

}
