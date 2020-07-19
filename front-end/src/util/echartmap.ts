import Vue from "vue"

/**
 * 判断是否已经注册过地图了
 * @param name 
 */
export function hasMap(name: String): boolean {

    if (Vue.prototype.$echarts) {

        let echarts: any = Vue.prototype.$echarts

        let result: Object = echarts.getMap(name)

        if (result) {
            return true
        } else {
            return false
        }
    } else {
        // 如果没有echarts，就别再查找了
        return true
    }
}

/**
 * 注册地图
 * @param name 
 * @param geoJson 
 */
export function registerMap(name: String, geoJson: Object) {

    if (!hasMap(name)) {
        let echarts: any = Vue.prototype.$echarts
        echarts.registerMap(name, geoJson)
    }
}

/**
 * 更新地图
 * @param name 
 * @param geoJson 
 */
export function updateMap(name: String, geoJson: Object) {

    let echarts: any = Vue.prototype.$echarts
    echarts.registerMap(name, geoJson)

}