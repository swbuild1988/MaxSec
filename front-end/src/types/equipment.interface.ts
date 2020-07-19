export interface Equipment {
    id: number,
        name: string,
        sn: string,
        stationId: number,
        stationName: string,
        equipmentType: number,
        equipmentTypeName: string,
        equipmentState: number,
        equipmentStateName: string,
        createTime: Date
}

/** 视频查找参数 */
export interface EquipmentVo {
    pageNum ? : number,
        pageSize ? : number,
        equipmentType ? : number,
        equipmentState ? : number,
        stationId ? : number,
        stationIds ? : number[]
}