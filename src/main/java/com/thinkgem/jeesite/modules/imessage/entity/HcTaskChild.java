/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.entity;

import org.hibernate.validator.constraints.Length;
import com.thinkgem.jeesite.modules.sys.entity.User;
import javax.validation.constraints.NotNull;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 子任务Entity
 * @author fangxb
 * @version 2016-09-06
 */
public class HcTaskChild extends DataEntity<HcTaskChild> {
	
	private static final long serialVersionUID = 1L;
	private String taskId;		// 父任务id
	private String count;		// 号码数
	private String taskStatus;		// 子任务状态 [0-未完成 1-已完成 2-处理中]
	private String sendNumber;
	private String successNumber;		// success_number
	
	public HcTaskChild() {
		super();
	}

	public HcTaskChild(String id){
		super(id);
	}

	@Length(min=1, max=64, message="父任务id长度必须介于 1 和 64 之间")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	@Length(min=1, max=5, message="号码数长度必须介于 1 和 5 之间")
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
	@Length(min=1, max=1, message="子任务状态 [0-未完成 1-已完成 2-处理中]长度必须介于 1 和 1 之间")
	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	
	@Length(min=0, max=8, message="success_number长度必须介于 0 和 8 之间")
	public String getSuccessNumber() {
		return successNumber;
	}

	public void setSuccessNumber(String successNumber) {
		this.successNumber = successNumber;
	}

	public String getSendNumber() {
		return sendNumber;
	}

	public void setSendNumber(String sendNumber) {
		this.sendNumber = sendNumber;
	}
}