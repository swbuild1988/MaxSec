export interface NetNode {
    id: number,
        name: string,
        fatherId: number,
        level: number,
        type: string,
        usedId: number,
        connected: boolean,
        root: boolean,
        createTime: Date
}