package com.tgcyber.hotelmobile._utils;

import android.widget.EditText;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {


    public static String unicodeToString(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }



    /*
     * public static int getZan(int zan) { Random r=new Random(10);
     * zan=r.nextInt()+zan; return zan; }
     */
    public static boolean isBlank(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }

    // 判断是否是6位数字验证码
    public static boolean isActivate(String str) {
        if (isBlank(str)) {
            return true;
        }
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("[0-9]{6}$");
            Matcher m = p.matcher(str);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static int getEditTextLen(EditText et) {
        int len = 0;
        if (et != null && et.getText() != null
                && et.getText().toString() != null) {
            len = et.getText().toString().length();
        }

        return len;
    }

    public static boolean equals(String str1, String str2) {
        if (!isBlank(str1) && str1.equals(str2)) {
            return true;
        }
        return false;
    }

    public static int getListCount(List list) {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    public static String formatCatelogString(String strDate) {
        if (isBlank(strDate)) {
            return "";
        }
        String[] datas = strDate.split("/");
        if (datas != null && datas.length == 2) {
            return formatHits(datas[0], datas[1]);
        }
        return strDate;
    }

    public static String formatHits(String child, String hits) {
        try {
            double dChild = Integer.parseInt(child);
            double dHits = Integer.parseInt(hits);
            String wan = "万";
            if (dChild > 10000) {
                dChild = dChild / 10000;
                if (dChild > 10) {
                    child = (((int) dChild)) + wan;
                } else {
                    child = String.format("%.1f" + wan, dChild);
                }

            }

            if (dHits > 10000) {
                dHits = dHits / 10000;
                if (dHits > 10) {
                    hits = (((int) dHits)) + wan;
                } else {
                    hits = String.format("%.1f" + wan, dHits);
                }
            }
            return child + "/" + hits;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0/0";
    }

    public static int parseInt(String str) {
        int result = 0;
        try {
            result = Integer.parseInt(str);
        } catch (Exception e) {

        }
        return result;
    }

    public static boolean isTrue(String str) {
        if ("1".equals(str)) {
            return true;
        }
        return false;
    }

    private static SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static String formatHomeDateString2(String formatTime) {
        long t = System.currentTimeMillis();
        try {
            Date d = formatter.parse(formatTime);
            t = d.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formatHomeDateString("" + t / 1000);

    }

    public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
				/* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

    public static boolean isRightUserName(String name) {
        if (isBlank(name)) {
            return false;
        }
        try {
            String reg = "[\u4e00-\u9fa5A-Za-z0-9_]+";
            return Pattern.matches(reg, name);
        } catch (Exception e) {

        }
        return false;
    }

    public static boolean isRightUserId(String name) {
        if (isBlank(name)) {
            return false;
        }
        try {
            String reg = "[1-9]+[0-9]*";
            return Pattern.matches(reg, name);
        } catch (Exception e) {

        }
        return false;
    }

    public static String DateFormat() {
        java.text.DateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(new Date());
        return dateStr;
    }

    public static String getpotraitUrl(String useid) {
        return "http://imgcdn.cat898.com/upload/userface/" + useid
                + "_190_190.jpg";
    }

    public static String generateURL(String host, String[] params) {
        StringBuilder sb = new StringBuilder();
        sb.append(host);
        if (params != null) {
            sb.append("?");
            for (int i = 0; i < params.length; i = i + 2) {
                if (isBlank(params[i]) || isBlank(params[i + 1])) {
                    continue;
                }
                LogCat.i("StringUtil", "params[i + 1] = " + params[i + 1]);
                String p1 = params[i + 1];
                try {
                    p1 = URLEncoder.encode(params[i + 1], "utf-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                sb.append(params[i]);
                sb.append("=");
                sb.append(p1);
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String generateURL(String host, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(host);
        if (params != null) {
            sb.append("?");
            Set<String> keys = params.keySet();
            if (keys != null) {
                for (String key : keys) {
                    String value = params.get(key);
                    if (isBlank(key) || isBlank(value)) {
                        continue;
                    }
                    try {
                        value = URLEncoder.encode(value, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    sb.append(key);
                    sb.append("=");
                    sb.append(value);
                    sb.append("&");
                }
            }
        }
        return sb.toString();
    }

    public static String makeMD5(String password) {
        MessageDigest md;
        try {
            // 生成一个MD5加密计算摘要
            md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(password.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            password = new BigInteger(1, md.digest()).toString(16);

        } catch (Exception e) {
            e.printStackTrace();
        }
        password = password.substring(8, 24);
        LogCat.i("StringUtil", "password = " + password);
        return password;
    }

    /*
     * private String getIP(long ip_long) { String hexString =
     * Long.toHexString(ip_long); String ip = "" +
     * Integer.parseInt(hexString.substring(0, 2), 16); for (int i = 2; i < 6; i
     * = i + 2) { ip = ip + "." + Integer.parseInt(hexString.substring(i, i +
     * 2), 16); } ip = ip + "." + (2 + Random.nextInt(250)); return ip; }
     */
    public static String long2ip(long ipLong) {
        long mask[] = {0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000};
        long num = 0;
        StringBuffer ipInfo = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            num = (ipLong & mask[i]) >> (i * 8);
            if (i > 0)
                ipInfo.insert(0, ".");
            ipInfo.insert(0, Long.toString(num, 10));
        }
        return ipInfo.toString();
    }

    public static boolean isPhoneNumber(String num) {
        if (isBlank(num)) {
            return false;
        }
        Pattern p = Pattern.compile("1\\d{10}$");
        Matcher m = p.matcher(num);
        return m.matches();
    }

    public static boolean isEmail(String email) {
        if (isBlank(email)) {
            return false;
        }
        return email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+");
    }

    public static String encode(String text) {
        try {
            text = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return text;
    }



    public static String dateToString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String str = sdf.format(date);
        return str;
    }

    public static int getLen(String text) {
        if (isBlank(text)) {
            return 0;
        }
        return text.length();
    }

    /**
     * @param timeStr 秒数，
     * @return
     */
    public static String formatHomeDateString(String timeStr) {
        if (isBlank(timeStr)) {
            return "";
        }
        String result = "";
        try {
            long time = Long.parseLong(timeStr) * 1000L;
            Date date = new Date(time);

            Date now = new Date(System.currentTimeMillis());
            // LogCat.i("StringUtil", date+" now="+now);
            long nowTime = now.getTime();
            long lastTime = date.getTime();
            int daysOfTwo = daysOfTwo(date, now);
            nowTime = nowTime - lastTime;
            nowTime /= 1000 * 60;
            Calendar calNow = Calendar.getInstance();
            calNow.setTime(now);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (nowTime < 1) {
                result = "刚刚";
            } else if (nowTime < 60 && nowTime >= 1) {
                result = nowTime + "分钟前";
            } else if (nowTime >= 60 && nowTime < 60 * 24) {
                result = (nowTime / 60) + "小时前";
            }/* else if (daysOfTwo == 1) {
                result = "昨天";
            } else if (daysOfTwo <= 30) {
                result = daysOfTwo + "天前";
            }*/ else {
                result = cal.get(Calendar.YEAR) + "-" + (cal.get

                        (Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public static String formatHomeDateString3(String timeStr) {
        if (isBlank(timeStr)) {
            return "";
        }
        String result = "";
        try {
            long time = Long.parseLong(timeStr);
            Date date = new Date(time);

            Date now = new Date(System.currentTimeMillis());
            // LogCat.i("StringUtil", date+" now="+now);
            long nowTime = now.getTime();
            long lastTime = date.getTime();
            int daysOfTwo = daysOfTwo(date, now);
            nowTime = nowTime - lastTime;
            nowTime /= 1000 * 60;
            Calendar calNow = Calendar.getInstance();
            calNow.setTime(now);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (nowTime < 1) {
                result = "刚刚";
            } else if (nowTime < 60 && nowTime >= 1) {
                result = nowTime + "分钟前";
            } else if (nowTime >= 60 && nowTime < 60 * 24) {
                result = (nowTime / 60) + "小时前";
            } else if (daysOfTwo == 1) {
                result = "昨天";
            } else if (daysOfTwo <= 30) {
                result = daysOfTwo + "天前";
            } else {
                result = cal.get(Calendar.YEAR) + "-" + (cal.get

                        (Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public static String formatHomeDateString(long time) {
        if (time <= 0) {
            return "";
        }
        String result = "";
        try {
            Date date = new Date(time);
            Date now = new Date(System.currentTimeMillis());
            long nowTime = now.getTime();
            long lastTime = date.getTime();
            int daysOfTwo = daysOfTwo(date, now);
            nowTime = nowTime - lastTime;
            nowTime /= 1000 * 60;
            Calendar calNow = Calendar.getInstance();
            calNow.setTime(now);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (nowTime < 1) {
                result = "刚刚";
            } else if (nowTime < 60 && nowTime >= 1) {
                result = nowTime + "分钟前";
            } else if (nowTime >= 60 && nowTime < 60 * 24) {
                result = (nowTime / 60) + "小时前";
            } else if (daysOfTwo == 1) {
                result = "昨天";
            } else if (daysOfTwo <= 30) {
                result = daysOfTwo + "天前";
            } else {
                result = cal.get(Calendar.YEAR) + "-" + (cal.get

                        (Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @return 20140101010101
     */
    public static String formatNowDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d = new Date(System.currentTimeMillis());
        return sdf.format(d);
    }

    public static String formatDateString(String strDate) {
        if (isBlank(strDate)) {
            return "";
        }
        String result = strDate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(strDate);
            Date now = new Date(System.currentTimeMillis());
            long nowTime = now.getTime();
            long lastTime = date.getTime();

            int daysOfTwo = daysOfTwo(date, now);
            nowTime = nowTime - lastTime;

            nowTime = nowTime / (1000 * 60);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            if (nowTime < 0) {

            } else if (nowTime <= 2) {
                result = "刚刚";
            } else if (nowTime < 60 && nowTime > 2) {
                result = nowTime + "分钟前";
            } else if (daysOfTwo == 0) {

                result = "今天 " + cal.get(Calendar.HOUR_OF_DAY) + ":"
                        + cal.get(Calendar.MINUTE);

            } else if (daysOfTwo == 1) {
                result = "昨天 " + cal.get(Calendar.HOUR_OF_DAY) + ":"
                        + cal.get(Calendar.MINUTE);
            } else {
                result = cal.get(Calendar.YEAR)
                        + "-"
                        + (cal.get(Calendar.MONTH) + 1)
                        + "-"
                        + cal.get(Calendar.DAY_OF_MONTH)
                        + " "
                        + cal.get(Calendar.HOUR_OF_DAY)
                        + ":"
                        + ((cal.get(Calendar.MINUTE) < 10) ? ("0" + cal
                        .get(Calendar.MINUTE)) : (cal
                        .get(Calendar.MINUTE)));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public static int daysOfTwo(Date smdate, Date bdate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));

    }

    public static String str32Md5(String plainText) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String str2Md5(String plainText) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString().substring(8, 24);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String formatStringNotNull(String str) {
        if (str == null || "null".equals(str)) {
            return "";
        }
        return str;
    }

    //是否只含数字
    public static boolean isDigital(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
    public static List<String> getImagList(JSONArray imglistArray) {
        List<String> list = null;
        if (imglistArray != null){
            list = new ArrayList<String>();
            String item;
            for (int i = 0;i<imglistArray.length();i++){
                item = imglistArray.optString(i);
                list.add(item);
            }
        }
        return list;
    }
}
