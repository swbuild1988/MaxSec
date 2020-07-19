<template>
  <div class="event_statistics">
    <h4>
      <span class="icon"></span>
      事件统计
    </h4>
    <div class="event_statistics_content">
      <div class="content_item total">
        <p>66个<br />总事件</p>
      </div>
      <div class="content_item handled">
        <p>13个<br />已处理</p>
      </div>
      <div class="content_item unprocessed">
        <p>36个<br />未处理</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
@Component({})
export default class EventStatistics extends Vue {}
</script>

<style lang="less">
.event_statistics {
  height: 100%;
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
      background: url("../../../assets/images/event.png") no-repeat;
      background-size: 100% 100%;
    }
  }
  .event_statistics_content {
    height: 230px;
    display: flex;
    justify-content: space-around;
    .content_item {
      margin-top: 20px;
      height: 120px;
      width: 120px;
      position: relative;
      p {
        text-align: center;
        font-size: 12px;
        color: #fff;
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
      }
    }
    .total {
      background: url("../../../assets/images/total.png") no-repeat center;
      background-size: cover;
    }
    .handled {
      background: url("../../../assets/images/handled.png") no-repeat center;
      background-size: cover;
    }
    .unprocessed {
      background: url("../../../assets/images/unprocessed.png") no-repeat center;
      background-size: cover;
    }
  }
}
</style>
