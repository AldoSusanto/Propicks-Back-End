package com.propicks.main.service;

import com.propicks.main.entity.LaptopEntity;
import com.propicks.main.model.UserBudget;
import com.propicks.main.repository.GraphicCardsRepository;
import com.propicks.main.repository.ProcessorRepository;
import com.propicks.main.repository.SoftwareRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PriceRankService {

    private SoftwareRepository softwareRepository;
    private ProcessorRepository processorRepository;
    private GraphicCardsRepository graphicCardsRepository;

    public PriceRankService(SoftwareRepository softwareRepository, ProcessorRepository processorRepository, GraphicCardsRepository graphicCardsRepository) {
        this.softwareRepository = softwareRepository;
        this.processorRepository = processorRepository;
        this.graphicCardsRepository = graphicCardsRepository;
    }


    public List<LaptopEntity> highPriceRank(UserBudget userBudget, List<LaptopEntity> laptopList, Integer numOfResults){
        // 1 Determine the range of price
        // 2 Filter only laptops that fit the price range
        // 3 Pick random laptops and send as top 10

        // 1
        BigDecimal priceRange = userBudget.getMaxBudget().subtract(userBudget.getMinBudget());
        // If user budget is 0 - 8, we increase priceRange to 0 - 8.5
        // If user budget is others, we increase priceRange to priceRange + 1
        // This is because we want to add 500k buffer on min and max budget

        BigDecimal minPrice = priceRange.multiply(new BigDecimal(0.66)).add (userBudget.getMinBudget());
        BigDecimal maxPrice = userBudget.getMaxBudget();

        // 2
        List<LaptopEntity> filteredList = laptopList.stream()
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0
                        && p.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());

        // 3
        Collections.shuffle(filteredList);
        List<LaptopEntity> result = filteredList.stream().limit(numOfResults).collect(Collectors.toList());

        return result;

    }
}
