package com.bandweaver.maxsec.dto;


import com.bandweaver.maxsec.entity.Management;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理部门的树节点
 */
@Data
public class TreeManagement {

    private int id;
    private String name;
    private String position;
    private int level;
    private int fatherId;
    private String map;
    private boolean leaf;
    private List<TreeManagement> children;

    public TreeManagement(){
        this.id = 0;
        this.name = "";
        this.position = "";
        this.level = 0;
        this.map = "";
        this.fatherId = 0;
        this.leaf = false;
        this.children = new ArrayList<>();
    }

    public TreeManagement(Management management) {
        this.id = management.getId().intValue();
        this.name = management.getName();
        this.position = management.getPosition();
        this.level = management.getLevel();
        this.map = management.getMap();
        this.fatherId = management.getFatherId();
        this.leaf = management.isLeaf();
        this.children = new ArrayList<>();
    }
}
