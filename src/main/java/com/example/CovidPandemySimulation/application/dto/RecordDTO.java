package com.example.CovidPandemySimulation.application.dto;

import com.example.CovidPandemySimulation.domain.agregate.Record;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
public class RecordDTO {
    private long infectedCount;
    private long susceptibleToInfection;
    private long deathCount;
    private long resistantCount;

    public RecordDTO(Record record)
    {
        infectedCount = record.getInfectedCount();
        susceptibleToInfection = record.getSusceptibleToInfection();
        deathCount = record.getDeathCount();
        resistantCount = record.getResistantCount();
    }
}
