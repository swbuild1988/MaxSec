package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.TestRabbitMQ.MQSender;
import com.bandweaver.maxsec.constants.MessageTypeEnum;
import com.bandweaver.maxsec.constants.ObjectEnum;
import com.bandweaver.maxsec.dto.Message;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.Alarm;
import com.bandweaver.maxsec.entity.Station;
import com.bandweaver.maxsec.entity.User;
import com.bandweaver.maxsec.service.AlarmService;
import com.bandweaver.maxsec.service.StationService;
import com.bandweaver.maxsec.service.UserService;
import com.bandweaver.maxsec.util.DateUtil;
import com.bandweaver.maxsec.util.JwtUtil;
import com.bandweaver.maxsec.util.XmlUtil;
import com.bandweaver.maxsec.vo.AlarmVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
@RestController
public class AlarmController {
    @Autowired
    private MQSender mqSender;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private UserService userService;
    @Autowired
    private StationService stationService;

    @GetMapping("/alarms")
    public R getAll() {
        return new R(alarmService.selectAll());
    }

    @PostMapping("/alarm/add")
    public R addAlarm(@RequestBody Alarm alarm) {
        // 发送MQ
        Message message = new Message();
        message.setType(MessageTypeEnum.IntrusionAlarm);
        message.setBody(alarm);
        mqSender.sendMessage(message);
        // 入库
        alarmService.insert(alarm);
        return new R();
    }

    @GetMapping("/alarms/{id}/clean")
    public R cleanAlarm(@PathVariable("id") Integer id) {
        alarmService.clean(id, new Date());
        return new R();
    }

    /**
     * 获取告警类型的统计数据
     *
     * @param request
     * @return
     */
    @GetMapping("/alarm_type_statistic")
    public R getTypeStatistic(HttpServletRequest request) {
        List<JSONObject> result = new ArrayList<>();

        // 获取当前用户所在的部门
        Integer userId = JwtUtil.getId(request);
        User user = userService.selectByPrimaryKey(userId);
        Integer managementId = user.getManagementId();
        List<Station> stations = stationService.getStationsByManagement(managementId);
        List<Integer> idList = new ArrayList<>();
        for (Station station : stations) {
            idList.add(station.getId());
        }

        for (ObjectEnum objectEnum : ObjectEnum.values()) {
            JSONObject object1 = new JSONObject();
            object1.put("key", objectEnum.getName());

            // 查找告警数目
            AlarmVo vo = new AlarmVo();
            vo.setType(objectEnum.getValue());
            if (idList.size() > 0) {
                vo.setStationIds(idList);
                object1.put("val", alarmService.getCountByCondition(vo));
            } else {
                object1.put("val", 0);
            }

            result.add(object1);
        }

        return new R(result);
    }

    /**
     * 获取某场站告警类型的统计数据
     *
     * @param id
     * @return
     */
    @GetMapping("stations/{id}/alarm_type_statistic")
    public R getTypeStatistic(@PathVariable("id") Integer id) {
        List<JSONObject> result = new ArrayList<>();

        for (ObjectEnum objectEnum : ObjectEnum.values()) {

            // 查找告警数目
            AlarmVo vo = new AlarmVo();
            vo.setType(objectEnum.getValue());
            vo.setStationId(id);

            JSONObject object1 = new JSONObject();
            object1.put("key", objectEnum.getName());
            object1.put("val", alarmService.getCountByCondition(vo));

            result.add(object1);
        }

        return new R(result);
    }

    /**
     * 获取告警的近一周的数据
     *
     * @param request
     * @return
     */
    @GetMapping("/alarm_type_trend")
    public R getTypeTrend(HttpServletRequest request) {
        List<JSONObject> result = new ArrayList<>();

        // 获取当前用户所在的部门
        Integer userId = JwtUtil.getId(request);
        User user = userService.selectByPrimaryKey(userId);
        Integer managementId = user.getManagementId();
        List<Station> stations = stationService.getStationsByManagement(managementId);
        List<Integer> idList = new ArrayList<>();
        for (Station station : stations) {
            idList.add(station.getId());
        }

        for (ObjectEnum objectEnum : ObjectEnum.values()) {
            JSONObject object1 = new JSONObject();
            object1.put("key", objectEnum.getName());

            // 查找告警数目
            AlarmVo vo = new AlarmVo();
            vo.setType(objectEnum.getValue());

            List<JSONObject> l = new ArrayList<>();
            for (int i = 6; i >= 0; i--) {
                Date day = DateUtil.getFrontDay(new Date(), i);
                Date startTime = DateUtil.getDayStartTime(day);
                Date endTime = DateUtil.getDayEndTime(day);
                vo.setStartTime(startTime);
                vo.setEndTime(endTime);

                JSONObject object2 = new JSONObject();
                object2.put("key", DateUtil.getDate2StringFormat(day, "yyyy/MM/dd"));
                if (idList.size() > 0) {
                    vo.setStationIds(idList);
                    object2.put("val", alarmService.getCountByCondition(vo));
                } else {
                    object2.put("val", 0);
                }
                l.add(object2);
            }
            object1.put("val", l);


            result.add(object1);
        }

        return new R(result);
    }

