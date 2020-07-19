<template>
    <div class="production_area_center">
        <h4>{{ videoName }}</h4>
        <div id="OCXOverviewBottomBody">
            <div class="smallocxdiv" id="NetPlayOCX3">
                <object
                    classid="CLSID:CAFCF48D-8E34-4490-8154-026191D73924"
                    codebase="../../../NetVideoActiveX23.cab#version=2,3,23,9"
                    standby="Waiting..."
                    id="HIKOBJECT3"
                    width="100%"
                    height="100%"
                    name="HIKOBJECT3"
                ></object>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { getVideoDtos } from "@/api/video";

@Component({})
export default class ProductionAreaCenter extends Vue {
    videoName: String = "";
    m_bDVRControl: any = "";
    m_iLoginUserId: Number = -1; //注册设备用户ID
    m_iPlay: Number = 0; //当前是否正在预览
    m_iNowChanNo: Number = 0; //当前通道号
    m_iProtocolType: Number = 0; //协议类型，0 – TCP， 1 - UDP
    m_iStreamType: Number = 0; //码流类型，0 表示主码流， 1 表示子码流
    videoServer: any = {};
    videoListArr: Array<any> = []; //视频列表

    mounted() {
        this.getVideosInfo();
        this.m_bDVRControl = document.getElementById("HIKOBJECT3");
        this.ChangeStatus();
    }

    destroyed() {
        if (!this.m_bDVRControl.StopRealPlay()) {
            return;
        } else {
            this.m_iPlay = 0;
        }
    }

    // 获取视频信息
    getVideosInfo() {
        getVideoDtos().then((res: any) => {
            let { code, data } = res.data;
            if (code != 200) {
                return;
            } else {
                this.videoListArr = data;
                this.videoName = this.videoListArr[0].name;
                if (!this.videoListArr) {
                    return;
                }
                // 开始预览
                this.PreviewStart(this.videoListArr[0]);
            }
        });
    }

    ChangeStatus() {
        document.getElementById("NetPlayOCX3").style.border = "1px solid #00F";
    }

    // 开始预览
    PreviewStart(videoItem) {
        if (!videoItem.videoServer) {
            return;
        }
        this.videoServer = videoItem.videoServer;
        this.m_iNowChanNo = videoItem.channelNo;
        var { ip, port, username, password } = this.videoServer;
        this.m_iLoginUserId = this.m_bDVRControl.Login(
            // ip,
            // port,
            // username,
            // password
            "172.16.2.101",
            "8000",
            "admin",
            "bw123456"
        );
        if (this.m_iLoginUserId == -1) {
            console.log("注册失败！");
        } else {
            console.log("注册成功！");
        }
        if (this.m_iNowChanNo > -1) {
            if (this.m_iPlay == 1) {
                this.m_bDVRControl.StopRealPlay();
            }
            this.m_bDVRControl.SetPlayWndType(0);
            var bRet = this.m_bDVRControl.StartRealPlay(
                this.m_iNowChanNo,
                this.m_iProtocolType,
                this.m_iStreamType
            );
            var newNo = +this.m_iNowChanNo + 1;
            if (bRet) {
                console.log("预览通道" + newNo + "成功！");
                this.m_iPlay = 1;
            } else {
                console.log("预览通道" + newNo + "失败！");
            }
        } else {
            console.log("请选择通道号！");
        }
    }
}
</script>

<style lang="less">
.production_area_center {
    height: 100%;

    h4 {
        line-height: 40px;
        font-size: 22px;
        color: #2ed1f6;
        font-weight: 500;
        padding-left: 21px;
        font-weight: 700;
    }

    #OCXOverviewBottomBody {
        width: 90%;
        height: 80%;
        margin: 0 auto;

        .smallocxdiv {
            float: left;
            width: 100%;
            height: 100%;
            z-index: 99;
        }

        #HIKOBJECT1 {
            width: 100%;
            height: 100%;
        }
    }
}
</style>
