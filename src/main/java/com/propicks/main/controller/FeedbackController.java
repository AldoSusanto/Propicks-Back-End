package com.propicks.main.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propicks.main.controller.request.FeedbackRequest;
import com.propicks.main.controller.request.userpicks.UserPicks;
import com.propicks.main.entity.FeedbackEntity;
import com.propicks.main.repository.FeedbackRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("v1")
@Log4j2
public class FeedbackController {

    private FeedbackRepository feedbackRepository;

    public FeedbackController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestBody FeedbackRequest feedback) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        UserPicks userPicks = feedback.getUserPicks();
        String jsonPicks = mapper.writeValueAsString(userPicks);

        FeedbackEntity entity = new FeedbackEntity();
        entity.setScore(feedback.getScore());
        entity.setMessage(feedback.getMessage());
        entity.setJsonPicks(jsonPicks);
        entity.setPriceRange(userPicks.getPriceRange());
        entity.setPricePref(userPicks.getPricePref());
        entity.setActivities(userPicks.getActivities().toString());
        entity.setGraphicsSoftware(userPicks.getImageGraphics().getSoftware().toString());
        entity.setGamingSoftware(userPicks.getGaming().getSoftware().toString());
        entity.setVideoSoftware(userPicks.getVideoEditing().getSoftware().toString());
        entity.setGraphicsThreedSoftware(userPicks.getThreeDGraphics().getSoftware().toString());
        entity.setSize(userPicks.getSize());
        entity.setWeight(userPicks.getWeight().contains("Light") ? "Light" : "Medium");
        entity.setIsTouch(userPicks.getTouchScreen().equalsIgnoreCase("True") ? true : false);
        entity.setBrandList(userPicks.getBrand().toString());

        feedbackRepository.save(entity);
        return "Hello World ! V1.2.7";
    }

}
