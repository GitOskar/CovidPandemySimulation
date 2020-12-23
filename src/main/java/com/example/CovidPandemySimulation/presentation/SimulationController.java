package com.example.CovidPandemySimulation.presentation;

import com.example.CovidPandemySimulation.application.dto.SimulationDTO;
import com.example.CovidPandemySimulation.application.service.SimulationService;
import com.example.CovidPandemySimulation.domain.agregate.Simulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/simulation")
public class SimulationController
{
    @Autowired
    SimulationService simulationService;

    @PostMapping
    public SimulationDTO addSimulation(@RequestBody Simulation simulation)
    {
        simulation.createRecords();
        return new SimulationDTO(simulationService.save(simulation));
    }

    @GetMapping("/all")
    @ResponseBody
    public List<SimulationDTO> getAll()
    {
        List<SimulationDTO> simulationDTOs = new ArrayList<>();

        for(Simulation simulation : simulationService.findAll())
            simulationDTOs.add(new SimulationDTO(simulation));

        return simulationDTOs;
    }

    @GetMapping
    @ResponseBody
    public SimulationDTO getByName(@RequestBody String name)
    {
        List<SimulationDTO> simulationDTOs = new ArrayList<>();

        return new SimulationDTO(simulationService.findByName(name));

        //return simulationDTOs;
    }
}
