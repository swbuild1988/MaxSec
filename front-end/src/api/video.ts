import request from '../util/request'
import {
    Video
} from '@/types/video.interface'
import {
    PageQuery
} from '@/types/pagequery.interface'
import {
    MeasObjectVo
} from '@/types/measobject.interface'

export function getDefaultVideo(): Video {
    return {
        id: 0,
        name: '',
        stationId: 0,
        stationName: '',
        objectType: 0,
        objectTypeName: '',
        dataType: 0,
        dataTypeName: '',
        equipmentSn: '',
        createTime: new Date(),
        ip: '127.0.0.1',
        port: 80,
        username: 'admin',
        password: '12345',
        serverId: 0,
        channelNo: 0,
        channelName: '',
        defaultPreset: 0,
        vendor: 0,
        videoServer: null
    }
}

/** 获取所有的监测视频 */
export function getVideos() {
    return request({
        url: 'videos',
        method: 'get'
    })
}

/** 分页获取视频 */
export function getPageInfo(param: PageQuery) {
    return request({
        url: 'videos_page',
        method: 'post',
        data: param
    })
}

/** 添加视频 */
export function addVideo(video: Video) {
    return request({
        url: 'videos',
        method: 'post',
        data: video
    })
}

/** 编辑视频 */
export function editVideo(video: Video) {
    return request({
        url: 'videos_edit',
        method: 'post',
        data: video
    })
}

/** 删除视频 */
export function deleteVideo(id: number) {
    return request({
        url: 'videos_delete/' + id,
        method: 'get'
    })
}

export function getVideoDtos() {
    return request({
        url: 'videodtos',
        method: 'get'
    })
}

export function getVideoDto(id: number) {
    return request({
        url: 'videodtos/' + id,
        method: "get"
    })
}

/** 获取场站下的所有视频 */
export function getVideoDtosByStation(stationId: number) {
    return request({
        url: `stations/${stationId}/videodtos`,
        method: 'get'
    })
}

/** 根据条件获得视频 */
export function getVideoDtosByCondition(vo: MeasObjectVo) {
    return request({
        url: 'videodtos/condition',
        method: 'post',
        data: vo
    })
}