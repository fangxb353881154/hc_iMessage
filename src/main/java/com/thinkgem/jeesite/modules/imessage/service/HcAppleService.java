/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.service;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.imessage.dao.HcAppleDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcApple;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 苹果账号表Service
 * @author fangxb
 * @version 2016-09-03
 */
@Service
@Transactional(readOnly = true)
public class HcAppleService extends CrudService<HcAppleDao, HcApple> {

	public HcApple get(String id) {
		return super.get(id);
	}
	
	public List<HcApple> findList(HcApple hcApple) {
		return super.findList(hcApple);
	}
	
	public Page<HcApple> findPage(Page<HcApple> page, HcApple hcApple) {
		return super.findPage(page, hcApple);
	}
	
	@Transactional(readOnly = false)
	public void save(HcApple hcApple) {
		super.save(hcApple);
	}
	
	@Transactional(readOnly = false)
	public void delete(HcApple hcApple) {
		super.delete(hcApple);
	}

	@Transactional(readOnly = false)
	public void importFileSave(HcApple apple) {
		dao.deleteAllAppleTemp();
		dao.importFileSave(apple);
		dao.updateRepeatState();
	}

	public Page<HcApple> findPageTemp(Page<HcApple> page, HcApple hcApple) {
		hcApple.setPage(page);
		page.setList(dao.findTempList(hcApple));
		return page;
	}
	@Transactional(readOnly = false)
	public void syncAppleId(){
		dao.syncAppleId();
	}

	@Transactional(readOnly = false)
	public void deleteAll() {
		HcApple apple = new HcApple();
		apple.setCreateBy(UserUtils.getUser());
		dao.deleteAll(apple);
	}

	@Transactional(readOnly = false)
	public void updateAllIsUse(HcApple apple){
		dao.updateAllIsUse(apple);
	}
}