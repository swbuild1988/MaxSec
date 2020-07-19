<template>
    <div :id="id" class="WebPlayer"></div>
</template>
<script lang="ts">
    import {
        Component,
        Vue,
        Prop,
        Watch
    } from 'vue-property-decorator'

    @Component({})
    export default class Unity3d_WebGL extends Vue {

        id = "unityContainer"

        /** 待加载的模型 */
        @Prop({
            type: String,
            required: true,
            default: ''
        })
        modelName: String

        // 实例
        unityInstance: any

        @Watch('modelName')
        onModelNameChange(val: string, oldVal: string) {
            this.loadUnity()
        }

        mounted() {
            this.importJS()
            this.loadUnity()
        }

        // 载入需要的js文件
        importJS() {
            const s1 = document.createElement('script');
            s1.type = 'text/javascript';
            s1.src = '/unity3d/jquery.min.js';
            document.body.appendChild(s1);
            const s2 = document.createElement('script');
            s2.type = 'text/javascript';
            s2.src = '/unity3d/UnityObject2.js';
            document.body.appendChild(s2);
        }

        loadUnity() {
            if (this.modelName == '') return

            console.log(`Player加载模型：${this.modelName}`)

            let config: any = {
                width: document.getElementById(this.id).offsetWidth,
                height: document.getElementById(this.id).offsetHeight,
                params: {
                    enableDebugging: "0"
                }
            }

            this.unityInstance = new UnityObject2(config);
            let _this = this
            this.unityInstance.observeProgress(function (progress) {
                console.log("progress", progress)
                switch (progress.pluginStatus) {
                    case "broken":
                        break;
                    case "missing":
                        break;
                    case "installed":
                        break;
                    case "first":
                        _this.completeUnity3DBox();
                        break;
                }
            });

            this.unityInstance.initPlugin(document.getElementById(this.id),
                encodeURI(`/unity3d/WebPlayer/${this.modelName}/model.unity3d`))
        }

        completeUnity3DBox() {

        }

        refreshAlarmStatus(newAlarm: any) {
            this.unityInstance.getUnity().SendMessage("MainUI", "AddAlarm", JSON.stringify(newAlarm))
        }

        refreshData(id: number, cv: number) {
            this.unityInstance.getUnity().SendMessage("MainUI", "UpdateCV", `[{'id': ${id}, 'cv': ${cv} }]`)
        }
    }
</script>
<style lang="less" scoped>
    .WebPlayer {
        width: 100%;
        height: 100%;
    }
</style>