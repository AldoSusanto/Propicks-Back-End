package com.propicks.main.controller.transformer;

import com.propicks.main.controller.response.LaptopResponse;
import com.propicks.main.entity.LaptopEntity;
import com.propicks.main.entity.LaptopImagesEntity;
import com.propicks.main.entity.LaptopLinksEntity;
import com.propicks.main.model.LaptopLinks;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LaptopTransformer {

    public LaptopResponse generateLaptopResponse(LaptopEntity entity, LaptopImagesEntity laptopImagesEntity, LaptopLinksEntity laptopLinks){
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

        List<String> imagelinks = new ArrayList<>();
        imagelinks.add(laptopImagesEntity.getImageLinkOne());
        imagelinks.add(laptopImagesEntity.getImageLinkTwo());
        imagelinks.add(laptopImagesEntity.getImageLinkThree());
        imagelinks.add(laptopImagesEntity.getImageLinkFour());
        imagelinks.add(laptopImagesEntity.getImageLinkFive());
        response.setImageLink(imagelinks);

        //todo: Needs refactoring
        LaptopLinks link1 = new LaptopLinks();
        link1.setLink(laptopLinks.getLinkOne());
        link1.setLinkFrom(laptopLinks.getLinkOriginOne());
        LaptopLinks link2 = new LaptopLinks();
        link2.setLink(laptopLinks.getLinkTwo());
        link2.setLinkFrom(laptopLinks.getLinkOriginTwo());
        LaptopLinks link3 = new LaptopLinks();
        link3.setLink(laptopLinks.getLinkThree());
        link3.setLinkFrom(laptopLinks.getLinkOriginThree());

        List<LaptopLinks> links = new ArrayList<>();
        links.add(link1);
        links.add(link2);
        links.add(link3);
        response.setLink(links);

        return response;
    }
}
