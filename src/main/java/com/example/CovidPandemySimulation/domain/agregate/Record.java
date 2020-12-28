package com.example.CovidPandemySimulation.domain.agregate;

import com.example.CovidPandemySimulation.domain.primitive.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Record")
@Getter
@Setter
@NoArgsConstructor
public class Record extends BaseEntity
{
    @Column(name = "Infected_Count")
    private long infectedCount;
    @Column(name = "Susceptible_To_Infection")
    private long susceptibleToInfection;
    @Column(name = "Death_Count")
    private long deathCount;
    @Column(name = "Resistant_Count")
    private long resistantCount;
    @ManyToOne
    @JoinColumn(name="simulation_id")
    private Simulation simulation;

    public Record(long infectedCount, long susceptibleToInfection, long deathCount, long resistantCount, Simulation simulation) {
        this.infectedCount = infectedCount;
        this.susceptibleToInfection = susceptibleToInfection;
        this.deathCount = deathCount;
        this.resistantCount = resistantCount;
        this.simulation = simulation;
    }

    public Record(Record record)
    {
        this.infectedCount = record.getInfectedCount();
        this.susceptibleToInfection = record.getSusceptibleToInfection();
        this.deathCount = record.getDeathCount();
        this.resistantCount = record.getResistantCount();
        this.simulation = record.getSimulation();
    }

    public void addDeaths(long newDeaths)
    {
        this.deathCount += newDeaths;
        this.infectedCount -= newDeaths;
    }

    public void addInfected(long newInfected)
    {
        this.infectedCount += newInfected;
        this.susceptibleToInfection -= newInfected;
    }

    public void addResistant(long newResistant)
    {
        this.resistantCount += newResistant;
        this.infectedCount -= newResistant;
    }

    public void endOfProtection(long newSusceptible)
    {
        resistantCount -= newSusceptible;
        susceptibleToInfection += newSusceptible;
    }
}
