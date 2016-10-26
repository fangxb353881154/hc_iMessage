package com.thinkgem.jeesite.common.utils;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2016/9/29.
 */
public class ListUtils extends org.apache.commons.collections.ListUtils {

    /**
     * 去除重复
     * @param list
     * @return
     */
    public static List<String> removeRepeat(List<String> list) {
        if (list != null && list.size() > 0) {
            HashSet h = new HashSet(list);
            list.clear();
            list.addAll(h);
            return list;
        }
        return null;
    }
}
