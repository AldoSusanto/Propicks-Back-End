package com.propicks.main.controller.transformer;

import com.propicks.main.controller.response.LaptopResponse;
import com.propicks.main.entity.LaptopEntity;
import org.springframework.stereotype.Service;

@Service
public class LaptopTransformer {

    public LaptopResponse generateLaptopResponse(LaptopEntity entity){
        LaptopResponse response = new LaptopResponse();

        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setName(entity.getName());
        response.setPrice(entity.getPrice());
        response.setBrand(entity.getBrand());
        response.setLaptopType(entity.getLaptopType());
        response.setProcessor(entity.getProcessor());
        response.setRam(entity.getRam());
        response.setGraphics(entity.getGraphics());
        response.setStorageType(entity.getStorageType());
        response.setStorageOne(entity.getStorageOne());
        response.setStorageTwo(entity.getStorageTwo());
        response.setDisplay(entity.getDisplay());
        response.setIsTouchscreen(entity.getIsTouchscreen());
        response.setWeightGrams(entity.getWeightGrams());
        response.setSize(entity.getSize());
        response.setDescription(entity.getDescription());

        return response;
    }
}
