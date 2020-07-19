<template>
    <div>
        <h4>视频服务管理</h4>
        <div class='btnClass'>
            <Button type='info' icon='md-add' @click="addModel">新建</Button>
            <Button type='warning' icon='md-arrow-down'>导出</Button>
        </div>
        <Table border :columns="columns" :data="videoServers" height="720">
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
                <FormItem label="视频服务名:">
                    <Input v-model="addItem.name" />
                </FormItem>
                <FormItem label="所属场站:">
                    <el-select v-model="addItem.stationId">
                        <el-option v-for="station in stations" :value="station.id" :key="station.id"
                            :label="station.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="所属设备序列号:">
                    <Input v-model="addItem.equipmentSn" />
                </FormItem>
                <FormItem label="IP:">
                    <Input v-model="addItem.ip" />
                </FormItem>
                <FormItem label="端口:">
                    <Input v-model="addItem.port" />
                </FormItem>
                <FormItem label="通道数:">
                    <InputNumber :max="1000" :min="1" v-model="addItem.portNum"></InputNumber>
                </FormItem>
            </Form>
        </Modal>
        <Modal v-model="flag_edit" title="编辑" @on-ok="editOk" @on-cancel="editCancel">
            <Form :model="editItem" :label-width="120">
                <FormItem label="ID:">
                    <Input v-model="editItem.id" />
                </FormItem>
                <FormItem label="视频服务名:">
                    <Input v-model="editItem.name" />
                </FormItem>
                <FormItem label="所属场站:">
                    <el-select v-model="editItem.stationId">
                        <el-option v-for="station in stations" :value="station.id" :key="station.id"
                            :label="station.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="所属设备序列号:">
                    <Input v-model="editItem.equipmentSn" />
                </FormItem>
                <FormItem label="IP:">
                    <Input v-model="editItem.ip" />
                </FormItem>
                <FormItem label="端口:">
                    <Input v-model="editItem.port" />
                </FormItem>
                <FormItem label="通道数:">
                    <InputNumber :max="1000" :min="1" v-model="editItem.portNum"></InputNumber>
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
        VideoServer
    } from '@/types/videoserver.interface'
    import {
        Station
    } from '@/types/station.interface'
    import {
        PageQuery
    } from '@/types/pagequery.interface'
    import {
        getVideoServers,
        addVideoServer,
        editVideoServer,
        deleteVideoServer,
        getPageInfo
    } from '@/api/videoserver'
    import {
        getStations
    } from '@/api/station'
    import {
        EnumType
    } from '@/types/enumtype.interface'

    @Component({})
    export default class VideoServerClass extends Vue {
        stations: Station[] = []
        videoServers: VideoServer[] = []

        columns: any[] = [{
                title: 'ID',
                key: 'id'
            },
            {
                title: '视频服务名',
                key: 'name'
            },
            {
                title: '所属场站',
                key: 'stationName'
            },
            {
                title: 'IP',
                key: 'ip'
            },
            {
                title: '端口',
                key: 'port'
            },
            {
                title: '通道数',
                key: 'portNum'
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

        /** 对象类型 */
        objectType: Array < EnumType > = this.$store.state.objectType.filter((type: EnumType) => type.key.indexOf(
            '视频服务') >= 0)

        /** 数据类型 */
        dataType: Array < EnumType > = this.$store.state.dataType.filter((type: EnumType) => type.key.indexOf(
            '视频') >= 0)

        /** 添加窗口是否弹出 */
        flag_add: boolean = false

        /** 待添加的对象 */
        addItem: VideoServer = this.getDefaultItem()

        /** 编辑窗口是否弹出 */
        flag_edit: boolean = false

        /** 待编辑的对象 */
        editItem: VideoServer = this.getDefaultItem()

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

            Promise.all([getStations(), getPageInfo(pageParam)]).then((res: any) => {
                // 场站
                if (res[0].data.code == 200) this.stations = res[0].data.data

                // 视频服务
                if (res[1].data.code == 200) {
                    this.page.total = res[1].data.data.totalSize
                    this.videoServers = res[1].data.data.content
                    this.videoServers.map((item: VideoServer) => {
                        item.createTime = new Date(item.createTime)

                        let tmp_station: Station | undefined = this.stations.find((tmp: Station) =>
                            tmp.id == item.stationId)
                        item.stationName = tmp_station ? tmp_station.name : ''
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
        getDefaultItem(): VideoServer {
            if (!this.objectType) {
                this.objectType = this.$store.state.objectType.filter((type: EnumType) => type.key.indexOf(
                    '视频服务') >= 0)
            }
            if (!this.dataType) {
                this.dataType = this.$store.state.dataType.filter((type: EnumType) => type.key.indexOf(
                    '视频') >= 0)
            }
            return {
                id: 0,
                name: '',
                stationId: 0,
                stationName: '',
                objectType: this.objectType.length > 0 ? this.objectType[0].val : 0,
                objectTypeName: this.objectType.length > 0 ? this.objectType[0].key : '',
                dataType: this.dataType.length > 0 ? this.dataType[0].val : 0,
                dataTypeName: this.dataType.length > 0 ? this.dataType[0].key : '',
                equipmentSn: '',
                createTime: new Date(),
                ip: '127.0.0.1',
                port: 80,
                portNum: 100,
                username: '',
                password: '',
                vendor: 0
            }
        }

        /** 添加新对象 */
        addModel() {
            this.flag_add = true
        }

        edit(index: number) {
            this.flag_edit = true
            this.editItem = this.videoServers[index]
        }

        remove(index: number) {
            deleteVideoServer(this.videoServers[index].id).then((res: any) => {
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

        /** 添加对象 */
        addOk() {
            addVideoServer(this.addItem).then((res: any) => {
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
        addCancel() {
            this.flag_add = false
            this.addItem = this.getDefaultItem()
        }

        /** 编辑对象 */
        editOk() {
            editVideoServer(this.editItem).then((res: any) => {
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

        /** 取消编辑 */
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