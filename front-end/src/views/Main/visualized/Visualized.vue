<template>
    <div class="visualized">
        <header class="visualized_header">
            <div class="tit_time">
                <span class="date">{{ date }}</span>
                <span class="week">{{ week }}</span>
                <span class="time">{{ time }}</span>
            </div>
            <div class="tit">广东管网粤西干线综合安防系统</div>
            <div class="tit_icon">
                <span class="user_icon"></span>
                <span class="logout_icon" @click="logout"></span>
            </div>
        </header>
        <section class="visualized_content">
            <div class="col">
                <div class="drag_panel">
                    <EquipmentCondition></EquipmentCondition>
                </div>
                <div class="drag_panel">
                    <AlarmStatistics></AlarmStatistics>
                </div>
                <div class="drag_panel">
                    <EquipmentStatistics></EquipmentStatistics>
                </div>
            </div>
            <div class="col">
                <div class="map">
                    <Map> </Map>
                </div>
                <div class="chart">
                    <div class="drag_panel_center">
                        <h4>
                            <span class="icon"></span>
                            广州站
                        </h4>
                    </div>
                    <div class="drag_panel_center">
                        <h4>
                            <span class="icon"></span>
                            阳江站
                        </h4>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="drag_panel">
                    <EventStatistics></EventStatistics>
                </div>
                <div class="drag_panel">
                    <GrowthTrend></GrowthTrend>
                </div>
                <div class="drag_panel">
                    <RealTimeAlarm></RealTimeAlarm>
                </div>
            </div>
        </section>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import EquipmentCondition from "@/views/Main/visualized/EquipmentCondition.vue";
import AlarmStatistics from "@/views/Main/visualized/AlarmStatistics.vue";
import EquipmentStatistics from "@/views/Main/visualized/EquipmentStatistics.vue";
import EventStatistics from "@/views/Main/visualized/EventStatistics.vue";
import GrowthTrend from "@/views/Main/visualized/GrowthTrend.vue";
import RealTimeAlarm from "@/views/Main/visualized/RealTimeAlarm.vue";
import Map from "@/views/Main/visualized/Map.vue";
import { log } from "util";
import { getDate, getTime, getWeek } from "@/api/date";

@Component({
    components: {
        EquipmentCondition,
        AlarmStatistics,
        EquipmentStatistics,
        EventStatistics,
        GrowthTrend,
        RealTimeAlarm,
        Map,
    },
})
export default class Visualized extends Vue {
    date: String = "";
    week: String = "";
    time: String = "";
    mounted() {
        this.date = getDate(".");
        this.week = getWeek();
        this.getTime();
    }

    getTime() {
        this.time = getTime();
        setTimeout(() => {
            this.getTime();
        }, 1000);
    }
    logout() {
        console.log(this.$store);
        this.$store
            .dispatch("logout")
            .then((result: any) => {
                this.$Message.success("登出成功~");
                this.$router.push({
                    path: "/login",
                });
            })
            .catch((err) => {
                this.$Message.warning("登陆失败！");
            });
    }
}
</script>
<style lang="less">
.visualized {
    background: url("../../../assets/images/visualized_bg.png") no-repeat top
        center;
    background-size: 100% 100%;
    width: 100%;
    height: 100%;
}
@font-face {
    font-family: "UnidreamLED";
    src: url("../../../assets/icon/1244627994/597/UnidreamLED.ttf")
        format("truetype");
}
.visualized_header {
    .tit {
        line-height: 82px;
        text-align: center;
        background: url("../../../assets/images/visualized_hd.png") no-repeat;
        background-size: 100% 100%;
        color: rgba(255, 255, 255, 1);
        text-shadow: 0px 3px 4px rgba(12, 85, 223, 1);
    }
    position: relative;
    .tit_time {
        width: 30%;
        line-height: 48px;
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
    .tit_icon {
        width: 60px;
        line-height: 82px;
        position: absolute;
        right: 60px;
        top: 15px;
        overflow: hidden;
        .user_icon {
            display: block;
            width: 20px;
            height: 20px;
            background: url("../../../assets/images/user_icon.png") no-repeat;
            background-size: 100%;
            float: left;
            margin-right: 20px;
            cursor: pointer;
        }
        .logout_icon {
            display: block;
            width: 20px;
            height: 22px;
            background: url("../../../assets/images/logout_icon.png") no-repeat;
            background-size: 100%;
            float: left;
            cursor: pointer;
        }
    }
}

.visualized_content {
    margin: 0 auto;
    padding: 0 0.270833rem 0.208333rem;
    display: flex;
    height: 100%;

    div.col {
        flex: 3;
        height: 100%;

        &:nth-child(2) {
            flex: 5;
        }

        .drag_panel {
            width: 429px;
            // height: 290px;
            height: 29%;
            background: rgba(255, 255, 255, 0.1);
            margin: 0 auto 15px;
            border: 2px solid rgba(0, 74, 165, 1);
            position: relative;
            .alarm_statistics_chart {
                // height: 230px;
                height: 80%;
            }

            &::before {
                content: "";
                display: block;
                width: 10px;
                height: 10px;
                position: absolute;
                top: 0;
                right: 0;
                border-top: 1px solid rgba(0, 203, 239, 1);
                border-right: 1px solid rgba(0, 203, 239, 1);
            }

            &::after {
                content: "";
                display: block;
                width: 10px;
                height: 10px;
                position: absolute;
                left: 0;
                bottom: 0;
                border-left: 1px solid rgba(0, 203, 239, 1);
                border-bottom: 1px solid rgba(0, 203, 239, 1);
            }
            .alarm_statistics {
                height: 100%;
            }
        }

        .map {
            width: 815px;
            // height: 634px;
            height: 60%;
            margin: 0 auto 17px;
        }

        .chart {
            display: flex;
            height: 35%;

            .drag_panel_center {
                // height: 285px;
                height: 80%;
                flex: 1;
                position: relative;
                background: rgba(255, 255, 255, 0.1);
                border: 2px solid rgba(0, 74, 165, 1);

                &:nth-child(1) {
                    margin-right: 10px;
                }
                h4 {
                    line-height: 60px;
                    font-size: 22px;
                    color: #f9ffff;
                    font-weight: 500;
                    padding-left: 61px;

                    .icon {
                        display: block;
                        width: 22px;
                        height: 24px;
                        position: absolute;
                        left: 17px;
                        top: 18px;
                        background: url("../../../assets/images/video_icon.png")
                            no-repeat;
                        background-size: 100% 100%;
                    }
                }
                &::before {
                    content: "";
                    display: block;
                    width: 10px;
                    height: 10px;
                    position: absolute;
                    top: 0;
                    right: 0;
                    border-top: 1px solid rgba(0, 203, 239, 1);
                    border-right: 1px solid rgba(0, 203, 239, 1);
                }

                &::after {
                    content: "";
                    display: block;
                    width: 10px;
                    height: 10px;
                    position: absolute;
                    left: 0;
                    bottom: 0;
                    border-left: 1px solid rgba(0, 203, 239, 1);
                    border-bottom: 1px solid rgba(0, 203, 239, 1);
                }
            }
        }
    }
}
</style>
