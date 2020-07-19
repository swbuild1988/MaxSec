import request from '@/util/request'
import {
    AlarmVo,
    Alarm
} from '@/types/alarm.interface'

export function getDefaultAlarm(): Alarm {
    return {
        id: 0,
        alarmTypeId: 0,
        objId: 0,
        level: 0,
        description: "",
        cleaned: false,
        source: '',
        time: new Date(),
        alarmType: {
            id: 0,
            name: "",
            objectType: 0,
            objectTypeName: '',
            description: '',
        },
        objectName: '',
        levelName: '',
    }
}

/** 获取告警统计信息 */
export function getAlarmStatistics() {
    return request({
        url: 'alarm_type_statistic',
        method: 'get'
    })
}

/** 获取某场站告警统计信息 */
export function getAlarmStatisticsByStation(stationId: number) {
    return request({
        url: `stations/${stationId}/alarm_type_statistic`,
        method: 'get'
    })
}

/** 获取告警趋势 */
export function getAlarmTrend() {
    return request({
        url: 'alarm_type_trend',
        method: 'get'
    })
}

/** 根据条件分页搜索告警 */
export function getPageAlarmsByCondition(data: AlarmVo) {
    return request({
        url: 'alarms/condition',
        method: 'post',
        data: data
    })
}

/** 根据条件获取告警数目 */
export function getCountByCondition(vo: AlarmVo) {
    return request({
        url: 'alarm_count/condition',
        method: 'post',
        data: vo
    })
}

/** 获取告警的图片地址 */
export function getImagePathOfAlarm(id: number) {
    return request({
        url: `alarms/${id}/images`,
        method: 'get'
    })
}

/** 获取告警的视频地址 */
export function getVideoPathOfAlarm(id: number) {
    return request({
        url: `alarms/${id}/videos`,
        method: 'get'
    })
}

/** 告警清除 */
export function cleanAlarm(id: number) {
    return request({
        url: `alarms/${id}/clean`,
        method: "get"
    })
}