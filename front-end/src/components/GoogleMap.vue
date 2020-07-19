<template>
    <div class="gmap-wrap">
       <div :id="map_canvas_id"></div>
    </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator'
import { CoordMapType, LocalMapType} from '@/util/googlemap'
import { getStations } from '@/api/station'
import $ from 'jquery'

@Component({})
export default class Map extends Vue {
    
    map_canvas_id: string = 'map_canvas'
    window: any = window
    stations: any[] = [
        {
            id: 1,
            longitude: 31.23070,
            latitude: 121.47471,
            content: '场站1',
            alarms: [
                {
                    time: '04-29 10:08:30',
                    alarmLevel: '一般',
                    name: '温度',
                    location: '实验路18区'
                },
                {
                    time: '04-29 10:18:30',
                    alarmLevel: '一般',
                    name: '湿度',
                    location: '纬三路22区'
                }
            ]
        },
        {
            id: 2,
            longitude: 32.24082,
            latitude: 118.48500,
            content: '场站2',
            alarms: [
                 {
                    time: '04-29 10:08:30',
                    alarmLevel: '一般',
                    name: '温度',
                    location: '实验路3区'
                },
                {
                    time: '04-30 10:18:30',
                    alarmLevel: '一般',
                    name: '湿度',
                    location: '纬三路22区'
                }
            ]
        },
        {
            id: 3,
            longitude: 31.22082,
            latitude: 119.46500,
            content: '场站3',
            alarms: [
                 {
                    time: '04-29 10:08:30',
                    alarmLevel: '一般',
                    name: '温度',
                    location: '纬三路18区'
                },
                {
                    time: '05-01 10:18:30',
                    alarmLevel: '一般',
                    name: '湿度',
                    location: '纬三路22区'
                }
            ]
        }
    ]
    markers: any[] = []
    localMapType: any = null
    map: any = null

    mounted() {
        this.getStationInfo()
    }

    getStationInfo(){
        getStations().then((res: any) => {
            let {
                code,
                data
            } = res.data
            if (code == 200) {
                this.stations = data
                this.stations.forEach((station: any) => {
                    station.alarms = [
                         {
                            time: '04-29 10:08:30',
                            alarmLevel: '一般',
                            name: '温度',
                            location: '纬三路18区'
                        },
                        {
                            time: '05-01 10:18:30',
                            alarmLevel: '一般',
                            name: '湿度',
                            location: '纬三路22区'
                        }
                    ]
                })
                this.initialize()
            }
        })
    }

    initialize() { 
        let myLatlng = new google.maps.LatLng(31.23070,121.47471);
        // 地图配置初始化
        let mapOptions = {
            zoom: 5,
            center: myLatlng,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            scaleControl: true
        }

        this.map = new google.maps.Map(document.getElementById(this.map_canvas_id), mapOptions);
        // 加载本地离线地图
        this.localMapType = new LocalMapType()
        this.map.mapTypes.set('local', this.localMapType);
        this.map.setMapTypeId('local');
        this.map.overlayMapTypes.insertAt(0, new CoordMapType(new google.maps.Size(256, 256)));

        this.stations.map((station: any) => {
            let [ latitude, longitude ] = station.position.split(',')
            // 在地图上添加定位标签
            let marker =  new google.maps.Marker({
                position: new google.maps.LatLng(longitude, latitude),
                map: this.map,
                title: station.name
            });
            this.setInfoWindowStyle(marker, station)
            this.markers.push(marker)
        })
    }

    setInfoWindowStyle(marker: any, item: any){
        let alarms = ''
        item.alarms.map((alarm: any) => {
            alarms += '<p>' + alarm.time + '  ' + alarm.name + ' ' + alarm.location + ' ' + alarm.alarmLevel + '</p>'
        })

        let content = '<div id="iw-container">' +
            '<div class="iw-title">' + item.name + '</div>' +
            '<div class="iw-content">' +
            alarms +
            '</div>';

        let infowindow = new google.maps.InfoWindow({
            content: content,
            maxWidth: 350
        });
        google.maps.event.addListener(infowindow, 'domready', () => {
            let iwOuter = $('.gm-style-iw');
            let iwBackground = iwOuter.prev();
            iwBackground.children(':nth-child(2)').css({
                'display': 'none'
            });
            iwBackground.children(':nth-child(4)').css({
                'display': 'none'
            });
            iwOuter.parent().parent().css({
                left: '115px'
            });
            iwBackground.children(':nth-child(1)').attr('style', function (i, s) {
                return s + 'left: 76px !important;'
            });
            iwBackground.children(':nth-child(3)').attr('style', function (i, s) {
                return s + 'left: 76px !important;'
            });
            iwBackground.children(':nth-child(3)').find('div').children().css({
                'box-shadow': 'rgba(72, 181, 233, 0.6) 0px 1px 6px',
                'z-index': '1'
            });
            let iwCloseBtn = iwOuter.next();
            iwCloseBtn.css({
                opacity: '1',
                right: '38px',
                top: '3px',
                border: '7px solid #48b5e9',
                'border-radius': '13px',
                'box-shadow': '0 0 5px #3990B9'
            });
        });
        google.maps.event.addListener(marker, 'click', () => {
            infowindow.open(this.map,marker);
        });
    }
}
</script>

<style lang="less">
    .gmap-wrap {
        width: 100%;
        height: 100%;
        position: relative;
    }
    #map_canvas {
        left:0;
        top:0;
        width:100%;
        height:100%;
        position:absolute;
    }
    .alarms{
        font-size: 14px;
    }
    /* Custom Info Window code starts from here */
    .gm-style-iw
    {
        width: 350px !important;
        top: 15px !important;
        left: 0px !important;
        background-color: #fff;
        box-shadow: 0 1px 6px rgba(178, 178, 178, 0.6);
        border: 1px solid rgba(72, 181, 233, 0.6);
        border-radius: 2px 2px 10px 10px;
    }
    #iw-container
    {
        margin-bottom: 10px;
        .iw-title {
            font-family: 'Open Sans Condensed', sans-serif;
            font-size: 22px;
            font-weight: 400;
            padding: 8px;
            background-color: #48b5e9;
            color: white;
            margin: 0 auto;
            border-radius: 2px 2px 0 0;
            width: 94%;
        }

        .iw-content {
            font-size: 13px;
            line-height: 18px;
            font-weight: 400;
            margin-right: 1px;
            padding: 15px 5px 20px 15px;
            max-height: 140px;
            overflow-y: auto;
            overflow-x: hidden;

            &::-webkit-scrollbar{
                width: 4px;
                height: 2px;
            }
            &::-webkit-scrollbar-thumb {
                border-radius: 1vmin;
                box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
                background: #83a6ed;
            }
            &::-webkit-scrollbar-track {
                box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
                border-radius: 1vmin;
                background: #ededed;
            }
        }
    }
</style>
