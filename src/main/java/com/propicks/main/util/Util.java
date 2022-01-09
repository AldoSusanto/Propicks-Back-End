package com.propicks.main.util;

import com.propicks.main.controller.request.userpicks.UserPicks;
import com.propicks.main.entity.GraphicCardsEntity;
import com.propicks.main.entity.ProcessorEntity;

import java.util.ArrayList;
import java.util.List;

public class Util {

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
}
