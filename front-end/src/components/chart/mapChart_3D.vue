<template>
    <div class="mapChart-wrap" :id="id" ref="element"></div>
</template>

<script lang="ts">
    import {
        Component,
        Vue,
        Prop,
        Watch,
        Emit
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

        @Prop({
            required: false,
            default: () => []
        }) scatter: any[]

        option!: any

        myChart!: ECharts

        @Watch('mapName', {
            deep: true
        })
        onDataChanged(val: string, oldVal: string) {
            this.drawChart()
        }

        @Watch('scatter')
        onScatterChanged(val: any[], oldVal: any[]) {
            this.drawChart()
        }

        mounted() {
            this.initData()
            this.drawChart()
            this.resizeChart()
        }

        initData() {
            this.myChart = this.$echarts.init(document.getElementById(this.id))
            this.myChart.on('click', this.clickEvent)
        }

        drawChart() {
            if (!hasMap(this.mapName)) return

            this.myChart.clear()

            this.option = {
                geo3D: {
                    map: this.mapName,
                    itemStyle: {
                        color: '#00559D', // 地图配色
                        opacity: 1,
                        borderWidth: 0.8,
                        borderColor: '#cccccc' // 地图边配色
                    },
                    label: {
                        show: false,
                    },
                    emphasis: { //当鼠标放上去  地区区域是否显示名称
                        itemStyle: {
                            color: '#7db41b', // 鼠标移入地图配色
                        },
                        label: {
                            show: true,
                            textStyle: {
                                color: '#fff',
                                fontSize: 3,
                                backgroundColor: 'rgba(0,0,0,0.5)' //鼠标移入文字加背景
                            }
                        }
                    },
                    // shading: 'lambert',
                    light: { //光照阴影
                        main: {
                            color: '#fff', //光照颜色
                            intensity: 1.2, //光照强度
                            //shadowQuality: 'high', //阴影亮度
                            shadow: true, //是否显示阴影
                            alpha: 60,
                            beta: 120

                        },
                        ambient: {
                            intensity: 0.3
                        }
                    },
                    viewControl: {
                        panMouseButton: 'left',
                        rotateMouseButton: 'middle',
                        distance: 100,
                        minDistance: 40,
                        maxDistance: 200,
                        animation: true,
                        animationDurationUpdate: 1000,
                        animationEasingUpdate: 'cubicInOut',
                        // 初始旋转角度
                        alpha: 50,
                        // beta: 40
                    },
                },
                series: {
                        name: 'scatter3D',
                        type: "scatter3D",
                        coordinateSystem: 'geo3D',
                        symbol: 'circle',
                        symbolSize: 20,
                        opacity: 1,
                        label: {
                            show: true,
                            position: 'bottom',
                            formatter: p => {
                                return p.data.name
                            }
                        },
                        itemStyle: {
                            borderWidth: 0.5,
                            borderColor: '#fff' //气泡边的颜色
                        },
                        data: []
                    }
            }

            if (this.scatter.length > 0) {
                for (let i = 0; i < this.scatter.length; i++) {
                    // 设置初始高度
                    this.scatter[i].value.push(2)
                }
                this.option.series.data = this.scatter
            }

            this.myChart.resize();
            this.myChart.setOption(this.option)
        }

        clickEvent(param) {
            let {
                componentSubType,
                componentType,
                data,
                seriesType
            } = param
            if (componentSubType == "scatter3D" && componentType == "series" && seriesType == "scatter3D") {
                this.click(data)
            }
        }

        @Emit('on-click')
        private click(data: any) {}

        resizeChart() {
            let _this = this;
            window.addEventListener("resize", () => {
                _this.myChart.resize();
                _this.drawChart();
            });
        }
    }
</script>

<style lang="less">
    .mapChart-wrap {
        width: 100%;
        height: 100%;
    }
</style>