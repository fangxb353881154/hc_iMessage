/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.web;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.utils.*;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.modules.imessage.EmojiUtils;
import com.thinkgem.jeesite.modules.imessage.TxtUtils;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskChild;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskPhone;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskChildService;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskPhoneService;
import com.thinkgem.jeesite.modules.imessage.task.HcTaskScheduled;
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
import com.thinkgem.jeesite.modules.imessage.entity.HcTask;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskService;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 发送任务Controller
 *
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
    public HcTask get(@RequestParam(required = false) String id) {
        HcTask entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = hcTaskService.get(id);
        }
        if (entity == null) {
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
        if (page != null && page.getList() != null) {
            DesUtils desUtils = new DesUtils();
            for (HcTask task : page.getList()) {
                try {
                    String content = desUtils.decryptString(task.getContent());

                    task.setContent(EmojiUtils.emojiRecovery(content));
                } catch (Exception e) {
                }
            }
        }
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
                    model.addAttribute("phone", taskPhone.getPhone());
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
        if (StringUtils.equals(hcTask.getType(), "2")) {
            //任务类型=3  指定号码 计算号码数
            String fileName = file.getOriginalFilename();
            String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            List<String> phoneList = Lists.newArrayList();

            //判断文件类型
            if (StringUtils.equals((fileSuffix), "txt")) {
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
                return "redirect:" + Global.getAdminPath() + "/imessage/task/form?repage";
            }
            hcTask.setCount(String.valueOf(phoneList.size()));
            hcTask.setPhoneList(phoneList);

        }

        if (!beanValidator(model, hcTask)) {
            return form(hcTask, model);
        }
        try {
            hcTaskService.save(hcTask);
            addMessage(redirectAttributes, "保存发送任务成功");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(redirectAttributes, "保存失败，失败原因:" + e.getMessage());
        }

        return "redirect:" + Global.getAdminPath() + "/imessage/task/?repage";
    }

    @RequiresPermissions("imessage:task:edit")
    @RequestMapping(value = "updateStatus")
    public String updateStatus(HcTask hcTask, Model model, RedirectAttributes redirectAttributes) {
        hcTaskService.update(hcTask);
        return "redirect:" + Global.getAdminPath() + "/imessage/task/?repage";
    }


    @RequiresPermissions("imessage:task:edit")
    @RequestMapping(value = "delete")
    public String delete(HcTask hcTask, RedirectAttributes redirectAttributes) {
        hcTaskService.delete(hcTask);
        addMessage(redirectAttributes, "删除发送任务成功");
        return "redirect:" + Global.getAdminPath() + "/imessage/task/?repage";
    }

    @RequiresPermissions("imessage:task:edit")
    @RequestMapping(value = "deleteAll")
    public String deleteAll(RedirectAttributes redirectAttributes) {
        hcTaskService.deleteAll();
        addMessage(redirectAttributes, "发送任务清空成功");
        return "redirect:" + Global.getAdminPath() + "/imessage/task/?repage";
    }


    @RequiresPermissions("imessage:task:edit")
    @RequestMapping(value = "exportPhone")
    public String exportSuccessPhone(HcTaskPhone taskPhone, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        if (StringUtils.isNotEmpty(taskPhone.getTaskId())) {
            taskPhone.setTaskStatus("1");
            List<HcTaskPhone> phoneList = hcTaskPhoneService.findList(taskPhone);
            if (phoneList != null && phoneList.size() > 0) {
                exportSuccessPhone(phoneList, response);
            } else {
                addMessage(redirectAttributes, "导出手机号号码失败,失败原因：无成功号码数据！");
            }
        } else {
            addMessage(redirectAttributes, "导出手机号号码失败,请选择发送任务！");
        }

        return "redirect:" + Global.getAdminPath() + "/imessage/task/list?repage";
    }

    @RequiresPermissions("imessage:task:edit")
    @RequestMapping(value = "exportPhoneAll")
    public String exportSuccessPhoneAll(HcTaskPhone taskPhone, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        taskPhone.setTaskStatus("1");
        List<HcTaskPhone> phoneList = hcTaskPhoneService.findList(taskPhone);
        if (phoneList != null && phoneList.size() > 0) {
            exportSuccessPhone(phoneList, response);
        } else {
            addMessage(redirectAttributes, "导出手机号号码失败,失败原因：无成功号码数据！");
        }
        return "redirect:" + Global.getAdminPath() + "/imessage/task/list?repage";
    }

    /**
     * 任务回收
     * @param hcTask
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("imessage:task:edit")
    @RequestMapping(value = "recycleChild")
    public String recycleTaskChild(HcTask hcTask,RedirectAttributes redirectAttributes) {
        String isRecycle = ConfigUtils.get("task.child.isRecycle");
        if (StringUtils.equals(isRecycle, "1")) {
            //if (StringUtils.equals(hcTask.getTaskStatus(), "2")) {
                /*HcTaskChild hcTaskChild = new HcTaskChild();
                hcTaskChild.setTaskId(hcTask.getId());
                hcTaskChild.setTaskStatus("2");
                List<HcTaskChild> taskChildList = hcTaskChildService.findList(hcTaskChild);*/
                List<HcTaskChild> taskChildList = hcTaskChildService.getRecycleTaskChild(hcTask.getId());
                if (taskChildList != null && taskChildList.size() > 0) {
                    hcTaskChildService.recycleTaskChild(hcTask, taskChildList);
                    addMessage(redirectAttributes, "任务回收成功，请静候几分钟！");
                }else{
                    addMessage(redirectAttributes, "任务回收失败，失败原因：暂无需要回收的任务！");
                }
           /* }else{
                addMessage(redirectAttributes, "任务回收失败，失败原因：任务状态非处理中！");
            }*/
        }else{
            addMessage(redirectAttributes, "系统未开启任务回收功能，如有需要请联系管理员！");
        }
        return "redirect:" + Global.getAdminPath() + "/imessage/task/?repage";
    }


    public void exportSuccessPhone(List<HcTaskPhone> phoneList, HttpServletResponse response) {
            //导出txt文件
            response.setContentType("text/plain");
            String fileName = "成功手机号码" + DateUtils.getDate("yyyy-MM-dd") + "_" + phoneList.size() + "条";
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".txt");
            BufferedOutputStream buff = null;
            StringBuffer write = new StringBuffer();
            String enter = "\r\n";
            ServletOutputStream outSTr = null;
            for (HcTaskPhone phone : phoneList) {
                write.append(phone.getPhone() + enter);
            }
            try {
                outSTr = response.getOutputStream(); // 建立
                buff = new BufferedOutputStream(outSTr);
                buff.write(write.toString().getBytes("UTF-8"));
                buff.flush();
                buff.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    buff.close();
                    outSTr.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }
}
