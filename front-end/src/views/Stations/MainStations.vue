<template>
    <div class="stations_box">
        <div class="tit_time">
            <span class="date">{{ date }}</span>
            <span class="week">{{ week }}</span>
            <span class="time">{{ time }}</span>
        </div>
        <h1 class="stations_hd">
            {{ station.name }}
        </h1>
        <div class="message">
            <Icon
                type="md-notifications"
                @click.native="showAlarm"
                class="message_icon"
            />
            <span class="badge_icon" v-if="alarmNum"> </span>
        </div>
        <el-dropdown class="user_icon" @command="handleCommand">
            <span class="el-dropdown-link">
                <Icon type="ios-contact" />
            </span>
            <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="setting">设置</el-dropdown-item>
                <el-dropdown-item command="logout">退出</el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
        <div class="stations_content">
            <Menu mode="horizontal" :active-name="activeName">
                <MenuItem name="Overview" to="overview">
                    总 览
                </MenuItem>
                <MenuItem name="industrialTV" to="industrial-tv">
                    工 业 电 视
                </MenuItem>
                <MenuItem
                    name="HighConsequenceVideo"
                    to="high-consequence-video"
                >
                    高 后 果 视 频
                </MenuItem>
                <!-- <MenuItem name="FaceRecognition" to="face-recognition">
                人脸识别
                </MenuItem> -->
                <MenuItem name="VideoIntercom" to="video-intercom">
                    可 视 对 讲
                </MenuItem>
                <MenuItem name="PerimeterAlarm" to="perimeter-alarm">
                    周 界 报 警
                </MenuItem>
                <MenuItem name="FireAlarm" to="fire-alarm">
                    火 灾 报 警
                </MenuItem>
            </Menu>
            <router-view></router-view>
        </div>
        <Modal
            v-model="modelShow"
            title="告警详情"
            class="alarmDialog"
            width="80%"
            @on-cancel="closeModal"
        >
            <AlarmQuery :queryPrams="alarmVo" ref="alarmQuery"></AlarmQuery>
        </Modal>
        <Modal
            v-model="alarmVideoShow"
            width="45%"
            @on-visible-change="AlarmModalVisibleVhange"
        >
            <iframe
                frameborder="0"
                src="about:blank"
                style="position:absolute; visibility:inherit; top:0px;left:0px;width:100%; height:100%;z-index:-1; filter:alpha(opacity=0);"
                marginHeight="0"
                marginWidth="0"
            ></iframe>
            <AlarmVideo ref="AlarmVideoModel"></AlarmVideo>
        </Modal>
        <Modal
            v-model="intercomShow"
            width="45%"
            title="可视对讲通话"
            @on-visible-change="AlarmModalVisibleVhange"
        >
            <iframe
                frameborder="0"
                src="about:blank"
                style="position:absolute; visibility:inherit; top:0px;left:0px;width:100%; height:100%;z-index:-1; filter:alpha(opacity=0);"
                marginHeight="0"
                marginWidth="0"
            ></iframe>
            <Intercom
                ref="IntercomModel"
                :video="intercomVideo"
                :initCallState="true"
                v-if="intercomShow"
            ></Intercom>
        </Modal>
    </div>
</template>
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import AlarmQuery from "@/components/view/AlarmQuery.vue";
import AlarmVideo from "@/components/view/AlarmVideo.vue";
import Intercom from "@/components/view/Intercom.vue";
import { Station } from "@/types/station.interface";
import { AlarmVo, Alarm } from "@/types/alarm.interface";
import { getStation, getDefaultStation } from "@/api/station";
import { getCountByCondition, getDefaultAlarm } from "@/api/alarm";
import { getVideoDtosByCondition, getDefaultVideo } from "@/api/video";
import { MessageType } from "@/types/message.interface";
import { Video } from "@/types/video.interface";
import { MeasObjectVo } from "@/types/measobject.interface";
import { Audio } from "@/util/audio";
import { getDate, getTime, getWeek } from "@/api/date";

@Component({
    components: {
        AlarmQuery,
        AlarmVideo,
        Intercom,
    },
})
export default class MainStations extends Vue {
    date: String = "";
    week: String = "";
    time: String = "";
    activeName: any = "overview";
    stationId: any = 1;
    station: Station = getDefaultStation();
    /** 告警数目 */
    alarmNum: number = 0;

    /** 告警的模态框 */
    modelShow: boolean = false;
    /** 默认点击查看的告警属性 */
    alarmVo: AlarmVo = {
        pageNum: 1,
        pageSize: 10,
        cleaned: false,
    };

