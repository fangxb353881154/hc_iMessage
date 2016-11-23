package com.thinkgem.jeesite.modules.imessage.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2016/11/23.
 */
@Controller
@RequestMapping(value = "${adminPath}/imessage/config")
public class IMessageConfigController {


    @RequestMapping("list")
    public String list(){

        return null;
    }
}
