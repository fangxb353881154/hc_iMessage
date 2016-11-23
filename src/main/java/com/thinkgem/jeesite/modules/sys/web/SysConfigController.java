/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.SysConfig;
import com.thinkgem.jeesite.modules.sys.service.SysConfigService;

/**
 * 系统配置Controller
 * @author fangxb
 * @version 2016-11-23
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/config")
public class SysConfigController extends BaseController {

	@Autowired
	private SysConfigService sysConfigService;
	
	@ModelAttribute
	public SysConfig get(@RequestParam(required=false) String id) {
		SysConfig entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysConfigService.get(id);
		}
		if (entity == null){
			entity = new SysConfig();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:sysConfig:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysConfig sysConfig, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysConfig> page = sysConfigService.findPage(new Page<SysConfig>(request, response), sysConfig); 
		model.addAttribute("page", page);
		return "modules/sys/sysConfigList";
	}

	@RequiresPermissions("sys:sysConfig:view")
	@RequestMapping(value = "form")
	public String form(SysConfig sysConfig, Model model) {
		model.addAttribute("sysConfig", sysConfig);
		return "modules/sys/sysConfigForm";
	}

	@RequiresPermissions("sys:sysConfig:edit")
	@RequestMapping(value = "save")
	public String save(SysConfig sysConfig, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysConfig)){
			return form(sysConfig, model);
		}
		sysConfigService.save(sysConfig);
		addMessage(redirectAttributes, "保存配置成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysConfig/list?repage";
	}
	
	@RequiresPermissions("sys:sysConfig:edit")
	@RequestMapping(value = "delete")
	public String delete(SysConfig sysConfig, RedirectAttributes redirectAttributes) {
		sysConfigService.delete(sysConfig);
		addMessage(redirectAttributes, "删除配置成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysConfig/list?repage";
	}

}