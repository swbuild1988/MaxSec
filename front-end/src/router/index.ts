import Vue from "vue";
import VueRouter from "vue-router";
// 登录页面
const Login = () => import('@/views/Login.vue')

/** 可视化 */
const Test = () => import('@/views/Main/View.vue')
const Visualized = () => import('@/views/Main/visualized/Visualized.vue')
// 场站页面
const MainStations = () => import('@/views/Stations/MainStations.vue')
const Overview = () => import('@/views/Stations/overview/Overview.vue')
const IndustrialTV = () => import('@/views/Stations/IndustrialTV.vue')
const HighConsequenceVideo = () => import('@/views/Stations/HighConsequenceVideo.vue')
const FaceRecognition = () => import('@/views/Stations/FaceRecognition.vue')
const VideoIntercom = () => import('@/views/Stations/VideoIntercom.vue')
const PerimeterAlarm = () => import('@/views/Stations/perimeteralarm/PerimeterAlarm.vue')
const FireAlarm = () => import('@/views/Stations/firealarm/FireAlarm.vue')

/** 后台配置页面 */
const ConfigreMain = () => import('@/views/Configration/MainPage.vue')
const User = () => import('@/views/Configration/User.vue')
const Role = () => import('@/views/Configration/Role.vue')
const Permission = () => import('@/views/Configration/Permission.vue')
const Management = () => import('@/views/Configration/Management.vue')
const Station = () => import('@/views/Configration/Station.vue')
const StationLink = () => import('@/views/Configration/StationLink.vue')
const MeasObject = () => import('@/views/Configration/MeasObject.vue')
const Equipment = () => import('@/views/Configration/Equipment.vue')
const Video = () => import('@/views/Configration/Video.vue')
const VideoServer = () => import('@/views/Configration/VideoServer.vue')
const AlarmType = () => import('@/views/Configration/AlarmType.vue')
const NetNode = () => import('@/views/Configration/NetNode.vue')
const GeoMap = () => import('@/views/Configration/GeoMap.vue')


Vue.use(VueRouter);

// 跳转当前路由时，不显示告警
const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(to: any) {
    return (VueRouterPush.call(this, to) as any).catch((err: any) => err)
}

const routes = [{
        path: "/login",
        name: "login",
        component: Login
    },
    {
        path: '/',
        redirect: '/main'
    },
    // 可视化路由
    {
        path: '/main',
        name: 'main',
        component: Visualized
    },
    // 场站路由
    {
        path: '/stations',
        name: 'Stations',
        redirect: '/stations/overview',
        component: MainStations,
        children: [{
                path: 'overview',
                name: 'Overview',
                component: Overview
            },
            {
                path: 'industrial-tv',
                name: 'industrialTV',
                component: IndustrialTV
            },
            {
                path: 'high-consequence-video',
                name: 'HighConsequenceVideo',
                component: HighConsequenceVideo
            },
            {
                path: 'face-recognition',
                name: 'FaceRecognition',
                component: FaceRecognition
            },
            {
                path: 'video-intercom',
                name: 'VideoIntercom',
                component: VideoIntercom
            },
            {
                path: 'perimeter-alarm',
                name: 'PerimeterAlarm',
                component: PerimeterAlarm
            },
            {
                path: 'fire-alarm',
                name: 'FireAlarm',
                component: FireAlarm
            },
        ]
    },
    // 后台
    {
        path: "/config",
        name: 'config',
        redirect: '/config/user',
        component: ConfigreMain,
        children: [{
                path: 'user',
                name: 'user',
                component: User,
            },
            {
                path: 'role',
                name: 'role',
                component: Role,
            },
            {
                path: 'permission',
                name: 'permission',
                component: Permission,
            },
            {
                path: 'management',
                name: 'management',
                component: Management,
            },
            {
                path: 'station',
                name: 'station',
                component: Station,
            },
            {
                path: 'stationlink',
                name: 'stationlink',
                component: StationLink,
            },
            {
                path: 'measobject',
                name: 'measobject',
                component: MeasObject,
            },
            {
                path: 'equipment',
                name: 'equipment',
                component: Equipment,
            },
            {
                path: 'video',
                name: 'video',
                component: Video,
            },
            {
                path: 'videoserver',
                name: 'videoserver',
                component: VideoServer,
            },
            {
                path: 'alarmtype',
                name: 'alarmtype',
                component: AlarmType,
            },
            {
                path: 'netnode',
                name: 'netnode',
                component: NetNode,
            },
            {
                path: 'geomap',
                name: 'geomap',
                component: GeoMap,
            }
        ]
    },
    // 测试
    {
        path: '/test',
        name: 'test',
        component: Test
    },
]

const router = new VueRouter({
    mode: "history",
    base: process.env.BASE_URL,
    routes
})

export default router;