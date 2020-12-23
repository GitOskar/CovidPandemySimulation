package com.example.CovidPandemySimulation.domain.factory;

import com.example.CovidPandemySimulation.application.dto.SimulationDTO;
import com.example.CovidPandemySimulation.domain.agregate.Simulation;
import com.example.CovidPandemySimulation.domain.exception.SimulationCreationException;
import org.springframework.stereotype.Component;

@Component
public class SimulationFactory {

    public Simulation createSimulation(SimulationDTO simulationDTO) throws SimulationCreationException
    {
        return new Simulation(
                simulationDTO.getName(),
                simulationDTO.getPopulationCount(),
                simulationDTO.getInitialInfectedNumber(),
                simulationDTO.getRNumber(),
                simulationDTO.getMortalityRate(),
                simulationDTO.getDiseaseDuration(),
                simulationDTO.getTimeOfDying(),
                simulationDTO.getDaysOfSimulation(),
                simulationDTO.getProtectionDuration());
    }

}
