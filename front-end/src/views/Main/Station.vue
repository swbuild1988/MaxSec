<template>
    <div>
        {{ station.name }}111
        <div class="station-graph-chart-wrap">
            <GraphChart :station="station"></GraphChart>
        </div>
    </div>
</template>
<script lang="ts">
    import {
        Component,
        Vue,
        Watch
    } from "vue-property-decorator";
    import {
        Station
    } from "@/types/station.interface";
    import {
        getStations
    } from "@/api/station";
    import GraphChart from "@/views/Main/Station_Graph.vue";

    @Component({
        components: {
            GraphChart,
        },
    })
    export default class StationClass extends Vue {
        station: Station = this.getDefaultItem()

        mounted() {
            this.initData();
        }

        initData() {
            getStations().then((res: any) => {
                let {
                    code,
                    data
                } = res.data;
                if (code == 200) {
                    for (let index = 0; index < data.length; index++) {
                        if (data[index].id == Number(this.$route.params.id)) {
                            this.station = data[index];
                            break;
                        }
                    }
                }
            });
        }
        
        getDefaultItem(): Station {
            return {
                id: 0,
                name: '',
                position: '',
                managementId: 0,
                managementName: '',
                icon: 1,
                createTime: new Date()
            }
        }
    }
</script>

<style scoped>
    .station-graph-chart-wrap {
        width: 800px;
        height: 600px;
        border: 1px solid #000;
    }
</style>