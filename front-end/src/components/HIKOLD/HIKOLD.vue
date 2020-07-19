<template>
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
        <div class="smallocxdiv" id="NetPlayOCX5">
            <object
                classid="CLSID:CAFCF48D-8E34-4490-8154-026191D73924"
                standby="Waiting..."
                id="HIKOBJECT5"
                width="100%"
                height="100%"
                name="HIKOBJECT5"
            ></object>
        </div>
        <div class="smallocxdiv" id="NetPlayOCX6">
            <object
                classid="CLSID:CAFCF48D-8E34-4490-8154-026191D73924"
                standby="Waiting..."
                id="HIKOBJECT6"
                width="100%"
                height="100%"
                name="HIKOBJECT6"
            ></object>
        </div>
        <div class="smallocxdiv" id="NetPlayOCX7">
            <object
                classid="CLSID:CAFCF48D-8E34-4490-8154-026191D73924"
                standby="Waiting..."
                id="HIKOBJECT7"
                width="100%"
                height="100%"
                name="HIKOBJECT7"
            ></object>
        </div>
        <div class="smallocxdiv" id="NetPlayOCX8">
            <object
                classid="CLSID:CAFCF48D-8E34-4490-8154-026191D73924"
                standby="Waiting..."
                id="HIKOBJECT8"
                width="100%"
                height="100%"
                name="HIKOBJECT8"
            ></object>
        </div>
        <div class="smallocxdiv" id="NetPlayOCX9">
            <object
                classid="CLSID:CAFCF48D-8E34-4490-8154-026191D73924"
                standby="Waiting..."
                id="HIKOBJECT9"
                width="100%"
                height="100%"
                name="HIKOBJECT9"
            ></object>
        </div>
        <div style="float:right; display:none;"></div>
    </div>
