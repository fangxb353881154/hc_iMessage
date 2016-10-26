/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.imessage.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 号码库Entity
 * @author fangxb
 * @version 2016-09-03
 */
public class HcRandPhoneTemp extends DataEntity<HcRandPhoneTemp> {

	private static final long serialVersionUID = 1L;
	private String phone;		// 手机号
	private String useStatus;		// 状态[0-未使用 1-成功 2-失败],当整个表号码全部成功后，全部update成0
	private Area area;		// 号码归属地
	private String useNumber;		// 使用次数
	private String isRepeat;


	public HcRandPhoneTemp() {
		super();
	}

	public HcRandPhoneTemp(String id){
		super(id);
	}

	@Length(min=1, max=64, message="手机号长度必须介于 1 和 64 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=1, max=1, message="状态[0-未使用 1-成功 2-失败],当整个表号码全部成功后，全部update成0长度必须介于 1 和 1 之间")
	public String getUseStatus() {
		return useStatus;
	}

	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
	}
	
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
	
	@Length(min=0, max=11, message="使用次数长度必须介于 0 和 11 之间")
	public String getUseNumber() {
		return useNumber;
	}

	public void setUseNumber(String useNumber) {
		this.useNumber = useNumber;
	}

	public String getIsRepeat() {
		return isRepeat;
	}

	public void setIsRepeat(String isRepeat) {
		this.isRepeat = isRepeat;
	}
}