/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.service;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.imessage.TxtUtils;
import com.thinkgem.jeesite.modules.imessage.dao.HcTaskChildDao;
import com.thinkgem.jeesite.modules.imessage.dao.HcTaskPhoneDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskChild;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskPhone;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 发送任务Service
 *
 * @author fangxb
 * @version 2016-09-05
 */
@Service
@Transactional(readOnly = true)
public class HcTaskChildService extends CrudService<HcTaskChildDao, HcTaskChild> {

    @Autowired
    private HcTaskPhoneDao hcTaskPhoneDao;

    public HcTaskChild get(String id) {
        return super.get(id);
    }

    public List<HcTaskChild> findList(HcTaskChild hcTaskChild) {
        return super.findList(hcTaskChild);
    }

    public Page<HcTaskChild> findPage(Page<HcTaskChild> page, HcTaskChild hcTask) {
        return super.findPage(page, hcTask);
    }

    @Transactional(readOnly = false)
    public void save(HcTaskChild hcTask) {
        super.save(hcTask);
    }

    @Transactional(readOnly = false)
    public void delete(HcTaskChild hcTask) {
        super.delete(hcTask);
    }


    public HcTaskChild getTaskChildByTaskId(String taskId) {
        HcTaskChild child = new HcTaskChild();
        child.setTaskId(taskId);
        return getTaskChild(child);
    }

    public HcTaskChild getTaskChild(HcTaskChild child) {
        List<HcTaskChild> childList = super.findList(child);
        if (childList != null && childList.size() > 0) {
            return childList.get(0);
        }
        return null;
    }

    public HcTaskChild getTaskByInterface(User user) {
        return dao.getTaskByInterface(user);
    }

    public List<HcTaskChild> getRecycleTaskChild() {
        return dao.getRecycleTaskChild();
    }

    @Transactional(readOnly = false)
    public void recycleTaskChild(List<HcTaskChild> taskChildList) {
        for (HcTaskChild taskChild : taskChildList) {
            new RecycleTaskChild(taskChild).start();
        }
    }

    public class RecycleTaskChild extends Thread {
        private HcTaskChild taskChild;

        public RecycleTaskChild(HcTaskChild taskChild) {
            this.taskChild = taskChild;
        }

        public void run() {
            HcTaskPhone phone = new HcTaskPhone();
            phone.setTaskId(taskChild.getTaskId());
            phone.setTaskChildId(taskChild.getId());
            phone.setCreateDate(taskChild.getCreateDate());
            phone.setCreateBy(taskChild.getCreateBy());
            phone.setTaskStatus(null);
            List<HcTaskPhone> phoneList = hcTaskPhoneDao.findList(phone);
            List<String> txtPhoneList = TxtUtils.readTxt(phone);
            //指定号码+随机号码 -> 读取txt文件
            if (txtPhoneList != null && txtPhoneList.size() > 0 && phoneList.size() != txtPhoneList.size()) {
                for (HcTaskPhone taskPhone : phoneList) {
                    for (int i = 0; i < txtPhoneList.size(); i++) {
                        if (StringUtils.equals(txtPhoneList.get(i), taskPhone.getPhone())) {
                            txtPhoneList.remove(i);
                            i--;
                        }
                    }
                }
                phone.setPhoneList(txtPhoneList);
                TxtUtils.writeTxt(phone);//将剩余未发送号码回填txt
            }

            if (txtPhoneList != null && txtPhoneList.size() > 0) {
                taskChild.setTaskStatus("4");//已收回
            } else {
                taskChild.setTaskStatus("1");
            }
            dao.update(taskChild);
        }
    }
}

