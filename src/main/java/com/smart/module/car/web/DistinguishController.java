package com.smart.module.car.web;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.smart.common.constant.SystemConstant;
import com.smart.common.model.Result;
import com.smart.common.util.CommonUtils;
import com.smart.common.util.DateUtils;
import com.smart.module.car.entity.CarManage;
import com.smart.module.car.entity.CarParkManage;
import com.smart.module.car.entity.CarParkingRecord;
import com.smart.module.car.repository.CarParkingRecordRepository;
import com.smart.module.car.repository.ParkManageRepository;
import com.smart.module.car.service.CarManageService;
import com.smart.module.car.service.CarParkingRecordService;
import com.smart.module.car.util.BaiDuUtils;
import com.smart.module.car.util.CostUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/car/distinguish")
public class DistinguishController {

    @Value("${file.path}")
    private String filePath;

    @Autowired
    private BaiDuUtils baiDuUtils;

    @Autowired
    private ParkManageRepository parkManageRepository;

    @Autowired
    private CarManageService carManageService;

    @Autowired
    private CarParkingRecordRepository carParkingRecordRepository;

    @Autowired
    private CarParkingRecordService carParkingRecordService;

    /**
     * 演示上传
     */
    @RequestMapping("upload")
    public Result upload(MultipartFile file,Long id) {
        try {
            if(file!=null){
                CarParkManage carParkManage =
                        parkManageRepository.findById(id).orElse(new CarParkManage());
                if(id==null||carParkManage==null){
                    return Result.error("请选择停车场");
                }
                File parentFile = CommonUtils.createParentFile(filePath);
                String fileName = file.getOriginalFilename();
                String suffix = fileName.substring(fileName.lastIndexOf("."));
                String uuid = IdUtil.simpleUUID();
                fileName = uuid + suffix;
                File imageFile = new File(parentFile,fileName);
                FileUtil.writeFromStream(file.getInputStream(), imageFile);
                String fileDay = DateUtil.thisYear()+"/"+(DateUtil.thisMonth()+1)+"/"+DateUtil.thisDayOfMonth();
                String imagePath = SystemConstant.FILE + "/" + fileDay+"/"+fileName;
                String plateNumber = baiDuUtils.plateLicense(imageFile.getAbsolutePath());
                if(StringUtils.isBlank(plateNumber)){
                    return Result.error("识别失败");
                }
                Map<String, Object> map = new HashMap<>();
                map.put("plateNumber",plateNumber);
                map.put("imagePath",imagePath);

                CarParkingRecord record =
                        carParkingRecordService.getByPlateNumber(plateNumber,id);
                /**
                 * 出厂
                 */
                if(record!=null){
                    record.setCost(CostUtils.calculate(record,carParkManage));
                    record.setGmtOut(DateUtils.getTimestamp());
                    map.put("msg","出厂成功");
                }else{
                    record = new CarParkingRecord();
                    record.setOrgId(carParkManage.getOrgId());
                    record.setOrgName(carParkManage.getOrgName());
                    record.setParkManageId(carParkManage.getId());
                    record.setParkManageName(carParkManage.getName());
                    record.setGmtInto(DateUtils.getTimestamp());
                    record.setPlateNumber(plateNumber);
                    CarManage carManage =
                            carManageService.getByPlateNumber(plateNumber,id);
                    if(carManage!=null){
                        record.setType(carManage.getType());
                    }else{
                        record.setType(SystemConstant.CAR_TYPE_TEMP);
                    }
                    map.put("msg","进厂成功");
                }
                carParkingRecordService.save(record);
                return Result.ok(map);
            }else{
                return Result.error();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }
    }

}
