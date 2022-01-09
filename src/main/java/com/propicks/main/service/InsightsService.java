package com.propicks.main.service;

import com.propicks.main.controller.request.userpicks.UserPicks;
import com.propicks.main.controller.response.LaptopResponse;
import com.propicks.main.entity.GraphicCardsEntity;
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
import java.util.List;

import static com.propicks.main.util.Util.*;

@Service
@Log4j2
public class InsightsService {

    private InsightsRepository insightsRepository;
    private SoftwareRepository softwareRepository;
    private ProcessorRepository processorRepository;
    private GraphicCardsRepository graphicCardsRepository;

    public InsightsService(InsightsRepository insightsRepository, SoftwareRepository softwareRepository, ProcessorRepository processorRepository, GraphicCardsRepository graphicCardsRepository) {
        this.insightsRepository = insightsRepository;
        this.softwareRepository = softwareRepository;
        this.processorRepository = processorRepository;
        this.graphicCardsRepository = graphicCardsRepository;
    }

    public List<LaptopResponse> generateInsights(List<LaptopResponse> rawTopTen, UserPicks request) {
        List<GraphicCardsEntity> sortedGraphicCardList = graphicCardsRepository.findAllByOrderByGraphicCardRankAsc();
        List<ProcessorEntity> sortedProcessorList = processorRepository.findAllByOrderByProcessorRankAsc();
        for(LaptopResponse laptop : rawTopTen){
            // 1) Check whether ada software yang diinginkan oleh user tapi tidak bisa di run oleh laptopnya
            // 2) Check other possible insights TODO
            List<Insights> insights = new ArrayList<>();

            insights.addAll(checkUnrunnableSoftware(request, laptop, sortedGraphicCardList, sortedProcessorList ));

            laptop.setInsights(insights);

        }
        return rawTopTen;
    }

    private List<Insights> checkUnrunnableSoftware(UserPicks userPicks, LaptopResponse laptop, List<GraphicCardsEntity> sortedGraphicCardList, List<ProcessorEntity> sortedProcessorList) {
        ProcessorEntity laptopProcessor = getProcessor(laptop.getProcessor(), sortedProcessorList);
        GraphicCardsEntity laptopGraphics = getGraphicsCard(laptop.getGraphics(), sortedGraphicCardList);
        List<SoftwareEntity> softwareList = softwareRepository.findByNameIn(gatherAllSoftwareFromUserPicks(userPicks));

        List<Insights> insightsList = new ArrayList<>();

        for(SoftwareEntity software : softwareList){
            ProcessorEntity softwareProcessor = getProcessor(software.getMinimumProcessor(), sortedProcessorList);
            GraphicCardsEntity softwareGraphicCard = getGraphicsCard(software.getMinimumGraphics(), sortedGraphicCardList);

//            if(softwareProcessor.getProcessorRank() < laptopProcessor.getProcessorRank()
//                || softwareGraphicCard.getNtileRank() < laptopGraphics.getNtileRank()
//                || software.getMinimumRam() > laptop.getRam()){
            //todo: test
            if(softwareProcessor.getProcessorRank() <= laptopProcessor.getProcessorRank()
                || softwareGraphicCard.getNtileRank() >= laptopGraphics.getNtileRank()
                || software.getMinimumRam() >= laptop.getRam()){

                // Add Insight
                Insights i = new Insights();
                i.setTitle(software.getName());
                i.setDescription("Laptop ini tidak bisa menjalankan " + software.getName());
                i.setIcon("Warning");
                i.setType("Negative");

                insightsList.add(i);
            }
        }

        return insightsList;
    }
}
