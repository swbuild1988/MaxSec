import Vue from 'vue'
import Stomp, {
    Client
} from "stompjs";

// MQ消息队列中转站
let TransferStation: any = {
    listeners: new Map(),

    updateData: function (data: any) {
        console.log("中转站更新数据", data)

        for (let [key, listener] of this.listeners) {
            listener(data);
        }
    },

    addListener: function (key: string, listener: any) {
        console.log("中转站添加新的监听器", key)
        this.listeners.set(key, listener)
    },

    deleteListener: function (key: string) {
        this.listeners.delete(key)
    },

    getListener: function (key: string) {
        this.listeners.get(key);
    }
}

/**
 *
 * 初始化并连接MQ服务器
 *
 */
export class MQ {
    private client: Client
    private ws: WebSocket
    private MQParams: any

    constructor() {
        this.client = null;
        this.ws = null;
        this.MQParams = Vue.prototype.MQParams;
    }

    openMQ() {
        let _this = this;
        _this._InitMQ(_this._MQCallback);
    }

    //断开与MQ的连接
    closeMQ() {
        this.client.disconnect(() => {
            console.log("See you next time!");
        });
    }

    _MQCallback() {
        return function (data: any) {
            TransferStation.updateData(data.body)
        }
    }

    _onConnect(callback: any) {
        let _this = this;
        return function () {
            _this.client.subscribe(`/${_this.MQParams.connectType}/${_this.MQParams.connectName}`, callback.apply(_this))
        }
    }

    //断连后重连
    _onError() {
        console.info("rabbitmq error")
        console.error(arguments)
        console.info("connected", this.client.connected)
        // this.openMQ()
    }

    //初始化MQ
    _InitMQ(callback: any) {
        let _this = this;

        if ('WebSocket' in window) {
            _this.ws = new WebSocket(this.MQParams.address);
        } else {
            // ws = new SockJS('http://192.168.0.41:15670/stomp');
            alert("浏览器不支持WebSocket");
        }
        // 获得Stomp client对象
        _this.client = Stomp.over(_this.ws);
        // SockJS does not support heart-beat: disable heart-beats
        _this.client.heartbeat.outgoing = 5000;
        _this.client.heartbeat.incoming = 5000;

        _this.client.connect(this.MQParams.login, this.MQParams.passcode, _this._onConnect(callback), _this._onError.apply(_this), '/');
    };
}

Vue.prototype.TransferStation = TransferStation;