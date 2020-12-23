package com.example.CovidPandemySimulation;

import com.example.CovidPandemySimulation.application.dto.SimulationDTO;
import com.example.CovidPandemySimulation.domain.agregate.Record;
import com.example.CovidPandemySimulation.domain.agregate.Simulation;
import com.example.CovidPandemySimulation.domain.exception.SimulationCreationException;
import com.example.CovidPandemySimulation.domain.factory.SimulationFactory;
import com.example.CovidPandemySimulation.infrastructure.persistance.SimulationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
public class test {

    @Autowired
    SimulationRepository simulationRepository;

    @Autowired
    SimulationFactory simulationFactory;

    @EventListener(ApplicationReadyEvent.class)
    public void testMethod()
    {
        SimulationDTO simulationDTO = new SimulationDTO(
                "Covid",
                40000000,
                5,
                3.2,
                0.05,
                15,
                10,
                200,
                null);
        Simulation simulation = null;
        try {
            simulation = simulationFactory.createSimulation(simulationDTO);
        } catch (SimulationCreationException e) {
            e.printStackTrace();
        }

        simulationRepository.save(simulation);
    }
}
