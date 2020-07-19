import {
    VideoServer
} from './videoserver.interface';

export interface Video {
    id: number,
        name: string,
        stationId: number,
        stationName: string,
        objectType: number,
        objectTypeName: string,
        dataType: number,
        dataTypeName: string,
        equipmentSn: string,
        createTime: Date,

        ip: string,
        port: number,
        username: string,
        password: string,
        serverId: number,
        /** 通道号 */
        channelNo: number,
        /** 通道名 */
        channelName: string,
        /** 默认预置位 */
        defaultPreset: number,
        /** 类型 */
        vendor: number,

        videoServer: VideoServer,
}