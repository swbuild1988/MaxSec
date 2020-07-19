import request from '../util/request'
import {
    VideoServer
} from '@/types/videoserver.interface'
import {
    PageQuery
} from '@/types/pagequery.interface'

/** 获取所有的视频服务 */
export function getVideoServers() {
    return request({
        url: 'videoservers',
        method: 'get'
    })
}

/** 分页获取视频服务 */
export function getPageInfo(param: PageQuery) {
    return request({
        url: 'videoservers_page',
        method: 'post',
        data: param
    })
}

/** 添加视频服务 */
export function addVideoServer(videoserver: VideoServer) {
    return request({
        url: 'videoservers',
        method: 'post',
        data: videoserver
    })
}

/** 编辑视频服务 */
export function editVideoServer(videoserver: VideoServer) {
    return request({
        url: 'videoservers_edit',
        method: 'post',
        data: videoserver
    })
}

/** 删除视频服务 */
export function deleteVideoServer(id: number) {
    return request({
        url: 'videoservers_delete/' + id,
        method: 'get'
    })
}