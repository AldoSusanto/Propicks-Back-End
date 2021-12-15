package com.propicks.main.controller.request.userpicks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoEditingPicks {

    private List<String> software;
    private String hd;

}
