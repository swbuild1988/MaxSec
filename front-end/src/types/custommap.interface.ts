/**
 * 自定义地图
 */
export interface CustomMap {
    id: number,
    /** 自定义地图名 */
        name: string,
        geomaps: BelongGeoMap[],
        /** 地图文件转str */
        geomaps_str: string,
}

/**
 * 所属的地图文件
 */
export interface BelongGeoMap {
    /** 级别 */
    level: string,
    /** 名称 */
    name: string,
    /** 所有包含的孩子 */
    children: string[],
}