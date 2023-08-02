package com.easyJava.bean;

import com.easyJava.utils.PropertyUtils;

//常量
public class Constants {
//    固定字符串
    public static String PRIVATE = "private";
    public static String PUBLIC = "public";

    public static String STATIC = "static";
    public static String PATH_JAVA = "java";
    public static String PATH_RESOURCE = "resources";
//    作者
    public static String AUTHOR_COMMENT;
//    忽略表前缀
    public static boolean IGNORE_TABLE_PREFIX;
//    参数bean后缀
    public static String SUFFIX_BEAN_QUERY;
//  参数模糊搜索后缀
    public static String SUFFIX_BEAN_QUERY_FUZZY;
//  参数日期起至
    public static String SUFFIX_BEAN_QUERY_TIME_START;
    public static String SUFFIX_BEAN_QUERY_TIME_END;
//    Mapper后缀
    public static String SUFFIX_MAPPERS;
//    文件输出路径
    public static String PATH_BASE;
//    包
    public static String PACKAGE_BASE;
//    bean
    public static String PATH_PO;
    public static String PACKAGE_PO;
    //    vo
    public static String PATH_VO;
    public static String PACKAGE_VO;
//    query包
    public static String PATH_QUERY;
    public static String PACKAGE_QUERY;
//    util包
    public static String PACKAGE_UTILS;
    public static String PATH_UTILS;
//    枚举包
    public static String PATH_ENUMS;
    public static String PACKAGE_ENUMS;
//    Mapper包
    public static String PATH_MAPPERS;
    public static String PACKAGE_MAPPERS;
    //    Service包
    public static String PATH_SERVICE;
    public static String PACKAGE_SERVICE;
    //    ServiceImpl包
    public static String PATH_SERVICEIMPL;
    public static String PACKAGE_SERVICEIMPL;
    //    controller包
    public static String PATH_CONTROLLER;
    public static String PACKAGE_CONTROLLER;
    //    exception包
    public static String PATH_EXCEPTION;
    public static String PACKAGE_EXCEPTION;
//    XML路径
    public static String PATH_MAPPERS_MXLS;
//    需要忽略的属性
    public static String IGNORE_BEAN_TOJSON_FIELD;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;
//    日期格式序列化
    public static String BEAN_DATE_FORMAT_EXPRESS;
    public static String BEAN_DATE_FORMAT_CLASS;
//    日期格式反序列化
    public static String BEAN_DATE_UNFORMAT_EXPRESS;
    public static String BEAN_DATE_UNFORMAT_CLASS;

//    SQL类型到JAVA类型映射
    public final static String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};
    public final static String[] SQL_DATE_TYPES = new String[]{"date"};

    public final static String[] SQL_DECIMAL_TYPE = new String[]{"decimal","double","float"};
    public final static String[] SQL_STRING_TYPE = new String[]{"char","varchar","text","mediumtext","longtext"};
    public final static String[] SQL_INTEGER_TYPE = new String[]{"int","tinyint"};
    public final static String[] SQL_LONG_TYPE = new String[]{"bigint"};

    static {
        AUTHOR_COMMENT = PropertyUtils.getString("author.comment");
        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertyUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_QUERY = PropertyUtils.getString("suffix.bean.query");
        SUFFIX_BEAN_QUERY_FUZZY = PropertyUtils.getString("suffix.bean.query.fuzzy");
        SUFFIX_BEAN_QUERY_TIME_START = PropertyUtils.getString("suffix.bean.query.time.start");
        SUFFIX_BEAN_QUERY_TIME_END = PropertyUtils.getString("suffix.bean.query.time.end");
        SUFFIX_MAPPERS = PropertyUtils.getString("suffix.mappers");


        PACKAGE_BASE = PropertyUtils.getString("package.base");
        PACKAGE_PO = PACKAGE_BASE + "." + PropertyUtils.getString("package.po");
        PACKAGE_VO = PACKAGE_BASE + "." + PropertyUtils.getString("package.vo");
        PACKAGE_QUERY = PACKAGE_BASE + "." + PropertyUtils.getString("package.query");
        PACKAGE_UTILS = PACKAGE_BASE + "." + PropertyUtils.getString("package.utils");
        PACKAGE_ENUMS = PACKAGE_BASE + "." + PropertyUtils.getString("package.enums");
        PACKAGE_MAPPERS = PACKAGE_BASE + "." + PropertyUtils.getString("package.mappers");
        PACKAGE_SERVICE = PACKAGE_BASE + "." + PropertyUtils.getString("package.service");
        PACKAGE_SERVICEIMPL = PACKAGE_BASE + "." + PropertyUtils.getString("package.service.impl");
        PACKAGE_CONTROLLER = PACKAGE_BASE + "." + PropertyUtils.getString("package.controller");
        PACKAGE_EXCEPTION = PACKAGE_BASE + "." + PropertyUtils.getString("package.exception");


        PATH_BASE = PropertyUtils.getString("path.base") + PATH_JAVA + "/" + PACKAGE_BASE.replace('.','/') + "/";
        PATH_PO = PATH_BASE + "/" + PropertyUtils.getString("package.po").replace('.','/') + "/";
        PATH_VO = PATH_BASE + "/" + PropertyUtils.getString("package.vo").replace('.','/') + "/";
        PATH_QUERY = PATH_BASE + "/" + PropertyUtils.getString("package.query").replace('.','/') + "/";
        PATH_UTILS = PATH_BASE + "/" + PropertyUtils.getString("package.utils").replace('.','/') + "/";
        PATH_ENUMS = PATH_BASE + "/" + PropertyUtils.getString("package.enums").replace('.','/') + "/";
        PATH_MAPPERS = PATH_BASE + "/" + PropertyUtils.getString("package.mappers").replace('.','/') + "/";
        PATH_MAPPERS_MXLS = PropertyUtils.getString("path.base") + "/" + PATH_RESOURCE + "/" + PACKAGE_MAPPERS.replace('.','/')+"/";
        PATH_SERVICE = PATH_BASE + PropertyUtils.getString("package.service") + "/";
        PATH_SERVICEIMPL = PATH_BASE + PropertyUtils.getString("package.service.impl") + "/" ;
        PATH_CONTROLLER = PATH_BASE + PropertyUtils.getString("package.controller") + "/" ;
        PATH_EXCEPTION = PATH_BASE + PropertyUtils.getString("package.exception") + "/" ;


        IGNORE_BEAN_TOJSON_FIELD = PropertyUtils.getString("ignore.bean.tojson.field");
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertyUtils.getString("ignore.bean.tojson.expression");
        IGNORE_BEAN_TOJSON_CLASS = PropertyUtils.getString("ignore.bean.tojson.class");

        BEAN_DATE_FORMAT_EXPRESS = PropertyUtils.getString("bean.date.format.express");
        BEAN_DATE_FORMAT_CLASS = PropertyUtils.getString("bean.date.format.class");

        BEAN_DATE_UNFORMAT_EXPRESS = PropertyUtils.getString("bean.date.unformat.express");
        BEAN_DATE_UNFORMAT_CLASS = PropertyUtils.getString("bean.date.unformat.class");


    }
    public static void main(String[] args) {
        System.out.println(PATH_PO);
    }
}
