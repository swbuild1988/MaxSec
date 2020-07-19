/**
 * @msg: 获取系统当前时间
 * @param {string} fmt 时间格式 具体看代码
 * @return: string
 */
Date.prototype.format = function (format: string): string {
    let date: any = {
        // tslint:disable-next-line:no-invalid-this
        "M+": this.getMonth() + 1,
        // tslint:disable-next-line:no-invalid-this
        "d+": this.getDate(),
        // tslint:disable-next-line:no-invalid-this
        "h+": this.getHours(),
        // tslint:disable-next-line:no-invalid-this
        "m+": this.getMinutes(),
        // tslint:disable-next-line:no-invalid-this
        "s+": this.getSeconds(),
        // tslint:disable-next-line:no-invalid-this
        "q+": Math.floor((this.getMonth() + 3) / 3),
        // tslint:disable-next-line:no-invalid-this
        "S+": this.getMilliseconds()
    }

    let res: string = format

    if (/(y+)/i.test(res)) {
        // tslint:disable-next-line:no-invalid-this
        res = res.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length))
    }
    for (let k in date) {
        if (new RegExp("(" + k + ")").test(res)) {
            res = res.replace(RegExp.$1, RegExp.$1.length == 1 ? date[k] : ("00" + date[k]).substr(("" + date[k]).length))
        }
    }
    return res
}

/**
 * 对日期进行格式化，
 * @param date 要格式化的日期
 * @param format 进行格式化的模式字符串
 *     支持的模式字母有：
 *     y:年,
 *     M:年中的月份(1-12),
 *     d:月份中的天(1-31),
 *     h:小时(0-23),
 *     m:分(0-59),
 *     s:秒(0-59),
 *     S:毫秒(0-999),
 *     q:季度(1-4)
 * @return String
 */
// 兼容ie
export function dateFormat(date: any, format: string) {
    var time: Date;
    if (!date) return "";
    if (typeof date === 'string') {
        // time = new Date(date.replace(/-/g, '/').replace(/T|Z/g, ' ').trim());
        time = new Date(0)
    } else if (typeof date === 'object') {
        time = new Date(date);
    }
    console.log("time", time)
    var o = {
        "M+": time.getMonth() + 1, //月份
        "d+": time.getDate(), //日
        "h+": time.getHours(), //小时
        "m+": time.getMinutes(), //分
        "s+": time.getSeconds(), //秒
        "q+": Math.floor((time.getMonth() + 3) / 3), //季度
        "S": time.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(format)) format = format.replace(RegExp.$1, (time.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(format)) format = format.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return format;
}