<template>
    <div class="Unity3D">
        <Unity3DWebPlayer
            v-if="brower == 'IE'"
            :modelName="modelName"
            ref="child"
        ></Unity3DWebPlayer>
        <Unity3DWebGL v-else :modelName="modelName" ref="child"></Unity3DWebGL>
    </div>
</template>
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { Station } from "@/types/station.interface";
import { getStation } from "@/api/station";
import Unity3DWebGL from "@/components/Unity3d/Unity3D_WebGL.vue";
import Unity3DWebPlayer from "@/components/Unity3d/Unity3D_WebPlayer.vue";

@Component({
    components: {
        Unity3DWebGL,
        Unity3DWebPlayer,
    },
})
export default class Unity3d extends Vue {
    stationId: number = 0;
    station: Station;
    modelName: String = "";

    /** 当前浏览器 */
    brower: string = "";

    created() {
        // 判断当前的浏览器
        this.brower = this.currentBrower();
        console.log(`当前浏览器为:${this.brower}`);

        // 获取当前的场站
        this.stationId = this.$store.state.stationId;
        this.init();
    }

    currentBrower() {
        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
        var isOpera = userAgent.indexOf("Opera") > -1;
        if (isOpera) {
            //判断是否Opera浏览器
            return "Opera";
        }
        if (userAgent.indexOf("Firefox") > -1) {
            //判断是否Firefox浏览器
            return "FF";
        }
        if (userAgent.indexOf("Chrome") > -1) {
            //判断是否Chrome浏览器
            return "Chrome";
        }
        if (userAgent.indexOf("Safari") > -1) {
            //判断是否Safari浏览器
            return "Safari";
        }
        if (
            userAgent.indexOf("compatible") > -1 &&
            userAgent.indexOf("MSIE") > -1 &&
            !isOpera
        ) {
            //判断是否IE浏览器
            return "IE";
        }
        return "";
    }

    init() {
        getStation(this.stationId).then((res) => {
            if (res.data.code == 200) {
                this.station = res.data.data;
                this.modelName = this.station.name;
            }
        });
    }

    refreshAlarmStatus(newAlarm: any) {
        const child: any = this.$refs.child;
        child.refreshAlarmStatus(newAlarm);
    }

    refreshData(id: number, cv: number) {
        const child: any = this.$refs.child;
        child.refreshData(id, cv);
    }
}
</script>
<style lang="less" scoped>
.Unity3D {
    width: 100%;
    height: 100%;
}
</style>
