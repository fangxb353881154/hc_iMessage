/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.service;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.*;
import com.thinkgem.jeesite.modules.imessage.EmojiUtils;
import com.thinkgem.jeesite.modules.imessage.TxtUtils;
import com.thinkgem.jeesite.modules.imessage.dao.HcRandPhoneDao;
import com.thinkgem.jeesite.modules.imessage.dao.HcTaskChildDao;
import com.thinkgem.jeesite.modules.imessage.dao.HcTaskDao;
import com.thinkgem.jeesite.modules.imessage.dao.HcTaskPhoneDao;
import com.thinkgem.jeesite.modules.imessage.entity.*;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 发送任务Service
 *
 * @author fangxb
 * @version 2016-09-05
 */
@Service
@Transactional(readOnly = true)
public class HcTaskService extends CrudService<HcTaskDao, HcTask> {

    @Autowired
    private HcTaskChildDao hcTaskChildDao;
    @Autowired
    private HcTaskPhoneDao hcTaskPhoneDao;
    @Autowired
    private HcRandPhoneDao hcRandPhoneDao;
    @Autowired
    private AreaDao areaDao;

    public HcTask get(String id) {
        HcTask hcTask = super.get(id);
        try {
            DesUtils desUtils = new DesUtils();
            String content = desUtils.decryptString(hcTask.getContent());
            hcTask.setContent(EmojiUtils.emojiRecovery(content));
        } catch (Exception e) {
        }
        return hcTask;
    }

    public List<HcTask> findList(HcTask hcTask) {
        return super.findList(hcTask);
    }

    public Page<HcTask> findPage(Page<HcTask> page, HcTask hcTask) {
        return super.findPage(page, hcTask);
    }

    @Transactional(readOnly = false)
    public void update(HcTask hcTask) {
        String content = EmojiUtils.emojiConvert(hcTask.getContent());
        DesUtils desUtils = new DesUtils();
        hcTask.setContent(desUtils.encryptString(content));
        hcTask.preUpdate();
        dao.update(hcTask);
    }

    @Transactional(readOnly = false)
    public void save(HcTask hcTask) {
        if (StringUtils.isNotEmpty(hcTask.getId())) {
            //修改=>清空号码表、子任务表
            hcTaskPhoneDao.deleteByTaskId(hcTask.getId());
            hcTaskChildDao.deleteByTaskId(hcTask.getId());

            /**
             * 删除子任务文件
             */
            String path = TxtUtils.getPath(hcTask.getCreateDate(),UserUtils.getUser());
            FileUtils.deleteFile(FileUtils.getFilesByPathPrefix(new File(path), hcTask.getId()));
        }

        String content = EmojiUtils.emojiConvert(hcTask.getContent());
        DesUtils desUtils = new DesUtils();
        hcTask.setContent(desUtils.encryptString(content));
        //保存主任务
        super.save(hcTask);

        /**
         * 创建子任务实体
         */
        HcTaskChild taskChild = new HcTaskChild();
        taskChild.setTaskId(hcTask.getId());
        taskChild.setCount(hcTask.getCount());
        taskChild.preInsert();

        /**
         * 根据任务类型 保存数据
         */
        HcTaskPhone taskPhone = new HcTaskPhone();
        taskPhone.setTaskId(hcTask.getId());
        taskPhone.preInsert();

        if (StringUtils.equals(hcTask.getType(), "1")) {
            //添加子任务
            hcTaskChildDao.insert(taskChild);
            //指定号码段
            taskPhone.setTaskChildId(taskChild.getId());
            taskPhone.setPhone(hcTask.getPhone());
            hcTaskPhoneDao.insert(taskPhone);

        } else if (StringUtils.equals(hcTask.getType(), "2") || StringUtils.equals(hcTask.getType(), "3")) {
            int count = Integer.valueOf(hcTask.getCount());
            if (StringUtils.equals(hcTask.getType(), "3")) {
                hcTask.setPhoneList(getPhoneListByTaskAreaId(hcTask.getArea().getId(), count));
            }

            int GROUP_NUMBER = Integer.valueOf(ConfigUtils.get("child.group.number"));

            List<String> phoneList = hcTask.getPhoneList();
            if (phoneList != null && phoneList.size() == count) {
                int listInt = 0;
                while (listInt < count) {
                    List<String> list = phoneList.subList(listInt, (listInt + GROUP_NUMBER) > count ? count : listInt + GROUP_NUMBER);
                    listInt += GROUP_NUMBER;
                    //添加子任务
                    taskChild = new HcTaskChild();
                    taskChild.setTaskId(hcTask.getId());
                    taskChild.setCount(list.size() + "");
                    taskChild.preInsert();
                    hcTaskChildDao.insert(taskChild);
                    //添加任务号码
                    taskPhone.setTaskChildId(taskChild.getId());
                    taskPhone.setPhoneList(list);
                    logger.info("-----------------开始写入文件--------------");
                    TxtUtils.writeTxt(taskPhone);//任务号码写入txt文件
                    logger.info("-----------------开始写入文件 end --------------");
                }
            }else {
                throw new RuntimeException("保存失败，号码库存不足！");
            }
        }
    }

    @Transactional(readOnly = false)
    public void delete(HcTask hcTask) {
        super.delete(hcTask);
        hcTaskChildDao.deleteByTaskId(hcTask.getId());
        //
        String path = TxtUtils.getPath(hcTask.getCreateDate(), UserUtils.getUser());
        FileUtils.deleteFile(FileUtils.getFilesByPathPrefix(new File(path), hcTask.getId()));
    }


