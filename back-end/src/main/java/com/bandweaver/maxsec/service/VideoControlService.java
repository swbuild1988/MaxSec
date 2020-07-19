package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.dto.IntercomCard;

import java.util.Date;

public interface VideoControlService {
    boolean init();

    boolean startAlarmService();

    boolean listenAlarm(int sessionID, int measObjID);

    boolean heartBeat(int sessionID, int measObjID);

    boolean logout(int sessionID, int measObjID);

    int login(String serverIP, int serverPort, String userName, String password, int measObjID);

    int getLastError();

    Date getCurrentTime(int sessionID);

    boolean gotoPreset(int sessionID, int channelNo, int preset);

    boolean capturePicture(int sessionID, int channelNo, String fileName);

    boolean getFileByTime(int sessionID, int channelNo, Date start, Date end, String fileName);

    int connectIntercom(int sessionID, String ip);

    boolean disconnectIntercom(int intercomID);

    boolean controlIntercom(int intercomID, int cmd);

    boolean controlGateway(int sessionID, int index, int command);

    boolean getFace(int sessionID, String cardNo);

    boolean getCard(int sessionID);

    boolean setFace(int sessionID, IntercomCard card);

    boolean setCard(int sessionID, IntercomCard card);

    boolean deleteCard(int sessionID, IntercomCard card);
}
