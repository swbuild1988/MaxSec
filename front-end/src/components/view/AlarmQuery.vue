<template>
    <div>
        <Row class="queryCondition">
            <Col span="6" class="select_item">
            <span>所属场站：</span>
            <el-select v-model="queryPrams.stationId" placeholder="请选择" width="65%">
                <el-option v-for="item in stations" :key="item.id" :label="item.name" :value="item.id">
                </el-option>
            </el-select>
            </Col>
            <Col span="6" class="select_item">
            <span>告警状态：</span>
            <el-select v-model="queryPrams.cleaned" placeholder="请选择" width="65%">
                <el-option v-for="item in alarmStates" :key="item.key" :label="item.key" :value="item.val">
                </el-option>
            </el-select>
            </Col>
            <Col span="6" class="select_item">
            <span>对象类型：</span>
            <el-select v-model="queryPrams.type" placeholder="请选择" width="65%">
                <el-option v-for="item in objectTypes" :key="item.key" :label="item.key" :value="item.val">
                </el-option>
            </el-select>
            </Col>
            <Col span="6" class="select_item">
            <span>告警级别：</span>
            <el-select v-model="queryPrams.level" placeholder="请选择" width="65%">
                <el-option v-for="item in alarmLevels" :key="item.key" :label="item.key" :value="item.val">
                </el-option>
            </el-select>
            </Col>
            <Col span="6" class="select_item">
            <span>开始时间：</span>
            <el-date-picker v-model="queryPrams.startTime" type="date" placeholder="选择日期" width="65%">
            </el-date-picker>
            </Col>
            <Col span="6" class="select_item">
            <span>结束时间：</span>
            <el-date-picker v-model="queryPrams.endTime" type="date" placeholder="选择日期" width="65%">
            </el-date-picker>
            </Col>
            <Col span="12" class="select_item">
            <Button type="primary" @click="queryAlarmData" icon="ios-search"></Button>
            <Button type="info" @click="clearAlarms" icon="ios-trash-outline"></Button>
            </Col>
        </Row>
        <div>
            <Table border ref="selection" :columns="alarmData.columns" :data="alarmData.alarms" height="521"
                class="alarmTable">
                <template slot-scope="{ row }" slot="cleaned">
                    <Icon type="ios-checkmark-circle" :class="{ actived: row.cleaned }" />
                </template>
                <template slot-scope="{ row, index }" slot="option">
                    <Button type="primary" size="small" style="margin-right: 5px"
                        @click="showDetail(row, index)">详情</Button>
                    <Button type="error" size="small" style="margin-right: 5px"
                        @click="cleanDetail(row,index)">清除</Button>
                </template>
            </Table>
            <el-pagination @size-change="pageSizeChange" @current-change="currentChange"
                :current-page.sync="alarmData.page.current" :page-sizes="[10, 20, 50]"
                :page-size="alarmData.page.pageSize" layout="total, sizes, prev, pager, next"
                :total="alarmData.page.total">
            </el-pagination>
        </div>

        <!-- 告警详细信息模态框 -->
        <Modal v-model="showDetailModel" title="告警详细信息" width="45%" class="detailModel">
            <Row>
                <Col span="8" text-align="right">
                告警编号
                <Input v-model="DetailModelData.id" class="detail_input"></Input>
                </Col>
                <Col span="8">
                告警类型
                <Input v-model="DetailModelData.alarmType.name" class="detail_input"></Input>
                </Col>
                <Col span="8">
                告警级别
                <Input v-model="DetailModelData.levelName" class="detail_input"></Input>
                </Col>
            </Row>
            <Row>
                <Col span="8">
                对象编号
                <Input v-model="DetailModelData.objId" class="detail_input"></Input>
                </Col>
                <Col span="8">
                对象名称
                <Input v-model="DetailModelData.objectName" class="detail_input"></Input>
                </Col>
                <Col span="8">
                告警来源
                <Input v-model="DetailModelData.source" class="detail_input"></Input>
                </Col>
            </Row>
            <Row>
                <Col span="8">
                <!-- 告警时间 -->
                <!-- <Input v-model="DetailModelData.time" class="detail_input"></Input> -->
                </Col>
                <Col span="8">
                检测区域
                <Input v-model="DetailModelData.stationName" class="detail_input"></Input>
                </Col>
                <Col span="8">
                <!-- 清除状态 -->
                <!-- <Input v-model="DetailModelData.cleaned" class="detail_input"></Input> -->
                </Col>
            </Row>
            <Row>
                <Col span="8">
                <!-- 清除时间 -->
                <!-- <Input v-model="DetailModelData.cleanedTime" class="detail_input"></Input> -->
                </Col>
            </Row>
            <Row>
                <Col span="24">
                描述
                <Input v-model="DetailModelData.description" type="textarea"
                    :autosize="{ minRows: 2, maxRows: 5 }"></Input>
                </Col>
            </Row>
            <Row>
                <Col span="12">
                <div class="video">
                    <Button @click="downloadVideo">下载告警视频</Button>
                </div>
                </Col>
                <Col span="12">
                <div class="alarm_img">
                    <AlarmImage :alarm="DetailModelData"></AlarmImage>
                </div>
                </Col>
            </Row>
        </Modal>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue,
        Prop
    } from "vue-property-decorator";
    import {
        AlarmVo,
        Alarm
    } from "@/types/alarm.interface";
    import {
        getPageAlarmsByCondition,
        getDefaultAlarm,
        getVideoPathOfAlarm,
        cleanAlarm
    } from "@/api/alarm";
    import {
        Station
    } from "@/types/station.interface";
    import {
        EnumType
    } from "@/types/enumtype.interface";
    import {
        getStations,
        getDefaultStation
    } from "@/api/station";
    import {
        getDate,
        getTime
    } from "@/api/date";
    import {
        getFileFromPath
    } from '@/api/file'
    import AlarmImage from '@/components/view/AlarmImage.vue'

    @Component({
        components: {
            AlarmImage
        }
    })
    export default class AlarmQuery extends Vue {
        showDetailModel: Boolean = false; //告警详情模态框

        @Prop({
            required: false,
            default: () => {
                return {
                    pageNum: 1,
                    pageSize: 10,
                };
            },
        })
        queryPrams: AlarmVo;

        // 告警数据
        alarmData: any = {
            columns: [{
                    type: "selection",
                    width: 60,
                    align: "center",
                },
                {
                    title: "告警编号",
                    key: "id",
                    width: 100,
                },
                {
                    title: "所属场站",
                    key: "stationName",
                    width: 120,
                },
                {
                    title: "告警对象",
                    key: "objectName",
                    width: 100,
                },
                {
                    title: "告警类型",
                    key: "alarmType",
                    render: (h: any, params: any) => {
                        return h("div", params.row.alarmType.name);
                    },
                    width: 100,
                },
                {
                    title: "告警等级",
                    key: "levelName",
                    width: 100,
                },
                {
                    title: "描述",
                    key: "description",
                },
                {
                    title: "已清除",
                    key: "cleaned",
                    slot: "cleaned",
                    width: 80,
                },
                {
                    title: "时间",
                    key: "time",
                    render: (h: any, params: any) => {
                        return h(
                            "div",
                            new Date(params.row.timeStamp).format("yyyy-MM-dd hh:mm:ss")
                        );
                    },
                    width: 160,
                },
                {
                    title: "操作",
                    slot: "option",
                    width: 135,
                },
            ],
            alarms: [],
            page: {
                total: 0,
                pageSize: 10,
                current: 1,
            },
        };
        // 告警详情数据
        DetailModelData: Alarm = getDefaultAlarm();

        /** 所有场站 */
        stations: Station[] = [];
        /** 告警状态 */
        alarmStates: any[] = [{
                key: "未清除",
                val: false,
            },
            {
                key: "已清除",
                val: true,
            },
        ];
        /** 对象类型 */
        objectTypes: EnumType[] = this.$store.state.objectType;
        /** 告警级别 */
        alarmLevels: EnumType[] = this.$store.state.alarmLevel;

        mounted() {
            if (this.queryPrams.pageNum)
                this.alarmData.page.current = this.queryPrams.pageNum;
            if (this.queryPrams.pageSize)
                this.alarmData.page.pageSize = this.queryPrams.pageSize;
            this.init();
        }

        clearQueryVo() {
            this.queryPrams.objId = null
            this.queryPrams.type = null
            this.queryPrams.stationId = null
            this.queryPrams.cleaned = null
            this.queryPrams.level = null
            this.queryPrams.startTime = null
            this.queryPrams.endTime = null
        }

        init() {
            // 将选项加上全部
            let tmp: Station = getDefaultStation();
            tmp.id = 0;
            tmp.name = "全部";
            this.stations = [tmp];
            this.alarmStates = [{
                key: "全部",
                value: 0,
            }, ].concat(this.alarmStates);
            this.objectTypes = [{
                key: "全部",
                val: 0,
            }, ].concat(this.objectTypes);
            this.alarmLevels = [{
                key: "全部",
                val: 0,
            }, ].concat(this.alarmLevels);

            getStations().then((res) => {
                if (res.data.code == 200)
                    this.stations = this.stations.concat(res.data.data);
            });
        }

        queryAlarmData() {
            let params: AlarmVo = {
                pageNum: this.alarmData.page.current,
                pageSize: this.alarmData.page.pageSize,
                stationId: this.queryPrams.stationId && this.queryPrams.stationId > 0 ?
                    this.queryPrams.stationId : null,
                level: this.queryPrams.level && this.queryPrams.level > 0 ?
                    this.queryPrams.level : null,
                type: this.queryPrams.type && this.queryPrams.type > 0 ?
                    this.queryPrams.type : null,
                cleaned: typeof this.queryPrams.cleaned === "boolean" ?
                    this.queryPrams.cleaned : null,
                startTime: this.queryPrams.startTime ?
                    this.queryPrams.startTime : null,
                endTime: this.queryPrams.endTime ? this.queryPrams.endTime : null,
            };

            getPageAlarmsByCondition(params)
                .then((res) => {
                    let {
                        code,
                        data
                    } = res.data;
                    if (code != 200) return;
                    this.alarmData.page.total = data.totalSize;
                    this.alarmData.alarms = data.content;
                })
                .finally(() => {});
        }

        clearAlarms() {
            let ele: any = this.$refs.selection
            let selectAlarms: Alarm[] = ele.getSelection()

            let requests: any[] = []
            for (let i = 0; i < selectAlarms.length; i++) {
                requests.push(cleanAlarm(selectAlarms[i].id))
            }

            Promise.all(requests).then((res: any) => {
                this.$Message.info("清除成功！")
                this.queryAlarmData()
            })
        }

        // 分页
        currentChange(value: number) {
            this.alarmData.page.current = value;
            this.queryAlarmData();
        }

        pageSizeChange(value: number) {
            this.alarmData.page.pageSize = value;
            this.queryAlarmData();
        }

        // 点击详情
        showDetail(row, index) {
            console.log(row);
            this.DetailModelData = row;
            this.showDetailModel = true;
        }

        /** 清除告警 */
        cleanDetail(row, index) {
            cleanAlarm(row.id).then((res: any) => {
                this.$Message.info("清除成功！")
                this.queryAlarmData()
            })
        }

        /** 下载告警视频 */
        downloadVideo() {
            let _this = this

            getVideoPathOfAlarm(this.DetailModelData.id).then((res: any) => {
                if (res.data.code != 200) return

                let paths: string[] = res.data.data
                for (let i = 0; i < paths.length; i++) {

                    getFileFromPath(paths[i]).then((res: any) => {
                        const blob = new Blob([res.data])
                        const fileName = `告警${_this.DetailModelData.id}视频${i}.mp4`

                        if ('download' in document.createElement('a')) { // 支持a标签download的浏览器

                            const link = document.createElement('a') // 创建a标签
                            link.download = fileName
                            link.style.display = 'none'
                            link.href = URL.createObjectURL(blob)
                            document.body.appendChild(link)
                            link.click() // 执行下载
                            URL.revokeObjectURL(link.href) // 释放url
                            document.body.removeChild(link) // 释放标签
                        } else {
                            navigator.msSaveBlob(blob, fileName)
                        }
                    })
                }
            })
        }
    }
