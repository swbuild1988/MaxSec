package com.bandweaver.maxsec.controller;

import com.bandweaver.maxsec.dto.IntercomCard;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.service.VideoManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
public class IntercomController {

    @Autowired
    private VideoManagerService videoManagerService;

    /**
     * 接听
     *
     * @return
     */
    @GetMapping("/intercom/{id}/answer")
    public R answer(@PathVariable("id") Integer id) {

        boolean f = videoManagerService.controlIntercom(id, 2);
        return new R(f);
    }

    /**
     * 挂断
     *
     * @return
     */
    @GetMapping("/intercom/{id}/ringoff")
    public R ringoff(@PathVariable("id") Integer id) {

        boolean f = videoManagerService.controlIntercom(id, 5);
        return new R(f);
    }

    /**
     * 拒绝
     *
     * @return
     */
    @GetMapping("/intercom/{id}/refuse")
    public R refuse(@PathVariable("id") Integer id) {

        boolean f = videoManagerService.controlIntercom(id, 3);
        return new R(f);
    }

    /**
     * 开门
     *
     * @return
     */
    @GetMapping("/intercom/{id}/open")
    public R open(@PathVariable("id") Integer id) {

        boolean f = videoManagerService.controlGateway(id, 1);
        return new R(f);
    }

    /**
     * 获取所有卡
     *
     * @return
     */
    @GetMapping("/intercom/{id}/cards")
    public R getCard(@PathVariable("id") Integer id) {

        boolean f = videoManagerService.getCard(id);
        return new R(f);
    }

    /**
     * 发卡
     *
     * @return
     */
    @PostMapping("/intercom/{id}/cards")
    public R setCard(@PathVariable("id") Integer id, @RequestBody IntercomCard card) {

        boolean f = videoManagerService.setCard(id, card);
        return new R(f);
    }

    /**
     * 删除卡 同时也会删除人脸
     *
     * @return
     */
    @PostMapping("/intercom/{id}/cards_delete")
    public R deleteCard(@PathVariable("id") Integer id, @RequestBody IntercomCard card) {

        boolean f = videoManagerService.deleteCard(id, card);
        return new R(f);
    }

    /**
     * 下发人脸
     *
     * @return
     */
    @PostMapping("/intercom/{id}/setFace")
    public R setFace(@PathVariable("id") Integer id, @RequestBody IntercomCard card) {

        boolean f = videoManagerService.setFace(id, card);
        return new R(f);
    }

}
