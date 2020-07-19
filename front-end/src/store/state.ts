import {
    EnumType
} from '@/types/enumtype.interface'

export interface State {
    /** 用户名 */
    userName: string | null,
        /** token */
        token: string | null,
        /** 所属管理部门ID */
        managementId: number | null,
        /** 场站页面的场站ID */
        stationId: number | null,
        /** 角色 */
        roles: Array < string > | null,
        /** 权限 */
        permissions: Array < string > | null,
        /** 数据类型 */
        dataType: Array < EnumType > | null,
        /** 对象类型 */
        objectType: Array < EnumType > | null,
        /** 设备类型 */
        equipmentType: Array < EnumType > | null,
        /** 设备状态类型 */
        equipmentStateType: Array < EnumType > | null,
        /** 告警级别 */
        alarmLevel: Array < EnumType > | null,
        /** 图标 */
        icons: Array < any > ,
        /** 线的样式 */
        lineStyles: Array < any > ,
}

export const state: State = {
    userName: '',
    token: '',
    managementId: null,
    stationId: null,
    roles: null,
    permissions: null,
    dataType: null,
    objectType: null,
    equipmentType: null,
    equipmentStateType: null,
    alarmLevel: null,
    icons: [{
        id: 1,
        name: "station.png",
        img: require("@/assets/icon/station.png")
    }, {
        id: 2,
        name: "station.svg",
        img: require("@/assets/icon/station.svg")
    }],
    lineStyles: [{
        id: 1,
        color: "#DA251D"
    }, {
        id: 2,
        color: "#00923F"
    }, {
        id: 3,
        color: "#005CA1"
    }]
}