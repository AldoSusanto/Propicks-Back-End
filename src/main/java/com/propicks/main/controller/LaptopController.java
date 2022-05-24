package com.propicks.main.controller;

import com.propicks.main.controller.request.userpicks.UserPicks;
import com.propicks.main.controller.response.LaptopResponse;
import com.propicks.main.controller.response.SponsorLaptopResponse;
import com.propicks.main.controller.transformer.LaptopTransformer;
import com.propicks.main.entity.LaptopEntity;
import com.propicks.main.entity.LaptopImagesEntity;
import com.propicks.main.entity.LaptopLinksEntity;
import com.propicks.main.entity.SponsorLaptopEntity;
import com.propicks.main.model.UserBudget;
import com.propicks.main.repository.LaptopImagesRepository;
import com.propicks.main.repository.LaptopLinksRepository;
import com.propicks.main.repository.LaptopRepository;
import com.propicks.main.repository.SponsorLaptopRepository;
import com.propicks.main.service.InsightsService;
import com.propicks.main.service.SpecificationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("v1")
@Log4j2
public class LaptopController {

    private LaptopRepository laptopRepository;
    private SponsorLaptopRepository sponsorLaptopRepository;
    private LaptopTransformer laptopTransformer;
    private LaptopImagesRepository laptopImagesRepository;
    private LaptopLinksRepository laptopLinksRepository;
    private SpecificationService specificationService;
    private InsightsService insightsService;

    public LaptopController(LaptopRepository laptopRepository, SponsorLaptopRepository sponsorLaptopRepository, LaptopTransformer laptopTransformer, LaptopImagesRepository laptopImagesRepository, LaptopLinksRepository laptopLinksRepository, SpecificationService specificationService, InsightsService insightsService) {
        this.laptopRepository = laptopRepository;
        this.sponsorLaptopRepository = sponsorLaptopRepository;
        this.laptopTransformer = laptopTransformer;
        this.laptopImagesRepository = laptopImagesRepository;
        this.laptopLinksRepository = laptopLinksRepository;
        this.specificationService = specificationService;
        this.insightsService = insightsService;
    }

    @PostMapping("/sponsor-laptops/{id}")
    public SponsorLaptopResponse getSponsorLaptop(@PathVariable(value="id") String id,
                                                  @RequestBody(required = false) UserPicks request){
        SponsorLaptopEntity entity = sponsorLaptopRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Sponsored Laptop ID: " + id + " not found")
        );
        log.info(entity.toString());
        LaptopEntity laptopEntity = laptopRepository.findBySponsorId(id).orElseThrow(
                () -> new RuntimeException("Laptop with Sponsor ID: " + id + " not found")
        );
        LaptopImagesEntity imagesEntity = laptopImagesRepository.findByName(laptopEntity.getName()).get(0);
        LaptopLinksEntity linksEntity = laptopLinksRepository.findById(laptopEntity.getId()).get();

        LaptopResponse laptopResponse = laptopTransformer.generateLaptopResponse(laptopEntity, imagesEntity, linksEntity);

        if (request != null) {
            UserBudget userBudget = specificationService.determineUserBudget(request);
            List<LaptopResponse> topTen = insightsService.generateInsights(List.of(laptopResponse), request, userBudget);
            laptopResponse = topTen.get(0);
        }

        SponsorLaptopResponse sponsorResponse = new SponsorLaptopResponse();
        sponsorResponse.setSponsorId(id);
        sponsorResponse.setLaptop(laptopResponse);
        sponsorResponse.setSponsorName(entity.getSponsorName());
        sponsorResponse.setButtonMessage("Cek " + entity.getSponsorName());
        sponsorResponse.setLinkTo(laptopResponse.getLink().get(0).getLink());
        sponsorResponse.setIsValid(entity.getIsValid());

        return sponsorResponse;
    }

}
