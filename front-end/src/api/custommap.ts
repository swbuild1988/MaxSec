import request from '../util/request'
import {
    CustomMap
} from '@/types/custommap.interface'
import {
    PageQuery
} from '@/types/pagequery.interface'

/**
 * 获取地图信息
 * @param level 等级，包括country，province， city， district等
 * @param name 名称，如china，广东省，广州市，白云区等
 */
export function getGeoMap(level: string, name: string) {
    return request({
        url: 'geomap_json',
        method: 'post',
        data: {
            "level": level,
            "name": name,
        }
    })
}

/** 分页获取地图类型 */
export function getPageInfo(param: PageQuery) {
    return request({
        url: 'custommaps_page',
        method: 'post',
        data: param
    })
}

/**
 * 根据名字获得地图信息
 * @param name 
 */
export function getCustomMapByName(name: string){
    return request({
        url: '/custommaps_name/' + name,
        method: 'get'
    })
}


/** 获取所有的自定义地图 */
export function getCustomMap() {
    return request({
        url: 'custommaps',
        method: 'get'
    })
}

/** 添加自定义地图 */
export function addCustomMap(custommap: CustomMap) {
    custommap.geomaps_str = JSON.stringify(custommap.geomaps)
    return request({
        url: 'custommaps',
        method: 'post',
        data: custommap
    })
}

/** 编辑自定义地图 */
export function editCustomMap(custommap: CustomMap) {
    custommap.geomaps_str = JSON.stringify(custommap.geomaps)
    return request({
        url: 'custommaps_edit',
        method: 'post',
        data: custommap
    })
}

/** 删除自定义地图 */
export function deleteCustomMap(id: number) {
    return request({
        url: 'custommaps_delete/' + id,
        method: 'get'
    })
}