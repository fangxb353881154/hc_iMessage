/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.SysConfig;

/**
 * 系统配置DAO接口
 * @author fangxb
 * @version 2016-11-23
 */
@MyBatisDao
public interface SysConfigDao extends CrudDao<SysConfig> {

    /**
     * key获取配置
     * @param config
     * @return
     */
    SysConfig getConfigByKey(SysConfig config);

}