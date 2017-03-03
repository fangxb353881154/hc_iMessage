/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.service;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.ConfigUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.imessage.TxtUtils;
import com.thinkgem.jeesite.modules.imessage.dao.HcTaskChildDao;
import com.thinkgem.jeesite.modules.imessage.dao.HcTaskPhoneDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcTask;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskChild;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskPhone;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

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
    private HcTaskPhoneService hcTaskPhoneService;
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

    @Transactional(readOnly = false)
    public void deleteByTaskId(String taskId) {
        HcTaskChild child = new HcTaskChild();
        child.setTaskId(taskId);
        List<HcTaskChild> taskChildList = dao.findList(child);
        if (taskChildList != null) {
            for (HcTaskChild c : taskChildList) {
                if (Integer.valueOf(c.getSendNumber()) - Integer.valueOf(c.getSuccessNumber()) > 0) {
                    //有失败的数据
                    HcTaskPhone taskPhone = new HcTaskPhone();
                    taskPhone.setTaskId(c.getTaskId());
                    taskPhone.setTaskChildId(c.getId());
                    hcTaskPhoneDao.deleteByTaskIdChildId(taskPhone);
                }
            }
            dao.deleteByTaskId(taskId);
        }
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

    /**
     * 获取一条可执行任务
     *
     * @param user
     * @return
     */
    public HcTaskChild getTaskByInterface(User user) {
        return dao.getTaskByInterface(user);
    }

    /**
     * 获取需要回收的子任务
     *
     * @return
     */
    public List<HcTaskChild> getRecycleTaskChild(String taskId) {
        String diffe = ConfigUtils.get("task.recycle.time");
        return dao.getRecycleTaskChild(taskId, Integer.valueOf(diffe));
    }

    @Transactional(readOnly = false)
    public void recycleTaskChild(final HcTask hcTask, List<HcTaskChild> taskChildList) {
        int count = taskChildList.size();       //总条数
        int taskSize = Integer.parseInt(ConfigUtils.get("task.recycle.size"));  //线程数

        int over = count / taskSize,        //每个线程处理的条数
                rem = count % taskSize;        //前rem个线程 、数据处理数据+1

        logger.info("---------------------------需要回收任务总数：" + taskChildList.size() + "            每组线程处理任务数：" + over + "  rem:" + rem);
        int listInt = 0, i = 0;
        taskSize = over == 0 ? rem : taskSize;  //任务条数<线程数  =>余数为线程数
        final List<String> phoneList = Lists.newArrayList();
        final CountDownLatch latch = new CountDownLatch(taskSize);
        Thread[] threads = new Thread[taskSize];
        logger.info("-------------------------- 线程数：" + taskSize);
        //分组执行
        while (listInt < count) {
            int end = i < rem ? over + 1 : over;
            final List<HcTaskChild> list = taskChildList.subList(listInt, (listInt + end) > count ? count : listInt + end);
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    phoneList.addAll(getRecyclePhone(hcTask, list));
                    latch.countDown();
                }
            });
            //执行线程
            threads[i].start();
            listInt += end;
            i++;
        }
        try {
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("回收任务线程出错，" + e.getMessage());
        }
        logger.debug("------------------------ 任务号码整理完成， size: "+ phoneList.size()+ "         开始重新生成子任务             ");
        /**
         * 重新生成子任务
         */
        if (phoneList != null && phoneList.size() > 0) {
            insertChildAndTxt(hcTask, phoneList);
        }
        logger.debug("------------------------ 子任务重新生成完成，开始删除旧任务文件");
        //删除回收过的任务文件
        for (String path : txtPathList) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
            }
        }
        logger.debug("------------------------ 任务回收完成");
    }

    public final List<String> txtPathList = Lists.newArrayList();

    public List<String> getRecyclePhone(HcTask hcTask, List<HcTaskChild> taskChildList) {
        List<String> newPhone = Lists.newArrayList();//需要处理的新任务号码
        HcTaskPhone phone = new HcTaskPhone();
        phone.setTaskId(hcTask.getId());
        phone.setCreateDate(hcTask.getCreateDate());
        phone.setCreateBy(hcTask.getCreateBy());
        /**
         *  整理任务，获取子任务中未发送的号码集合，并将子任务状态更新为完成、删除任务文件
         */
        for (HcTaskChild taskChild : taskChildList) {
            phone.setTaskChildId(taskChild.getId());
            phone.setTaskStatus(null);
            List<HcTaskPhone> phoneList = hcTaskPhoneService.findList(phone);//获取已发送号码

            String txtPath = TxtUtils.getFileName(phone);
            //发送任务号码
            List<String> txtPhoneList = TxtUtils.readTxt(txtPath);
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
                newPhone.addAll(txtPhoneList);
            }
            /**
             * 更新原子任务状态+号码数
             */
            taskChild.setCount(String.valueOf(phoneList != null ? phoneList.size() : 0));
            taskChild.setTaskStatus("1");
            dao.update(taskChild);

            txtPathList.add(txtPath);
        }
        return newPhone;
    }

    public void insertChildAndTxt(HcTask hcTask, List<String> phoneList) {
        int listInt = 0, count = phoneList.size(),
                GROUP_NUMBER = Integer.valueOf(ConfigUtils.get("child.group.number"));

        HcTaskChild taskChild = new HcTaskChild();
        taskChild.setTaskId(hcTask.getId());
        taskChild.setCreateBy(hcTask.getCreateBy());
        taskChild.setCreateDate(hcTask.getCreateDate());
        taskChild.setUpdateBy(hcTask.getUpdateBy());
        taskChild.setUpdateDate(new Date());

        while (listInt < count) {
            List<String> list = phoneList.subList(listInt, (listInt + GROUP_NUMBER) > count ? count : listInt + GROUP_NUMBER);
            listInt += GROUP_NUMBER;
            taskChild.setId(IdGen.uuid());
            taskChild.setCount(list.size() + "");
            dao.insert(taskChild);

            taskChild.setPhoneList(list);
            TxtUtils.writeTxt(taskChild);//任务号码写入txt文件
        }
    }
}

