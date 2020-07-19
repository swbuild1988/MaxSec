import request from '@/util/request'
import {
    AlarmType
} from '@/types/alarmType.interface'
import {
    PageQuery
} from '@/types/pagequery.interface'

/** 获取所有的告警类型 */
export function getAlarmTypes() {
    return request({
        url: 'alarmtypes',
        method: 'get'
    })
}

/** 分页获取告警类型 */
export function getPageInfo(param: PageQuery) {
    return request({
        url: 'alarmtypes_page',
        method: 'post',
        data: param
    })
}

/** 添加告警类型 */
export function addAlarmType(alarmType: AlarmType) {
    return request({
        url: 'alarmtypes',
        method: 'post',
        data: alarmType
    })
}

/** 编辑告警类型 */
export function editAlarmType(alarmType: AlarmType) {
    return request({
        url: 'alarmtypes_edit',
        method: 'post',
        data: alarmType
    })
}

/** 删除告警类型 */
export function deleteAlarmType(id: number) {
    return request({
        url: 'alarmtypes_delete/' + id,
        method: 'get'
    })
}