<template>
    <div class="mapChart-wrap" :id="id" ref="element"></div>
</template>

<script lang="ts">
    import {
        Component,
        Vue,
        Prop,
        Watch
    } from "vue-property-decorator"
    import {
        EChartOption,
        ECharts
    } from 'echarts'
    import {
        hasMap,
        registerMap
    } from '@/util/echartmap'

    @Component({})
    export default class About extends Vue {

        @Prop({
            required: false,
            default: 'mapChartId'
        }) id!: string

        @Prop({
            required: true
        }) mapName!: string

        option!: EChartOption

        myChart!: ECharts

        @Watch('mapName', {
            deep: true
        })
        onDataChanged(val: string, oldVal: string) {
            this.drawChart()
        }

        mounted() {
            this.initData()
            this.drawChart()
            this.resizeChart()
        }

        initData() {
            this.myChart = this.$echarts.init(document.getElementById(this.id))
        }

        drawChart() {
            if (!hasMap(this.mapName)) return

            this.option = {
                animation: true,
                animationDuration: 1000,
                animationEasing: 'cubicInOut',
                animationDurationUpdate: 1000,
                animationEasingUpdate: 'cubicInOut',
                grid: {
                    right: '1%',
                    top: '15%',
                    bottom: '10%',
                    width: '20%'
                },
                // tooltip: {
                //     trigger: 'item', // hover触发器
                //     axisPointer: { // 坐标轴指示器，坐标轴触发有效
                //         type: 'shadow', // 默认为直线，可选为：'line' | 'shadow'
                //         shadowStyle: {
                //             color: 'rgba(150,150,150,0.1)' //hover颜色
                //         }
                //     },
                //     formatter: (p: any) => {
                //         console.log("formatter", p)
                //         return p.name
                //     }
                // },
                geo: {
                    show: true,
                    map: this.mapName,
                    roam: true,
                    zoom: 1,
                    label: {
                        emphasis: {
                            show: true,
                            textStyle: {
                                color: "rgb(249, 249, 249)"
                            }
                        },
                        normal: {
                            show: true, //显示省份标签
                            textStyle: {
                                color: "rgb(0, 0, 0)", //省份标签字体颜色
                            }
                        },
                    },
                    itemStyle: {
                        normal: {
                            borderColor: 'rgba(147, 235, 248, 1)',
                            borderWidth: 1,
                            areaColor: {
                                type: 'radial',
                                x: 0.5,
                                y: 0.5,
                                r: 0.8,
                                colorStops: [{
                                    offset: 0,
                                    color: 'rgba(147, 235, 248, 0)' // 0% 处的颜色
                                }, {
                                    offset: 1,
                                    color: 'rgba(147, 235, 248, .2)' // 100% 处的颜色
                                }],
                                globalCoord: false // 缺省为 false
                            },
                            shadowColor: 'rgba(128, 217, 248, 1)',
                            // shadowColor: 'rgba(255, 255, 255, 1)',
                            shadowOffsetX: -2,
                            shadowOffsetY: 2,
                            shadowBlur: 10
                        },
                        emphasis: {
                            areaColor: '#389BB7',
                            borderWidth: 0
                        }
                    }
                },
            }

            this.myChart.resize();
            this.myChart.setOption(this.option)
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
            return Math.min(this.getSizeByWidth(val), this.getSizeByHeight(val))
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
    .mapChart-wrap {
        width: 100%;
        height: 100%;
    }
</style>