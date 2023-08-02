package com.easyJava.builder;

import com.easyJava.bean.Constants;
import com.easyJava.bean.FieldInfo;
import com.easyJava.bean.TableInfo;
import com.easyJava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildMapperXml {

    private static final String BASE_COLUMN_LIST = "base_column_list";
    private static final String BASE_QUERY_CONDITION = "base_query_condition";
    private static final String BASE_QUERY_CONDITION_EXTEND = "base_query_condition_extend";
    private static final String QUERY_CONDITION = "query_condition";

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPERS_MXLS);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;

        File poFile = new File(folder, className + ".xml");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out, "utf-8");
            bw = new BufferedWriter(outw);

            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            bw.newLine();
            bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"");
            bw.newLine();
            bw.write("        \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            bw.newLine();
            bw.write("<mapper namespace=\""+Constants.PACKAGE_MAPPERS + "." + className +"\">");
            bw.newLine();
            bw.write("\t<!--实体映射-->");
            bw.newLine();
            String poClass = Constants.PACKAGE_PO + "." +tableInfo.getBeanName();
            bw.write("\t<resultMap id=\"base_result_map\" type=\""+poClass+"\">");
            bw.newLine();

            FieldInfo idField = null;
            Set<Map.Entry<String, List<FieldInfo>>> keyIndexMap = tableInfo.getKeyIndexMap().entrySet();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap) {
                if("PRIMARY".equals(entry.getKey())){
                    List<FieldInfo> fieldInfoList = entry.getValue();
                    if(fieldInfoList.size()==1){
                        idField = fieldInfoList.get(0);
                        break;
                    }
                }
            }

            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                bw.write("\t<!--"+fieldInfo.getComment()+"-->");
                bw.newLine();
//                主键用id
                String key = "";
                if(idField!=null && fieldInfo.getPropertyName().equals(idField.getPropertyName())){
                    key = "id";
                }
                else{
                    key = "result";
                }
                bw.write("\t\t<"+key+" column=\""+fieldInfo.getFieldName()+"\" property=\""+fieldInfo.getPropertyName()+"\"/>");
                bw.newLine();
            }
            
            bw.write("\t</resultMap>");
            bw.newLine();
            bw.newLine();

//            通用查询列
            bw.write("\t<!--通用查询列-->");
            bw.newLine();
            bw.write("\t<sql id=\""+BASE_COLUMN_LIST+"\">");
            bw.newLine();
            StringBuilder columnBuilder = new StringBuilder();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                columnBuilder.append(fieldInfo.getFieldName()).append(",");
            }
            String columnBuilderStr = columnBuilder.substring(0,columnBuilder.lastIndexOf(","));
            bw.write("\t\t"+columnBuilderStr);
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();

//            基础查询条件
            bw.write("<!--基础查询条件-->");
            bw.newLine();
            bw.write("\t<sql id=\""+BASE_QUERY_CONDITION+"\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                String stringQuery = "";
                if(ArrayUtils.contains(Constants.SQL_STRING_TYPE,fieldInfo.getSqlType())){
                    stringQuery = " and query." + fieldInfo.getPropertyName() + " != ''";
                }

                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null"+stringQuery+"\">");
                bw.newLine();
                bw.write("\t\t\tand " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();

//            扩展查询条件
            bw.write("<!--扩展查询条件-->");
            bw.newLine();
            bw.write("\t<sql id=\""+BASE_QUERY_CONDITION_EXTEND+"\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getExtendFieldInfoList()) {
                String andWhere = "";
                if(ArrayUtils.contains(Constants.SQL_STRING_TYPE,fieldInfo.getSqlType())){
                    andWhere = "and "+ fieldInfo.getFieldName() + " like concat('%', #{query."+fieldInfo.getPropertyName()+"},'%')" + " != ''";
                }else if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,fieldInfo.getSqlType())){
                    if(fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_START)){
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " >= str_to_date(#{query."+ fieldInfo.getPropertyName() + "}, '%Y-%m-%d') ]]>";
                    }
                    else if(fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_END)){
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " < date_sub(str_to_date(#{query."+ fieldInfo.getPropertyName() + "}, '%Y-%m-%d'), interval -1 day) ]]>";
                    }
                }

                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null and query." + fieldInfo.getPropertyName() + " != ''\">");
                bw.newLine();
                bw.write("\t\t\t"+andWhere);
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();


