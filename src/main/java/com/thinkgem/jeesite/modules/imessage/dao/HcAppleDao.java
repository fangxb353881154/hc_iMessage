/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcApple;
import com.thinkgem.jeesite.modules.sys.entity.User;

import java.util.List;

/**
 * 苹果账号表DAO接口
 * @author fangxb
 * @version 2016-09-03
 */
@MyBatisDao
public interface HcAppleDao extends CrudDao<HcApple> {
    void importFileSave(HcApple apple);

    /**
     * 清空临时表
     */
    void deleteAllAppleTemp();

    /**
     * 更新临时表中重复字段
     */
    void updateRepeatState();

    List<HcApple> findTempList(HcApple HcApple);

    /**
     * 将临时表数据同步到正式表
     */
    void syncAppleId();

    void deleteAll(HcApple apple);

    void updateAllIsUse(HcApple apple);
}