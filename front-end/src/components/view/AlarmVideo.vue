<template>
    <div style="height: 100%;width: 100%">
        <Col span="6">
        <ul class="alarm_info">
            <li class="info_tit">告警详细信息</li>
            <li>
                编号 &nbsp;
                <span>{{ currentAlarm.id }}</span>
            </li>
            <li>
                对象
                <span>{{ currentAlarm.objectName }}</span>
            </li>
            <li>
                类型
                <span>{{ currentAlarm.alarmType.name }}</span>
            </li>
            <li>
                级别
                <span>{{ currentAlarm.levelName }}</span>
            </li>
            <li>
                时间
                <span>{{ currentAlarm.time }}</span>
            </li>
            <li>
                详情
                <span>{{ currentAlarm.description }}</span>
            </li>
        </ul>
        <div class="btn_box">
            <div class="btn">误报</div>
            <div class="btn">确认</div>
            <div class="btn">升级确认</div>
        </div>
        </Col>
        <Col span="12">
        <div class="alarm_video">
            <HIKOLD :video="video" ref="video" id="alarmVideo_id"></HIKOLD>
        </div>
        </Col>
        <Col span="6">
        <p class="control_tit">视频控制面板</p>
        <ul class="circle_control">
            <!-- 上 -->
            <li class="circle_item item_top" @click="ptzTurn(0)">
                <span class="top_icon"></span>
            </li>
            <!-- 右 -->
            <li class="circle_item item_right" @click="ptzTurn(3)">
                <span class="right_icon"></span>
            </li>
            <!-- 下 -->
            <li class="circle_item item_bottom" @click="ptzTurn(1)">
                <span class="bottom_icon"></span>
            </li>
            <!-- 左 -->
            <li class="circle_item item_left" @click="ptzTurn(2)">
                <span class="left_icon"></span>
            </li>
            <!-- 中 -->
            <li class="item_center" @click="ptzStop">
                <span class="center_icon"></span>
            </li>
        </ul>
        <div class="preset">
            <span class="preset_tit">预置点</span>
            <el-select v-model="selectedValue" placeholder="请选择" class="preset_select">
                <el-option v-for="item in presetList" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
            </el-select>
            <div class="preset_btn">
                <div class="btn" @click="setPreset(selectedValue)">
                    设置
                </div>
                <div class="btn" @click="goPreset(selectedValue)">跳转</div>
            </div>
        </div>
        <ul class="camera_adjust">
            <p class="adjust_tit">镜头调整</p>
            <li>
                <span class="tit">焦距</span>
                <span class="add" @click="ptzTurn(4)">+</span>
                <span class="reduce" @click="ptzTurn(5)">-</span>
            </li>
            <li>
                <span class="tit">焦点</span>
                <span class="add" @click="ptzTurn(6)">+</span>
                <span class="reduce" @click="ptzTurn(7)">-</span>
            </li>
            <li>
                <span class="tit">光圈</span>
                <span class="add" @click="ptzTurn(8)">+</span>
                <span class="reduce" @click="ptzTurn(9)">-</span>
            </li>
        </ul>
        </Col>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue,
        Prop
    } from "vue-property-decorator";
    import {
        getVideoDto
    } from "@/api/video";
    import {
        Alarm
    } from "@/types/alarm.interface";
    import {
        getDefaultAlarm
    } from "@/api/alarm";
    import {
        Video
    } from "@/types/video.interface";
    import HIKOLD from '@/components/Video/HIK_old.vue'

    @Component({
        components: {
            HIKOLD
        }
    })
    export default class AlarmVideo extends Vue {
        currentAlarm: Alarm = getDefaultAlarm();

        selectedValue: number;
        presetList: any[] = [{
                label: "1",
                value: 1,
            },
            {
                label: "2",
                value: 2,
            },
            {
                label: "3",
                value: 3,
            },
            {
                label: "4",
                value: 4,
            },
        ];

        video: Video;

        mounted() {}

        /** 初始化视频 */
        initVideo(alarm: Alarm) {
            // 因为同步问题，所以这里采用将alarm直接传入的方式来
            console.log("当前告警", this.currentAlarm);
            console.log("传入的alarm", alarm);
            this.currentAlarm = alarm;
            this.getVideosInfo();
        }

        // 获取视频信息
        getVideosInfo() {
            console.log("准备获取视频")
            getVideoDto(this.currentAlarm.objId).then((res: any) => {
                let {
                    code,
                    data
                } = res.data;
                if (code != 200) return;
                this.video = data;
                console.log("this.video", this.video)
                let t: any = this.$refs.video
                t.PreviewStart(this.video)
            });
        }

        // 向上、下、左、右、焦距、焦聚、光圈
        ptzTurn(direction) {
            let r: any = this.$refs.video
            r.ptzTurn(direction)
        }
        // 停止
        ptzStop() {
            let r: any = this.$refs.video
            r.ptzStop()
        }
        // 设置
        setPreset(iPreset) {
            let r: any = this.$refs.video
            r.setPreset(iPreset)
        }
        // 跳转
        goPreset(iPreset) {
            let r: any = this.$refs.video
            r.goPreset(iPreset)
        }
    }
</script>

