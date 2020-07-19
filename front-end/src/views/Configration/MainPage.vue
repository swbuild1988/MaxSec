<template>
    <div class="layout">
        <Layout style="height:100%">
            <Header style="height: 64px;">
                <Menu mode="horizontal" theme="dark" active-name="1">
                    <div class="layout-logo">场站后台管理系统</div>
                    <div class="layout-nav">
                        <MenuItem name="3">
                        <Icon type="ios-contact-outline" />
                        {{UserName}}
                        </MenuItem>
                        <MenuItem name="4" @click.native="logout">
                        <Icon type="ios-log-out" />
                        登出
                        </MenuItem>
                    </div>
                </Menu>
            </Header>
            <Layout class="comment">
                <Sider hide-trigger>
                    <Menu theme="dark" width="auto" accordion :open-names="['1']" @on-select="itemSelect">
                        <Submenu name="1">
                            <template slot="title">
                                <Icon type="ios-navigate" />
                                基础设置
                            </template>
                            <MenuItem name="user" v-permission="['user:list']">
                            <Icon type="ios-navigate" />
                            用户管理
                            </MenuItem>
                            <MenuItem name="role" v-permission="['role:list']">
                            <Icon type="ios-navigate" />
                            角色管理
                            </MenuItem>
                            <MenuItem name="permission" v-permission="['permission:list']">
                            <Icon type="ios-navigate" />
                            权限管理
                            </MenuItem>
                        </Submenu>
                        <Submenu name="2">
                            <template slot="title">
                                <Icon type="ios-navigate" />
                                管理结构
                            </template>
                            <MenuItem name="management">
                            <Icon type="ios-navigate" />
                            部门管理
                            </MenuItem>
                            <MenuItem name="station">
                            <Icon type="ios-navigate" />
                            场站管理
                            </MenuItem>
                            <MenuItem name="stationlink">
                            <Icon type="ios-navigate" />
                            场站关联
                            </MenuItem>
                        </Submenu>
                        <Submenu name="3">
                            <template slot="title">
                                <Icon type="ios-navigate" />
                                监测设置
                            </template>
                            <MenuItem name="equipment">
                            <Icon type="ios-navigate" />
                            设备管理
                            </MenuItem>
                            <MenuItem name="measobject">
                            <Icon type="ios-navigate" />
                            对象管理
                            </MenuItem>
                            <MenuItem name="video">
                            <Icon type="ios-navigate" />
                            视频管理
                            </MenuItem>
                            <MenuItem name="videoserver">
                            <Icon type="ios-navigate" />
                            视频服务
                            </MenuItem>
                            <MenuItem name="alarmtype">
                            <Icon type="ios-navigate" />
                            告警类型
                            </MenuItem>
                            <!-- <MenuItem name="netnode">
                            <Icon type="ios-navigate" />
                            网络节点管理
                            </MenuItem> -->
                        </Submenu>
                        <Submenu name="4">
                            <template slot="title">
                                <Icon type="ios-navigate" />
                                系统监控
                            </template>
                            <MenuItem name="4-1">
                            <Icon type="ios-navigate" />
                            服务器监控
                            </MenuItem>
                            <MenuItem name="4-2">
                            <Icon type="ios-navigate" />
                            异常日志监控
                            </MenuItem>
                            <MenuItem name="4-3">
                            <Icon type="ios-navigate" />
                            告警查看
                            </MenuItem>
                        </Submenu>
                        <Submenu name="5">
                            <template slot="title">
                                <Icon type="ios-navigate" />
                                系统工具
                            </template>
                            <MenuItem name="geomap">
                            <Icon type="ios-navigate" />
                            地图设置
                            </MenuItem>
                        </Submenu>
                    </Menu>
                </Sider>
                <Layout style="top: 64px; bottom: 0;">
                    <Content :style="{padding: '24px', minHeight: '680px', background: '#fff'}">
                        <router-view></router-view>
                    </Content>
                </Layout>
            </Layout>
        </Layout>
        </Layout>
        </Layout>
    </div>
</template>
<script lang="ts">
    import {
        Component,
        Vue
    } from "vue-property-decorator";
    import router from "@/router";

    @Component({})
    export default class Main extends Vue {
        get UserName(): string {
            return this.$store.state.userName;
        }

        itemSelect(routerName: string) {
            let param: any = {
                name: routerName,
            };
            this.$router.push(param);
        }

        jump() {
            let param: any = {
                name: "mainpage",
            };
            this.$router.push(param);
        }

        logout() {
            this.$store.commit("removeParamter");
            this.$router.push({
                path: "/login",
            });
        }
    }
</script>
<style scoped>
    .layout {
        border: 1px solid #d7dde4;
        background: #f5f7f9;
        position: absolute;
        border-radius: 4px;
        overflow: hidden;
        width: 100%;
        height: 100%;
        height: 100%;
        width: 100%;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
    }

    .layout-logo {
        color: #AE9DCF;
        font-size: 30px;
        font-weight: bold;
        float: left;
        position: relative;
        text-align: center;
    }

    .layout-nav {
        margin: 0 auto;
        margin-right: 10px;
        position: relative;
        float: right;
    }
</style>