package com.example.CovidPandemySimulation.domain.agregate;

import com.example.CovidPandemySimulation.domain.exception.SimulationCreationException;
import com.example.CovidPandemySimulation.domain.primitive.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL)
    private List<Record> records;

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

    public void createRecords()
    {
        List<Record> records = new ArrayList<>();
        records.add(initialRecord());

        int fromInfectedToSusceptible = protectionDuration + diseaseDuration;
        /*
        Arrays that store information about how many ppl got sick
        The for loop will use dynamic programming
         */
        long[] peopleWaitingForDisease = new long[diseaseDuration];
        long[] sickPeopleWaitingForRecovery = new long[diseaseDuration];
        long[] sickPeopleWaitingForDeath = new long[timeOfDying];
        long[] resistancePeopleProtectionDuration = new long[protectionDuration];

        boolean areAnyRestrictions = false;
        double infectionRateTmp = infectionRate;

        simulateInfectionRate(initialInfectedNumber, peopleWaitingForDisease);

        double toBeInfected = 0.0;
        double waitingForDeath = 0.0;

        for (int i=1 ; i<daysOfSimulation ; i++)
        {
            long infectedCount = records.get(i-1).getInfectedCount();
            long susceptibleToInfection = records.get(i-1).getSusceptibleToInfection();
            long deathCount = records.get(i-1).getDeathCount();
            long resistantCount = records.get(i-1).getResistantCount();

            long newInfectedCount = peopleWaitingForDisease[i%diseaseDuration];
            peopleWaitingForDisease[i%diseaseDuration] = 0;
            if (newInfectedCount > susceptibleToInfection)
                newInfectedCount = susceptibleToInfection;

            waitingForDeath += newDyingSimulation(waitingForDeath, newInfectedCount, sickPeopleWaitingForDeath, infectionRateTmp);
            toBeInfected = newInfectedSimulation(toBeInfected, newInfectedCount, peopleWaitingForDisease, infectionRateTmp);

            /*
            Too many new infected
            New restrictions incoming
            */
            if (newInfectedCount > 0.01 * populationCount && !areAnyRestrictions) {
                infectionRateTmp /= 3;
                areAnyRestrictions = true;
            }

            /*
            People think that they don't need restrictions anymore
            R number back to previous value
            */
            if (newInfectedCount < 0.001 * populationCount && areAnyRestrictions) {
                infectionRateTmp *= 3;
                areAnyRestrictions = false;
            }

            resistantCount -= resistancePeopleProtectionDuration[i%protectionDuration];
            susceptibleToInfection += resistancePeopleProtectionDuration[i%protectionDuration];

            resistantCount += sickPeopleWaitingForRecovery[i%diseaseDuration];
            infectedCount -= sickPeopleWaitingForRecovery[i%diseaseDuration];

            deathCount += sickPeopleWaitingForDeath[i%timeOfDying];
            infectedCount -= sickPeopleWaitingForDeath[i%timeOfDying];

            resistancePeopleProtectionDuration[i%protectionDuration] = sickPeopleWaitingForRecovery[i%diseaseDuration];
            sickPeopleWaitingForRecovery[i%diseaseDuration] = newInfectedCount - sickPeopleWaitingForDeath[i%timeOfDying];

            infectedCount += newInfectedCount;
            susceptibleToInfection -= newInfectedCount;
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

    private Record initialRecord()
    {
        return new Record(initialInfectedNumber,
                populationCount - initialInfectedNumber,
                0,
                0,
                this);
    }

    private double newInfectedSimulation(double toBeInfected, long newInfectedCount, long[] peopleWaitingForDisease, double infectionRateTmp)
    {
        toBeInfected += newInfectedCount * infectionRateTmp - newInfectedCount * infectionRateTmp * mortalityRate;
        simulateInfectionRate((long)toBeInfected, peopleWaitingForDisease);
        return toBeInfected - (long)toBeInfected;
    }

    private double newDyingSimulation(double waitingForDeath, long newInfectedCount, long[] peopleWaitingForDeath, double infectionRateTmp)
    {
        waitingForDeath += newInfectedCount * infectionRateTmp * mortalityRate;
        simulateInfectionRate((long)waitingForDeath, peopleWaitingForDeath);
        return waitingForDeath - (long)waitingForDeath;
    }

    private void simulateInfectionRate(long peopleToBeInfected, long[] peopleWaitingForDisease)
    {
        for (int i=0 ; i<peopleWaitingForDisease.length-1 ; i++)
        {
            long sickPeopleForThatDay = randPeopleToBeInfectedPerDay(peopleToBeInfected);
            peopleWaitingForDisease[i] += sickPeopleForThatDay;
            peopleToBeInfected -= sickPeopleForThatDay;
        }
        peopleWaitingForDisease[peopleWaitingForDisease.length-1] += peopleToBeInfected;
    }

    private long randPeopleToBeInfectedPerDay(long peopleToBeInfected)
    {
        Random r = new Random();
        return Math.abs(r.nextLong()) % (peopleToBeInfected+1);
    }
}
