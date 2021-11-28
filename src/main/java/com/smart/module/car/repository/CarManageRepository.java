package com.smart.module.car.repository;

import com.smart.module.car.entity.CarManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarManageRepository extends JpaRepository<CarManage, Long> {

}
