package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.CustomMap;
import com.bandweaver.maxsec.service.CustomMapService;
import com.bandweaver.maxsec.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
public class CustomMapController {

    @Autowired
    private CustomMapService customMapService;

    /**
     * 获取保存的json文件
     *
     * @param json
     * @return
     */
    @PostMapping("/geomap_json")
    public R getGeoMapJson(@RequestBody JSONObject json) {

        if (!json.containsKey("level") || !json.containsKey("name")) return new R(500, "参数错误");

        try {
            String level = json.getString("level");
            String name = json.getString("name");
            String file = "geoMap/" + level + "/" + name + ".json";

            JSONObject object = JsonUtil.readJsonFromClassPath(file);

            return new R(object);
        } catch (Exception e) {
            return new R(500, e);
        }
    }


    @GetMapping("/custommaps")
    public R getGeoMaps() {
        List<CustomMap> customMaps = customMapService.selectAll();

        return new R(customMaps);
    }

    @PostMapping("/custommaps_page")
    public R getPageInfo(HttpServletRequest request, @RequestBody JSONObject object) {
        PageResult result;
        PageRequest pageQuery = new PageRequest();
        pageQuery.setPageNum(object.getInteger("pageNum"));
        pageQuery.setPageSize(object.getInteger("pageSize"));

        result = customMapService.selectPage(pageQuery);
        return new R(result);
    }

    @PostMapping("/custommaps")
    public R addGeoMaps(@RequestBody CustomMap customMap) {
        customMap.setCreateTime(new Date());
        customMapService.insert(customMap);
        return new R();
    }

    @PostMapping("/custommaps_edit")
    public R editGeoMaps(@RequestBody CustomMap customMap) {
        customMapService.update(customMap);
        return new R();
    }

    @GetMapping("/custommaps_delete/{id}")
    public R deleteGeoMap(@PathVariable("id") Integer id) {
        customMapService.delete(id);
        return new R();
    }

    @GetMapping("/custommaps/{id}")
    public R getGeoMap(@PathVariable Integer id) {
        CustomMap customMap = customMapService.selectByPrimaryKey(id);
        return new R(customMap);
    }

    @GetMapping("/custommaps_name/{name}")
    public R getGeoMapByName(@PathVariable String name) {

        List<CustomMap> customMaps = customMapService.selectAll();
        for (CustomMap tmp : customMaps) {
            if (tmp.getName().equals(name)) {
                return new R(tmp);
            }
        }

        return new R();
    }


}