    /** 告警视频的弹窗 */
    alarmVideoShow: boolean = false;
    /** 弹窗告警 */
    alarmVideo: Alarm = getDefaultAlarm();
    $refs: {
        alarmQuery: HTMLFormElement;
        AlarmVideoModel: HTMLFormElement;
    };

    /** 可视对讲呼入弹窗 */
    intercomShow: boolean = false;
    /** 可视对讲视频 */
    intercomVideo: Video = getDefaultVideo();
    /** 对象类型 -- 可视对讲 */
    objectTypeId: number = 5;

    /** 当前显示的消息 */
    currentMessage: MessageType;
    /** 缓存的消息 */
    cacheMessage: MessageType | null = null;

    mounted() {
        this.activeName = this.$route.name;
        this.stationId = this.$store.state.stationId;
        this.init();
        this.startListenMQ();
        this.date = getDate(".");
        this.week = getWeek();
        this.getTime();
    }

    init() {
        getStation(this.stationId).then((res: any) => {
            let { code, data } = res.data;
            if (code == 200) {
                this.station = data;
            }
        });

        // 查找为清除的告警数目
        let vo: AlarmVo = {
            cleaned: false,
        };
        getCountByCondition(vo)
            .then((res: any) => {
                let { code, data } = res.data;

                if (code == 200) this.alarmNum = data;
            })
            .finally(() => {
                this.alarmNum = 10;
            });

        // 可视对讲相机
        let intercomVo: MeasObjectVo = {
            stationId: this.stationId,
            objectType: this.objectTypeId,
        };

        getVideoDtosByCondition(intercomVo).then((res: any) => {
            if (res.data.code == 200) {
                let tmp: Video[] = res.data.data;
                if (tmp.length > 0) this.intercomVideo = tmp[0];
                console.log("intercomVideo", this.intercomVideo);
            }
        });
    }

    getTime() {
        this.time = getTime();
        setTimeout(() => {
            this.getTime();
        }, 1000);
    }
    handleCommand(command) {
        if (command == "logout") {
            this.logout();
        } else if (command == "setting") {
            this.setting();
        }
    }

    showAlarm() {
        this.modelShow = true;
        let ref: any = this.$refs.alarmQuery;
        ref.queryAlarmData();
    }

    startListenMQ() {
        console.log("MainStation 添加监听器到MQ");
        this.TransferStation.addListener(
            "MainStation",
            this.showAlarmVideo.bind(this)
        );
    }

    stopListenMQ() {
        console.log("MainStation 移除监听器");
        this.TransferStation.deleteListener("MainStation");
    }

    showAlarmVideo(respond: any) {
        let message: MessageType = JSON.parse(respond);
        console.log("MainStation 接收到消息：", message);

        // 告警弹窗
        if (message.type == "VideoAlarm") {
            this.cacheMessage = message;
        } else if (message.type == "IntercomAlarm") {
            // 可视对讲呼叫

            let alarm: Alarm = message.body;
            // 3003：设备呼入
            if (alarm.alarmTypeId == 3003) {
                // this.intercomShow = true;
                this.cacheMessage = message;
            }
        }

        // 检查告警，看是否需要弹窗
        this.checkAlarmMessage();

        /** 声音工具 */
        let audio: Audio = new Audio();

        console.log("audio", audio);
        // 声音处理
        if (
            message.type == "IntercomAlarm" &&
            message.body.alarmTypeId == 3003
        ) {
            // 可视对讲呼入
            audio.playSound("intercom");
        } else {
            let alarm: Alarm = message.body;
            // 根据告警级别
            if (alarm.level == 1) {
                audio.playSound("tipSound");
            } else if (alarm.level == 2) {
                audio.playSound("preAlarmSound");
            } else if (alarm.level == 3) {
                audio.playSound("alarmSound");
            } else if (alarm.level == 4) {
                audio.playSound("faultSound");
            }
        }
    }

    /**
     * 先看有没有缓存消息
     * 再看当前有没有弹窗显示
     * 最后将缓存显示
     *
     */
    checkAlarmMessage() {
        // 缓存都没有，直接退出
        if (!this.cacheMessage) return;

        // 判断当前有没有弹窗显示
        if (this.intercomShow || this.alarmVideoShow) return;

        // 符合显示要求，开始操作
        // 将缓存复制到当前消息
        this.currentMessage = JSON.parse(JSON.stringify(this.cacheMessage));
        this.cacheMessage = null;

        // 告警弹窗
        if (this.currentMessage.type == "VideoAlarm") {
            // 视频告警
            this.alarmVideoShow = true;
            this.alarmVideo = this.currentMessage.body;

            let alarmVideoModel: any = this.$refs.AlarmVideoModel;
            alarmVideoModel.initVideo(this.alarmVideo);
        } else if (this.currentMessage.type == "IntercomAlarm") {
            // 可视对讲呼叫

            // 如果当前在可视对讲，则无需弹框
            if (this.$route.name != "VideoIntercom") {
                let alarm: Alarm = this.currentMessage.body;
                // 3003：设备呼入
                if (alarm.alarmTypeId == 3003) {
                    this.intercomShow = true;
                }
            }
        }
    }

