<template>
    <div class="alarm_statistics">
        <h4>
            报警统计
        </h4>
        <div class="alarm_statistics_pie" id="pieId"></div>
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
import { Component, Vue, Prop, Watch } from "vue-property-decorator";
import { ECharts, EChartOption, EChartsSeriesType } from "echarts";
import { MeasObject } from "@/types/measobject.interface";
import { AlarmVo } from "@/types/alarm.interface";
import { getCountByCondition, getPageAlarmsByCondition } from "@/api/alarm";

@Component({})
export default class AlarmStatistics extends Vue {
    myChart!: ECharts;

    @Prop({
        required: true,
        type: Array,
        default: [],
    })
    objects: MeasObject[];

    @Watch("objects")
    onObjectsChange(val: any[], oldVal: any[]) {
        this.initData();
    }

    chartData: any[] = [];

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
        objId: 0,
        page: {
            total: 0,
            pageSize: 10,
            current: 1,
        },
    };

    mounted() {
        this.initData();
    }

    initData() {
        if (this.objects.length == 0) return;

        let funcs: any[] = [];
        for (let i = 0; i < this.objects.length; i++) {
            let vo: AlarmVo = {
                objId: this.objects[i].id,
            };
            funcs.push(getCountByCondition(vo));
        }

        Promise.all(funcs)
            .then((res: any) => {
                for (let i = 0; i < res.length; i++) {
                    if (res[i].data.code == 200) {
                        this.chartData.push({
                            id: this.objects[i].id,
                            name: this.objects[i].name,
                            count: res[i].data.data,
                        });
                    }
                }
            })
            .finally(() => {
                if (this.TestData) {
                    // 造假
                    this.chartData.length = 0;
                    for (let i = 0; i < this.objects.length; i++) {
                        this.chartData.push({
                            id: this.objects[i].id,
                            name: this.objects[i].name,
                            count: Math.round(Math.random() * 10),
                        });
                    }
                }

                this.initChart();
            });
    }

    initChart() {
        this.myChart = this.$echarts.init(document.getElementById("pieId"));

        let colors: string[] = [
            "#118CFF",
            "#8433FD",
            "#FF9019",
            "#0063FF",
            "#39D9D6",
        ];

        let pieData: any[] = [];
        for (let i = 0; i < this.chartData.length; i++) {
            pieData.push({
                id: this.chartData[i].id,
                value: this.chartData[i].count,
                name: this.chartData[i].name,
                itemStyle: {
                    color: colors[i % colors.length],
                },
            });
        }
        let initOption: EChartOption = {
            grid: {},
            tooltip: {
                trigger: "item",
                formatter: "{a} <br/>{b} : {c} ({d}%)",
            },
            series: [
                {
                    name: "告警统计",
                    type: "pie",
                    radius: "55%",
                    center: ["50%", "40%"],
                    data: pieData,
                    roseType: "radius",
                    label: {
                        color: "rgba(255, 255, 255, 0.3)",
                    },
                    labelLine: {
                        lineStyle: {
                            color: "rgba(255, 255, 255, 0.3)",
                        },
                        smooth: 0.2,
                        length: 5,
                        length2: 10,
                    },
                    itemStyle: {
                        color: "#c23531",
                        shadowBlur: 200,
                        shadowColor: "rgba(0, 0, 0, 0.5)",
                    },
                    animationType: "scale",
                    animationEasing: "elasticOut",
                },
            ],
        };

        this.myChart.setOption(initOption);
        this.myChart.on("click", this.clickEvent);
        window.addEventListener("resize", () => this.myChart.resize(), false);
    }

    clickEvent(params: any) {
        this.modelData.objId = params.data.id;
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
            objId: this.modelData.objId,
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
    height: 100%;

    h4 {
        line-height: 40px;
        font-size: 22px;
        color: #2ed1f6;
        font-weight: 500;
        padding-left: 21px;
        font-weight: 700;
    }

    .alarm_statistics_pie {
        height: 100%;
    }
}
</style>
