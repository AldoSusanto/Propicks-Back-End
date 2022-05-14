package com.propicks.main.controller.request;

import com.propicks.main.controller.request.userpicks.UserPicks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    private Integer score;
    private String message;
    private UserPicks userPicks;
}
