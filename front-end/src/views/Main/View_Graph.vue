<template>
  <!-- 网络图模块 -->
  <div style="height: 100%; width: 100%; position:relative;">
    <div class="graph-chart-wrap" id="dom_netnodes"></div>
    <img :src="descriptionImg" class="description-img-wrap" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { getTree } from "@/api/netnode";
import { TreeNode } from "@/types/treenode.interface";
import { ECharts, EChartOption, EChartsSeriesType } from "echarts";

@Component({})
export default class ViewGraphClass extends Vue {
  root!: TreeNode;

  /**
   * 当前的根节点
   */
  curRoot!: TreeNode;

  myChart!: ECharts;
  descriptionImg: string = require("@/assets/images/connected-description.png");

  mounted() {
    this.initDate();
    this.initChart();
  }

  initDate() {
    getTree().then((res: any) => {
      let { code, data } = res.data;
      if (code == 200) {
        this.root = data;
        this.root.childrenConnected = this.getChildrenConneted(this.root);

        this.setCurRoot(this.root);
      }
    });
  }

  /**
   * 设定当前根节点
   */
  setCurRoot(node: TreeNode) {
    if (this.curRoot && node.id == this.curRoot.id) return;

    this.curRoot = node;
    this.setGraphData(this.curRoot);
  }

  /** 获取该节点下所有的孩子是否有未连接的 */
  getChildrenConneted(curNode: TreeNode): boolean {
    let f: boolean = true;

    curNode.children.forEach((node: TreeNode) => {
      node.childrenConnected = this.getChildrenConneted(node);
      f = f && node.childrenConnected && node.connected;
    });

    return f;
  }

  /** 初始化echart */
  initChart() {
    this.myChart = this.$echarts.init(document.getElementById("dom_netnodes"));

    let categories: any[] = [];
    let initOption: EChartOption = {
      title: {
        text: "网络连接关系图",
      },
      tooltip: {
        formatter: "{a}<br/>{b}",
      },
      animationDuration: 1500,
      animationEasingUpdate: "quinticInOut",
      series: [
        {
          name: "网络连接关系图",
          type: "graph",
          layout: "force",
          legendHoverLink: true,
          hoverAnimation: true,
          roam: true,
          focusNodeAdjacency: true,
          itemStyle: {
            borderColor: "#fff",
            borderWidth: 1,
            shadowBlur: 10,
            shadowColor: "rgba(0, 0, 0, 0.3)",
          },
          label: {
            position: "right",
            formatter: "{b}",
          },
          lineStyle: {
            curveness: 0.3,
          },
          emphasis: {
            lineStyle: {
              width: 3,
              opacity: 0.5,
            },
          },
          cursor: "pointer",
        },
      ],
    };

    this.myChart.setOption(initOption);
    this.myChart.on("click", this.echartsClickEvent);
    //去除默认的鼠标事件
    document.oncontextmenu = function() {
      return false;
    };
    //新加上鼠标右击事件
    this.myChart.on("contextmenu", this.goBack);
  }

  /** echarts的点击事件 */
  echartsClickEvent(params: any) {
    // 点击的是节点
    if (params.componentType == "series" && params.dataType == "node") {
      let node: TreeNode | null = this.searchTreeNode(
        this.root,
        params.data.id
      );

      if (node) {
        this.setCurRoot(node);
      }
    }
  }

  /** 返回上一层 */
  goBack() {
    let node: TreeNode | null = this.searchFatherNode(
      this.root,
      this.curRoot.id
    );
    if (node) {
      this.setCurRoot(node);
    }
  }

  /**
   * 根据id查找节点
   */
  searchTreeNode(curNode: TreeNode, id: number): TreeNode | null {
    if (curNode.id == id) return curNode;

    for (let index = 0; index < curNode.children.length; index++) {
      const element = curNode.children[index];
      let res: TreeNode | null = this.searchTreeNode(element, id);
      if (res != null) return res;
    }

    return null;
  }

  /**
   * 根据id查找父节点
   */
  searchFatherNode(curNode: TreeNode, id: number): TreeNode | null {
    for (let index = 0; index < curNode.children.length; index++) {
      const element: TreeNode = curNode.children[index];
      if (element.id == id) return curNode;

      let tmp: TreeNode | null = this.searchFatherNode(element, id);
      if (tmp) return tmp;
    }

    return null;
  }

  /**
   * 设置关系图数据
   */
  setGraphData(curNode: TreeNode) {
    let nodes: any[] = [];
    let links: any[] = [];

    console.log("curNode", curNode);
    let img: any = this.getSymbolImg(curNode);

    nodes.push({
      id: curNode.id,
      name: curNode.name,
      category: 0,
      draggable: true,
      symbol: `image://${img}`,
      symbolSize: 50,
      itemStyle: {
        color: "#3af",
        shadowBlur: 10,
        shadowColor: "#a00",
      },
    });

    curNode.children.forEach((node: TreeNode) => {
      let tmp_itemStyle: any = {
        color: "#000",
        shadowBlur: 10,
        shadowColor: "#0aa",
      };

      if (node.connected) {
        tmp_itemStyle.color = "#9a8";
      } else {
        tmp_itemStyle.color = "#ccc";
        tmp_itemStyle.shadowBlur = 0;
      }

      let img: any = this.getSymbolImg(node);
      nodes.push({
        id: node.id,
        name: node.name,
        category: 1,
        draggable: true,
        symbol: `image://${img}`,
        symbolSize: 40,
        itemStyle: tmp_itemStyle,
      });

      let tmp_lineStyle: any = {
        color: "#000",
        width: 1,
        type: "solid",
      };

      if (node.connected) {
        // 如果这个点和父亲是连接状态
        if (node.childrenConnected) {
          // 所有的孩子都在连接状态
          tmp_lineStyle.color = "#00f";
          tmp_lineStyle.type = "solid";
        } else {
          // 有孩子是未连接的
          tmp_lineStyle.color = "#00f";
          tmp_lineStyle.type = "dashed";
        }
      } else {
        // 如果是未连接状态
        tmp_lineStyle.color = "#f00";
        tmp_lineStyle.type = "dashed";
      }

      links.push({
        source: 0,
        target: nodes.length - 1,
        lineStyle: tmp_lineStyle,
      });
    });

    nodes.forEach((node: any) => {
      node.label = {
        normal: {
          show: true,
        },
      };
    });

    let categories: any[] = [];
    for (var i = 0; i < 9; i++) {
      categories[i] = {
        name: "类目" + i,
      };
    }
    this.myChart.setOption({
      series: [
        {
          name: "网络连接关系图",
          type: "graph",
          layout: "force",
          data: nodes,
          links: links,
          categories: categories,
          force: {
            repulsion: 5000,
            gravity: 0.5,
            layoutAnimation: true,
          },
        },
      ],
    });
  }

  /** 获取该节点的图片 */
  getSymbolImg(curNode: TreeNode): any {
    let img: any;

    try {
      img = curNode.connected
        ? require(`@/assets/images/${curNode.type}.png`)
        : require(`@/assets/images/${curNode.type}-error.png`);
    } catch (error) {
      img = curNode.connected
        ? (img = require(`@/assets/images/default.png`))
        : require(`@/assets/images/default-error.png`);
    }

    return img;
  }
}
</script>

<style scoped>
.graph-chart-wrap {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 100%;
}

.description-img-wrap {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 100px;
}
</style>
