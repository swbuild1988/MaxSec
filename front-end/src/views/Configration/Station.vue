<template>
    <div>
        <h4>场站管理</h4>
        <div class='btnClass'>
            <Button type='info' icon='md-add' @click="addModel">新建</Button>
            <Button type='warning' icon='md-arrow-down'>导出</Button>
        </div>
        <Table border :columns="columns" :data="stations" height="720">
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
                <FormItem label="场站名:">
                    <Input v-model="addItem.name" />
                </FormItem>
                <FormItem label="地理位置信息:">
                    <Input v-model="addItem.position" />
                </FormItem>
                <FormItem label="管理部门:">
                    <el-select v-model="addItem.managementId">
                        <el-option v-for="management in managements" :value="management.id" :key="management.id"
                            :label="management.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="图标:">
                    <RadioGroup v-model="addItem.icon">
                        <Radio v-for="icon in icons" :label="icon.id" :key="icon.id">
                            <img :src="icon.img" height="20" width="20"></img>
                            <span>{{icon.name}}</span>
                        </Radio>
                    </RadioGroup>
                </FormItem>
            </Form>
        </Modal>
        <Modal v-model="flag_edit" title="编辑" @on-ok="editOk" @on-cancel="editCancel">
            <Form :model="editItem" :label-width="120">
                <FormItem label="场站名:">
                    <Input v-model="editItem.name" />
                </FormItem>
                <FormItem label="地理位置信息:">
                    <Input v-model="editItem.position" />
                </FormItem>
                <FormItem label="管理部门:">
                    <el-select v-model="editItem.managementId">
                        <el-option v-for="management in managements" :value="management.id" :key="management.id"
                            :label="management.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="图标:">
                    <RadioGroup v-model="editItem.icon">
                        <Radio v-for="icon in icons" :label="icon.id" :key="icon.id">
                            <img :src="icon.img" height="20" width="20"></img>
                            <span>{{icon.name}}</span>
                        </Radio>
                    </RadioGroup>
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
        Management
    } from '@/types/management.interface'
    import {
        PageQuery
    } from '@/types/pagequery.interface'
    import {
        getStations,
        addStation,
        editStation,
        deleteStation,
        getDefaultStation,
        getPageInfo
    } from '@/api/station'
    import {
        getManagements
    } from '@/api/management'

    @Component({})
    export default class StationClass extends Vue {
        stations: Station[] = []
        managements: Management[] = []

        columns: any[] = [{
                title: 'ID',
                key: 'id'
            },
            {
                title: '场站名',
                key: 'name'
            },
            {
                title: '地理位置信息',
                key: 'position'
            },
            {
                title: '所处管理部门',
                key: 'managementName'
            },
            {
                title: '图标',
                key: 'icon'
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
        addItem: Station = getDefaultStation()

        /** 编辑窗口是否弹出 */
        flag_edit: boolean = false

        /** 待编辑的场站对象 */
        editItem: Station = getDefaultStation()

        /** 图标待选项 */
        icons: any[] = this.$store.state.icons

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

            Promise.all([getPageInfo(pageParam), getManagements()]).then((res: any) => {
                if (res[0].data.code == 200) {
                    this.page.total = res[0].data.data.totalSize

                    this.stations = res[0].data.data.content
                }
                // 筛选根节点的管理部门，场站只能与这样的管理部门挂钩
                if (res[1].data.code == 200) this.managements = res[1].data.data.filter((item: Management) =>
                    item.leaf)
                this.managements.unshift({
                    id: 0,
                    name: '无',
                    position: '',
                    level: 0,
                    fatherId: 0,
                    fatherName: '无',
                    leaf: false,
                    map: '',
                    createTime: new Date()
                })

                this.stations.map((item: Station) => {
                    item.createTime = new Date(item.createTime)
                    let tmp: Management | undefined = this.managements.find((mana: Management) => item
                        .managementId == mana.id)
                    item.managementName = tmp ? tmp.name : "无"
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

        /** 添加新对象 */
        addModel() {
            this.flag_add = true
        }

        edit(index: number) {
            this.flag_edit = true
            this.editItem = this.stations[index]
        }

        remove(index: number) {
            deleteStation(this.stations[index].id).then((res: any) => {
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
            addStation(this.addItem).then((res: any) => {
                let {
                    code,
                    data
                } = res.data
                if (code == 200) {
                    this.flag_add = false
                    this.addItem = getDefaultStation()
                    this.initData()
                } else {
                    this.$Message.info("添加出错！！！")
                }
            })
        }

        /** 取消添加场站 */
        addCancel() {
            this.flag_add = false
            this.addItem = getDefaultStation()
        }

        /** 编辑场站 */
        editOk() {
            editStation(this.editItem).then((res: any) => {
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