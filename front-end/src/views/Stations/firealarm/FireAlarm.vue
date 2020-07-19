<template>
    <div class="fire_alarm">
        <div class="col">
            <div class="aside_top">
                <div class="search_box">
                    <span class="search_icon"></span>
                    <Input
                        class="search"
                        prefix="ios-search"
                        placeholder="搜索"
                        style="width: auto"
                    />
                </div>
                <!-- 视频列表 -->
                <div class="video_list">
                    <button>
                        <span class="down_arrow"></span>
                        <span class="video_icon"></span>
                        视频列表
                    </button>
                    <ul class="dropdown ">
                        <li
                            class=" isVideosItemActived dropdown_item"
                            v-for="item in videos"
                            :key="item.id"
                        >
                            <span class="item_icon"></span>
                            {{ item.name }}
                        </li>
                    </ul>
                </div>
            </div>
            <div class="fire_alarm_data">
                <h4>火灾告警</h4>
                <ul>
                    <li v-for="item in fireAlarmData" :key="item.id">
                        {{ item.name }}
                    </li>
                </ul>
            </div>
        </div>
        <div class="col">
            <FireAlarmVideo></FireAlarmVideo>
        </div>
        <div class="col">
            <div class="laser_methane">
                <h4>激光甲烷</h4>
                <div id="OCXBody">
                    <div class="smallocxdiv" id="NetPlayOCX9">
                        <object
                            classid="CLSID:CAFCF48D-8E34-4490-8154-026191D73924"
                            codebase="/NetVideoActiveX23.cab"
                            standby="Waiting..."
                            id="HIKOBJECT9"
                            width="100%"
                            height="100%"
                            name="HIKOBJECT9"
                        ></object>
                    </div>
                </div>
            </div>
            <div class="methane_concentration">
                <h4>
                    甲烷浓度
                </h4>
                <MethaneConcentration></MethaneConcentration>
            </div>
            <div class="history_trend">
                <h4>
                    历史趋势
                </h4>
                <div id="historyTrendId"></div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import FireAlarmVideo from "@/components/FireAlarmVideo/FireAlarmVideo.vue";
import MethaneConcentration from "@/views/Stations/firealarm/MethaneConcentration.vue";
import { ECharts, EChartOption, EChartsSeriesType } from "echarts";

@Component({
    components: {
        FireAlarmVideo,
        MethaneConcentration,
    },
})
export default class FireAlarm extends Vue {
    myChart!: ECharts;
    isShowVideoList: Boolean = true;
    m_bDVRControl: any;
    videos: any = [
        {
            id: "1",
            name: "生产区东",
        },
        {
            id: "2",
            name: "生产区西",
        },
        {
            id: "3",
            name: "走廊",
        },
    ];
    fireAlarmData: any = [
        {
            name: "06-03 14:29:01    机柜间",
            id: "1",
        },
        {
            name: "06-03 14:29:01    UPS",
            id: "1",
        },
        {
            name: "06-03 14:29:01    监控室",
            id: "1",
        },
    ];

    //   视频列表显示隐藏
    showVideoList() {
        this.isShowVideoList = !this.isShowVideoList;
    }

    mounted() {
        this.m_bDVRControl = document.getElementById("HIKOBJECT9");
        this.ArrangeWindowChildren();
        this.initHistoryTrend();
    }

    // 重置窗口大小
    ArrangeWindowChildren() {
        document.getElementById("NetPlayOCX9").style.width = "100%";
        document.getElementById("NetPlayOCX9").style.height = "90%";
    }

