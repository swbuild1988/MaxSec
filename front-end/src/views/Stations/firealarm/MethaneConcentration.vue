<template>
    <div class="methane_concentration_chart">
        <!-- 甲烷浓度： -->
        <div class="concentration methane">
            <span class="text item_name">甲烷浓度：</span>
            <div class="chart_item">
                <span class="progress"></span>
            </div>
            <span class="text item_value">
                4 ppm·m
            </span>
        </div>
        <!-- 光强强度: -->
        <div class="concentration light">
            <span class="text item_name">光强强度：</span>
            <div class="chart_item">
                <span class="progress"></span>
            </div>
            <span class="text item_value">
                500
            </span>
        </div>
        <!-- 水平位置: -->
        <div class="concentration horizontal">
            <span class="text item_name">水平位置：</span>
            <div class="chart_item">
                <span class="progress">
                    <span class="progress_active"></span>
                </span>
            </div>
            <span class="text item_value">
                ﹢213.5
            </span>
        </div>
        <!-- 垂直位置: -->
        <div class="concentration vertical">
            <span class="text item_name">垂直位置：</span>
            <div class="chart_item">
                <span class="progress">
                    <span class="progress_active"></span>
                </span>
            </div>
            <span class="text item_value">
                ﹣7.3
            </span>
        </div>
    </div>
</template>
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { ECharts, EChartOption, EChartsSeriesType } from "echarts";

@Component({
    components: {},
})
export default class FireAlarm extends Vue {}
</script>
<style lang="less">
.methane_concentration_chart {
    height: 80%;
    padding: 5px;
    color: #fff;
    .concentration {
        height: 25%;
        overflow: hidden;
        line-height: 100%;

        .text {
            font-size: 14px;
            color: #ffff;
            float: left;
        }
        .chart_item {
            width: 50%;
            height: 15px;
            background-color: #184565;
            float: left;
            margin-right: 5px;
            position: relative;
            top: 25%;
            transform: skewX(140deg);
            .progress {
                display: block;
                width: 20%;
                height: 100%;
                background-color: #36e5ff;
                position: absolute;
                left: 0;
                top: 0;
            }
        }
    }
    .light {
        .chart_item {
            background-color: #413a45;
            .progress {
                background-color: #ffae5c;
            }
        }
    }
    .horizontal {
        .chart_item {
            background-color: transparent;
            border: 1px solid #59493f;
            position: relative;
            .progress {
                width: 50%;
                background-color: #59493f;
                position: absolute;
                right: 0;
                top: 0;
                .progress_active {
                    display: block;
                    width: 30%;
                    height: 100%;
                    position: absolute;
                    left: 0;
                    top: 0;
                    background-color: #b28850;
                }
            }
        }
    }
    .vertical {
        .chart_item {
            background-color: transparent;
            border: 1px solid #59493f;

            .progress {
                width: 50%;
                background-color: #59493f;
                left: 0;
                top: 0;
                .progress_active {
                    display: block;
                    width: 20%;
                    height: 100%;
                    position: absolute;
                    right: 0;
                    top: 0;
                    background-color: #b28850;
                }
            }
        }
    }
}
</style>
