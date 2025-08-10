package com.itheima.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.groups.Default;
import lombok.Data;

@Data
public class Feature {
    private Integer id;
    private String filename;

    private String histBlue;
    private String histGreen;
    private String histRed;
    private String hogFeatures;
    private String orbDescriptors;
    private String lbpHist;

    private Integer catId;

    public interface Add extends Default {};
}
