import request from '@/util/request'
import Vue from 'vue'

export default async () => {
    // 根据生产环境还是开发环境读取配置文件
    let isProducetion: boolean = process.env.NODE_ENV === 'production'
    let config: any = isProducetion ?
        (await request.get('../serverconfig.json')).data :
        require('../../public/serverconfig.json')

    request.defaults.baseURL = config.ApiUrl
    Vue.prototype.VisualizedParams = config.VisualizedParams
    Vue.prototype.TestData = config.TestData
    Vue.prototype.MQParams = config.MQParams
}