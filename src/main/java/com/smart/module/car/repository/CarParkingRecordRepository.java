package com.smart.module.car.repository;

import com.smart.module.car.entity.CarParkingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarParkingRecordRepository extends JpaRepository<CarParkingRecord, Long> {

}
