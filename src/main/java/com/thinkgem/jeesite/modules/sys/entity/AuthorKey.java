/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import org.hibernate.validator.constraints.Length;
import com.thinkgem.jeesite.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 授权码Entity
 * @author fangxb
 * @version 2016-08-15
 */
public class AuthorKey extends DataEntity<AuthorKey> {
	
	private static final long serialVersionUID = 1L;
	private String creditCode;		// credit_code
	private String machineCode;		// machine_code
	private String isUse;		// 是否已用
	private User user;		// 所属用户ID
	private Date laseDate;		// lase_date
	
	public AuthorKey() {
		super();
	}

	public AuthorKey(String id){
		super(id);
	}

	@Length(min=1, max=64, message="credit_code长度必须介于 1 和 64 之间")
	public String getCreditCode() {
		return creditCode;
	}

	public void setCreditCode(String creditCode) {
		this.creditCode = creditCode;
	}
	
	@Length(min=0, max=64, message="machine_code长度必须介于 0 和 64 之间")
	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}
	
	@Length(min=1, max=1, message="是否已用长度必须介于 1 和 1 之间")
	public String getIsUse() {
		return isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}
	
	@NotNull(message="所属用户ID不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLaseDate() {
		return laseDate;
	}

	public void setLaseDate(Date laseDate) {
		this.laseDate = laseDate;
	}
	
}