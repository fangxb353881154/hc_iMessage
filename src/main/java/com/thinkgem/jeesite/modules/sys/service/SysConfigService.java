/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.sys.entity.SysConfig;
import com.thinkgem.jeesite.modules.sys.dao.SysConfigDao;

/**
 * 系统配置Service
 * @author fangxb
 * @version 2016-11-23
 */
@Service
@Transactional(readOnly = true)
public class SysConfigService extends CrudService<SysConfigDao, SysConfig> {

	public SysConfig get(String id) {
		return super.get(id);
	}
	
	public List<SysConfig> findList(SysConfig sysConfig) {
		return super.findList(sysConfig);
	}
	
	public Page<SysConfig> findPage(Page<SysConfig> page, SysConfig sysConfig) {
		return super.findPage(page, sysConfig);
	}
	
	@Transactional(readOnly = false)
	public void save(SysConfig sysConfig) {
		super.save(sysConfig);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysConfig sysConfig) {
		super.delete(sysConfig);
	}
	
}