    AlarmModalVisibleVhange(val: boolean) {
        console.log("visible", val);
        /** 如果关闭的话，检查一下 */
        if (!val) {
            setTimeout(() => {
                this.checkAlarmMessage();
            }, 1000);
        }
    }

    closeModal() {
        this.$refs.alarmQuery.clearQueryVo();
    }

    // 退出
    logout() {
        this.$store.commit("removeParamter");
        this.$router.push({
            path: "/login",
        });
    }
    // 设置
    setting() {
        this.$router.push({
            name: "config",
        });
    }

    beforeDestroy() {
        this.stopListenMQ();
    }
}
</script>
<style lang="less" scoped>
.alarmDialog {
    .ivu-modal {
        height: 100%;
    }

    .ivu-table-wrapper {
        position: static;
    }
}

.stations_box {
    width: 100%;
    height: 100%;
    background: url("../../assets/images/overview_bg.png") center no-repeat;
    background-size: 100% 100%;

    .tit_time {
        width: 30%;
        line-height: 86px;
        position: absolute;
        top: 0;
        left: 60px;
        color: #ffffff;
        font-size: 18px;
        // margin-left: 60px;
        .date {
            margin-right: 15px;
        }
        .week {
            margin-right: 15px;
        }
        .time {
            font-family: "UnidreamLED";
            font-size: 24px;
        }
    }

    h1.stations_hd {
        line-height: 90px;
        text-align: center;
        color: #36e5ff;
        text-shadow: 0px 3px 4px rgba(12, 85, 223, 1);
        font-size: 38px;
        font-weight: 400;
    }

    .message {
        width: 36px !important;
        height: 30px !important;
        position: absolute;
        top: 2%;
        right: 8%;
        cursor: pointer;

        .ivu-icon {
            font-size: 34px;
        }
        .badge_icon {
            display: block;
            width: 13px;
            height: 13px;
            border-radius: 50%;
            background-color: red;
            position: absolute;
            top: 4px;
            right: 0;
        }

        .ivu-icon-md-notifications:before {
            color: #36e5ff;
        }
    }

    .user_icon {
        position: absolute;
        top: 3%;
        right: 5%;

        .ivu-icon-ios-contact {
            cursor: pointer;
        }

        .ivu-icon-ios-contact:before {
            color: #36e5ff;
            font-size: 36px;
        }
    }

    .stations_content {
        padding: 5px 50px 0;
        height: 100%;
    }

    .ivu-menu {
        padding: 0 0 0 120px;
        height: 34px;
        line-height: 34px;
        margin-bottom: 10px;
    }

    .ivu-menu-horizontal::after {
        background-color: transparent;
    }

    .ivu-menu-light {
        background-color: transparent;
    }

    .ivu-menu-item {
        font-size: 18px;
        font-weight: bold;
        color: #0d1836;
        width: 220px;
        text-align: center;
        border-bottom: none;
        background: url("../../assets/images/shape-2.png") no-repeat;
        background-size: 100% 100%;
        margin-right: 30px;

        &:nth-child(3) {
            margin-right: 120px;
        }

        &:nth-child(4),
        &:nth-child(5),
        &:nth-child(6) {
            background: url("../../assets/images/shape-3.png") no-repeat;
            background-size: 100% 100%;

            &:hover {
                background: url("../../assets/images/shape-4.png") no-repeat;
                background-size: 100% 100%;
                color: #0d1836;
                border-bottom: none;
            }
        }

        &:hover {
            background: url("../../assets/images/shape-1.png") no-repeat;
            background-size: 100% 100%;
            color: #0d1836;
            border-bottom: none;
        }
    }

    .ivu-menu-item-active {
        background: url("../../assets/images/shape-1.png") no-repeat;
        background-size: 100% 100%;
        color: #0d1836;
    }

    .ivu-menu-item-selected {
        &:nth-child(5),
        &:nth-child(6),
        &:nth-child(4) {
            background: url("../../assets/images/shape-4.png") no-repeat;
            background-size: 100% 100%;
            color: #0d1836;
        }
    }
}
</style>
