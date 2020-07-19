import request from '../util/request'

/** 接听 */
export function answer(id: number) {
    return request({
        url: `intercom/${id}/answer`,
        method: 'get'
    })
}

/** 挂断 */
export function ringoff(id: number) {
    return request({
        url: `intercom/${id}/ringoff`,
        method: 'get'
    })
}

/** 拒绝 */
export function refuse(id: number) {
    return request({
        url: `intercom/${id}/refuse`,
        method: 'get'
    })
}

/** 开门 */
export function open(id: number) {
    return request({
        url: `intercom/${id}/open`,
        method: 'get'
    })
}

/** 获取所有的人员，异步 */
export function getCards(id: number) {
    return request({
        url: `intercom/${id}/cards`,
        method: 'get'
    })
}

/** 获取所有部门 */
export function getDepartments() {
    return request({
        url: 'departments',
        method: 'get'
    })
}