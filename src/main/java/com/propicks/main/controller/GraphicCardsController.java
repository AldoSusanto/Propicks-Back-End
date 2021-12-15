package com.propicks.main.controller;

import com.propicks.main.controller.request.GraphicCardsRequest;
import com.propicks.main.controller.response.GraphicCardsResponse;
import com.propicks.main.entity.GraphicCardsEntity;
import com.propicks.main.repository.GraphicCardsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("v1")
@Log4j2
public class GraphicCardsController {

    private GraphicCardsRepository graphicCardsRepository;

    public GraphicCardsController(GraphicCardsRepository graphicCardsRepository){
        this.graphicCardsRepository = graphicCardsRepository;
    }


    @GetMapping("/graphic-cards/{id}")
    public GraphicCardsResponse getGraphicCardsById(@PathVariable(value="id") Integer id){
        GraphicCardsEntity entity = graphicCardsRepository.getById(id);
        log.info(entity.toString());

        GraphicCardsResponse response = new GraphicCardsResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setGraphicCardRank(entity.getGraphicCardRank());
        response.setBenchmark(entity.getBenchmark());

        return response;
    }

    @PostMapping("/graphic-cards")
    public String insertGraphicCards(@RequestBody GraphicCardsRequest request){

        GraphicCardsEntity entity = new GraphicCardsEntity();
        entity.setId(request.getId());
        entity.setName(request.getName());
        entity.setGraphicCardRank(request.getGraphicCardRank());
        entity.setBenchmark(request.getBenchmark());

        try{
            graphicCardsRepository.save(entity);
        }catch (Exception e){
            log.error("Error when saving processor to DB: {}", e);
            log.error("Graphic Cards Entity: {}", entity.toString());

            throw e;
        }

        return "Graphic Cards with Name: " + request.getName() + " has been inserted successfully";
    }
}
