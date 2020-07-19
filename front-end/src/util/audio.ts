import request from '@/util/request'

export class Audio {
    private _audioCtx: AudioContext
    private _source: AudioBufferSourceNode
    private _audioBuffer: AudioBuffer

    /** 构造 */
    constructor() {
        this._audioCtx = new AudioContext()
    }

    /** 开始播放 */
    public playSound(fileName: string) {
        console.log("开始播放")
        this._loadAudioFile(fileName)
    }

    /** 停止播放 */
    public stopSound() {
        console.log("停止播放")
        if (this._source) this._source.stop()
    }

    _voice() {

        this._source = this._audioCtx.createBufferSource();
        this._source.buffer = this._audioBuffer;
        this._source.loop = false;
        this._source.connect(this._audioCtx.destination);
        this._source.start(); //立即播放

    }

    _initSound(arrayBuffer: ArrayBuffer) {
        let _this = this

        this._audioCtx.decodeAudioData(arrayBuffer, function (buffer) { //解码成功时的回调函数
            _this._audioBuffer = buffer
            _this._voice()
        }, function (e) { //解码出错时的回调函数
            console.log('解码出错', e);
        });
    }

    // 加载音频文件
    _loadAudioFile(fileName: string) {
        let _this = this
        request({
            url: 'alarm_voice/' + fileName,
            method: 'get',
            responseType: 'arraybuffer',
        }).then((res: any) => {
            _this._initSound(res.data)
        })
    }
}