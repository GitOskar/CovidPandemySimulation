package com.example.CovidPandemySimulation.domain.agregate;

import com.example.CovidPandemySimulation.domain.exception.SimulationCreationException;
import com.example.CovidPandemySimulation.domain.primitive.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Simulation")
@Getter
@Setter
@NoArgsConstructor
public class Simulation extends BaseEntity
{
    @Column(name = "name", length = 25, unique = true)
    private String name;
    @Column(name = "population_count")
    private long populationCount;
    @Column(name = "initial_infected_number")
    private long initialInfectedNumber;
    @Column(name = "r_number")
    private double rNumber;
    @Column(name = "mortality_rate")
    private double mortalityRate;
    @Column(name = "disease_duration")
    private int diseaseDuration;
    @Column(name = "time_of_dying")
    private int timeOfDying;
    @Column(name = "days_of_simulation")
    private int daysOfSimulation;
    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL)
    private List<Record> records;

    public Simulation(String name, long populationCount,
                      long initialInfectedNumber, double rNumber,
                      double mortalityRate, int diseaseDuration,
                      int timeOfDying, int daysOfSimulation) throws SimulationCreationException
    {
        this.name = name;
        this.populationCount = populationCount;
        this.initialInfectedNumber = initialInfectedNumber;
        this.rNumber = rNumber;
        this.mortalityRate = mortalityRate;
        this.diseaseDuration = diseaseDuration;
        this.timeOfDying = timeOfDying;
        this.daysOfSimulation = daysOfSimulation;
        //this.records = new ArrayList<>();

        //validate();
    }

    private void validate() throws SimulationCreationException
    {
        if (populationCount > initialInfectedNumber)
            throw new SimulationCreationException("The initial number of infected must not exceed the number of the population");
        if (rNumber < 0.0)
            throw new SimulationCreationException("R number cannot be negative");
        if (mortalityRate < 0.0 || mortalityRate > 1.0)
            throw new SimulationCreationException("Mortality rate must be between 0 and 1");
        if (populationCount < initialInfectedNumber)
            throw new SimulationCreationException("Infected number cannot exceed the number of population");
        if (timeOfDying > diseaseDuration)
            throw new SimulationCreationException("Healthy person cannot die of a disease");
    }

    @Override
    public String toString() {
        return "Simulation{" +
                "name='" + name + '\'' +
                ", populationCount=" + populationCount +
                ", initialInfectedNumber=" + initialInfectedNumber +
                ", rNumber=" + rNumber +
                ", mortalityRate=" + mortalityRate +
                ", diseaseDuration=" + diseaseDuration +
                ", timeOfDying=" + timeOfDying +
                ", daysOfSimulation=" + daysOfSimulation +
                '}';
    }

    public void createRecords()
    {
        List<Record> records = new ArrayList<>();
        /* Initial record */
        records.add(new Record(initialInfectedNumber, populationCount - initialInfectedNumber, 0, 0, this));

        /*
        Arrays that store information about how many ppl got sick
        The for loop will use dynamic programming
         */
        long[] sickPeopleWaitingForRecovery = new long[diseaseDuration];
        long[] sickPeopleWaitingForDeath = new long[timeOfDying];

        sickPeopleWaitingForDeath[0] = Math.round(initialInfectedNumber * mortalityRate);
        sickPeopleWaitingForRecovery[0] = initialInfectedNumber - sickPeopleWaitingForDeath[0];

        boolean areAnyRestricitons = false;

        for (int i=1 ; i<daysOfSimulation ; i++)
        {
            long infectedCount = records.get(i-1).getInfectedCount();
            long susceptibleToInfection = records.get(i-1).getSusceptibleToInfection();
            long deathCount = records.get(i-1).getDeathCount();
            long resistantCount = records.get(i-1).getResistantCount();
            int sickPeopleWaitingForRecoveryIndex = i%diseaseDuration;
            int sickPeopleWaitingForDeathIndex = i%timeOfDying;

            long newInfectedNumber = Math.round(rNumber * infectedCount) - infectedCount;



            if (newInfectedNumber > susceptibleToInfection)
                newInfectedNumber = susceptibleToInfection;

            resistantCount += sickPeopleWaitingForRecovery[sickPeopleWaitingForRecoveryIndex];
            infectedCount -= sickPeopleWaitingForRecovery[sickPeopleWaitingForRecoveryIndex];

            deathCount += sickPeopleWaitingForDeath[sickPeopleWaitingForDeathIndex];
            infectedCount -= sickPeopleWaitingForDeath[sickPeopleWaitingForDeathIndex];

            sickPeopleWaitingForDeath[sickPeopleWaitingForDeathIndex] = Math.round(newInfectedNumber * mortalityRate);
            sickPeopleWaitingForRecovery[sickPeopleWaitingForRecoveryIndex] = newInfectedNumber - sickPeopleWaitingForDeath[sickPeopleWaitingForDeathIndex];

            infectedCount += newInfectedNumber;
            susceptibleToInfection -= newInfectedNumber;

            records.add(new Record(
                    infectedCount,
                    susceptibleToInfection,
                    deathCount,
                    resistantCount,
                    this
            ));
        }
        this.records = records;
    }
}
