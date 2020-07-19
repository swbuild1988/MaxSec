import request from '../util/request'
import {
    User
} from '@/types/user.interface'

export function loginApi(data: any) {
    return request({
        url: 'login',
        method: 'post',
        data
    })
}

/** 获取所有用户 */
export function getUsers() {
    return request({
        url: 'users',
        method: 'get'
    })
}


/** 添加用户 */
export function addUser(user: User) {
    return request({
        url: 'users',
        method: 'post',
        data: user
    })
}

/** 编辑用户 */
export function editUser(user: User) {
    return request({
        url: 'users_edit',
        method: 'post',
        data: user
    })
}

/** 删除用户 */
export function deleteUser(id: number) {
    return request({
        url: 'users_delete/' + id,
        method: 'get'
    })
}

/** 获取当前用户的角色 */
export function getRoles() {
    return request({
        url: 'roles',
        method: 'get'
    })
}

/** 获取当前用户的所有权限 */
export function getPermissions() {
    return request({
        url: 'permissions',
        method: 'get'
    })
}