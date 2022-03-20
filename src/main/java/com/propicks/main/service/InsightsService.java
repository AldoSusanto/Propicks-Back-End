package com.propicks.main.service;

import com.propicks.main.controller.request.userpicks.UserPicks;
import com.propicks.main.controller.response.LaptopResponse;
import com.propicks.main.entity.GraphicCardsEntity;
import com.propicks.main.entity.InsightsEntity;
import com.propicks.main.entity.ProcessorEntity;
import com.propicks.main.entity.SoftwareEntity;
import com.propicks.main.model.Insights;
import com.propicks.main.repository.GraphicCardsRepository;
import com.propicks.main.repository.InsightsRepository;
import com.propicks.main.repository.ProcessorRepository;
import com.propicks.main.repository.SoftwareRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.propicks.main.util.Util.*;

@Service
@Log4j2
public class InsightsService {

    private InsightsRepository insightsRepository;
    private SoftwareRepository softwareRepository;
    private ProcessorRepository processorRepository;
    private GraphicCardsRepository graphicCardsRepository;
    public static final int INSIGHTS_LIMIT = 6;

    public InsightsService(InsightsRepository insightsRepository, SoftwareRepository softwareRepository, ProcessorRepository processorRepository, GraphicCardsRepository graphicCardsRepository) {
        this.insightsRepository = insightsRepository;
        this.softwareRepository = softwareRepository;
        this.processorRepository = processorRepository;
        this.graphicCardsRepository = graphicCardsRepository;
    }

    public List<LaptopResponse> generateInsights(List<LaptopResponse> rawTopTen, UserPicks request) {
        List<GraphicCardsEntity> sortedGraphicCardList = graphicCardsRepository.findAllByOrderByGraphicCardRankAsc();
        List<ProcessorEntity> sortedProcessorList = processorRepository.findAllByOrderByProcessorRankAsc();
        List<InsightsEntity> dbInsightsList = insightsRepository.findAll();

        // Create a list with size of [total num of insights] then initialize all with 0.
        // For each insights found, we increase the counter by one
        List<Integer> insightsCounter = new ArrayList<>();
        for (int i = 0 ; i < dbInsightsList.size(); i++){
            insightsCounter.add(0);
        }

        for(LaptopResponse laptop : rawTopTen){
            // 1) Check whether ada software yang diinginkan oleh user tapi tidak bisa di run oleh laptopnya
            // 2) Check other possible insights
            List<Insights> insights = new ArrayList<>();

            // 1
            insights.addAll(checkUnrunnableSoftware(request, laptop, dbInsightsList,  sortedGraphicCardList, sortedProcessorList));

            // 2
            // Weight
            insights.addAll(checkLaptopWeight(laptop, dbInsightsList));

            // CPU & HDD & RAM
            insights.addAll(checkMainSpecs(request, laptop, dbInsightsList, sortedProcessorList));

            // Gaming & GCards
            if (request.getGaming() != null && !request.getGaming().getSoftware().isEmpty()){
                insights.addAll(checkGamingInsights(request, laptop, dbInsightsList, sortedGraphicCardList));
            }

            // Secondary Factors
            insights.addAll(checkSecondaryFactors(request, laptop, dbInsightsList));

            //todo
//            updateInsightsCounter(insights)

            insights = sortByPriorityAndFilter(insights);

            laptop.setInsights(insights);

        }

        //todo: check list of insights that should be removed
        //todo: go through each laptopResponse, then create removeRedundantInsights(response.getInsights, redundantInsightsList)
        //todo: This func can use the remove arraylist.remove(object o) func since we overrided the equals
        return rawTopTen;
    }

    private List<Insights> sortByPriorityAndFilter(List<Insights> insights) {
        return insights.stream()
                .sorted(Comparator.comparing(Insights::getPriority))
                .limit(INSIGHTS_LIMIT)
                .collect(Collectors.toList());
    }

    private List<Insights> checkUnrunnableSoftware(UserPicks userPicks,
                                                   LaptopResponse laptop,
                                                   List<InsightsEntity> insightsDBList,
                                                   List<GraphicCardsEntity> sortedGraphicCardList,
                                                   List<ProcessorEntity> sortedProcessorList) {

        ProcessorEntity laptopProcessor = getProcessor(laptop.getProcessor(), sortedProcessorList);
        GraphicCardsEntity laptopGraphics = getGraphicsCard(laptop.getGraphics(), sortedGraphicCardList);
        List<SoftwareEntity> softwareList = softwareRepository.findByNameIn(gatherAllSoftwareFromUserPicks(userPicks));
        List<Insights> insightsList = new ArrayList<>();


        for(SoftwareEntity software : softwareList){
            ProcessorEntity softwareProcessor = getProcessor(software.getMinimumProcessor(), sortedProcessorList);
            GraphicCardsEntity softwareGraphicCard = getGraphicsCard(software.getMinimumGraphics(), sortedGraphicCardList);

            if(softwareProcessor.getProcessorRank() < laptopProcessor.getProcessorRank()
                || softwareGraphicCard.getNtileRank() < laptopGraphics.getNtileRank()
                || software.getMinimumRam() > laptop.getRam()){

                // Add Insight
                Insights i = new Insights();
                i.setTitle(software.getName());
                i.setDescription("Laptop ini tidak bisa menjalankan " + software.getName());
                i.setIcon("exclamation");
                i.setType("Negative");
                i.setPriority(1);

                insightsList.add(i);
            }
        }

        // If all chosen softwares can be run by the laptop we add one positive insight instead
        if(insightsList.isEmpty()) insightsList.add(obtainInsightFromId("1", insightsDBList));

        return insightsList;
    }

