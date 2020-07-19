import request from '../util/request'
import {
    VideoGroup
} from '@/types/videogroup.interface'

/** 获取所有的视频组 */
export function getVideoGroups() {
    return request({
        url: 'videogroups',
        method: 'get'
    })
}

/** 添加视频组 */
export function addVideoGroup(videoGroup: VideoGroup) {
    return request({
        url: 'videogroups',
        method: 'post',
        data: videoGroup
    })
}

/** 编辑视频组 */
export function editVideoGroup(videoGroup: VideoGroup) {
    return request({
        url: 'videogroups_edit',
        method: 'post',
        data: videoGroup
    })
}

/** 删除视频组 */
export function deleteVideoGroup(id: number) {
    return request({
        url: 'videogroups_delete/' + id,
        method: 'get'
    })
}

/** 获取场站内的视频组 */
export function getVideoGroupsByStation(stationId: number) {
    return request({
        url: `stations/${stationId}/videogroups`,
        method: 'get'
    })
}