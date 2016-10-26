/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskChild;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 子任务DAO接口
 * @author fangxb
 * @version 2016-09-06
 */
@MyBatisDao
public interface HcTaskChildDao extends CrudDao<HcTaskChild> {

    /**
     * taskId->删除
     * @param taskId
     */
    void deleteByTaskId(String taskId);


    /**
     * 获取一条可用任务
     *
     * @return
     */
    HcTaskChild getTaskByInterface(User user);

    void deleteAll(User user);
}