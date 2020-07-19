<template>
    <div id="unityContainer"></div>
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

        importJS() {
            const s = document.createElement('script');
            s.type = 'text/javascript';
            s.src = '/unity3d/UnityLoader.js';
            document.body.appendChild(s);
        }

        loadUnity() {
            if (this.modelName == '') return

            console.log(`GL加载模型：${this.modelName}`)

            this.unityInstance = UnityLoader.instantiate("unityContainer",
                `/unity3d/WebGL/${this.modelName}/model.json`, {
                    onProgress: this.unityProgress
                });
        }

        unityProgress(unityInstance: any, progress: any) {
            if (!unityInstance.Module) return;
            if (!unityInstance.logo) {
                unityInstance.logo = document.createElement("div");
                unityInstance.logo.className = "logo " + unityInstance.Module.splashScreenStyle;
                unityInstance.container.appendChild(unityInstance.logo);
            }
            if (!unityInstance.progress) {
                unityInstance.progress = document.createElement("div");
                unityInstance.progress.className = "progress " + unityInstance.Module.splashScreenStyle;
                unityInstance.progress.empty = document.createElement("div");
                unityInstance.progress.empty.className = "empty";
                unityInstance.progress.appendChild(unityInstance.progress.empty);
                unityInstance.progress.full = document.createElement("div");
                unityInstance.progress.full.className = "full";
                unityInstance.progress.appendChild(unityInstance.progress.full);
                unityInstance.container.appendChild(unityInstance.progress);
            }
            unityInstance.progress.full.style.width = (100 * progress) + "%";
            unityInstance.progress.empty.style.width = (100 * (1 - progress)) + "%";
            if (progress == 1)
                unityInstance.logo.style.display = unityInstance.progress.style.display = "none";
        }

        refreshAlarmStatus(newAlarm: any) {
            this.unityInstance.SendMessage("MainUI", "AddAlarm", JSON.stringify(newAlarm))
        }

        refreshData(id: number, cv: number) {
            this.unityInstance.SendMessage("MainUI", "UpdateCV", `[{'id': ${id}, 'cv': ${cv} }]`)
        }
    }
</script>
<style lang="less" scoped>
    #unityContainer {
        width: 100%;
        height: 100%;
    }
</style>