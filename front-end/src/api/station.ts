import request from '../util/request'
import {
    Station
} from '@/types/station.interface'
import {
    PageQuery
} from '@/types/pagequery.interface'

/** 获取默认场站 */
export function getDefaultStation(): Station {
    return {
        id: 0,
        name: '',
        position: '',
        managementId: 0,
        managementName: '',
        icon: 1,
        createTime: new Date()
    }
}

/** 根据id获取某个场站 */
export function getStation(id: number) {
    return request({
        url: `/stations/${id}`,
        method: 'get'
    })
}

/** 获取所有的场站 */
export function getStations() {
    return request({
        url: 'stations',
        method: 'get'
    })
}

/** 获取所有的场站 */
export function getAllStations() {
    return request({
        url: 'allstations',
        method: 'get'
    })
}

/** 分页获取场站 */
export function getPageInfo(param: PageQuery) {
    return request({
        url: 'stations_page',
        method: 'post',
        data: param
    })
}

/** 获取管理部门下的所有场站 */
export function getStationsByManagement(id: number) {
    return request({
        url: `managements/${id}/stations`,
        method: 'get'
    })
}

/** 添加场站 */
export function addStation(station: Station) {
    return request({
        url: 'stations',
        method: 'post',
        data: station
    })
}

/** 编辑场站 */
export function editStation(station: Station) {
    return request({
        url: 'stations_edit',
        method: 'post',
        data: station
    })
}

/** 删除场站 */
export function deleteStation(id: number) {
    return request({
        url: `stations_delete/${id}`,
        method: 'get'
    })
}