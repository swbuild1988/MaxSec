<template>
    <div>
        <Row>
            <Col span="6">
            <div class="items">1</div>
            <!-- 网络图模块 -->
            <div class="view-graph-chart-wrap">
                <GraphChart></GraphChart>
            </div>
            </Col>
            <Col span="12">
            <div class="items map-wrap">
                <!-- <google-map></google-map> -->
            </div>
            </Col>
            <Col span="6">
            <div class="items" style="position: relative;">
                <unity3d></unity3d>
            </div>
            <div class="items">5</div>
            </Col>
            <Col span="9">
            <div class="items bottom-wrap">6</div>
            </Col>
            <Col span="9">
            <div class="items bottom-wrap">7</div>
            </Col>
        </Row>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue
    } from 'vue-property-decorator'
    import GraphChart from '@/views/Main/View_Graph.vue'
    // import GoogleMap from '@/components/GoogleMap.vue'
    import Unity3d from '@/components/view/Unity3d.vue'

    @Component({
        components: {
            GraphChart,
            // GoogleMap,
            Unity3d
        }
    })
    export default class ViewClass extends Vue {
        mounted() {}

    }
</script>

<style scoped>
    .items {
        border: 1px dotted black;
        height: 260px;
    }

    .map-wrap {
        height: 520px;
    }

    .bottom-wrap {
        height: 320px
    }

    .view-graph-chart-wrap {
        /* width: 600px; */
        height: 580px;
        border: 1px solid #000;
    }
</style>