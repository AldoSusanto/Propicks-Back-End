package com.propicks.main.service;

import com.propicks.main.controller.request.userpicks.UserPicks;
import com.propicks.main.controller.response.LaptopResponse;
import com.propicks.main.controller.transformer.LaptopTransformer;
import com.propicks.main.entity.*;
import com.propicks.main.model.*;
import com.propicks.main.repository.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RankingService {
    public static final Double priceDirectionPower = 60.0;
    public static final Double priceDirectionPowerPerMillion = priceDirectionPower / 7;
    public static final int MAXIMUM_RANK_RAM= 64;
    public static final String LIGHT = "light";
    public static final String MEDIUM = "medium";
    public static final Double MIN_WEIGHT_IN_DB = 0.75;
    public static final Double MAX_WEIGHT_IN_DB = 4.0;
    public static final int SECONDARY_FACTOR_POWER = 50;
    public static final Double MAX_PROCESSOR_SCORE = 100.0;
    public static final Double MAX_RAM_SCORE = 100.0;
    public static final Double MAX_GRAPHICS_CARD_SCORE = 100.0;
    public Double MAX_WEIGHT_SCORE = 100.0;

    private SoftwareRepository softwareRepository;
    private ProcessorRepository processorRepository;
    private GraphicCardsRepository graphicCardsRepository;
    private LaptopImagesRepository laptopImagesRepository;
    private LaptopLinksRepository laptopLinksRepository;
    private LaptopTransformer laptopTransformer;

    public RankingService(SoftwareRepository softwareRepository,
                          ProcessorRepository processorRepository,
                          GraphicCardsRepository graphicCardsRepository,
                          LaptopImagesRepository laptopImagesRepository,
                          LaptopLinksRepository laptopLinksRepository,
                          LaptopTransformer laptopTransformer) {
        this.softwareRepository = softwareRepository;
        this.processorRepository = processorRepository;
        this.graphicCardsRepository = graphicCardsRepository;
        this.laptopImagesRepository = laptopImagesRepository;
        this.laptopLinksRepository = laptopLinksRepository;
        this.laptopTransformer = laptopTransformer;
    }

    public List<LaptopResponse> generateTopTen(List<LaptopEntity> laptopEntityList,
                                             UserBudget userBudget,
                                             RecommendedSpecs recommendedSpecs,
                                             UserPicks result){
        List<LaptopResponse> topTen = new ArrayList<>();

        List<BigDecimal> priceDivisions = generatePriceDivisions(userBudget);
        List<LaptopEntity> highPriceList = laptopEntityList.stream().filter(p -> p.getPrice().compareTo(priceDivisions.get(2)) > 0).collect(Collectors.toList());
        List<LaptopEntity> mediumPriceList = laptopEntityList.stream().filter(p -> p.getPrice().compareTo(priceDivisions.get(1)) >= 0
                && p.getPrice().compareTo(priceDivisions.get(2)) <= 0).collect(Collectors.toList());
        List<LaptopEntity> lowPriceList = laptopEntityList.stream().filter(p -> p.getPrice().compareTo(priceDivisions.get(1)) < 0).collect(Collectors.toList());
        List<Integer> resultsRatio = determinePriceRatio(highPriceList.size(), mediumPriceList.size(), lowPriceList.size());

        topTen.addAll(generateTopN(highPriceList, getMidValue(priceDivisions.get(2), priceDivisions.get(3)), recommendedSpecs, result, resultsRatio.get(0) ));
        topTen.addAll(generateTopN(mediumPriceList, getMidValue(priceDivisions.get(1), priceDivisions.get(2)), recommendedSpecs, result, resultsRatio.get(1) ));
        topTen.addAll(generateTopN(lowPriceList, getMidValue(priceDivisions.get(0), priceDivisions.get(1)), recommendedSpecs, result, resultsRatio.get(2) ));

        return topTen;
    }

    private List<Integer> determinePriceRatio(int highListSize, int medListSize, int lowListSize) {
        List<Integer> ratio = new ArrayList<>(Arrays.asList(3, 4, 3));

        if(highListSize <= 5) ratio.set(0, 0);
        if(medListSize <= 5) ratio.set(1, 0);
        if(lowListSize <= 5) ratio.set(2, 0);

        // [3, 4, 0]
        // 3/7 * 10 = 4.28
        // 4/7 * 10 = 5.7
        // If a list that is empty exist, we adjust the price ratio of top ten
        int ratioSum = ratio.stream().mapToInt(value -> value).sum();
        if(ratioSum < 10){
            for(int i = 0 ; i < ratio.size() ; i++){
                float division = (float) ratio.get(i) / ratioSum;
                ratio.set(i, Math.round(division * 10));
            }
        }

        return ratio;
    }

    // This function always return
    private List<BigDecimal> generatePriceDivisions(UserBudget userBudget) {
        // We want to divide the range into 3 sections  |-------|-------|-------|
        //                                             7.5    10.2    12.8    15.5

        List<BigDecimal> result = new ArrayList<>();
        BigDecimal range = userBudget.getMaxBudget().subtract(userBudget.getMinBudget());

        result.add(userBudget.getMinBudget());
        result.add(userBudget.getMinBudget().add(range.multiply(BigDecimal.valueOf(0.33))));
        result.add(userBudget.getMinBudget().add(range.multiply(BigDecimal.valueOf(0.66))));
        result.add(userBudget.getMaxBudget());

        return result;
    }

    // For more information on the algorithms, check
    public List<LaptopResponse> generateTopN(List<LaptopEntity> laptopEntityList,
                                             BigDecimal midPrice,
                                             RecommendedSpecs recommendedSpecs,
                                             UserPicks result,
                                           Integer numOfresults){
        // P) Rank Primary Factors
        // S) Rank Secondary Factors

        List<ScoreSheet> scoreSheetList = new ArrayList<>();
        List<ProcessorEntity> processorEntityList = processorRepository.findAllByOrderByProcessorRankAsc();
        List<GraphicCardsEntity> graphicCardsList = graphicCardsRepository.findAllByOrderByGraphicCardRankAsc();

        // Score adjustments based on user picks
        // Weight
        MAX_WEIGHT_SCORE = generateMaxWeightScore(result);

        // Gamers who don't care about weight
        Integer GRAPHICS_CARD_POWER = 100;
        if (midPrice.compareTo(new BigDecimal(12000000)) >= 0 &&
                (!result.getGaming().getSoftware().isEmpty() || !result.getThreeDGraphics().getSoftware().isEmpty())){
            // If user budget is above 12million & he is either playing games or doing 3D animation, then we care about GCards
            GRAPHICS_CARD_POWER = 400;
             if (result.getWeight().contains("medium")){
                MAX_WEIGHT_SCORE = 25.0;
            }
        }


        for(LaptopEntity entity : laptopEntityList){
//            if (entity.getId().equalsIgnoreCase("10317")){
//                 log.info("Here's the entity");
//            }

            ScoreSheet scoreSheet = new ScoreSheet();
            scoreSheet.setLaptopId(entity.getId());

            // Primary Factors Max Scores
            scoreSheet.setProcessorMaxScore(MAX_PROCESSOR_SCORE);
            scoreSheet.setGraphicsMaxScore(MAX_GRAPHICS_CARD_SCORE);
            scoreSheet.setRamMaxScore(MAX_RAM_SCORE);

            // Primary Factors Scores
            scoreSheet.setPriceScore(generatePriceScore(entity, midPrice));
            scoreSheet.setProcessorScore(generateProcessorScore(entity, processorEntityList,recommendedSpecs));
            scoreSheet.setRamScore(generateRamScore(entity, recommendedSpecs));
            scoreSheet.setGraphicsScore(generateGraphicsScore(entity, graphicCardsList, recommendedSpecs, GRAPHICS_CARD_POWER));
            scoreSheet.setPrimaryTotalScore((scoreSheet.getPriceScore() + scoreSheet.getProcessorScore() + scoreSheet.getRamScore() + scoreSheet.getGraphicsScore()) / 4 );

            // Secondary Factors Max Scores
            scoreSheet.setWeightMaxScore(MAX_WEIGHT_SCORE);
            scoreSheet.setSizeMaxScore(50.0);
            scoreSheet.setTouchscreenMaxScore(50.0);
            scoreSheet.setBrandMaxScore(50.0);

            // Secondary Factors Scores
            scoreSheet.setWeightScore(generateWeightScore(entity, scoreSheet.getWeightMaxScore()));
            scoreSheet.setSizeScore(generateSizeScore(entity.getSize(), Double.valueOf(result.getSize())));
            scoreSheet.setTouchscreenScore(entity.getIsTouchscreen().equals(result.getTouchScreen().equals("YES")) ? 50.0 : 0.00);
            scoreSheet.setBrandScore(result.getBrand().contains(entity.getBrand()) ? 50.0 : 0.00);
            scoreSheet.setSecondaryTotalScore((scoreSheet.getWeightScore() + scoreSheet.getSizeScore() + scoreSheet.getTouchscreenScore() + scoreSheet.getBrandScore()) /
                    (scoreSheet.getWeightMaxScore() + scoreSheet.getSizeMaxScore() + scoreSheet.getTouchscreenMaxScore() + scoreSheet.getBrandMaxScore()) * SECONDARY_FACTOR_POWER);

            scoreSheet.setTotalScore(scoreSheet.getPrimaryTotalScore() + scoreSheet.getSecondaryTotalScore());

            scoreSheetList.add(scoreSheet);
        }

        scoreSheetList.sort(Comparator.comparing(ScoreSheet::getTotalScore).reversed());

        // Change response to include scoreSheet
//        for(int i = 0 ; i < numOfresults ; i ++){
//            String laptopId = scoreSheetList.get(i).getLaptopId();
//            topNresults.add(laptopEntityList.stream().filter(p -> laptopId.equalsIgnoreCase(p.getId())).findFirst()
//                    .orElseThrow(() -> new RuntimeException("Laptop with ID: " + laptopId + " cant be found")));
//        }
        int i = 0;
        List<LaptopResponse> topNresults = new ArrayList<>();
        List<String> laptopNames = new ArrayList<>();
        List<String> laptopIdList = new ArrayList<>();
        while (topNresults.size() < numOfresults && i < scoreSheetList.size()){
            String laptopId = scoreSheetList.get(i).getLaptopId();
            LaptopEntity entity = laptopEntityList.stream().filter(p -> laptopId.equalsIgnoreCase(p.getId())).findFirst()
                    .orElseThrow(() -> new RuntimeException("Laptop with ID: " + laptopId + " cant be found"));

            // If a similar laptop hasn't been added before, we add the laptop to the top N results
            if(!laptopNames.contains(entity.getName())){
                laptopNames.add(entity.getName());
                laptopIdList.add(entity.getId());
                topNresults.add(laptopTransformer.generateLaptopResponse(entity));
            }
            i = i + 1;
        }

        List<LaptopImagesEntity> laptopImagesList = laptopImagesRepository.findByNameIn(laptopNames);
        List<LaptopLinksEntity> laptopLinksEntities = laptopLinksRepository.findAllById(laptopIdList);
        for (LaptopResponse laptopResponse: topNresults) {
            LaptopImagesEntity laptopImagesEntity = laptopImagesList.stream().filter(p -> laptopResponse.getName().equalsIgnoreCase(p.getName())).findFirst()
                    .orElseThrow(() -> new RuntimeException("Images cant be found for Name: " + laptopResponse.getName()));

            List<String> imagelinks = new ArrayList<>();
            imagelinks.add(laptopImagesEntity.getImageLinkOne());
            imagelinks.add(laptopImagesEntity.getImageLinkTwo());
            imagelinks.add(laptopImagesEntity.getImageLinkThree());
            imagelinks.add(laptopImagesEntity.getImageLinkFour());
            imagelinks.add(laptopImagesEntity.getImageLinkFive());
            laptopResponse.setImageLink(imagelinks);

            LaptopLinksEntity laptopLinks = laptopLinksEntities.stream().filter(p -> laptopResponse.getId().equalsIgnoreCase(p.getId())).findFirst()
                    .orElseThrow(() -> new RuntimeException("Links cant be found for ID: " + laptopResponse.getId()));

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
            laptopResponse.setLink(links);
        }

        return topNresults;
    }

    private Double generateSizeScore(Double laptopSize, Double userSize) {
        double difference = Math.abs(laptopSize - userSize);
        if (difference > 1.0){
            return 0.0;
        }else if (difference == 1.0){
            return 25.0;
        }else {
            return 50.0;
        }
    }

    private Double generateMaxWeightScore(UserPicks result) {
        if(result.getWeight().contains(LIGHT) && result.getWeight().contains(MEDIUM)) return 75.0;
        if(result.getWeight().contains(LIGHT)) return 100.0;
        if(result.getWeight().contains(MEDIUM)) return 50.0;

        log.warn("Unable to determine appropriate weight class, returning default of 100");
        return 100.0;
    }

    private Double generateWeightScore(LaptopEntity entity, Double maxWeight) {
        double weightScore = (MIN_WEIGHT_IN_DB - entity.getWeightGrams()) / (MAX_WEIGHT_IN_DB - MIN_WEIGHT_IN_DB) * maxWeight;
        return maxWeight + weightScore;
    }

    private Double generateRamScore(LaptopEntity entity, RecommendedSpecs recommendedSpecs) {
        // Rank the score of the ram (If its 64GB, rank 1 | if its 4GB, rank 5, etc)
        int laptopRank = classifyRAMToRanks(entity.getRam()); // Initialize with rank 1
        if(laptopRank > 5){
            log.error("RAM Rank is {} which is bigger than 5 | Laptop ID: {} which has ram of {}", laptopRank, entity.getId(), entity.getRam());
        }

        int recommendationRank = classifyRAMToRanks(recommendedSpecs.getMinRam());
        int rankDiff = recommendationRank - laptopRank;

        //formula is a bit different from processor since RAM buckets will always be 5 (64, 32, 16, 8, 4), so the increase will always be 20% per bucket
        return Double.valueOf((rankDiff * 20) + 100);

    }

    private int classifyRAMToRanks(Integer input) {
        int maxRam = MAXIMUM_RANK_RAM; //64
        int rank = 1; // Initialize with rank 1

        // We divide 64 n times until we reach the input, that is the rank
        while ((maxRam > input) && (maxRam > 1)){ // We add a safety net to make sure we don't cause infinite loop
            rank = rank + 1;
            maxRam = maxRam / 2;
        }
        return rank;
    }

    // For more details about the algorithm, see note 9 - Primary Specs Ranking Algorithm
    private Double generateGraphicsScore(LaptopEntity currentLaptop, List<GraphicCardsEntity> graphicCardsList, RecommendedSpecs recommendedSpecs, Integer GRAPHICS_CARD_POWER) {
        GraphicCardsEntity currentGraphicCard = graphicCardsList.stream().filter(p -> p.getName().equalsIgnoreCase(currentLaptop.getGraphics())).findFirst()
                .orElseThrow(() -> new RuntimeException("Graphics Card " + currentLaptop.getGraphics() + " is not found in the graphics card list"));

        int rankDiff = recommendedSpecs.getMinGraphicsCard().getNtileRank() - currentGraphicCard.getNtileRank();

        // rankDiff * (Power / Max NTILE) + 100
        return Double.valueOf(rankDiff * (GRAPHICS_CARD_POWER / graphicCardsList.get(graphicCardsList.size() - 1).getNtileRank()) + 100);
    }

    // For more details about the algorithm, see note 9 - Primary Specs Ranking Algorithm
    private Double generateProcessorScore(LaptopEntity currentLaptop, List<ProcessorEntity> processorList, RecommendedSpecs recommendedSpecs) {
        ProcessorEntity currentLaptopEntity = processorList.stream().filter(p -> p.getName().equalsIgnoreCase(currentLaptop.getProcessor())).findFirst()
                .orElseThrow(() -> new RuntimeException("Processor " + currentLaptop.getProcessor() + " is not found in the processor list"));

        int rankDiff = recommendedSpecs.getMinProcessor().getProcessorRank() - currentLaptopEntity.getProcessorRank();

        return Double.valueOf(rankDiff * (100 / (processorList.get(processorList.size() - 1).getProcessorRank())) + 100);
        // We pick the last element in the list which has the highest rank (Ex: 5)
    }

    // For more details about the algorithm, see note 9 - Primary Specs Ranking Algorithm
    private Double generatePriceScore(LaptopEntity entity, BigDecimal midPrice) {
//        UserBudget userBudget = new UserBudget(original.getMinBudget(), original.getMaxBudget());
//        userBudget.setMinBudget(userBudget.getMinBudget().add(new BigDecimal(500000)));
//        userBudget.setMaxBudget(userBudget.getMaxBudget().subtract(new BigDecimal(500000)));
//        double priceRange = userBudget.getMaxBudget().subtract(userBudget.getMinBudget()).doubleValue();
//        double midPrice = userBudget.getMinBudget().doubleValue() + (priceRange/2) ;

        return 100 - Math.abs ((entity.getPrice().doubleValue() - midPrice.doubleValue()) / 1000000 * priceDirectionPowerPerMillion);
    }

    private BigDecimal getMidValue(BigDecimal min, BigDecimal max){
        BigDecimal priceRange = max.subtract(min).divide(BigDecimal.valueOf(2));
        return min.add(priceRange);
    }

}
