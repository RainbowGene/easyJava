package com.easyJava.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import com.easyJava.bean.Constants;
import com.easyJava.bean.FieldInfo;
import com.easyJava.bean.TableInfo;
import com.easyJava.utils.PropertyUtils;
import com.easyJava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildTable {
    private static Connection connection=null;
    private static String SQL_SHOW_TABLE_STATUS = "show table status";
    private static String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";
    private static String SQL_SHOW_TABLE_INDEX = "show index from %s";

    static {
        String drivername = PropertyUtils.getString("db.driver.name");
        String url = PropertyUtils.getString("db.url");
        String username = PropertyUtils.getString("db.username");
        String password = PropertyUtils.getString("db.password");

        try{
            Class.forName(drivername);
            connection = DriverManager.getConnection(url,username,password);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库中所有表的信息
     */
    public static List<TableInfo> getTables(){
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TableInfo> tableInfos = new ArrayList<>();
        try{
            ps = connection.prepareStatement(SQL_SHOW_TABLE_STATUS);
            rs = ps.executeQuery();
            while(rs.next()){
                String tablename = rs.getString("name");
                String comment = rs.getString("comment");

                TableInfo tableInfo = new TableInfo();
                String beanName = tablename;
                tableInfo.setTableName(tablename);
                tableInfo.setComment(comment);
                if(Constants.IGNORE_TABLE_PREFIX){
                    beanName = tablename.substring(beanName.indexOf('_')+1);
                }
                beanName = processField(beanName,true);
                tableInfo.setBeanName(beanName);
                tableInfo.setBeanParamName(beanName+Constants.SUFFIX_BEAN_QUERY);

                readFieldInfo(tableInfo);
                getKeyIndexof(tableInfo);
                tableInfos.add(tableInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (connection!=null){
                try {
                    connection.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return tableInfos;
    }

    /**
     * 通过表信息获取其中字段信息
     * @param tableInfo 表信息
     * @return 字段信息数组
     */
    private static void readFieldInfo(TableInfo tableInfo){
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<FieldInfo> fieldInfos = new ArrayList<>();
        try{
            ps = connection.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS,tableInfo.getTableName()));
            rs = ps.executeQuery();

            boolean haveDate = false;
            boolean haveBigDecimal = false;

            while(rs.next()){
                FieldInfo fieldInfo = new FieldInfo();
                String field = rs.getString("field");
                String propertyName = processField(field,false);
                String type = rs.getString("type");
                String extra = rs.getString("extra");
                String comment = rs.getString("comment");
                if(type.indexOf('(')>0){
                    type = type.substring(0,type.indexOf('('));
                }
                String javaType = processJavaType(type);
                fieldInfo.setFieldName(field);
                fieldInfo.setComment(comment);
                fieldInfo.setSqlType(type);
                fieldInfo.setAutoIncrement("".equals(extra)?false:true);
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(javaType);
                //                System.out.println(field+type+extra+comment);

                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,type) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,type)){
                    haveDate = true;
                }
                if(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPE,type)){
                    haveBigDecimal = true;
                }
                tableInfo.setFieldInfoList(fieldInfos);

//                System.out.println(type + ":" + javaType);
                fieldInfos.add(fieldInfo);
            }

            tableInfo.setHaveBigDecimal(haveBigDecimal);
            tableInfo.setHaveDate(haveDate);
            tableInfo.setHaveDateTime(haveDate);

            List<FieldInfo> extendList = new ArrayList<>();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
//                String类型参数,
                if(ArrayUtils.contains(Constants.SQL_STRING_TYPE,fieldInfo.getSqlType())){
                    FieldInfo field = new FieldInfo();
                    field.setJavaType(fieldInfo.getJavaType());
                    field.setPropertyName(fieldInfo.getPropertyName()+Constants.SUFFIX_BEAN_QUERY_FUZZY);
                    field.setFieldName(fieldInfo.getFieldName());
                    field.setSqlType(fieldInfo.getSqlType());
                    extendList.add(field);

                }
//                日期
                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,fieldInfo.getSqlType())||ArrayUtils.contains(Constants.SQL_DATE_TYPES,fieldInfo.getSqlType())){
                    FieldInfo field = new FieldInfo();
                    field.setJavaType("String");
                    field.setPropertyName(fieldInfo.getPropertyName()+Constants.SUFFIX_BEAN_QUERY_TIME_START);
                    field.setFieldName(fieldInfo.getFieldName());
                    field.setSqlType(fieldInfo.getSqlType());
                    extendList.add(field);
                    field = new FieldInfo();
                    field.setJavaType("String");
                    field.setPropertyName(fieldInfo.getPropertyName()+Constants.SUFFIX_BEAN_QUERY_TIME_END);
                    field.setFieldName(fieldInfo.getFieldName());
                    field.setSqlType(fieldInfo.getSqlType());
                    extendList.add(field);
                }
            }
            tableInfo.setExtendFieldInfoList(extendList);

        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        tableInfo.setFieldInfoList(fieldInfos);
    }

    /**
     * 解析唯一索引
     * @param tableInfo 表信息
     * @return
     */
    private static void getKeyIndexof(TableInfo tableInfo){
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<FieldInfo> fieldInfos = new ArrayList<>();
        try{

            Map<String,FieldInfo> tempMap = new HashMap<>();
            for(FieldInfo fieldInfo:tableInfo.getFieldInfoList()){
               tempMap.put(fieldInfo.getFieldName(),fieldInfo);
            }

            ps = connection.prepareStatement(String.format(SQL_SHOW_TABLE_INDEX,tableInfo.getTableName()));
            rs = ps.executeQuery();
            while(rs.next()){
                FieldInfo fieldInfo = new FieldInfo();
                String keyName = rs.getString("key_name");
                int nonUnique = rs.getInt("non_unique");
                String colunmName = rs.getString("column_name");
                if(nonUnique==1){
                    continue;
                }
                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().get(keyName);
                if(null == keyFieldList){
                    keyFieldList = new ArrayList<>();
                    tableInfo.getKeyIndexMap().put(keyName,keyFieldList);
                }
//                for (FieldInfo info : tableInfo.getFieldInfoList()) {
//                    if(info.getFieldName().equals(colunmName)){
//                        keyFieldList.add(info);
//                    }
//                }
                keyFieldList.add(tempMap.get(colunmName));

//                System.out.println(type + ":" + javaType);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    /**
     * 下划线转驼峰
     * @param field 要转换的字符串
     * @param upCaseFirstLetter  首字母是否大写
     * @return 驼峰命名法字符串
     */
    private static String processField(String field,boolean upCaseFirstLetter){
        StringBuffer sb = new StringBuffer();
        String[] fields = field.split("_");
        sb.append(upCaseFirstLetter? StringUtils.UpperCaseFirstLetter(fields[0]):fields[0]);
        for(int i = 1,len=fields.length;i<len;i++){
            sb.append(StringUtils.UpperCaseFirstLetter(fields[i]));
        }
        return sb.toString();
    }

    /**
     * 将数据库中类型转化为Java类型
     * @param type 数据库类型
     * @return Java类
     */
    private static String processJavaType(String type){
        if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,type)||ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,type)){
            return "Date";
        }
        else if(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPE,type)){
            return "BigDecimal";
        }else if(ArrayUtils.contains(Constants.SQL_STRING_TYPE,type)){
            return "String";
        } else if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPE,type)) {
            return "Integer";
        } else if (ArrayUtils.contains(Constants.SQL_LONG_TYPE,type)) {
            return "Long";
        }else {
            throw new RuntimeException("无法识别的类型:"+type);
        }
    }

    public static void main(String[] args) {
        System.out.println(processField("aaa_aaa_aaa", true));
    }
}
