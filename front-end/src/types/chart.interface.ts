import { EChartOption } from 'echarts';


/**
 * 图表类型
 */
export enum ChartType {
    /** 啥都不是 */
    NONE = 0,
    /** 普通柱状图 */
    BARCHART_NORMAL = 100,
    /** 横向一般柱状图 */
    BARCHART_NORMAL_HORIZONTAL = 101,
    /** 多根柱状图 */
    BARCHART_MULTIPLE = 110,
    /** 堆叠的柱状图 */
    BARCHART_STACK = 120,
    /** 多条折线图 */
    LINECHART_MULTIPLE = 210,
    /** 混合图-柱状和折线 */
    MIXEDCHART_BARANDLINE = 300,
    /** 饼图 */
    PIECHART_NORMAL = 400,
    /** 空心饼图 */
    PIECHART_HOLLOW = 410,
    /** 三维饼图 */
    PIECHART_3D = 420,
    /** 2.5D饼图 */
    PIRCHART_CUSTOMIZED = 430,
    /** 仪表盘 */
    GAUGECHART_NORMAL = 500,
    /** 雷达图 */
    RADARCHART_NORMAL = 600,
    /** 半圆环进度条 */
    SEMICIRCLE_NORMAL = 700
}


/**
 * 页面传值到组件的数据类型
 */
export interface ChartBindData {
    /**
     * 组件id
     */
    id: string,
    /** 
     * 图表类型 
     */
    type: ChartType,
    /**
     * 传进的数据
     */
    data: ChartData,
    /**
     * 传进组件的样式（可不填，即默认）
     */
    option?: EChartOption
}

/**
 * 最基础的数据模式，即key,value模式
 */
export interface BaseData {
    key: string,
    value: number
}

/**
 * 雷达图的data，value为number[], name为string
 */

export interface RadarData {
    value: number[],
    name: string
}

/**
 * 雷达图的radar，text为string类型，max为number类型
 */

export interface RadarIndicator {
    text: string,
    max?: number
}

/**
 * 每个序列
 */
export interface Series {
    /**
     * 序列名
     */
    name?: string,
    /**
     * 单位
     */
    unit?: string,
    /**
     * 数据
     */
    data: BaseData[]
}

/**
 * 一般由页面传到echarts中所需的数据
 */
export interface ChartData {
    /**
     * 图标名称
     */
    title: string,
    /**
     * 序列
     * 单个序列就不用传输组了
     */
    series?: Series | Series[]
}