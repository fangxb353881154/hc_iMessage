/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.entity;

import com.thinkgem.jeesite.modules.sys.entity.Area;
import org.hibernate.validator.constraints.Length;
import com.thinkgem.jeesite.modules.sys.entity.User;
import javax.validation.constraints.NotNull;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 发送任务Entity
 * @author fangxb
 * @version 2016-09-05
 */
public class HcTask extends DataEntity<HcTask> {
	
	private static final long serialVersionUID = 1L;
	private String title;		// title
	private String type;		// 任务类型[1-指定号码区段 2-指定号码 3-随机任务]
	private String count;		// 手机号数量
	private String content;		// 短信内容
	private String taskStatus;		// 任务状态[0-未完成 1-已完成 2-进行中]
	private Integer sendNumber;
	private String successNumber;		// 成功发送的个数
	private Area area;

	private String phone;
	private List<String> phoneList;
	private String phones;
	
	public HcTask() {
		super();
	}

	public HcTask(String id){
		super(id);
	}

	@Length(min=0, max=255, message="title长度必须介于 0 和 255 之间")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@Length(min=1, max=1, message="任务类型[1-指定号码区段 2-指定号码 3-随机任务]长度必须介于 1 和 1 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=1, max=11, message="手机号数量长度必须介于 1 和 11 之间")
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
	@Length(min=1, max=2000, message="短信内容长度必须介于 1 和 2000 之间")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	

	@Length(min=0, max=1, message="任务状态[0-未完成 1-已完成 2-进行中]长度必须介于 0 和 1 之间")
	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Integer getSendNumber() {
		return sendNumber;
	}

	public void setSendNumber(Integer sendNumber) {
		this.sendNumber = sendNumber;
	}

	@Length(min=0, max=8, message="成功发送的个数长度必须介于 0 和 8 之间")
	public String getSuccessNumber() {
		return successNumber;
	}

	public void setSuccessNumber(String successNumber) {
		this.successNumber = successNumber;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhones() {
		return phones;
	}

	public void setPhones(String phones) {
		this.phones = phones;
	}
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public List<String> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(List<String> phoneList) {
		this.phoneList = phoneList;
	}
}