/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.web;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.ListUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.imessage.TxtUtils;
import com.thinkgem.jeesite.modules.imessage.entity.HcRandPhone;
import com.thinkgem.jeesite.modules.imessage.entity.HcRandPhoneTemp;
import com.thinkgem.jeesite.modules.imessage.entity.TaskVo;
import com.thinkgem.jeesite.modules.imessage.service.HcRandPhoneService;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.util.Enumeration;
import java.util.List;

/**
 * 号码库Controller
 *
 * @author fangxb
 * @version 2016-09-03
 */
@Controller
@RequestMapping(value = "${adminPath}/imessage/phone")
public class HcRandPhoneController extends BaseController {

    @Autowired
    private HcRandPhoneService hcRandPhoneService;

    @ModelAttribute
    public HcRandPhone get(@RequestParam(required = false) String id) {
        HcRandPhone entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = hcRandPhoneService.get(id);
        }
        if (entity == null) {
            entity = new HcRandPhone();
        }
        return entity;
    }

    @RequestMapping(value = {"list", ""})
    public String list(HcRandPhone hcRandPhone, HttpServletRequest request, HttpServletResponse response, Model model) {
        //Page<HcRandPhone> page = hcRandPhoneService.findPage(new Page<HcRandPhone>(request, response), hcRandPhone);
        //model.addAttribute("page", page);
        return "modules/imessage/phoneList";
    }

    @RequestMapping("/tempList")
    public String tempList(HcRandPhoneTemp hcRandPhoneTemp, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<HcRandPhoneTemp> page = hcRandPhoneService.findTempPage(new Page<HcRandPhoneTemp>(request, response), hcRandPhoneTemp);
        model.addAttribute("page", page);
        return "modules/imessage/phoneTempList";
    }

    @RequestMapping("toImportPhone")
    public String toImportPhone() {

        return "modules/imessage/importPhone";
    }

    @RequestMapping("importFile")
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes, HcRandPhone randPhone) {
        System.out.println("------------------" + file.getOriginalFilename());
        if (StringUtils.equals(FileUtils.getFileExtension(file.getOriginalFilename()), "zip")) {
            String tempPath = TxtUtils.getRequestPath("/temp");
            File tempFile = new File(tempPath);
            if (!tempFile.exists() && !tempFile.isDirectory()) {
                System.out.println(tempFile + "目录不存在，需要创建");
                tempFile.mkdir();
            }
            try {

                String fileName = file.getOriginalFilename();
                file.transferTo(new File(tempFile, fileName));
                File tempZipFile = new File(tempPath + File.separator + fileName);
                if (tempZipFile.exists()) {
                    ZipFile zipFile = new ZipFile(tempZipFile, "GBK");
                    Enumeration<ZipEntry> zipEntries = zipFile.getEntries();
                    try {
                        while (zipEntries.hasMoreElements()) {
                            ZipEntry zipEnt = zipEntries.nextElement();

                            String entName = zipEnt.getName();
                            System.out.println("---------- " + entName);
                            if (!zipEnt.isDirectory()) {

                                InputStream inputStream = zipFile.getInputStream(zipEnt);
                                InputStreamReader is = new InputStreamReader(inputStream);
                                BufferedReader reader = new BufferedReader(is);
                                String line;
                                List<String> phoneList = Lists.newArrayList();
                                while ((line = reader.readLine()) != null) {
                                    phoneList.add(line);
                                }

                                String[] entSplit = entName.split("_");
                                //查找号码库 是否有该地区文件

                                File zf = FileUtils.getFileByPathPrefix(new File(TaskVo.getTxtPath()), entSplit[0]+"_");
                                if (zf == null || !zf.exists()) {
                                    TxtUtils.writeTxt(TaskVo.getTxtPath() + File.separator + entName, phoneList);
                                } else {
                                    phoneList.addAll(TxtUtils.readTxt(zf));
                                    phoneList = ListUtils.removeRepeat(phoneList);
                                    entName = entSplit[0] + "_" + phoneList.size() + "条.txt";
                                    //生成新文件  删除旧文件
                                    TxtUtils.writeTxt(TaskVo.getTxtPath() + File.separator + entName, phoneList);

                                    zf.delete();
                                }
                                inputStream.close();
                                is.close();
                            }
                        }
                    } catch (FileExistsException f) {
                    }finally {
                        zipFile.close();
                        System.out.println("===========删除上传文件：" + tempZipFile.getPath());
                        tempZipFile.delete();
                    }
                    addMessage(redirectAttributes, "导入成功！");
                }
            } catch (Exception e) {
                addMessage(redirectAttributes, "导入失败，请选择文件！");
            }
        } else {
            addMessage(redirectAttributes, "导入失败，请选择文件！");
        }

        return "redirect:" + Global.getAdminPath() + "/imessage/phone/list?repage";
    }

    @RequestMapping("syncTemp")
    public String syncTemp(RedirectAttributes redirectAttributes) {
        hcRandPhoneService.syncPhone();
        addMessage(redirectAttributes, "同步成功！");
        return "redirect:" + Global.getAdminPath() + "/imessage/phone/list?repage";
    }

    @RequestMapping(value = "delete")
    public String delete(HcRandPhone hcRandPhone, RedirectAttributes redirectAttributes) {
        hcRandPhoneService.delete(hcRandPhone);
        addMessage(redirectAttributes, "删除号码成功");
        return "redirect:" + Global.getAdminPath() + "/imessage/phone/list?repage";
    }

    @RequestMapping(value = "deleteAll")
    public String deleteAll(RedirectAttributes redirectAttributes){
        try {
            FileUtils.deleteDirectory(new File(TaskVo.getTxtPath()));
            addMessage(redirectAttributes, "清空号码库成功！");
        } catch (IOException e) {
            addMessage(redirectAttributes, "清空号码库失败！");
        }

        return "redirect:" + Global.getAdminPath() + "/imessage/phone/list?repage";
    }

}