<template>
    <div class="Fire_alarm_video">
        <div id="OCXBody">
            <div class="smallocxdiv" id="NetPlayOCX1">
                <object
                    classid="CLSID:CAFCF48D-8E34-4490-8154-026191D73924"
                    codebase="/NetVideoActiveX23.cab"
                    standby="Waiting..."
                    id="HIKOBJECT1"
                    width="100%"
                    height="100%"
                    name="HIKOBJECT1"
                ></object>
            </div>
            <div class="smallocxdiv" id="NetPlayOCX2">
                <object
                    classid="CLSID:CAFCF48D-8E34-4490-8154-026191D73924"
                    standby="Waiting..."
                    id="HIKOBJECT2"
                    width="100%"
                    height="100%"
                    name="HIKOBJECT2"
                ></object>
            </div>
            <div class="smallocxdiv" id="NetPlayOCX3">
                <object
                    classid="CLSID:CAFCF48D-8E34-4490-8154-026191D73924"
                    standby="Waiting..."
                    id="HIKOBJECT3"
                    width="100%"
                    height="100%"
                    name="HIKOBJECT3"
                ></object>
            </div>
            <div class="smallocxdiv" id="NetPlayOCX4">
                <object
                    classid="CLSID:CAFCF48D-8E34-4490-8154-026191D73924"
                    standby="Waiting..."
                    id="HIKOBJECT4"
                    width="100%"
                    height="100%"
                    name="HIKOBJECT4"
                ></object>
            </div>
            <div style="float:right; display:none;"></div>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component({})
export default class FireAlarmVideo extends Vue {
    m_bDVRControl: any;

    mounted() {
        this.m_bDVRControl = document.getElementById("HIKOBJECT1");
        this.ArrangeWindowChildren(4);
        // this.preview();
    }

    // 重置窗口大小
    ArrangeWindowChildren(x) {
        var iMaxWidth = document.getElementById("OCXBody").offsetWidth;
        var iMaxHeight = document.getElementById("OCXBody").offsetHeight;

        for (var j = 1; j <= x; j++) {
            if (x == 1) {
                document.getElementById("NetPlayOCX" + j).style.width = "100%";
                document.getElementById("NetPlayOCX" + j).style.height = "100%";
            } else if (x == 4) {
                console.log("ArrangeWindowChildren", x);
                document.getElementById("NetPlayOCX" + j).style.width = "49%";
                document.getElementById("NetPlayOCX" + j).style.height = "49%";
            } else {
                document.getElementById("NetPlayOCX" + j).style.width = "33%";
                document.getElementById("NetPlayOCX" + j).style.height = "33%";
            }
        }
        if (x == 1) {
        } else if (x == 4) {
        } else {
            //
        }
    }

    // 预览
    preview() {
        this.m_bDVRControl.StartRealPlay(0, 0, 0);
    }
}
</script>
<style lang="less">
.Fire_alarm_video {
    height: 100%;
    #OCXBody {
        width: 100%;
        height: 100%;
        margin: 0 auto;

        .smallocxdiv {
            float: left;
            display: ;
            width: 33%;
            height: 33%;
            z-index: 1000;
            border: 1px solid #eee;
        }
    }
}
</style>
