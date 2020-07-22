<template>
    <div class="growth_trend">
        <h4>
            <span class="icon"></span>
            增长趋势
        </h4>
        <div
            class="growth_trend_line"
            :style="{ height: growth_trend_line_height + 'px' }"
        >
            <div
                v-for="(item, index) in showList"
                :key="index"
                :style="{
                    height: growth_trend_line_height / showList.length + 'px',
                }"
                :id="`trend_${index}`"
            >
                {{ item }}
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { ECharts, EChartOption, EChartsSeriesType } from "echarts";
import { getAlarmTrend } from "@/api/alarm";

@Component({})
export default class GrowthTrend extends Vue {
    trendData: any[] = [];
    // 高度
    growth_trend_line_height = 160;

    // 显示的趋势线条目
    showList: string[] = [];

    mounted() {
        this.showList = this.VisualizedParams.GrouthTrend.ShowList;
        this.init();
    }

    init() {
        getAlarmTrend()
            .then((res: any) => {
                let { code, data } = res.data;
                if (code == 200) {
                    this.trendData = data;
                }
            })
            .finally(() => {
                if (this.TestData) {
                    this.trendData = [
                        {
                            val: [
                                {
                                    val: 20,
                                    key: "2020/06/30",
                                },
                                {
                                    val: 31,
                                    key: "2020/07/01",
                                },
                                {
                                    val: 42,
                                    key: "2020/07/02",
                                },
                                {
                                    val: 35,
                                    key: "2020/07/03",
                                },
                                {
                                    val: 44,
                                    key: "2020/07/04",
                                },
                                {
                                    val: 54,
                                    key: "2020/07/05",
                                },
                                {
                                    val: 35,
                                    key: "2020/07/06",
                                },
                            ],
                            key: "视频",
                        },
                        {
                            val: [
                                {
                                    val: 10,
                                    key: "2020/06/30",
                                },
                                {
                                    val: 12,
                                    key: "2020/07/01",
                                },
                                {
                                    val: 32,
                                    key: "2020/07/02",
                                },
                                {
                                    val: 25,
                                    key: "2020/07/03",
                                },
                                {
                                    val: 19,
                                    key: "2020/07/04",
                                },
                                {
                                    val: 30,
                                    key: "2020/07/05",
                                },
                                {
                                    val: 10,
                                    key: "2020/07/06",
                                },
                            ],
                            key: "高后果视频",
                        },
                        {
                            val: [
                                {
                                    val: 12,
                                    key: "2020/06/30",
                                },
                                {
                                    val: 30,
                                    key: "2020/07/01",
                                },
                                {
                                    val: 10,
                                    key: "2020/07/02",
                                },
                                {
                                    val: 6,
                                    key: "2020/07/03",
                                },
                                {
                                    val: 13,
                                    key: "2020/07/04",
                                },
                                {
                                    val: 20,
                                    key: "2020/07/05",
                                },
                                {
                                    val: 12,
                                    key: "2020/07/06",
                                },
                            ],
                            key: "周界入侵",
                        },
                        {
                            val: [
                                {
                                    val: 1,
                                    key: "2020/06/30",
                                },
                                {
                                    val: 3,
                                    key: "2020/07/01",
                                },
                                {
                                    val: 2,
                                    key: "2020/07/02",
                                },
                                {
                                    val: 5,
                                    key: "2020/07/03",
                                },
                                {
                                    val: 3,
                                    key: "2020/07/04",
                                },
                                {
                                    val: 7,
                                    key: "2020/07/05",
                                },
                                {
                                    val: 2,
                                    key: "2020/07/06",
                                },
                            ],
                            key: "甲烷",
                        },
                        {
                            val: [
                                {
                                    val: 1,
                                    key: "2020/06/30",
                                },
                                {
                                    val: 2,
                                    key: "2020/07/01",
                                },
                                {
                                    val: 1,
                                    key: "2020/07/02",
                                },
                                {
                                    val: 4,
                                    key: "2020/07/03",
                                },
                                {
                                    val: 3,
                                    key: "2020/07/04",
                                },
                                {
                                    val: 2,
                                    key: "2020/07/05",
                                },
                                {
                                    val: 0,
                                    key: "2020/07/06",
                                },
                            ],
                            key: "消防手报",
                        },
                        {
                            val: [
                                {
                                    val: 2,
                                    key: "2020/06/30",
                                },
                                {
                                    val: 3,
                                    key: "2020/07/01",
                                },
                                {
                                    val: 1,
                                    key: "2020/07/02",
                                },
                                {
                                    val: 1,
                                    key: "2020/07/03",
                                },
                                {
                                    val: 3,
                                    key: "2020/07/04",
                                },
                                {
                                    val: 5,
                                    key: "2020/07/05",
                                },
                                {
                                    val: 1,
                                    key: "2020/07/06",
                                },
                            ],
                            key: "视频服务",
                        },
                    ];
                }
                this.initCharts();
            });
    }

    initCharts() {
        for (let i = 0; i < this.showList.length; i++) {
            this.drawCharts(i, this.showList[i]);
        }
    }

    drawCharts(index: number, name: string) {
        // id
        const chartId = "trend_" + index;
        let dataIndex = this.trendData.findIndex((d) => d.key == name);
        if (dataIndex < 0) return;
        // chart中的数据部分
        let data = this.trendData[dataIndex].val;

        let xAxisData = data.map((d) => {
            return d.key;
        });
        let seriesData = data.map((d) => {
            return d.val;
        });

        let myChart: ECharts = this.$echarts.init(
            document.getElementById(chartId)
        );
        let option: EChartOption = {
            title: {
                text: name,
                top: "middle",
                left: 10,
                textStyle: {
                    color: "#fff",
                    fontWeight: "400",
                    fontSize: 12,
                },
            },
            tooltip: {
                trigger: "axis",
            },
            grid: {
                top: "5%",
                bottom: "5%",
                left: "25%",
                right: "5%",
            },
            xAxis: {
                type: "category",
                boundaryGap: false,
                axisLine: {
                    show: false,
                },
                axisLabel: {
                    show: false,
                },
                splitLine: {
                    show: false,
                },
                axisTick: {
                    show: false,
                },
                data: xAxisData,
            },
            yAxis: {
                type: "value",
                axisLine: {
                    show: false,
                },
                axisLabel: {
                    show: false,
                },
                splitLine: {
                    show: false,
                },
                axisTick: {
                    show: false,
                },
            },
            series: [
                {
                    smooth: true,
                    data: seriesData,
                    type: "line",
                    symbol: "none",
                    lineStyle: {
                        // color: "#168AF6",
                        color: "#2A65BE",
                    },
                },
            ],
        };
        myChart.setOption(option);
        // myChart.getZr().on('click', this.clickEvent)
        myChart.on("click", this.clickEvent);
        window.addEventListener("resize", () => this.myChart.resize(), false);
    }

    clickEvent(params: any) {
        console.log("params", params);
    }
}
</script>

<style lang="less" scope>
.growth_trend {
    height: 100%;

    h4 {
        line-height: 60px;
        font-size: 22px;
        color: #f9ffff;
        font-weight: 500;
        padding-left: 61px;

        .icon {
            display: block;
            width: 22px;
            height: 24px;
            position: absolute;
            left: 17px;
            top: 18px;
            background: url("../../../assets/images/growth_trend.png") no-repeat;
            background-size: 100% 100%;
        }
    }
}
</style>