//            通用条件查询列
            bw.write("<!--通用查询条件列-->");
            bw.newLine();
            bw.write("\t<sql id=\""+QUERY_CONDITION+"\">");
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\""+BASE_QUERY_CONDITION+"\"/>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\""+BASE_QUERY_CONDITION_EXTEND+"\"/>");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();
            bw.newLine();

//            查询列表
            bw.write("<!--查询列表-->");
            bw.newLine();
            bw.write("\t<select id=\"selectList\" resultMap=\"base_result_map\">");
            bw.newLine();
            bw.write("\t\tSELECT <include refid=\"" + BASE_COLUMN_LIST + "\"/> FROM " + tableInfo.getTableName() + " <include refid=\""+QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.orderBy!=null\"> order by ${query.orderBy} </if>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.simplePage!=null\"> limit ${query.simplePage.start},${query.simplePage.end} </if>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();
            bw.newLine();

//            查询数量
            bw.write("<!--查询数量-->");
            bw.newLine();
            bw.write("\t<select id=\"selectCount\" resultType=\"java.lang.Integer\">");
            bw.newLine();
            bw.write("\t\tselect count(1) FROM "+ tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<include refid=\""+QUERY_CONDITION+"\"/>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();
            bw.newLine();


//            插入，匹配有值的字段
            bw.write("<!--插入，匹配有值的字段-->");
            bw.newLine();
            bw.write("\t<insert id=\"insert\" parameterType=\""+poClass+"\">");
            bw.newLine();
            FieldInfo autoIncrementField = null;
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                if(fieldInfo.isAutoIncrement()){
                    autoIncrementField = fieldInfo;
                    break;
                }
            }
            if(autoIncrementField!=null){
                bw.write("\t\t<selectKey keyProperty=\"bean."+autoIncrementField.getFieldName()+"\" resultType=\""+autoIncrementField.getJavaType()+"\" order=\"AFTER\">");
                bw.newLine();
                bw.write("\t\t\tSELECT LAST_INSERT_ID()");
                bw.newLine();
                bw.write("\t\t</selectKey>");
                bw.newLine();

            }
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName()+" != null\">");
                bw.newLine();
                bw.write("\t\t\t\t"+fieldInfo.getFieldName()+",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName()+" != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean."+fieldInfo.getPropertyName()+"},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

//            插入或者更新
            bw.write("<!--插入或者更新-->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdate\" parameterType=\""+poClass+"\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName()+" != null\">");
                bw.newLine();
                bw.write("\t\t\t\t"+fieldInfo.getFieldName()+",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName()+" != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean."+fieldInfo.getPropertyName()+"},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\ton DUPLICATE key update");
            bw.newLine();
            Map<String,String> keyTempMap = new HashMap<>();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                for (FieldInfo item : fieldInfoList) {
                    keyTempMap.put(item.getFieldName(),item.getFieldName());
                }
            }
            bw.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                if(keyTempMap.get(fieldInfo.getFieldName()) != null){
                    continue;
                }
                bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName()+" != null\">");
                bw.newLine();
                bw.write("\t\t\t\t"+fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() +"),");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

