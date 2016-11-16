/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.service;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.imessage.dao.HcTaskChildDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskChild;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 发送任务Service
 * @author fangxb
 * @version 2016-09-05
 */
@Service
@Transactional(readOnly = true)
public class HcTaskChildService extends CrudService<HcTaskChildDao, HcTaskChild> {

	public HcTaskChild get(String id) {
		return super.get(id);
	}
	
	public List<HcTaskChild> findList(HcTaskChild hcTaskChild) {
		return super.findList(hcTaskChild);
	}
	
	public Page<HcTaskChild> findPage(Page<HcTaskChild> page, HcTaskChild hcTask) {
		return super.findPage(page, hcTask);
	}
	
	@Transactional(readOnly = false)
	public void save(HcTaskChild hcTask) {
		super.save(hcTask);
	}
	
	@Transactional(readOnly = false)
	public void delete(HcTaskChild hcTask) {
		super.delete(hcTask);
	}


	public HcTaskChild getTaskChildByTaskId(String taskId){
		HcTaskChild child = new HcTaskChild();
		child.setTaskId(taskId);
		return getTaskChild(child);
	}

	public HcTaskChild getTaskChild(HcTaskChild child) {
		List<HcTaskChild> childList = super.findList(child);
		if (childList != null && childList.size() > 0) {
			return childList.get(0);
		}
		return null;
	}

	public HcTaskChild getTaskByInterface(User user) {
		return dao.getTaskByInterface(user);
	}

	public List<HcTaskChild> getRecycleTaskChild() {
		return dao.getRecycleTaskChild();
	}
}

