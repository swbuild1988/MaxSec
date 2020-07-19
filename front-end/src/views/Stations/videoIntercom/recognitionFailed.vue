<template>
    <div class="recognition_failed">
        <ul class="failed_box">
            <li
                class="failed_img"
                v-for="item in failedImgList"
                :key="item.src"
            >
                <img :src="item.src" alt="" />
            </li>
        </ul>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { getRecognitionFailed } from "@/api/videointercom.ts";

@Component({})
export default class recognitionFailed extends Vue {
    failedImgList: Array<any> = [];

    mounted() {
        this.initData();
    }

    initData() {
        getRecognitionFailed()
            .then((res: any) => {
                let { code, data } = res.data;
                if (code != 200) return;
                this.failedImgList = data;
            })
            .finally(() => {
                this.failedImgList = [{ src: "" }, { src: "" }];
            });
    }
}
</script>

<style lang="less">
.recognition_failed {
    height: 100%;
    .failed_box {
        height: 100%;
        display: flex;
        flex-wrap: wrap;
        justify-content: space-around;
        .failed_img {
            list-style: none;
            background-color: #ccc;
            width: 146px;
            height: 150px;
        }
    }
}
</style>
