package com.propicks.main.controller;

import com.propicks.main.controller.request.userpicks.UserPicks;
import com.propicks.main.controller.response.LaptopResponse;
import com.propicks.main.entity.LaptopEntity;
import com.propicks.main.entity.SponsorLaptopEntity;
import com.propicks.main.model.LaptopLinks;
import com.propicks.main.model.RecommendedSpecs;
import com.propicks.main.model.UserBudget;
import com.propicks.main.repository.LaptopRepository;
import com.propicks.main.repository.SponsorLaptopRepository;
import com.propicks.main.service.InsightsService;
import com.propicks.main.service.PriceRankService;
import com.propicks.main.service.RankingService;
import com.propicks.main.service.SpecificationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("v1")
@Log4j2
public class RecommendationController {

    private SpecificationService specificationService;
    private RankingService rankingService;
    private LaptopRepository laptopRepository;
    private PriceRankService priceRankService;
    private InsightsService insightsService;
    private SponsorLaptopRepository sponsorLaptopRepository;

    public RecommendationController(SpecificationService specificationService,
                                    RankingService rankingService,
                                    LaptopRepository laptopRepository,
                                    PriceRankService priceRankService,
                                    InsightsService insightsService,
                                    SponsorLaptopRepository sponsorLaptopRepository) {
        this.specificationService = specificationService;
        this.rankingService = rankingService;
        this.laptopRepository = laptopRepository;
        this.priceRankService = priceRankService;
        this.insightsService = insightsService;
        this.sponsorLaptopRepository = sponsorLaptopRepository;
    }

    @GetMapping("/")
    public String healthCheck(){
        return "Hello World ! V1.2.14";
    }

    @CrossOrigin
    @PostMapping("/recommendation")
    public List<LaptopResponse> generateRecommendation(@RequestBody UserPicks request){
        // 1) Determine the specs needed by user
        // 2) Parse/determine the budget based on user picks
        // 3) Prepare query data & try to query the laptops that meet the minimum specs & meets the budget
            // 3.2) If still fails, need to Create special case
        // 4) Apply the scoring function for each of these laptops, then pick the top 10
        // 5) For each laptop, check which software can it use, which boxes it ticks

        log.info("Received Request: " + request.toString());
        // 1)
        RecommendedSpecs recommendedSpecs = specificationService.calculateSpecifications(request);

        // 2)
        UserBudget userBudget = specificationService.determineUserBudget(request);

        // 3)
        List<String>  processorNamesList = specificationService.determineProcessorList(recommendedSpecs.getMinProcessor());
        List<String>  graphicCardsNamesList = specificationService.determineGraphicCardsList(recommendedSpecs.getMinGraphicsCard());
        List<LaptopEntity> laptopEntityList = laptopRepository.findSuitableLaptops(userBudget.getMinBudget(), userBudget.getMaxBudget(), processorNamesList, recommendedSpecs.getMinRam(), graphicCardsNamesList);

        // 3.2)
        if (laptopEntityList.isEmpty()) {
            laptopEntityList = laptopRepository.findAnyLaptopsInPriceRange(userBudget.getMinBudget(), userBudget.getMaxBudget());
        }

        // 4
        List<LaptopResponse> rawTopTen = new ArrayList<>();
        if (!laptopEntityList.isEmpty()){
            rawTopTen = rankingService.generateTopTen(laptopEntityList, userBudget, recommendedSpecs, request);
        }

        // 5 Add Sponsored Laptops
        List<LaptopEntity> sponsoredLaptopList = laptopRepository.findSuitableSponsorLaptops(userBudget.getMinBudget(), userBudget.getMaxBudget(), processorNamesList, recommendedSpecs.getMinRam(), graphicCardsNamesList);
        List<SponsorLaptopEntity> sponsoredLaptopData = sponsorLaptopRepository.findSuitableSponsorLaptops(userBudget.getMinBudget(), userBudget.getMaxBudget(), processorNamesList, recommendedSpecs.getMinRam(), graphicCardsNamesList);
        List<LaptopResponse> sponsoredLaptops = rankingService.generateTopTen(sponsoredLaptopList, userBudget, recommendedSpecs, request);
        sponsoredLaptops = populateFieldsForSponsor(sponsoredLaptops, sponsoredLaptopData);

        List<LaptopResponse> finalLaptopList = combineLaptopLists(rawTopTen, sponsoredLaptops);


        List<LaptopResponse> topTen = insightsService.generateInsights(finalLaptopList, request, userBudget);

        log.info("Returning {} Response: {}", topTen.size(), topTen.toString());
        return topTen;

    }

