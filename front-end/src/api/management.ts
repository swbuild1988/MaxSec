import request from '@/util/request'
import {
    Management
} from '@/types/management.interface'
import {
    PageQuery
} from '@/types/pagequery.interface'

/** 获取所有的管理部门 */
export function getManagements() {
    return request({
        url: 'managements',
        method: 'get'
    })
}


/** 分页获取管理部门类型 */
export function getPageInfo(param: PageQuery) {
    return request({
        url: 'managements_page',
        method: 'post',
        data: param
    })
}

/** 添加管理部门 */
export function addManagement(management: Management) {
    return request({
        url: 'managements',
        method: 'post',
        data: management
    })
}

/** 编辑管理部门 */
export function editManagement(management: Management) {
    return request({
        url: 'managements_edit',
        method: 'post',
        data: management
    })
}

/** 删除管理部门 */
export function deleteManagement(id: number) {
    return request({
        url: 'managements_delete/' + id,
        method: 'get'
    })
}

/** 获取管理部分的tree */
export function getManagementTree() {
    return request({
        url: 'managements/tree',
        method: 'get'
    })
}