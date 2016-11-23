package com.thinkgem.jeesite.common.utils;

import com.opensymphony.module.sitemesh.Config;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.modules.sys.dao.SysConfigDao;
import com.thinkgem.jeesite.modules.sys.entity.SysConfig;

/**
 * Created by Administrator on 2016/11/23.
 */
public class ConfigUtils {

    private static SysConfigDao sysConfigDao = SpringContextHolder.getBean(SysConfigDao.class);

    public static String get(String key) {
        String value = Global.getConfig(key);
        if (StringUtils.isEmpty(value)) {
            SysConfig config = sysConfigDao.getConfigByKey(new SysConfig(key, null));
            if (config == null) {
                return null;
            }
            Global.setConfig(key, config.getValue());

            return config.getValue();
        }
        return value;
    }

    public static void set(SysConfig config) {
        Global.setConfig(config.getKey(), config.getValue());
    }
}
