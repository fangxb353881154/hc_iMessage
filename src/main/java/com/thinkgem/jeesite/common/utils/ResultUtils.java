package com.thinkgem.jeesite.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/12.
 */
public class ResultUtils {

    public final static String STATE_FAILURE = "0";
    public final static String STATE_SUCCESS = "1";


    public static Map<String, Object> getFailure() {
        return getResults(STATE_FAILURE, "操作失败！", null);
    }

    public static Map<String,Object> getSuccess(){
        return getResults(STATE_SUCCESS, "操作成功！", null);
    }


    public static Map<String, Object> getSuccess(Object data) {
        return getResults(STATE_SUCCESS, "操作成功！", data);
    }

    public static Map<String, Object> getFailure(String message){
        HashMap<String, Object> result = new HashMap<>();
        result.put("flag", STATE_FAILURE);
        result.put("msg", message);
        return result;
    }

    public static Map<String, Object> getSuccess(String message){
        HashMap<String, Object> result = new HashMap<>();
        result.put("flag", STATE_SUCCESS);
        result.put("msg", message);
        return result;
    }

    public static Map<String, Object> getFailure(String message, Object data){
        return getResults(STATE_FAILURE, message, data);
    }

    public static Map<String, Object> getSuccess(String message, Object data){
        return getResults(STATE_SUCCESS, message, data);
    }

    public static Map<String, Object> getResults(String flag, String message, Object data) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("flag", flag);
        result.put("msg", message);
        result.put("data", data);
        return result;
    }
}
