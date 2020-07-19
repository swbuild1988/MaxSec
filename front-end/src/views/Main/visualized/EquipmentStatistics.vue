<template>
    <div class="equipment_statistics">
        <h4>
            <span class="icon"></span>
            设备统计
        </h4>
        <div class="statistics_chart" id="equipment_statistics"></div>
        <Modal v-model="modelData.show" width="800">
            <p slot="header" style="color:#f60;text-align:center">
                <Icon type="ios-information-circle"></Icon>
                <span style="font-size: medium">{{ modelData.title }}</span>
            </p>
            <div style="text-align:right">
                <Table
                    border
                    :columns="modelData.columns"
                    :data="modelData.equipments"
                    height="520"
                ></Table>
                <Page
                    :total="modelData.page.total"
                    :page-size="modelData.page.pageSize"
                    :current="modelData.page.current"
                    show-total
                    show-sizer
                    @on-change="currentChange"
                    @on-page-size-change="pageSizeChange"
                />
            </div>
            <div slot="footer" style="text-align:right">
                <Button type="primary" @click="modelData.show = false"
                    >关闭</Button
                >
            </div>
        </Modal>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { ECharts, EChartOption, EChartsSeriesType } from "echarts";
import {
    getEquipmentStatistics,
    getPageEquipmentsByManagement,
} from "@/api/equipment";
import { EnumType } from "@/types/enumtype.interface";

@Component({})
export default class EquipmentStatistics extends Vue {
    myChart!: ECharts;

    // 统计信息
    StatisticsData: any[];
    // 设备类型
    equipmentType: EnumType[] = this.$store.state.equipmentType;

    // 对话框内的参数
    modelData: any = {
        show: false,
        name: "",
        title: "",
        columns: [
            {
                title: "设备名",
                key: "name",
            },
            {
                title: "序列号",
                key: "sn",
            },
            {
                title: "所属场站",
                key: "stationName",
            },
            {
                title: "设备类型",
                key: "equipmentTypeName",
            },
            {
                title: "设备状态",
                key: "equipmentStateName",
            },
        ],
        equipments: [],
        page: {
            total: 0,
            pageSize: 10,
            current: 1,
        },
    };

    mounted() {
        this.init();
    }

    init() {
        getEquipmentStatistics()
            .then((res: any) => {
                let { code, data } = res.data;
                if (code == 200) {
                    this.StatisticsData = data;
                }
            })
            .finally(() => {
                if (this.TestData) {
                    this.StatisticsData = [
                        {
                            光纤: 83,
                            product: "广州管理处",
                            消防: 68,
                            视频: 51,
                            甲烷: 70,
                            周界安防: 40,
                        },
                        {
                            光纤: 55,
                            product: "汕头管理处",
                            消防: 82,
                            视频: 74,
                            甲烷: 61,
                            周界安防: 45,
                        },
                        {
                            光纤: 50,
                            product: "阳江管理处",
                            消防: 82,
                            视频: 61,
                            甲烷: 35,
                            周界安防: 45,
                        },
                    ];
                }
                this.initChart();
            });
    }

    initChart() {
        this.myChart = this.$echarts.init(
            document.getElementById("equipment_statistics")
        );

        let dimensions: string[] = ["product"];
        this.equipmentType.forEach((type: EnumType) => {
            dimensions.push(type.key);
        });

        let source = this.StatisticsData;

        let linearColor: any[] = [
            ["#01A0FF", "#1167FF"],
            ["#70DCFF", "#138BD6"],
            ["#5843EF", "#2D1BDA"],
            ["#04A576", "#0CF5D7"],
            ["#5F07FF", "#B16BDD"],
        ];
        let series: any[] = [];
        for (let i = 0; i < this.equipmentType.length; i++) {
            series.push({
                type: "bar",
                itemStyle: {
                    color: {
                        type: "linear",
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [
                            {
                                offset: 0,
                                color: linearColor[i % linearColor.length][0], // 0% 处的颜色
                            },
                            {
                                offset: 1,
                                color: linearColor[i % linearColor.length][1], // 100% 处的颜色
                            },
                        ],
                        global: false, // 缺省为 false
                    },
                },
            });
        }

        let initOption: EChartOption = {
            legend: {
                textStyle: {
                    color: "#D5E5F9",
                    fontSize: 12,
                },
                itemWidth: 10,
                itemHeight: 10,
                borderRadius: 5,
            },
            tooltip: {
                show: true,
                trigger: "axis",
                axisPointer: {
                    type: "none",
                },
            },
            dataset: {
                dimensions: dimensions,
                source: source,
            },
            xAxis: {
                type: "category",
                axisLine: {
                    lineStyle: {
                        color: "#0E8BFF",
                    },
                },
                axisTick: {
                    show: false,
                },
                axisLabel: {
                    margin: 10,
                    color: "#fff",
                    interval: 0,
                },
            },
            yAxis: {
                show: false,
            },
            grid: {
                bottom: 40,
            },
            series: series,
        };

        this.myChart.setOption(initOption);
        this.myChart.on("click", this.clickEvent);
        window.addEventListener("resize", () => this.myChart.resize(), false);
    }

    clickEvent(params: any) {
        this.modelData.show = true;
        this.modelData.name = params.name;
        this.modelData.title = params.name + "设备详情";
        this.modelData.equipments = [];
        this.modelData.page = {
            total: 0,
            pageSize: 10,
            current: 1,
        };
        this.searchData();
    }

    currentChange(value: number) {
        this.modelData.page.current = value;
        this.searchData();
    }

    pageSizeChange(value: number) {
        this.modelData.page.pageSize = value;
        this.searchData();
    }

    searchData() {
        getPageEquipmentsByManagement(
            this.modelData.name,
            this.modelData.page.current,
            this.modelData.page.pageSize
        ).then((res: any) => {
            let { code, data } = res.data;
            if (code == 200 && data) {
                this.modelData.equipments = data.content;
                this.modelData.page.total = data.totalSize;
            }
        });
    }
}
</script>

<style lang="less">
.growth_trend_line {
    height: 80% !important;
}
.equipment_statistics {
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
            background: url("../../../assets/images/equipment_statistics.png")
                no-repeat;
            background-size: 100% 100%;
        }
    }

    .statistics_chart {
        height: 80%;
    }
}
</style>
