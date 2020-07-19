<template>
    <div class="equipment_condition">
        <h4>
            <span class="icon"></span>
            设备状况
        </h4>
        <div class="alarm_statistics_chart" id="equipment_condition_chartId">
            <div class="lt" id="lt_id"></div>
            <div class="rt" id="rt_id"></div>
            <div class="cc" id="cc_id"></div>
            <div class="lb" id="lb_id"></div>
            <div class="rb" id="rb_id"></div>
        </div>
        <Modal v-model="modelData.show" width="800">
            <p slot="header" style="color:#f60;text-align:center">
                <Icon type="ios-information-circle"></Icon>
                <span style="font-size: medium">{{modelData.title}}</span>
            </p>
            <div style="text-align:right">
                <Table border :columns="modelData.columns" :data="modelData.equipments" height="520"></Table>
                <Page :total="modelData.page.total" :page-size="modelData.page.pageSize"
                    :current="modelData.page.current" show-total show-sizer @on-change="currentChange"
                    @on-page-size-change="pageSizeChange" />
            </div>
            <div slot="footer" style="text-align:center">
                <Button type="primary" @click="modelData.show = false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue
    } from "vue-property-decorator";
    import {
        ECharts,
        EChartOption,
        EChartsSeriesType
    } from "echarts";
    import {
        getEquipmentTypeStateStatistice,
        getPageEquipmentsByCondition
    } from '@/api/equipment'
    import {
        EnumType
    } from '@/types/enumtype.interface'
    import {
        Tooltip
    } from 'iview';
    import {
        EquipmentVo
    } from '@/types/equipment.interface';

    @Component({})
    export default class EquipmentCondition extends Vue {

        statisticeData: any[]

        charts: any[] = [{
            id: "lt_id",
            typeId: 3
        }, {
            id: "rt_id",
            typeId: 2
        }, {
            id: "cc_id",
            typeId: 1
        }, {
            id: "lb_id",
            typeId: 4
        }, {
            id: "rb_id",
            typeId: 5
        }]

        // 对话框内的参数
        modelData: any = {
            show: false,
            title: "",
            columns: [{
                    title: '设备名',
                    key: 'name'
                },
                {
                    title: '序列号',
                    key: 'sn'
                },
                {
                    title: '所属场站',
                    key: 'stationName'
                },
                {
                    title: '设备类型',
                    key: 'equipmentTypeName'
                },
                {
                    title: '设备状态',
                    key: 'equipmentStateName'
                }
            ],
            equipments: [],
            page: {
                total: 0,
                pageSize: 10,
                current: 1
            },
            typeId: 0,
            stateId: 0,
        }

        equipmentTypes: EnumType[] = this.$store.state.equipmentType
        equipmentStates: EnumType[] = this.$store.state.equipmentStateType

        colors: string[] = ["#91FDFF", "#025689", "#0C4277"]

        mounted() {
            this.init()
        }

        init() {
            getEquipmentTypeStateStatistice().then((res: any) => {
                let {
                    code,
                    data
                } = res
                if (code == 200) {
                    this.statisticeData = data
                }
            }).finally(() => {

                if (this.TestData) {
                    this.statisticeData = [{
                            "val": [{
                                    "val": 50,
                                    "key": "在线"
                                },
                                {
                                    "val": 1,
                                    "key": "离线"
                                },
                                {
                                    "val": 1,
                                    "key": "故障"
                                }
                            ],
                            "key": "视频"
                        },
                        {
                            "val": [{
                                    "val": 24,
                                    "key": "在线"
                                },
                                {
                                    "val": 1,
                                    "key": "离线"
                                },
                                {
                                    "val": 2,
                                    "key": "故障"
                                }
                            ],
                            "key": "周界安防"
                        },
                        {
                            "val": [{
                                    "val": 40,
                                    "key": "在线"
                                },
                                {
                                    "val": 0,
                                    "key": "离线"
                                },
                                {
                                    "val": 3,
                                    "key": "故障"
                                }
                            ],
                            "key": "消防"
                        },
                        {
                            "val": [{
                                    "val": 27,
                                    "key": "在线"
                                },
                                {
                                    "val": 1,
                                    "key": "离线"
                                },
                                {
                                    "val": 2,
                                    "key": "故障"
                                }
                            ],
                            "key": "光纤"
                        },
                        {
                            "val": [{
                                    "val": 50,
                                    "key": "在线"
                                },
                                {
                                    "val": 1,
                                    "key": "离线"
                                },
                                {
                                    "val": 3,
                                    "key": "故障"
                                }
                            ],
                            "key": "甲烷"
                        }
                    ]
                }

                for (let index = 0; index < this.charts.length; index++) {
                    this.initChart(this.charts[index].id, this.charts[index].typeId)
                }
            })
        }

        initChart(id: string, typeId: number) {
            // 先找出这个图应该有的类型
            let typeIndex: number = this.equipmentTypes.findIndex((type: EnumType) => type.val == typeId)
            if (typeIndex < 0) return

            const type: EnumType = this.equipmentTypes[typeIndex]

            // 找出这个图的数据
            let dataIndex: number = this.statisticeData.findIndex((d: any) => d.key == type.key)
            if (dataIndex < 0) return

            const data = this.statisticeData[dataIndex].val
            let total: number = 0

            // 数据格式
            let seriesData: any[] = []
            for (let i = 0; i < data.length; i++) {
                const element = data[i];
                seriesData.push({
                    value: Number(data[i].val),
                    name: data[i].key,
                    itemStyle: {
                        color: this.colors[i % this.colors.length],
                        shadowColor: this.colors[i % this.colors.length],
                        shadowBlur: 5,
                    }
                })
                total += Number(data[i].val)
            }

            let myChart: ECharts = this.$echarts.init(document.getElementById(id))
            let initOption: EChartOption = {
                title: {
                    show: true,
                    text: type.key,
                    textStyle: {
                        color: "#91FDFF",
                        fontSize: this.getSizeByWidth("15%", id),
                        fontWeight: "bold"
                    },
                    subtext: (Number(data[0].val) / total * 100).toFixed(0) + "%",
                    subtextStyle: {
                        color: "#91FDFF",
                        fontSize: this.getSizeByWidth("18%", id),
                        fontWeight: "bold"
                    },
                    left: "center",
                    top: "25%"
                },
                tooltip: {
                    trigger: "item",
                    formatter: "{a} <br/>{b}: {c} ({d}%)",
                },
                series: [{
                    name: type.key,
                    type: "pie",
                    radius: ["75%", "95%"],
                    avoidLabelOverlap: false,
                    top: "top",
                    left: "left",
                    hoverAnimation: false,
                    label: {
                        show: false
                    },
                    labelLine: {
                        show: false,
                    },
                    data: seriesData,
                }],
            }
            myChart.setOption(initOption)
            myChart.on('click', this.clickEvent)
            window.addEventListener("resize", () => myChart.resize(), false);
        }

        /**
         * 
         */
        clickEvent(params: any) {

            let typeIndex: number = this.equipmentTypes.findIndex((type: EnumType) => type.key == params.seriesName)
            if (typeIndex < 0) return
            let stateIndex: number = this.equipmentStates.findIndex((state: EnumType) => state.key == params.name)
            if (stateIndex < 0) return

            this.modelData.typeId = this.equipmentTypes[typeIndex].val
            this.modelData.stateId = this.equipmentStates[stateIndex].val

            this.modelData.show = true
            this.modelData.title = params.seriesName + params.name + "设备详情"
            this.modelData.equipments = []
            this.modelData.page = {
                total: 0,
                pageSize: 10,
                current: 1
            }

            this.searchData()
        }

        currentChange(value: number) {
            this.modelData.page.current = value
            this.searchData()
        }

        pageSizeChange(value: number) {
            this.modelData.page.pageSize = value
            this.searchData()
        }

        searchData() {
            const data: EquipmentVo = {
                pageNum: this.modelData.page.current,
                pageSize: this.modelData.page.pageSize,
                equipmentType: this.modelData.typeId,
                equipmentState: this.modelData.stateId,
            }
            getPageEquipmentsByCondition(data).then((res: any) => {
                let {
                    code,
                    data
                } = res.data
                if (code == 200 && data) {
                    this.modelData.equipments = data.content
                    this.modelData.page.total = data.totalSize
                }
            })
        }

        /**
         * 根据element的宽度获取百分比
         */
        getSizeByWidth(val: number | string, elementId: string): number {
            if (typeof val === "number") return val;
            if (typeof val === "string") {
                if (val.indexOf("%") > 0) {
                    let tmp = parseFloat(val.replace("%", "")) / 100;
                    let height = document.getElementById(elementId).offsetWidth;
                    return Math.round(height * tmp);
                }
            }
            return 0;
        }

    }
</script>

<style lang="less" scoped>
    .equipment_condition {
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
                background: url("../../../assets/images/condition.png") no-repeat;
                background-size: 100% 100%;
            }
        }

        .alarm_statistics_chart {
            height: 230px;
            position: relative;

            .lt {
                width: 82px;
                height: 82px;
                position: absolute;
                top: 10px;
                left: 10px;
            }

            .rt {
                width: 82px;
                height: 82px;
                position: absolute;
                top: 10px;
                right: 10px;
            }

            .lb {
                width: 82px;
                height: 82px;
                position: absolute;
                bottom: 10px;
                left: 10px;
            }

            .rb {
                width: 82px;
                height: 82px;
                position: absolute;
                bottom: 10px;
                right: 10px;
            }

            .cc {
                width: 120px;
                height: 120px;
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
            }
        }
    }
</style>