<template>
  <div class="layout">
    <Layout style="height:100%">
      <Header style="height: 64px;">
        <Menu mode="horizontal" theme="dark" active-name="1">
          <div class="layout-logo">adafdsfadfad</div>
          <div class="layout-nav">
            <MenuItem name="1" @click.native="viewJump">
              <Icon type="ios-navigate"></Icon>
              可视化
            </MenuItem>
            <Submenu name="2">
              <template slot="title">
                <Icon type="ios-keypad" />
                场站
              </template>
              <MenuItem
                v-for="(item, index) in stations"
                :key="item.id"
                @click.native="stationJump(index)"
                >{{ item.name }}</MenuItem
              >
            </Submenu>
            <MenuItem name="3" @click.native="jump">
              <Icon type="ios-analytics"></Icon>
              后台
            </MenuItem>
            <MenuItem name="4">
              <Icon type="ios-paper"></Icon>
              Item 4
            </MenuItem>
          </div>
        </Menu>
      </Header>
      <Content style="top: 64px; bottom: 0;">
        <Content
          :style="{ padding: '24px', minHeight: '680px', background: '#fff' }"
        >
          <router-view></router-view>
        </Content>
      </Content>
    </Layout>
  </div>
  <!-- <Button @click="jump">后台</Button> -->
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { Station } from "@/types/station.interface";
import { getStations } from "@/api/station";

@Component({})
export default class Main extends Vue {
  stations: Station[] = [];

  mounted() {
    this.initData();
  }

  initData() {
    getStations().then((res: any) => {
      let { code, data } = res.data;
      if (code == 200) {
        this.stations = data;
      }
    });
  }

  viewJump() {
    let param: any = {
      name: "MainView",
    };
    this.$router.push(param);
  }

  stationJump(index: number) {
    let id: number = this.stations[index].id;
    let param: any = {
      path: "/main/station/" + id,
    };
    this.$router.push(param);
  }

  jump() {
    let param: any = {
      name: "user",
    };
    this.$router.push(param);
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
  height: 100%;
  width: 100%;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
}

.layout-logo {
  width: 100px;
  height: 30px;
  background: #5b6270;
  border-radius: 3px;
  float: left;
  position: relative;
  top: 15px;
  left: 20px;
}

.layout-nav {
  width: 426px;
  margin: 0 auto;
  margin-right: 20px;
}
</style>
