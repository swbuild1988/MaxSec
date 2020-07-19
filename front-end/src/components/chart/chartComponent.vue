<template>
  <div class="chartComponent-wrap">
    <PieChartNormal
      v-bind="bindData"
      v-if="bindData.type === 400"
    ></PieChartNormal>
    <GaugeChartNormal
      v-bind="bindData"
      v-else-if="bindData.type === 500"
    ></GaugeChartNormal>
    <div v-else>
      敬请期待！！！
    </div>
  </div>
</template>

<script lang="ts">
import GaugeChartNormal from "@/components/chart/gaugeChart_normal/gaugeChart_normal.vue";
import PieChartNormal from "@/components/chart/pieChart_normal/pieChart_normal.vue";

import { ChartBindData, ChartType } from "@/types/chart.interface";

import { Component, Vue, Prop } from "vue-property-decorator";

@Component({
  components: {
    GaugeChartNormal,
    PieChartNormal,
  },
})
export default class About extends Vue {
  @Prop({
    required: true,
  })
  bindData!: any;
}
</script>

<style lang="less">
.chartComponent-wrap {
  height: 100%;
  width: 100%;

  canvas {
    background: transparent;
  }
}
</style>
