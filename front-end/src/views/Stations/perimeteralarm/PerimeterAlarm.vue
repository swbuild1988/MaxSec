<template>
    <div class="perimeter_alarm">
        <div class="col">
            <div class="search_box">
                <span class="search_icon"></span>
                <Input class="search" placeholder="搜索" style="width: auto" />
            </div>
            <div class="security_area_list">
                <button @click="showSecurityAreaList">
                    防区列表
                    <span class="arrow_down icon"></span>
                    <span class="shield_samll icon"></span>
                </button>
                <ul class="dropdown" v-show="isShowSecurityAreaList">
                    <li
                        v-for="(item, index) in measObjectVals"
                        :key="item.id"
                        class="dropdown_item"
                    >
                        <!-- <span class="shield_big"></span> -->
                        {{ item.name }}
                        <span
                            class="fortification_icon"
                            v-if="item.actived"
                            @click="changeActived(index)"
                        ></span>
                        <span
                            class="forbid_fortification_icon"
                            v-else
                            @click="changeActived(index)"
                        ></span>
                        <span
                            class="time_icon"
                            v-if="allDayMonitor[index].flag"
                        ></span>
                        <span class="no_time_icon" v-else></span>
                    </li>
                </ul>
            </div>
        </div>
        <div class="col">
            <Unity3D></Unity3D>
        </div>
        <div class="col">
            <div class="panel">
                <SecurityAreaStatus
                    :measObjectVals="measObjectVals"
                ></SecurityAreaStatus>
            </div>
            <div class="panel">
                <AlarmStatistics :objects="measObjectVals"></AlarmStatistics>
            </div>
            <div class="panel">
                <RealTimeAlarm ref="realTimeAlarm"></RealTimeAlarm>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import SecurityAreaStatus from "@/views/Stations/perimeteralarm/SecurityAreaStatus.vue";
import AlarmStatistics from "@/views/Stations/perimeteralarm/AlarmStatistics.vue";
import RealTimeAlarm from "@/views/Stations/perimeteralarm/RealTimeAlarm.vue";
import { MeasObjectVo } from "@/types/measobject.interface";
import { MeasObjectVal } from "@/types/measobjectval.interface";
import {
    selectMeasObjectValsByCondition,
    editMeasObjectVal,
} from "@/api/measobject";
import Unity3D from "@/components/view/Unity3d.vue";
import { MessageType } from "@/types/message.interface";

@Component({
    components: {
        SecurityAreaStatus,
        AlarmStatistics,
        RealTimeAlarm,
        Unity3D,
    },
})
export default class IndustrialTV extends Vue {
    isShowSecurityAreaList: Boolean = true;

    stationId: number = 0;

    // 对象类型 -- 周界入侵
    objectTypeId: number = 3;
    // 所有防区
    measObjectVals: MeasObjectVal[] = [];
    // 所有防区是否全天监控
    allDayMonitor: any[] = [
        {
            id: 0,
            flag: true,
        },
        {
            id: 1,
            flag: false,
        },
        {
            id: 2,
            flag: true,
        },
    ];

    mounted() {
        this.stationId = this.$store.state.stationId;
        let typeIndex: number = this.$store.state.objectType.findIndex(
            (a) => a.key == "周界入侵"
        );
        this.objectTypeId =
            typeIndex < 0 ? 0 : this.$store.state.objectType[typeIndex].val;

        this.initData();

        this.startListenMQ();
    }

    initData() {
        let vo: MeasObjectVo = {
            stationId: this.stationId,
            objectType: this.objectTypeId,
        };
        selectMeasObjectValsByCondition(vo).then((res: any) => {
            let { code, data } = res.data;
            if (code == 200) {
                this.measObjectVals = data;
                console.log("measObjectVals", this.measObjectVals);
            }
        });

        // 实时告警刷新
        let realTimeAlarm: any = this.$refs.realTimeAlarm;
        realTimeAlarm.initData();
    }

    //   防区列表显示隐藏
    showSecurityAreaList() {
        this.isShowSecurityAreaList = !this.isShowSecurityAreaList;
    }

    changeActived(index: number) {
        console.log(
            `改变${this.measObjectVals[index].name}的状态为${!this
                .measObjectVals[index].actived}`
        );
        this.measObjectVals[index].actived = !this.measObjectVals[index]
            .actived;
        editMeasObjectVal(this.measObjectVals[index]).then((res: any) => {
            if (res.data.code == 200) {
                this.$Message.success("修改成功!");
                this.initData();
            }
        });
    }

    /** MQ监听 */
    startListenMQ() {
        console.log("周界安防 添加监听器到MQ");
        this.TransferStation.addListener("PermeterAlarm", this.callback);
    }

    /** MQ停止监听 */
    stopListenMQ() {
        console.log("周界安防 移除监听器");
        this.TransferStation.deleteListener("PermeterAlarm");
    }

