import request from '../util/request'
import {
    StationLink
} from '@/types/stationlink.interface'
import {
    PageQuery
} from '@/types/pagequery.interface'

/** 获取所有的场站关联 */
export function getStationLinks() {
    return request({
        url: 'stationlinks',
        method: 'get'
    })
}

/** 分页获取场站关联 */
export function getPageInfo(param: PageQuery) {
    return request({
        url: 'stationlinks_page',
        method: 'post',
        data: param
    })
}


/** 添加场站关联 */
export function addStationLink(stationlink: StationLink) {
    return request({
        url: 'stationlinks',
        method: 'post',
        data: stationlink
    })
}

/** 编辑场站关联 */
export function editStationLink(stationlink: StationLink) {
    return request({
        url: 'stationlinks_edit',
        method: 'post',
        data: stationlink
    })
}

/** 删除场站关联 */
export function deleteStationLink(id: number) {
    return request({
        url: `stationlinks_delete/${id}`,
        method: 'get'
    })
}