package com.example.CovidPandemySimulation.application.service;

import com.example.CovidPandemySimulation.domain.agregate.Simulation;

public interface ISimulationService {
    public Simulation save(Simulation simulation);
    public Simulation findByName(String name);
    public Iterable<Simulation> findAll();
    public Simulation update(Simulation simulation);
    public void deleteByName(String name);
}
