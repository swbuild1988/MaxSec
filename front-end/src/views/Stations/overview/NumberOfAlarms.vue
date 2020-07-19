<template>
    <div class="number_of_alarms">
        <h4>告警数量</h4>
        <div class="number_pie" id="pieId"></div>
        <Modal v-model="modelData.show" width="800">
            <p slot="header" style="color:#f60;text-align:center">
                <Icon type="ios-information-circle"></Icon>
                <span style="font-size: medium">{{ modelData.title }}</span>
            </p>
            <div style="text-align:right">
                <Table
                    border
                    :columns="modelData.columns"
                    :data="modelData.alarms"
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
    getAlarmStatisticsByStation,
    getPageAlarmsByCondition,
} from "@/api/alarm";
import { EnumType } from "@/types/enumtype.interface";
import { AlarmVo } from "@/types/alarm.interface";

@Component({})
export default class NumberOfAlarms extends Vue {
    myChart!: ECharts;

    stationId: number = 0;

    // 数据
    statisticsData: any[] = [];
    // 对象类型
    objectTypes: EnumType[] = this.$store.state.objectType;

    // 对话框内的参数
    modelData: any = {
        show: false,
        title: "",
        columns: [
            {
                title: "对象号",
                key: "objId",
            },
            {
                title: "对象名称",
                key: "objectName",
            },
            {
                title: "所属场站",
                key: "stationName",
            },
            {
                title: "告警级别",
                key: "levelName",
            },
            {
                title: "告警类型",
                key: "alarmType",
                render: (h: any, params: any) => {
                    return h("div", params.row.alarmType.name);
                },
            },
            {
                title: "描述",
                key: "description",
            },
            {
                title: "时间",
                key: "time",
            },
        ],
        alarms: [],
        type: 0,
        page: {
            total: 0,
            pageSize: 10,
            current: 1,
        },
    };

    mounted() {
        this.stationId = this.$store.state.stationId;
        this.initData();
    }

    initData() {
        getAlarmStatisticsByStation(this.stationId)
            .then((res: any) => {
                let { code, data } = res.data;
                if (code == 200) {
                    this.statisticsData = data;
                }
            })
            .finally(() => {
                if (this.TestData) {
                    this.statisticsData = [
                        {
                            val: 20,
                            key: "视频",
                        },
                        {
                            val: 12,
                            key: "高后果视频",
                        },
                        {
                            val: 10,
                            key: "周界入侵",
                        },
                        {
                            val: 6,
                            key: "甲烷",
                        },
                        {
                            val: 1,
                            key: "消防手报",
                        },
                        {
                            val: 1,
                            key: "视频服务",
                        },
                    ];
                }

                this.initChart();
            });
    }

    initChart() {
        this.myChart = this.$echarts.init(document.getElementById("pieId"));

        const colors = [
            "#8733FE",
            "#0F89FD",
            "#3DD4D7",
            "#0066FF",
            "#F88813",
            "#85A3FD",
        ];

        let legends: string[] = this.statisticsData.map((data) => {
            return data.key;
        });

        let seriesData: any[] = [];

        this.statisticsData.sort(function(a, b) {
            return a.val - b.val;
        });
        for (let i = 0; i < this.statisticsData.length; i++) {
            const element = this.statisticsData[i];

            seriesData.push({
                value: element.val,
                name: element.key,
                itemStyle: {
                    color: colors[i % colors.length],
                    shadowBlur: 200,
                    shadowColor: "rgba(0, 0, 0, 0.5)",
                },
            });
        }

        let initOption: any = {
            tooltip: {
                trigger: "item",
                formatter: "{b} : {c} ({d}%)",
            },
            grid: {},
            series: [
                {
                    type: "pie",
                    radius: "55%",
                    center: ["50%", "50%"],
                    data: seriesData,
                    roseType: "radius",
                    label: {
                        color: "rgba(255, 255, 255)",
                        fontSize: 12,
                        position: "outside",
                        formatter: "{b}({d}%)",
                    },
                    labelLine: {
                        lineStyle: {
                            color: "rgba(255, 255, 255)",
                            type: "dotted",
                        },
                        length: 18,
                        length2: 3,
                    },
                    itemStyle: {
                        color: colors,
                        shadowBlur: 200,
                        shadowColor: "rgba(0, 0, 0, 0.5)",
                    },

                    animationType: "scale",
                    animationEasing: "elasticOut",
                    //   animationDelay: function(idx) {
                    //     return Math.random() * 200;
                    //   },
                },
            ],
        };
        this.myChart.setOption(initOption);
        this.myChart.on("click", this.clickEvent);
        window.addEventListener("resize", () => this.myChart.resize(), false);
    }

    clickEvent(params: any) {
        let objectTypeIndex = this.objectTypes.findIndex(
            (t) => t.key == params.name
        );
        if (objectTypeIndex < 0) return;

        this.modelData.type = this.objectTypes[objectTypeIndex].val;
        this.modelData.show = true;
        this.modelData.title = params.name + "类型告警详情";
        this.modelData.alarms = [];
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
        const data: AlarmVo = {
            pageNum: this.modelData.page.current,
            pageSize: this.modelData.page.pageSize,
            type: this.modelData.type,
        };
        getPageAlarmsByCondition(data).then((res: any) => {
            let { code, data } = res.data;
            if (code == 200 && data) {
                this.modelData.alarms = data.content;
                this.modelData.page.total = data.totalSize;
            }
        });
    }
}
</script>

<style lang="less">
.number_of_alarms {
    height: 100%;

    h4 {
        line-height: 45px;
        font-size: 22px;
        color: #2ed1f6;
        font-weight: 500;
        padding-left: 21px;
        font-weight: 700;
    }

    .number_pie {
        height: 180px;
        margin: 0 auto;
    }
}
</style>
