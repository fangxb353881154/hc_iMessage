package com.thinkgem.jeesite.modules.imessage.task;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.imessage.TxtUtils;
import com.thinkgem.jeesite.modules.imessage.dao.HcTaskDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskChild;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskPhone;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskChildService;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskPhoneService;
import com.thinkgem.jeesite.modules.imessage.service.HcTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/11/14.
 */
@Service
@Lazy(false)
public class HcTaskScheduled {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HcTaskService hcTaskService;
    @Autowired
    private HcTaskChildService hcTaskChildService;
    @Autowired
    private HcTaskPhoneService hcTaskPhoneService;



    /**
     * 定时器 5分钟统计一次
     *
     *
     * 统计更新任务（发送数，状态）
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateTaskGroup() {
        hcTaskService.updateTaskGroup();
    }

    /**
     * 任务回收
     *
     * 判断子任务 处于'处理中'状态长达1个小时
     */
    @Scheduled(cron = "0 0/20 * * * ?")
    @Transactional(readOnly = false)
    public void recycleTaskChild() {
        List<HcTaskChild> taskChildList = hcTaskChildService.getRecycleTaskChild();
        if (taskChildList != null && taskChildList.size() > 0) {
            logger.info("---------------------------------任务回收------------------ " + taskChildList.size());
            for (HcTaskChild taskChild : taskChildList) {
                new RecycleTaskChild(taskChild).start();
            }
        }
    }

    public class RecycleTaskChild extends Thread{
        private HcTaskChild taskChild;

        public RecycleTaskChild(HcTaskChild taskChild) {
            this.taskChild = taskChild;
        }
        public void run(){
            HcTaskPhone phone = new HcTaskPhone();
            phone.setTaskId(taskChild.getTaskId());
            phone.setTaskChildId(taskChild.getId());
            phone.setCreateDate(taskChild.getCreateDate());
            phone.setCreateBy(taskChild.getCreateBy());

            List<HcTaskPhone> phoneList = hcTaskPhoneService.findList(phone);
            if (phoneList != null && phoneList.size() > 0 ) {
                //指定号码+随机号码 -> 读取txt文件
                List<String> txtPhoneList = TxtUtils.readTxt(phone);
                if (txtPhoneList != null && txtPhoneList.size() > 0 && phoneList.size() != txtPhoneList.size()) {
                    for (HcTaskPhone taskPhone : phoneList) {
                        for (int i = 0; i < txtPhoneList.size(); i++) {
                            if (StringUtils.equals(txtPhoneList.get(i), taskPhone.getPhone())) {
                                txtPhoneList.remove(i);
                                i--;
                            }
                        }
                    }
                    phone.setPhoneList(txtPhoneList);
                    TxtUtils.writeTxt(phone);//将剩余未发送号码回填txt
                }
                taskChild.setTaskStatus("4");//已收回
                hcTaskChildService.save(taskChild);
            }
        }
    }
}
