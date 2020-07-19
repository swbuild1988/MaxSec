<template>
    <div class="scroll-board" :ref="ref">
        <div class="header" v-if="header.length" :style="`background-color: ${mergedConfig.headerBGC};`">
            <div class="header-item" v-for="(headerItem, i) in header" :key="`${headerItem}${i}`" :style="`
          height: ${mergedConfig.headerHeight}px;
          line-height: ${mergedConfig.headerHeight}px;
          width: ${widths[i]}px;
        `" :align="aligns[i]" v-html="headerItem" />
        </div>

        <div v-if="mergedConfig" class="rows"
            :style="`height: ${height - (header.length ? mergedConfig.headerHeight : 0)}px;`">
            <div class="row-item" v-for="(row, ri) in rows" :key="`${row.toString()}${row.scroll}`" :style="`
          height: ${heights[ri]}px;
          line-height: ${heights[ri]}px;
          background-color: ${mergedConfig[row.rowIndex % 2 === 0 ? 'evenRowBGC' : 'oddRowBGC']};
        `">
                <div class="ceil" v-for="(ceil, ci) in row.ceils" :key="`${ceil}${ri}${ci}`"
                    :style="`width: ${widths[ci]}px;`" :align="aligns[ci]" v-html="ceil"
                    @click="emitEvent('click', ri, ci, row, ceil)" @mouseenter="handleHover(true, ri, ci, row, ceil)"
                    @mouseleave="handleHover(false)" />

            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import {
        Component,
        Vue,
        Prop,
        Watch
    } from 'vue-property-decorator'

    interface boardInterface {
        header: Array < string > ,
            data: any,
            rowNum: number,
            headerBGC: string,
            oddRowBGC: string,
            evenRowBGC: string,
            waitTime: number,
            headerHeight: number,
            columnWidth: Array < number > ,
            align: Array < string > ,
            index: boolean,
            indexHeader: string,
            carousel: string,
            hoverPause: boolean
    }
    const defaultConfig = {
        header: [],
        data: [],
        rowNum: 5,
        headerBGC: '#00BAFF',
        oddRowBGC: 'rgba(0,0,0,0)',
        evenRowBGC: 'rgba(0,0,0,0)',
        waitTime: 2000,
        headerHeight: 35,
        columnWidth: [],
        /**
         * @example align = ['left', 'center', 'right']
         */
        align: [],
        index: false,
        indexHeader: '#',
        /**
         * @example carousel = 'single' | 'page'
         */
        carousel: 'single',
        hoverPause: true
    }

    @Component({})
    export default class Map extends Vue {

        // prop
        @Prop({
            default: () => {
                let config: boardInterface = {
                    header: [],
                    data: [],
                    rowNum: 5,
                    headerBGC: '#00BAFF',
                    oddRowBGC: '#003B51',
                    evenRowBGC: '#0A2732',
                    waitTime: 2000,
                    headerHeight: 35,
                    columnWidth: [],
                    /**
                     * @example align = ['left', 'center', 'right']
                     */
                    align: [],
                    index: false,
                    indexHeader: '#',
                    /**
                     * @example carousel = 'single' | 'page'
                     */
                    carousel: 'single',
                    hoverPause: true
                }
                return config
            }
        })
        config!: boardInterface

        // data
        ref: string = 'scroll-board'
        mergedConfig: boardInterface
        header: Array < string > = []
        widths: any = []
        heights: Array < number > = []
        aligns: any = []
        width: number = 0
        height: number = 0
        rowsData: any = []
        rows: Array < any > = []
        dom: any
        avgHeight: number = 0
        animationIndex: number = 0
        animationHandler: any = ''
        updater: number = 0
        // watch
        @Watch('config', {
            deep: true
        })
        propChange() {
            const {
                calcData
            } = this
            calcData() // 初始化数据
        }
        mounted() {
            this.calcData()
        }
        // methond
        calcData() {
            const {
                mergeConfig,
                calcHeaderData,
                calcRowsData,
                initWH,
                calcWidths,
                calcHeights,
                calcAligns,
                animation
            } = this

            mergeConfig() // 初始化数据
            calcHeaderData() // 头部数据
            calcRowsData() // 行数据
            initWH() // 初始化宽高
            calcWidths() // 表格宽度
            calcHeights() // 表格高度
            calcAligns() // 对齐方式
            animation(true) // 表格动画
        }
        mergeConfig() {
            let {
                config
            } = this
            this.mergedConfig = Object.assign({}, defaultConfig, config)

        }
        handleHover(enter, ri, ci, row, ceil) {
            const {
                mergedConfig,
                emitEvent,
                stopAnimation,
                animation
            } = this
            if (enter) emitEvent('mouseover', ri, ci, row, ceil)
            if (!mergedConfig.hoverPause) return
            if (enter) {
                stopAnimation()
            } else {
                animation(true)
            }
        }
        initWH() {
            const {
                $refs,
                ref
            } = this

            const dom = this.dom = $refs[ref]

            this.width = (dom as any).clientWidth
            this.height = (dom as any).clientHeight
        }
        calcHeaderData() {
            let {
                header,
                index,
                indexHeader
            } = this.mergedConfig

            if (!header.length) {
                this.header = []
                return
            }
            header = [...header]
            if (index) header.unshift(indexHeader) // 显示行号
            this.header = header
        }
        calcRowsData() {
            let {
                data,
                index,
                headerBGC,
                rowNum
            } = this.mergedConfig

            if (index) { // 添加行数
                data = data.map((row, i) => {
                    row = [...row]
                    const indexTag =
                        `<span class="index" style="background-color: ${headerBGC};">${i + 1}</span>`
                    row.unshift(indexTag)
                    return row
                })
            }

            data = data.map((ceils, i) => ({
                ceils,
                rowIndex: i
            }))

            const rowLength = data.length
            if (rowLength > rowNum && rowLength < 2 * rowNum) {
                data = [...data, ...data]
            }
            data = data.map((d, i) => ({
                ...d,
                scroll: i
            }))

            this.rowsData = data
            this.rows = data
        }
        calcWidths() {
            const {
                width,
                mergedConfig: {
                    columnWidth,
                    header
                },
                rowsData
            } = this

            const usedWidth = columnWidth.reduce((all, w) => all + w, 0)
            let columnNum = 0
            if (rowsData[0]) {
                columnNum = rowsData[0].ceils.length
            } else if (header.length) {
                columnNum = header.length
            }
            const avgWidth = (width - usedWidth) / (columnNum - columnWidth.length)
            const widths = new Array(columnNum).fill(avgWidth)

            this.widths = columnWidth.length ? columnWidth : widths
        }
        calcHeights(onresize = false) {
            const {
                height,
                mergedConfig: {
                    headerHeight,
                    rowNum,
                    data
                },
                header
            } = this

            let allHeight = height
            if (header.length) allHeight -= headerHeight
            const avgHeight = allHeight / rowNum
            this.avgHeight = avgHeight
            if (!onresize) this.heights = new Array(data.length).fill(avgHeight)
        }
        calcAligns() {
            const {
                header,
                mergedConfig: {
                    align
                }
            } = this
            const columnNum = header.length
            let aligns = new Array(columnNum).fill('center')

            this.aligns = align.length ? align : aligns
        }
        async animation(start = false) {
            let {
                avgHeight,
                animationIndex,
                mergedConfig: {
                    waitTime,
                    carousel,
                    rowNum
                },
                rowsData,
                animation,
                updater
            } = this

            const rowLength = rowsData.length
            if (rowNum >= rowLength) return // 数据不够，没有动画
            if (start) {
                await new Promise(resolve => setTimeout(resolve, waitTime)) // 立即调用定时器 开启动画效果
                if (updater !== this.updater) return
            }
            const animationNum = carousel === 'single' ? 1 : rowNum
            let rows = rowsData.slice(animationIndex) // 去除头部信息 添加到尾部
            rows.push(...rowsData.slice(0, animationIndex))
            this.rows = rows
            this.heights = new Array(rowLength).fill(avgHeight)

            await new Promise(resolve => setTimeout(resolve, 300))
            if (updater !== this.updater) return

            this.heights.splice(0, animationNum, ...new Array(animationNum).fill(0))

            animationIndex += animationNum
            const back = animationIndex - rowLength
            if (back >= 0) animationIndex = back
            this.animationIndex = animationIndex

            this.animationHandler = setTimeout(animation, waitTime - 300)
        }
        emitEvent(type, ri, ci, row, ceil) { // emit 事件
            const {
                ceils,
                rowIndex
            } = row
            this.$emit(type, {
                row: ceils,
                ceil,
                rowIndex,
                columnIndex: ci
            })
        }
        stopAnimation() {
            const {
                animationHandler,
                updater
            } = this
            this.updater = (updater + 1) % 999999
            if (!animationHandler) return
            clearTimeout(animationHandler)
        }
        destroyed() {
            const {
                stopAnimation
            } = this
            stopAnimation()
        }

    }
</script>

<style lang="less">
    .scroll-board {
        position: relative;
        width: 100%;
        height: 100%;
        color: #fff;

        .text {
            padding: 0 10px;
            box-sizing: border-box;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .header {
            display: flex;
            flex-direction: row;
            font-size: 15px;

            .header-item {
                .text;
                transition: all 0.3s;
            }
        }

        .rows {
            overflow: hidden;

            .row-item {
                display: flex;
                font-size: 14px;
                transition: all 0.3s;
            }

            .ceil {
                .text;
            }

            .index {
                border-radius: 3px;
                padding: 0px 3px;
            }
        }
    }
</style>