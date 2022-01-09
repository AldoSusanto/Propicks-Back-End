package com.propicks.main.service;

import com.propicks.main.controller.request.userpicks.UserPicks;
import com.propicks.main.entity.GraphicCardsEntity;
import com.propicks.main.entity.ProcessorEntity;
import com.propicks.main.entity.SoftwareEntity;
import com.propicks.main.model.RecommendedSpecs;
import com.propicks.main.model.UserBudget;
import com.propicks.main.repository.GraphicCardsRepository;
import com.propicks.main.repository.ProcessorRepository;
import com.propicks.main.repository.SoftwareRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.propicks.main.util.Util.gatherAllSoftwareFromUserPicks;


@Service
@Log4j2
public class SpecificationService {
    public static final String BUDGET_UNKNOWN = "budgetUnknown";
    public static final Pattern budget = Pattern.compile("([0-9]+)\\-([0-9]+)");

    private SoftwareRepository softwareRepository;
    private ProcessorRepository processorRepository;
    private GraphicCardsRepository graphicCardsRepository;

    public SpecificationService(SoftwareRepository softwareRepository,
                                ProcessorRepository processorRepository,
                                GraphicCardsRepository graphicCardsRepository){
        this.softwareRepository = softwareRepository;
        this.processorRepository = processorRepository;
        this.graphicCardsRepository = graphicCardsRepository;
    }

    public RecommendedSpecs calculateSpecifications(UserPicks userPicks){
        // 1) Query the softwares from the DB & get list of specs
        // 2) Find the maximum of each lists

        // 1)
        List<SoftwareEntity> entityList = softwareRepository.findByNameIn(gatherAllSoftwareFromUserPicks(userPicks));
        List<String> minProcessorList = new ArrayList<>();
        List<Integer> minRamList = new ArrayList<>();
        List<String> minGraphicsList = new ArrayList<>();
        List<String> recProcessorList = new ArrayList<>();
        List<Integer> recRamList = new ArrayList<>();
        List<String> recGraphicsList = new ArrayList<>();
        Integer storageUsed = 0;

        for (SoftwareEntity entity : entityList){
            minProcessorList.add(entity.getMinimumProcessor());
            minRamList.add(entity.getMinimumRam());
            minGraphicsList.add(entity.getMinimumGraphics());
            recProcessorList.add(entity.getRecommendedProcessor());
            recRamList.add(entity.getRecommendedRam());
            recGraphicsList.add(entity.getRecommendedGraphics());
            storageUsed = storageUsed + entity.getStorageSize();
        }

        // 2)
        RecommendedSpecs recommendedSpecs = new RecommendedSpecs();

        // Processors
        List<ProcessorEntity> sortedProcessorList = processorRepository.findAllByOrderByProcessorRankAsc();
        recommendedSpecs.setMinProcessor(GetMaxProcessor(minProcessorList, sortedProcessorList));
        recommendedSpecs.setRecProcessor(GetMaxProcessor(recProcessorList, sortedProcessorList));

        // Ram
        recommendedSpecs.setMinRam(Collections.max(minRamList));
        recommendedSpecs.setRecRam(Collections.max(recRamList));

        // Graphics
        List<GraphicCardsEntity> sortedGraphicCardList = graphicCardsRepository.findAllByOrderByGraphicCardRankAsc();
        recommendedSpecs.setMinGraphicsCard(GetMaxGraphicCard(minGraphicsList, sortedGraphicCardList));
        recommendedSpecs.setRecGraphicsCard(GetMaxGraphicCard(recGraphicsList, sortedGraphicCardList));

        recommendedSpecs.setStorage(storageUsed);
        recommendedSpecs.setIsMinimum(true); // Usage of isMinimum is Deprecated, can be removed

        return recommendedSpecs;
    }

    private GraphicCardsEntity GetMaxGraphicCard(List<String> graphicCardsList, List<GraphicCardsEntity> sortedGraphicCardList) {
        for (GraphicCardsEntity entity : sortedGraphicCardList){
            if(graphicCardsList.contains(entity.getName())){
                return entity;
            }
        }

        log.error("Error occured, unable to find max graphic Card. Graphic Cards: " + graphicCardsList.toString());
        throw new RuntimeException("Graphic Cards unidentified");

    }

    private ProcessorEntity GetMaxProcessor(List<String> processorList, List<ProcessorEntity> sortedProcessorList) {
        for(ProcessorEntity entity : sortedProcessorList){
            if(processorList.contains(entity.getName())){
                return entity;
            }
        }

        log.error("Error occured, unable to find max processor. Processors: " + processorList.toString());
        throw new RuntimeException("Processor unidentified");
    }

    public UserBudget determineUserBudget(UserPicks userPicks){
        UserBudget userBudget = new UserBudget();
        BigDecimal minBudget;
        BigDecimal maxBudget;

        if(userPicks.getPriceRange().equals(BUDGET_UNKNOWN)){
            //todo: Check BE Techdebt, this implementation needs to change in the future
            switch (userPicks.getPricePref()){
                case "LOW":
                    minBudget = new BigDecimal(500000); // start at 500k because value will be reduced later on
                    maxBudget = new BigDecimal(8000000);
                    break;
                case "MEDIUM":
                    minBudget = new BigDecimal(8000000);
                    maxBudget = new BigDecimal(15000000);
                    break;
                case "HIGH":
                    minBudget = new BigDecimal(15000000);
                    maxBudget = new BigDecimal(99000000);
                    break;
                default:
                    log.error("Price Preference Unidentifiable: {}", userPicks.getPricePref());
                    throw new RuntimeException("Price Preference Unidentifiable");
            }
        }else{
            Matcher matcher = budget.matcher(userPicks.getPriceRange());
            if(matcher.find()){
                minBudget = BigDecimal.valueOf(Integer.parseInt(matcher.group(1))).multiply(BigDecimal.valueOf(1000000));
                maxBudget = BigDecimal.valueOf(Integer.parseInt(matcher.group(2))).multiply(BigDecimal.valueOf(1000000));
            }else{
                log.error("Price Range unidentifiable: {}", userPicks.getPriceRange());
                throw new RuntimeException("Price Range Unidentifiable");
            }
        }

        // Expand the range by +- 500k to include 7.999.999 type of prices
        minBudget = minBudget.subtract(new BigDecimal(500000));
        maxBudget = maxBudget.add(new BigDecimal(500000));

        userBudget.setMinBudget(minBudget);
        userBudget.setMaxBudget(maxBudget);

        return userBudget;
    }

    public List<String> determineProcessorList(ProcessorEntity processor) {
        List<ProcessorEntity> processorList = processorRepository.findByProcessorRankLessThanEqualOrderByProcessorRankAsc(processor.getProcessorRank());
        return processorList.stream().map(ProcessorEntity::getName).collect(Collectors.toList());
    }

    public List<String> determineGraphicCardsList(GraphicCardsEntity recGraphicsCard) {
        List<GraphicCardsEntity> graphicCardsEntityList = graphicCardsRepository.findByNtileRankLessThanEqualOrderByGraphicCardRankAsc(recGraphicsCard.getNtileRank());
        return graphicCardsEntityList.stream().map(GraphicCardsEntity::getName).collect(Collectors.toList());
    }
}
