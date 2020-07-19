import request from '../util/request'

/** 获取队列名 */
export function getQueueName() {
    return request({
        url: 'mq/createqueue',
        method: 'get'
    })
}