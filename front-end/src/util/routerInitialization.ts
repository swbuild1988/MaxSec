import Vue from 'vue'
import router from "@/router"
import store from '@/store'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'; // Progress 进度条样式
import {
    getRoles,
    getPermissions
} from '@/api/user'
import {
    getDataType,
    getObjectType,
    getEquipmentStateType,
    getEquipmentType,
    getAlarmLevel
} from '@/api/enum'
import {
    getQueueName
} from '@/api/rabbitmq'
import {
    AxiosPromise
} from 'axios';

NProgress.configure({
    showSpinner: false
}) // NProgress Configuration

const whiteList: string[] = ['/login'] // no redirect 的白名单

const routerInitialization = async () => {

    router.beforeEach(async (to: any, from: any, next: any) => {

        NProgress.start()
        // 确定用户是否登录
        const hasToken: any = store.state.token

        if (hasToken) {

            // 先获取当前人员的角色和权限
            await storeRolesAndPermissions()
            // 获取枚举类
            await getEnum()

            if (to.path === '/login' || to.path == '/') {
                next('/main')
                NProgress.done(); // 结束Progress
            } else if (to.path === '/main') {
                if (store.state.managementId > 0) {
                    next()
                } else {
                    next('/stations')
                    NProgress.done(); // 结束Progress
                }
            } else {
                next()
            }

        } else {
            if (whiteList.indexOf(to.path) !== -1) {
                // 白名单内 直接进入
                next()
            } else {
                // 没有访问权限的其他页将重定向到登录页。
                next(`/login`)
                NProgress.done()
            }
        }
    })

    router.afterEach(() => {
        NProgress.done()
    })
}

function getRolesPromise() {
    return new Promise((resolve, reject) => {
        getRoles().then((response: any) => {
            const {
                code,
                data,
            } = response.data

            if (code == 200) {
                let res: Array < string > = (data as Array < any > ).map((a: any) => {
                    return a.name
                })
                resolve(res)
            } else {
                reject()
            }
        }).catch(err => {
            reject(err)
        })
    })
}

function getPermissionsPromise() {
    return new Promise((resolve, reject) => {
        getPermissions().then((response: any) => {
            const {
                code,
                data,
            } = response.data

            if (code == 200) {
                let res: Array < string > = (data as Array < any > ).map((a: any) => {
                    return a.name
                })
                resolve(res)
            } else {
                reject()
            }
        }).catch(err => {
            reject(err)
        })
    })
}

/**
 * 获取枚举的异步调用
 * @param f 
 */
function getEnumPromise(f: AxiosPromise < any > ) {
    return new Promise((resolve, reject) => {
        f.then((response: any) => {
            const {
                code,
                data,
            } = response.data

            if (code == 200) {
                resolve(data)
            } else {
                reject()
            }
        }).catch(err => {
            reject(err)
        })
    })
}

/** 异步转同步，获取角色和权限 */
async function storeRolesAndPermissions() {

    if (!store.state.roles) {
        let roles = await getRolesPromise()
        store.commit("setRoles", roles)
    }

    if (!store.state.permissions) {
        let permissions = await getPermissionsPromise()
        console.log("permissions", permissions)
        store.commit("setPermissions", permissions)
    }
}

/** 获取所有的枚举类 */
async function getEnum() {

    if (!store.state.dataType) {
        let dataType = await getEnumPromise(getDataType())
        store.commit("setDataType", dataType)
    }

    if (!store.state.objectType) {
        let objectType = await getEnumPromise(getObjectType())
        store.commit("setObjectType", objectType)
    }

    if (!store.state.equipmentType) {
        let equipmentType = await getEnumPromise(getEquipmentType())
        store.commit("setEquipmentType", equipmentType)
    }

    if (!store.state.equipmentStateType) {
        let equipmentStateType = await getEnumPromise(getEquipmentStateType())
        store.commit("setEquipmentStateType", equipmentStateType)
    }

    if (!store.state.alarmLevel) {
        let alarmLevel = await getEnumPromise(getAlarmLevel())
        store.commit("setAlarmLevel", alarmLevel)
    }
}

export {
    routerInitialization
}
// 判断有没有登陆过（token），否则直接跳到登录页