</template>
<script lang="ts">
import { Component, Vue, Prop } from "vue-property-decorator";
import { log } from "util";
import { getDate, getTime } from "@/api/date";
@Component({})
export default class HIKOLD extends Vue {
    m_bDVRControl: any = "";
    m_iLoginUserId: Number = -1; //注册设备用户ID
    m_iChannelNum: Number = -1; //模拟通道总数
    m_iNowChanNo: Number = 0; //当前通道号
    m_iPlay: Number = 0; //当前是否正在预览
    m_iPlayBlack: Number = 0; //当前是否正在回放
    m_iProtocolType: Number = 0; //协议类型，0 – TCP， 1 - UDP
    m_iStreamType: Number = 0; //码流类型，0 表示主码流， 1 表示子码流
    m_iAutoPTZ: Number = 0; //当前云台是否正在自转
    m_iPTZSpeed: Number = 4; //云台速度
    videoServer: any = {};
    created() {}
    mounted() {
        this.m_bDVRControl = document.getElementById("HIKOBJECT1");
        this.ChangeStatus(1);
        this.ArrangeWindowChildren(9);
        this.bindClick();
    }
    destroyed() {
        if (!this.m_bDVRControl.StopRealPlay()) {
            return;
        } else {
            this.m_iPlay = 0;
        }
    }
    // 聚焦窗口
    ChangeStatus(iWindowNum) {
        this.m_bDVRControl = document.getElementById("HIKOBJECT" + iWindowNum);
        for (var i = 1; i <= 9; i++) {
            if (i == iWindowNum) {
                document.getElementById("NetPlayOCX" + i).style.border =
                    "1px solid #00F";
            } else {
                document.getElementById("NetPlayOCX" + i).style.border =
                    "1px solid #EBEBEB";
            }
        }
    }
    // 重置窗口大小
    ArrangeWindowChildren(x) {
        var iMaxWidth = document.getElementById("OCXBody").offsetWidth;
        var iMaxHeight = document.getElementById("OCXBody").offsetHeight;
        for (var i = 1; i <= 9; i++) {
            if (i <= x) {
                document.getElementById("NetPlayOCX" + i).style.display = "";
            } else {
                document.getElementById("NetPlayOCX" + i).style.display =
                    "none";
            }
        }
        for (var j = 1; j <= x; j++) {
            if (x == 1) {
                document.getElementById("NetPlayOCX" + j).style.width = "100%";
                document.getElementById("NetPlayOCX" + j).style.height = "100%";
            } else if (x == 4) {
                document.getElementById("NetPlayOCX" + j).style.width = "50%";
                document.getElementById("NetPlayOCX" + j).style.height = "50%";
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
    // 获取设备名称
    getDevName() {
        {
            var szDecName = this.m_bDVRControl.GetServerName();
            //szDecName = szDecName.replace(/\s/g,"&nbsp;");
            if (szDecName == "") {
                console.log("获取名称失败！");

                szDecName = "Embedded Net DVR";
            } else {
                console.log("获取名称成功！");
            }
        }
    }
    // 获取通道
    getDevChan() {
        {
            var szServerInfo = this.m_bDVRControl.GetServerInfo();
            var xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
            xmlDoc.async = "false";
            xmlDoc.loadXML(szServerInfo);
            this.m_iChannelNum = parseInt(
                xmlDoc.documentElement.childNodes[0].childNodes[0].nodeValue
            );
            if (this.m_iChannelNum < 1) {
                console.log("获取通道失败！");
            } else {
                console.log("获取通道成功！", this.m_iChannelNum);
            }
        }
    }
    // 点击事件
    bindClick() {
        var that = this;
        for (let i = 1; i <= 9; i++) {
            var ocxControl: any = document.getElementById("HIKOBJECT" + i);
            switch (i) {
                case 1:
                    ocxControl.addEvent("SelectWindow", function() {
                        that.ChangeStatus(1);
                    });
                    break;
                case 2:
                    ocxControl.addEvent("SelectWindow", function() {
                        that.ChangeStatus(2);
                    });
                    break;
                case 3:
                    ocxControl.addEvent("SelectWindow", function() {
                        that.ChangeStatus(3);
                    });
                    break;
                case 4:
                    ocxControl.addEvent("SelectWindow", function() {
                        that.ChangeStatus(4);
                    });
                    break;
                case 5:
                    ocxControl.addEvent("SelectWindow", function() {
                        that.ChangeStatus(5);
                    });
                    break;
                case 6:
                    ocxControl.addEvent("SelectWindow", function() {
                        that.ChangeStatus(6);
                    });
                    break;
                case 7:
                    ocxControl.addEvent("SelectWindow", function() {
                        that.ChangeStatus(7);
                    });
                    break;
                case 8:
                    ocxControl.addEvent("SelectWindow", function() {
                        that.ChangeStatus(8);
                    });
                    break;
                case 9:
                    ocxControl.addEvent("SelectWindow", function() {
                        that.ChangeStatus(9);
                    });
                    break;
            }
        }
    }
    // 开始预览
    PreviewStart(videoItem) {
        if (!videoItem.videoServer) {
            return;
        }
        this.m_bDVRControl.StopPlayBack();
        this.videoServer = videoItem.videoServer;
        this.m_iNowChanNo = videoItem.channelNo;
        var { ip, port, username, password } = this.videoServer;
        // var userId = this.$store.state[this.videoServer.id];
        // if (userId != -1 && userId != undefined) {
        //     this.m_iLoginUserId = userId;
        //     console.log("不用登录");
        // } else {
        this.m_iLoginUserId = this.m_bDVRControl.Login(
            // ip,
            // port,
            // username,
            // password
            // "192.168.12.100",
            "172.16.2.101",
            "8000",
            "admin",
            // "12345"
            "bw123456"
        );
        if (this.m_iLoginUserId == -1) {
            console.log("注册失败！");
        } else {
            console.log("注册成功！");
            this.$store.state[this.videoServer.id] = this.m_iLoginUserId;
        }
        // }
        // for (var i = 2; i <= 9; i++) {
        //     var HIKOBJECTi: any = document.getElementById("HIKOBJECT" + i);
        //     HIKOBJECTi.SetUserID(this.m_iLoginUserId);
        // }
        // this.getDevName();
        // this.getDevChan();
        if (this.m_iNowChanNo > -1) {
            if (this.m_iPlay == 1) {
                this.m_bDVRControl.StopRealPlay();
            }
            this.m_bDVRControl.SetPlayWndType(0);
            console.log("通道号", this.m_iNowChanNo);
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

    // 云台旋转
    // 向上、下、左、右
    ptzTurnChildren(direction) {
        if (this.m_iPlay == 1) {
            if (this.m_iAutoPTZ == 1) {
                this.m_bDVRControl.PTZCtrlStop(10, this.m_iPTZSpeed);
                this.m_iAutoPTZ = 0;
            }
            if (this.m_bDVRControl.PTZCtrlStart(direction, this.m_iPTZSpeed)) {
                switch (direction) {
                    case 0:
                        console.log("PTZ上成功！");
                        break;
                    case 1:
                        console.log("PTZ下成功！");
                        break;
                    case 2:
                        console.log("PTZ左成功！");
                        break;
                    case 3:
                        console.log("PTZ右成功！");
                        break;
                    case 4:
                        console.log("焦距拉近成功！");
                        break;
                    case 5:
                        console.log("焦距拉远成功！");
                        break;
                }
            } else {
                console.log("PTZ失败！");
            }
        } else {
            console.log("请先预览！", this.m_iPlay);
        }
    }
    // 停止
    ptzStopChildren() {
        if (this.m_iPlay == 1) {
            if (this.m_bDVRControl.PTZCtrlStop(10, this.m_iPTZSpeed)) {
                this.m_iAutoPTZ = 0;
                console.log("停止PTZ成功！");
            } else {
                console.log("停止PTZ失败！");
            }
        }
    }
    // 设置
    setPresetChildren(iPreset) {
        if (this.m_iPlay == 1) {
            var bRet = this.m_bDVRControl.PTZCtrlSetPreset(iPreset);
            if (bRet) {
                console.log("设置预置点成功！");
            } else {
                console.log("设置预置点失败！");
            }
        } else {
            console.log("请先预览！");
        }
    }
    // 跳转
    goPresetChildren(iPreset) {
        if (this.m_iPlay == 1) {
            var bRet = this.m_bDVRControl.PTZCtrlGotoPreset(iPreset);
            if (bRet) {
                console.log("调用预置点成功！");
            } else {
                console.log("调用预置点成功！");
            }
        } else {
            console.log("请先预览！");
        }
    }

    // 获取当前日期

    // 视频回放
    // 开始
    startPlayChildren(date, time) {
        let lpStartTime = date + " " + time;
        // 结束时间默认当前时间
        let lpStopTime = getDate("-") + " " + getTime();
        console.log(lpStopTime, "lpStopTime");
        if (this.m_iNowChanNo > -1) {
            if (this.m_iPlay == 1) {
                this.m_bDVRControl.StopRealPlay();
                this.m_bDVRControl.StopPlayBack();
            }
            this.m_bDVRControl.SetPlayWndType(0);
            var bRet = this.m_bDVRControl.PlayBackByTime(
                this.m_iNowChanNo,
                lpStartTime,
                lpStopTime
            );
            var newNo = +this.m_iNowChanNo + 1;
            if (bRet) {
                this.m_iPlayBlack = 1;
                console.log("回放通道" + newNo + "成功！");
            } else {
                console.log("回放通道" + newNo + "失败！");
            }
        } else {
            console.log("请选择通道号！");
        }
    }
    // 停止
    stopPlayBackChildren() {
        this.m_bDVRControl.StopPlayBack();
    }
    // 暂停
    suspendPlayBackChildren() {
        this.m_bDVRControl.PlayBackControl(3, 1);
    }
    // 继续
    continuePlayBackChildren() {
        this.m_bDVRControl.PlayBackControl(4, 1);
    }
    // 慢放
    slowPlayBackChildren() {
        this.m_bDVRControl.PlayBackControl(6, 1);
    }
    // 快放
    fastPlayBackChildren() {
        this.m_bDVRControl.PlayBackControl(5, 1);
    }
}
</script>

<style lang="less">
#OCXBody {
    width: 100%;
    height: 100%;
    margin-bottom: 5px;

    .smallocxdiv {
        float: left;
        display: ;
        width: 33%;
        height: 33%;
        z-index: 1000;
    }
}
</style>
