<template>
    <div class="AlarmImageClass">
        <div v-if="imagePaths.length == 0">告警{{alarm.id}}无图片</div>
        <image-from-path v-else-if="imagePaths.length == 1" :path="imagePaths[0]">
        </image-from-path>
        <el-carousel style="height:100%;width:100%;" v-else>
            <el-carousel-item v-for="item in imagePaths" :key="item">
                <image-from-path :path="item"></image-from-path>
            </el-carousel-item>
        </el-carousel>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue,
        Prop,
        Watch
    } from "vue-property-decorator"
    import ImageFromPath from '@/components/Image/ImageFromPath.vue'
    import {
        Alarm
    } from "@/types/alarm.interface"
    import {
        getImagePathOfAlarm
    } from '@/api/alarm'


    @Component({
        components: {
            ImageFromPath
        }
    })
    export default class AlarmImage extends Vue {
        @Prop({
            required: true,
            type: Object
        }) alarm: Alarm

        @Watch('alarm', {
            deep: true
        })
        onAlarmChange(val: any, oldVal: any) {
            this.initData()
        }

        imagePaths: string[] = []

        mounted() {
            this.initData()
        }

        initData() {
            if (!this.alarm || this.alarm.id == 0) return

            getImagePathOfAlarm(this.alarm.id).then((res: any) => {
                if (res.data.code != 200) return

                this.imagePaths = res.data.data
            })
        }
    }
</script>

<style lang="less">
    .AlarmImageClass {
        height: 100%;
        width: 100%;
        .el-carousel,.el-carousel--horizontal{
            .el-carousel__container {
                height: 100% !important;
            }
        }
    }
</style>