    /**
     * 根据条件分页查询设备
     *
     * @param request
     * @param object
     * @return
     */
    @PostMapping("/alarms/condition")
    public R getPageInfoByCondition(HttpServletRequest request, @RequestBody JSONObject object) {
        PageResult result;

        AlarmVo vo = new AlarmVo();
        if (!(object.containsKey("pageNum") && object.containsKey("pageSize")))
            return new R(500, null, "");

        PageRequest pageQuery = new PageRequest();
        pageQuery.setPageNum(object.getInteger("pageNum"));
        pageQuery.setPageSize(object.getInteger("pageSize"));

        // 获取当前用户所在的部门
        Integer userId = JwtUtil.getId(request);
        User user = userService.selectByPrimaryKey(userId);
        Integer managementId = user.getManagementId();
        List<Station> stations = stationService.getStationsByManagement(managementId);
        List<Integer> idList = new ArrayList<>();
        for (Station station : stations) {
            idList.add(station.getId());
        }

        if (idList.size() > 0) vo.setStationIds(idList);
        if (object.containsKey("objId")) vo.setObjId(object.getInteger("objId"));
        if (object.containsKey("stationId")) vo.setStationId(object.getInteger("stationId"));
        if (object.containsKey("type")) vo.setType(object.getInteger("type"));
        if (object.containsKey("alarmType")) vo.setAlarmType(object.getInteger("alarmType"));
        if (object.containsKey("level")) vo.setLevel(object.getInteger("level"));
        if (object.containsKey("cleaned")) vo.setCleaned(object.getBoolean("cleaned"));
        if (object.containsKey("startTime")) vo.setStartTime(object.getDate("startTime"));
        if (object.containsKey("endTime")) vo.setEndTime(object.getDate("endTime"));

        log.info("告警查找条件：" + vo.toString());
        result = alarmService.getPageAlarmsByCondition(pageQuery, vo);

        return new R(result);
    }

    /**
     * 根据条件获取告警数目
     *
     * @return
     */
    @PostMapping("/alarm_count/condition")
    public R getCountByCondition(HttpServletRequest request, @RequestBody JSONObject object) {

        AlarmVo vo = new AlarmVo();

        // 获取当前用户所在的部门
        Integer userId = JwtUtil.getId(request);
        User user = userService.selectByPrimaryKey(userId);
        Integer managementId = user.getManagementId();
        List<Station> stations = stationService.getStationsByManagement(managementId);
        List<Integer> idList = new ArrayList<>();
        for (Station station : stations) {
            idList.add(station.getId());
        }

        if (idList.size() > 0) vo.setStationIds(idList);
        if (object.containsKey("objId")) vo.setStationId(object.getInteger("objId"));
        if (object.containsKey("stationId")) vo.setStationId(object.getInteger("stationId"));
        if (object.containsKey("type")) vo.setType(object.getInteger("type"));
        if (object.containsKey("alarmType")) vo.setAlarmType(object.getInteger("alarmType"));
        if (object.containsKey("level")) vo.setLevel(object.getInteger("level"));
        if (object.containsKey("cleaned")) vo.setCleaned(object.getBoolean("cleaned"));
        if (object.containsKey("startTime")) vo.setStartTime(object.getDate("startTime"));
        if (object.containsKey("endTime")) vo.setEndTime(object.getDate("endTime"));

        Integer num = alarmService.getCountByCondition(vo);

        return new R(num);
    }

    /**
     * 通过告警id获取图片和视频列表
     *
     * @param id
     * @return
     * @author ya.liu
     * @date 2020年7月14日
     */
    @GetMapping("/alarms/{id}/files")
    public R getFilesByAlarmId(@PathVariable("id") Integer id) {
        Map<String, List<String>> map = null;
        Map<String, String> configMap = XmlUtil.getSystemConfig("AlarmLinkage");
        String alarmLinkageDir = String.valueOf(configMap.get("directory"));
        Alarm alarm = alarmService.selectByPrimaryKey(id);
        if (alarm != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String date = format.format(alarm.getTime());
            String filepath = System.getProperty("user.dir") + "\\src\\main\\resources"
                    + alarmLinkageDir + "\\" + date + "\\" + id + "\\";
            map = getListJpegAndMp4(filepath);
        }
        return new R(map);
    }

    /**
     * 获取告警图片
     *
     * @param id
     * @return
     */
    @GetMapping("/alarms/{id}/images")
    public R getImagesByAlarmId(@PathVariable("id") Integer id) {
        List<String> images = new ArrayList<>();
        try {
            Map<String, String> configMap = XmlUtil.getSystemConfig("AlarmLinkage");
            String alarmLinkageDir = String.valueOf(configMap.get("directory"));
            Alarm alarm = alarmService.selectByPrimaryKey(id);
            if (alarm != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String date = format.format(alarm.getTime());
                String filepath = System.getProperty("user.dir") + "\\src\\main\\resources"
                        + alarmLinkageDir + "\\" + date + "\\" + id + "\\";
                images = getListJpeg(filepath);
            }
            return new R(images);
        } catch (Exception e) {
            return new R(500, e);
        }
    }


