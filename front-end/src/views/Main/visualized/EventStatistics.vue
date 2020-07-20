<template>
    <div class="event_statistics">
        <h4>
            <span class="icon"></span>
            事件统计
        </h4>
        <div class="event_statistics_content">
            <div class="content_item total" @click="clickDiv(null)">
                <p>{{totalNum}}个<br />总事件</p>
            </div>
            <div class="content_item handled" @click="clickDiv(true)">
                <p>{{cleanedNum}}个<br />已处理</p>
            </div>
            <div class="content_item unprocessed" @click="clickDiv(false)">
                <p>{{uncleanedNum}}个<br />未处理</p>
            </div>
        </div>
        <Modal v-model="modelData.show" width="800">
            <p slot="header" style="color:#f60;text-align:center">
                <Icon type="ios-information-circle"></Icon>
                <span style="font-size: medium">{{ modelData.title }}</span>
            </p>
            <div style="text-align:right">
                <Table border :columns="modelData.columns" :data="modelData.alarms" height="520"></Table>
                <Page :total="modelData.page.total" :page-size="modelData.page.pageSize"
                    :current="modelData.page.current" show-total show-sizer @on-change="currentChange"
                    @on-page-size-change="pageSizeChange" />
            </div>
            <div slot="footer" style="text-align:right">
                <Button type="primary" @click="modelData.show = false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue
    } from "vue-property-decorator";
    import {
        getCountByCondition
    } from "@/api/alarm"
    import {
        AlarmVo
    } from '@/types/alarm.interface'
    import {
        getPageAlarmsByCondition
    } from '@/api/alarm'

    @Component({})
    export default class EventStatistics extends Vue {

        totalNum: number = 0
        cleanedNum: number = 0

        // 对话框内的参数
        modelData: any = {
            show: false,
            title: "",
            columns: [{
                    title: "对象号",
                    key: "objId",
                },
                {
                    title: "对象名称",
                    key: "objectName",
                },
                {
                    title: "所属场站",
                    key: "stationName",
                },
                {
                    title: "告警级别",
                    key: "levelName",
                },
                {
                    title: "告警类型",
                    key: "alarmType",
                    render: (h: any, params: any) => {
                        return h("div", params.row.alarmType.name);
                    },
                },
                {
                    title: "描述",
                    key: "description",
                },
                {
                    title: "时间",
                    key: "time",
                    render: (h: any, params: any) => {
                        return h(
                            "div",
                            new Date(params.row.timeStamp).format(
                                "yyyy-MM-dd hh:mm:ss"
                            )
                        );
                    },
                },
            ],
            alarms: [],
            cleaned: null,
            page: {
                total: 0,
                pageSize: 10,
                current: 1,
            },
        }

        private get uncleanedNum() {
            return this.totalNum - this.cleanedNum
        }

        mounted() {
            this.initData()
        }

        initData() {
            // 获取所有的告警数量
            getCountByCondition({}).then((res: any) => {
                if (res.data.code != 200) return

                this.totalNum = res.data.data
            })

            getCountByCondition({
                cleaned: true
            }).then((res: any) => {
                if (res.data.code != 200) return
                this.cleanedNum = res.data.data
            })
        }

        clickDiv(cleaned: boolean | null) {
            console.log("cleaned 状态", cleaned)
            this.modelData.show = true;
            this.modelData.title = cleaned == null ? "所有告警详情" : (cleaned ? "所有清除告警详情" : "所有未清除告警详情")
            this.modelData.alarms = [];
            this.modelData.page = {
                total: 0,
                pageSize: 10,
                current: 1,
            };
            this.modelData.cleaned = cleaned
            this.searchData()
        }

        currentChange(value: number) {
            this.modelData.page.current = value;
            this.searchData();
        }

        pageSizeChange(value: number) {
            this.modelData.page.pageSize = value;
            this.searchData();
        }

        searchData() {
            const data: AlarmVo = {
                pageNum: this.modelData.page.current,
                pageSize: this.modelData.page.pageSize,
                cleaned: this.modelData.cleaned,
            };
            getPageAlarmsByCondition(data).then((res: any) => {
                let {
                    code,
                    data
                } = res.data;
                if (code == 200 && data) {
                    this.modelData.alarms = data.content;
                    this.modelData.page.total = data.totalSize;
                }
            });
        }
    }
</script>

<style lang="less">
    .event_statistics {
        height: 100%;

        h4 {
            line-height: 60px;
            font-size: 22px;
            color: #f9ffff;
            font-weight: 500;
            padding-left: 61px;

            .icon {
                display: block;
                width: 22px;
                height: 24px;
                position: absolute;
                left: 17px;
                top: 18px;
                background: url("../../../assets/images/event.png") no-repeat;
                background-size: 100% 100%;
            }
        }

        .event_statistics_content {
            height: 230px;
            display: flex;
            justify-content: space-around;

            .content_item {
                margin-top: 20px;
                height: 120px;
                width: 120px;
                position: relative;
                cursor: pointer;

                p {
                    text-align: center;
                    font-size: 12px;
                    color: #fff;
                    position: absolute;
                    left: 50%;
                    top: 50%;
                    transform: translate(-50%, -50%);
                }
            }

            .total {
                background: url("../../../assets/images/total.png") no-repeat center;
                background-size: cover;
            }

            .handled {
                background: url("../../../assets/images/handled.png") no-repeat center;
                background-size: cover;
            }

            .unprocessed {
                background: url("../../../assets/images/unprocessed.png") no-repeat center;
                background-size: cover;
            }
        }
    }
</style>