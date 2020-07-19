<template>
    <div style="overflow-y: auto; height: 85vh;">
        <h4>网络节点管理</h4>
        <div class="btnClass">
            <Button type="info" icon="md-add" @click="addModel">新建</Button>
            <Button type="warning" icon="md-arrow-down">导出</Button>
        </div>
        <Table border :columns="columns" :data="netNodes">
            <template slot-scope="{ row, index }" slot="action">
                <Button type="success" size="small" style="margin-right: 5px" icon="md-create"
                    @click="edit(index)">编辑</Button>
                <Button type="error" size="small" icon="md-trash" @click="remove(index)">删除</Button>
            </template>
        </Table>
        <Modal v-model="flag_add" title="新增" @on-ok="addOk" @on-cancel="addCancel">
            <Form :model="addItem" :label-width="120">
                <FormItem label="节点名:">
                    <Input v-model="addItem.name" />
                </FormItem>
                <FormItem label="父节点ID:">
                    <el-select v-model="addItem.fatherId" @change="addSelectChange">
                        <el-option v-for="item in netNodes" :value="item.id" :key="item.id" :label="item.name">
                        </el-option>
                    </el-select>
                </FormItem>
                <FormItem label="层级:">
                    <Input v-model="addItem.level" />
                </FormItem>
                <FormItem label="所属类型:">
                    <Input v-model="addItem.type" />
                </FormItem>
                <FormItem label="所在表ID:">
                    <Input v-model="addItem.usedId" />
                </FormItem>
                <FormItem label="连接状态:">
                    <Checkbox v-model="addItem.connected">是否连接</Checkbox>
                </FormItem>
                <FormItem label="是否为根节点:">
                    <Checkbox v-model="addItem.root">根节点</Checkbox>
                </FormItem>
            </Form>
        </Modal>
        <Modal v-model="flag_edit" title="编辑" @on-ok="editOk" @on-cancel="editCancel">
            <Form :model="editItem" :label-width="120">
                <FormItem label="节点名:">
                    <Input v-model="editItem.name" />
                </FormItem>
                <FormItem label="父节点ID:">
                    <el-select v-model="editItem.fatherId" @change="editSelectChange">
                        <el-option v-for="item in netNodes" :value="item.id" :key="item.id" :label="item.name">
                        </el-option>
                    </el-select>
                </FormItem>
                <FormItem label="层级:">
                    <Input v-model="editItem.level" />
                </FormItem>
                <FormItem label="所属类型:">
                    <Input v-model="editItem.type" />
                </FormItem>
                <FormItem label="所在表ID:">
                    <Input v-model="editItem.usedId" />
                </FormItem>
                <FormItem label="连接状态:">
                    <Checkbox v-model="editItem.connected">是否连接</Checkbox>
                </FormItem>
                <FormItem label="是否为根节点:">
                    <Checkbox v-model="editItem.root">根节点</Checkbox>
                </FormItem>
            </Form>
        </Modal>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue
    } from "vue-property-decorator";
    import {
        NetNode
    } from "@/types/netnode.interface";
    import {
        getNetNodes,
        addNetNode,
        editNetNode,
        deleteNetNode,
    } from "@/api/netnode";

    @Component({})
    export default class MeasObjectClass extends Vue {
        netNodes: NetNode[] = [];

        columns: any[] = [{
                title: "ID",
                key: "id",
            },
            {
                title: "节点名",
                key: "name",
            },
            {
                title: "父节点ID",
                key: "fatherId",
            },
            {
                title: "层级",
                key: "level",
            },
            {
                title: "所属类型",
                key: "type",
            },
            {
                title: "所在表ID",
                key: "usedId",
            },
            {
                title: "是否连接",
                key: "connected",
            },
            {
                title: "是否为根节点",
                key: "root",
            },
            {
                title: "创建时间",
                key: "createTime",
                render: (h: any, params: any) => {
                    return h(
                        "div",
                        new Date(params.row.createTime).format("yyyy-MM-dd hh:mm:ss")
                    );
                },
            },
            {
                title: "操作",
                slot: "action",
                width: 350,
                align: "center",
            },
        ];

        /** 添加窗口是否弹出 */
        flag_add: boolean = false;

        defaultItem: NetNode = {
            id: 0,
            name: "",
            fatherId: 0,
            level: 1,
            type: "",
            usedId: 0,
            connected: true,
            root: false,
            createTime: new Date(),
        };

        /** 待添加的对象 */
        addItem: NetNode = this.defaultItem;

        /** 编辑窗口是否弹出 */
        flag_edit: boolean = false;

        /** 待编辑的对象 */
        editItem: NetNode = this.defaultItem;

        mounted() {
            this.initData();
        }

        initData() {
            getNetNodes().then((res: any) => {
                let {
                    code,
                    data
                } = res.data;
                if (code == 200) {
                    this.netNodes = data;
                    this.netNodes.map((item: NetNode) => {
                        item.createTime = new Date(item.createTime);
                    });
                }
            });
        }

        /** 添加新对象 */
        addModel() {
            this.flag_add = true;
        }

        edit(index: number) {
            this.flag_edit = true;
            this.editItem = this.netNodes[index];
        }

        remove(index: number) {
            deleteNetNode(this.netNodes[index].id).then((res: any) => {
                let {
                    code,
                    data
                } = res.data;
                if (code == 200) {
                    this.initData();
                    this.$Message.success("删除成功！！！");
                } else {
                    this.$Message.info("删除出错！！！");
                }
            });
        }

        /** 添加父亲改变 */
        addSelectChange(value: any) {
            let tmp: NetNode | undefined = this.netNodes.find((item: NetNode) => {
                return item.id == value;
            });

            if (tmp) {
                this.addItem.level = tmp.level + 1;
            }
        }

        /** 编辑父亲改变 */
        editSelectChange(value: any) {
            let tmp: NetNode | undefined = this.netNodes.find((item: NetNode) => {
                return item.id == value;
            });

            if (tmp) {
                this.editItem.level = tmp.level + 1;
            }
        }

        /** 添加对象 */
        addOk() {
            addNetNode(this.addItem).then((res: any) => {
                let {
                    code,
                    data
                } = res.data;
                if (code == 200) {
                    this.flag_add = false;
                    this.addItem = this.defaultItem;
                    this.initData();
                } else {
                    this.$Message.info("添加出错！！！");
                }
            });
        }

        /** 取消添加 */
        addCancel() {
            this.flag_add = false;
            this.addItem = this.defaultItem;
        }

        /** 编辑对象 */
        editOk() {
            editNetNode(this.editItem).then((res: any) => {
                let {
                    code,
                    data
                } = res.data;
                if (code == 200) {
                    this.flag_edit = false;
                    this.initData();
                } else {
                    this.$Message.info("编辑出错！！！");
                }
            });
        }

        /** 取消编辑 */
        editCancel() {
            this.flag_edit = false;
        }
    }
</script>

<style scoped>
    .btnClass button {
        margin: 0px 10px;
    }
</style>