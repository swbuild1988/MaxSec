package com.bandweaver.maxsec.controller;

import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.dto.TreeNode;
import com.bandweaver.maxsec.entity.NetNode;
import com.bandweaver.maxsec.service.NetNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@Slf4j
@RestController
public class NetNodeController {

    @Autowired
    private NetNodeService netNodeService;

    @GetMapping("/netnodes")
    public R getAll() {
        return new R(netNodeService.selectAll());
    }

    @PostMapping("/netnodes")
    public R addVideos(@RequestBody NetNode model) {
        model.setCreateTime(new Date());
        log.info("insert node :  " + model.toString());
        netNodeService.insert(model);
        return new R();
    }

    @PostMapping("/netnodes_edit")
    public R editVideos(@RequestBody NetNode model) {
        netNodeService.update(model);
        return new R();
    }

    @GetMapping("/netnodes_delete/{id}")
    public R deleteNetNode(@PathVariable("id") Integer id) {
        netNodeService.delete(id);
        return new R();
    }

    @GetMapping("/netnodes/{id}")
    public R getModel(@PathVariable Integer id) {
        return new R(netNodeService.selectByPrimaryKey(id));
    }

    /**
     * 获取节点的树结构
     *
     * @return
     */
    @GetMapping("/netnodes/tree")
    public R getNodeTree() {
        List<NetNode> netNodes = netNodeService.selectAll();

        TreeNode treeNode = new TreeNode();
        treeNode.setName("可视化平台");

        treeNode = getNodeWithChildren(treeNode, netNodes);

        return new R(treeNode);
    }

    /**
     * 创建当前节点为根的树
     *
     * @param node     当前节点
     * @param netNodes 网络图的所有节点
     * @return
     */
    private TreeNode getNodeWithChildren(TreeNode node, List<NetNode> netNodes) {

        List<TreeNode> children = node.getChildren();

        for (NetNode netNode : netNodes) {
            // 如果netnode为当前节点的儿子，则加上
            if (netNode.getFatherId().intValue() == node.getId()) {
                TreeNode sonNode = new TreeNode(netNode);
                // 递归获取所有的孩子后
                sonNode = getNodeWithChildren(sonNode, netNodes);

                children.add(sonNode);
            }
        }

        node.setChildren(children);

        return node;
    }


}
