import {
    getGeoMap
} from '@/api/custommap'
import {
    hasMap,
    registerMap
} from '@/util/echartmap'
import {
    CustomMap,
    BelongGeoMap
} from '@/types/custommap.interface'

export async function createGeoMap(item: CustomMap) {

    let result: any = {
        type: "FeatureCollection",
        features: []
    }

    if (!item.geomaps && item.geomaps_str) item.geomaps = JSON.parse(item.geomaps_str)

    for (let i = 0; i < item.geomaps.length; i++) {
        const geo = item.geomaps[i]

        let tmp: any = await getGeoMap2(geo.level, geo.name)
        tmp.features.forEach((feature: any) => {
            if (geo.children.indexOf(feature.properties.name) >= 0) {
                result.features.push(feature)
            }
        })

    }

    return result
}

// 获取父节点地图信息
function getGeoMap2(level: string, name: string) {
    return new Promise((resolve, reject) => {
        getGeoMap(level, name).then((res: any) => {
            let {
                code,
                data
            } = res.data

            if (code == 200) {
                resolve(data)
            } else {
                reject()
            }
        }).catch(err => {
            reject(err)
        })
    })
}