<style lang="less">
    .ivu-modal-body {
        height: 420px;
        padding: 0;

        .ivu-col {
            height: 100%;
            position: relative;

            .alarm_info {
                li {
                    list-style: none;
                    line-height: 38px;
                    border-bottom: 1px solid #ccc;
                    font-size: 14px;
                    padding: 0 5px;

                    & .info_tit {
                        font-size: 18px;
                        font-weight: 700;
                        line-height: 42px;
                        background-color: #18809a;
                        color: #fff;
                    }
                }
            }

            .btn_box {
                position: absolute;
                bottom: 0;
                left: 50%;
                transform: translateX(-50%);
                width: 60%;

                .btn {
                    line-height: 24px;
                    background-color: #18809a;
                    text-align: center;
                    border-radius: 5px;
                    margin-bottom: 5px;
                    color: #fff;
                }
            }

            // 视频
            .alarm_video {
                height: 100%;
                border: 1px solid #000;

                #OCXOverviewTopBody {
                    width: 100%;
                    height: 100%;
                    margin: 0 auto;

                    .smallocxdiv {
                        float: left;
                        width: 100%;
                        height: 100%;
                        z-index: 99;
                    }

                    #HIKOBJECT {
                        width: 100%;
                        height: 100%;
                    }
                }
            }

            // 控制面板
            .control_tit {
                line-height: 38px;
                font-size: 18px;
                font-weight: 700;
                line-height: 42px;
                background-color: #18809a;
                color: #fff;
            }

            // 云台旋转
            .circle_control {
                width: 120px;
                height: 120px;
                top: 10px;
                border-radius: 50%;
                margin: 0 auto;
                overflow: hidden;
                position: relative;

                .circle_item {
                    list-style: none;
                    overflow: hidden;
                    width: 52%;
                    height: 52%;
                    position: absolute;
                    right: 0;
                    top: 0;
                    transform-origin: 0% 100%;
                    border-left: 2px solid rgba(0, 0, 0, 0.3);
                    border-bottom: 2px solid rgba(0, 0, 0, 0.3);
                    background-color: #18809a;
                    cursor: pointer;
                }

                .item_center {
                    list-style: none;
                    width: 50px;
                    height: 50px;
                    border: 5px solid rgba(0, 0, 0, 0.3);
                    border-radius: 50%;
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    background-color: #18809a;
                    cursor: pointer;

                    .center_icon {
                        display: block;
                        width: 20px;
                        height: 20px;
                        background: url("../../assets/images/control_center.png") no-repeat;
                        background-size: 100% 100%;
                        position: absolute;
                        left: 50%;
                        top: 50%;
                        transform: translate(-50%, -50%);
                    }
                }

                .item_top {
                    transform: rotate(-45deg);

                    .top_icon {
                        display: block;
                        width: 20px;
                        height: 20px;
                        background: url("../../assets/images/control_top.png") no-repeat;
                        background-size: 100% 100%;
                        position: absolute;
                        left: 50%;
                        top: 50%;
                        transform: translate(-50%, -50%) rotate(45deg);
                    }
                }

                .item_right {
                    transform: rotate(45deg);

                    .right_icon {
                        display: block;
                        width: 20px;
                        height: 20px;
                        background: url("../../assets/images/control_right.png") no-repeat;
                        background-size: 100% 100%;
                        position: absolute;
                        left: 50%;
                        top: 50%;
                        transform: translate(-50%, -50%) rotate(-45deg);
                    }
                }

                .item_bottom {
                    transform: rotate(135deg);

                    .bottom_icon {
                        display: block;
                        width: 20px;
                        height: 20px;
                        background: url("../../assets/images/control_bottom.png") no-repeat;
                        background-size: 100% 100%;
                        position: absolute;
                        left: 40%;
                        top: 53%;
                        transform: translate(-50%, -50%) rotate(-135deg);
                    }
                }

                .item_left {
                    transform: rotate(-135deg);

                    .left_icon {
                        display: block;
                        width: 20px;
                        height: 20px;
                        background: url("../../assets/images/control_left.png") no-repeat;
                        background-size: 100% 100%;
                        position: absolute;
                        left: 40%;
                        top: 53%;
                        transform: translate(-50%, -50%) rotate(135deg);
                    }
                }
            }

            // 预设点
            .preset {
                margin-top: 20px;

                .preset_tit {
                    display: inline-block;
                    width: 30%;
                    text-align: center;
                }

                .preset_select {
                    width: 60%;
                }

                .preset_btn {
                    display: flex;
                    justify-content: space-around;
                    margin-top: 5px;

                    .btn {
                        width: 36%;
                        text-align: center;
                        line-height: 32px;
                        color: #fff;
                        background-color: #18809a;
                        border-radius: 5px;
                        cursor: pointer;
                    }
                }
            }

            // 镜头调整
            .camera_adjust {
                p.adjust_tit {
                    line-height: 32px;
                    font-size: 14px;
                    text-align: left;
                    padding-left: 10px;
                }

                li {
                    list-style: none;
                    display: flex;
                    justify-content: space-around;
                    margin-bottom: 5px;

                    .tit,
                    .add,
                    .reduce {
                        display: inline-block;
                        line-height: 24px;
                        width: 15%;
                        text-align: right;
                    }

                    .add,
                    .reduce {
                        width: 30%;
                        text-align: center;
                        color: #fff;
                        background-color: #18809a;
                        border-radius: 5px;
                        cursor: pointer;
                    }
                }
            }
        }
    }
</style>