</script>

<style lang="less">
    .queryCondition {
        padding: 10px 10px 0;

        // 筛选条件的搜索和删除按钮
        .select_item {
            margin-bottom: 10px;

            .ivu-btn {
                background-color: #36e5ff;
                border: none;
                color: #000;
                font-size: 18px;
                margin-left: 8%;
                width: 9%;
            }
        }
    }

    // 日期
    .el-date-table td.current:not(.disabled) span,
    .el-date-table td.available:hover {
        background-color: #36e5ff;
        color: #fff;
    }

    .el-date-table td.today span,
    .el-date-picker__header-label.active,
    .el-date-picker__header-label:hover,
    .el-picker-panel__icon-btn:hover {
        color: #36e5ff;
    }

    // 已清除图标
    .actived {
        &::before {
            color: #2d8cf0;
        }
    }

    .alarmTable {
        width: 100%;
        height: 100% !important;
    }

    // 模态框底部确定按钮
    .ivu-modal-content {
        .ivu-modal-footer {
            .ivu-btn-primary {
                background-color: #36e5ff !important;
                border: none;
                color: #000;
            }
        }
    }

    // 取消按钮鼠标悬浮
    .ivu-btn:hover {
        color: #36e5ff;
    }

    // 表格中复选框
    .ivu-checkbox-checked .ivu-checkbox-inner {
        border-color: #36e5ff;
        background-color: #36e5ff;
    }

    // 表格中的详情按钮
    .alarmTable.ivu-table-wrapper {
        position: static !important;

        .ivu-table-tbody {
            .ivu-table-row {
                .ivu-table-cell {
                    .ivu-btn {
                        background-color: #36e5ff;
                        border: none;
                        color: #000;
                    }
                }
            }
        }
    }

    // 分页
    .el-pager li.active,
    .el-pager li:hover,
    .el-pagination button:hover,
    .el-select-dropdown__item.selected,
    .el-select .el-input__inner:focus,
    .el-select .el-input.is-focus .el-input__inner,
    .el-pagination__sizes .el-input .el-input__inner:hover,
    .el-input.is-active .el-input__inner,
    .el-input__inner:focus {
        color: #36e5ff;
        border-color: #36e5ff;
    }

    .alarmDialog {
        .ivu-modal-body {
            height: 100% !important;
        }
    }

    .alarm_img,
    .video {
        height: 240px;
        border: 1px solid #000;
        margin-top: 5px;
    }

    .detail_input {
        width: 65%;
        margin-bottom: 5px;
    }

    // 详情模态框
    .detailModel {
        .ivu-modal-body {
            padding: 10px;
        }

        .ivu-input:hover,
        .ivu-input:focus {
            border-color: #36e5ff;
        }
    }
</style>