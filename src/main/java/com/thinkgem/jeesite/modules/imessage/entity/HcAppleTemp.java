/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Map;

/**
 * 苹果账号表Entity
 * @author fangxb
 * @version 2016-09-03
 */
public class HcAppleTemp extends DataEntity<HcAppleTemp> {

	private static final long serialVersionUID = 1L;
	private String appleId;		// 苹果账号
	private String applePwd;		// 苹果密码
	private String isUse;		// 已使用
	private String isRepeat;	//是否重复


	public HcAppleTemp() {
		super();
	}

	public HcAppleTemp(String id){
		super(id);
	}

	@Length(min=1, max=128, message="苹果账号长度必须介于 1 和 128 之间")
	public String getAppleId() {
		return appleId;
	}

	public void setAppleId(String appleId) {
		this.appleId = appleId;
	}
	
	@Length(min=1, max=128, message="苹果密码长度必须介于 1 和 128 之间")
	public String getApplePwd() {
		return applePwd;
	}

	public void setApplePwd(String applePwd) {
		this.applePwd = applePwd;
	}
	
	@Length(min=1, max=1, message="已使用长度必须介于 1 和 1 之间")
	public String getIsUse() {
		return isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public String getIsRepeat() {
		return isRepeat;
	}

	public void setIsRepeat(String isRepeat) {
		this.isRepeat = isRepeat;
	}
}