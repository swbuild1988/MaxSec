<template>
  <!-- 网络图模块 -->
  <div style="height: 100%; width: 100%; position:relative;">
    <div class="graph-chart-wrap" id="dom_netnodes"></div>
    <img :src="descriptionImg" class="description-img-wrap" />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from "vue-property-decorator";
import { getTree } from "@/api/netnode";
import { TreeNode } from "@/types/treenode.interface";
import { ECharts, EChartOption, EChartsSeriesType } from "echarts";
import { Station } from "../../types/station.interface";

@Component({})
export default class StationGraphClass extends Vue {
  @Prop({
    required: true,
  })
  station!: Station;

  descriptionImg: string = require("@/assets/images/connected-description.png");

  root!: TreeNode;

  /**
   * 当前的根节点
   */
  curRoot!: TreeNode;

  myChart!: ECharts;

  @Watch("station")
  stationchange() {
    this.initDate();
  }

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

        // 找到当前场站的所有节点
        for (let i = 0; i < this.root.children.length; i++) {
          const element: TreeNode = this.root.children[i];
          if (element.usedId == this.station.id) {
            this.curRoot = element;
            break;
          }
        }

        if (this.curRoot) {
          this.setGraphData(this.curRoot);
        } else {
          console.info("未找到当前场站的网络图");
        }
      }
    });
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
  }

  /**
   * 设置关系图数据
   */
  setGraphData(curNode: TreeNode) {
    let nodes: any[] = [];
    let links: any[] = [];

    this.setNodesAndLinks(curNode, 0, nodes, links);

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

  /** 设置所有的nodes和links */
  setNodesAndLinks(
    curNode: TreeNode,
    fatherIndex: number,
    nodes: any[],
    links: any[]
  ) {
    let img: any = this.getSymbolImg(curNode);
    let param: any = this.getItemStyle(curNode.level);

    nodes.push({
      id: curNode.id,
      name: curNode.name,
      category: 0,
      draggable: true,
      symbol: `image://${img}`,
      symbolSize: param.symbolSize,
      itemStyle: {
        color: curNode.connected ? param.color : "#ccc",
        shadowBlur: curNode.connected ? param.shadowBlur : 0,
        shadowColor: param.shadowColor,
      },
    });

    let tmp_lineStyle: any = {
      color: "#000",
      width: 1,
      type: "solid",
    };

    if (curNode.connected) {
      // 如果这个点和父亲是连接状态
      if (curNode.childrenConnected) {
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

    let curIndex = nodes.length - 1;
    if (fatherIndex != curIndex) {
      links.push({
        source: fatherIndex,
        target: curIndex,
        lineStyle: tmp_lineStyle,
      });
    }

    curNode.children.forEach((node: TreeNode) => {
      this.setNodesAndLinks(node, curIndex, nodes, links);
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

  /** 不同等级的样式 */
  getItemStyle(level: number): any {
    let param: any = {
      symbolSize: 30,
      color: "#3af",
      shadowBlur: 10,
      shadowColor: "#a00",
    };

    switch (level) {
      case 1:
        param.symbolSize = 80;
        param.color = "#3af";
        param.shadowBlur = 10;
        param.shadowColor = "#a00";
        break;

      case 2:
        param.symbolSize = 50;
        param.color = "#9a8";
        param.shadowBlur = 10;
        param.shadowColor = "#0aa";
        break;

      case 3:
        param.symbolSize = 30;
        param.color = "#d5c";
        param.shadowBlur = 10;
        param.shadowColor = "#e6d";

        break;

      default:
        break;
    }

    return param;
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