    initHistoryTrend() {
        this.myChart = this.$echarts.init(
            document.getElementById("historyTrendId")
        );

        var option: any = {
            grid: {
                top: "35%",
                left: "20%",
            },
            xAxis: {
                type: "category",
                data: ["6/8", "6/9", "6/10", "6/11", "6/12"],
                axisLabel: {
                    color: "#fff",
                },
                axisLine: {
                    lineStyle: {
                        color: "#00ABEB",
                    },
                },
                axisTick: {
                    show: false,
                },
            },
            yAxis: {
                type: "value",
                axisLine: {
                    show: false,
                },
                axisLabel: {
                    color: "#fff",
                },
                splitLine: {
                    lineStyle: {
                        color: "#00ABEB",
                    },
                },
                interval: 500,
                max: 1000,
                min: 0,
            },
            series: [
                {
                    data: [20, 532, 101, 934, 390],
                    type: "line",
                    smooth: true,
                    symbol: "none",
                    lineStyle: {
                        color: {
                            type: "linear",
                            x: 0,
                            y: 0,
                            x2: 0,
                            y2: 1,
                            colorStops: [
                                {
                                    offset: 0,
                                    color: "#2A65BE", // 0% 处的颜色
                                },
                                {
                                    offset: 1,
                                    color: "#38BDB4", // 100% 处的颜色
                                },
                            ],
                            global: false, // 缺省为 false
                        },
                    },
                },
            ],
        };
        this.myChart.setOption(option);
        window.addEventListener("resize", () => this.myChart.resize(), false);
    }
}
</script>
<style lang="less">
.fire_alarm {
    display: flex;
    padding: 5px 25px;
    height: 100%;
    .col {
        flex: 2;
        height: 80%;
        &:nth-child(1) {
        }
        &:nth-child(2) {
            flex: 8;
            margin: 0 5px 0;
        }
        &:nth-child(3) {
            flex: 2;
            // 激光甲烷
            .laser_methane {
                height: 45%;
                margin-bottom: 2%;
                background: url("../../../assets/images/security_panel_bd.png")
                    no-repeat;
                background-size: 100% 100%;
                padding: 5px;
                #OCXBody {
                    width: 100%;
                    height: 95%;
                    margin-bottom: 5px;

                    .smallocxdiv {
                        float: left;
                        display: ;
                        width: 33%;
                        height: 33%;
                        z-index: 1000;
                        border: 1px solid #eee;
                    }
                }
                h4 {
                    line-height: 45px;
                    font-size: 22px;
                    color: #2ed1f6;
                    font-weight: 500;
                    padding-left: 21px;
                    font-weight: 700;
                }
            }
            // 甲烷浓度
            .methane_concentration {
                height: 27%;
                margin-bottom: 2%;
                background: url("../../../assets/images/security_panel_bd.png")
                    no-repeat;
                background-size: 100% 100%;
                h4 {
                    line-height: 45px;
                    font-size: 22px;
                    color: #2ed1f6;
                    font-weight: 500;
                    padding-left: 21px;
                    font-weight: 700;
                }
            }
            // 历史趋势
            .history_trend {
                height: 27%;
                background: url("../../../assets/images/security_panel_bd.png")
                    no-repeat;
                background-size: 100% 100%;
                h4 {
                    line-height: 45px;
                    font-size: 22px;
                    color: #2ed1f6;
                    font-weight: 500;
                    padding-left: 21px;
                    font-weight: 700;
                }
                #historyTrendId {
                    height: 80%;
                }
            }
        }
        // 侧边上部分
        .aside_top {
            height: 70%;
            background-color: #001b55;
            // 侧边搜索
            .search_box {
                position: relative;
                .search_icon {
                    display: block;
                    width: 30px;
                    height: 30px;
                    background: url("../../../assets/images/search_icon.png")
                        no-repeat;
                    background-size: 100% 100%;
                    position: absolute;
                    left: 40px;
                    top: 9px;
                }
                .search {
                    width: 80%;
                    height: 48px;
                    margin-left: 105px;
                    margin-right: 15px;
                    input {
                        width: 100%;
                    }
                    i {
                        display: none;
                    }
                    .ivu-input {
                        background-color: transparent;
                        border: none;
                        font-size: 20px;
                        color: #fff;
                        padding-left: 0px;
                    }
                }
            }
        }

        // 侧边视频列表
        .video_list {
            height: 90%;
            font-size: 20px;
            overflow-y: auto;
            overflow-x: hidden;
            button {
                width: 100%;
                line-height: 34px;
                background-color: transparent;
                color: #fff;
                border: none;
                cursor: pointer;
                padding-left: 18px;
                outline: none;
                position: relative;
                .video_icon {
                    display: block;
                    width: 36px;
                    height: 30px;
                    background: url("../../../assets/images/video_list_icon.png")
                        no-repeat;
                    background-size: 100% 100%;
                    position: absolute;
                    top: 3px;
                    left: 40px;
                }
                .group_icon {
                    display: block;
                    width: 36px;
                    height: 30px;
                    background: url("../../../assets/images/group_icon.png")
                        no-repeat;
                    background-size: 100% 100%;
                    position: absolute;
                    top: 5px;
                    left: 40px;
                }
            }
            .down_arrow {
                display: block;
                width: 16px;
                height: 10px;
                background: url("../../../assets/images/down_arrow.png")
                    no-repeat;
                background-size: 100% 100%;
                position: absolute;
                top: 10px;
                left: 10px;
            }
            .dropdown {
                width: 100%;
                color: #fff;
                .dropdown_item {
                    list-style: none;
                    width: 100%;
                    line-height: 34px;
                    height: 34px;
                    text-align: left;
                    padding-left: 140px;
                    cursor: pointer;
                    position: relative;
                    &:nth-child(1) {
                        // padding-left: 100px;
                    }
                    .ivu-icon-ios-folder {
                        position: absolute;
                        left: 100px;
                        top: 8px;
                    }
                    .ivu-icon-ios-create {
                        position: absolute;
                        left: 45px;
                        top: 8px;
                        font-size: 24px;
                    }
                    .item_icon {
                        display: block;
                        width: 14px;
                        height: 17px;
                        background: url("../../../assets/images/item_icon.png")
                            no-repeat;
                        background-size: 100% 100%;
                        position: absolute;
                        left: 100px;
                        top: 8px;
                    }

                    .reduce_icon {
                        display: block;
                        width: 24px;
                        height: 24px;
                        line-height: 16px;
                        text-align: center;
                        border: 3px solid #fff;
                        border-radius: 50%;
                        position: absolute;
                        left: 8px;
                        top: 6px;
                    }
                    .save_txt {
                        position: absolute;
                        right: 0;
                        top: 0;
                    }
                    &.active {
                        border-right: 15px solid #47a1ff;
                        background-color: rgba(71, 161, 255, 0.2);
                        border-radius: 3px;
                        color: #36e5ff;
                        .item_icon {
                            background: url("../../../assets/images/item_icon_active.png");
                            background-size: 100% 100%;
                        }
                    }
                    .groupName {
                        height: 34px;
                        width: 100%;
                        background: none;
                        outline: none;
                        outline: none;
                        border: 0px;
                        color: #fff;
                        cursor: pointer;
                        &:focus {
                            cursor: auto;
                        }
                    }
                    .edit {
                        background-color: blue;
                        cursor: auto;
                    }
                }
                .add_item {
                    padding-left: 140px;
                }
            }
        }

        // 火灾告警
        .fire_alarm_data {
            height: 28%;
            margin-top: 5%;
            background: url("../../../assets/images/security_panel_bd.png")
                no-repeat;
            background-size: 100% 100%;
            ul {
                height: 80%;

                li {
                    list-style: none;
                    height: 28px;
                    line-height: 28px;
                    font-size: 14px;
                    color: #fff;
                    padding: 0 50px;
                }
            }
            h4 {
                line-height: 45px;
                font-size: 22px;
                color: #2ed1f6;
                font-weight: 500;
                padding-left: 21px;
                font-weight: 700;
            }
        }
        // 中间视频区
        .video {
            height: 100%;
            background-color: #ccc;
        }
    }
}
</style>
