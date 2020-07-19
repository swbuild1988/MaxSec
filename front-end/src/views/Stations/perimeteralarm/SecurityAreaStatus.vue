<template>
    <div class="security_area_status">
        <h4>
            防区状态
        </h4>
        <table class="status_table">
            <thead>
                <td>防区</td>
                <td>正常</td>
                <td>断纤</td>
                <td>告警</td>
            </thead>

            <tr v-for="item in measObjectVals" :key="item.id">
                <td>{{ item.name }}</td>
                <td>
                    <span
                        class="circle status_normal_circle"
                        v-if="item.lastValue == 0"
                    ></span>
                    <span class="circle status_circle" v-else></span>
                </td>
                <td>
                    <span
                        class="circle status_break_circle"
                        v-if="item.lastValue == 1"
                    ></span>
                    <span class="circle status_circle" v-else></span>
                </td>
                <td>
                    <span
                        class="circle status_alarm_circle"
                        v-if="item.lastValue == 2"
                    ></span>
                    <span class="circle status_circle" v-else></span>
                </td>
            </tr>
        </table>
    </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from "vue-property-decorator";
import { MeasObjectVal } from "@/types/measobjectval.interface";
@Component({})
export default class SecurityAreaStatus extends Vue {
    @Prop({
        required: true,
        type: Array,
        default: () => [],
    })
    measObjectVals: MeasObjectVal[];
}
</script>

<style lang="less">
.security_area_status {
    height: 100%;

    h4 {
        line-height: 40px;
        font-size: 22px;
        color: #2ed1f6;
        font-weight: 500;
        padding-left: 21px;
        font-weight: 700;
    }

    .status_table {
        width: 100%;
        height: 200px;

        thead {
            font-size: 18px;
            line-height: 34px;
            color: #fff;
            text-align: center;
        }

        tr {
            line-height: 34px;
            font-size: 14px;
            color: #fff;

            td {
                text-align: center;

                .circle {
                    display: block;
                    width: 25px;
                    height: 25px;
                    border-radius: 50%;
                    margin: 0 auto;
                }

                .status_circle {
                    background-color: #a4a4a4;
                }

                .status_normal_circle {
                    background: linear-gradient(
                        0deg,
                        rgba(0, 180, 255, 1),
                        rgba(38, 69, 219, 1)
                    );
                }

                .status_break_circle {
                    background: linear-gradient(
                        0deg,
                        rgba(255, 210, 0, 1),
                        rgba(180, 246, 40, 1)
                    );
                }

                .status_alarm_circle {
                    background: linear-gradient(
                        0deg,
                        rgba(249, 42, 1, 1),
                        rgba(249, 1, 1, 1),
                        rgba(227, 141, 45, 1)
                    );
                }
            }
        }
    }
}
</style>
