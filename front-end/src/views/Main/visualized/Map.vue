<template>
    <map-chart :mapName="mapName" :nodes="nodes" :links="links" ref="mapChart" @on-click="mapClick"></map-chart>
</template>

<script lang="ts">
    import {
        Component,
        Vue
    } from "vue-property-decorator"
    import {
        getManagementTree
    } from '@/api/management'
    import {
        getStations
    } from '@/api/station'
    import {
        getStationLinks
    } from '@/api/stationlink'
    import {
        getCustomMapByName
    } from '@/api/custommap'
    import {
        ManagementTree
    } from '@/types/management.interface'
    import {
        CustomMap
    } from '@/types/custommap.interface'
    import {
        Station
    } from '@/types/station.interface'
    import {
        StationLink
    } from '@/types/stationlink.interface'
    import {
        hasMap,
        registerMap,
        updateMap
    } from '@/util/echartmap'
    import {
        createGeoMap
    } from '@/util/geomap'
    import MapChart from '@/components/chart/mapChart_2·5D.vue'

    @Component({
        components: {
            MapChart
        }
    })
    export default class Map extends Vue {
        /** 当前角色的部门树结构 */
        BaseTree: ManagementTree
        /** 当前所处的树结构 */
        CurTree: ManagementTree
        /** 当前角色下的所有场站 */
        stations: Station[]
        /** 场站关联 */
        stationLinks: StationLink[]

        /** 当前地图 */
        mapName: string = "china"
        /** 显示的点位 */
        scatter: any[] = []
        /** 关系图点位 */
        nodes: any[] = []
        /** 关联线 */
        links: any[] = []

        mounted() {
            this.initData()
        }

        initData() {
            Promise.all([getStations(), getManagementTree(), getStationLinks()]).then((res: any) => {
                // 场站
                if (res[0].data.code == 200) this.stations = res[0].data.data

                // 当前的树结构
                if (res[1].data.code == 200) this.BaseTree = this.CurTree = res[1].data.data

                // 场站关联
                if (res[2].data.code == 200) this.stationLinks = res[2].data.data

                this.drawMap()
            })
        }

        async drawMap() {
            this.mapName = this.CurTree.map

            // 如果已经有了该地图，则直接加载，否则需要新建地图
            if (!hasMap(this.mapName)) {
                // 先根据地图名称获取自定义地图
                let customMap: any = await this.getCustomMapByName()
                // 根据自定义的地图获取标准地图格式
                let geoMap: any = await createGeoMap(customMap)
                // 注册进echarts
                registerMap(this.mapName, geoMap)
            }

            // 显示关系图
            this.showGraph();

            // 画上地图
            (this.$refs.mapChart as any).drawChart()
        }

        showGraph() {
            this.stations.forEach((station: Station) => {
                // 地理坐标分割
                let splitStr: string[] = station.position.split(',')
                if (splitStr.length > 0) {
                    this.nodes.push({
                        id: station.id,
                        name: station.name,
                        value: splitStr.map(val => Number(val)),
                        icon: station.icon
                    })
                }
            })

            this.stationLinks.forEach((link: StationLink) => {
                let source: number = this.nodes.findIndex((node: any) => node.id == link.source)
                let target: number = this.nodes.findIndex((node: any) => node.id == link.target)
                if (source > -1 && target > -1) {
                    this.links.push({
                        source: source,
                        target: target,
                        type: link.type
                    })
                }
            })
        }

        // 根据地图名获取自定义地图的结构
        getCustomMapByName() {
            return new Promise((resolve, reject) => {
                getCustomMapByName(this.mapName).then((res: any) => {
                    let {
                        code,
                        data
                    } = res.data

                    if (code == 200) {
                        resolve(data)
                    } else {
                        reject()
                    }
                }).catch(err => {
                    reject(err)
                })
            })
        }

        mapClick(param: any) {
            // if (param.type == "management") { // 如果是管理部门
            //     for (let i = 0; i < this.CurTree.children.length; i++) {
            //         const element: ManagementTree = this.CurTree.children[i]
            //         if (element.id == param.id) {
            //             this.CurTree = element
            //             this.drawMap()
            //             break
            //         }
            //     }
            // } else { // 场站
            //     // 跳转到场站页面

            // }
            // 将 param.id即场站id 存到localStorage中，并跳转
            this.$store.commit('setStationId', param.id)
            this.$router.push({
                name: 'Stations'
            })
        }
    }
</script>

<style scoped></style>