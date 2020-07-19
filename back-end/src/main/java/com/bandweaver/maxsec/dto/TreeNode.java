package com.bandweaver.maxsec.dto;

import com.bandweaver.maxsec.entity.NetNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点
 */
@Data
public class TreeNode {

    private int id;
    private String name;
    private int level;
    private String type;
    private int usedId;
    private boolean connected;
    private List<TreeNode> children;

    public TreeNode() {
        this.id = 0;
        this.name = "";
        this.connected = true;
        this.level = 0;
        this.type = "root";
        this.usedId = 0;
        this.children = new ArrayList<>();
    }

    public TreeNode(NetNode node) {
        this.id = node.getId();
        this.name = node.getName();
        this.connected = node.isConnected();
        this.level = node.getLevel();
        this.type = node.getType();
        this.usedId = node.getUsedId();
        this.children = new ArrayList<>();
    }
}
