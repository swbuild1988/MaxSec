<template>
    <div class="high_consequence_video">
        高后果视频
        <div class="col"></div>
        <div class="col"></div>
    </div>
</template>
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { ECharts, EChartOption, EChartsSeriesType } from "echarts";
@Component({})
export default class HighConsequenceVideo extends Vue {}
</script>
<style lang="less">
.high_consequence_video {
    display: flex;
    .col {
        flex: 1;
        height: 900px;
    }
}
</style>
