package com.example.CovidPandemySimulation.infrastructure.persistance;

import com.example.CovidPandemySimulation.domain.agregate.Simulation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationRepository extends CrudRepository<Simulation, Integer> {
    List<Simulation> findByName(String name);
}
