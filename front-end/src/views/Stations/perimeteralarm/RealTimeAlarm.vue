<template>
    <div class="real_time_alarm">
        <h4>
            实时告警
        </h4>
        <div class="scroll">
            <ScrollBoard
                :config="config"
                style="width:100%;height:80%;"
            ></ScrollBoard>
        </div>
    </div>
</template>
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { Alarm, AlarmVo } from "@/types/alarm.interface";
import { getPageAlarmsByCondition } from "@/api/alarm";
import ScrollBoard from "@/components/scrollBoard/scrollBoard.vue";
import { getDate, getTime } from "@/api/date";

@Component({
    components: {
        ScrollBoard,
    },
})
export default class RealTimeAlarm extends Vue {
    stationId: number = 0;
    list: Alarm[] = [];
    // 对象类型 -- 周界入侵
    objectTypeId: number = 3;
    config: any = {
        data: [],
    };

    mounted() {
        this.stationId = this.$store.state.stationId;

        this.initData();
    }

    initData() {
        let vo: AlarmVo = {
            pageNum: 1,
            pageSize: 20,
            type: this.objectTypeId,
            stationId: this.stationId,
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
<style lang="less">
.real_time_alarm {
    height: 100%;
    .scroll {
        width: 100%;
        // height: 220px;
        height: 100%;
    }

    h4 {
        line-height: 40px;
        font-size: 22px;
        color: #2ed1f6;
        font-weight: 500;
        padding-left: 21px;
        font-weight: 700;
    }

    .dv-scroll-board {
        width: 100%;
        height: 230px;

        .rows {
            .row-item {
                background-color: transparent !important;
            }
        }
    }
}
</style>
