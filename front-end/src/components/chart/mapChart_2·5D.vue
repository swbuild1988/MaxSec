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
    import {
        TableColumnRenderHeadParams
    } from 'iview'

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

        @Prop({
            required: false,
            default: () => []
        }) nodes: any[]

        @Prop({
            required: false,
            default: () => []
        }) links: any[]

        option!: any

        myChart!: ECharts

        /** 图标待选项 */
        icons: any[] = this.$store.state.icons
        /** 连接线样式 */
        lineStyles: any[] = this.$store.state.lineStyles

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

        @Watch('nodes')
        onNodeChanged(val: any[], oldVal: any[]) {
            this.drawChart()
        }

        @Watch('links')
        onLinkChanged(val: any[], oldVal: any[]) {
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
            // 设置散点
            if (this.nodes.length > 0) {
                this.nodes.forEach(node => {
                    let index = this.icons.findIndex(i => i.id == node.icon)
                    if (index > -1) {
                        node.symbol = `image://${this.icons[index].img}`
                        node.symbolSize = 20
                    }
                })
            }

            // 设置连接线样式
            if (this.links.length > 0) {
                this.links.forEach(link => {
                    let index = this.lineStyles.findIndex(i => i.id == link.type)
                    if (index > -1) link.lineStyle = {
                        color: this.lineStyles[index].color
                    }
                    // 设置数据
                    link.coords = [this.nodes[link.target].value, this.nodes[link.source].value]
                })
            }

            this.option = {
                geo: {
                    show: true,
                    map: this.mapName,
                    roam: true,
                    zoom: 1.2,
                    label: {
                        normal: {
                            show: false,
                        },
                        emphasis: {
                            show: true,
                            textStyle: {
                                color: '#fff'
                            }
                        },
                    },
                    itemStyle: {
                        normal: {
                            borderColor: 'rgba(147, 235, 248, 1)',
                            borderWidth: 1,
                            areaColor: '#00559D',
                            shadowColor: 'rgba(128, 217, 248, 1)',
                            // shadowColor: 'rgba(255, 255, 255, 1)',
                            shadowOffsetX: -2,
                            shadowOffsetY: 5,
                            shadowBlur: 5
                        },
                        emphasis: {
                            areaColor: '#2AB8FF',
                            borderWidth: 0
                        }
                    }
                },
                series: [{
                        name: 'scatter',
                        type: "scatter",
                        coordinateSystem: 'geo',
                        label: {
                            show: true,
                            position: "right",
                            color: '#01E7EE',
                            fontWeight: 'bold',
                            backgroundColor: '#0C385D',
                            borderWidth: 8,
                            borderColor: '#0C385D',
                            borderRadius: 2,
                            formatter: '{b}'
                        },
                        data: this.nodes,
                        zlevel: 10
                    },
                    {
                        type: "lines",
                        coordinateSystem: 'geo',
                        data: this.links,
                        silent: true,
                        lineStyle: {
                            opacity: 0.2,
                            width: 5
                        },
                        progressiveThreshold: 500,
                        progressive: 200,
                    },
                    {
                        type: "lines",
                        coordinateSystem: 'geo',
                        data: this.links,
                        lineStyle: {
                            width: 0
                        },
                        effect: {
                            // period: 2,
                            constantSpeed: 30,
                            show: true,
                            trailLength: 0.3,
                            symbolSize: 6,
                            color: '#fff'
                        },
                        zlevel: 5
                    }
                ]
            }

            this.myChart.resize();
            this.myChart.setOption(this.option)
        }

        clickEvent(param) {
            let {
                componentSubType,
                componentType,
                data,
                seriesType,
                dataType,
            } = param
            // console.log("param", param)
            // 关系图的节点
            if (componentSubType == "scatter" && componentType == "series" && seriesType == "scatter") {
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