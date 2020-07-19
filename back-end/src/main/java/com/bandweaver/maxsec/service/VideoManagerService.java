package com.bandweaver.maxsec.service;

import com.alibaba.fastjson.JSON;
import com.bandweaver.maxsec.TestRabbitMQ.MQSender;
import com.bandweaver.maxsec.constants.*;
import com.bandweaver.maxsec.dto.*;
import com.bandweaver.maxsec.schedule.ScheduleServiceManage;
import com.bandweaver.maxsec.service.Impl.HikVideoControlImpl;
import com.bandweaver.maxsec.entity.*;

import java.io.File;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

import com.bandweaver.maxsec.util.DateUtil;
import com.bandweaver.maxsec.util.RedisUtil;
import com.bandweaver.maxsec.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VideoManagerService {

    @Autowired
    private MeasObjectService measObjectService;
    @Autowired
    private VideoServerService videoServerService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoLinkageService videoLinkageService;
    @Autowired
    private AlarmTypeService alarmTypeService;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private HikVideoControlImpl hikVideoControlImpl;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ScheduleServiceManage scheduleServiceManage;
    @Autowired
    private MQSender mqSender;

    // 视频服务实现
    private Map<Integer, VideoControlService> videoControlServiceMap = new HashMap<>();
    // 告警联动缓存告警
    private ConcurrentHashMap<Integer, Alarm> videoLinkageAlarmMap = new ConcurrentHashMap<>();

    // 是否开启视频告警服务
    private Boolean alarmServiceActived;
    // 告警间隔时间 秒
    private Integer alarmIntervalTime;
    // 是否开启视频联动
    private Boolean alarmLinkageActived;
    // 联动重复告警检测时间
    private Integer alarmLinkageIntervalTime;
    // 告警文件目录
    private String alarmLinkageDir;
    // 告警截图
    private Boolean alarmLinkageCatPic;
    // 告警截图数量
    private Integer alarmLinkageCatPicCount;
    // 告警截图间隔
    private Integer alarmLinkageCatPicInterval;
    // 告警视频
    private Boolean alarmLinkageRecord;
    // 告警视频存储提前时间
    private Integer alarmLinkageRecordAheadTime;
    // 告警视频存储时间
    private Integer alarmLinkageRecordTime;
    // 告警视频存储等待时间
    private Integer alarmLinkageRecordWaitTime;
    // 告警升级确认等待时间
    private Integer alarmLinkageUpgradeWaitTime;
    // 可视对讲人脸图片目录
    private String intercomDir;

    public void start() {

        // 初始化视频服务实现
        boolean init = hikVideoControlImpl.init();
        log.info("Hik init:" + init);
        videoControlServiceMap.put(VideoVendorEnum.HikNvr.getValue(), hikVideoControlImpl);

        // 初始化Redis会话
        initSession();

        // 初始化视频告警服务
        Map<String, String> configMap = XmlUtil.getSystemConfig("VideoAlarmService");
        alarmServiceActived = Boolean.valueOf(configMap.get("actived"));
        alarmIntervalTime = Integer.valueOf(configMap.get("intervalTime"));
        if (alarmServiceActived) {
            for (VideoControlService videoControlService : videoControlServiceMap.values()
                    ) {
                boolean result = videoControlService.startAlarmService();
                log.info(String.format("startAlarmService [%s]", result));
            }
        }

        // 初始化告警联动参数
        configMap = XmlUtil.getSystemConfig("AlarmLinkage");
        alarmLinkageActived = Boolean.valueOf(configMap.get("actived"));
        alarmLinkageIntervalTime = Integer.valueOf(configMap.get("intervalTime"));
        alarmLinkageDir = String.valueOf(configMap.get("directory"));
        alarmLinkageDir = System.getProperty("user.dir") + "\\src\\main\\resources" + alarmLinkageDir + "\\";
        alarmLinkageCatPic = Boolean.valueOf(configMap.get("catPic"));
        alarmLinkageCatPicCount = Integer.valueOf(configMap.get("catPicCount"));
        alarmLinkageCatPicInterval = Integer.valueOf(configMap.get("catPicInterval"));
        alarmLinkageRecord = Boolean.valueOf(configMap.get("record"));
        alarmLinkageRecordAheadTime = Integer.valueOf(configMap.get("recordAheadTime"));
        alarmLinkageRecordTime = Integer.valueOf(configMap.get("recordTime"));
        alarmLinkageRecordWaitTime = Integer.valueOf(configMap.get("recordWaitTime"));
        alarmLinkageUpgradeWaitTime = Integer.valueOf(configMap.get("upgradeWaitTime"));

        // 初始化可视对讲参数
        configMap = XmlUtil.getSystemConfig("Intercom");
        intercomDir = String.valueOf(configMap.get("directory"));
        intercomDir = System.getProperty("user.dir") + "\\src\\main\\resources" + intercomDir + "\\";
        File pathName = new File(intercomDir);
        if (!pathName.exists()) {
            pathName.mkdirs();
        }

        // 视频服务心跳
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(new HeartRunner(), 0, 10, TimeUnit.SECONDS);
    }

    public int login(int objId, int vendor, String ip, int port, String userName, String pwd) {
        log.info("Video try login," + objId);
        String sessionKey = Constants.REDIS_VIDEOSERVER_SESSION + objId;
        redisUtil.del(sessionKey);
        VideoControlService videoControlService = videoControlServiceMap.get(vendor);
        if (videoControlService == null) {
            return -1;
        }
        int sessionID = videoControlService.login(ip, port, userName, pwd, objId);
        if (sessionID < 0) {
            sessionID = -1;
            log.info("Video login error," + videoControlService.getLastError());
        } else {
            redisUtil.set(sessionKey, sessionID);
            log.info("Video login ok," + objId);
        }
        return sessionID;
    }

    private MeasObject getAlarmVideo(VideoAlarm videoAlarm, int channel) {
        int objectId = videoAlarm.getObjectId();
        // 回调有IP
        if (objectId <= 0) {
            String ip = videoAlarm.getIp();
            for (Video video : videoService.selectAllDto()
                    ) {
                if (video.getIp().equals(ip)) {
                    return video;
                }
            }
        } else {
            // 说明回调是Server编号和通道号
            for (VideoServerDto videoServerDto : videoServerService.selectAllDto()
                    ) {
                if (videoServerDto.getId() == objectId) {
                    for (Video video : videoServerDto.getVideos()
                            ) {
                        if (video.getChannelNo() == channel) {
                            return video;
                        }
                    }
                }
            }
        }
        return null;
    }

    private VideoServerDto getAlarmVideoServer(VideoAlarm videoAlarm) {
        int objectId = videoAlarm.getObjectId();
        if (objectId <= 0) {
            String ip = videoAlarm.getIp();
            for (VideoServerDto videoServerDto : videoServerService.selectAllDto()
                    ) {
                if (videoServerDto.getIp().equals(ip)) {
                    return videoServerDto;
                }
            }
        } else {
            for (VideoServerDto videoServerDto : videoServerService.selectAllDto()
                    ) {
                if (videoServerDto.getId() == objectId) {
                    return videoServerDto;
                }
            }
        }
        return null;
    }

    // 视频原始告警
    public void processAlarm(VideoAlarm videoAlarm) {
        String key = String.format("OriginVideoAlarmRunner[%s]", JSON.toJSONString(videoAlarm));
        if (redisUtil.get(key) != null || videoAlarm.getAlarmTypeId() <= 0) {
            return;
        }
        redisUtil.set(key, new Date(), 3);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(new OriginVideoAlarmRunner(videoAlarm), 0, TimeUnit.SECONDS);
    }

    // 处理门禁图片
    public void processFace(IntercomCard intercomCard) {
        String fileName = intercomCard.getFileName();
        String picPath = intercomDir + "\\" + intercomCard.getCardNo() + ".jpeg";
        File oldName = new File(fileName);
        File newName = new File(picPath);
        // 重命名文件
        oldName.renameTo(newName);
    }

    // 视频联动入口
    public void processAlarmLinkage(Alarm alarm) {
        if (!alarmLinkageActived) {
            return;
        }
        String alarmKey = String.format("%s_%s_%s", alarm.getObjId(), alarm.getAlarmTypeId(), alarm.getSource());
        // 是否重复告警
        if (checkRepetitionVideoLinkageAlarm(alarmKey)) {
            return;
        }
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(new AlarmLinkageRunner(alarm), 0, TimeUnit.SECONDS);
    }

    // 周界告警时升级
    private void upgradeAlarmByUnVideo(Alarm alarm, VideoLinkage videoLinkage) {
        // 缓存视频联动对应告警编号
        videoLinkageAlarmMap.put(videoLinkage.getId(), alarm);
        String videoKey = Constants.REDIS_VIDEOLINKAGE_UPGRADE + "_" + videoLinkage.getId() + "_" + videoLinkage.getVideoId();
        if (redisUtil.get(videoKey) != null) {
            redisUtil.del(videoKey);
            // 直接升级
            log.info("upgradeAlarmByUnVideo:" + alarm.getObjId());
            upgradeAlarm(alarm);
        } else {
            String objKey = Constants.REDIS_VIDEOLINKAGE_UPGRADE + "_" + videoLinkage.getId() + "_" + videoLinkage.getObjId();
            redisUtil.del(objKey);
            redisUtil.set(objKey, new Date(), alarmLinkageUpgradeWaitTime);
            log.info("upgradeAlarmByVideo set object key:" + objKey);
        }
    }

    // 视频告警时升级
    private Boolean upgradeAlarmByVideo(int videoId) {
        Boolean result = false;
        List<VideoLinkage> videoLinkageList = videoLinkageService.selectAll();
        for (VideoLinkage videoLinkage : videoLinkageList
                ) {
            if (!videoLinkage.getActived()) {
                continue;
            }
            if (videoLinkage.getVideoId() == videoId) {
                String objKey = String.valueOf(Constants.REDIS_VIDEOLINKAGE_UPGRADE + "_" + videoLinkage.getId() + "_" + videoLinkage.getObjId());
                if (redisUtil.get(objKey) != null) {
                    redisUtil.del(objKey);
                    // 查找对应对象告警 再进行升级
                    log.info("upgradeAlarmByVideo:" + videoId);
                    Alarm alarm = videoLinkageAlarmMap.get(videoLinkage.getId());
                    upgradeAlarm(alarm);
                    result = true;
                    videoLinkageAlarmMap.remove(videoLinkage.getId());
                } else {
                    String videoKey = String.valueOf(Constants.REDIS_VIDEOLINKAGE_UPGRADE + "_" + videoLinkage.getId() + "_" + videoLinkage.getVideoId());
                    redisUtil.del(videoKey);
                    redisUtil.set(videoKey, new Date(), alarmLinkageUpgradeWaitTime);
                    log.info("upgradeAlarmByVideo set video key:" + videoKey);
                }
            }
        }
        return result;
    }

    // 告警升级
    private void upgradeAlarm(Alarm alarm) {
        int objId = alarm.getObjId();
        MeasObject measObject = measObjectService.selectByPrimaryKey(objId);
        ObjectEnum objectType = ObjectEnum.getEnum(measObject.getObjectType());
        MessageTypeEnum messageType = MessageTypeEnum.IntrusionUpgradeAlarm;
        AlarmType alarmType = null;
        if (objectType == ObjectEnum.Intrusion) {
            alarmType = alarmTypeService.selectByPrimaryKey(2);
            messageType = MessageTypeEnum.IntrusionUpgradeAlarm;
        }
        if (alarmType == null) {
            log.info("upgradeAlarm can find alarmtype:" + alarm.getId());
            return;
        }
        Alarm newAlarm = new Alarm();
        newAlarm.setTime(new Date());
        newAlarm.setLevel(AlarmLevelEnum.Critical.getValue());
        newAlarm.setCleaned(false);
        newAlarm.setDescription(alarmType.getDescription());
        newAlarm.setObjId(objId);
        newAlarm.setAlarmTypeId(alarmType.getId());
        newAlarm.setSource(alarm.getSource());
        // 入库
        alarmService.insert(newAlarm);
        // 发送MQ
        Message message = new Message();
        message.setType(messageType);
        message.setBody(newAlarm);
        mqSender.sendMessageToFront(message);
    }

    // 视频告警及视频联动
    private void processAlarmData(Alarm alarm, int measObjID, int preset, String
            fileName) {
        int alarmId = alarm.getId();
        AlarmType alarmType = alarmTypeService.selectByPrimaryKey(alarm.getAlarmTypeId());
        log.info("Process Alarm Data:" + alarmId);
        // 仅处理业务类告警数据
        if (alarmType.getCategory() != AlarmCategoryEnum.Biz.getValue()) {
            log.info("Not Biz ALarm Type,Quit:" + alarmId);
        }
        int channelNo = 0;
        int vendor = 0;
        // 是否具备硬盘录像能力
        Boolean supportRecord = false;
        ObjectEnum objectType = ObjectEnum.getEnum(alarmType.getObjectType());
        if (objectType == ObjectEnum.VideoServer) {
            VideoServer videoServer = videoServerService.selectByPrimaryKey(measObjID);
            vendor = videoServer.getVendor();
            channelNo = 0;
        } else {
            VideoDto video = videoService.selectDtoByPrimaryKey(measObjID);
            VideoServer videoServer = video.getVideoServer();
            if (videoServer == null) {
                vendor = video.getVendor();
            } else {
                vendor = videoServer.getVendor();
                measObjID = videoServer.getId();
                supportRecord = true;
            }
            channelNo = video.getChannelNo();
        }

        VideoControlService videoControlService = videoControlServiceMap.get(vendor);
        int sessionID = -1;
        String sessionKey = Constants.REDIS_VIDEOSERVER_SESSION + measObjID;
        if (!redisUtil.hasKey(sessionKey)) {
            log.info("video not login:" + measObjID);
            return;
        }
        sessionID = (int) redisUtil.get(sessionKey);

        // 视频联动预置位
        if (preset > 0) {
            Boolean result = videoControlService.gotoPreset(sessionID, channelNo, preset);
            if (!result) {
                String stringTemp = String.format("Video GotoPreset Error,[%s][%s][%s]", measObjID, channelNo, preset);
                log.info(stringTemp);
            }
        }

        // 文件目录
        Date alarmTime = videoControlService.getCurrentTime(sessionID);
        String alarmPath = alarmLinkageDir + DateUtil.getDate2YYYYMMdd(alarm.getTime()) + "\\" + alarmId;
        File alarmPathFile = new File(alarmPath);
        if (!alarmPathFile.exists()) {
            alarmPathFile.mkdirs();
        }

        // 处理图片
        if (alarmLinkageCatPic) {
            if (fileName != null && fileName.length() > 0) {
                String picPath = alarmPath + "\\" + measObjID + ".jpeg";
                File oldName = new File(fileName);
                File newName = new File(picPath);
                // 重命名文件
                oldName.renameTo(newName);
                log.info(String.format("Video Alarm Picture Move OK:%s", picPath));
            } else {
                int catPicCount = alarmLinkageCatPicCount;
                if (objectType == ObjectEnum.Intercom) {
                    catPicCount = 1;
                }
                for (int i = 1; i <= catPicCount; i++) {
                    String picPath = alarmPath + "\\" + measObjID + "_" + channelNo + "_" + i + ".jpeg";
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    scheduler.schedule(new PictureRunner(videoControlService, vendor, sessionID, channelNo, picPath), alarmLinkageCatPicInterval * i, TimeUnit.SECONDS);
                }
            }
        }

        // 处理视频 仅视频服务可以获取录像
        if (supportRecord && alarmLinkageRecord) {
            String recordPath = alarmPath + "\\" + measObjID + "_" + channelNo + "_" + DateUtil.getDate2StringFormat(alarmTime, "yyyyMMddHHmmss") + ".mp4";
            log.info("Video Alarm Record FileName:" + recordPath);
            Date startTime = new Date(alarmTime.getTime() - alarmLinkageRecordAheadTime * 1000);
            Date endTime = new Date(alarmTime.getTime() + alarmLinkageRecordTime * 1000);
            log.info("Video Alarm Record AlarmTime:" + DateUtil.getDate2String(alarmTime));
            log.info("Video Alarm Record StartTime:" + DateUtil.getDate2String(startTime));
            log.info("Video Alarm Record EndTime:" + DateUtil.getDate2String(endTime));
            int waitTime = alarmLinkageRecordTime + alarmLinkageRecordWaitTime;
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.schedule(new RecordRunner(videoControlService, sessionID, channelNo, startTime, endTime, recordPath), waitTime, TimeUnit.SECONDS);
        }

        // 下载人脸图片 卡片可能没有关联人脸 统一抓图处理
        /*if (alarmType.getId() == 3001) {
            String cardNo = alarm.getSource();
            String picPath = intercomDir + "\\" + cardNo + ".jpeg";
            File file = new File(picPath);
            if (!file.exists()) {
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.schedule(new GetFaceRunner(videoControlService, sessionID, cardNo), 0, TimeUnit.SECONDS);
            } else {
                log.info("Video Face Picture Already Exist:" + picPath);
            }
        }*/
    }

    /// <summary>
    /// 检测是否为重复原始告警
    /// </summary>
    private Boolean checkRepetitionOriginVideoAlarm(String alarmKey) {
        Boolean result = false;
        if (alarmIntervalTime > 0) {
            // 对象_通道_告警类型/最后告警时间
            log.debug(MessageFormat.format("Check Repetition Alarm, AlarmKey:{0}", alarmKey));
            LocalDate dateTime = LocalDate.now();
            String key = Constants.REDIS_ORIGIN_VIDEO_ALARM + alarmKey;
            if (redisUtil.hasKey(key)) {
                result = true;
            } else {
                log.info(MessageFormat.format("Not Repetition  Alarm, AlarmKey:{0}", alarmKey));
            }
            redisUtil.set(key, dateTime, alarmIntervalTime);
        }
        return result;
    }

    /// <summary>
    /// 检测是否为重复联动告警
    /// </summary>
    private Boolean checkRepetitionVideoLinkageAlarm(String alarmKey) {
        Boolean result = false;
        if (alarmLinkageIntervalTime > 0) {
            // 对象_告警源_告警类型/最后告警时间
            log.debug(MessageFormat.format("Check Repetition Alarm, AlarmKey:{0}", alarmKey));
            LocalDate dateTime = LocalDate.now();
            String key = Constants.REDIS_VIDEOLINKAGE_ALARM + alarmKey;
            if (redisUtil.hasKey(key)) {
                result = true;
            } else {
                log.info(MessageFormat.format("Not Repetition  Alarm, AlarmKey:{0}", alarmKey));
            }
            redisUtil.set(key, dateTime, alarmLinkageIntervalTime);
        }
        return result;
    }

    /// <summary>
    /// 生成视频告警
    /// </summary>
    private Alarm raiseVideoAlarm(int objId, AlarmType alarmType, int channel, VideoAlarm videoAlarm) {
        log.info(String.format("Raise Video Alarm, MeasObj:{%s}, Channel:{%s}, AlarmTypeId:{%s}", objId, channel, alarmType.getId()));
        Alarm alarm = new Alarm();
        alarm.setTime(new Date());
        alarm.setLevel(alarmType.getLevel());
        alarm.setCleaned(false);
        alarm.setDescription(String.format(alarmType.getDescription(), videoAlarm.getParas().stream().toArray(String[]::new)));
        alarm.setObjId(objId);
        alarm.setAlarmTypeId(alarmType.getId());
        if (alarmType.getId() == 3001) {
            String cardNo = videoAlarm.getParas().get(2);
            alarm.setSource(cardNo);
        } else {
            alarm.setSource(String.valueOf(channel));
        }
        return alarm;
    }

    /// <summary>
    /// 开始巡检预置位
    /// </summary>
    public void startPatrolPreset(PatrolPresetJob job) {
        String key = "PatrolPresetRunner" + job.getVideoId();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(new PatrolPresetRunner(job), 0, TimeUnit.SECONDS);
        scheduleServiceManage.put(key, scheduler);
    }

    /// <summary>
    /// 停止巡检预置位
    /// </summary>
    public void stopPatrolPreset(PatrolPresetJob job) {
        String key = "PatrolPresetRunner" + job.getVideoId();
        scheduleServiceManage.shutdown(key);
    }

    // 初始化会话
    private void initSession() {
        for (VideoDto video : videoService.selectAllDto()
                ) {
            int videoId = video.getId();
            int vendor = video.getVendor();
            if (vendor == VideoVendorEnum.HikNvr.getValue()) {
                VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
                if (video.getObjectType() == ObjectEnum.Intercom.getValue()) {
                    // 可视对讲长连接 初始化关闭
                    String intercomKey = Constants.REDIS_INTERCOM_REMOTE_SESSION + videoId;
                    if (redisUtil.hasKey(intercomKey)) {
                        int intercomId = (int) redisUtil.get(intercomKey);
                        redisUtil.del(intercomKey);
                        boolean result = netVideoControlService.disconnectIntercom(intercomId);
                        log.info(String.format("Hik video init disconnect intercom [%s][%s]", videoId, result));
                    }
                }
            }
        }
    }

    /// <summary>
    /// 可视对讲控制 2接听 3拒绝 5挂断
    /// </summary>
    public boolean controlIntercom(int objId, int command) {
        boolean result = false;
        Video video = videoService.selectByPrimaryKey(objId);
        String key = Constants.REDIS_INTERCOM_REMOTE_SESSION + objId;
        if (redisUtil.hasKey(key)) {
            int vendor = video.getVendor();
            VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
            int intercomSession = (int) redisUtil.get(key);
            result = netVideoControlService.controlIntercom(intercomSession, command);
            log.info(String.format("Video control intercom[%s][%s][%s][%s]", objId, intercomSession, command, netVideoControlService.getLastError()));
        }
        return result;
    }

    /// <summary>
    /// 可视对讲控制 0关一会 1开一会 2常开 3常关
    /// </summary>
    public boolean controlGateway(int objId, int command) {
        Video video = videoService.selectByPrimaryKey(objId);
        String key = Constants.REDIS_VIDEOSERVER_SESSION + objId;
        if (redisUtil.hasKey(key)) {
            log.info(String.format("Video control gateway[%s][%s]", objId, command));
            int vendor = video.getVendor();
            VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
            int loginSession = (int) redisUtil.get(key);
            return netVideoControlService.controlGateway(loginSession, 1, command);
        }
        return false;
    }

    /// <summary>
    /// 可视对讲获取所有卡片
    /// </summary>
    public boolean getCard(int objId) {
        Video video = videoService.selectByPrimaryKey(objId);
        String key = Constants.REDIS_VIDEOSERVER_SESSION + objId;
        if (redisUtil.hasKey(key)) {
            log.info(String.format("Video get card[%s]", objId));
            int vendor = video.getVendor();
            VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
            int loginSession = (int) redisUtil.get(key);
            return netVideoControlService.getCard(loginSession);
        }
        return false;
    }

    /// <summary>
    /// 可视对讲设置卡片
    /// </summary>
    public boolean setCard(int objId, IntercomCard card) {
        Video video = videoService.selectByPrimaryKey(objId);
        String key = Constants.REDIS_VIDEOSERVER_SESSION + objId;
        if (redisUtil.hasKey(key)) {
            log.info(String.format("Video set card[%s][%s]", objId, card.getCardNo()));
            int vendor = video.getVendor();
            VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
            int loginSession = (int) redisUtil.get(key);
            return netVideoControlService.setCard(loginSession, card);
        }
        return false;
    }

    /// <summary>
    /// 可视对讲删除卡片
    /// </summary>
    public boolean deleteCard(int objId, IntercomCard card) {
        Video video = videoService.selectByPrimaryKey(objId);
        String key = Constants.REDIS_VIDEOSERVER_SESSION + objId;
        if (redisUtil.hasKey(key)) {
            log.info(String.format("Video delete card[%s][%s]", objId, card.getCardNo()));
            int vendor = video.getVendor();
            VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
            int loginSession = (int) redisUtil.get(key);
            return netVideoControlService.deleteCard(loginSession, card);
        }
        return false;
    }

    /// <summary>
    /// 可视对讲设置人脸
    /// </summary>
    public boolean setFace(int objId, IntercomCard card) {
        Video video = videoService.selectByPrimaryKey(objId);
        String key = Constants.REDIS_VIDEOSERVER_SESSION + objId;
        if (redisUtil.hasKey(key)) {
            log.info(String.format("Video set card[%s][%s]", objId, card.getCardNo()));
            int vendor = video.getVendor();
            VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
            int loginSession = (int) redisUtil.get(key);
            return netVideoControlService.setFace(loginSession, card);
        }
        return false;
    }

    /// <summary>
    /// 清除告警 返回预置位
    /// </summary>
    public boolean cleanAlarm(Alarm alarm) {
        int objId = 0;
        int vendor = 0;
        VideoDto videoDto = videoService.selectDtoByPrimaryKey(alarm.getObjId());
        if (videoDto.getServerId() != null && videoDto.getServerId() != 0) {
            VideoServer videoServer = videoServerService.selectByPrimaryKey(videoDto.getServerId());
            objId = videoServer.getId();
            vendor = videoServer.getVendor();
        } else {
            objId = videoDto.getId();
            vendor = videoDto.getVendor();
        }
        String key = Constants.REDIS_VIDEOSERVER_SESSION + objId;
        if (redisUtil.hasKey(key)) {
            log.info(String.format("Video clean alarm[%s][%s]", objId, alarm.getId()));
            VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
            int loginSession = (int) redisUtil.get(key);
            return netVideoControlService.gotoPreset(loginSession, videoDto.getChannelNo(), videoDto.getDefaultPreset());
        }
        return false;
    }

    /// <summary>
    /// 断开可视对讲长连接
    /// </summary>
    public void disconnectIntercom(String ip) {
        for (VideoDto video : videoService.selectAllDto()
                ) {
            if (video.getIp().equals(ip)) {
                int objId = video.getId();
                int vendor = video.getVendor();
                String key = Constants.REDIS_INTERCOM_REMOTE_SESSION + objId;
                if (redisUtil.hasKey(key)) {
                    log.info(String.format("Video disconnect intercom [%s][%s]", objId, ip));
                    VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
                    int remoteSession = (int) redisUtil.get(key);
                    redisUtil.del(key);
                    netVideoControlService.disconnectIntercom(remoteSession);
                }
                break;
            }
        }
    }

    // 心跳线程
    class HeartRunner implements Runnable {
        public void run() {
            try {
                for (VideoServerDto videoServer : videoServerService.selectAllDto()
                        ) {
                    if (!videoServer.getActived()) {
                        continue;
                    }
                    boolean needLogin = false;
                    int videoServerId = videoServer.getId();
                    int vendor = videoServer.getVendor();
                    VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
                    String key = Constants.REDIS_VIDEOSERVER_SESSION + videoServerId;
                    if (!redisUtil.hasKey(key)) {
                        log.info("VideoServer not login," + videoServerId);
                        needLogin = true;
                    } else {
                        int sessionId = (int) redisUtil.get(key);
                        if (!netVideoControlService.heartBeat(sessionId, videoServerId)) {
                            log.info("VideoServer offline," + videoServerId);
                            netVideoControlService.logout(sessionId, videoServerId);
                            needLogin = true;
                        }
                    }
                    if (needLogin) {
                        int sessionID = login(videoServerId, vendor, videoServer.getIp(), videoServer.getPort(), videoServer.getUsername(), videoServer.getPassword());
                        if (sessionID >= 0) {
                            // 监听告警
                            Boolean listen = netVideoControlService.listenAlarm(sessionID, videoServerId);
                            log.info("VideoServer listen result," + listen);
                        }
                    }
                }

                for (VideoDto video : videoService.selectAllDto()
                        ) {
                    if (video.getVideoServer() != null) {
                        continue;
                    }
                    if (!video.getActived()) {
                        continue;
                    }
                    boolean needLogin = false;
                    int videoId = video.getId();
                    int vendor = video.getVendor();
                    VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
                    String key = Constants.REDIS_VIDEOSERVER_SESSION + videoId;
                    if (!redisUtil.hasKey(key)) {
                        log.info("Video not login," + videoId);
                        needLogin = true;
                    } else {
                        int sessionId = (int) redisUtil.get(key);
                        if (!netVideoControlService.heartBeat(sessionId, videoId)) {
                            log.info("Video offline," + videoId);
                            netVideoControlService.logout(sessionId, videoId);
                            needLogin = true;
                        }
                    }
                    if (needLogin) {
                        int sessionID = login(videoId, vendor, video.getIp(), video.getPort(), video.getUsername(), video.getPassword());
                        if (sessionID >= 0) {
                            // 监听告警
                            Boolean listen = netVideoControlService.listenAlarm(sessionID, videoId);
                            log.info("Video listen result," + listen);
                        }
                    }
                }

                for (VideoDto video : videoService.selectAllDto()
                        ) {
                    if (video.getObjectType() == ObjectEnum.Intercom.getValue()) {
                        int videoId = video.getId();
                        int vendor = video.getVendor();
                        VideoControlService netVideoControlService = videoControlServiceMap.get(vendor);
                        String sessionKey = Constants.REDIS_VIDEOSERVER_SESSION + videoId;
                        if (!redisUtil.hasKey(sessionKey)) {
                            continue;
                        }
                        int sessionId = (int) redisUtil.get(sessionKey);
                        String intercomKey = Constants.REDIS_INTERCOM_REMOTE_SESSION + videoId;
                        boolean needConnectIntercom = false;
                        if (!redisUtil.hasKey(intercomKey)) {
                            log.info("Video not connect intercom," + videoId);
                            needConnectIntercom = true;
                        }
                        if (needConnectIntercom) {
                            // 建立可视对讲长连接
                            int result = netVideoControlService.connectIntercom(sessionId, video.getIp());
                            if (result >= 0) {
                                log.info("Video connect intercom ok:" + result);
                                redisUtil.set(intercomKey, result);
                            } else {
                                log.info("Video connect intercom error," + netVideoControlService.getLastError());
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("VideoHeartBeat error", ex);
            }
        }
    }

    // 获取视频录像线程
    class RecordRunner implements Runnable {

        private VideoControlService videoControlService;
        private Integer sessionID;
        private Integer channelNo;
        private Date startTime;
        private Date endTime;
        private String fileName;

        public RecordRunner(VideoControlService videoControlService, Integer sessionID, Integer channelNo, Date startTime, Date endTime, String fileName) {
            this.videoControlService = videoControlService;
            this.sessionID = sessionID;
            this.channelNo = channelNo;
            this.startTime = startTime;
            this.endTime = endTime;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            log.info("PatrolPresetRunner run:" + fileName);
            Boolean recordResult = videoControlService.getFileByTime(sessionID, channelNo, startTime, endTime, fileName);
            if (!recordResult) {
                log.error(String.format("Video Alarm Get Record File Fail:[%s][%s]", fileName, videoControlService.getLastError()));
            }
        }
    }

    // 抓图线程
    class PictureRunner implements Runnable {

        private VideoControlService videoControlService;
        private Integer sessionID;
        private Integer channelNo;
        private String fileName;
        private Integer vendor;

        public PictureRunner(VideoControlService videoControlService, Integer vendor, Integer sessionID, Integer channelNo, String fileName) {
            this.videoControlService = videoControlService;
            this.sessionID = sessionID;
            this.channelNo = channelNo;
            this.fileName = fileName;
            this.vendor = vendor;
        }

        @Override
        public void run() {
            log.info("PictureRunner run:" + fileName);
            Boolean catPicResult = videoControlService.capturePicture(sessionID, channelNo, fileName);
            if (catPicResult) {
                // 大华异步截图 等待时间较长

                // 宇视截图 会自动加上格式后缀
                if (vendor == VideoVendorEnum.Uniview.getValue()) {
                    File newName = new File(fileName);
                    String oldFileName = fileName + ".jpg";
                    File oldName = new File(oldFileName);
                    oldName.renameTo(newName);
                }
            } else {
                log.error(String.format("Video Alarm Capture Picture Error:[%s][%s]", fileName, videoControlService.getLastError()));
            }
        }
    }

    // 按卡号下载人脸线程
    class GetFaceRunner implements Runnable {

        private VideoControlService videoControlService;
        private Integer sessionID;
        private String cardNo;

        public GetFaceRunner(VideoControlService videoControlService, Integer sessionID, String cardNo) {
            this.videoControlService = videoControlService;
            this.sessionID = sessionID;
            this.cardNo = cardNo;
        }

        @Override
        public void run() {
            log.info("GetFaceRunner run:" + cardNo);
            Boolean result = videoControlService.getFace(sessionID, cardNo);
            if (!result) {
                log.info(String.format("Video Download Fsce Picture Result:[%s][%s]", cardNo, videoControlService.getLastError()));
            }
        }
    }

    // 预置位巡检线程
    class PatrolPresetRunner implements Runnable {

        private PatrolPresetJob job;

        public PatrolPresetRunner(PatrolPresetJob job) {
            this.job = job;
        }

        @Override
        public void run() {
            log.info("PatrolPresetRunner run:" + job.getVideoId());
            int measObjID = 0;
            VideoDto video = videoService.selectDtoByPrimaryKey(job.getVideoId());
            VideoServer videoServer = video.getVideoServer();
            int vendor = 0;
            if (videoServer == null) {
                vendor = video.getVendor();
                measObjID = video.getId();
            } else {
                vendor = videoServer.getVendor();
                measObjID = videoServer.getId();
            }
            int channelNo = video.getChannelNo();
            int sessionID = -1;
            String sessionKey = Constants.REDIS_VIDEOSERVER_SESSION + measObjID;
            if (!redisUtil.hasKey(sessionKey)) {
                log.info("video not login:" + measObjID);
                return;
            }
            VideoControlService videoControlService = videoControlServiceMap.get(vendor);
            sessionID = (int) redisUtil.get(sessionKey);
            try {
                for (Integer preset : job.getPresets()) {
                    boolean result = videoControlService.gotoPreset(sessionID, channelNo, preset);
                    String stringTemp = String.format("Patrol Preset Result,[%s][%s][%s][%s]", measObjID, channelNo, preset, result);
                    log.info(stringTemp);
                    Thread.sleep(job.getStayTime() * 1000);
                }
                videoControlService.gotoPreset(sessionID, channelNo, video.getDefaultPreset());
                stopPatrolPreset(job);
            } catch (InterruptedException e) {
                log.error("PatrolPresetRunner error", e);
            }
        }
    }

    // 原始视频告警处理线程
    class OriginVideoAlarmRunner implements Runnable {

        private VideoAlarm videoAlarm;

        public OriginVideoAlarmRunner(VideoAlarm videoAlarm) {
            this.videoAlarm = videoAlarm;
        }

        @Override
        public void run() {
            // 获取告警类型
            int alarmTypeId = videoAlarm.getAlarmTypeId();
            AlarmType alarmType = alarmTypeService.selectByPrimaryKey(alarmTypeId);
            if (alarmType == null) {
                log.info("ProcessAlarm not find alarmType:" + alarmTypeId);
                return;
            }
            // 多通道告警
            for (int channel : videoAlarm.getChannelList()
                    ) {
                int objId = 0;
                boolean deploy = false;
                // 根据告警对象类型确定告警对象 视频服务告警没有通道号 视频告警必须有通道号或IP
                ObjectEnum objectType = ObjectEnum.getEnum(alarmType.getObjectType());
                if (objectType == ObjectEnum.VideoServer) {
                    VideoServerDto videoServerDto = getAlarmVideoServer(videoAlarm);
                    if (videoServerDto != null) {
                        objId = videoServerDto.getId();
                        deploy = videoServerDto.getActived();
                    } else {
                        log.info("ProcessAlarm server not find:" + channel);
                    }
                } else {
                    MeasObject video = getAlarmVideo(videoAlarm, channel);
                    if (video != null) {
                        deploy = video.getActived();
                        objId = video.getId();
                    } else {
                        log.info("ProcessAlarm video not find:" + channel);
                    }
                }
                log.info("OriginVideoAlarmRunner run:" + objId);
                // 是否布防
                if (!deploy) {
                    log.info("ProcessAlarm not deploy:" + channel);
                    continue;
                }
                // 清除告警
                String alarmKey = String.format("%s_%s_%s", objId, alarmTypeId, channel);
                List<Alarm> uncleanedAlarm = alarmService.selectUncleaned(objId, alarmTypeId, String.valueOf(channel));
                if (videoAlarm.getCleaned()) {
                    if (uncleanedAlarm != null && uncleanedAlarm.size() > 0) {
                        for (Alarm alarm : uncleanedAlarm
                                ) {
                            log.info("Auto clean alarm:", alarmKey);
                            alarmService.clean(alarm.getId(), videoAlarm.getTime());
                        }
                    }
                    continue;
                } else {
                    // 升级告警 判断重复告警之前 代表活动告警
                    if (uncleanedAlarm != null && uncleanedAlarm.size() > 0) {
                        // 满足升级条件
                        if (upgradeAlarmByVideo(objId)) {
                            log.info("upgradeAlarmByVideo already alarm:", objId);
                            continue;
                        }
                    }
                }
                // 是否重复告警
                if (checkRepetitionOriginVideoAlarm(alarmKey)) {
                    continue;
                }
                // 生成告警
                Alarm alarm = raiseVideoAlarm(objId, alarmType, channel, videoAlarm);
                // 入库
                alarmService.insert(alarm);
                // 发送MQ
                MessageTypeEnum messageType = MessageTypeEnum.VideoAlarm;
                if (objectType == ObjectEnum.Intercom) {
                    messageType = MessageTypeEnum.IntercomAlarm;
                } else if (objectType == ObjectEnum.VideoServer) {
                    messageType = MessageTypeEnum.VideoServerAlarm;
                }
                Message message = new Message();
                message.setType(messageType);
                message.setBody(alarm);
                mqSender.sendMessageToFront(message);
                // 满足升级条件
                if (upgradeAlarmByVideo(objId)) {
                    log.info("upgradeAlarmByVideo new alarm:", objId);
                    continue;
                }
                // 处理告警数据
                if (AlarmCategoryEnum.getEnum(alarmType.getCategory()) == AlarmCategoryEnum.Biz) {
                    processAlarmData(alarm, alarm.getObjId(), -1, videoAlarm.getFileName());
                    processAlarmLinkage(alarm);
                }
            }
        }
    }

    // 告警联动处理线程
    class AlarmLinkageRunner implements Runnable {

        private Alarm alarm;

        public AlarmLinkageRunner(Alarm alarm) {
            this.alarm = alarm;
        }

        @Override
        public void run() {
            String[] sourceArray = alarm.getSource().split("-");
            int sourceStart = Integer.valueOf(sourceArray[0]);
            int sourceEnd = sourceStart;
            if (sourceArray.length > 1) {
                sourceEnd = Integer.valueOf(sourceArray[1]);
            }
            int objId = alarm.getObjId();
            log.info("OriginVideoAlarmRunner run:" + objId);
            List<VideoLinkage> videoLinkageList = videoLinkageService.selectAll();
            for (VideoLinkage videoLinkage : videoLinkageList
                    ) {
                if (!videoLinkage.getActived()) {
                    continue;
                }
                if (videoLinkage.getObjId() == objId) {
                    int start = videoLinkage.getStartLocation();
                    int end = videoLinkage.getEndLocation();
                    if (sourceStart >= start && sourceEnd <= end) {
                        processAlarmData(alarm, videoLinkage.getVideoId(), videoLinkage.getPreset(), null);
                        // 告警确认升级判断
                        upgradeAlarmByUnVideo(alarm, videoLinkage);
                    }
                }
            }
        }
    }
}
