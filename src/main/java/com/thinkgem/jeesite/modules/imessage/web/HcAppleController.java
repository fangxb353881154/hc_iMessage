/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.DesUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.imessage.entity.HcApple;
import com.thinkgem.jeesite.modules.imessage.entity.HcAppleTemp;
import com.thinkgem.jeesite.modules.imessage.service.HcAppleService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 苹果账号表Controller
 * @author fangxb
 * @version 2016-09-03
 */
@Controller
@RequestMapping(value = "${adminPath}/imessage/appleId")
public class HcAppleController extends BaseController {

	@Autowired
	private HcAppleService hcAppleService;
	
	@ModelAttribute
	public HcApple get(@RequestParam(required=false) String id) {
		HcApple entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = hcAppleService.get(id);
		}
		if (entity == null){
			entity = new HcApple();
		}
		return entity;
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(HcApple hcApple, HttpServletRequest request, HttpServletResponse response, Model model) {
		hcApple.setCreateBy(UserUtils.getUser());
		Page<HcApple> page = hcAppleService.findPage(new Page<HcApple>(request, response), hcApple);
		model.addAttribute("page", page);
		return "modules/imessage/appleIdList";
	}


	@RequestMapping("/tempList")
	public String tempList(HcApple hcApple, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<HcApple> page = hcAppleService.findPageTemp(new Page<HcApple>(request, response), hcApple);
		model.addAttribute("page", page);
		return "modules/imessage/appleIdTempList";
	}

	@RequestMapping(value = "import", method= RequestMethod.POST)
	public String importFile(MultipartFile file, RedirectAttributes redirectAttributes, HcApple apple) {
		try {
			InputStream inputStream = file.getInputStream();
			InputStreamReader is = new InputStreamReader(inputStream);
			BufferedReader reader = new BufferedReader(is);
			String line = null;
			List<Map<String, Object>> mapList = Lists.newArrayList();
			DesUtils desUtils = new DesUtils();
			while ((line = reader.readLine())  != null) {
				Map<String, Object> map = Maps.newHashMap();
				String sp[] = line.split("----");
				map.put("apple_id", sp[0]);
				map.put("apple_pwd", desUtils.encryptString(sp[1]));
				mapList.add(map);
			}
			if (mapList != null && mapList.size() > 0) {
				apple.setMapList(mapList);
				apple.setCreateDate(new Date());
				apple.setCreateBy(UserUtils.getUser());

				hcAppleService.importFileSave(apple);
				addMessage(redirectAttributes, "导入临时表成功，请确认同步！");
				return "redirect:"+Global.getAdminPath()+"/imessage/appleId/tempList?repage";
			}else {
				addMessage(redirectAttributes, "导入失败，请选择文件！");
			}

		} catch (IOException e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导入失败！");
		}
		return "redirect:"+Global.getAdminPath()+"/imessage/appleId/list?repage";
	}

	@RequestMapping("/conImport")
	public String confirmImport(RedirectAttributes redirectAttributes){
		hcAppleService.syncAppleId();
		addMessage(redirectAttributes, "同步成功！");
		return "redirect:"+Global.getAdminPath()+"/imessage/appleId/list?repage";
	}

	@RequestMapping(value = "form")
	public String form(HcApple hcApple, Model model) {
		model.addAttribute("hcApple", hcApple);
		return "modules/imessage/appleIdForm";
	}

	@RequestMapping(value = "save")
	public String save(HcApple hcApple, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, hcApple)){
			return form(hcApple, model);
		}
		hcAppleService.save(hcApple);
		addMessage(redirectAttributes, "保存账号成功");
		return "redirect:"+Global.getAdminPath()+"/imessage/appleId/?repage";
	}
	
	@RequestMapping(value = "delete")
	public String delete(HcApple hcApple, RedirectAttributes redirectAttributes) {
		hcAppleService.delete(hcApple);
		addMessage(redirectAttributes, "删除账号成功");
		return "redirect:"+Global.getAdminPath()+"/imessage/appleId/list?repage";
	}

	@RequestMapping(value = "deleteAll")
	public String deleteAll(RedirectAttributes redirectAttributes) {
		hcAppleService.deleteAll();
		addMessage(redirectAttributes, "清空appleId账号成功");
		return "redirect:" + Global.getAdminPath() + "/imessage/appleId/list?repage";
	}

	@RequestMapping(value = "updateAllIsUse")
	public String updateAllIsUse(RedirectAttributes redirectAttributes) {
		HcApple apple = new HcApple();
		apple.setCreateBy(UserUtils.getUser());

		hcAppleService.updateAllIsUse(apple);
		addMessage(redirectAttributes, "还原appleId状态成功");
		return "redirect:" + Global.getAdminPath() + "/imessage/appleId/list?repage";
	}
}