/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 系统配置Entity
 * @author fangxb
 * @version 2016-11-23
 */
public class SysConfig extends DataEntity<SysConfig> {
	
	private static final long serialVersionUID = 1L;
	private String key;		// 识别码
	private String value;		// 值
	private String depict;		// 描述
	
	public SysConfig() {
		super();
	}

	public SysConfig(String id){
		super(id);
	}

	public SysConfig(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Length(min=0, max=255, message="识别码长度必须介于 0 和 255 之间")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	@Length(min=0, max=255, message="值长度必须介于 0 和 255 之间")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Length(min=0, max=255, message="描述长度必须介于 0 和 255 之间")
	public String getDepict() {
		return depict;
	}

	public void setDepict(String depict) {
		this.depict = depict;
	}
	
}