<template>
    <div>
        <h4>告警类型管理</h4>
        <div class='btnClass'>
            <Button type='info' icon='md-add' @click="addModel">新建</Button>
            <Button type='warning' icon='md-arrow-down'>导出</Button>
        </div>
        <Table border :columns="columns" :data="alarmTypes" height="720">
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
                <FormItem label="ID:">
                    <Input v-model="addItem.id" />
                </FormItem>
                <FormItem label="类型名:">
                    <Input v-model="addItem.name" />
                </FormItem>
                <FormItem label="对象类型:">
                    <el-select v-model="addItem.objectType">
                        <el-option v-for="type in objectTypes" :value="type.val" :key="type.val" :label="type.key">
                        </el-option>
                    </el-select>
                </FormItem>
                <FormItem label="类别:">
                    <Input v-model="addItem.category" />
                </FormItem>
                <FormItem label="级别:">
                    <Input v-model="addItem.level" />
                </FormItem>
                <FormItem label="描述:">
                    <Input v-model="addItem.description" />
                </FormItem>
            </Form>
        </Modal>
        <Modal v-model="flag_edit" title="编辑" @on-ok="editOk" @on-cancel="editCancel">
            <Form :model="editItem" :label-width="120">
                <FormItem label="ID:">
                    <Input v-model="editItem.id" />
                </FormItem>
                <FormItem label="类型名:">
                    <Input v-model="editItem.name" />
                </FormItem>
                <FormItem label="对象类型:">
                    <el-select v-model="editItem.objectType">
                        <el-option v-for="type in objectTypes" :value="type.val" :key="type.val" :label="type.key">
                        </el-option>
                    </el-select>
                </FormItem>
                <FormItem label="类别:">
                    <Input v-model="editItem.category" />
                </FormItem>
                <FormItem label="级别:">
                    <Input v-model="editItem.level" />
                </FormItem>
                <FormItem label="描述:">
                    <Input v-model="editItem.description" />
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
        AlarmType
    } from '@/types/alarmType.interface'
    import {
        EnumType
    } from '@/types/enumtype.interface'
    import {
        getPageInfo,
        getAlarmTypes,
        addAlarmType,
        editAlarmType,
        deleteAlarmType
    } from '@/api/alarmType'
    import {
        PageQuery
    } from '@/types/pagequery.interface'

    @Component({})
    export default class ManagementClass extends Vue {
        alarmTypes: AlarmType[] = []

        columns: any[] = [{
                title: 'ID',
                key: 'id'
            },
            {
                title: '类型名',
                key: 'name'
            },
            {
                title: '对象类别',
                key: 'objectTypeName'
            },
            {
                title: '类别',
                key: 'category'
            },
            {
                title: '等级',
                key: 'level'
            },
            {
                title: '描述',
                key: 'description'
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

        /** 待添加的告警类型对象 */
        addItem: AlarmType = this.getDefaultItem()

        /** 编辑窗口是否弹出 */
        flag_edit: boolean = false

        /** 待编辑的告警类型对象 */
        editItem: AlarmType = this.getDefaultItem()

        /** 对象类型类型 */
        objectTypes: Array < EnumType > = this.$store.state.objectType

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

                    this.alarmTypes = data.content
                    this.alarmTypes.map((item: AlarmType) => {
                        let objIndex: number = this.objectTypes.findIndex(t => t.val == item.objectType)
                        item.objectTypeName = objIndex < 0 ? '' : this.objectTypes[objIndex].key
                    })
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
        getDefaultItem(): AlarmType {
            return {
                id: 0,
                name: '',
                objectType: 0,
                objectTypeName: '',
                category: 0,
                level: 0,
                description: ''
            }
        }

        /** 添加新对象 */
        addModel() {
            this.flag_add = true
        }

        edit(index: number) {
            this.flag_edit = true
            this.editItem = this.alarmTypes[index]
        }

        remove(index: number) {
            deleteAlarmType(this.alarmTypes[index].id).then((res: any) => {
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

        /** 添加告警类型 */
        addOk() {
            addAlarmType(this.addItem).then((res: any) => {
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

        /** 取消添加告警类型 */
        addCancel() {
            this.flag_add = false
            this.addItem = this.defaultItem
        }

        /** 编辑告警类型 */
        editOk() {
            editAlarmType(this.editItem).then((res: any) => {
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

        /** 取消编辑告警类型 */
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