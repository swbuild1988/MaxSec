<template>
    <Row style="height: 100%">
        <Col span="12" style="height: 900px" class="overflow:auto">
        <h4>地图管理</h4>

        <div class='btnClass'>
            <Button type='info' icon='md-add' @click="addModel">新建</Button>
            <Button type='warning' icon='md-arrow-down'>导出</Button>
        </div>

        <Form :model="addItem" label-position="right" :label-width="200" v-show="flag_add">
            <FormItem label="地图名：">
                <Input v-model="addItem.name" />
            </FormItem>
            <FormItem label="地图级别：">
                <RadioGroup v-model="curLevel" @on-change="levelChange">
                    <Radio v-for="item in mapLevel" :label="item.val" :key="item.val">
                        {{item.key}}
                    </Radio>
                </RadioGroup>
            </FormItem>
            <FormItem label="具体选择：">
                <Row>
                    <Col span="7" offset="1">
                    <el-select @change="countryChange" v-model="provinceSelect">
                        <el-option v-for="item in provinceGroup" :value="item" :key="item" :label="item"></el-option>
                    </el-select>
                    </Col>
                    <Col span="7" offset="1">
                    <el-select @change="provinceChange" v-model="citySelect">
                        <el-option v-for="item in cityGroup" :value="item" :key="item" :label="item"></el-option>
                    </el-select>
                    </Col>
                    <Col span="7" offset="1">
                    <el-select v-if="curLevel >1" @change="cityChange" v-model="districtSelect">
                        <el-option v-for="item in districtGroup" :value="item" :key="item" :label="item"></el-option>
                    </el-select>
                    </Col>
                </Row>
            </FormItem>
            <FormItem label="选择的父节点：">
                <Tag v-for="item in addItem.geomaps" :key="item.name" closable @on-close="deleteFatherGeo(item)">
                    {{item.name}}
                </Tag>
            </FormItem>
            <FormItem label="选择的子节点：">
                <div v-for="item1 in addItem.geomaps" :key="item1.name">
                    <Tag v-for="item2 in item1.children" :key="item2" closable @on-close="deleteChildGeo(item2)">
                        {{item2}}
                    </Tag>
                </div>
            </FormItem>
            <FormItem>
                <Button type="primary" @click="test">显示测试</Button>
                <Button type="success" @click="add">确认添加</Button>
                <Button @click="cancel">取消</Button>
            </FormItem>
        </Form>

        <Table border :columns="columns" :data="customs" height="400">
            <template slot-scope="{ row, index }" slot="action">
                <Button type="error" size="small" icon='md-trash' @click="remove(index)">删除</Button>
            </template>
        </Table>
        <el-pagination style="text-align: right" :total="page.total" :page-size="page.pageSize"
            :current-page="page.current" :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper" @current-change="currentChange"
            @size-change="pageSizeChange"></el-pagination>

        </Col>

        <Col span="12" style="height: 900px">
        <map-chart :mapName="mapName" ref="mapChart"></map-chart>
        </Col>
    </Row>
</template>

