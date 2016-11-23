package com.thinkgem.jeesite.modules.imessage;

import com.drew.lang.BufferReader;
import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskChild;
import com.thinkgem.jeesite.modules.imessage.entity.HcTaskPhone;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.commons.codec.language.Soundex;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Administrator on 2016/9/26.
 */
public class TxtUtils {

    public static String TXT_PATH = "/taskTxt";
    public static Logger logger = LoggerFactory.getLogger(TxtUtils.class);

    public static String getRequestPath(String path) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("");
        try {
            String classPath = resource.getURL().getPath();
            if (classPath.startsWith("/")) {
                classPath = classPath.substring(1);
            }
            classPath = classPath.substring(0, classPath.indexOf("/WEB-INF/"));

            path = classPath + path;

            File file = new File(path);
            if (!file.exists() && !file.isDirectory()) {
                System.out.println("TxtUtils.getRequestPath " + path + "目录不存在，需要创建");
                //创建目录
                file.mkdir();
            }
            System.out.println(path);
        } catch (IOException e) {
            throw new RuntimeException("获取任务文件夹失败！");
        }

        return path;
    }


    public static List<String> readTxt(String fileName) {
        File file = new File(fileName);
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("------------------目标文件所在目录不存在--------------");
            /*if (!file.getParentFile().mkdirs()) {
                throw new RuntimeException("创建目标文件所在目录失败！");
            }*/
        }
        return readTxt(file);
    }

    public static List<String> readTxt(File file) {
        List<String> strList = Lists.newArrayList();
        FileReader fileReader;
        BufferedReader bufRead = null;
        try {
            fileReader = new FileReader(file);
            bufRead = new BufferedReader(fileReader);
            String read;
            while ((read = bufRead.readLine()) != null) {
                strList.add(read);
            }
        } catch (IOException e) {
            throw new RuntimeException("读取任务发送号码txt文件失败！");
        } finally {
            if (bufRead != null) {
                try {
                    bufRead.close();
                } catch (IOException e) {
                }
            }
        }
        return strList;
    }

    public static void writeTxt(String fileName, List<String> strList) {
        StringBuffer writeBuff = new StringBuffer();
        String enter = "\r\n";

        for (String s : strList) {
            writeBuff.append(s + enter);
        }
        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            File file = new File(fileName);
            //判断目标文件所在的目录是否存在
            if (!file.getParentFile().exists()) {
                //如果目标文件所在的目录不存在，则创建父目录
                System.out.println("------------------目标文件所在目录不存在，准备创建它！");
                if (!file.getParentFile().mkdirs()) {
                    throw new RuntimeException("创建目标文件所在目录失败！");
                }
            }
            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(writeBuff.toString().toCharArray());
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("保存失败，任务号码生成失败！");
        } finally {
            try {
                if (fos != null)
                    fos.close();

                if (pw != null)
                    pw.close();

            } catch (IOException e) {
                throw new RuntimeException("保存失败，write close 失败！");
            }
        }
    }

    public static String getPath(Date date, User user) {
        String path = "";
        if (user == null) {
            path= getRequestPath(TXT_PATH);
        } else {
            path= getRequestPath(TXT_PATH + "/" + user.getId());
        }
        return path + getDatePath(date);
    }

    public static String getDatePath(Date date) {
        return "/" + new SimpleDateFormat("YY/MM/dd/").format(date);
    }

    public static String getTxtPathByUserId(String userId){
        return getRequestPath(TXT_PATH + "/" + userId);
    }

    public static String getFileName(HcTaskPhone taskPhone) {
        String fileName = taskPhone.getTaskId() + "_" + taskPhone.getTaskChildId() + ".txt";
        fileName = getTxtPathByUserId(taskPhone.getCreateBy().getId()) + getDatePath(taskPhone.getCreateDate()) + fileName;
        return fileName;
    }

    public static List<String> readTxt(HcTaskPhone taskPhone) {
        return readTxt(getFileName(taskPhone));
    }

    public static void writeTxt(HcTaskPhone taskPhone) {
        writeTxt(getFileName(taskPhone), taskPhone.getPhoneList());
    }

    public static List<String> readTxt(MultipartFile file) {
        List<String> list = Lists.newArrayList();
        try {
            InputStream inputStream = file.getInputStream();
            InputStreamReader is = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("txt文件读取失败！");
        }
        return list;
    }


    public static List<File> getFile(Object obj) {
        File directory = null;
        if (obj instanceof File) {
            directory = (File) obj;
        } else {
            directory = new File(obj.toString());
        }
        List<File> files = Lists.newArrayList();
        if (directory.isFile()) {
            files.add(directory);
            return files;
        } else if (directory.isDirectory()) {
            File[] fileArr = directory.listFiles();
            for (int i = 0; i < fileArr.length; i++) {
                File fileOne = fileArr[i];
                files.addAll(getFile(fileOne));
            }
        }
        return files;
    }

}
