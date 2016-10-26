package com.thinkgem.jeesite.common.utils.txt;

import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.Encodes;
import com.thinkgem.jeesite.common.utils.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Fangxb
 * @version 2016-07-22 19:45
 */
public class ExportTxt {

    private List<String> dataList;//写出数据
    private String CACHE_NAME = "EXPORT_TXT_CHCHE_NAME";//缓存名称
    private String CACHE_DATA_LIST_KEY = "EXPORT_TXT_DATA_LIST";//缓存KEY
    private Class clz ;

    public ExportTxt(Class<?> clz) {
        this.clz = clz;
        this.CACHE_DATA_LIST_KEY = this.CACHE_DATA_LIST_KEY + "_" + clz.getName();
    }

    /**
     * set结果数据集合
     * @param contents
     * @param list
     * @param isCache 是否存入缓存
     * @param <E>
     * @return
     */
    public <E> ExportTxt setDataList(List<String> contents, List<E> list, boolean isCache) {
        try {
            dataList = Lists.newArrayList();
            //Class clz = list.get(0).getClass();
            Method method = clz.getMethod("getRemarks");

            for (int i=0;i<contents.size();i++) {
                E entity = list.get(i);
                String msg = (String) method.invoke(entity);
                String result = contents.get(i);
                if (StringUtils.isNotEmpty(msg)) {
                    result += "|" + msg;
                }
                dataList.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isCache) {
            this.putCache(dataList);
        }
        return this;
    }
    public <E> ExportTxt setDataList(List<String> contents, List<E> list) {
        return setDataList(contents, list, false);
    }

    /**
     * 输出到客户端
     * @param response
     * @param fileName 输出文件名
     * @param isCache   是否从缓存中取输出数据
     * @return
     * @throws IOException
     */
    public ExportTxt write(HttpServletResponse response, String fileName,boolean isCache) throws IOException {
        response.reset();
        response.setContentType("atext/plain");
        response.setHeader("Content-Disposition", "attachment; filename="+ Encodes.urlEncode(fileName));
        if (isCache) {
            this.dataList = (List<String>) getCache();
           // removeCache();
        }
        write(response.getOutputStream());
        return this;
    }
    public ExportTxt write(HttpServletResponse response, String fileName) throws IOException {
        return write(response, fileName, false);
    }

    /**
     * 输出数据流
     * @param os 输出数据流
     */
    public ExportTxt write(OutputStream os) {
        StringBuffer writeBuff = new StringBuffer();
        String enter = "\r\n";
        BufferedOutputStream buff = new BufferedOutputStream(os);
        try {
            for (String s : dataList) {
                writeBuff.append(s + enter);
            }
            buff.write(writeBuff.toString().getBytes("UTF-8"));
            buff.flush();
            buff.close();
        } catch (IOException e) {
            throw new RuntimeException("导出失败！");
        }finally {
            try {
                buff.close();
                os.close();
            } catch (IOException e) {
            }
        }
        return this;
    }

    public void putCache(Object value) {
        CacheUtils.put(CACHE_NAME, CACHE_DATA_LIST_KEY , value);
    }

    public Object getCache() {
        return CacheUtils.get(CACHE_NAME, CACHE_DATA_LIST_KEY);
    }

    public void removeCache() {
        CacheUtils.remove(CACHE_NAME, CACHE_DATA_LIST_KEY);
    }
}
