package com.thesis.business.musicinstrument;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {

    private String message;

    private List<String> details = new ArrayList<>();

    public ErrorMessage(String message){
        this.message = message;
    }
}
