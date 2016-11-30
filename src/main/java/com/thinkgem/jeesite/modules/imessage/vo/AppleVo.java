package com.thinkgem.jeesite.modules.imessage.vo;

import com.thinkgem.jeesite.modules.imessage.entity.HcApple;

import java.io.Serializable;

/**
 * Created by asus on 2016/11/19.
 */
public class AppleVo implements Serializable {
    public HcApple apple;
    public Integer number;

    public AppleVo() {

    }

    public HcApple getApple() {
        return apple;
    }

    public void setApple(HcApple apple) {
        this.apple = apple;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
