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

    public LaptopResponse generateLaptopResponse(LaptopEntity entity, LaptopImagesEntity imagesEntity, LaptopLinksEntity linksEntity){
        LaptopResponse response = new LaptopResponse();

        //Checker
        if (imagesEntity.getImageLinkOne().isEmpty()) {
            throw new RuntimeException("Image Link for laptop ID: " + entity.getId() + " is empty");
        }
        if (linksEntity.getLinkOne().isEmpty()) {
            throw new RuntimeException("Link for laptop ID: " + entity.getId() + " is empty");
        }

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
        imagelinks.add(imagesEntity.getImageLinkOne());
        imagelinks.add(imagesEntity.getImageLinkTwo());
        imagelinks.add(imagesEntity.getImageLinkThree());
        imagelinks.add(imagesEntity.getImageLinkFour());
        imagelinks.add(imagesEntity.getImageLinkFive());
        response.setImageLink(imagelinks);

        //todo: Since we already have buttonMessage field, we might not need this anymore
        LaptopLinks link1 = new LaptopLinks();
        link1.setLink(linksEntity.getLinkOne());
        link1.setLinkFrom(linksEntity.getLinkOriginOne() != null &&  linksEntity.getLinkOriginOne().length() > 0 ? "Cek " + linksEntity.getLinkOriginOne() : linksEntity.getLinkOriginOne());
        LaptopLinks link2 = new LaptopLinks();
        link2.setLink(linksEntity.getLinkTwo());
        link2.setLinkFrom(linksEntity.getLinkOriginTwo() != null &&  linksEntity.getLinkOriginTwo().length() > 0 ? "Cek " + linksEntity.getLinkOriginTwo() : linksEntity.getLinkOriginTwo());
        LaptopLinks link3 = new LaptopLinks();
        link3.setLink(linksEntity.getLinkThree());
        link3.setLinkFrom(linksEntity.getLinkOriginThree() != null &&  linksEntity.getLinkOriginThree().length() > 0 ? "Cek " + linksEntity.getLinkOriginThree() : linksEntity.getLinkOriginThree());

        List<LaptopLinks> links = new ArrayList<>();
        links.add(link1);
        links.add(link2);
        links.add(link3);
        response.setLink(links);

        response.setIsSponsored(false); // Set default to false
        response.setSponsorId(entity.getSponsorId());
        response.setButtonMessage(linksEntity.getLinkOriginOne() != null &&  linksEntity.getLinkOriginOne().length() > 0 ? "Cek " + linksEntity.getLinkOriginOne() : linksEntity.getLinkOriginOne());

        response.setReason("Laptop ini bisa menjalankan semua software/game yang anda pilih");
        response.setReview("-");

        return response;
    }
}
