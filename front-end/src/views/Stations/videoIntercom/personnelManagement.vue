<template>
    <table class="personnel_management">
        <tr>
            <td>部门</td>
            <td>工号</td>
            <td>姓名</td>
            <td>合法区域</td>
        </tr>
        <tr v-for="item in personnelManagementList" :key="item.jobNum">
            <td>{{ item.part }}</td>
            <td>{{ item.jobNum }}</td>
            <td>{{ item.name }}</td>
            <td>{{ item.legalArea }}</td>
        </tr>
    </table>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { getPersonnelManagement } from "@/api/videointercom.ts";
@Component({})
export default class personnelManagement extends Vue {
    personnelManagementList: Array<any> = [];

    mounted() {
        this.initData();
    }

    initData() {
        getPersonnelManagement()
            .then((res: any) => {
                let { code, data } = res.data;
                if (code != 200) return;
                this.personnelManagementList = data;
            })
            .finally(() => {
                this.personnelManagementList = [
                    {
                        part: "运检部",
                        jobNum: "001",
                        name: "王二小",
                        legalArea: "生活区",
                    },
                    {
                        part: "运检部",
                        jobNum: "001",
                        name: "王二小",
                        legalArea: "生活区",
                    },
                    {
                        part: "运检部",
                        jobNum: "001",
                        name: "王二小",
                        legalArea: "生活区",
                    },
                    {
                        part: "运检部",
                        jobNum: "001",
                        name: "王二小",
                        legalArea: "生活区",
                    },
                ];
            });
    }
}
</script>

<style lang="less">
.personnel_management {
    width: 100%;
    // height: 100%;
    tr {
        td {
            text-align: center;
            font-size: 14px;
            line-height: 30px;
        }
    }
}
</style>
