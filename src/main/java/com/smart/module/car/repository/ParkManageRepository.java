package com.smart.module.car.repository;

import com.smart.module.car.entity.CarParkManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkManageRepository extends JpaRepository<CarParkManage, Long> {

}
