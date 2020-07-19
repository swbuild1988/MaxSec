import {
    loginApi
} from '@/api/user'

export default {
    login(context: any, param: any) {
        const {
            username,
            password
        } = param
        return new Promise((resolve: any, reject: any) => {
            loginApi({
                name: username,
                password: password
            }).then((response: any) => {
                const {
                    code,
                    data,
                } = response.data

                if (code === 200 && Object.prototype.toString.call(data) === '[object Object]') {

                    context.commit("setToken", data.token)
                    context.commit("setUserName", data.user.name)
                    context.commit("setManagementId", data.user.managementId)
                    context.commit("setStationId", data.user.stationId)
                    context.commit("setRoles", data.roles)
                    context.commit("setPermissions", data.permissions)

                    resolve(data)

                } else {
                    console.log("登录失败")
                    reject(data)
                }
            }).catch(err => {
                console.log("错误", err)
                reject(err)
            })
        })
    }


}