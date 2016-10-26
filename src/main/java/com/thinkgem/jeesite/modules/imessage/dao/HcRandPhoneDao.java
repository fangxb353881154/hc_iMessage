/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcRandPhone;
import com.thinkgem.jeesite.modules.imessage.entity.HcRandPhoneTemp;
import com.thinkgem.jeesite.modules.imessage.entity.HcTask;
import com.thinkgem.jeesite.modules.sys.entity.Area;

import java.util.List;

/**
 * 号码库DAO接口
 * @author fangxb
 * @version 2016-09-03
 */
@MyBatisDao
public interface HcRandPhoneDao extends CrudDao<HcRandPhone> {


    /**
     * 清空临时表
     */
    void deleteAllTemp();

    /**
     * 判断是否有重复数据
     */
    void updateRepeatState();

    /**
     * 同步更新至正式表
     */
    void syncPhone();

    /**
     * 批量新增临时表数据
     * @param randPhone
     */
    void batchInsertTemp(HcRandPhone randPhone);

    /**
     * 临时表导入结果
     * @param randPhoneTemp
     * @return
     */
    List<HcRandPhoneTemp> findTempList(HcRandPhoneTemp randPhoneTemp);

    /**
     * 去前limit（使用次数排序）
     * @return
     */
    List<HcRandPhone> findLimitList(HcRandPhone randPhone);

    /**
     * 更新使用次数+1
     *
     * @param
     */
    void updateNumberInPhoneId(List<String> idList);
}