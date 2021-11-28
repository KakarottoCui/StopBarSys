package com.smart.common.util;

import cn.hutool.core.date.DateUtil;
import com.smart.common.constant.MsgConstant;
import com.smart.common.constant.SystemConstant;
import com.smart.common.model.Result;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 通用工具类
 * 爪哇笔记：https://blog.52itstyle.vip
 */
public class CommonUtils {

	/**
	 * 对象是否为空
	 *
	 * @param obj
	 * @return
	 */
	public static boolean isNullOrEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		return false;
	}


	/**
	 * 查询详情提示
	 * @param data
	 * @return
	 */
	public static Result msg(Object data) {
		if (isNullOrEmpty(data)) {
			return Result.error(MsgConstant.MSG_INIT_FORM);
		}
		return Result.ok(data);
	}

	/**
	 * 返回数据
	 * 
	 * @param data
	 * @return
	 */
	public static Result msgNotCheckNull(Object data) {
		return Result.ok().put(SystemConstant.DATA_ROWS, data);
	}

    /**
     * 使用java正则表达式去掉多余的.与0
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");// 去掉多余的0
            s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 下载图片
     * @param imgUrl
     * @param imgPath
     */
	public static void downLoadImg(String imgUrl,String imgPath){
        BufferedImage bufferedImage;
        try {
            URL url=new URL(imgUrl);
            URLConnection urlConnection=url.openConnection();
            HttpURLConnection httpURLConnection=(HttpURLConnection)urlConnection;
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode()== HttpURLConnection.HTTP_OK){
                InputStream inputStream=httpURLConnection.getInputStream();
                bufferedImage = ImageIO.read(inputStream);
                ImageIO.write(bufferedImage,"png",new File(imgPath));
            }else {
                System.out.println("连接失败");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

	/**
	 * 创建多级文件夹
	 * @return
	 */
	public static File createParentFile(String filePath){
		File parentFile = new File(filePath+ SystemConstant.SF_FILE_SEPARATOR+ DateUtil.thisYear());
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		parentFile = new File(parentFile,(DateUtil.thisMonth()+1)+"");
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		parentFile = new File(parentFile,DateUtil.thisDayOfMonth()+"");
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		return parentFile;
	}
}
