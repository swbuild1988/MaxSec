import {
    State
} from './state'
import {
    EnumType
} from '@/types/enumtype.interface'

const TOKEN_KEY: string = "TOKEN"
const USERNAME_KEY: string = "USER_NAME"
const MANAGEMENT_KEY: string = "MANAGEMENTID"
const STATION_KEY: string = "STATIONID"

export default {

    /** 初始所有的参数 */
    initParamter(state: State) {
        state.token = localStorage.getItem(TOKEN_KEY)
        state.userName = localStorage.getItem(USERNAME_KEY)
        state.managementId = Number(localStorage.getItem(MANAGEMENT_KEY))
        state.stationId = Number(localStorage.getItem(STATION_KEY))
        state.roles = null
        state.permissions = null
    },

    /** 清除所有的缓存 */
    removeParamter(state: State) {
        state.token = null
        localStorage.removeItem(TOKEN_KEY)
        state.userName = null
        localStorage.removeItem(USERNAME_KEY)
        state.managementId = null
        localStorage.removeItem(MANAGEMENT_KEY)
        state.stationId = null
        localStorage.removeItem(STATION_KEY)
        state.roles = null
        state.permissions = null
    },

    setUserName(state: State, value: any) {
        state.userName = value
        localStorage.setItem(USERNAME_KEY, value)
    },

    setToken(state: State, value: any) {
        state.token = value
        localStorage.setItem(TOKEN_KEY, value)
    },

    setManagementId(state: State, value: any) {
        state.managementId = value
        localStorage.setItem(MANAGEMENT_KEY, value)
    },

    setStationId(state: State, value: any) {
        state.stationId = value
        localStorage.setItem(STATION_KEY, value)
    },

    setRoles(state: State, value: any) {
        state.roles = value
    },

    setPermissions(state: State, value: any) {
        state.permissions = value
    },

    setDataType(state: State, value: Array < EnumType > ) {
        state.dataType = value
    },

    setObjectType(state: State, value: Array < EnumType > ) {
        state.objectType = value
    },

    setEquipmentType(state: State, value: Array < EnumType > ) {
        state.equipmentType = value
    },

    setEquipmentStateType(state: State, value: Array < EnumType > ) {
        state.equipmentStateType = value
    },

    setAlarmLevel(state: State, value: Array < EnumType > ) {
        state.alarmLevel = value
    },

}