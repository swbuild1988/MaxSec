export function getDate(connector) {
    let timeStr = new Date();
    let yearString = timeStr.getFullYear();
    let monthString = +timeStr.getMonth() + 1;
    let dateString = timeStr.getDate();
    let date = `${yearString}${connector}${monthString}${connector}${dateString}`;
    return date
}
export function getTime() {

    let timeStr = new Date();
    let hoursStr =
        timeStr.getHours() > 9
            ? timeStr.getHours()
            : "0" + timeStr.getHours();
    let minutesStr =
        timeStr.getMinutes() > 9
            ? timeStr.getMinutes()
            : "0" + timeStr.getMinutes();
    let secondsStr =
        timeStr.getSeconds() > 9
            ? timeStr.getSeconds()
            : "0" + timeStr.getSeconds();
    let time = `${hoursStr}:${minutesStr}:${secondsStr}`;
    return time

}
export function getWeek() {
    switch (new Date().getDay()) {
        case 0:
            return "星期天";

        case 1:
            return "星期一";

        case 2:
            return "星期二";

        case 3:
            return "星期三";

        case 4:
            return "星期四";

        case 5:
            return "星期五";

        case 6:
            this.week = "星期六";
    }
}