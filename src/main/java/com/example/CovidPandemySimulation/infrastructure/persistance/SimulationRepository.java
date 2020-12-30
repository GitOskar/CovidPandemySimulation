package com.example.CovidPandemySimulation.infrastructure.persistance;

import com.example.CovidPandemySimulation.domain.agregate.Simulation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationRepository extends CrudRepository<Simulation, Integer> {
    Simulation findByName(String name);
    void deleteByName(String name);
}
