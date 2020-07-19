<template>
    <div class="overview_content">
        <div class="col">
            <div class="drag_panel">
                <EquipmentCondition :ref="equipmentCondition"></EquipmentCondition>
            </div>
            <div class="drag_panel">
                <NumberOfAlarms :ref="numberOfAlarms"></NumberOfAlarms>
            </div>
            <div class="drag_panel">
                <RealTimeAlarm :ref="realTimeAlarm"></RealTimeAlarm>
            </div>
        </div>
        <div class="col">
            <div class="model">
                <Unity3D></Unity3D>
            </div>
            <div class="history_alerm">
                <HistoryAlerm></HistoryAlerm>
            </div>
        </div>
        <div class="col">
            <div class="drag_panel">

                <HIKVideo ref="video1" id="overview_id1"></HIKVideo>
                <!-- <ProductionAreaEast></ProductionAreaEast> -->
            </div>
            <div class="drag_panel">

                <HIKVideo ref="video2" id="overview_id2"></HIKVideo>
                <!-- <ProductionAreaWest></ProductionAreaWest> -->
            </div>
            <div class="drag_panel">

                <HIKVideo ref="video3" id="overview_id3"></HIKVideo>
                <!-- <ProductionAreaCenter></ProductionAreaCenter> -->
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue
    } from "vue-property-decorator";
    import EquipmentCondition from "@/views/Stations/overview/EquipmentCondition.vue";
    import NumberOfAlarms from "@/views/Stations/overview/NumberOfAlarms.vue";
    import RealTimeAlarm from "@/views/Stations/overview/RealTimeAlarm.vue";
    import HistoryAlerm from "@/views/Stations/overview/HistoryAlerm.vue";
    // import ProductionAreaEast from "@/views/Stations/overview/ProductionAreaEast.vue";
    // import ProductionAreaWest from "@/views/Stations/overview/ProductionAreaWest.vue";
    // import ProductionAreaCenter from "@/views/Stations/overview/ProductionAreaCenter.vue";
    import Unity3D from "@/components/view/Unity3d.vue";
    import HIKVideo from "@/components/Video/HIK_old.vue";
    import {
        getVideoDtosByStation
    } from '@/api/video'
    import {
        Video
    } from '../../../types/video.interface';

    @Component({
        components: {
            EquipmentCondition,
            NumberOfAlarms,
            RealTimeAlarm,
            HistoryAlerm,
            // ProductionAreaEast,
            // ProductionAreaWest,
            // ProductionAreaCenter,
            Unity3D,
            HIKVideo,
        },
    })
    export default class Overview extends Vue {
        stationId: number = 0
        videos: Video[] = []

        mounted() {
            this.stationId = this.$store.state.stationId;
            console.log("this", this)
            this.initData()
        }

        initData() {

            // let realTimeAlarm: any = this.$refs.realTimeAlarm;
            // realTimeAlarm.initData();

            // let equipmentCondition: any = this.$refs.equipmentCondition;
            // equipmentCondition.initData();

            // let numberOfAlarms: any = this.$refs.numberOfAlarms;
            // numberOfAlarms.initData();

            getVideoDtosByStation(this.stationId).then((res: any) => {
                if (res.data.code != 200) return
                this.videos = res.data.data

                console.log("overview", this.videos)

                if (this.videos.length == 0) return
                let v1: any = this.$refs.video1
                console.log("执行1", v1)
                v1.PreviewStart(this.videos[0])

                if (this.videos.length == 1) return
                let v2: any = this.$refs.video2
                v2.PreviewStart(this.videos[1])

                if (this.videos.length == 2) return
                let v3: any = this.$refs.video3
                v3.PreviewStart(this.videos[2])
            })
        }
    }
</script>

<style lang="less" scoped>
    .overview_content {
        display: flex;
        height: 100%;

        .col {
            flex: 3;
            height: 80%;

            &:nth-child(2) {
                flex: 5;
            }

            .drag_panel {
                width: 429px;
                // height: 225px;
                height: 32%;
                background: rgba(13, 24, 54, 1 0.1);
                margin: 0 auto 10px;
                background-image: url("../../../assets/images/frame.png");
                background-size: 100% 100%;
            }

            .model {
                width: 815px;
                // height: 460px;
                height: 65%;
                background-color: #ccc;
                margin: 0 auto;
            }

            .history_alerm {
                width: 815px;
                // height: 218px;
                height: 57%;
                margin: 10px auto 0;
            }
        }
    }
</style>