    /**
     * 获取告警视频
     *
     * @param id
     * @return
     */
    @GetMapping("/alarms/{id}/videos")
    public R getVideosByAlarmId(@PathVariable("id") Integer id) {
        List<String> videos = new ArrayList<>();
        try {
            Map<String, String> configMap = XmlUtil.getSystemConfig("AlarmLinkage");
            String alarmLinkageDir = String.valueOf(configMap.get("directory"));
            Alarm alarm = alarmService.selectByPrimaryKey(id);
            if (alarm != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String date = format.format(alarm.getTime());
                String filepath = System.getProperty("user.dir") + "\\src\\main\\resources"
                        + alarmLinkageDir + "\\" + date + "\\" + id + "\\";
                videos = getListMp4(filepath);
            }
            return new R(videos);
        } catch (Exception e) {
            return new R(500, e);
        }
    }

    /**
     * 预览告警图片
     *
     * @param path
     * @param response
     * @throws IOException
     * @author ya.liu
     * @date 2020年7月14日
     */
    @GetMapping("/alarms/img/view")
    public void viewImg(@RequestParam("path") String path, HttpServletResponse response) throws IOException {
        BufferedInputStream bis = null;
        OutputStream out = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new RuntimeException("没有发现文件：" + path);
            }

            bis = new BufferedInputStream(new FileInputStream(file));
            byte[] bytes = new byte[bis.available()];
            bis.read(bytes);
            out = response.getOutputStream();
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("预览告警图片失败：" + e.getMessage());
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 根据绝对路径获得图片
     *
     * @param object
     * @param response
     * @throws IOException
     */
    @PostMapping("/file")
    public void getFileByPath(@RequestBody JSONObject object, HttpServletResponse response) throws IOException {
        BufferedInputStream bis = null;
        OutputStream out = null;

        try {
            String path = object.getString("path");

            File file = new File(path);
            if (!file.exists()) {
                throw new RuntimeException("没有发现文件：" + path);
            }

            bis = new BufferedInputStream(new FileInputStream(file));
            byte[] bytes = new byte[bis.available()];
            bis.read(bytes);
            out = response.getOutputStream();
            out.write(bytes);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
            log.error("预览告警图片失败：" + e.getMessage());
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (out != null) {
                out.close();
            }
        }

    }

    @GetMapping("alarm_voice/{fileName}")
    public void getAlarmVoice(HttpServletResponse response, @PathVariable("fileName") String fileName) {

        log.info("audio play: " + fileName);
        FileInputStream fis = null;
        OutputStream os = null;
        try {

            String audioPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "Voice/" + fileName + ".mp3";

            fis = new FileInputStream(new File(audioPath));
            byte[] data = new byte[fis.available()];
            fis.read(data);

            response.setHeader("Content-Type", "audio/mp3");
            os = response.getOutputStream();
            os.write(data);
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
        } finally {
            close(fis, os);
        }
    }

    private Map<String, List<String>> getListJpegAndMp4(String filepath) {
        Map<String, List<String>> map = new TreeMap<>();
        List<String> listJPEG = new ArrayList<>();
        List<String> listMP4 = new ArrayList<>();
        File file = new File(filepath);
        if (file != null) {
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(filepath + "\\" + filelist[i]);
                String originalFilename = readfile.getName();
                int index = originalFilename.lastIndexOf(".");
                String suffix = originalFilename.substring(index, originalFilename.length());
                if (".jpeg".equals(suffix)) {
                    listJPEG.add(readfile.getAbsolutePath());
                } else if (".mp4".equals(suffix)) {
                    listMP4.add(readfile.getAbsolutePath());
                }
            }
        }
        map.put("jpeg", listJPEG);
        map.put("mp4", listMP4);
        return map;
    }

    private List<String> getListJpeg(String filepath) {
        List<String> result = new ArrayList<>();
        File file = new File(filepath);

        if (file != null) {
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(filepath + "\\" + filelist[i]);
                String originalFilename = readfile.getName();
                int index = originalFilename.lastIndexOf(".");
                String suffix = originalFilename.substring(index, originalFilename.length());
                if (".jpeg".equals(suffix)) {
                    result.add(readfile.getAbsolutePath());
                }
            }
        }
        return result;
    }

    private List<String> getListMp4(String filepath) {
        List<String> result = new ArrayList<>();
        File file = new File(filepath);

        if (file != null) {
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(filepath + "\\" + filelist[i]);
                String originalFilename = readfile.getName();
                int index = originalFilename.lastIndexOf(".");
                String suffix = originalFilename.substring(index, originalFilename.length());
                if (".mp4".equals(suffix)) {
                    result.add(readfile.getAbsolutePath());
                }
            }
        }
        return result;
    }

    private void close(FileInputStream fis, OutputStream os) {
        try {
            fis.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
        }
    }
}
