<template>
    <div class="video_intercom">
        <div class="col">可视对讲视频区</div>
        <div class="col">
            <div class="panel admission_personnel_box">
                <h4>入场人员</h4>
                <admissionPersonnel></admissionPersonnel>
            </div>
            <div class="panel real_time_alarm">
                <h4>
                    实时告警
                </h4>
            </div>
        </div>
        <div class="col">
            <div class="panel">
                <h4>识别失败</h4>
                <recognitionFailed></recognitionFailed>
            </div>
            <div class="panel">
                <h4>
                    人员管理
                </h4>
                <personnelManagement></personnelManagement>
            </div>
        </div>
    </div>
</template>
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import admissionPersonnel from "@/views/Stations/videoIntercom/admissionPersonnel.vue";
import recognitionFailed from "@/views/Stations/videoIntercom/recognitionFailed.vue";
import personnelManagement from "@/views/Stations/videoIntercom/personnelManagement.vue";

@Component({
    components: {
        admissionPersonnel,
        recognitionFailed,
        personnelManagement,
    },
})
export default class VideoIntercom extends Vue {}
</script>
<style lang="less">
.video_intercom {
    display: flex;
    padding: 10px 40px 10px;
    .col {
        flex: 2;

        &:nth-child(1) {
            flex: 6;
            background-color: #1a2855;
            margin-right: 10px;
        }
        .admission_personnel_box {
        }
        .panel {
            color: #ffffff;
            height: 450px;
            background-color: #112143;
            background: url("../../../assets/images/security_panel_bd.png")
                no-repeat;
            background-size: 100% 100%;
            h4 {
                line-height: 60px;
                font-size: 22px;
                color: #2ed1f6;
                font-weight: 500;
                padding-left: 21px;
                font-weight: 700;
            }
            &:nth-child(1),
            &:nth-child(2) {
                margin: 0 10px 10px 0;
            }
            &:nth-child(3),
            &:nth-child(4) {
                margin: 0 10px 10px 0;
            }
        }
    }
}
</style>
