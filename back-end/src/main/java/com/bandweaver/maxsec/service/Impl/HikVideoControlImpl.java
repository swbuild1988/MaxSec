package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.NetVideo.HCNetSDK;
import com.bandweaver.maxsec.TestRabbitMQ.MQSender;
import com.bandweaver.maxsec.constants.MessageTypeEnum;
import com.bandweaver.maxsec.dto.IntercomCard;
import com.bandweaver.maxsec.dto.Message;
import com.bandweaver.maxsec.dto.VideoAlarm;
import com.bandweaver.maxsec.schedule.ScheduleServiceManage;
import com.bandweaver.maxsec.service.VideoControlService;
import com.bandweaver.maxsec.util.CommonUtil;
import com.bandweaver.maxsec.util.FileUtils;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class HikVideoControlImpl implements VideoControlService {

    /**
     * 按卡号获取人脸会话标识
     */
    private final String GET_FACE_BY_CARD = "getfacebycard";
    /**
     * 按卡号设置人脸会话标识
     */
    private final String SET_FACE_BY_CARD = "setfacebycard";
    /**
     * 获取卡会话标识
     */
    private final String GET_CARD = "getcard";
    /**
     * 设置卡会话标识
     */
    private final String SET_CARD = "setcard";
    /**
     * 删除卡会话标识
     */
    private final String DELETE_CARD = "deletecard";

    @Autowired
    private MQSender mqSender;
    @Autowired
    private ScheduleServiceManage scheduleServiceManage;

    private ConcurrentHashMap<String, Pointer> pointerMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> remoteConfigMap = new ConcurrentHashMap<>();
    private List<IntercomCard> cardList = new ArrayList<>();

    private HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
    private FMSGCallBack_V31 fMSFCallBack_V31;//告警回调函数实现
    private FIntercomRemoteConfigCallback fIntercomRemoteConfigCB;//可视对讲消息回调函数实现
    private FGetFaceRemoteConfigCallback fGetFaceRemoteConfigCB;//可视对讲人脸图片下载回调函数实现
    private FSetFaceRemoteConfigCallback fSetFaceRemoteConfigCB;//可视对讲人脸图片设置回调函数实现
    private FGetCardRemoteConfigCallback fGetCardRemoteConfigCB;//可视对讲获取卡片回调函数实现
    private FSetCardRemoteConfigCallback fSetCardRemoteConfigCB;//可视对讲设置卡片回调函数实现
    private FDeleteCardRemoteConfigCallback fDeleteCardRemoteConfigCB;//可视对讲设置卡片回调函数实现


    public int login(String serverIP, int serverPort, String userName, String password, int measObjID) {
        HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
        //注册
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(serverIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, serverIP.length());

        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(userName.getBytes(), 0, m_strLoginInfo.sUserName, 0, userName.length());

        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(password.getBytes(), 0, m_strLoginInfo.sPassword, 0, password.length());

        m_strLoginInfo.wPort = (short) serverPort;

        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是

        m_strLoginInfo.write();
        HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息
        int lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);

        if (lUserID == -1) {
            hCNetSDK.NET_DVR_GetLastError();
        }
        return lUserID;
    }

    public boolean logout(int sessionID, int measObjID) {
        boolean result = hCNetSDK.NET_DVR_Logout_V30(sessionID);
        return result;
    }

    public boolean heartBeat(int sessionID, int measObjID) {
        HCNetSDK.NET_DVR_WORKSTATE_V30 net_dvr_workstate_v30 = new HCNetSDK.NET_DVR_WORKSTATE_V30();
        boolean result = hCNetSDK.NET_DVR_GetDVRWorkState_V30(sessionID, net_dvr_workstate_v30);
        return result;
    }

    public boolean init() {
        boolean initSuc = hCNetSDK.NET_DVR_Init();
        if (fMSFCallBack_V31 == null) {
            fMSFCallBack_V31 = new FMSGCallBack_V31();
        }
        if (fIntercomRemoteConfigCB == null) {
            fIntercomRemoteConfigCB = new FIntercomRemoteConfigCallback();
        }
        if (fGetFaceRemoteConfigCB == null) {
            fGetFaceRemoteConfigCB = new FGetFaceRemoteConfigCallback();
        }
        if (fSetFaceRemoteConfigCB == null) {
            fSetFaceRemoteConfigCB = new FSetFaceRemoteConfigCallback();
        }
        if (fGetCardRemoteConfigCB == null) {
            fGetCardRemoteConfigCB = new FGetCardRemoteConfigCallback();
        }
        if (fSetCardRemoteConfigCB == null) {
            fSetCardRemoteConfigCB = new FSetCardRemoteConfigCallback();
        }
        if (fDeleteCardRemoteConfigCB == null) {
            fDeleteCardRemoteConfigCB = new FDeleteCardRemoteConfigCallback();
        }
        return initSuc;
    }

    public boolean startAlarmService() {
        boolean result = false;
        if (fMSFCallBack_V31 != null) {
            Pointer pUser = null;
            result = hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack_V31, pUser);
        }
        if (!result) {
            hCNetSDK.NET_DVR_GetLastError();
        }
        return result;
    }

    public boolean listenAlarm(int sessionID, int measObjID) {
        boolean result = false;
        HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
        m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
        m_strAlarmInfo.byLevel = 1;//智能交通布防优先级：0- 一等级（高），1- 二等级（中），2- 三等级（低）
        m_strAlarmInfo.byAlarmInfoType = 1;//智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
        m_strAlarmInfo.byDeployType = 1; //布防类型(仅针对门禁主机、人证设备)：0-客户端布防(会断网续传)，1-实时布防(只上传实时数据)
        m_strAlarmInfo.write();
        int lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(sessionID, m_strAlarmInfo);
        result = lAlarmHandle != -1;
        return result;
    }

    public int connectIntercom(int sessionID, String ip) {
        Pointer pointer = new Memory(ip.length() + 1);
        pointer.setString(0, ip);
        HCNetSDK.NET_DVR_VIDEO_CALL_COND struVideoCallCond = new HCNetSDK.NET_DVR_VIDEO_CALL_COND();
        struVideoCallCond.dwSize = struVideoCallCond.size();
        struVideoCallCond.write();
        int intercomHandle = hCNetSDK.NET_DVR_StartRemoteConfig(sessionID, hCNetSDK.NET_DVR_VIDEO_CALL_SIGNAL_PROCESS, struVideoCallCond.getPointer(), struVideoCallCond.size(), fIntercomRemoteConfigCB, pointer);
        if (intercomHandle >= 0) {
            pointerMap.put(ip, pointer);
        }
        return intercomHandle;
    }

    public boolean disconnectIntercom(int intercomID) {
        return hCNetSDK.NET_DVR_StopRemoteConfig(intercomID);
    }

    private boolean stopRemoteConfig(String key) {
        Boolean result = false;
        if (remoteConfigMap.containsKey(key)) {
            int handle = remoteConfigMap.get(key);
            //result = hCNetSDK.NET_DVR_StopRemoteConfig(handle);
            remoteConfigMap.remove(key);
            pointerMap.remove(key);
            log.info(String.format("stopRemoteConfig ok [%s][%s]", key, result));
            result = true;
        } else {
            log.info(String.format("stopRemoteConfig not find key [%s][%s]", key, result));
        }
        return result;
    }

    public boolean controlIntercom(int intercomID, int command) {
        //接听设备呼叫
        HCNetSDK.NET_DVR_VIDEO_CALL_PARAM struVideCallParam = new HCNetSDK.NET_DVR_VIDEO_CALL_PARAM();
        struVideCallParam.dwSize = struVideCallParam.size();
        struVideCallParam.dwCmdType = command;//信令类型：0- 请求呼叫，1- 取消本次呼叫，2- 接听本次呼叫，3- 拒绝本地来电呼叫，4- 被叫响铃超时，5- 结束本次通话，6- 设备正在通话中，7- 客户端正在通话中
        struVideCallParam.write();
        return hCNetSDK.NET_DVR_SendRemoteConfig(intercomID, 0, struVideCallParam.getPointer(), struVideCallParam.size());
    }

    public boolean getFace(int sessionID, String cardNo) {
        Pointer pointer = new Memory(cardNo.length() + 1);
        pointer.setString(0, cardNo);
        HCNetSDK.NET_DVR_FACE_PARAM_COND struCond = new HCNetSDK.NET_DVR_FACE_PARAM_COND();
        struCond.dwSize = struCond.size();
        struCond.dwFaceNum = 0;
        struCond.byFaceID = 1;
        struCond.byCardNo = cardNo.getBytes();
        struCond.byEnableCardReader[0] = 1;
        struCond.write();
        int m_lGetFaceParamCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(sessionID, HCNetSDK.NET_DVR_GET_FACE_PARAM_CFG, struCond.getPointer(), struCond.size(), fGetFaceRemoteConfigCB, pointer);
        if (m_lGetFaceParamCfgHandle >= 0) {
            String key = GET_FACE_BY_CARD + cardNo;
            pointerMap.put(key, pointer);
        }
        return m_lGetFaceParamCfgHandle >= 0;
    }

    public boolean setFace(int sessionID, IntercomCard card) {
        String cardNo = card.getCardNo();
        Pointer pointer = new Memory(cardNo.length() + 1);
        pointer.setString(0, cardNo);
        HCNetSDK.NET_DVR_FACE_PARAM_COND struCond = new HCNetSDK.NET_DVR_FACE_PARAM_COND();
        struCond.dwSize = struCond.size();
        struCond.dwFaceNum = 1;
        struCond.byFaceID = 1;
        struCond.byCardNo = CommonUtil.stringToByte(cardNo, null);
        struCond.byEnableCardReader[0] = 1;
        struCond.write();
        int m_lGetFaceParamCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(sessionID, HCNetSDK.NET_DVR_SET_FACE_PARAM_CFG, struCond.getPointer(), struCond.size(), fSetFaceRemoteConfigCB, pointer);
        if (m_lGetFaceParamCfgHandle >= 0) {
            String key = SET_FACE_BY_CARD + cardNo;
            remoteConfigMap.put(key, m_lGetFaceParamCfgHandle);
            pointerMap.put(key, pointer);
            // 下发人脸
            HCNetSDK.NET_DVR_FACE_PARAM_CFG faceCfg = getFaceCfg(card);
            boolean m_lSetCardParamCfgHandle = hCNetSDK.NET_DVR_SendRemoteConfig(m_lGetFaceParamCfgHandle, 9, faceCfg.getPointer(), faceCfg.size());
            log.info(String.format("sendFace[%s][%s]", card.getCardNo(), m_lSetCardParamCfgHandle));

        }
        return m_lGetFaceParamCfgHandle >= 0;
    }

    public boolean getCard(int sessionID) {
        cardList.clear();
        String key = GET_CARD;
        Pointer pointer = new Memory(key.length() + 1);
        pointer.setString(0, key);
        HCNetSDK.NET_DVR_CARD_CFG_COND struCond = new HCNetSDK.NET_DVR_CARD_CFG_COND();
        struCond.dwSize = struCond.size();
        struCond.byCheckCardNo = 1;
        struCond.dwCardNum = 0xffffffff;
        struCond.write();
        int m_lGetFaceParamCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(sessionID, HCNetSDK.NET_DVR_GET_CARD_CFG_V50, struCond.getPointer(), struCond.size(), fGetCardRemoteConfigCB, pointer);
        if (m_lGetFaceParamCfgHandle >= 0) {
            remoteConfigMap.put(key, m_lGetFaceParamCfgHandle);
            pointerMap.put(key, pointer);
        } else {
            log.info("getCard error:" + getLastError());
        }
        return m_lGetFaceParamCfgHandle >= 0;
    }

    public boolean setCard(int sessionID, IntercomCard card) {
        Pointer pointer = new Memory(card.getCardNo().length() + 1);
        pointer.setString(0, card.getCardNo());
        HCNetSDK.NET_DVR_CARD_CFG_COND struCond = new HCNetSDK.NET_DVR_CARD_CFG_COND();
        struCond.dwSize = struCond.size();
        struCond.byCheckCardNo = 1;
        struCond.dwCardNum = 1;
        struCond.write();
        int m_lGetFaceParamCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(sessionID, HCNetSDK.NET_DVR_SET_CARD_CFG_V50, struCond.getPointer(), struCond.size(), fSetCardRemoteConfigCB, pointer);
        if (m_lGetFaceParamCfgHandle >= 0) {
            String key = SET_CARD + card.getCardNo();
            remoteConfigMap.put(key, m_lGetFaceParamCfgHandle);
            pointerMap.put(key, pointer);
            // 下发卡片
            HCNetSDK.NET_DVR_CARD_CFG_V50 cardCfg = getCardCfg(card, 1);
            boolean m_lSetCardParamCfgHandle = hCNetSDK.NET_DVR_SendRemoteConfig(m_lGetFaceParamCfgHandle, 3, cardCfg.getPointer(), cardCfg.size());
            log.info(String.format("sendCard[%s][%s]", card.getCardNo(), m_lSetCardParamCfgHandle));
        }
        return m_lGetFaceParamCfgHandle >= 0;
    }

    public boolean deleteCard(int sessionID, IntercomCard card) {
        Pointer pointer = new Memory(card.getCardNo().length() + 1);
        pointer.setString(0, card.getCardNo());
        HCNetSDK.NET_DVR_CARD_CFG_COND struCond = new HCNetSDK.NET_DVR_CARD_CFG_COND();
        struCond.dwSize = struCond.size();
        struCond.byCheckCardNo = 1;
        struCond.dwCardNum = 1;
        struCond.write();
        int m_lGetFaceParamCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(sessionID, HCNetSDK.NET_DVR_SET_CARD_CFG_V50, struCond.getPointer(), struCond.size(), fDeleteCardRemoteConfigCB, pointer);
        if (m_lGetFaceParamCfgHandle >= 0) {
            String key = DELETE_CARD + card.getCardNo();
            remoteConfigMap.put(key, m_lGetFaceParamCfgHandle);
            pointerMap.put(key, pointer);
            // 删除卡片
            HCNetSDK.NET_DVR_CARD_CFG_V50 cardCfg = getCardCfg(card, 0);
            boolean m_lSetCardParamCfgHandle = hCNetSDK.NET_DVR_SendRemoteConfig(m_lGetFaceParamCfgHandle, 3, cardCfg.getPointer(), cardCfg.size());
            log.info(String.format("deleteCard[%s][%s]", card.getCardNo(), m_lSetCardParamCfgHandle));
        }
        return m_lGetFaceParamCfgHandle >= 0;
    }

    private HCNetSDK.NET_DVR_CARD_CFG_V50 getCardCfg(IntercomCard card, int valid) {
        HCNetSDK.NET_DVR_CARD_CFG_V50 struCardCfg = new HCNetSDK.NET_DVR_CARD_CFG_V50();
        struCardCfg.dwSize = struCardCfg.size();
        byte[] cardByte = CommonUtil.stringToByte(card.getCardNo(), null);
        for (int i = 0; i < cardByte.length; i++) {
            struCardCfg.byCardNo[i] = cardByte[i];
        }
        struCardCfg.dwModifyParamType |= 0x1;
        struCardCfg.dwModifyParamType |= 0x2;
        struCardCfg.dwModifyParamType |= 0x4;
        struCardCfg.dwModifyParamType |= 0x8;
        struCardCfg.dwModifyParamType |= 0x10;
        struCardCfg.dwModifyParamType |= 0x20;
        struCardCfg.dwModifyParamType |= 0x40;
        struCardCfg.dwModifyParamType |= 0x80;
        struCardCfg.dwModifyParamType |= 0x100;
        struCardCfg.dwModifyParamType |= 0x200;
        struCardCfg.dwModifyParamType |= 0x400;
        struCardCfg.dwModifyParamType |= 0x800;
        struCardCfg.dwModifyParamType |= 0x1000;
        struCardCfg.dwModifyParamType |= 0x2000;
        struCardCfg.dwModifyParamType |= 0x4000;
        struCardCfg.dwModifyParamType |= 0x8000;
        struCardCfg.dwModifyParamType |= 0x10000;
        struCardCfg.dwModifyParamType |= 0x40000;
        struCardCfg.byCardValid = (byte) valid;
        struCardCfg.byCardType = 1;
        struCardCfg.byDoorRight[0] = 1;
        struCardCfg.struValid.byEnable = 1;
        struCardCfg.struValid.struBeginTime = getHkExTime(card.getBegin());
        struCardCfg.struValid.struEndTime = getHkExTime(card.getEnd());
        struCardCfg.dwEmployeeNo = Integer.valueOf(card.getJobNo());
        byte[] nameByte = CommonUtil.stringToByte(card.getName(), "gbk");
        for (int i = 0; i < nameByte.length; i++) {
            struCardCfg.byName[i] = nameByte[i];
        }
        struCardCfg.wDepartmentNo = (short) card.getDepartment();
        struCardCfg.write();
        return struCardCfg;
    }

    private HCNetSDK.NET_DVR_FACE_PARAM_CFG getFaceCfg(IntercomCard card) {
        HCNetSDK.NET_DVR_FACE_PARAM_CFG struCardCfg = new HCNetSDK.NET_DVR_FACE_PARAM_CFG();
        byte[] cardByte = CommonUtil.stringToByte(card.getCardNo(), null);
        for (int i = 0; i < cardByte.length; i++) {
            struCardCfg.byCardNo[i] = cardByte[i];
        }
        struCardCfg.byFaceID = 1;
        struCardCfg.byFaceDataType = 1;
        struCardCfg.byEnableCardReader[0] = 1;
        byte[] fileByte = CommonUtil.stringToByte(card.getFileName(), "gbk");
        for (int i = 0; i < fileByte.length; i++) {
            struCardCfg.byRes[i] = fileByte[i];
        }
        HCNetSDK.NET_DVR_FACE_BUFFER bufferCfg = new HCNetSDK.NET_DVR_FACE_BUFFER();
        bufferCfg.buffer = FileUtils.getFileByte(card.getFileName());
        bufferCfg.write();
        struCardCfg.dwFaceLen = bufferCfg.buffer.length;
        struCardCfg.pFaceBuffer = bufferCfg.getPointer();
        struCardCfg.dwSize = struCardCfg.size();
        struCardCfg.write();
        return struCardCfg;
    }

    public boolean controlGateway(int sessionID, int index, int command) {
        return hCNetSDK.NET_DVR_ControlGateway(sessionID, index, command);
    }

    public int getLastError() {
        return hCNetSDK.NET_DVR_GetLastError();
    }

    public Date getCurrentTime(int sessionID) {
        HCNetSDK.NET_DVR_TIME m_strTimeConfig = new HCNetSDK.NET_DVR_TIME();
        IntByReference ibrBytesReturned = new IntByReference(0);
        m_strTimeConfig.write();
        Pointer lpConfig = m_strTimeConfig.getPointer();
        boolean getDVRConfigSuc = hCNetSDK.NET_DVR_GetDVRConfig(sessionID, HCNetSDK.NET_DVR_GET_TIMECFG,
                0, lpConfig, m_strTimeConfig.size(), ibrBytesReturned);
        m_strTimeConfig.read();
        if (getDVRConfigSuc) {
            return getTimeByHik(m_strTimeConfig);
        } else {
            return null;
        }
    }

    private Date getTimeByHik(HCNetSDK.NET_DVR_TIME time) {
        return new Date(time.dwYear - 1900, time.dwMonth - 1, time.dwDay, time.dwHour, time.dwMinute, time.dwSecond);
    }

    private Date getTimeByHik(HCNetSDK.NET_DVR_TIME_EX time) {
        return new Date(time.wYear - 1900, time.byMonth - 1, time.byDay, time.byHour, time.byMinute, time.bySecond);
    }

    public boolean gotoPreset(int sessionID, int channelNo, int preset) {
        boolean result = hCNetSDK.NET_DVR_PTZPreset_Other(sessionID, channelNo + 1, HCNetSDK.GOTO_PRESET, preset);
        return result;
    }

    public boolean capturePicture(int sessionID, int channelNo, String fileName) {
        HCNetSDK.NET_DVR_JPEGPARA lpJpegPara = new HCNetSDK.NET_DVR_JPEGPARA();
        lpJpegPara.wPicQuality = 1;
        lpJpegPara.wPicSize = 0;
        return hCNetSDK.NET_DVR_CaptureJPEGPicture(sessionID, channelNo + 1, lpJpegPara, fileName);
    }

    public boolean getFileByTime(int sessionID, int channelNo, Date start, Date end, String fileName) {
        HCNetSDK.NET_DVR_TIME struStartTime = getHkTime(start);
        HCNetSDK.NET_DVR_TIME struStopTime = getHkTime(end);
        HCNetSDK.NET_DVR_PLAYCOND struDownloadCond = new HCNetSDK.NET_DVR_PLAYCOND();
        struDownloadCond.read();
        struDownloadCond.dwChannel = channelNo + 1;
        struDownloadCond.struStartTime = struStartTime;
        struDownloadCond.struStopTime = struStopTime;
        struDownloadCond.write();
        int m_lLoadHandle = hCNetSDK.NET_DVR_GetFileByTime_V40(sessionID, fileName.getBytes(), struDownloadCond);
        if (m_lLoadHandle >= 0) {
            log.info(String.format("Video Alarm Get Record File Start:[%s][%s]", m_lLoadHandle, fileName));
            IntByReference intByRef = new IntByReference(0);
            Pointer lpInBuffer = intByRef.getPointer();
            Boolean result = hCNetSDK.NET_DVR_PlayBackControl_V40(m_lLoadHandle, HCNetSDK.NET_DVR_PLAYSTART, lpInBuffer, 4, null, null);
            if (result) {
                String key = new Date().getTime() + "DownloadRunner" + m_lLoadHandle;
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleWithFixedDelay(new DownloadRunner(m_lLoadHandle, key), 0, 5, TimeUnit.SECONDS);
                scheduleServiceManage.put(key, scheduler);
            }
            return result;
        } else {
            return false;
        }
    }

    class DownloadRunner implements Runnable {

        private Integer m_lLoadHandle;
        private String key;

        public DownloadRunner(Integer m_lLoadHandle, String key) {
            this.m_lLoadHandle = m_lLoadHandle;
            this.key = key;
        }

        @Override
        public void run() {
            int pos = hCNetSDK.NET_DVR_GetDownloadPos(m_lLoadHandle);
            log.debug(String.format("Video Alarm Get Record File Pos:[%s][%s]", m_lLoadHandle, pos));
            if (pos >= 100) {
                log.info(String.format("Video Alarm Get Record File End:[%s][%s]", m_lLoadHandle, pos));
                hCNetSDK.NET_DVR_StopGetFile(m_lLoadHandle);
                scheduleServiceManage.shutdown(key);
            }
        }
    }

    public HCNetSDK.NET_DVR_TIME getHkTime(Date time) {
        HCNetSDK.NET_DVR_TIME structTime = new HCNetSDK.NET_DVR_TIME();
        String str = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(time);
        String[] times = str.split("-");
        structTime.dwYear = Integer.parseInt(times[0]);
        structTime.dwMonth = Integer.parseInt(times[1]);
        structTime.dwDay = Integer.parseInt(times[2]);
        structTime.dwHour = Integer.parseInt(times[3]);
        structTime.dwMinute = Integer.parseInt(times[4]);
        structTime.dwSecond = Integer.parseInt(times[5]);
        return structTime;
    }

    private HCNetSDK.NET_DVR_TIME_EX getHkExTime(Date time) {
        HCNetSDK.NET_DVR_TIME_EX structTime = new HCNetSDK.NET_DVR_TIME_EX();
        String str = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(time);
        String[] times = str.split("-");
        structTime.wYear = Short.parseShort(times[0]);
        structTime.byMonth = Byte.parseByte(times[1]);
        structTime.byDay = Byte.parseByte(times[2]);
        structTime.byHour = Byte.parseByte(times[3]);
        structTime.byMinute = Byte.parseByte(times[4]);
        structTime.bySecond = Byte.parseByte(times[5]);
        return structTime;
    }

    // 告警回调
    private class FMSGCallBack_V31 implements HCNetSDK.FMSGCallBack_V31 {

        //报警信息回调函数
        public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
            VideoAlarm alarm = alarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
            if (alarm != null) {
                alarm.setObjectId(0);
                alarm.setTime(new Date());
                Message message = new Message();
                message.setType(MessageTypeEnum.VideoOriginAlarm);
                message.setBody(alarm);
                mqSender.sendMessageToBack(message);
            }
            return true;
        }
    }

    // 可视对讲回调
    private class FIntercomRemoteConfigCallback implements HCNetSDK.FRemoteConfigCallback {

        //远程配置回调函数
        public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
            VideoAlarm alarm = intercomDataHandle(dwType, lpBuffer, dwBufLen, pUserData);
            if (alarm != null) {
                alarm.setTime(new Date());
                Message message = new Message();
                message.setType(MessageTypeEnum.IntercomAlarm);
                message.setBody(alarm);
                mqSender.sendMessageToBack(message);
            }
        }
    }

    // 可视对讲下载图片回调
    private class FGetFaceRemoteConfigCallback implements HCNetSDK.FRemoteConfigCallback {

        //远程配置回调函数
        public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
            IntercomCard card = getFaceDataHandle(dwType, lpBuffer, dwBufLen, pUserData);
            if (card != null) {
                Message message = new Message();
                message.setType(MessageTypeEnum.IntercomGetFace);
                message.setBody(card);
                mqSender.sendMessageToBack(message);
            }
        }
    }

    // 可视对讲获取卡片回调
    private class FGetCardRemoteConfigCallback implements HCNetSDK.FRemoteConfigCallback {

        //远程配置回调函数
        public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
            IntercomCard card = getCardDataHandle(dwType, lpBuffer, dwBufLen, pUserData);
            if (card != null) {
                cardList.add(card);
            } else {
                if (stopRemoteConfig(GET_CARD)) {
                    Message message = new Message();
                    message.setType(MessageTypeEnum.IntercomGetCard);
                    message.setBody(cardList);
                    mqSender.sendMessageToFront(message);
                }
            }
        }
    }

    // 可视对讲设置卡片回调
    private class FSetCardRemoteConfigCallback implements HCNetSDK.FRemoteConfigCallback {

        //远程配置回调函数
        public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
            String cardNo = setCardDataHandle(dwType, lpBuffer, dwBufLen, pUserData);
            if (stopRemoteConfig(SET_CARD + cardNo)) {
                IntercomCard card = new IntercomCard();
                card.setCardNo(cardNo);
                Message message = new Message();
                message.setType(MessageTypeEnum.IntercomSetCard);
                message.setBody(card);
                mqSender.sendMessageToFront(message);
            }
        }
    }

    // 可视对讲删除卡片回调
    private class FDeleteCardRemoteConfigCallback implements HCNetSDK.FRemoteConfigCallback {

        //远程配置回调函数
        public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
            String cardNo = deleteCardDataHandle(dwType, lpBuffer, dwBufLen, pUserData);
            if (stopRemoteConfig(DELETE_CARD + cardNo)) {
                IntercomCard card = new IntercomCard();
                card.setCardNo(cardNo);
                Message message = new Message();
                message.setType(MessageTypeEnum.IntercomDeleteCard);
                message.setBody(card);
                mqSender.sendMessageToFront(message);
            }
        }
    }

    // 可视对讲设置图片回调
    private class FSetFaceRemoteConfigCallback implements HCNetSDK.FRemoteConfigCallback {

        //远程配置回调函数
        public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
            String cardNo = setFaceDataHandle(dwType, lpBuffer, dwBufLen, pUserData);
            if (cardNo != null) {
                IntercomCard card = new IntercomCard();
                card.setCardNo(cardNo);
                Message message = new Message();
                message.setType(MessageTypeEnum.IntercomSetFace);
                message.setBody(card);
                mqSender.sendMessageToFront(message);
            }
        }
    }

    private IntercomCard getFaceDataHandle(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        String cardNo = pUserData.getString(0);
        if (dwType == HCNetSDK.NET_SDK_CALLBACK_TYPE.NET_SDK_CALLBACK_TYPE_DATA.getValue()) {
            HCNetSDK.NET_DVR_FACE_PARAM_CFG lpCardCfg = new HCNetSDK.NET_DVR_FACE_PARAM_CFG();
            lpCardCfg.write();
            Pointer pInfoV30 = lpCardCfg.getPointer();
            pInfoV30.write(0, lpBuffer.getByteArray(0, lpCardCfg.size()), 0, lpCardCfg.size());
            lpCardCfg.read();
            Boolean bSendOk = false;
            for (int i = 0; i < lpCardCfg.byEnableCardReader.length; ++i) {
                if (1 == lpCardCfg.byEnableCardReader[i]) {
                    bSendOk = true;
                    break;
                }
            }
            log.info(String.format("[%s]getFaceDataHandle[%s]", cardNo, bSendOk));
            if (bSendOk) {
                String fileName = FileUtils.saveTemp(lpCardCfg.pFaceBuffer, lpCardCfg.dwFaceLen);
                if (fileName != null) {
                    IntercomCard card = new IntercomCard();
                    card.setCardNo(cardNo);
                    card.setFileName(fileName);
                    return card;
                }
            }
        } else if (dwType == HCNetSDK.NET_SDK_CALLBACK_TYPE.NET_SDK_CALLBACK_TYPE_STATUS.getValue()) {
            int dwStatus = lpBuffer.getInt(0);
            log.info(String.format("getFaceDataHandle NET_SDK_CALLBACK_TYPE_STATUS,dwStatus[%s]", dwStatus));
            if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_SUCCESS.getValue()) {
            } else if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_FAILED.getValue()) {
            }
        }
        return null;
    }

    private String setFaceDataHandle(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        String cardNo = pUserData.getString(0);
        if (dwType == HCNetSDK.NET_SDK_CALLBACK_TYPE.NET_SDK_CALLBACK_TYPE_DATA.getValue()) {
            return cardNo;
        } else if (dwType == HCNetSDK.NET_SDK_CALLBACK_TYPE.NET_SDK_CALLBACK_TYPE_STATUS.getValue()) {
            int dwStatus = lpBuffer.getInt(0);
            log.info(String.format("setFaceDataHandle NET_SDK_CALLBACK_TYPE_STATUS,dwStatus[%s]", dwStatus));
            if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_SUCCESS.getValue()) {
            } else if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_FAILED.getValue()) {
            }
            stopRemoteConfig(SET_FACE_BY_CARD + cardNo);
        }
        return null;
    }

    private IntercomCard getCardDataHandle(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        if (dwType == HCNetSDK.NET_SDK_CALLBACK_TYPE.NET_SDK_CALLBACK_TYPE_DATA.getValue()) {
            HCNetSDK.NET_DVR_CARD_CFG_V50 lpCardCfg = new HCNetSDK.NET_DVR_CARD_CFG_V50();
            lpCardCfg.write();
            Pointer pInfoV30 = lpCardCfg.getPointer();
            pInfoV30.write(0, lpBuffer.getByteArray(0, lpCardCfg.size()), 0, lpCardCfg.size());
            lpCardCfg.read();
            IntercomCard card = new IntercomCard();
            card.setCardNo(CommonUtil.byteToString(lpCardCfg.byCardNo, null));
            card.setJobNo(String.valueOf(lpCardCfg.dwEmployeeNo));
            card.setDepartment(lpCardCfg.wDepartmentNo);
            card.setName(CommonUtil.byteToString(lpCardCfg.byName, "gbk"));
            card.setBegin(getTimeByHik(lpCardCfg.struValid.struBeginTime));
            card.setEnd(getTimeByHik(lpCardCfg.struValid.struEndTime));
            log.info(String.format("[%s]getCardDataHandle[%s]", card.getCardNo(), card.getName()));
            return card;
        } else if (dwType == HCNetSDK.NET_SDK_CALLBACK_TYPE.NET_SDK_CALLBACK_TYPE_STATUS.getValue()) {
            int dwStatus = lpBuffer.getInt(0);
            log.info(String.format("getCardDataHandle NET_SDK_CALLBACK_TYPE_STATUS,dwStatus[%s]", dwStatus));
            if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_SUCCESS.getValue()) {
            } else if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_FAILED.getValue()) {
            }
        }
        return null;
    }

    private String setCardDataHandle(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        String cardNo = pUserData.getString(0);
        if (dwType == HCNetSDK.NET_SDK_CALLBACK_TYPE.NET_SDK_CALLBACK_TYPE_STATUS.getValue()) {
            int dwStatus = lpBuffer.getInt(0);
            log.info(String.format("[%s]setCardDataHandle NET_SDK_CALLBACK_TYPE_STATUS,dwStatus[%s]", cardNo, dwStatus));
            if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_SUCCESS.getValue()) {
            } else if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_FAILED.getValue()) {
            }
        }
        return cardNo;
    }

    private String deleteCardDataHandle(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        String cardNo = pUserData.getString(0);
        if (dwType == HCNetSDK.NET_SDK_CALLBACK_TYPE.NET_SDK_CALLBACK_TYPE_STATUS.getValue()) {
            int dwStatus = lpBuffer.getInt(0);
            log.info(String.format("[%s]deleteCardDataHandle NET_SDK_CALLBACK_TYPE_STATUS,dwStatus[%s]", cardNo, dwStatus));
            if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_SUCCESS.getValue()) {
            } else if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_FAILED.getValue()) {
            }
        }
        return cardNo;
    }

    private VideoAlarm intercomDataHandle(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        String ip = pUserData.getString(0);
        int alarmTypeId = 0;
        if (dwType == HCNetSDK.NET_SDK_CALLBACK_TYPE.NET_SDK_CALLBACK_TYPE_DATA.getValue()) {
            HCNetSDK.NET_DVR_VIDEO_CALL_PARAM struCallParam = new HCNetSDK.NET_DVR_VIDEO_CALL_PARAM();
            struCallParam.write();
            Pointer pInfoV30 = struCallParam.getPointer();
            pInfoV30.write(0, lpBuffer.getByteArray(0, struCallParam.size()), 0, struCallParam.size());
            struCallParam.read();
            log.info(String.format("[%s]可视对讲消息类型[%s]", ip, struCallParam.dwCmdType));
            if (struCallParam.dwCmdType == 0) //0-请求呼叫
            {
                alarmTypeId = 3003;
            } else if (struCallParam.dwCmdType == 2) //0-接听本次呼叫
            {
            } else if (struCallParam.dwCmdType == 1) //0-接听本次呼叫
            {
            }
        } else if (dwType == HCNetSDK.NET_SDK_CALLBACK_TYPE.NET_SDK_CALLBACK_TYPE_STATUS.getValue()) {
            int dwStatus = lpBuffer.getInt(0);
            log.info(String.format("[%s]NET_SDK_CALLBACK_TYPE_STATUS,dwStatus[%s],lastError[%s]", ip, dwStatus, getLastError()));
            if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_SUCCESS.getValue()) {
                //DisconnectIntercom(measObjID);
            } else if (dwStatus == HCNetSDK.NET_SDK_CALLBACK_STATUS_NORMAL.NET_SDK_CALLBACK_STATUS_FAILED.getValue()) {
                Message message = new Message();
                message.setType(MessageTypeEnum.IntercomOffline);
                message.setBody(ip);
                mqSender.sendMessageToBack(message);
            }
        }
        if (alarmTypeId > 0) {
            VideoAlarm videoAlarm = new VideoAlarm();
            videoAlarm.setObjectId(0);
            videoAlarm.setIp(ip);
            videoAlarm.setCleaned(false);
            List<Integer> channelList = new ArrayList<>();
            List<String> parasList = new ArrayList<>();
            if (channelList.size() == 0) {
                channelList.add(-1);
            }
            videoAlarm.setChannelList(channelList);
            videoAlarm.setAlarmTypeId(alarmTypeId);
            videoAlarm.setParas(parasList);
            return videoAlarm;
        } else {
            return null;
        }
    }

    private VideoAlarm alarmDataHandle(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        VideoAlarm alarm = null;
        //lCommand是传的报警类型
        switch (lCommand) {
            case HCNetSDK.COMM_ALARM_V30:
                alarm = processCommAlarm_V30(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
                break;
            case HCNetSDK.COMM_ALARM_RULE:
                alarm = processCommAlarm_RULE(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
                break;
            case HCNetSDK.COMM_ALARM_ACS://门禁主机报警上传
                alarm = processCommAlarm_AcsAlarm(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
                break;
            default:
                //报警设备IP地址
                String[] sIP = new String[2];
                sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                log.info(String.format("alarmDataHandle，[{0}][{1}]", lCommand, sIP));
                break;
        }
        return alarm;
    }

    private VideoAlarm processCommAlarm_V30(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        VideoAlarm videoAlarm = new VideoAlarm();
        videoAlarm.setCleaned(false);
        List<Integer> channelList = new ArrayList<>();
        List<String> parasList = new ArrayList<>();
        int alarmTypeId = 0;
        String[] sIP = new String[2];
        String stringAlarm = "";

        HCNetSDK.NET_DVR_ALARMINFO_V30 strAlarmInfoV30 = new HCNetSDK.NET_DVR_ALARMINFO_V30();
        strAlarmInfoV30.write();
        Pointer pInfoV30 = strAlarmInfoV30.getPointer();
        pInfoV30.write(0, pAlarmInfo.getByteArray(0, strAlarmInfoV30.size()), 0, strAlarmInfoV30.size());
        strAlarmInfoV30.read();
        switch (strAlarmInfoV30.dwAlarmType) {
            case 0:
                stringAlarm = "信号量报警，报警报警输入口：" + strAlarmInfoV30.dwAlarmInputNumber + "，触发录像通道：";
                alarmTypeId = 1012;
                break;
            case 1:
                stringAlarm = "硬盘满，报警硬盘号：";
                alarmTypeId = 2000;
                break;
            case 2:
                stringAlarm = "信号丢失，报警通道：";
                alarmTypeId = 1013;
                break;
            case 3:
                stringAlarm = "移动侦测，报警通道：";
                alarmTypeId = 1004;
                break;
            case 4:
                stringAlarm = "硬盘未格式化，报警硬盘号：";
                alarmTypeId = 2001;
                break;
            case 5:
                stringAlarm = "读写硬盘出错，报警硬盘号：";
                alarmTypeId = 2001;
                break;
            case 6:
                stringAlarm = "遮挡报警，报警通道：";
                alarmTypeId = 1005;
                break;
            case 7:
                stringAlarm = "制式不匹配，报警通道";
                alarmTypeId = 254;
                break;
            case 8:
                stringAlarm = "非法访问";
                alarmTypeId = 2003;
                break;
            case 9:
                stringAlarm = "视频信号异常，报警通道";
                alarmTypeId = 1015;
                break;
            case 10:
                stringAlarm = "录像/抓图异常，报警通道";
                alarmTypeId = 1016;
                break;
            case 11:
                stringAlarm = "智能场景变化，报警通道";
                alarmTypeId = 1006;
                break;
            case 12:
                stringAlarm = "阵列异常";
                alarmTypeId = 2004;
                break;
            case 13:
                stringAlarm = "前端/录像分辨率不匹配，报警通道";
                alarmTypeId = 254;
                break;
            case 15:
                stringAlarm = "智能侦测，报警通道";
                alarmTypeId = 1007;
                break;
            default:
                stringAlarm = "其他未知报警信息";
                alarmTypeId = 254;
                parasList.add(String.valueOf(strAlarmInfoV30.dwAlarmType));
                break;
        }
        //报警设备IP地址
        sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
        log.debug(String.format("ProcessCommAlarm_V30，AlarmType[%s][%s]", strAlarmInfoV30.dwAlarmType, stringAlarm));

        if (alarmTypeId == 254 || alarmTypeId == 255) {
            parasList.clear();
            parasList.add(String.valueOf(strAlarmInfoV30.dwAlarmType));
            parasList.add(GetAlarmPara(strAlarmInfoV30));
        } else if (alarmTypeId == 1012) {
            List<Integer> relateChannelList = new ArrayList<Integer>();
            for (int i = 0; i < HCNetSDK.MAX_CHANNUM_V30; i++) {
                if (strAlarmInfoV30.byAlarmRelateChannel[i] == 1) {
                    relateChannelList.add(i);
                }
            }
            channelList.addAll(relateChannelList);
            parasList.add(String.valueOf(strAlarmInfoV30.dwAlarmInputNumber));
            parasList.add(CommonUtil.joinInteger(relateChannelList));
        } else if (alarmTypeId == 2000 || alarmTypeId == 2001) {
            parasList.add(GetAlarmPara(strAlarmInfoV30));
        } else {
            channelList.addAll(GetAlarmChannel(strAlarmInfoV30));
            if (!channelList.isEmpty()) {
                parasList.add(CommonUtil.joinInteger(channelList));
            }
        }
        videoAlarm.setIp(sIP[0]);
        if (channelList.size() == 0) {
            channelList.add(-1);
        }
        videoAlarm.setChannelList(channelList);
        videoAlarm.setAlarmTypeId(alarmTypeId);
        videoAlarm.setParas(parasList);
        return videoAlarm;
    }

    private VideoAlarm processCommAlarm_RULE(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        VideoAlarm videoAlarm = new VideoAlarm();
        videoAlarm.setCleaned(false);
        List<Integer> channelList = new ArrayList<>();
        List<String> parasList = new ArrayList<>();
        int alarmTypeId = 0;
        String[] sIP = new String[2];
        String stringAlarm = "";

        HCNetSDK.NET_VCA_RULE_ALARM strVcaAlarm = new HCNetSDK.NET_VCA_RULE_ALARM();
        strVcaAlarm.write();
        Pointer pVcaInfo = strVcaAlarm.getPointer();
        pVcaInfo.write(0, pAlarmInfo.getByteArray(0, strVcaAlarm.size()), 0, strVcaAlarm.size());
        strVcaAlarm.read();
        int channel = strVcaAlarm.struDevInfo.byIvmsChannel - 1;
        parasList.add(String.valueOf(channel));
        channelList.add(channel);
        switch (strVcaAlarm.struRuleInfo.wEventTypeEx) {
            case 1:
                alarmTypeId = 1001;
                break;
            case 2:
                alarmTypeId = 1008;
                break;
            case 3:
                alarmTypeId = 1009;
                break;
            case 4:
                alarmTypeId = 1000;
                break;
            case 5:
                alarmTypeId = 1011;
                break;
            case 9:
                alarmTypeId = 1003;
                break;
            default:
                alarmTypeId = 255;
                channelList.clear();
                parasList.clear();
                parasList.add(String.valueOf(strVcaAlarm.struRuleInfo.wEventTypeEx));
                parasList.add(String.valueOf(strVcaAlarm.struTargetInfo.dwID));
                break;
        }
        //报警设备IP地址
        sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
        log.debug(String.format("processCommAlarm_RULE，AlarmType[%s][%s]", strVcaAlarm.struRuleInfo.wEventTypeEx, sIP));
        videoAlarm.setIp(sIP[0]);
        videoAlarm.setChannelList(channelList);
        videoAlarm.setAlarmTypeId(alarmTypeId);
        videoAlarm.setParas(parasList);
        return videoAlarm;
    }

    private VideoAlarm processCommAlarm_AcsAlarm(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        VideoAlarm videoAlarm = new VideoAlarm();
        videoAlarm.setCleaned(false);
        List<Integer> channelList = new ArrayList<>();
        List<String> parasList = new ArrayList<>();
        int alarmTypeId = 0;
        String[] sIP = new String[2];

        HCNetSDK.NET_DVR_ACS_ALARM_INFO struAcsAlarm = new HCNetSDK.NET_DVR_ACS_ALARM_INFO();
        struAcsAlarm.write();
        Pointer pVcaInfo = struAcsAlarm.getPointer();
        pVcaInfo.write(0, pAlarmInfo.getByteArray(0, struAcsAlarm.size()), 0, struAcsAlarm.size());
        struAcsAlarm.read();
        String cardNo = new String(struAcsAlarm.struAcsEventInfo.byCardNo).trim();
        parasList.add(String.valueOf(struAcsAlarm.dwMajor));
        parasList.add(String.valueOf(struAcsAlarm.dwMinor));
        parasList.add(cardNo);
        //报警设备IP地址
        sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
        log.debug(String.format("processCommAlarm_AcsAlarm，AlarmType[%s][%s][%s]", struAcsAlarm.dwMajor, struAcsAlarm.dwMinor, sIP));
        videoAlarm.setFileName(FileUtils.saveTemp(struAcsAlarm.pPicData, struAcsAlarm.dwPicDataLen));
        if (struAcsAlarm.dwMajor == 0x01) {
            if (struAcsAlarm.dwMinor == 0x404 || struAcsAlarm.dwMinor == 0x406 || struAcsAlarm.dwMinor == 0x40f || struAcsAlarm.dwMinor == 0x41e || struAcsAlarm.dwMinor == 0x422) {
                // 设备防拆
                alarmTypeId = 3000;
            } else if (struAcsAlarm.dwMinor == 0x405 || struAcsAlarm.dwMinor == 0x407 || struAcsAlarm.dwMinor == 0x410 || struAcsAlarm.dwMinor == 0x41f || struAcsAlarm.dwMinor == 0x423) {
                // 设备防拆恢复
                alarmTypeId = 3000;
                videoAlarm.setCleaned(true);
            }
        } else if (struAcsAlarm.dwMajor == 0x05) {
            if (struAcsAlarm.dwMinor == 0x01 || struAcsAlarm.dwMinor == 0x02 || struAcsAlarm.dwMinor == 0x10 || struAcsAlarm.dwMinor == 0x26
                    || struAcsAlarm.dwMinor == 0x28 || struAcsAlarm.dwMinor == 0x2b || struAcsAlarm.dwMinor == 0x2e || struAcsAlarm.dwMinor == 0x36
                    || struAcsAlarm.dwMinor == 0x39 || struAcsAlarm.dwMinor == 0x3c || struAcsAlarm.dwMinor == 0x3f || struAcsAlarm.dwMinor == 0x42
                    || struAcsAlarm.dwMinor == 0x45 || struAcsAlarm.dwMinor == 0x48 || struAcsAlarm.dwMinor == 0x4b || struAcsAlarm.dwMinor == 0x4d
                    || struAcsAlarm.dwMinor == 0x65 || struAcsAlarm.dwMinor == 0x69 || struAcsAlarm.dwMinor == 0x99 || struAcsAlarm.dwMinor == 0x69) {
                // 认证通过
                alarmTypeId = 3001;
            } else if ((struAcsAlarm.dwMinor >= 0x03 && struAcsAlarm.dwMinor <= 0x09) || (struAcsAlarm.dwMinor >= 0x0c && struAcsAlarm.dwMinor <= 0x0f)
                    || struAcsAlarm.dwMinor == 0x27 || struAcsAlarm.dwMinor == 0x4c || struAcsAlarm.dwMinor == 0x70
                    || (struAcsAlarm.dwMinor >= 0x29 && struAcsAlarm.dwMinor <= 0x2a) || (struAcsAlarm.dwMinor >= 0x2c && struAcsAlarm.dwMinor <= 0x2d)
                    || (struAcsAlarm.dwMinor >= 0x2f && struAcsAlarm.dwMinor <= 0x31) || (struAcsAlarm.dwMinor >= 0x9a && struAcsAlarm.dwMinor <= 0x9b)
                    || (struAcsAlarm.dwMinor >= 0x37 && struAcsAlarm.dwMinor <= 0x38) || (struAcsAlarm.dwMinor >= 0x3a && struAcsAlarm.dwMinor <= 0x3b)
                    || (struAcsAlarm.dwMinor >= 0x3d && struAcsAlarm.dwMinor <= 0x3e) || (struAcsAlarm.dwMinor >= 0x40 && struAcsAlarm.dwMinor <= 0x41)
                    || (struAcsAlarm.dwMinor >= 0x43 && struAcsAlarm.dwMinor <= 0x44) || (struAcsAlarm.dwMinor >= 0x46 && struAcsAlarm.dwMinor <= 0x47)
                    || (struAcsAlarm.dwMinor >= 0x49 && struAcsAlarm.dwMinor <= 0x4a) || (struAcsAlarm.dwMinor >= 0x4e && struAcsAlarm.dwMinor <= 0x50)
                    || (struAcsAlarm.dwMinor >= 0x66 && struAcsAlarm.dwMinor <= 0x68) || (struAcsAlarm.dwMinor >= 0x97 && struAcsAlarm.dwMinor <= 0x98)) {
                // 认证失败
                alarmTypeId = 3002;
            }
        }
        videoAlarm.setIp(sIP[0]);
        channelList.add(0);
        videoAlarm.setChannelList(channelList);
        videoAlarm.setAlarmTypeId(alarmTypeId);
        videoAlarm.setParas(parasList);
        return videoAlarm;
    }

    private List<Integer> GetAlarmChannel(HCNetSDK.NET_DVR_ALARMINFO_V30 struAlarmInfoV30) {
        List<Integer> channelList = new ArrayList<>();
        try {
            for (int i = 0; i < HCNetSDK.MAX_CHANNUM_V30; i++) {
                if (struAlarmInfoV30.byChannel[i] == 1) {
                    channelList.add(i);
                }
            }
        } catch (Exception ex) {
            log.error("GetAlarmChannel error", ex);
        }
        return channelList;
    }

    private String GetAlarmPara(HCNetSDK.NET_DVR_ALARMINFO_V30 struAlarmInfoV30) {
        List<String> parasList = new ArrayList<>();
        try {
            for (int i = 0; i < HCNetSDK.MAX_CHANNUM_V30; i++) {
                if (struAlarmInfoV30.byDiskNumber[i] == 1) {
                    parasList.add(String.valueOf(i));
                }
            }
        } catch (Exception ex) {
            log.error("GetAlarmPara error", ex);
        }
        return CommonUtil.joinString(parasList);
    }
}
