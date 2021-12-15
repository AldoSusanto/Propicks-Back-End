package com.propicks.main.controller;

import com.propicks.main.controller.request.ProcessorRequest;
import com.propicks.main.controller.response.ProcessorResponse;
import com.propicks.main.entity.ProcessorEntity;
import com.propicks.main.repository.ProcessorRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1")
@Log4j2
public class ProcessorController {

    private ProcessorRepository processorRepository;

    public ProcessorController(ProcessorRepository processorRepository){
        this.processorRepository = processorRepository;
    }

    @GetMapping("/processor/{id}")
    public ProcessorResponse getProcessorById(@PathVariable(value="id") Integer id){
        ProcessorEntity processorEntity = processorRepository.getById(id);
        log.info(processorEntity.toString());
        ProcessorResponse response = new ProcessorResponse();
        response.setId(processorEntity.getId());
        response.setName(processorEntity.getName());
        response.setProcessorRank(processorEntity.getProcessorRank());

        return response;
    }

    @PostMapping("/processor")
    public String insertProcessor(@RequestBody ProcessorRequest request){

        ProcessorEntity processorEntity = new ProcessorEntity();
        processorEntity.setName(request.getName());
        processorEntity.setProcessorRank(request.getProcessorRank());

        try{
            processorRepository.save(processorEntity);
        }catch (Exception e){
            log.error("Error when saving processor to DB: {}", e);
            log.error("Processor Entity: {}", processorEntity.toString());

            throw e;
        }

        return "Processor with Name: " + request.getName() + " has been inserted successfully";
    }
}
