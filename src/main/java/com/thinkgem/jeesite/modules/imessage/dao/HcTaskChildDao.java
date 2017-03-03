/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskChild;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 统计发送日志更新子任务
     */
    void updateNumberGroupByChildId();

    /**
     * 获取需要回收的子任务
     * @return
     */
    List<HcTaskChild> getRecycleTaskChild(@Param("taskId") String taskId, @Param("difference") Integer difference);

    /**
     * 短信发送成功
     * @param id
     */
    void updateNumberSendSuccess(String id);

    /**
     * 发送失败
     * @param id
     */
    void updateNumberSendFailure(String id);
}