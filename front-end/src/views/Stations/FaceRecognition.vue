<template>
    <div class="face_recognition">
        <div class="col">人脸识别视频区</div>
        <div class="col">
            <div class="panel lawfu_person">
                <h4>
                    合法人员
                </h4>
            </div>
            <div class="panel real_time_alarm">
                <h4>实时告警</h4>
            </div>
        </div>
        <div class="col">
            <div class="panel">
                <h4>非法闯入</h4>
            </div>
            <div class="panel">
                <h4>人员管理</h4>
                <table class="people_table">
                    <tr>
                        <td>部门</td>
                        <td>工号</td>
                        <td>姓名</td>
                        <td>合法区域</td>
                    </tr>
                    <tr>
                        <td>运检部</td>
                        <td>001</td>
                        <td>王二小</td>
                        <td>生活区</td>
                    </tr>
                    <tr>
                        <td>运检部</td>
                        <td>001</td>
                        <td>王二小</td>
                        <td>生活区</td>
                    </tr>
                    <tr>
                        <td>运检部</td>
                        <td>001</td>
                        <td>王二小</td>
                        <td>生活区</td>
                    </tr>
                    <tr>
                        <td>运检部</td>
                        <td>001</td>
                        <td>王二小</td>
                        <td>生活区</td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</template>
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component({})
export default class FaceRecognition extends Vue {}
</script>
<style lang="less">
.face_recognition {
    display: flex;
    padding: 10px 40px 10px;
    .col {
        flex: 2;
        h4 {
            line-height: 60px;
            font-size: 22px;
            color: #2ed1f6;
            font-weight: 500;
            padding-left: 21px;
            font-weight: 700;
        }
        &:nth-child(1) {
            flex: 6;
            background-color: #1a2855;
            margin-right: 10px;
        }
        .panel {
            color: #ffffff;
            height: 409px;
            background-color: #1a2855;
            background: url("../../assets/images/security_panel_bd.png")
                no-repeat;
            background-size: 100% 100%;
            &:nth-child(1),
            &:nth-child(2) {
                margin: 0 10px 10px 0;
            }
            &:nth-child(3),
            &:nth-child(4) {
                margin: 0 10px 10px 0;
            }
            .people_table {
                width: 100%;
                // height: 100%;
                tr {
                    td {
                        text-align: center;
                        font-size: 14px;
                        line-height: 30px;
                    }
                }
            }
        }
    }
}
</style>
