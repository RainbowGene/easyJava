package com.easyJava.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyUtils {
    private static Properties prop = new Properties();
    private static Map<String,String> PROP_MAP = new ConcurrentHashMap<>();

    static {
        InputStream is = null;
        try {
            is = PropertyUtils.class.getClassLoader().getResourceAsStream("application.properties");
            prop.load(new InputStreamReader(is,"utf-8"));
            Iterator<Object> io = prop.keySet().iterator();
            while (io.hasNext()){
                String key = (String) io.next();
                String val = prop.getProperty(key);
                PROP_MAP.put(key,val);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取配置文件中的值
     * @param key 配置文件中键的名称
     * @return 键的值
     */
    public static String getString(String key){
        return PROP_MAP.get(key);
    }

//    public static void main(String[] args) {
//        System.out.println(getString("db.username"));
//    }
}
