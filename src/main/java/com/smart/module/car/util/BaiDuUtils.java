package com.smart.module.car.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baidu.aip.ocr.AipOcr;
import com.smart.common.util.SslUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * 百度智能AI
 * @author 小柒2012
 */
@Component
@Configuration
@EnableConfigurationProperties({BaiDuProperties.class})
public class BaiDuUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(BaiDuUtils.class);

    private BaiDuProperties baiDu;

    public BaiDuUtils(BaiDuProperties baiDu) {
        this.baiDu = baiDu;
    }

    private AipOcr client;

    @PostConstruct
    public void init() {
        try {
            client = new AipOcr(baiDu.getAppId(), baiDu.getApiKey(), baiDu.getAccessKeySecret());
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);
        } catch (Exception e) {
            LOGGER.error("百度智能AI初始化失败,{}", e.getMessage());
        }
    }

    /**
     * 参数为本地图片路径
     */
    public String plateLicense(String image) {
        try {
            HashMap<String, String> options = new HashMap<>();
            /**
             * 是否检测多张车牌，默认为false
             * 当置为true的时候可以对一张图片内的多张车牌进行识别
             */
            options.put("multi_detect", "true");
            SslUtils.ignoreSsl();
            JSONObject res = client.plateLicense(image, options);
            System.out.println(res.toString());
            Object result = res.get("words_result");
            JSONArray array = JSON.parseArray(result.toString());
            com.alibaba.fastjson.JSONObject object = JSON.parseObject(array.get(0).toString());
            Object number = object.get("number");
            return number.toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args)  {
        try {
            AipOcr client = new AipOcr("23992343", "uCs8VsdMkdeOZVh4nEzeOhDZ", "0lFwCRpZs9i76Pm2OXXaNo2EgrSXFsGa");
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);
            HashMap<String, String> options = new HashMap<>();
            String image = "C:\\Users\\lenovo\\Desktop\\车牌\\3.jpg";
            /**
             * 是否检测多张车牌，默认为false
             * 当置为true的时候可以对一张图片内的多张车牌进行识别
             */
            options.put("multi_detect", "true");
            SslUtils.ignoreSsl();
            JSONObject res = client.plateLicense(image, options);
            Object result = res.get("words_result");
            JSONArray array = JSON.parseArray(result.toString());
            com.alibaba.fastjson.JSONObject object = JSON.parseObject(array.get(0).toString());
            Object number = object.get("number");
            System.out.println("车牌:"+number);
        }catch (Exception e){
            e.printStackTrace();
        }
//        String image = "F://11111.jpg";
//        String url = "https://car.52itstyle.vip/upload";
//        try {
//            HttpResponse response = HttpRequest.post(url)
//                    .form("licensePlate", new File(image))
//                    .executeAsync();
//            System.out.println(response.body());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
