package com.bandweaver.maxsec.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class User implements Serializable {
    private Integer id;
    private String name;
    private String password;
    private Integer managementId;
    private Integer stationId;
}