    callback(respond: any) {
        let message: MessageType = JSON.parse(respond);
        console.log("周界安防 接收到消息：", message);
        switch (message.type) {
            case "IntrusionUpgradeAlarm":
            case "IntrusionAlarm":
                // 周界告警则刷新页面数据
                this.initData();
                break;

            default:
                // 其他告警不处理
                break;
        }
    }

    beforeDestroy() {
        this.stopListenMQ();
    }
}
</script>

<style lang="less">
.perimeter_alarm {
    display: flex;
    height: 100%;
    padding: 10px 40px 10px;

    .col {
        flex: 2;
        // height: 720px;
        height: 80%;

        &:nth-child(1) {
            background-color: #001b55;
            padding: 10px 0;
        }

        &:nth-child(2) {
            flex: 6;
        }

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
                left: 38px;
                top: 9px;
            }

            .search {
                width: 80%;
                height: 48px;
                margin-left: 70px;

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
                }
            }
        }

        .security_area_list {
            // 侧边防区列表
            button {
                width: 100%;
                line-height: 68px;
                background-color: transparent;
                color: #fff;
                font-size: 22px;
                border: none;
                cursor: pointer;
                text-align: left;
                padding-left: 78px;
                outline: none;
                position: relative;

                .icon {
                    display: block;
                    width: 16px;
                    height: 10px;
                    position: absolute;
                    top: 50%;
                    transform: translateY(-50%);
                }

                .arrow_down {
                    background: url("../../../assets/images/down_arrow.png")
                        no-repeat;
                    background-size: 100% 100%;
                    left: 10px;
                }

                .shield_samll {
                    width: 30px;
                    height: 30px;
                    background: url("../../../assets/images/shield_samll.png")
                        no-repeat;
                    background-size: 100% 100%;
                    left: 38px;
                }
            }

            .dropdown {
                width: 100%;
                color: #fff;

                .dropdown_item {
                    list-style: none;
                    width: 100%;
                    line-height: 68px;
                    text-align: left;
                    font-size: 22px;
                    padding-left: 132px;
                    cursor: pointer;
                    position: relative;

                    .shield_big {
                        display: block;
                        width: 32px;
                        height: 34px;
                        background: url("../../../assets/images/shield_big.png")
                            no-repeat;
                        background-size: 100% 100%;
                        position: absolute;
                        top: 50%;
                        transform: translateY(-50%);
                        left: 82px;
                    }

                    .fortification_icon {
                        display: block;
                        width: 26px;
                        height: 28px;
                        background: url("../../../assets/images/fortification_icon.png")
                            no-repeat;
                        background-size: 100% 100%;
                        position: absolute;
                        right: 75px;
                        top: 50%;
                        transform: translateY(-50%);
                    }

                    .forbid_fortification_icon {
                        display: block;
                        width: 26px;
                        height: 28px;
                        background: url("../../../assets/images/no_fortification_icon.png")
                            no-repeat;
                        background-size: 100% 100%;
                        position: absolute;
                        right: 75px;
                        top: 50%;
                        transform: translateY(-50%);
                    }

                    .time_icon {
                        display: block;
                        width: 26px;
                        height: 26px;
                        background: url("../../../assets/images/time_icon.png")
                            no-repeat;
                        background-size: 100% 100%;
                        position: absolute;
                        right: 40px;
                        top: 50%;
                        transform: translateY(-50%);
                    }

                    .no_time_icon {
                        display: block;
                        width: 26px;
                        height: 26px;
                        background: url("../../../assets/images/no_time_icon.png")
                            no-repeat;
                        background-size: 100% 100%;
                        position: absolute;
                        right: 40px;
                        top: 50%;
                        transform: translateY(-50%);
                    }

                    &.active {
                        border-right: 15px solid #49aae0;
                        background-color: #113379;
                        border-radius: 3px;

                        .fortification_icon {
                        }

                        .forbid_fortification_icon {
                            background: url("../../../assets/images/no_fortification_icon_active.png")
                                no-repeat;
                            background-size: 100% 100%;
                        }

                        .time_icon {
                        }

                        .no_time_icon {
                            background: url("../../../assets/images/no_time_icon_active.png")
                                no-repeat;
                            background-size: 100% 100%;
                        }
                    }
                }
            }
        }

        // 中间模型区
        .model {
            height: 100%;
            background-color: rgba(17, 29, 63, 1);
            margin: 10px;
        }

        .panel {
            width: 350px;
            // height: 235px;
            height: 33%;
            margin: 0 auto 10px;
            background-color: rgba(17, 29, 63, 1);
            background: url("../../../assets/images/security_panel_bd.png")
                no-repeat;
            background-size: 100% 100%;
        }
    }
}
</style>