<script lang="ts">
    import {
        Component,
        Vue
    } from 'vue-property-decorator'
    import {
        getGeoMap,
        getCustomMap,
        addCustomMap,
        editCustomMap,
        deleteCustomMap,
        getPageInfo
    } from '@/api/custommap'
    import {
        hasMap,
        registerMap,
        updateMap
    } from '@/util/echartmap'
    import {
        CustomMap,
        BelongGeoMap
    } from '@/types/custommap.interface'
    import MapChart from '@/components/chart/mapChart_2D.vue'
    import {
        EnumType
    } from '@/types/enumtype.interface'
    import {
        createGeoMap
    } from '@/util/geomap'
    import {
        PageQuery
    } from '@/types/pagequery.interface'

    @Component({
        components: {
            MapChart
        }
    })
    export default class GeoMapClass extends Vue {
        customs: CustomMap[] = []

        columns: any[] = [{
                title: 'ID',
                key: 'id',
                width: 50,
            },
            {
                title: '地图名',
                key: 'name',
                width: 80,
            },
            {
                title: '详细信息',
                key: 'geomaps_str',
                width: 450,
            },
            {
                title: '创建时间',
                key: 'createTime',
                render: (h: any, params: any) => {
                    return h(
                        "div",
                        new Date(params.row.createTime).format("yyyy-MM-dd hh:mm:ss")
                    );
                },
                width: 150,
            },
            {
                title: '操作',
                slot: 'action',
                align: 'center'
            }
        ]

        /** 国家级别 */
        countryGeoMap: any = {
            features: []
        }
        /** 省级别 */
        provinceGeoMap: any = {
            features: []
        }
        /** 市级 */
        cityGeoMap: any = {
            features: []
        }

        provinceGroup: string[] = []
        cityGroup: string[] = []
        districtGroup: string[] = []

        provinceSelect: string = ''
        citySelect: string = ''
        districtSelect: string = ''


        /** 当前地图 */
        mapName: string = "china"

        /** 地图级别枚举 */
        mapLevel: any[] = [{
            key: "市级",
            val: 1,
            level: "city"
        }, {
            key: "区级",
            val: 2,
            level: "district"
        }]

        /** 当前级别 */
        curLevel: number = 1

        /** 添加是否显示 */
        flag_add: boolean = false

        /** 带添加对象 */
        addItem: CustomMap = this.getDefaultItem()

        /** 分页 */
        page: any = {
            total: 0,
            pageSize: 10,
            current: 1,
        }


        mounted() {
            this.initData()
        }

        initData() {
            this.countryGeoMap = {
                features: []
            }
            this.provinceGeoMap = {
                features: []
            }
            this.cityGeoMap = {
                features: []
            }

            // 下拉框
            this.provinceGroup = []
            this.cityGroup = []
            this.districtGroup = []
            // 下拉框所选择的
            this.provinceSelect = ''
            this.citySelect = ''
            this.districtSelect = ''

            this.addItem.geomaps.length = 0

            getGeoMap("country", "china").then((res: any) => {
                let {
                    code,
                    data
                } = res.data

                if (code == 200) {
                    this.countryGeoMap = data
                    this.countryGeoMap.features.forEach((element: any) => {
                        this.provinceGroup.push(element.properties.name)
                    })

                    this.changeEchartMap("china", data)
                } else {
                    this.$Message.info(data)
                }
            })

            this.initCustomMap()
        }

        initCustomMap() {

            let pageParam: PageQuery = {
                pageNum: this.page.current,
                pageSize: this.page.pageSize
            }

            getPageInfo(pageParam).then((res: any) => {
                let {
                    code,
                    data
                } = res.data

                if (code == 200) {
                    this.page.total = data.totalSize

                    this.customs = data.content
                } else {
                    this.$Message.info(data)
                }
            })
        }


        currentChange(value: number) {
            this.page.current = value;
            this.initCustomMap();
        }

        pageSizeChange(value: number) {
            this.page.pageSize = value;
            this.initCustomMap();
        }


        changeEchartMap(mapName: string, data: any) {
            this.mapName = mapName
            updateMap(this.mapName, data);
            (this.$refs.mapChart as any).drawChart()
        }

        getDefaultItem(): CustomMap {
            return {
                id: 0,
                name: "",
                geomaps: [],
                geomaps_str: ''
            }
        }

        levelChange() {
            this.initData()
        }

        countryChange(val: any) {

            if (!val) return

            getGeoMap("province", val).then((res: any) => {
                let {
                    code,
                    data
                } = res.data

                if (code == 200) {
                    this.provinceGeoMap = data
                    this.cityGroup = this.curLevel == 1 ? ["全部"] : []
                    this.citySelect = ''
                    this.provinceGeoMap.features.forEach((element: any) => {
                        this.cityGroup.push(element.properties.name)
                    })

                    this.changeEchartMap(val, data)
                } else {
                    this.provinceGeoMap = {
                        features: []
                    }
                }
            })
        }

        provinceChange(val: any) {

            if (!val) return

            if (this.getCurLevel() == "city") { // 当前只能选择

                this.updateAddItem()

            } else {

                getGeoMap("city", val).then((res: any) => {
                    let {
                        code,
                        data
                    } = res.data

                    if (code == 200) {
                        this.cityGeoMap = data
                        this.districtGroup = ["全部"]
                        this.districtSelect = ''
                        this.cityGeoMap.features.forEach((element: any) => {
                            this.districtGroup.push(element.properties.name)
                        })

                        this.changeEchartMap(val, data)
                    } else {
                        this.cityGeoMap = {
                            features: []
                        }
                    }
                })
            }
        }

        cityChange(val: any) {

            if (!val) return
            this.updateAddItem()
        }

        getCurLevel(): string {
            let curMap: any | undefined = this.mapLevel.find((item: EnumType) => item.val == this.curLevel)
            if (!curMap) {
                return ""
            } else {
                return curMap.level
            }
        }

        updateAddItem() {
            let level = this.getCurLevel()

            let father: string = level == 'city' ? this.provinceSelect : this.citySelect
            let fatherLevel: string = level == 'city' ? 'province' : 'city'
            let child1: string = level == 'city' ? this.citySelect : this.districtSelect
            let children: string[] = []

            if (child1 == "全部") {
                children = level == 'city' ? this.cityGroup.slice() : this.districtGroup.slice()
                children.splice(0, 1)
            } else {
                children = [child1]
            }

            children.forEach((child: string) => {
                let f: boolean = true
                this.addItem.geomaps.forEach((item: BelongGeoMap) => {
                    if (item.name == father) {
                        if (item.children.indexOf(child) < 0) {
                            item.children.push(child)
                        }
                        f = false
                    }
                })
                // 全新的
                if (f) {
                    this.addItem.geomaps.push({
                        name: father,
                        level: fatherLevel,
                        children: [child]
                    })
                }
            })

        }

        deleteFatherGeo(val: any) {
            this.addItem.geomaps = this.addItem.geomaps.filter(item => item.name != val.name)
        }

        deleteChildGeo(val: any) {
            this.addItem.geomaps.forEach((item: BelongGeoMap) => {
                item.children = item.children.filter(child => child != val)
            })
        }

        /** 测试显示选择的地图 */
        async test() {
            let tmp = await createGeoMap(this.addItem)
            this.changeEchartMap("test", tmp)
        }

        /** 确认添加 */
        add() {
            addCustomMap(this.addItem).then((res: any) => {
                let {
                    code,
                    data
                } = res.data
                if (code == 200) {
                    this.flag_add = false
                    this.addItem = this.getDefaultItem()
                    this.initData()
                } else {
                    this.$Message.info("添加出错！！！")
                }
            })
        }

        /** 取消添加 */
        cancel() {
            this.flag_add = false
            this.addItem = this.getDefaultItem()
        }

        /** 添加新对象 */
        addModel() {
            this.flag_add = true
        }


        remove(index: number) {
            deleteCustomMap(this.customs[index].id).then((res: any) => {
                let {
                    code,
                    data
                } = res.data
                if (code == 200) {
                    this.initData()
                    this.$Message.success("删除成功！！！")
                } else {
                    this.$Message.info("删除出错！！！")
                }
            })
        }

    }
</script>

<style scoped>
</style>