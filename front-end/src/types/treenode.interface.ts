export interface TreeNode {
    id: number,
        /** 节点名 */
        name: string,
        /** 与父节点是否连接 */
        connected: boolean,
        /** 层级 */
        level: number,
        /** 类型（场站、视频服务等） */ 
        type: string,
        /** 所在表ID */
        usedId: number,
        /** 下面的节点是否有未连接的，包括孙子等 */
        childrenConnected: boolean,
        /** 子节点 */
        children: TreeNode[]
}