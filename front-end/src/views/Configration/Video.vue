<template>
    <div>
        <h4>视频管理</h4>
        <div class='btnClass'>
            <Button type='info' icon='md-add' @click="addModel">新建</Button>
            <Button type='warning' icon='md-arrow-down'>导出</Button>
        </div>
        <Table border :columns="columns" :data="videos" height="720">
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
                <FormItem label="视频名:">
                    <Input v-model="addItem.name" />
                </FormItem>
                <FormItem label="所属场站:">
                    <el-select v-model="addItem.stationId">
                        <el-option v-for="station in stations" :value="station.id" :key="station.id"
                            :label="station.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="对象类型:">
                    <RadioGroup v-model="addItem.objectType">
                        <Radio v-for="type in objectType" :label="type.val" :key="type.val"> {{type.key}} </Radio>
                    </RadioGroup>
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
                <FormItem label="用户名:">
                    <Input v-model="addItem.username" />
                </FormItem>
                <FormItem label="password:">
                    <Input v-model="addItem.password" />
                </FormItem>
                <FormItem label="视频服务:">
                    <el-select v-model="addItem.serverId">
                        <el-option v-for="server in videoServers" :value="server.id" :key="server.id"
                            :label="server.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="通道号:">
                    <Input v-model="addItem.channelNo" />
                </FormItem>
                <FormItem label="通道名称:">
                    <Input v-model="addItem.channelName" />
                </FormItem>
                <FormItem label="默认预置位:">
                    <Input v-model="addItem.defaultPreset" />
                </FormItem>
                <FormItem label="类型:">
                    <Input v-model="addItem.vendor" />
                </FormItem>
            </Form>
        </Modal>
        <Modal v-model="flag_edit" title="编辑" @on-ok="editOk" @on-cancel="editCancel">
            <Form :model="editItem" :label-width="120">
                <FormItem label="ID:">
                    <Input v-model="editItem.id" />
                </FormItem>
                <FormItem label="视频名:">
                    <Input v-model="editItem.name" />
                </FormItem>
                <FormItem label="所属场站:">
                    <el-select v-model="editItem.stationId">
                        <el-option v-for="station in stations" :value="station.id" :key="station.id"
                            :label="station.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="对象类型:">
                    <RadioGroup v-model="editItem.objectType">
                        <Radio v-for="type in objectType" :label="type.val" :key="type.val"> {{type.key}} </Radio>
                    </RadioGroup>
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
                <FormItem label="用户名:">
                    <Input v-model="editItem.username" />
                </FormItem>
                <FormItem label="password:">
                    <Input v-model="editItem.password" />
                </FormItem>
                <FormItem label="视频服务:">
                    <el-select v-model="editItem.serverId">
                        <el-option v-for="server in videoServers" :value="server.id" :key="server.id"
                            :label="server.name"></el-option>
                    </el-select>
                </FormItem>
                <FormItem label="通道号:">
                    <Input v-model="editItem.channelNo" />
                </FormItem>
                <FormItem label="通道名称:">
                    <Input v-model="editItem.channelName" />
                </FormItem>
                <FormItem label="默认预置位:">
                    <Input v-model="editItem.defaultPreset" />
                </FormItem>
                <FormItem label="类型:">
                    <Input v-model="editItem.vendor" />
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
        Video
    } from '@/types/video.interface'
    import {
        Station
    } from '@/types/station.interface'
    import {
        EnumType
    } from '@/types/enumtype.interface'
    import {
        VideoServer
    } from '@/types/videoserver.interface'
    import {
        PageQuery
    } from '@/types/pagequery.interface'
    import {
        getVideoDtos,
        addVideo,
        editVideo,
        deleteVideo,
        getPageInfo
    } from '@/api/video'
    import {
        getStations
    } from '@/api/station'
    import {
        getVideoServers,
    } from '@/api/videoserver'

    @Component({})
    export default class MeasObjectClass extends Vue {
        stations: Station[] = []
        videos: Video[] = []
        videoServers: VideoServer[] = []

        columns: any[] = [{
                title: 'ID',
                key: 'id'
            },
            {
                title: '监测对象名',
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
                title: '用户名',
                key: 'username'
            },
            {
                title: '密码',
                key: 'password'
            },
            {
                title: '所属服务',
                key: 'serverId',
            },
            {
                title: '通道号',
                key: 'channelNo',
            },
            {
                title: '通道名',
                key: 'channelName',
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

        /** 待添加的对象 */
        addItem: Video = this.getDefaultItem()

        /** 编辑窗口是否弹出 */
        flag_edit: boolean = false

        /** 待编辑的对象 */
        editItem: Video = this.getDefaultItem()

        /** 对象类型 */
        objectType: EnumType[] = this.$store.state.objectType.filter((type: EnumType) => type.key.indexOf(
            '视频') >= 0)

        /** 数据类型 */
        dataType: EnumType[] = this.$store.state.dataType.filter((type: EnumType) => type.key.indexOf(
            '视频') >= 0)

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

            Promise.all([getStations(), getPageInfo(pageParam), getVideoServers()]).then((res: any) => {
                // 场站
                if (res[0].data.code == 200) this.stations = res[0].data.data

                // 视频
                if (res[1].data.code == 200) {
                    this.page.total = res[1].data.data.totalSize
                    this.videos = res[1].data.data.content

                    this.videos.map((item: Video) => {
                        item.createTime = new Date(item.createTime)

                        let tmp_station: Station | undefined = this.stations.find((tmp: Station) =>
                            tmp.id == item.stationId)
                        item.stationName = tmp_station ? tmp_station.name : ''
                    })
                }
                // 服务
                if (res[2].data.code == 200) this.videoServers = res[2].data.data
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
        getDefaultItem(): Video {
            if (!this.objectType) {
                this.objectType = this.$store.state.objectType.filter((type: EnumType) => type.key.indexOf(
                    '视频') >= 0)
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
                username: 'admin',
                password: '12345',
                serverId: 0,
                channelNo: 0,
                channelName: '',
                defaultPreset: 0,
                vendor: 0,
                videoServer: null
            }
        }

        /** 添加新对象 */
        addModel() {
            this.flag_add = true
        }

        edit(index: number) {
            this.flag_edit = true
            this.editItem = this.videos[index]
        }

        remove(index: number) {
            deleteVideo(this.videos[index].id).then((res: any) => {
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
            addVideo(this.addItem).then((res: any) => {
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
            editVideo(this.editItem).then((res: any) => {
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