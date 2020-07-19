<template>
    <div class="IntercomClass">
        <HIKVideo ref="video" id="intercom_id"></HIKVideo>

        <div class="buttonClass" style="filter:alpha(opacity=0)">
            <iframe frameborder="0" src="about:blank"
                style="position:absolute; visibility:inherit; top:0px;left:0px;width:100%; height:100%;z-index:-1; filter:alpha(opacity=0);"
                marginHeight="0" marginWidth="0"></iframe>
            <div class="intercom_btn conversation_stop" v-if="talkState">
                <!-- 停止通话 -->
                <div class="icon conversation_stop_icon" @click="talkVideo()"></div>
            </div>
            <div class="intercom_btn conversation_start" v-else>
                <!-- 开始通话 -->
                <div class="icon conversation_start_icon" @click="talkVideo()"></div>
            </div>

            <div class="intercom_btn hang_up" v-if="listerState && callState">
                <!-- 挂断 -->
                <div class="icon hang_up_icon" @click="answerVideo"></div>
            </div>
            <div class="intercom_btn answer" v-else-if="!listerState && callState">
                <!-- 接听 -->
                <div class="icon answer_icon" @click="answerVideo"></div>
            </div>

            <div class="intercom_btn refuse" v-show="callState && !listerState">
                <!-- 拒绝 -->
                <div class="icon refuse_icon" @click="refuseVideo"></div>
            </div>
            <div class="intercom_btn open_door">
                <!-- 开门 -->
                <div class="icon open_door_icon" @click="openVideo"></div>
            </div>
            <!-- <Button type="success" @click="talkVideo">{{
                talkState ? "停止通话" : "开始通话"
            }}</Button>
            <Button type="success" @click="answerVideo" v-show="callState">{{
                listerState ? "挂断" : "接听"
            }}</Button>
            <Button
                type="error"
                @click="refuseVideo"
                v-show="callState && !listerState"
                >拒绝</Button
            >
            <Button type="info" @click="openVideo">开门</Button> -->
        </div>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue,
        Prop,
        Emit,
        Watch
    } from "vue-property-decorator";
    import HIKVideo from "@/components/Video/HIK_old.vue";
    import {
        Video
    } from "@/types/video.interface";
    import {
        MessageType
    } from "@/types/message.interface";
    import {
        Alarm
    } from "@/types/alarm.interface";
    import {
        open,
        refuse,
        ringoff,
        answer
    } from "@/api/intercom";

    @Component({
        components: {
            HIKVideo,
        },
    })
    export default class VideoIntercom extends Vue {
        @Prop({}) video: Video;

        @Prop({
            required: false,
            type: Boolean,
            default: false,
        })
        initCallState: boolean;

        @Watch('initCallState')
        onCallStateChange(o: any, n: any) {
            this.callState = this.initCallState;
        }

        /** 当前接听状态 */
        listerState: boolean = false;
        /** 对讲状态 */
        talkState: boolean = false;
        /** 呼叫状态 */
        callState: boolean = false;

        mounted() {
            this.callState = this.initCallState;
        }

        /** 接听 */
        answerVideo() {
            if (this.listerState) {
                // 当前接听状态，所以挂断

                ringoff(this.video.id).then((res: any) => {
                    if (res.data.code != 200) return;

                    this.listerState = !this.listerState;
                    this.callState = false;
                });
            } else {
                // 当前挂断状态，所以接听

                answer(this.video.id).then((res: any) => {
                    if (res.data.code != 200) return;

                    this.listerState = !this.listerState;
                });
            }
        }

        /** 拒绝 */
        refuseVideo() {
            refuse(this.video.id).then((res: any) => {
                this.callState = false;
            });
        }

        /** 开门 */
        openVideo() {
            open(this.video.id).then((res: any) => {});
        }

        /** 通话 */
        talkVideo() {
            let video: any = this.$refs.video;

            if (this.talkState) {
                // 当前正在通话，停止通话
                if (video.StopTalk()) this.talkState = false;
            } else {
                if (video.StartTalk()) this.talkState = true;
            }
        }

        /** 预览 */
        PreviewStart(video: Video) {
            let v: any = this.$refs.video;
            v.PreviewStart(video)
        }
    }
</script>

<style lang="less">
    .IntercomClass {
        height: 100%;
        width: 100%;
        position: relative;
    }

    .buttonClass {
        position: absolute;
        z-index: 99999;
        right: 10px;
        bottom: 10px;
        height: 70px;
        overflow: hidden;

        .intercom_btn {
            width: 70px;
            height: 100%;
            float: left;
            padding: 5px;
            cursor: pointer;

            .icon {
                width: 60px;
                height: 60px;
                border-radius: 50%;
            }

            // 开始通话
            .conversation_start_icon {
                background: url("../../assets/images/conversation_start.png") no-repeat;
                background-size: 100% 100%;
            }

            // 停止通话
            .conversation_stop_icon {
                background: url("../../assets/images/conversation_stop.png") no-repeat;
                background-size: 100% 100%;
            }

            // 接听
            .answer_icon {
                background: url("../../assets/images/answer.png") no-repeat;
                background-size: 100% 100%;
            }

            // 挂断
            .hang_up_icon {
                background: url("../../assets/images/hang_up.png") no-repeat;
                background-size: 100% 100%;
            }

            // 拒绝
            .refuse_icon {
                background: url("../../assets/images/hang_up.png") no-repeat;
                background-size: 100% 100%;
            }

            // 开门
            .open_door_icon {
                background: url("../../assets/images/open_door.png") no-repeat;
                background-size: 100% 100%;
            }
        }
    }

    button {
        margin: 5px;
    }

    .intercom {
        .ivu-modal {
            height: 52% !important;
        }

        .ivu-modal-content {
            height: 100% !important;
        }
    }
</style>