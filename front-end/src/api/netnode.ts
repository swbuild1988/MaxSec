import request from '../util/request'
import {
    NetNode
} from '@/types/netnode.interface'

/** 获取所有的监测对象 */
export function getNetNodes() {
    return request({
        url: 'netnodes',
        method: 'get'
    })
}

/** 添加节点 */
export function addNetNode(netNode: NetNode) {
    return request({
        url: 'netnodes',
        method: 'post',
        data: netNode
    })
}

/** 编辑节点 */
export function editNetNode(netNode: NetNode) {
    return request({
        url: 'netnodes_edit',
        method: 'post',
        data: netNode
    })
}

/** 删除节点 */
export function deleteNetNode(id: number) {
    return request({
        url: 'netnodes_delete/' + id,
        method: 'get'
    })
}

/** 获取整个树 */
export function getTree() {
    return request({
        url: 'netnodes/tree',
        method: 'get'
    })
}