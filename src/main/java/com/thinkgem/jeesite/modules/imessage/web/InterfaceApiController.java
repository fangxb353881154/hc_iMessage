package com.thinkgem.jeesite.modules.imessage.web;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.utils.*;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.imessage.AppleIdUtils;
import com.thinkgem.jeesite.modules.imessage.EmojiUtils;
import com.thinkgem.jeesite.modules.imessage.TxtUtils;
import com.thinkgem.jeesite.modules.imessage.entity.*;
import com.thinkgem.jeesite.modules.imessage.service.HcAppleService;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskChildService;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskPhoneService;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * iMessage 接口Controller
 * Created by Administrator on 2016/9/6.
 */
@Controller
@RequestMapping(value = "${adminPath}/imessage/api")
public class InterfaceApiController extends BaseController{

    @Autowired
    private HcAppleService hcAppleService;
    @Autowired
    private HcTaskService hcTaskService;
    @Autowired
    private HcTaskPhoneService hcTaskPhoneService;
    @Autowired
    private HcTaskChildService hcTaskChildService;


    /**
     * 获取AppleId账号
     *
     * @param SN
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/apple/getAppleAccount")
    public Map<String, Object> getAppleAccount(String SN,String userId) {
        Map<String, Object> map = Maps.newHashMap();
        User user = UserUtils.getInUser(userId);
        if (user == null) {
            map.put("flag", "0");
            map.put("msg", "用户ID错误，请联系管理员！");
            return map;
        }
        HcApple apple = AppleIdUtils.getApple(user);
        try {
            if (apple != null ) {
                DesUtils des = new DesUtils();
                //apple.setIsUse("1");
                //hcAppleService.save(apple);
                map.put("appleID", apple.getAppleId());
                map.put("applePwd", des.decryptString(apple.getApplePwd()));
                map.put("flag", "1");
                return map;
            } else {
                logger.warn("--------------获取账号密码失败");
                throw new RuntimeException("获取账号密码失败");
            }
        } catch (Exception e) {
            map.put("flag", "0");
            map.put("msg", e.getMessage());
        }
        return map;
    }


    @ResponseBody
    @RequestMapping(value = "/task/getTask")
    public Map<String, Object> getTask(String SN, String account, String userId) {
        Map<String, Object> result = Maps.newHashMap();
        User user = UserUtils.getInUser(userId);
        if (user == null) {
            result.put("flag", "0");
            result.put("msg", "用户ID错误，请联系管理员！");
        }
        //HcTask task = hcTaskService.getTaskByInterface();
        HcTaskChild taskChild = hcTaskChildService.getTaskByInterface(user);
        HcTask task = null;
        try {
            if (taskChild != null) {
                task = hcTaskService.get(taskChild.getTaskId());
                try {
                    DesUtils desUtils = new DesUtils();
                    String content = desUtils.decryptString(task.getContent());
                    task.setContent(EmojiUtils.emojiRecovery(content));
                }catch (Exception e ){
                }
                String taskId = task.getId();
                result.put("flag", 1);
                result.put("taskID", taskId);
                result.put("taskType", task.getType());
                result.put("content", task.getContent());
                result.put("taskChildID", taskChild.getId());

                HcTaskPhone taskPhone = new HcTaskPhone();
                taskPhone.setTaskId(taskId);
                taskPhone.setTaskChildId(taskChild.getId());
                taskPhone.setCreateDate(taskChild.getCreateDate());
                taskPhone.setCreateBy(user);

                    String phoneStr = "";
                if (StringUtils.equals(task.getType(), "1")) {
                    //指定号码段
                    List<HcTaskPhone> taskPhoneList = hcTaskPhoneService.findList(taskPhone);
                    if (taskPhoneList == null || taskPhoneList.size() == 0) {
                        throw new RuntimeException("获取发送任务失败，未找到发送号码");
                    }
                    phoneStr = Collections3.extractToString(taskPhoneList, "phone", ",");
                } else {
                    //指定号码+随机号码 -> 读取txt文件
                    List<String> phoneList = TxtUtils.readTxt(taskPhone);
                    if (phoneList == null || phoneList.size() == 0) {
                        throw new RuntimeException("获取发送任务失败，未找到发送号码");
                    }
                    phoneStr = StringUtils.join(phoneList, ",");
                }
                result.put("numbers", phoneStr);
                updateTaskStatus(task, "2");
                updateTaskChildStatus(taskChild, "2");
                return result;
            } else {
                throw new RuntimeException("获取发送任务失败，未找到可用任务！");
            }
        } catch (Exception e) {
            result = Maps.newHashMap();
            result.put("flag", "0");
            result.put("msg", e.getMessage());
            /*if (task != null) {
                //任务未获取到发送号码，将状态 >> 3
                updateTaskStatus(task, "3");
                updateTaskChildStatus(taskChild, "3");
            }*/
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/task/taskResult", method = RequestMethod.POST)
    public Map<String, Object> taskResult(TaskVo taskVo) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            //System.out.println("-----------------json : " + JSONUtils.toJSONString(taskVo));
            hcTaskService.manageResule(taskVo);
            result.put("flag", "1");
            return result;
        } catch (Exception e) {
            e.printStackTrace();

            result = Maps.newHashMap();
            result.put("flag", "0");
            result.put("msg", "任务状态更新失败, "+e.getMessage());
            return result;
        }
    }


    /**
     * 号码发送是否成功
     * @param taskPhone
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/phone/mobileResult")
    public Map<String, Object> mobileResult(HcTaskPhone taskPhone) {
        try {
            if (!isRepeatPhone(taskPhone)) {
                //增加成功数
                resultPhoneHandle(taskPhone);
                hcTaskPhoneService.save(taskPhone);
                return ResultUtils.getSuccess();
            }else{
                logger.info("--------------- "+taskPhone.getTaskId()+"  "+ taskPhone.getTaskChildId() +" -------------重复提交 phone:" + taskPhone.getPhone());
                return ResultUtils.getFailure();
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return ResultUtils.getFailure("更新失败，mobile=" + taskPhone.getPhone() + "  " + e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/phone/appleIdResult")
    public Map<String, Object> appleIdResult(String appleId, String userId) {
        try {
            HcApple apple = new HcApple();
            apple.setCreateBy(new User(userId));
            apple.setAppleId(appleId);
            List<HcApple> appleList = hcAppleService.findList(apple);
            if (appleList != null && appleList.size() > 0) {
                apple = appleList.get(0);
                AppleIdUtils.setApple(apple);
                return ResultUtils.getSuccess();
            }else{
                return ResultUtils.getFailure("AppleId存储失败");
            }
        } catch (Exception e) {
            logger.warn("AppleId存储失败," + e.getMessage());
            return ResultUtils.getFailure("AppleId存储失败" );
        }
    }

    /**
     * 更新主任务状态
     * @param task
     * @param status
     */
    private void updateTaskStatus(HcTask task, String status) {
        task.setTaskStatus(status);
        hcTaskService.update(task);
    }

    /**
     * 更新子任务状态
     * @param taskChild
     * @param status
     */
    public void updateTaskChildStatus(HcTaskChild taskChild, String status) {
        taskChild.setTaskStatus(status);
        hcTaskChildService.save(taskChild);
    }

    public boolean isRepeatPhone(HcTaskPhone taskPhone){
        String phone = taskPhone.getPhone();
        String taskChildId = taskPhone.getTaskChildId();

        String cacheKey = "TASK_PHONE_KEY_" + taskChildId + "_";
        Object p = CacheUtils.get(TASK_PHONE_CACHE, cacheKey + phone);
        if (p != null && StringUtils.equals(p.toString(), phone)) {
            return true;
        }
        CacheUtils.put(TASK_PHONE_CACHE, cacheKey + phone, phone);
        return false;
    }

    public void resultPhoneHandle(HcTaskPhone taskPhone) {
        if (StringUtils.equals(taskPhone.getTaskStatus(), "0")) {
            Integer ratio = Integer.valueOf(ConfigUtils.get("phone.success.ratio"));
            Object n = CacheUtils.get(taskPhone.getTaskId());
            Integer number = n == null ? 1 : (Integer) n;
            if (number >= ratio) {
                taskPhone.setTaskStatus("1");
                number = 0;
            }
            number++;
            CacheUtils.put(taskPhone.getTaskId(), number );
        }
    }

    public static String TASK_PHONE_CACHE = "taskPhoneCache";
}
