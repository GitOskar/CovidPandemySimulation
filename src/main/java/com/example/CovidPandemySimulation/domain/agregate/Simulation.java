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
    private double infectionRate;
    @Column(name = "mortality_rate")
    private double mortalityRate;
    @Column(name = "disease_duration")
    private int diseaseDuration;
    @Column(name = "time_of_dying")
    private int timeOfDying;
    @Column(name = "days_of_simulation")
    private int daysOfSimulation;
    @Column(name = "protection_duration")
    private int protectionDuration;
    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Record> records;
    @Transient
    boolean areAnyRestrictions;
    @Transient
    double dailyInfectionRate;

    public Simulation(String name, long populationCount,
                      long initialInfectedNumber, double infectionRate,
                      double mortalityRate, int diseaseDuration,
                      int timeOfDying, int daysOfSimulation,
                      int protectionDuration) throws SimulationCreationException
    {
        this.name = name;
        this.populationCount = populationCount;
        this.initialInfectedNumber = initialInfectedNumber;
        this.infectionRate = infectionRate;
        this.mortalityRate = mortalityRate;
        this.diseaseDuration = diseaseDuration;
        this.timeOfDying = timeOfDying;
        this.daysOfSimulation = daysOfSimulation;
        this.protectionDuration = protectionDuration;

        validate();

        createRecords();
    }

    private void validate() throws SimulationCreationException
    {
        if (populationCount < initialInfectedNumber)
            throw new SimulationCreationException("The initial number of infected must not exceed the number of the population");
        if (infectionRate < 0.0)
            throw new SimulationCreationException("R number cannot be negative");
        if (mortalityRate < 0.0 || mortalityRate > 1.0)
            throw new SimulationCreationException("Mortality rate must be between 0 and 1");
        if (populationCount < initialInfectedNumber)
            throw new SimulationCreationException("Infected number cannot exceed the number of population");
        if (timeOfDying > diseaseDuration)
            throw new SimulationCreationException("Healthy person cannot die of a disease");
    }

    public void deleteAllRecord()
    {
        records.clear();
    }

    public void createRecords()
    {
        if(records == null)
            records = new ArrayList<>();
        records.add(initialRecord());
        /*
        Arrays that store information about how many ppl got sick
        The for loop will use dynamic programming
         */
        long[] sickPeopleWaitingForRecovery = new long[diseaseDuration];
        long[] sickPeopleWaitingForDeath = new long[timeOfDying];
        long[] resistancePeopleProtection = new long[protectionDuration];

        areAnyRestrictions = false;
        dailyInfectionRate = infectionRate / diseaseDuration;
        double newInfected = 0, newDeaths = 0.0;
        sickPeopleWaitingForRecovery[0] = initialInfectedNumber;

        for (int i=1 ; i<daysOfSimulation ; i++)
        {
            Record newRecord = new Record(records.get(i-1));

            /*
            Infected people died
             */
            if (newRecord.getInfectedCount() < sickPeopleWaitingForDeath[i%timeOfDying])
                sickPeopleWaitingForDeath[i%timeOfDying] = newRecord.getInfectedCount();
            newRecord.addDeaths(sickPeopleWaitingForDeath[i%timeOfDying]);
            sickPeopleWaitingForDeath[i%timeOfDying] = 0;

            /*
            Resistant people have lost their immunity
             */
            newRecord.endOfProtection(resistancePeopleProtection[i%protectionDuration]);
            resistancePeopleProtection[i%protectionDuration] = 0; // not necessary

            /*
            Infected people recovered
             */
            newRecord.addResistant(sickPeopleWaitingForRecovery[i%diseaseDuration]);
            resistancePeopleProtection[i%protectionDuration] = sickPeopleWaitingForRecovery[i%diseaseDuration];
            sickPeopleWaitingForRecovery[i%diseaseDuration] = 0;

            newInfected += newRecord.getInfectedCount() * dailyInfectionRate;
            newDeaths += newRecord.getInfectedCount() * dailyInfectionRate * mortalityRate;
            newInfected -= (long) newDeaths;

            createDailyInfectionRate((long) newInfected + (long) newDeaths);

            if (newInfected + newDeaths > newRecord.getSusceptibleToInfection()) {
                newInfected = newRecord.getSusceptibleToInfection();
                if (newInfected < newDeaths)
                {
                    newDeaths = newInfected;
                    newInfected = 0;
                }
                else
                {
                    newInfected -= newDeaths;
                }
            }

            newRecord.addInfected((long) newDeaths);
            sickPeopleWaitingForDeath[i%timeOfDying] = (long) newDeaths;
            newDeaths -= (long) newDeaths;

            newRecord.addInfected((long) newInfected);
            sickPeopleWaitingForRecovery[i%diseaseDuration] = (long) newInfected;
            newInfected -= (long) newInfected;

            records.add(newRecord);
        }
        //this.records = records;
    }

    private Record initialRecord()
    {
        return new Record(initialInfectedNumber,
                populationCount - initialInfectedNumber,
                0,
                0,
                this);
    }

    private void createDailyInfectionRate(long newInfected)
    {
         /*
            Too many new infected
            New restrictions incoming
            */
        if (newInfected > 0.01 * populationCount && !areAnyRestrictions) {
            areAnyRestrictions = true;
            dailyInfectionRate /= 5;
        }

            /*
            People think that they don't need restrictions anymore
            R number back to previous value
            */
        if (newInfected < 0.001 * populationCount && areAnyRestrictions) {
            dailyInfectionRate *= 5;
            areAnyRestrictions = false;
        }
    }
}