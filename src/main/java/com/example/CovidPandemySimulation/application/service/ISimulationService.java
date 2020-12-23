package com.example.CovidPandemySimulation.application.service;

import com.example.CovidPandemySimulation.application.dto.SimulationDTO;
import com.example.CovidPandemySimulation.domain.agregate.Simulation;

import java.util.List;
import java.util.Optional;

public interface ISimulationService {
    public Simulation save(Simulation simulation);
    public Simulation findByName(String name);
    public Iterable<Simulation> findAll();
}
