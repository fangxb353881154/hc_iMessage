package com.thinkgem.jeesite.modules.imessage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.ConfigUtils;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.imessage.dao.HcAppleDao;
import com.thinkgem.jeesite.modules.imessage.entity.HcApple;
import com.thinkgem.jeesite.modules.imessage.vo.AppleVo;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2016/11/19.
 */
public class AppleIdUtils {

    private static HcAppleDao hcAppleDao = SpringContextHolder.getBean(HcAppleDao.class);
    public static String APPLE_KEY_ = "APPLE_KEY_";

    public static Logger logger = LoggerFactory.getLogger(AppleIdUtils.class);

    public static void setApple(HcApple apple) {
        int useNumber = Integer.parseInt(ConfigUtils.get("appleId.use.number"));
        if (useNumber > 1) {
            try {
                Map<String, Object> map = (Map) CacheUtils.get(APPLE_KEY_);
                if (map == null) {
                    map = Maps.newHashMap();
                }
                AppleVo vo = (AppleVo) map.get(apple.getId());
                if (vo == null) {
                    //不存在初始化
                    vo = new AppleVo();
                    vo.setNumber(1);
                    vo.setApple(apple);
                    map.put(apple.getId(), vo);
                    //加入缓存
                    CacheUtils.put(APPLE_KEY_, map);
                }
            }catch (Exception e){
                //e.printStackTrace();
            }
        }
    }

    public static HcApple getApple(User user) {
        HcApple resultApple = null;
        int useNumber = Integer.parseInt(ConfigUtils.get("appleId.use.number"));
        //获取配置 > 1
        if (useNumber > 1) {
            Map<String, Object> map = (Map) CacheUtils.get(APPLE_KEY_);
            if (map != null) {
                Iterator it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getKey();
                    AppleVo value = (AppleVo) entry.getValue();

                    if (value != null && StringUtils.equals(value.getApple().getCreateBy().getId(), user.getId())) {
                        value.setNumber(value.getNumber() + 1);
                        if (value.getNumber() >= useNumber) {
                            map.remove(key);
                        }else {
                            map.put(key, value);
                        }
                        resultApple = value.getApple();
                        logger.debug("--------------------cache getApple           appleId:" + resultApple.getAppleId());
                    }
                }
            }
        }
        if (resultApple == null){
            HcApple apple = new HcApple();
            apple.setIsUse("0");
            apple.setCreateBy(user);
            List<HcApple> appleList = hcAppleDao.findList(apple);
            String isCycle = ConfigUtils.get("appleId.use.isCycle");
            if (StringUtils.equals(isCycle, "1")) {
                if (appleList == null || appleList.size() == 0) {
                    //批量还原ID 使用状态
                    hcAppleDao.updateAllIsUse(apple);
                    appleList = hcAppleDao.findList(apple);
                }
            }
            if (appleList != null && appleList.size() > 0) {
                resultApple = appleList.get(0);
                resultApple.setIsUse("1");
                hcAppleDao.update(resultApple);
            }
        }
        return resultApple;
    }

}
