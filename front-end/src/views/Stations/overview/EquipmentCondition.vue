<template>
    <div class="equipment_condition">
        <h4>设备状况</h4>
        <div class="condition_box">
            <div class="statistics_data">
                <p>
                    设备总数：<span class="num">{{ totalCount }}</span> 套
                </p>

                <p
                    v-for="item in equipmentStatistic"
                    :key="item.key"
                    class="data_item"
                >
                    <span
                        class="data_circel data_online_circel"
                        v-if="item.key == '在线'"
                    ></span>
                    <span
                        class="data_circel data_notonline_circel"
                        v-else-if="item.key == '离线'"
                    ></span>
                    <span class="data_circel data_bad_circel" v-else></span>
                    {{ item.key }}: <span class="num">{{ item.val }}</span> 套
                </p>
            </div>
            <div class="equipment_condition_chart" id="conditionId"></div>
        </div>
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
    getPageEquipmentsByCondition,
    getEquiupmentStateStatisticeByStation,
} from "@/api/equipment";
import { EnumType } from "@/types/enumtype.interface";
import { EquipmentVo } from "@/types/equipment.interface";

@Component({})
export default class EquipmentCondition extends Vue {
    myChart!: ECharts;
    stationId!: number;
    // 设备统计
    equipmentStatistic: any[] = [];
    // 设备总数
    totalCount: number = 0;

    // 设备状态类型
    equipmentStates: EnumType[] = this.$store.state.equipmentStateType;

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
        this.stationId = this.$store.state.stationId;
        this.initData();
    }

    initData() {
        getEquiupmentStateStatisticeByStation(this.stationId)
            .then((res: any) => {
                let { code, data } = res.data;
                if (code == 200) {
                    this.equipmentStatistic = data;
                }
            })
            .finally(() => {
                if (this.TestData) {
                    this.equipmentStatistic = [
                        {
                            val: 38,
                            key: "在线",
                        },
                        {
                            val: 3,
                            key: "离线",
                        },
                        {
                            val: 1,
                            key: "故障",
                        },
                    ];
                }
                for (let i = 0; i < this.equipmentStatistic.length; i++) {
                    this.totalCount += this.equipmentStatistic[i].val;
                }
                this.initChart();
            });
    }

    initChart() {
        this.myChart = this.$echarts.init(
            document.getElementById("conditionId")
        );

        let colors: string[] = ["#2ed1f6", "#D22E26", "#FEAB5E"];
        let pieData: any[] = [];
        for (let i = 0; i < this.equipmentStatistic.length; i++) {
            const element = this.equipmentStatistic[i];
            pieData.push({
                value: element.val,
                name: element.key,
                itemStyle: {
                    color: colors[i % colors.length],
                },
            });
        }

        let initOption: EChartOption = {
            tooltip: {
                trigger: "item",
                formatter: "{a} <br/>{b}: {c} ({d}%)",
            },
            series: [
                {
                    name: "设备状况",
                    type: "pie",
                    radius: ["50%", "70%"],
                    avoidLabelOverlap: false,
                    label: {
                        normal: {
                            show: false,
                            position: "center",
                        },
                        emphasis: {
                            show: true,
                            fontSize: "18",
                        },
                    },

                    labelLine: {
                        show: false,
                    },
                    data: pieData,
                },
            ],
        };
        this.myChart.setOption(initOption);
        this.myChart.on("click", this.clickEvent);
        window.addEventListener("resize", () => this.myChart.resize(), false);
    }

    clickEvent(params: any) {
        let stateIndex: number = this.equipmentStates.findIndex(
            (state: EnumType) => state.key == params.name
        );
        if (stateIndex < 0) return;

        this.modelData.stateId = this.equipmentStates[stateIndex].val;

        this.modelData.show = true;
        this.modelData.title = params.seriesName + params.name + "设备详情";
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
        const data: EquipmentVo = {
            pageNum: this.modelData.page.current,
            pageSize: this.modelData.page.pageSize,
            equipmentState: this.modelData.stateId,
            stationId: this.stationId,
        };
        getPageEquipmentsByCondition(data).then((res: any) => {
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
.equipment_condition {
    height: 100%;

    h4 {
        line-height: 45px;
        font-size: 22px;
        color: #2ed1f6;
        font-weight: 500;
        padding-left: 21px;
        font-weight: 700;
    }

    .condition_box {
        height: 180px;
        display: flex;

        .statistics_data {
            height: 100%;
            flex: 3;
            padding: 15px;

            p {
                line-height: 32px;
                font-size: 12px;
                color: #fff;
                text-align: right;

                .num {
                    color: #2ed1f6;
                    font-size: 18px;
                    font-weight: 700;
                }
            }
            .data_item {
                position: relative;
                .data_circel {
                    display: block;
                    width: 10px;
                    height: 10px;

                    border-radius: 50%;
                    position: absolute;
                    left: 40%;
                    top: 50%;
                    transform: translateY(-50%);
                }
                .data_online_circel {
                    border: 3px solid #36e5ff;
                }
                .data_notonline_circel {
                    border: 3px solid #d22e26;
                }
                .data_bad_circel {
                    border: 3px solid #feab5e;
                }
            }
        }

        .equipment_condition_chart {
            height: 100%;
            flex: 5;
        }
    }
}
</style>
