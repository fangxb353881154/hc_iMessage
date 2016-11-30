package com.thinkgem.jeesite.modules.imessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/11/22.
 */
public class EmojiUtils {

    public static Logger logger = LoggerFactory.getLogger(EmojiUtils.class);

    /**
     * @param str 待转换字符串
     * @return 转换后字符串
     * @Description 将字符串中的emoji表情转换成可以在utf-8字符集数据库中保存的格式（表情占4个字节，需要utf8mb4字符集）
     */
    public static String emojiConvert(String str) {
        String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(sb,"[["+ URLEncoder.encode(matcher.group(1), "UTF-8") + "]]");
            } catch (Exception e) {
                logger.error("emojiConvert error", e);
                e.printStackTrace();

            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param str 转换后的字符串
     * @return 转换前的字符串
     * @Description 还原utf8数据库中保存的含转换后emoji表情的字符串
     */
    public static String emojiRecovery(String str){
        String patternString = "\\[\\[(.*?)\\]\\]";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(sb, URLDecoder.decode(matcher.group(1), "UTF-8"));
            } catch (Exception e) {
                logger.error("emojiRecovery error", e);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
