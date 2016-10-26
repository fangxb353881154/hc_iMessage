/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskPhone;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 任务号码DAO接口
 * @author fangxb
 * @version 2016-09-06
 */
@MyBatisDao
public interface HcTaskPhoneDao extends CrudDao<HcTaskPhone> {

    /**
     * taskId->删除
     * @param taskId
     */
    void deleteByTaskId(String taskId);

    /**
     * 批量插入
     * @param taskPhone
     */
    void batchInsert(HcTaskPhone taskPhone);

    void deleteAll(User user);
}