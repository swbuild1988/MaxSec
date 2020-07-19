<template>
    <div>
        <h4>场站关联管理</h4>
        <div class='btnClass'>
            <Button type='info' icon='md-add' @click="addModel">新建</Button>
            <Button type='warning' icon='md-arrow-down'>导出</Button>
        </div>
        <Table border :columns="columns" :data="stationLinks" height="720">
            <template slot-scope="{ row, index }" slot="action">
                <Button type="success" size="small" style="margin-right: 5px" icon='md-create'
                    @click="edit(index)">编辑</Button>
                <Button type="error" size="small" icon='md-trash' @click="remove(index)">删除</Button>
            </template>
        </Table>
        <el-pagination style="text-align: right" :total="page.total" :page-size="page.pageSize"
            :current-page="page.current" :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper" @current-change="currentChange"
            @size-change="pageSizeChange"></el-pagination>

        <Modal v-model="flag_add" title="新增" @on-ok="addOk" @on-cancel="addCancel">
            <Form :model="addItem" :label-width="120">
                <FormItem label="起始场站:">
                    <el-select v-model="addItem.source">
                        <el-option v-for="station in stations" :value="station.id" :key="station.id"
                            :label="station.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="结束场站:">
                    <el-select v-model="addItem.target">
                        <el-option v-for="station in stations" :value="station.id" :key="station.id"
                            :label="station.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="连接类型:">
                    <InputNumber :max="100" :min="1" v-model="addItem.type"></InputNumber>
                </FormItem>
            </Form>
        </Modal>
        <Modal v-model="flag_edit" title="编辑" @on-ok="editOk" @on-cancel="editCancel">
            <Form :model="editItem" :label-width="120">
                <FormItem label="起始场站:">
                    <el-select v-model="editItem.source">
                        <el-option v-for="station in stations" :value="station.id" :key="station.id"
                            :label="station.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="结束场站:">
                    <el-select v-model="editItem.target">
                        <el-option v-for="station in stations" :value="station.id" :key="station.id"
                            :label="station.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="连接类型:">
                    <InputNumber :max="100" :min="1" v-model="editItem.type"></InputNumber>
                </FormItem>
            </Form>
        </Modal>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue
    } from 'vue-property-decorator'
    import {
        Station
    } from '@/types/station.interface'
    import {
        StationLink
    } from '@/types/stationlink.interface'
    import {
        PageQuery
    } from '@/types/pagequery.interface'
    import {
        getStations,
    } from '@/api/station'
    import {
        getStationLinks,
        addStationLink,
        editStationLink,
        deleteStationLink,
        getPageInfo
    } from '@/api/stationlink'

    @Component({})
    export default class StationClass extends Vue {
        stationLinks: StationLink[] = []
        stations: Station[] = []

        columns: any[] = [{
                title: 'ID',
                key: 'id'
            },
            {
                title: '起始场站',
                key: 'sourceName'
            },
            {
                title: '结束场站',
                key: 'targetName'
            },
            {
                title: '类型',
                key: 'type'
            },
            {
                title: '操作',
                slot: 'action',
                width: 350,
                align: 'center'
            }
        ]

        /** 添加窗口是否弹出 */
        flag_add: boolean = false

        /** 待添加的场站对象 */
        addItem: StationLink = this.getDefaultItem()

        /** 编辑窗口是否弹出 */
        flag_edit: boolean = false

        /** 待编辑的场站对象 */
        editItem: StationLink = this.getDefaultItem()

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
            let pageParam: PageQuery = {
                pageNum: this.page.current,
                pageSize: this.page.pageSize
            }

            Promise.all([getPageInfo(pageParam), getStations()]).then((res: any) => {
                if (res[0].data.code == 200) {
                    this.page.total = res[0].data.data.totalSize

                    this.stationLinks = res[0].data.data.content
                }
                // 筛选根节点的管理部门，场站只能与这样的管理部门挂钩
                if (res[1].data.code == 200) this.stations = res[1].data.data

                this.stationLinks.map((item: StationLink) => {
                    let tmp: Station | undefined = this.stations.find((s: Station) => item
                        .source == s.id)
                    item.sourceName = tmp ? tmp.name : "无"
                    let tmp2: Station | undefined = this.stations.find((s: Station) => item
                        .target == s.id)
                    item.targetName = tmp2 ? tmp2.name : "无"
                })
            })
        }

        currentChange(value: number) {
            this.page.current = value;
            this.initData();
        }

        pageSizeChange(value: number) {
            this.page.pageSize = value;
            this.initData();
        }

        getDefaultItem(): StationLink {
            return {
                id: 0,
                source: 0,
                sourceName: '',
                target: 0,
                targetName: '',
                type: 1
            }
        }

        /** 添加新对象 */
        addModel() {
            this.flag_add = true
        }

        edit(index: number) {
            this.flag_edit = true
            this.editItem = this.stationLinks[index]
        }

        remove(index: number) {
            deleteStationLink(this.stations[index].id).then((res: any) => {
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

        /** 添加场站 */
        addOk() {
            addStationLink(this.addItem).then((res: any) => {
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

        /** 取消添加场站 */
        addCancel() {
            this.flag_add = false
            this.addItem = this.getDefaultItem()
        }

        /** 编辑场站 */
        editOk() {
            editStationLink(this.editItem).then((res: any) => {
                let {
                    code,
                    data
                } = res.data
                if (code == 200) {
                    this.flag_edit = false
                    this.initData()
                } else {
                    this.$Message.info("编辑出错！！！")
                }
            })
        }

        /** 取消编辑场站 */
        editCancel() {
            this.flag_edit = false
        }
    }
</script>

<style scoped>
    .btnClass button {
        margin: 0px 10px;
    }
</style>