//            批量插入
            bw.write("<!--批量插入-->");
            bw.newLine();
            bw.write("\t<insert id=\"insertBatch\" parameterType=\""+poClass+"\">");
            bw.newLine();
            StringBuffer insertField = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                if(fieldInfo.isAutoIncrement()){
                    continue;
                }
                insertField.append(fieldInfo.getFieldName()).append(",");
            }
            String insertFieldStr = insertField.substring(0,insertField.lastIndexOf(","));
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(" + insertFieldStr + ")Values");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\" open=\"(\" close=\")\">");
            bw.newLine();
            StringBuffer insertProperty = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                if(fieldInfo.isAutoIncrement()){
                    continue;
                }
                insertProperty.append("#{item."+fieldInfo.getPropertyName()+"}").append(",");
            }
            String insertPropertyStr = insertProperty.substring(0,insertProperty.lastIndexOf(","));
            bw.write("\t\t\t"+insertPropertyStr);
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

//          批量插入或者更新
            bw.write("<!--批量插入或更新-->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdateBatch\" parameterType=\""+poClass+"\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(" + insertFieldStr + ")Values");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\" open=\"(\" close=\")\">");
            bw.newLine();
            bw.write("\t\t\t"+insertPropertyStr);
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t\ton DUPLICATE key update");
            bw.newLine();
            StringBuffer insertBatchUpdate = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                if(fieldInfo.isAutoIncrement()){
                    continue;
                }
                insertBatchUpdate.append(fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + ")").append(",");
            }
            String insertBatchUpdateStr = insertBatchUpdate.substring(0,insertBatchUpdate.lastIndexOf(","));
            bw.write("\t\t" + insertBatchUpdateStr);
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.newLine();

//            根据主键更新
            bw.write("<!--根据主键更新-->");
            bw.newLine();
            for(Map.Entry<String,List<FieldInfo>> entry : keyIndexMap){
                List<FieldInfo> keyIndexInfoList = entry.getValue();
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                Integer index = 0;
                for (FieldInfo fieldInfo : keyIndexInfoList) {
                    index++;
                    methodParams.append(fieldInfo.getFieldName() + " = #{" + fieldInfo.getFieldName() + "}");
                    methodName.append(StringUtils.UpperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if(index<keyIndexInfoList.size()){
                        methodName.append("And");
                        methodParams.append(" and ");
                    }
                }



                bw.write("<!--根据"+methodName+"查询-->");
                bw.newLine();
                bw.write("\t<select id=\"selectBy"+methodName+"\" resultMap=\"base_result_map\">");
                bw.newLine();
                bw.write("\t\tSELECT <include refid=\"base_column_list\"/> FROM " + tableInfo.getTableName() + " where " + methodParams);
                bw.newLine();
                bw.write("\t</select>");
                bw.newLine();
                bw.newLine();

                bw.write("<!--根据"+methodName+"删除-->");
                bw.newLine();
                bw.write("\t<delete id=\"deleteBy"+methodName+"\">");
                bw.newLine();
                bw.write("\t\tDELETE FROM " + tableInfo.getTableName() + " where " + methodParams);
                bw.newLine();
                bw.write("\t</delete>");
                bw.newLine();
                bw.newLine();

                bw.write("<!--根据"+methodName+"更新-->");
                bw.newLine();
                bw.write("\t<update id=\"updateBy"+methodName+"\" parameterType=\""+poClass+"\">");
                bw.newLine();
                bw.write("\t\tUPDATE " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\t<set>");
                bw.newLine();
                for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                    if(fieldInfo.isAutoIncrement()){
                        continue;
                    }
                    bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName()+" != null\">");
                    bw.newLine();
                    bw.write("\t\t\t\t"+fieldInfo.getFieldName()+" = #{bean."+fieldInfo.getPropertyName()+"},");
                    bw.newLine();
                    bw.write("\t\t\t</if>");
                    bw.newLine();
                }
                bw.write("\t\t</set>");
                bw.newLine();
                bw.write("\t</update>");
                bw.newLine();
                bw.newLine();

            }


            bw.write("</mapper>");
            bw.newLine();


            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (outw != null) {
                try {
                    outw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
