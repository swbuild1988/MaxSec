<template>
    <div class="video_intercom">
        <div class="col">
            <Intercom :video="video" :initCallState="callState" ref="intercom"></Intercom>
        </div>
        <div class="col">
            <div class="panel lawfu_person">
                <!-- <h4>认证结果</h4>
                <div class="certification_results">
                    <div class="certification_img">
                        <alarm-image :alarm="test"></alarm-image>
                    </div>
                    <div class="certification_text">
                        <span class="tit">认证成功√</span>
                        <p>姓名:董杉</p>
                        <p>职务:巡检员</p>
                    </div>
                </div> -->
                <h4>认证成功</h4>
                <div class="admission_personnel">
                    <ul class="person_box">
                        <li v-for="item in alarm1" :key="item.id" class="person_img">
                            <alarm-image :alarm="item"></alarm-image>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="panel real_time_alarm">
                <h4>
                    实时告警
                </h4>

                <ScrollBoard :config="config" style="width:100%;height:80%;"></ScrollBoard>
            </div>
        </div>
        <div class="col">
            <div class="panel">
                <h4>认证失败</h4>
                <div class="admission_personnel">
                    <ul class="person_box">
                        <li v-for="item in alarm2" :key="item.id" class="person_img">
                            <alarm-image :alarm="item"></alarm-image>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="panel">
                <h4>
                    人员管理
                </h4>
                <!-- <Table stripe :columns="columns" :data="cards" :loading="cardLoading"> -->
                </Table>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue
    } from "vue-property-decorator";
    import Intercom from "@/components/view/Intercom.vue";
    import AlarmImage from "@/components/view/AlarmImage.vue";
    import {
        Video
    } from "@/types/video.interface";
    import {
        MeasObjectVo
    } from "@/types/measobject.interface";
    import {
        MessageType
    } from "@/types/message.interface";
    import {
        Alarm,
        AlarmVo
    } from "@/types/alarm.interface";
    import {
        Card,
        Department
    } from "@/types/intercom.interface";
    import {
        getVideoDtosByCondition,
        getDefaultVideo
    } from "@/api/video";
    import {
        getCards,
        getDepartments
    } from "@/api/intercom";
    import {
        getPageAlarmsByCondition
    } from "@/api/alarm";
    import {
        DateUtil
    } from "@/util/dateUtil";
    import ScrollBoard from "@/components/scrollBoard/scrollBoard.vue";
    import {
        getDate,
        getTime
    } from "@/api/date";

    @Component({
        components: {
            Intercom,
            AlarmImage,
            ScrollBoard,
        },
    })
    export default class VideoIntercom extends Vue {
        /** 场站ID */
        stationId: any = 1;
        /** 对象类型 -- 可视对讲 */
        objectTypeId: number = 5;
        /** 可视对讲视频 */
        video: Video = getDefaultVideo();

        /** 今天的所有告警 */
        alarms: Alarm[] = [];
        /** 成功或失败图片数目 */
        imgNum: number = 4;

        /** 认证成功告警 */
        alarm1: Alarm[] = [];
        /** 认证失败告警 */
        alarm2: Alarm[] = [];

        /** 呼入状态 */
        callState: boolean = false;

        /** 所有的人/卡 */
        cards: Card[] = [];
        /** 所有部门 */
        departments: Department[] = [];
        /** 获取卡片中 */
        cardLoading: boolean = true;
        columns: any[] = [{
                type: "index",
                width: 60,
                align: "center",
            },
            {
                title: "工号",
                key: "jobNo",
                width: 80,
                align: "left",
            },
            {
                title: "姓名",
                key: "name",
                width: 100,
                align: "left",
            },
            {
                title: "部门",
                key: "departmentName",
                align: "left",
            },
        ];
        /** 刷新列表 */
        config: any = {
            data: [],
        };

        mounted() {
            this.stationId = this.$store.state.stationId;

            this.initData();
        }

        initData() {
            let vo: MeasObjectVo = {
                stationId: this.stationId,
                objectType: this.objectTypeId,
            };

            getVideoDtosByCondition(vo).then((res: any) => {
                if (res.data.code == 200) {
                    let tmp: Video[] = res.data.data;
                    if (tmp.length > 0) {
                        this.video = tmp[0];
                        let v: any = this.$refs.intercom
                        v.PreviewStart(this.video)
                    }

                    this.refreshData();
                    this.refreshSuccessImage();
                    this.refreshFailImage();
                    this.refreshPerson();
                }
            });

            getDepartments().then((res: any) => {
                if (res.data.code == 200) {
                    this.departments = res.data.data;
                }
            });

            this.startListenMQ();
        }

        /** 刷新数据 */
        refreshData() {
            // 查找今天最近的20条
            let vo: AlarmVo = {
                pageNum: 1,
                pageSize: 20,
                objId: this.video.id,
            };

            getPageAlarmsByCondition(vo).then((res: any) => {
                if (res.data.code != 200) return;

                this.alarms = res.data.data.content;
                // var dataRes = getDate("-") + getTime();
                
                this.config = {
                    data: this.alarms.map((item, index) => {
                        return [new Date(item.timeStamp).format("MM-dd hh:mm"), item.stationName, item.alarmType.name];
                    }),
                    align: ["center", "center", "center"],
                    rowNum: 8,
                };
            });
        }

        /** 刷新成功认证图像 */
        refreshSuccessImage() {
            let vo: AlarmVo = {
                pageNum: 1,
                pageSize: 4,
                objId: this.video.id,
                alarmType: 3001
            };

            getPageAlarmsByCondition(vo).then((res: any) => {
                if (res.data.code != 200) return;

                this.alarm1 = res.data.data.content;
            })
        }

        /** 刷新失败认证图像 */
        refreshFailImage() {
            let vo: AlarmVo = {
                pageNum: 1,
                pageSize: 4,
                objId: this.video.id,
                alarmType: 3002
            };

            getPageAlarmsByCondition(vo).then((res: any) => {
                if (res.data.code != 200) return;

                this.alarm2 = res.data.data.content;
            })

        }

        /** 刷新人员 */
        refreshPerson() {
            getCards(this.video.id).then((res: any) => {});
        }

        /** MQ监听 */
        startListenMQ() {
            console.log("可视对讲 添加监听器到MQ");
            this.TransferStation.addListener("VideoIntercom", this.callback);
        }

        /** MQ停止监听 */
        stopListenMQ() {
            console.log("可视对讲 移除监听器");
            this.TransferStation.deleteListener("VideoIntercom");
        }

        callback(respond: any) {
            let message: MessageType = JSON.parse(respond);

            if (message.type == "IntercomAlarm") {
                let alarm: Alarm = message.body;

                if (alarm.alarmTypeId == 3001 || alarm.alarmTypeId == 3002) {
                    // 可视对讲则刷新即使告警
                    this.refreshData();
                    this.refreshSuccessImage();
                    this.refreshFailImage();
                } else if (alarm.alarmTypeId == 3003) {
                    // 呼入消息
                    this.callState = true;
                }
            } else if (message.type == "IntercomGetCard") {
                // 获取所有卡片
                console.log("获取所有卡", message.body);
                console.log("部门", this.departments);
                this.cards = message.body;
                this.cards.map((card: Card) => {
                    let index: number = this.departments.findIndex(
                        (a) => a.id == card.department
                    );
                    card.departmentName =
                        index >= 0 ? this.departments[index].name : "";
                });
                this.cardLoading = false;
            }
        }

        beforeDestroy() {
            this.stopListenMQ();
        }
    }
