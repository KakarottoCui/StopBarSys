package com.smart.common.config;

import com.smart.module.sys.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * 通用访问拦截匹配
 * 爪哇笔记 https://blog.52itstyle.vip
 * @author 小柒2012
 */
@Controller
public class IndexController {

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("login.html")
    public String login(ModelMap map) {
        Object value = sysConfigService.getByKey("login_title");
        map.addAttribute("login_title", value==null?"智能停车场管理平台":value);
        value = sysConfigService.getByKey("smart_name");
        map.addAttribute("smart_name", value==null?"智能停车场管理平台":value);
        return  "login";
    }

    @GetMapping("index.html")
    public String index(ModelMap map) {
        Object value = sysConfigService.getByKey("smart_name");
        map.addAttribute("smart_name", value==null?"智能停车场管理平台":value);
        return  "index";
    }

    @GetMapping("console.html")
    public String page() {
        return  "console";
    }

    @GetMapping("{module}/{url}.html")
    public String page(@PathVariable("module") String module,@PathVariable("url") String url) {
        return module + "/" + url;
    }
    @GetMapping("{module}/{sub}/{url}.html")
    public String page(@PathVariable("module") String module,
                       @PathVariable("url") String url,
                       @PathVariable("sub") String sub) {
        return module + "/" + sub + "/" + url;
    }
    @GetMapping("{module}/{sub}/{smallSub}/{url}.html")
    public String page(@PathVariable("module") String module,
                       @PathVariable("url") String url,
                       @PathVariable("sub") String sub,
                       @PathVariable("smallSub") String smallSub) {
        return module + "/" + sub + "/" + smallSub + "/" + url;
    }

}