import axios from 'axios'
import store from '@/store'
import router from '../router'

// 创建axios实例
const service = axios.create({
    // url前缀，如/api
    baseURL: '/api',
    // timeout: 5000, // request timeout
    headers: {
        'Content-Type': 'application/json;charset=utf-8',
        // 解决ie浏览器get请求缓存问题
        'cache-control': 'no-cache',
        'Pragma': 'no-cache'
    }
})

// request拦截器
service.interceptors.request.use(
    (config: any) => {
        if (store.state.token) {
            let Authorization = 'Authorization'
            config.headers.common[Authorization] = store.state.token
        }
        return config
    },
    (error: any) => {
        console.log(error) // for debug
        return Promise.reject(error)
    }
)

// respone拦截器
service.interceptors.response.use(
    (response: any) => {
        const res: any = response.data
        if (res.code === 401) {
            cleanAccount()
        } else {
            return response
        }
    },
    (error: any) => {
        console.log("拦截器error", error.response)
        let response: any = error.response
        if (response.status == 500) {
            // shiro 401异常抛出
            if (response.data) {
                let data = response.data
                if (data.message && data.message == "401") {
                    cleanAccount()
                }
            }
        } else if (response.status == 401) {
            cleanAccount()
        }
    }
)

function cleanAccount() {
    store.commit("removeParamter")
    router.push({
        path: '/login'
    })
}

export default service