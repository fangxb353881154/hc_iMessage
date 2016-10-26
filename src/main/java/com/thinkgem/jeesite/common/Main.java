package com.thinkgem.jeesite.common;

import com.google.common.collect.Lists;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedReader;
import java.io.File ;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Administrator on 2016/9/29.
 */
public class Main {
    public static void main(String args[]) throws Exception {    // 所有异常抛出
        File file = new File("F:" + File.separator + "newPhone" + File.separator + "newPhone.zip");    // 定义压缩文件名称
        /*ZipFile zipFile = new ZipFile(file, "GBK");
        Enumeration<ZipEntry> zipEntries = zipFile.getEntries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry zipEnt = (ZipEntry) zipEntries.nextElement();
            if (!zipEnt.isDirectory()) {
                InputStream inputStream = zipFile.getInputStream(zipEnt);
                InputStreamReader is = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(is);
                String line;
                List<String> list = Lists.newArrayList();
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
            }
        }*/
    }

}
