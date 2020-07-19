<template>
    <div>
        <h4>用户管理</h4>
        <div class='btnClass'>
            <Button type='info' icon='md-add' v-permission="['user:add']" @click="addModel">新建</Button>
            <Button type='warning' icon='md-arrow-down'>导出</Button>
        </div>
        <Table border :columns="columns" :data="users">
            <template slot-scope="{ row, index }" slot="action">
                <Button type="success" size="small" style="margin-right: 5px" icon='md-create'
                    @click="edit(index)">编辑</Button>
                <Button type="error" size="small" icon='md-trash' @click="remove(index)">删除</Button>
            </template>
        </Table>

        <Modal v-model="flag_add" title="新增" @on-ok="addOk" @on-cancel="addCancel">
            <Form :model="addItem" :label-width="120">
                <FormItem label="用户名:">
                    <Input v-model="addItem.name" />
                </FormItem>
                <FormItem label="密码:">
                    <Input type="password" v-model="addItem.password" />
                </FormItem>
                <FormItem label="确认密码:">
                    <Input type="password" v-model="addItem.confirmPassword" />
                </FormItem>
                <FormItem label="管理部门:">
                    <el-select v-model="addItem.managementId">
                        <el-option v-for="management in managements" :value="management.id" :key="management.id"
                            :label="management.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="所属场站:" v-show="addItem.managementId == 0">
                    <el-select v-model="addItem.stationId">
                        <el-option v-for="station in stations" :value="station.id" :key="station.id"
                            :label="station.name"></el-option>
                    </el-select>
                </FormItem>
            </Form>
        </Modal>
        <Modal v-model="flag_edit" title="编辑" @on-ok="editOk" @on-cancel="editCancel">
            <Form :model="editItem" :label-width="120">
                <FormItem label="用户名:">
                    <Input v-model="editItem.name" />
                </FormItem>
                <FormItem label="管理部门:">
                    <el-select v-model="editItem.managementId">
                        <el-option v-for="management in managements" :value="management.id" :key="management.id"
                            :label="management.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="所属场站:" v-show="editItem.managementId == 0">
                    <el-select v-model="editItem.stationId">
                        <el-option v-for="station in stations" :value="station.id" :key="station.id"
                            :label="station.name"></el-option>
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
        User
    } from '@/types/user.interface'
    import {
        Management
    } from '@/types/management.interface'
    import {
        Station
    } from '@/types/station.interface'
    import {
        getUsers,
        addUser,
        deleteUser,
        editUser,
    } from '@/api/user'
    import {
        getManagements
    } from '@/api/management'
    import {
        getStations
    } from '@/api/station'
    import {
        sha256
    } from 'js-sha256';

    @Component({})
    export default class UserClass extends Vue {
        users: User[] = []
        managements: Management[] = []
        stations: Station[] = []

        columns: any[] = [{
                title: 'ID',
                key: 'id'
            },
            {
                title: '用户名',
                key: 'name'
            },
            {
                title: '所属部门',
                key: 'managementName'
            },
            {
                title: '所属场站',
                key: 'stationName'
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

        /** 待添加的用户对象 */
        addItem: User = this.getDefaultItem()

        /** 编辑窗口是否弹出 */
        flag_edit: boolean = false

        /** 待编辑的用户对象 */
        editItem: User = this.getDefaultItem()

        mounted() {
            this.initData()
        }

        initData() {
            Promise.all([getUsers(), getManagements(), getStations()]).then((res: any) => {
                if (res[0].data.code == 200) this.users = res[0].data.data
                // 筛选根节点的管理部门，用户只能与这样的管理部门挂钩
                if (res[1].data.code == 200) this.managements = res[1].data.data
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
                if (res[2].data.code == 200) this.stations = res[2].data.data
                this.stations.unshift({
                    id: 0,
                    name: '无',
                })

                this.users.map((item: User) => {
                    let tmp: Management | undefined = this.managements.find((mana: Management) => item
                        .managementId == mana.id)
                    item.managementName = tmp ? tmp.name : "无"

                    let stationIndex: number = this.stations.findIndex(a => a.id == item.stationId)
                    item.stationName = stationIndex < 0 ? "" : this.stations[stationIndex].name
                })
            })
        }

        getDefaultItem(): User {
            return {
                id: 0,
                name: '',
                password: '',
                confirmPassword: '',
                managementId: 0,
                managementName: '',
                stationId: 0,
                stationName: '',
            }
        }

        /** 添加新对象 */
        addModel() {
            this.flag_add = true
        }

        edit(index: number) {
            this.flag_edit = true
            this.editItem = this.users[index]
        }

        remove(index: number) {
            deleteUser(this.users[index].id).then((res: any) => {
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

        /** 添加用户 */
        addOk() {
            if (this.addItem.password == this.addItem.confirmPassword) {
                this.addItem.password = sha256(this.addItem.password)
                addUser(this.addItem).then((res: any) => {
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
            } else {
                this.$Message.info("密码不一致！")
            }
        }

        /** 取消添加用户 */
        addCancel() {
            this.flag_add = false
            this.addItem = this.getDefaultItem()
        }

        /** 编辑用户 */
        editOk() {
            editUser(this.editItem).then((res: any) => {
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

        /** 取消编辑用户 */
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