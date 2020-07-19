<template>
    <div id="app">
        <!-- <div id="nav">
            <router-link to="/"></router-link> |
        </div> -->
        <router-view />
    </div>
</template>

<style lang="less">
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

html,
body {
    width: 100%;
    height: 100%;
    // background-color: #1c2438;

    #app {
        // font-family: "Avenir", Helvetica, Arial, sans-serif;
        // -webkit-font-smoothing: antialiased;
        // -moz-osx-font-smoothing: grayscale;
        // text-align: center;
        // color: #2c3e50;
        font-size: 32px;
        width: 100%;
        height: 100%;
        overflow: hidden;
    }

    #nav {
        padding: 30px;

        a {
            font-weight: bold;
            color: #2c3e50;

            &.router-link-exact-active {
                color: #42b983;
            }
        }
    }
}
</style>
