/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.service;

import java.util.List;

import com.thinkgem.jeesite.modules.imessage.entity.HcRandPhoneTemp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.imessage.entity.HcRandPhone;
import com.thinkgem.jeesite.modules.imessage.dao.HcRandPhoneDao;

/**
 * 号码库Service
 * @author fangxb
 * @version 2016-09-03
 */
@Service
@Transactional(readOnly = true)
public class HcRandPhoneService extends CrudService<HcRandPhoneDao, HcRandPhone> {

	public HcRandPhone get(String id) {
		return super.get(id);
	}
	
	public List<HcRandPhone> findList(HcRandPhone hcRandPhone) {
		return super.findList(hcRandPhone);
	}
	
	public Page<HcRandPhone> findPage(Page<HcRandPhone> page, HcRandPhone hcRandPhone) {
		return super.findPage(page, hcRandPhone);
	}
	
	@Transactional(readOnly = false)
	public void save(HcRandPhone hcRandPhone) {
		super.save(hcRandPhone);
	}
	
	@Transactional(readOnly = false)
	public void delete(HcRandPhone hcRandPhone) {
		super.delete(hcRandPhone);
	}

	/**
	 * 导入临时表
	 * @param phone
     */
	@Transactional(readOnly = false)
	public void importPhoneSave(HcRandPhone phone){
		dao.deleteAllTemp();
		System.out.println();
		List<String> phoneList = phone.getPhoneList();
		int listInt = 0, phoneCount = phoneList.size();

		while (listInt < phoneCount) {
			List<String> list = phoneList.subList(listInt, (listInt + GROUP_NUMBER) > phoneCount ? phoneCount : listInt + GROUP_NUMBER);
			listInt += GROUP_NUMBER;
			phone.setPhoneList(list);
			dao.batchInsertTemp(phone);
		}
		//dao.updateRepeatState();
	}

	/**
	 * 临时表数据查询
	 * @param page
	 * @param hcRandPhoneTemp
     * @return
     */
	public Page<HcRandPhoneTemp> findTempPage(Page<HcRandPhoneTemp> page, HcRandPhoneTemp hcRandPhoneTemp) {
		hcRandPhoneTemp.setPage(page);
		page.setList(dao.findTempList(hcRandPhoneTemp));
		return page;
	}

	@Transactional(readOnly = false)
	public void syncPhone(){
		dao.syncPhone();
	}


	private final static Integer GROUP_NUMBER = 2000;
}