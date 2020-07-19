import {
    AlarmType
} from './alarmType.interface';

export interface Alarm {
    id ? : number,
        alarmTypeId ? : number,
        objId ? : number,
        level ? : number,
        description ? : string,
        cleaned ? : boolean,
        cleanedTime ? : Date,
        source ? : string,
        time ? : Date,

        stationId ? : number,
        objectName ? : string,
        stationName ? : string,
        levelName ? : string,

        alarmType ? : AlarmType,
        /** 时间戳 */
        timeStamp ? : number
}

/** 告警查找参数 */
export interface AlarmVo {
    pageNum ? : number,
        pageSize ? : number,
        objId ? : number,
        type ? : number,
        level ? : number,
        cleaned ? : boolean | number,
        stationId ? : number,
        stationIds ? : number[],
        startTime ? : Date,
        endTime ? : Date,
}