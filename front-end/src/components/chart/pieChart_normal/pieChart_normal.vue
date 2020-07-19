<template>
  <div class="pieChart_normal-wrap" :id="id" ref="element"></div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from "vue-property-decorator";
import { ChartData, Series } from "@/types/chart.interface";
import { EChartOption, ECharts } from "echarts";

@Component({})
export default class Pie_Normal extends Vue {
  @Prop({
    required: true,
    default: "1",
  })
  id!: string;

  @Prop({
    required: true,
  })
  data!: ChartData;

  @Prop({
    required: false,
    default: null,
  })
  option!: EChartOption;

  myChart!: ECharts;

  EChartsTheme: any = {
    titleColor: "#eeeeee",
    textColor: "#aaa",
    color: ["#958EED", "#44F0E9", "#F4749D", "#4DA0F8"],
  };

  @Watch("data", {
    deep: true,
  })
  onDataChanged(val: ChartData, oldVal: ChartData) {
    this.drawChart();
  }

  mounted() {
    this.initData();
    this.drawChart();
    this.resizeChart();
  }

  initData() {
    this.myChart = this.$echarts.init(document.getElementById(this.id));
    // 初始化的样式
    let theme = this.EChartsTheme;
    let constFontSize: string = "9%";
    let legendFontSize: string = "7%";
    let initOption: EChartOption = {
      title: {
        textStyle: {
          color: theme.titleColor,
          fontSize: this.getFontSize("9%"),
        },
        left: "center",
      },
      legend: {
        orient: "vertical",
        left: "left",
        textStyle: {
          color: theme.textColor,
          fontSize: this.getFontSize(legendFontSize),
        },
        icon: "circle",
        itemGap: 10,
        itemWidth: this.getFontSize(legendFontSize) * 2,
        itemHeight: this.getFontSize(legendFontSize),
      },
      tooltip: {
        trigger: "item",
        formatter: "{a} <br/>{b}: {c} ({d}%)",
      },
      series: [
        {
          type: "pie",
          radius: ["0%", "55%"],
          avoidLabelOverlap: false,
          label: {
            formatter: "{b}",
            fontSize: this.getFontSize(constFontSize),
            textColor: theme.textColor,
          },
          labelLine: {
            length: this.getFontSize("10%"),
            length2: this.getFontSize("4%"),
          },
          data: [],
        },
      ],
      textStyle: {
        // color: theme.textColor
      },
      color: theme.color,
    };
    this.myChart.setOption(initOption);

    // 将外部传来的样式导入
    if (this.option != null) {
      this.myChart.setOption(this.option);
    }
  }

  drawChart() {
    this.myChart.showLoading();

    // 整合数据
    let series: Series = this.data.series as Series;
    if (!series) return;

    // 整理数据
    let _series: EChartOption.SeriesPie[] = [];
    let data: EChartOption.SeriesPie.DataObject[] = [];
    let _legends: string[] = [];
    series.data.forEach((element) => {
      data.push({
        name: element.key,
        value: element.value,
      });
      _legends.push(element.key);
    });
    _series.push({
      name: series.name,
      type: "pie",
      data: data,
    });

    // 获得最终option
    let _option: EChartOption = {
      title: {
        show: this.data.title.length > 0,
        text: this.data.title,
      },
      legend: {
        data: _legends,
      },
      series: _series,
    };

    this.myChart.setOption(_option);
    if (this.option) this.myChart.setOption(this.option);
    this.myChart.hideLoading();
  }

  resizeChart() {
    let _this = this;
    window.addEventListener("resize", () => {
      _this.myChart.resize();
      _this.drawChart();
    });
  }

  /**
   * 获取字体大小
   */
  getFontSize(val: number | string): number {
    return Math.min(this.getSizeByWidth(val), this.getSizeByHeight(val));
  }

  /**
   * 根据element的宽度获取百分比
   */
  getSizeByWidth(val: number | string): number {
    if (typeof val === "number") return val;
    if (typeof val === "string") {
      if (val.indexOf("%") > 0) {
        let tmp = parseFloat(val.replace("%", "")) / 100;
        let height = (this.$refs.element as HTMLElement).offsetWidth;
        return Math.round(height * tmp);
      }
    }
    return 0;
  }

  /**
   * 根据element的高度度获取百分比
   */
  getSizeByHeight(val: number | string): number {
    if (typeof val === "number") return val;
    if (typeof val === "string") {
      if (val.indexOf("%") > 0) {
        let tmp = parseFloat(val.replace("%", "")) / 100;
        let height = (this.$refs.element as HTMLElement).offsetHeight;
        return Math.round(height * tmp);
      }
    }
    return 0;
  }
}
</script>

<style lang="less">
.pieChart_normal-wrap {
  height: 100%;
  width: 100%;
}
</style>
