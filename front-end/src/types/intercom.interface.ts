export interface Card {
    /** 卡号 */
    cardNo ? : string,
        /** 工号 */
        jobNo ? : string,
        /** 人名 */
        name ? : string,
        /** 部门 */
        department ? : number,
        /** 部门名 */
        departmentName ? : string,
        /** 有效期开始 */
        begin ? : Date,
        /** 有效期结束 */
        end ? : Date,
}

/** 部门 */
export interface Department {
    id: number,
        name: string
}