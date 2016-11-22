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
    public void recycleTaskChild() {
        logger.info("-------------------------------定时回收任务------------------ ");
        List<HcTaskChild> taskChildList = hcTaskChildService.getRecycleTaskChild();
        if (taskChildList != null && taskChildList.size() > 0) {
            hcTaskChildService.recycleTaskChild(taskChildList);
        }
        logger.info("-------------------------------结束------------------------- ");
    }

}
