export interface VideoGroup {
    id: number,
    name: string,
    stationId: number,
    videos: string,
    createTime: Date,
    isVideoGroupsEdited?: boolean, //是否只读，默认只读

    videoList?: number[]
}