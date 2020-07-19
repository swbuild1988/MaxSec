<template>
    <div class="alarm_statistics">
        <h4 class="alarm_tit">
            <span class="icon"></span>
            告警统计
        </h4>
        <div class="alarm_statistics_chart" id="alarm_chartId"></div>
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
import { getAlarmStatistics, getPageAlarmsByCondition } from "@/api/alarm";
import { EnumType } from "@/types/enumtype.interface";
import { AlarmVo } from "@/types/alarm.interface";

@Component({})
export default class AlarmStatistics extends Vue {
    myChart!: ECharts;
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
                render: (h: any, params: any) => {
                    return h(
                        "div",
                        new Date(params.row.timeStamp).format(
                            "yyyy-MM-dd hh:mm:ss"
                        )
                    );
                },
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
        this.init();
    }

    init() {
        getAlarmStatistics()
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
                            val: 16,
                            key: "甲烷",
                        },
                        {
                            val: 21,
                            key: "消防手报",
                        },
                        {
                            val: 9,
                            key: "视频服务",
                        },
                    ];
                }

                this.initChart();
            });
    }

    initChart() {
        this.myChart = this.$echarts.init(
            document.getElementById("alarm_chartId")
        );

        const colors = [
            "#FFD900",
            "#24C768",
            "#FF7E00",
            "#58AE4E",
            "#A37812",
            "#85A3FD",
        ];

        let legends: string[] = this.statisticsData.map((data) => {
            return data.key;
        });

        let seriesData: any[] = [];

        for (let i = 0; i < this.statisticsData.length; i++) {
            const element = this.statisticsData[i];

            seriesData.push({
                value: element.val,
                name: element.key,
                itemStyle: {
                    normal: {
                        color: colors[i % colors.length],
                    },
                },
                labelLine: {
                    normal: {
                        show: true,
                        smooth: false,
                        length: 5,
                        length2: 2,
                    },
                    emphasis: {
                        show: true,
                    },
                },
            });
        }

        for (let i = 0; i < this.statisticsData.length; i++) {
            seriesData.push({
                value: 0,
                name: "",
                labelLine: {
                    show: false,
                },
            });
        }

        let initOption: any = {
            calculable: true,
            tooltip: {
                show: false,
                // trigger: "item",
                // formatter: "{a}<br/>{b}:{c}千万元",
            },
            legend: {
                icon: "rect",
                left: "left",
                top: "5%",
                borderRadius: 5,
                itemGap: 5,
                itemWidth: 15,
                itemHeight: 8,
                data: legends,
                textStyle: {
                    color: "#fff",
                },
            },
            series: [
                {
                    name: "",
                    type: "pie",
                    radius: [17, 120],
                    avoidLabelOverlap: false,
                    startAngle: 0,
                    center: ["50%", "25%"],
                    roseType: "area",
                    label: {
                        normal: {
                            show: true,
                            formatter: "{c}",
                        },
                        emphasis: {
                            show: true,
                        },
                    },
                    labelLine: {
                        normal: {
                            show: true,
                            smooth: false,
                            length: 5,
                            length2: 2,
                        },
                        emphasis: {
                            show: true,
                        },
                    },
                    data: seriesData,
                },
            ],
        };
        // @ts-ignore
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
.alarm_statistics {
    h4.alarm_tit {
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
            background: url("../../../assets/images/alarm_statistics.png")
                no-repeat;
            background-size: 100% 100%;
        }
    }
}
</style>
