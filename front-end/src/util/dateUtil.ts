export class DateUtil {
    /** 获取当天的起始时间 */
    public static TodayStartTime(): Date {
        let time = new Date()
        time.setHours(0)
        time.setMinutes(0)
        time.setSeconds(0)

        return time
    }

    /** 获取当天的结束时间 */
    public static TodayEndTime(): Date {
        let time = new Date()
        time.setHours(23)
        time.setMinutes(59)
        time.setSeconds(59)

        return time
    }
}