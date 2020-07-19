package com.bandweaver.maxsec.schedule;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.entity.MeasObject;
import com.bandweaver.maxsec.entity.MeasObjectVal;
import com.bandweaver.maxsec.dto.PatrolPresetJob;
import com.bandweaver.maxsec.service.MeasObjectService;
import com.bandweaver.maxsec.service.MeasObjectValService;
import com.bandweaver.maxsec.service.VideoManagerService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 定时任务入口
 *
 * @author ya.liu
 * @date 2019年12月19日
 */
@Slf4j
@Component
public class TaskEntrance {

    @Autowired
    private VideoManagerService videoManagerService;
    @Autowired
    private MeasObjectService measObjectService;
    @Autowired
    private MeasObjectValService measObjectValService;

    public void startPatrolPreset(String param) {
        log.info("startPatrolPreset:" + param);
        PatrolPresetJob job = JSONObject.parseObject(param, PatrolPresetJob.class);
      /*  PatrolPresetJob job1 = new PatrolPresetJob();
        job1.setVideoId(1001);
        job1.setStayTime(5);
        job1.setPresets(new Integer[]{1, 2, 3, 4, 5, 6});*/
        videoManagerService.startPatrolPreset(job);
    }

    /**
     * 控制防区布防撤防
     * @param parm
     * @author ya.liu
     * @date 2020年7月13日
     */
    public void controlActivedOfMeasObject(String parm) {
    	log.info("controlActivedOfMeasObject: " + parm);
    	MeasObject obj = JSONObject.parseObject(parm, MeasObject.class);
    	measObjectService.update(obj);
    }
    
    /**
     * 定时保存监测数据
     * 
     * @author ya.liu
     * @date 2020年7月14日
     */
    public void saveMeasObjectVal(String parm) {
    	List<MeasObjectVal> list = measObjectValService.getMeasObjectVals();
    	log.info(list.toString());
    	for (MeasObjectVal val : list) {
    		measObjectValService.saveValue(val);
    	}
    }
}
