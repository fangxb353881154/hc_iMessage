package com.thinkgem.jeesite.modules.imessage.entity;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 *
 * 接口参数接收实体
 * Created by Administrator on 2016/9/6.
 */
public class TaskVo {
    private String SN;          //系列号
    private String taskID;      //任务ID
    private String taskChildID; //子任务ID
    private String taskType;    //任务类型  随机号码的任务表其实在另一张
    private String status;      //结果 1-成功  0-失败
    private String successnum;  //成功数量
    private String mobile;


    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskChildID() {
        return taskChildID;
    }

    public void setTaskChildID(String taskChildID) {
        this.taskChildID = taskChildID;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getStatus() {
        return status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSuccessnum() {
        return successnum;
    }

    public void setSuccessnum(String successnum) {
        this.successnum = successnum;
    }

    public static String getTxtPath() {
        return Global.getConfig("phone.db.txt.path")+"/"+ UserUtils.getUser().getId();
    }
}
