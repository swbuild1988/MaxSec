export interface VideoServer {
    id: number,
        name: string,
        stationId: number,
        stationName: string,
        objectType: number,
        objectTypeName: string,
        dataType: number,
        dataTypeName: string,
        equipmentSn: string,
        createTime: Date,

        portNum: number,
        ip: string,
        port: number,
        username: string,
        password: string,
        vendor: number,
}