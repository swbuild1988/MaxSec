<template>
    <div class="gaugeChart_normal-wrap" :id="id" ref="element"></div>
</template>

<script lang="ts">
    import {
        Component,
        Vue,
        Prop,
        Watch
    } from "vue-property-decorator"

    import {
        ChartData,
        Series
    } from "@/types/chart.Interface"
    import {
        EChartOption,
        ECharts
    } from 'echarts'

    @Component({})
    export default class About extends Vue {

        @Prop({
            required: true,
            default: '1'
        }) id!: string

        @Prop({
            required: true
        }) data!: ChartData

        @Prop({
            required: false,
            default: null
        }) option!: EChartOption

        myChart!: ECharts
        EChartsTheme: any = {
            titleColor: '#eeeeee',
            textColor: '#aaa',
            color: ["#dd6b66", "#759aa0", "#e69d87", "#8dc1a9", "#ea7e53","#eedd78","#73a373","#73b9bc","#7289ab","#91ca8c","#f49f42"]
        }

        @Watch('data', {
            deep: true
        })
        onDataChanged(val: ChartData, oldVal: ChartData) {
            this.drawChart()
        }

        mounted() {
            this.initData()
            this.drawChart()
            this.resizeChart()
        }

        initData() {
            this.myChart = this.$echarts.init(document.getElementById(this.id))
            // 初始化的样式
            let theme = this.EChartsTheme
            let unit = (this.data.series as Series).unit ? (this.data.series as Series).unit : ''
            let initOption: EChartOption = {
                title: {
                    show: false
                },
                tooltip: {
                    formatter: "{a} <br/>{b} : {c}" + unit
                },
                series: [{
                    type: 'gauge',
                    radius: "90%",
                    axisLine: {
                        // 坐标轴线
                        lineStyle: {
                            // 属性lineStyle控制线条样式
                            width: this.getFontSize('1%'),
                            shadowBlur: 0
                        }
                    },
                    axisTick: {
                        length: this.getFontSize('4%'),
                        // 坐标轴小标记
                        lineStyle: {
                            // 属性lineStyle控制线条样式
                            color: "auto",
                        }
                    },
                    axisLabel: {
                        fontSize: this.getFontSize('6%'),
                        formatter: (value: any, index: any) => {
                            let val = parseFloat(value.toFixed(1));
                            return val;
                        }
                    },
                    splitLine: {
                        // 分隔线
                        length: this.getFontSize('6%'),
                        lineStyle: {
                            // 属性lineStyle控制线条样式
                            color: "auto"
                        }
                    },
                    // 标题
                    title: {
                        fontSize: this.getFontSize('8%'),
                        color: theme.titleColor,
                        offsetCenter: [0, '-30%']
                    },
                    // 数值显示
                    detail: {
                        width: '80%',
                        offsetCenter: [0, '75%'],
                        fontSize: this.getFontSize('12%')
                    },
                    //指针
                    pointer: {
                        width: this.getFontSize('2%')
                    },
                    itemStyle: {
                        shadowBlur: this.getFontSize('2%')
                    }

                }],
                textStyle: {
                    color: theme.textColor
                },
                color: theme.color
            }
            this.myChart.setOption(initOption)

            // 将外部传来的样式导入
            if (this.option != null) {
                this.myChart.setOption(this.option)
            }

        }

        drawChart() {
            this.myChart.showLoading()
            // 整合数据
            let series: Series = this.data.series as Series
            if (!series || series.data.length == 0) return

            // 获得最终option
            let _option: any = {
                title: {
                    show: this.data.title.length > 0,
                    text: this.data.title
                },
                series: [{
                    type: 'gauge',
                    name: '数值',
                    detail: {
                        formatter: '{value}' + series.unit
                    },
                    data: [{
                        name: series.name,
                        value: series.data[0].value
                    }]
                }]
            }

            this.myChart.setOption(_option)
            if (this.option) this.myChart.setOption(this.option)
            this.myChart.hideLoading()
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
    .gaugeChart_normal-wrap {
        height: 100%;
        width: 100%;
    }
</style>