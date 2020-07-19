<template>
    <div class="history_alerm">
        <h4>历史警告</h4>
        <div class="history_line" id="historyId"></div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { ECharts, EChartOption, EChartsSeriesType } from "echarts";

@Component({})
export default class HistoryAlerm extends Vue {
    myChart!: ECharts;
    created() {}
    mounted() {
        this.initChart();
    }
    initChart() {
        this.myChart = this.$echarts.init(document.getElementById("historyId"));
        let initOption: any = {
            grid: {
                left: "3%",
                right: "4%",
                bottom: "35%",
                containLabel: true,
            },
            tooltip: {
                trigger: "axis",
                axisPointer: {
                    type: "line",
                    label: {
                        backgroundColor: "#6a7985",
                    },
                    lineStyle: {
                        color: "#168AF6",
                    },
                },
            },
            xAxis: {
                type: "category",
                boundaryGap: false,
                axisLine: {
                    lineStyle: {
                        color: "#168AF6",
                    },
                },
                axisTick: {
                    show: false,
                },
                axisLabel: {
                    interval: 0,
                },
                data: [
                    "5/27",
                    "5/28",
                    "5/29",
                    "5/30",
                    "5/31",
                    "6/1",
                    "6/2",
                    "6/3",
                    "6/4",
                    "6/5",
                    "6/6",
                    "6/7",
                    "6/8",
                    "6/9",
                ],
            },
            yAxis: {
                show: false,
                axisTick: {},
            },

            series: [
                {
                    name: "告警数",
                    type: "line",
                    stack: "总量",
                    lineStyle: {
                        color: "#168AF6",
                    },
                    areaStyle: {
                        color: {
                            type: "linear",
                            x: 0,
                            y: 0,
                            x2: 0,
                            y2: 1,
                            colorStops: [
                                {
                                    offset: 0,
                                    color: "#168AF6", // 0% 处的颜色
                                },
                                {
                                    offset: 1,
                                    color: "#111E40", // 100% 处的颜色
                                },
                            ],
                            global: false, // 缺省为 false
                        },
                    },
                    smooth: true,
                    itemStyle: {
                        opacity: 0,
                    },
                    data: [
                        620,
                        933,
                        399,
                        765,
                        1151,
                        534,
                        900,
                        430,
                        210,
                        2234,
                        443,
                        1136,
                        555,
                    ],
                },
            ],
        };
        this.myChart.setOption(initOption);
        window.addEventListener("resize", () => this.myChart.resize(), false);
    }
}
</script>

<style lang="less" scoped>
.history_alerm {
    height: 100%;
    background: rgba(255, 255, 255, 0.1);
    background-image: url("../../../assets/images/frame_history.png");
    background-size: 100% 100%;

    h4 {
        line-height: 60px;
        font-size: 22px;
        color: #2ed1f6;
        font-weight: 500;
        padding-left: 21px;
        font-weight: 700;
    }

    .history_line {
        height: 215px;
    }
}
</style>
