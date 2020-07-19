import request from '../util/request'

/** 获得后台固定地址的文件 */
export function getFileFromPath(path: string) {
    return request({
        url: 'file',
        method: 'post',
        data: {
            path: path
        },
        responseType: 'arraybuffer'
    })
}