/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcTask;
import com.thinkgem.jeesite.modules.sys.entity.User;

import java.util.List;

/**
 * 发送任务DAO接口
 * @author fangxb
 * @version 2016-09-05
 */
@MyBatisDao
public interface HcTaskDao extends CrudDao<HcTask> {


    /**
     * 随机读取一条未完成任务
     * @return
     */
    HcTask getTaskByInterface();

    void deleteAll(User user);


    /**
     * 统计更新任务
     */
    void updateNumberGroupByTaskId();

}