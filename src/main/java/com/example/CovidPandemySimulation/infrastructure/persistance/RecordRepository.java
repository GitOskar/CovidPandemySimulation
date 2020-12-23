package com.example.CovidPandemySimulation.infrastructure.persistance;

import org.springframework.data.repository.CrudRepository;
import com.example.CovidPandemySimulation.domain.agregate.Record;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends CrudRepository<Record, Integer> {
}
