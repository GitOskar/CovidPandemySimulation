package com.example.CovidPandemySimulation.application.service;

import com.example.CovidPandemySimulation.domain.agregate.Simulation;
import com.example.CovidPandemySimulation.infrastructure.persistance.SimulationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimulationService implements ISimulationService{
    @Autowired
    SimulationRepository simulationRepository;

    @Override
    public Simulation save(Simulation simulation) {
        return simulationRepository.save(simulation);
    }

    @Override
    public List<Simulation> findByName(String name) {
        return simulationRepository.findByName(name);
    }

    @Override
    public Iterable<Simulation> findAll() {
        return simulationRepository.findAll();
    }
}