</script>

<style lang="less">
    .video_intercom {
        display: flex;
        height: 100%;
        padding: 10px 40px 10px;

        .col {
            flex: 2;
            // height: 720px;
            height: 80%;

            &:nth-child(1) {
                flex: 6;
                background-color: #1a2855;
                margin-right: 10px;
            }

            .lawfu_person {
                .certification_results {
                    height: 450px;

                    .certification_img {
                        width: 163px;
                        height: 182px;
                        margin: 0 auto;
                        background-color: #ccc;
                    }

                    .certification_text {
                        width: 200px;
                        height: 118px;
                        border: 2px solid #36e5ff;
                        border-radius: 10px;
                        margin: 20px auto 0;

                        .tit {
                            display: block;
                            width: 139px;
                            line-height: 42px;
                            background-color: #348d9d;
                            border-radius: 0px 0px 21px 21px;
                            margin: 0 auto;
                            font-size: 22px;
                            text-align: center;
                        }

                        p {
                            font-size: 16px;
                            line-height: 32px;
                            text-align: center;
                        }
                    }
                }
            }

            .panel {
                color: #ffffff;
                // height: 350px;
                height: 50%;
                background-color: #112143;
                background: url("../../assets/images/security_panel_bd.png") no-repeat;
                background-size: 100% 100%;

                h4 {
                    line-height: 40px;
                    font-size: 22px;
                    color: #2ed1f6;
                    font-weight: 500;
                    padding-left: 21px;
                    font-weight: 700;
                }

                .ivu-table-wrapper {
                    height: 89%;
                    border: none;
                    color: #fff;

                    .ivu-table:after {
                        background-color: transparent;
                    }

                    .ivu-table:before {
                        background-color: transparent;
                    }

                    .ivu-table {
                        background-color: transparent !important;
                        border: none !important;
                        color: #fff;

                        .ivu-table-column-center {
                            background-color: transparent;
                            border: none;
                        }

                        .ivu-table-column-left {
                            background-color: transparent;
                            border: none;
                        }

                        .ivu-table-stripe .ivu-table-body tr:nth-child(2n) td {
                            background-color: transparent;
                        }
                    }
                }

                &:nth-child(1),
                &:nth-child(2) {
                    margin: 0 10px 10px 0;
                }

                &:nth-child(3),
                &:nth-child(4) {
                    margin: 0 10px 10px 0;
                }

                .people_table {
                    width: 100%;

                    // height: 100%;
                    tr {
                        td {
                            text-align: center;
                            font-size: 14px;
                            line-height: 30px;
                        }
                    }
                }

                .admission_personnel {
                    height: 90%;

                    .person_box {
                        height: 100%;
                        display: flex;
                        flex-wrap: wrap;
                        justify-content: space-around;

                        .person_img {
                            list-style: none;
                            background-color: #ccc;
                            width: 146px;
                            height: 140px;
                        }
                    }
                }
            }

            .real_time_alarm {
                .scroll-board {
                    .rows {
                        height: 100% !important;
                    }
                }
            }
        }
    }
</style>