    private List<Insights> checkLaptopWeight(LaptopResponse laptop, List<InsightsEntity> DBList) {
        Double weight = laptop.getWeightGrams();
        List<Insights> insightsList = new ArrayList<>();

        // Positive
        if(weight <= 1.30){
            insightsList.add(obtainInsightFromId("2", DBList));
        }else if(weight <= 1.5){
            insightsList.add(obtainInsightFromId("9", DBList));
        }

        // Negative
        if(weight >= 2.3){
            insightsList.add(obtainInsightFromId("18", DBList));
        }else if(weight >= 2.0){
            insightsList.add(obtainInsightFromId("17", DBList));
        }

        return insightsList;
    }

    private List<Insights> checkMainSpecs(UserPicks request, LaptopResponse laptop, List<InsightsEntity> DBList, List<ProcessorEntity> sortedProcessorList) {
        List<Insights> insightsList = new ArrayList<>();

        // CPU
        ProcessorEntity intel7 = getProcessor("Intel Core I7", sortedProcessorList);
        ProcessorEntity intel3 = getProcessor("Intel Core I3", sortedProcessorList);
        ProcessorEntity laptopProcessor = getProcessor(laptop.getProcessor(), sortedProcessorList);

        if(laptopProcessor.getProcessorRank() <= intel7.getProcessorRank()){
            insightsList.add(obtainInsightFromId("3", DBList));
        }

        if(laptopProcessor.getProcessorRank() > intel3.getProcessorRank()){
            insightsList.add(obtainInsightFromId("26", DBList));
        }

        // RAM
        if(laptop.getRam() >= 16) insightsList.add(obtainInsightFromId("4", DBList));

        if(laptop.getRam() <= 4) insightsList.add(obtainInsightFromId("25", DBList));

        // Storage
        boolean hugeSize = (laptop.getStorageOne() + laptop.getStorageTwo()) >= 512;
        boolean isSSD = laptop.getStorageOne() > 0;
        if(hugeSize && isSSD){
            insightsList.add(obtainInsightFromId("28", DBList));
        }else{
            if(hugeSize) insightsList.add(obtainInsightFromId("15", DBList));
            if(isSSD) insightsList.add(obtainInsightFromId("14", DBList));
        }

        // Small Storage
        if(needsHugeStorage(request) && !hugeSize){
            insightsList.add(obtainInsightFromId("19", DBList));
        }

        return insightsList;
    }

    private List<Insights> checkGamingInsights(UserPicks request, LaptopResponse laptop, List<InsightsEntity> DBList, List<GraphicCardsEntity> sortedGraphicCardList) {
        List<Insights> insightsList = new ArrayList<>();

        GraphicCardsEntity laptopGraphics = getGraphicsCard(laptop.getGraphics(), sortedGraphicCardList);

        // Entry/mid/high level gaming
        if(laptopGraphics.getNtileRank() < 3) {
            insightsList.add(obtainInsightFromId("7", DBList));
        } else if (laptopGraphics.getNtileRank() == 4){
            insightsList.add(obtainInsightFromId("6", DBList));
        } else if (laptopGraphics.getNtileRank() == 5){
            insightsList.add(obtainInsightFromId("5", DBList));
        }

        // Todo:
        // Not gaming focused
        // Cocok untuk design
        // Cocok untuk kerja kantor/kampus
        // Cocok untuk gaming
        return insightsList;
    }

    private List<Insights> checkSecondaryFactors(UserPicks request, LaptopResponse laptop, List<InsightsEntity> DBList) {
        List<Insights> insightsList = new ArrayList<>();

        // Touchscreen
        if(laptop.getIsTouchscreen()){
            insightsList.add(obtainInsightFromId("16", DBList));
        }

        // Not touchscreen
        // Only show if user requests touchscreen
        if(!request.getTouchScreen().isEmpty() && !laptop.getIsTouchscreen()){
            insightsList.add(obtainInsightFromId("8", DBList));
        }


        // Brand
        if(request.getBrand().stream().anyMatch(laptop.getBrand()::equalsIgnoreCase)){
            insightsList.add(obtainInsightFromId("20", DBList));
        }

        // Size
        double difference = Math.abs(laptop.getSize() - Double.parseDouble(request.getSize()));
        if (difference > 1.0) {
            insightsList.add(obtainInsightFromId("27", DBList));
        }

        return insightsList;
    }
}
