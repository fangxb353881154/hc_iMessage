/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.utils.Collections3;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.modules.imessage.TxtUtils;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskChild;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskPhone;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskChildService;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskPhoneService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.imessage.entity.HcTask;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskService;

import java.util.List;

/**
 * 发送任务Controller
 * @author fangxb
 * @version 2016-09-05
 */
@Controller
@RequestMapping(value = "${adminPath}/imessage/task")
public class HcTaskController extends BaseController {

	@Autowired
	private HcTaskService hcTaskService;
	@Autowired
	private HcTaskChildService hcTaskChildService;
	@Autowired
	private HcTaskPhoneService hcTaskPhoneService;
	
	@ModelAttribute
	public HcTask get(@RequestParam(required=false) String id) {
		HcTask entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = hcTaskService.get(id);
		}
		if (entity == null){
			entity = new HcTask();
		}
		return entity;
	}

	@RequiresPermissions("imessage:task:view")
	@RequestMapping(value = {"list", ""})
	public String list(HcTask hcTask, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()) {
			hcTask.setCreateBy(user);
		}
		Page<HcTask> page = hcTaskService.findPage(new Page<HcTask>(request, response), hcTask); 
		model.addAttribute("page", page);
		return "modules/imessage/taskList";
	}

	@RequiresPermissions("imessage:task:view")
	@RequestMapping(value = "form")
	public String form(HcTask hcTask, Model model) {
		if (StringUtils.isNotEmpty(hcTask.getId())) {
			if (StringUtils.equals(hcTask.getType(), "1")) {
				HcTaskPhone taskPhone = hcTaskPhoneService.getPhoneByTaskId(hcTask.getId());
				if (taskPhone != null) {
					model.addAttribute("phone",taskPhone.getPhone());
				}
            } else if (StringUtils.equals(hcTask.getType(), "3")) {
                List<String> phoneList = hcTaskPhoneService.getPhonesByTaskId(hcTask.getId());
                if (phoneList != null) {
                    model.addAttribute("phones", StringUtils.join(phoneList, ","));
                }
            }
        }
		model.addAttribute("hcTask", hcTask);
		return "modules/imessage/taskForm";
	}

	@RequiresPermissions("imessage:task:edit")
	@RequestMapping(value = "save")
	public String save(HcTask hcTask, Model model, RedirectAttributes redirectAttributes, MultipartFile file) {
		logger.info("-----------------------------进来了！！");
	    if (StringUtils.equals(hcTask.getType(), "2")){
	        //任务类型=3  指定号码 计算号码数 TODO
			String fileName = file.getOriginalFilename();
			String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			List<String> phoneList = Lists.newArrayList();

			//判断文件类型
			if(StringUtils.equals((fileSuffix), "txt")){
				phoneList = TxtUtils.readTxt(file);
			}/*else if (fileSuffix.endsWith("xls") || fileSuffix.endsWith("xlsx")){
                try {
                    ImportExcel ei = new ImportExcel(file, 1, 0);
                    List<PhoneEntity> list = ei.getDataList(PhoneEntity.class);
                    phoneList = Collections3.extractToList(list, "phone");
                } catch (Exception e) {
                    addMessage(redirectAttributes, "导入用户失败！失败信息："+e.getMessage());
                    return "redirect:"+Global.getAdminPath()+"/imessage/task/form?repage";
                }
            }*/

			if (phoneList == null || phoneList.size() == 0) {
				addMessage(redirectAttributes, "指定号码任务创建失败，请选择正确的文件！");
                return "redirect:"+Global.getAdminPath()+"/imessage/task/form?repage";
			}
			hcTask.setCount(String.valueOf(phoneList.size()));
			hcTask.setPhoneList(phoneList);

		}

		if (!beanValidator(model, hcTask)){
			return form(hcTask, model);
		}
		try {
			hcTaskService.save(hcTask);
			addMessage(redirectAttributes, "保存发送任务成功");
		}catch (Exception e){
			e.printStackTrace();
			addMessage(redirectAttributes, "保存失败，失败原因:"+e.getMessage());
		}

		return "redirect:"+Global.getAdminPath()+"/imessage/task/?repage";
	}

	@RequiresPermissions("imessage:task:edit")
	@RequestMapping(value = "updateStatus")
	public String updateStatus(HcTask hcTask, Model model, RedirectAttributes redirectAttributes) {
		hcTaskService.update(hcTask);
		return "redirect:"+Global.getAdminPath()+"/imessage/task/?repage";
	}


	@RequiresPermissions("imessage:task:edit")
	@RequestMapping(value = "delete")
	public String delete(HcTask hcTask, RedirectAttributes redirectAttributes) {
		hcTaskService.delete(hcTask);
		addMessage(redirectAttributes, "删除发送任务成功");
		return "redirect:"+Global.getAdminPath()+"/imessage/task/?repage";
	}

	@RequiresPermissions("imessage:task:edit")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(RedirectAttributes redirectAttributes) {
		hcTaskService.deleteAll();
		addMessage(redirectAttributes, "发送任务清空成功");
		return "redirect:"+Global.getAdminPath()+"/imessage/task/?repage";
	}


}