    private List<LaptopResponse> populateFieldsForSponsor(List<LaptopResponse> sponsoredLaptops, List<SponsorLaptopEntity> sponsoredLaptopData) {
        for (LaptopResponse laptop : sponsoredLaptops) {
            SponsorLaptopEntity sponsorData = findCorrespondingSponsorLaptop(laptop.getSponsorId(), sponsoredLaptopData);
            laptop.setIsSponsored(true);
            laptop.setSponsorName(sponsorData.getSponsorName());
            laptop.setButtonMessage(sponsorData.getButtonMessage());

            // Link
            LaptopLinks laptopLink = new LaptopLinks();
            laptopLink.setLinkFrom(sponsorData.getButtonMessage());
            laptopLink.setLink(sponsorData.getLinkTo());
            laptop.setLink(List.of(laptopLink));
        }
        return sponsoredLaptops;
    }

    private SponsorLaptopEntity findCorrespondingSponsorLaptop(String sponsorId, List<SponsorLaptopEntity> sponsoredLaptopData) {
        for (SponsorLaptopEntity eachLaptop: sponsoredLaptopData) {
            if (eachLaptop.getSponsorId().equalsIgnoreCase(sponsorId)) {
                return eachLaptop;
            }
        }
        log.error("Unable to find Sponsor Laptop Entity with sponsorId: + " + sponsorId);
        return null;

    }

    private List<LaptopResponse> combineLaptopLists(List<LaptopResponse> rawTopTen, List<LaptopResponse> sponsoredLaptops) {
        List<LaptopResponse> finalList = rawTopTen;
        List<Integer> sponsorPatterns = new ArrayList<>(List.of(1,2,3,4,5)); // In what ranks do u want the sponsor laptops to be placed
        Integer sponsorLaptopIndex = 0;

        for (Integer index : sponsorPatterns) {
            if (sponsorLaptopIndex < sponsoredLaptops.size()) {
                try {
                    // If index doesn't exist, we simply add the sponsored laptops to the end of the list
                    if (index >= finalList.size()) {
                        finalList.add(sponsoredLaptops.get(sponsorLaptopIndex));
                    } else { // But if index exists, then we add the sponsored laptops at the specific index
                        finalList.add(index-1, sponsoredLaptops.get(sponsorLaptopIndex));
                    }
                } catch (Exception e) {
                    log.error("Failed to combine sponsored laptops to original laptops " + e);
                    log.error("Laptop Results: " + rawTopTen.toString());
                    log.error("Sponsored Laptops: " + sponsoredLaptops.toString());
                } finally {
                    sponsorLaptopIndex = sponsorLaptopIndex + 1;
                }
            }
        }

        return finalList;
    }
}


/* 3.? Special case

    - There will be cases when user budget is so low but users want high spec laptops
    - For cases like these, then we shouldn't go with spec first approach, we should go with budget first approach
    - Just pick the laptops that meet his budget (max 30)
    - Then we need to rank them to top 10, I think we can just rank based on specs and secondary factors
    - Then at the end, we go through each laptop and check which softwares will not work on this laptop, and then put this as remarks
    - Make sure we mark this recommendation to be veryLowBudget, so FE can maybe handle a UI like this
*/