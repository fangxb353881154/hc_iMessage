/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.entity;

import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 任务号码Entity
 * @author fangxb
 * @version 2016-09-06
 */
public class HcTaskPhone extends DataEntity<HcTaskPhone> {
	
	private static final long serialVersionUID = 1L;
	private String taskId;		// 任务id
	private String phone;		// 手机号
	private String taskStatus;		// 状态[0-未使用 1-成功 2-失败]
	private String taskChildId;		// 随机号码任务的子任务id

	private List<String> phoneList;
	private List<String> idList;

	private String tableName;

	public HcTaskPhone() {
		super();
		taskStatus = "0";
	}

	public HcTaskPhone(String id){
		super(id);
	}

	@Length(min=1, max=64, message="任务id长度必须介于 1 和 64 之间")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	@Length(min=1, max=64, message="手机号长度必须介于 1 和 64 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=1, max=1, message="状态[0-未使用 1-成功 2-失败]长度必须介于 1 和 1 之间")
	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	
	@Length(min=0, max=64, message="随机号码任务的子任务id长度必须介于 0 和 64 之间")
	public String getTaskChildId() {
		return taskChildId;
	}

	public void setTaskChildId(String taskChildId) {
		this.taskChildId = taskChildId;
	}

	public List<String> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(List<String> phoneList) {
		this.phoneList = phoneList;
	}

	public void setPhoneList(String phones){
		String sp[] = phones.split(",");
		phoneList = Lists.newArrayList();
		for (String s : sp) {
			phoneList.add(s);
		}
	}

	public List<String> getIdList() {
		return idList;
	}

	public void setIdList(List<String> idList) {
		this.idList = idList;
	}

	public String getTableName() {

		return "hc_task_phone_" + this.taskStatus;
	}
}