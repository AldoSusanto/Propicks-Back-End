package com.propicks.main.controller;

import com.propicks.main.controller.request.SoftwareRequest;
import com.propicks.main.controller.response.SoftwareResponse;
import com.propicks.main.entity.SoftwareEntity;
import com.propicks.main.repository.SoftwareRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1")
@Log4j2
public class SoftwareController {

    private SoftwareRepository softwareRepository;

    public SoftwareController(SoftwareRepository softwareRepository){
        this.softwareRepository = softwareRepository;
    }

    @GetMapping("/software/{id}")
    public SoftwareResponse getSoftwareById(@PathVariable(value="id") Integer id){
        SoftwareEntity entity = softwareRepository.getById(id);
        log.info(entity.toString());

        SoftwareResponse response = new SoftwareResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setMinimumProcessor(entity.getMinimumProcessor());
        response.setMinimumRam(entity.getMinimumRam());
        response.setMinimumGraphics(entity.getMinimumGraphics());
        response.setRecommendedProcessor(entity.getRecommendedProcessor());
        response.setRecommendedRam(entity.getRecommendedRam());
        response.setRecommendedGraphics(entity.getRecommendedGraphics());
        response.setStorageSize(entity.getStorageSize());

        return response;
    }

    @PostMapping("/software")
    public String insertSoftware(@RequestBody SoftwareRequest request){

        SoftwareEntity entity = new SoftwareEntity();
        entity.setId(request.getId());
        entity.setName(request.getName());
        entity.setMinimumProcessor(request.getMinimumProcessor());
        entity.setMinimumRam(request.getMinimumRam());
        entity.setMinimumGraphics(request.getMinimumGraphics());
        entity.setRecommendedProcessor(request.getRecommendedProcessor());
        entity.setRecommendedRam(request.getRecommendedRam());
        entity.setRecommendedGraphics(request.getRecommendedGraphics());
        entity.setStorageSize(request.getStorageSize());

        try{
            softwareRepository.save(entity);
        }catch (Exception e){
            log.error("Error when saving processor to DB: {}", e);
            log.error("Processor Entity: {}", entity.toString());

            throw e;
        }

        return "Software with Name: " + request.getName() + " has been inserted successfully";
    }
}
