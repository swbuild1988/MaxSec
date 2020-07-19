import request from '../util/request'


/** 获取数据类型 */
export function getDataType() {
    return request({
        url: 'data-enum',
        method: 'get'
    })
}

/** 获取设备类型 */
export function getEquipmentType() {
    return request({
        url: 'equipment-enum',
        method: 'get'
    })
}

/** 获取设备状态类型 */
export function getEquipmentStateType() {
    return request({
        url: 'equipment-state-enum',
        method: 'get'
    })
}

/** 获取对象类型 */
export function getObjectType() {
    return request({
        url: 'object-enum',
        method: 'get'
    })
}

/** 获取告警级别 */
export function getAlarmLevel() {
    return request({
        url: 'alarm-level-enum',
        method: 'get'
    })
}