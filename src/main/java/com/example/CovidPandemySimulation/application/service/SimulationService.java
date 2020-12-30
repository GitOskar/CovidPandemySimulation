package com.example.CovidPandemySimulation.application.service;

import com.example.CovidPandemySimulation.domain.agregate.Simulation;
import com.example.CovidPandemySimulation.infrastructure.persistance.SimulationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SimulationService implements ISimulationService{
    @Autowired
    SimulationRepository simulationRepository;

    @Override
    public Simulation save(Simulation simulation) {
        return simulationRepository.save(simulation);
    }

    @Override
    public Simulation findByName(String name) {
        return simulationRepository.findByName(name);
    }

    @Override
    public Iterable<Simulation> findAll() {
        return simulationRepository.findAll();
    }

    @Override
    public Simulation update(Simulation simulation) {
        Simulation simulationFromDB = simulationRepository.findByName(simulation.getName());
        simulationFromDB.deleteAllRecord();

        simulationFromDB.setProtectionDuration(simulation.getProtectionDuration());
        simulationFromDB.setInfectionRate(simulation.getInfectionRate());
        simulationFromDB.setDaysOfSimulation(simulation.getDaysOfSimulation());
        simulationFromDB.setDiseaseDuration(simulation.getDiseaseDuration());
        simulationFromDB.setInitialInfectedNumber(simulation.getInitialInfectedNumber());
        simulationFromDB.setMortalityRate(simulation.getMortalityRate());
        simulationFromDB.setName(simulation.getName());
        simulationFromDB.setPopulationCount(simulation.getPopulationCount());
        simulationFromDB.setTimeOfDying(simulation.getTimeOfDying());
        simulationFromDB.createRecords();

        return simulationRepository.save(simulationFromDB);
    }

    @Override
    @Transactional
    public void deleteByName(String name) {
        simulationRepository.deleteByName(name);
    }
}
