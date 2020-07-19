<template>
    <div class="admission_personnel">
        <!-- <div class="certification_img"></div>
        <div class="certification_text">
            <span class="tit">认证成功√</span>
            <p>姓名:董杉</p>
            <p>职务:巡检员</p>
        </div> -->
        <ul class="admission_box">
            <li
                class="admission_img"
                v-for="item in admissionImgList"
                :key="item.src"
            >
                <img :src="item.src" alt="" />
            </li>
        </ul>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { getAdmissionPersonnel } from "@/api/videointercom.ts";
@Component({})
export default class admissionPersonnel extends Vue {
    admissionImgList: Array<any> = [];
    mounted() {
        this.initData();
    }

    initData() {
        getAdmissionPersonnel()
            .then((res: any) => {
                let { code, data } = res.data;
                if (code != 200) return;

                this.admissionImgList = data;
            })
            .finally(() => {
                this.admissionImgList = [
                    {
                        src: "",
                    },
                    {
                        src: "",
                    },
                ];
            });
    }
}
</script>

<style lang="less">
.admission_personnel {
    .admission_box {
        height: 100%;
        display: flex;
        flex-wrap: wrap;
        justify-content: space-around;
        .admission_img {
            list-style: none;
            background-color: #ccc;
            width: 146px;
            height: 150px;
            img {
                width: 100%;
                height: 100%;
            }
        }
    }
    // .certification_img {
    //     width: 163px;
    //     height: 182px;
    //     margin: 0 auto;
    //     background-color: #ccc;
    // }
    // .certification_text {
    //     width: 200px;
    //     height: 118px;
    //     border: 2px solid #36e5ff;
    //     border-radius: 10px;
    //     margin: 20px auto 0;
    //     .tit {
    //         display: block;
    //         width: 139px;
    //         line-height: 42px;
    //         background-color: #348d9d;
    //         border-radius: 0px 0px 21px 21px;
    //         margin: 0 auto;
    //         font-size: 22px;
    //         text-align: center;
    //     }
    //     p {
    //         font-size: 16px;
    //         line-height: 32px;
    //         text-align: center;
    //     }
    // }
}
</style>
