export interface Management {
    id: number,
        name: string,
        position: string,
        level: number,
        fatherId: number,
        fatherName: string,
        leaf: boolean,
        map: string,
        createTime: Date
}

export interface ManagementTree {
    id: number,
        name: string,
        position: string,
        level: number,
        fatherId: number,
        leaf: boolean,
        map: string,
        children: ManagementTree[]
}