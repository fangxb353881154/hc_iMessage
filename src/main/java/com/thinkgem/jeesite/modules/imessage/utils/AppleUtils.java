package com.thinkgem.jeesite.modules.imessage.utils;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.StringUtils;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Administrator on 2016/11/28.
 */
public class AppleUtils {

    public String doPost(String url, Map<String, String> map, String charset) {
        HttpPost httpPost = null;
        String result = null;
        try {
            httpPost = new HttpPost(url);

            httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");


            CookieSpecFactory csf = new CookieSpecFactory() {
                public CookieSpec newInstance(HttpParams params) {
                    return new BrowserCompatSpec() {
                        @Override
                        public void validate(Cookie cookie, CookieOrigin origin)
                                throws MalformedCookieException {
                            // Oh, I am easy
                        }
                    };
                }
            };

            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCookieSpecs().register("easy", csf);
            httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");

            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> elem = (Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpclient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
           // ex.printStackTrace();
        }

        return result;
    }

    public boolean send(String appleId, String password) {
        Map<String, String> map = Maps.newHashMap();
        map.put("_a", "login.sign");
        map.put("_fid", "si");
        map.put("aywysya", ".8a44j1d7lY5BNvcKyAdMUDFBpBeA0fUm0NUbNiqUU8j0OXTA6FL.26y8GGEDd5ihORoVyFGh8cmvSuCKzIlnY6xljQlpRDB5rafe56Ehqxf7_OLgiPFMJhHFW_jftckkCoqAkCoq0NUuAuyPB94UXuGlfUm0NUbNiqUU8j0OXTA6FL.26y8GGEDd5ihORoVyFGh8cmvSuCKzIlnY6xljQlpRDB5rafe56Ehqxf7_OLgiPFMMxy0kyMpwoNJRPaHDz4a1yjaY2ftckuyPBDjaY1HGOg3ZLQ0FxNTVecJa.ezLu_dYV6HzL0TFc4NO7TjOy_Aw7Q_v9NA2pNidCgSvxQs.xLB.Tf1X.2X_DKpyg1wWF9kmFxFjpFjsJrJ5tTma1kWNNW5CfUXtStKjE4PIDxO9sPrsiMTKQnlLZnjxHQUhkYRQqwSMETnsKtEhlrlgNlrK1BNlY0aarVW5BSU..2XD");
        map.put("c", "aHR0cDovL3d3dy5hcHBsZS5jb20vY24vfDFhb3MzZWFiN2VjMTY3NjNjNWY0M2UzZjVhY2E1MzA3YjViNGEzZGZmYTAz");
        map.put("fdcBrowserData", "%7B%22U%22%3A%22Mozilla%2F5.0%20(Windows%20NT%206.1%3B%20WOW64)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F54.0.2840.99%20Safari%2F537.36%22%2C%22L%22%3A%22zh-CN%22%2C%22Z%22%3A%22GMT%2B08%3A00%22%2C%22V%22%3A%221.0%22%7D");
        map.put("login-appleId", appleId);
        map.put("login-password", password);
        map.put("r", "SCDHYHP7CY4H9XK2H");
        map.put("s", "aHR0cDovL3d3dy5hcHBsZS5jb20vY24vfDFhb3MzZWFiN2VjMTY3NjNjNWY0M2UzZjVhY2E1MzA3YjViNGEzZGZmYTAz");
        String url = "https://secure2.store.apple.com/cn/shop/sentryx/sign_in";

        String result = doPost(url, map, "utf-8");
        System.out.println(appleId + "         ----result:   " + result);
        if (StringUtils.isNotEmpty(result)) {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject headJson = (JSONObject) jsonObject.get("head");
            int status = headJson.getInt("status");
            boolean o = false;
            switch (status) {
                case 301:
                case 302:
                case 303:
                case 304:
                case 305:
                case 306:
                case 307:
                    o = true;
                    break;
                default:
                    break;
            }
            return o;
        }
        return false;
    }

    public static void main(String[] args) {
        AppleUtils apple = new AppleUtils();
        boolean result = apple.send("353881154@qq.com", "Fangxb1015.");

    }
}
