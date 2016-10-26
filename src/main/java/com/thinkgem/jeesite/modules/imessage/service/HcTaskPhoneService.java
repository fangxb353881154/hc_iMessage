/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.service;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.Collections3;
import com.thinkgem.jeesite.modules.imessage.dao.HcTaskDao;
import com.thinkgem.jeesite.modules.imessage.dao.HcTaskPhoneDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcTask;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskPhone;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 发送任务Service
 * @author fangxb
 * @version 2016-09-05
 */
@Service
@Transactional(readOnly = true)
public class HcTaskPhoneService extends CrudService<HcTaskPhoneDao, HcTaskPhone> {

	public HcTaskPhone get(String id) {
		return super.get(id);
	}
	
	public List<HcTaskPhone> findList(HcTaskPhone taskPhone) {
		return super.findList(taskPhone);
	}
	
	public Page<HcTaskPhone> findPage(Page<HcTaskPhone> page, HcTaskPhone taskPhone) {
		return super.findPage(page, taskPhone);
	}
	
	@Transactional(readOnly = false)
	public void save(HcTaskPhone taskPhone) {
		super.save(taskPhone);
	}
	
	@Transactional(readOnly = false)
	public void delete(HcTaskPhone taskPhone) {
		super.delete(taskPhone);
	}


	/**
	 * 主任务ID获取号码对象
	 * @param taskId
	 * @return
	 */
	public HcTaskPhone getPhoneByTaskId(String taskId) {
		List<HcTaskPhone> phoneList = getPhoneListByTaskId(taskId);
		if (phoneList != null)
			return phoneList.get(0);
		return null;
	}

	/**
	 * 主任务ID获取号码列表
	 * @param taskId
	 * @return
	 */
	public List<HcTaskPhone> getPhoneListByTaskId(String taskId){
		HcTaskPhone taskPhone = new HcTaskPhone();
		taskPhone.setTaskId(taskId);
		return super.findList(taskPhone);
	}

	/**
	 * 主任务ID获取号码集合
	 * @param taskId
	 * @return
	 */
	public List<String> getPhonesByTaskId(String taskId){
		List<HcTaskPhone> phoneList = getPhoneListByTaskId(taskId);
		return Collections3.extractToList(phoneList, "phone");
	}

}