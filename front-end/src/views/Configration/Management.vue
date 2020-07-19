<template>
    <div>
        <h4>部门管理</h4>
        <div class='btnClass'>
            <Button type='info' icon='md-add' @click="addModel">新建</Button>
            <Button type='warning' icon='md-arrow-down'>导出</Button>
        </div>
        <Table border :columns="columns" :data="managements" height="720">
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
                <FormItem label="管理部门名:">
                    <Input v-model="addItem.name" />
                </FormItem>
                <FormItem label="地理位置信息:">
                    <Input v-model="addItem.position" />
                </FormItem>
                <FormItem label="等级">
                    <Input v-model="addItem.level" />
                </FormItem>
                <FormItem label="父节点:">
                    <el-select v-model="addItem.fatherId">
                        <el-option v-for="management in selectManagements" :value="management.id" :key="management.id"
                            :label="management.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="叶子节点:">
                    <Checkbox v-model="addItem.leaf">是否为叶子结点</Checkbox>
                </FormItem>
                <FormItem label="地图:">
                    <el-select v-model="addItem.map">
                        <el-option v-for="map in customs" :value="map.name" :key="map.id" :label="map.name"></el-option>
                    </el-select>
                </FormItem>
            </Form>
        </Modal>
        <Modal v-model="flag_edit" title="编辑" @on-ok="editOk" @on-cancel="editCancel">
            <Form :model="editItem" :label-width="120">
                <FormItem label="管理部门名:">
                    <Input v-model="editItem.name" />
                </FormItem>
                <FormItem label="地理位置信息:">
                    <Input v-model="editItem.position" />
                </FormItem>
                <FormItem label="等级">
                    <Input v-model="editItem.level" />
                </FormItem>
                <FormItem label="父节点:">
                    <el-select v-model="editItem.fatherId">
                        <el-option v-for="management in selectManagements" :value="management.id" :key="management.id"
                            :label="management.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="叶子节点:">
                    <Checkbox v-model="editItem.leaf">是否为叶子结点</Checkbox>
                </FormItem>
                <FormItem label="地图:">
                    <el-select v-model="editItem.map">
                        <el-option v-for="map in customs" :value="map.name" :key="map.id" :label="map.name"></el-option>
                    </el-select>
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
        Management
    } from '@/types/management.interface'
    import {
        CustomMap
    } from '@/types/custommap.interface'
    import {
        PageQuery
    } from '@/types/pagequery.interface'
    import {
        getManagements,
        addManagement,
        editManagement,
        deleteManagement,
        getPageInfo
    } from '@/api/management'
    import {
        getCustomMap,
    } from '@/api/custommap'

    @Component({})
    export default class ManagementClass extends Vue {
        managements: Management[] = []
        customs: CustomMap[] = []

        columns: any[] = [{
                title: 'ID',
                key: 'id'
            },
            {
                title: '部门名',
                key: 'name'
            },
            {
                title: '地理位置信息',
                key: 'position'
            },
            {
                title: '级别',
                key: 'level'
            },
            {
                title: '父节点',
                key: 'fatherName'
            },
            {
                title: '叶子结点',
                key: 'leaf'
            },
            {
                title: '地图名',
                key: 'map'
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

        /** 下拉框中待选择的 */
        selectManagements: Management[] = [this.getDefaultItem()]

        /** 待添加的管理部门对象 */
        addItem: Management = this.getDefaultItem()

        /** 编辑窗口是否弹出 */
        flag_edit: boolean = false

        /** 待编辑的管理部门对象 */
        editItem: Management = this.getDefaultItem()

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

            getPageInfo(pageParam).then((res: any) => {
                let {
                    code,
                    data
                } = res.data
                if (code == 200) {
                    this.page.total = data.totalSize

                    this.managements = data.content
                    this.selectManagements = [this.getDefaultItem()]
                    this.managements.map((item: Management) => {
                        item.createTime = new Date(item.createTime)
                        let tmp: Management | undefined = this.managements.find((mana: Management) =>
                            item.fatherId == mana.id)
                        item.fatherName = tmp ? tmp.name : '无'
                        this.selectManagements.push(item)
                    })
                }
            })

            getCustomMap().then((res: any) => {
                let {
                    code,
                    data
                } = res.data

                if (code == 200) {
                    this.customs = data
                } else {
                    this.$Message.info(data)
                }
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

        /** 获取默认值 */
        getDefaultItem(): Management {
            return {
                id: 0,
                name: '无',
                position: '',
                level: 0,
                fatherId: 0,
                fatherName: '',
                leaf: false,
                map: '',
                createTime: new Date()
            }
        }

        /** 添加新对象 */
        addModel() {
            this.flag_add = true
        }

        edit(index: number) {
            this.flag_edit = true
            this.editItem = this.managements[index]
        }

        remove(index: number) {
            deleteManagement(this.managements[index].id).then((res: any) => {
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

        /** 添加管理部门 */
        addOk() {
            addManagement(this.addItem).then((res: any) => {
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

        /** 取消添加管理部门 */
        addCancel() {
            this.flag_add = false
            this.addItem = this.defaultItem
        }

        /** 编辑管理部门 */
        editOk() {
            editManagement(this.editItem).then((res: any) => {
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

        /** 取消编辑管理部门 */
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