<template>
    <img style="width:100%; height:100%;" :src="imgSrc" v-if="imgShowFlag">
</template>

<script lang="ts">
    import request from '@/util/request'
    import {
        Component,
        Vue,
        Prop,
        Watch
    } from "vue-property-decorator"

    @Component({})
    export default class ImageFromPath extends Vue {

        @Prop({
            required: true,
            type: String
        }) path: string

        @Watch('path')
        onPathChange(val: any, oldVal: any) {
            this.loadImage()
        }

        imgSrc: string = ''
        imgShowFlag: boolean = false
        /** 后端访问地址 */
        url: string = '/file'

        mounted() {
            this.loadImage();
        }

        loadImage() {
            let _this = this
            if (this.path.length > 0) {
                request({
                    url: this.url,
                    method: 'post',
                    data: {
                        path: this.path
                    },
                    responseType: 'arraybuffer'
                }).then((response: any) => {

                    _this.imgSrc = "data:image/jpeg;base64," + btoa(new Uint8Array(response.data)
                        .reduce((data, byte) => data + String.fromCharCode(byte), ""))
                    _this.imgShowFlag = true

                })
            }

        }

    };
</script>

<style scoped>
    
</style>