package com.propicks.main.util;

import com.propicks.main.controller.request.userpicks.UserPicks;
import com.propicks.main.entity.GraphicCardsEntity;
import com.propicks.main.entity.InsightsEntity;
import com.propicks.main.entity.ProcessorEntity;
import com.propicks.main.model.Insights;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static final String ACTV_GAMING = "gaming";
    public static final String FILM_OFFLINE = "filmOffline";
    public static final String INTERNAL = "internal";
    public static final String PIC_HIGH = "picHigh";

    public static List<String> gatherAllSoftwareFromUserPicks(UserPicks userPicks){
        List<String> softwareList = new ArrayList<>();
        softwareList.addAll(userPicks.getImageGraphics().getSoftware());
        softwareList.addAll(userPicks.getGaming().getSoftware());
        softwareList.addAll(userPicks.getVideoEditing().getSoftware());
        softwareList.addAll(userPicks.getThreeDGraphics().getSoftware());
        if(userPicks.getActivities().contains("Microsoft Office"))softwareList.add("Microsoft Office");
        if(softwareList.isEmpty()) softwareList.add("Test Software");

        return softwareList;
    }

    public static ProcessorEntity getProcessor(String processorName, List<ProcessorEntity> processorList) {
        return processorList.stream()
                .filter(cpu -> cpu.getName().equalsIgnoreCase(processorName))
                .findAny().get();
    }

    public static GraphicCardsEntity getGraphicsCard(String gCardName, List<GraphicCardsEntity> gCardList) {
        return gCardList.stream()
                .filter(gCard -> gCard.getName().equalsIgnoreCase(gCardName))
                .findAny().get();
    }

    public static Insights obtainInsightFromId(String id, List<InsightsEntity> insightsDBList){
        InsightsEntity entity = insightsDBList.stream()
                .filter(a -> a.getId().equalsIgnoreCase(id))
                .findFirst().get();

        return generateInsightFromEntity(entity);
    }

    public static Insights generateInsightFromEntity(InsightsEntity entity){
        Insights insights = new Insights();

        insights.setTitle(entity.getTitle());
        insights.setType(entity.getType());
        insights.setIcon(entity.getIcon());
        insights.setDescription(entity.getDescription());
        insights.setPriority(entity.getPriority());

        return insights;

    }


    public static boolean needsHugeStorage(UserPicks request) {
        // Watches Film Offline & Uses Internal HD
        if(request.getFilm().getMethod().equalsIgnoreCase(FILM_OFFLINE) && request.getFilm().getHd().equalsIgnoreCase(INTERNAL)){
            return true;
        }

        // Edits High qual pictures & Uses Internal HD
        if(request.getImageGraphics().getImage().getQuality().equalsIgnoreCase(PIC_HIGH) && request.getImageGraphics().getImage().getHd().equalsIgnoreCase(INTERNAL)){
            return true;
        }

        // If installs many games
        if(request.getGaming().getSoftware().size() >= 3){
            return true;
        }

        return false;
    }
}
