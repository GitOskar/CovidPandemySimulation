package com.example.CovidPandemySimulation.application.dto;

import com.example.CovidPandemySimulation.domain.agregate.Record;
import com.example.CovidPandemySimulation.domain.agregate.Simulation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class SimulationDTO {
    String name;
    private long populationCount;
    private long initialInfectedNumber;
    private double infectionRate;
    private double mortalityRate;
    private int diseaseDuration;
    private int timeOfDying;
    private int daysOfSimulation;
    private int protectionDuration;
    private List<RecordDTO> recordDTOs;

    public SimulationDTO(Simulation simulation)
    {
        this.name = simulation.getName();
        this.populationCount = simulation.getPopulationCount();
        this.initialInfectedNumber = simulation.getInitialInfectedNumber();
        this.infectionRate = simulation.getInfectionRate();
        this.mortalityRate = simulation.getMortalityRate();
        this.diseaseDuration = simulation.getDiseaseDuration();
        this.timeOfDying = simulation.getTimeOfDying();
        this.daysOfSimulation = simulation.getDaysOfSimulation();
        this.protectionDuration = simulation.getProtectionDuration();
        this.recordDTOs = new ArrayList<>();

        for (Record record : simulation.getRecords())
            recordDTOs.add(new RecordDTO(record));
    }
}
