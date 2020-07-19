import request from '../util/request'
import {
    Equipment,
    EquipmentVo
} from '@/types/equipment.interface'

/** 获取所有的设备 */
export function getEquipments() {
    return request({
        url: 'equipments',
        method: 'get'
    })
}

/** 添加设备 */
export function addEquipment(equipment: Equipment) {
    return request({
        url: 'equipments',
        method: 'post',
        data: equipment
    })
}

/** 编辑设备 */
export function editEquipment(equipment: Equipment) {
    return request({
        url: 'equipments_edit',
        method: 'post',
        data: equipment
    })
}

/** 删除设备 */
export function deleteEquipment(id: number) {
    return request({
        url: 'equipments_delete/' + id,
        method: 'get'
    })
}

/** 获取设备统计信息 */
export function getEquipmentStatistics() {
    return request({
        url: 'equipment_type_statistic',
        method: 'get'
    })
}

/** 获取设备类型、状态的统计信息 */
export function getEquipmentTypeStateStatistice() {
    return request({
        url: 'equipment_type_state_statistic',
        method: 'get'
    })
}

/** 获取场站中各状态设备统计 */
export function getEquiupmentStateStatisticeByStation(stationId: number) {
    return request({
        url: `stations/${stationId}/equipment_state_statistic`,
        method: 'get'
    })
}

/** 根据管理部门分页搜索设备 */
export function getPageEquipmentsByManagement(name: string, pageNum: number, pageSize: number) {
    return request({
        url: 'equipments_page_management',
        method: 'post',
        data: {
            name: name,
            pageNum: pageNum,
            pageSize: pageSize,
        }
    })
}

/** 根据条件分页搜索设备 */
export function getPageEquipmentsByCondition(data: EquipmentVo) {
    return request({
        url: 'equipments/condition',
        method: 'post',
        data: data
    })
}