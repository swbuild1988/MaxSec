<template>
    <div class="real_time_alarm">
        <h4>
            <span class="icon"></span>
            实时告警
        </h4>
        <ScrollBoard
            :config="config"
            style="width:100%;height:80%;"
        ></ScrollBoard>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { Alarm, AlarmVo } from "@/types/alarm.interface";
import { getPageAlarmsByCondition } from "@/api/alarm";
import { dateFormat } from "@/util/common";
import ScrollBoard from "@/components/scrollBoard/scrollBoard.vue";
import { getDate, getTime } from "@/api/date";

@Component({
    components: {
        ScrollBoard,
    },
})
export default class RealTimeAlarm extends Vue {
    list: Alarm[] = [];
    config: any = {
        data: [],
    };

    mounted() {
        this.initData();
    }

    initData() {
        let vo: AlarmVo = {
            pageNum: 1,
            pageSize: 50,
        };

        getPageAlarmsByCondition(vo).then((res: any) => {
            if (res.data.code != 200) return;

            this.list = res.data.data.content;
            var dataRes = getDate("-") + getTime();

            this.config = {
                data: this.list.map((item, index) => {
                    return [dataRes, item.stationName, item.alarmType.name];
                }),
                align: ["center", "center", "center"],
                rowNum: 6,
            };
        });
    }
}
</script>

<style lang="less" scoped>
.real_time_alarm {
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
            background: url("../../../assets/images/alarm.png") no-repeat;
            background-size: 100% 100%;
        }
    }
}
</style>