    /**
     * 校验任务是否完成并更新
     *
     * @param hcTask
     */
    public void checkUpdateTask(HcTask hcTask) {
        if (StringUtils.isNotEmpty(hcTask.getType()) && StringUtils.equals(hcTask.getType(), "3")) {

        }
    }

    public HcTask getTask(HcTask task) {
        List<HcTask> taskList = dao.findList(task);
        if (taskList != null && taskList.size() > 0) {
            return taskList.get(0);
        }
        return null;
    }

    /**
     * 任务分组
     */


    @Transactional(readOnly = false)
    public void manageResule(TaskVo taskVo) {
        HcTask task = get(taskVo.getTaskID());
        if (task == null) {
            throw new RuntimeException("未知任务");
        }
        if (StringUtils.equals(taskVo.getTaskType(), "1")) {
            //指定号码段 => 直接更新任务状态
            task.setTaskStatus("1");
            task.setSuccessNumber(taskVo.getSuccessnum());
            dao.update(task);

        } else if (StringUtils.equals(taskVo.getTaskType(), "2") || StringUtils.equals(taskVo.getTaskType(), "3")) {
            //随机号码 or  指定号码  => 更新子任务，判断所有子任务是否完成
            HcTaskChild taskChild = hcTaskChildDao.get(taskVo.getTaskChildID());
            if (taskChild == null || !StringUtils.equals(task.getId(), taskChild.getTaskId())) {
                throw new RuntimeException("未知子任务");
            }
            taskChild.setTaskStatus("1");
            taskChild.setSuccessNumber(taskVo.getSuccessnum());
            hcTaskChildDao.update(taskChild);//更新子任务
            /**
             * 查找所有子任务
             */
            taskChild = new HcTaskChild();
            taskChild.setTaskId(taskVo.getTaskID());
            List<HcTaskChild> taskChildList = hcTaskChildDao.findList(taskChild);
            boolean isComplete = true;
            for (HcTaskChild child : taskChildList) {
                if (!StringUtils.equals(child.getTaskStatus(), "1")) {
                    isComplete = false;
                }
            }
            //task.setSuccessNumber(String.valueOf(successNumber));
            if (isComplete) {
                task.setTaskStatus("1");
            }
            int successNumber = Integer.valueOf(task.getSuccessNumber()) + Integer.valueOf(taskVo.getSuccessnum());
            task.setSuccessNumber(String.valueOf(successNumber));
            dao.update(task);
        }
    }


    /**
     * 更新任务状态
     *
     * @param task
     * @param status
     */
    private void updateTaskStatus(HcTask task, String status) {
        task.setTaskStatus(status);
        super.save(task);
    }


    public List<String> getPhoneListByTaskAreaId(String areaId, Integer limit) {
        List<String> phoneList = getPhoneListByTaskAreaId(areaId);
        if (phoneList != null && limit <= phoneList.size()) {
            Collections.shuffle(phoneList);
            //截取list ， 随机起点
            int random = (int) (Math.random() * (phoneList.size() - limit));

            return phoneList.subList(random, random + limit);
        }
        return null;
    }

    public List<String> getPhoneListByTaskAreaId(String areaId) {
        File[] files = getFilesByTaskAreaId(areaId);
        if (files != null && files.length > 0) {
            List<String> phoneList = Lists.newArrayList();
            for (File f : files) {
                if (StringUtils.equals(FileUtils.getFileExtension(f.getName()), "txt")) {
                    List<String> lists = TxtUtils.readTxt(f);
                    phoneList.addAll(ListUtils.removeRepeat(lists));
                }
            }
            return phoneList;
        }
        return null;
    }

    public File[] getFilesByTaskAreaId(String areaId) {
        String txtName = ConfigUtils.get("phone.library.prefix");

        //areaId不为空and不等于0
        if (StringUtils.isNotEmpty(areaId) && !StringUtils.equals(areaId, "0")) {
            Area area = areaDao.get(areaId);
            //3：地市 => 获取父级
            if (StringUtils.equals(area.getType(), "3")) {
                Area pArea = areaDao.get(area.getParentId());
                txtName += pArea.getName() + ConfigUtils.get("phone.library.separator") + area.getName();
            } else {
                txtName += area.getName() + ConfigUtils.get("phone.library.separator");
            }
        }
        //String txtPath = getDbTxtPath();
        File txtFile = new File(TaskVo.getTxtPath());
        File[] files;

        if (StringUtils.isNotEmpty(txtName)) {
            // DB_TXT_PATH 下文件搜索
            files = FileUtils.getFilesByPathPrefix(txtFile, txtName);
        } else {
            //DB_TXT_PATH 下所有文件
            files = FileUtils.findChildrenList(txtFile, false);
        }
        return files;
    }


    /**
     * 清空所有任务
     */
    @Transactional(readOnly = false)
    public void deleteAll() {
        User user = UserUtils.getUser();
        dao.deleteAll(user);
        hcTaskChildDao.deleteAll(user);
        //hcTaskPhoneDao.deleteAll(user);

        FileUtils.deleteDirectory(TxtUtils.getTxtPathByUserId(user.getId()));
    }

    /**
     * 定时器 5分钟统计一次
     *
     *
     * 统计更新任务（发送数，状态）
     */
    @Transactional(readOnly = false)
    public void updateTaskGroup() {
        logger.debug("--------------------------- 统计任务开始："+ new Date());
        //统计发送日志 -> 更新子任务
        hcTaskChildDao.updateNumberGroupByChildId();

        /**
         * 子任务处理后   =>
         *
         * 统计子任务发送情况->更新主任务
         */
        dao.updateNumberGroupByTaskId();
    }



}