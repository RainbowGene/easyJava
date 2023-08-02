package com.easyJava.builder;

import com.easyJava.bean.Constants;
import com.easyJava.bean.FieldInfo;
import com.easyJava.bean.TableInfo;
import com.easyJava.utils.DateUtils;
import com.easyJava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

public class BuildPojo {
    public static void execute(TableInfo tableInfo){

        File folder = new File(Constants.PATH_PO);
        if(!folder.exists()){
            folder.mkdirs();
        }

        File poFile = new File(folder,tableInfo.getBeanName()+".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try{
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out,"utf-8");
            bw = new BufferedWriter(outw);
            bw.write("package " + Constants.PACKAGE_PO + ";");
            bw.newLine();
            bw.newLine();

//            包名，import
            bw.write("import java.io.Serializable;");
            bw.newLine();
            if(tableInfo.isHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            if(tableInfo.isHaveDate()||tableInfo.isHaveDateTime()){
                bw.write("import java.util.Date;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS);
                bw.newLine();
                bw.write(Constants.BEAN_DATE_UNFORMAT_CLASS);
                bw.newLine();
                bw.write("import com.easyJavaTest.enums.DateTimePatternEnum;");
                bw.newLine();
                bw.write("import com.easyJavaTest.utils.DateUtils;");
                bw.newLine();
            }
            boolean ignoreBean = false;
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                if(ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FIELD.split(","),fieldInfo.getPropertyName())){
                    ignoreBean = true;
                }
            }
            if(ignoreBean){
                bw.write(Constants.IGNORE_BEAN_TOJSON_CLASS);
                bw.newLine();
            }

            bw.newLine();

//            生成类注释
            BuildComment.createClassComment(bw,tableInfo.getComment());
            bw.write("public class "+tableInfo.getBeanName()+" implements Serializable {");
            bw.newLine();

//            属性
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                BuildComment.createFieldComment(bw,fieldInfo.getComment());

                if(ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FIELD.split(","),fieldInfo.getPropertyName())){
                    bw.write("\t"+Constants.IGNORE_BEAN_TOJSON_EXPRESSION);
                    bw.newLine();
                }

                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,fieldInfo.getSqlType())){
                    bw.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESS, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                    bw.write("\t"+String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESS, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                }
                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,fieldInfo.getSqlType())){
                    bw.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESS, DateUtils.YYYY_MM_DD));
                    bw.newLine();
                    bw.write("\t"+String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESS, DateUtils.YYYY_MM_DD));
                    bw.newLine();
                }

                bw.write("\t" + Constants.PRIVATE + " " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
            }

//            重写get，set方法
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                String tempField = StringUtils.UpperCaseFirstLetter(fieldInfo.getPropertyName());

                BuildComment.createMethodComment(bw,fieldInfo.getComment());
                bw.write("\tpublic void set"+ tempField+"("+fieldInfo.getJavaType()+" "+fieldInfo.getPropertyName()+") {");
                bw.newLine();
                bw.write("\t\tthis."+fieldInfo.getPropertyName()+" = "+fieldInfo.getPropertyName()+";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createMethodComment(bw,fieldInfo.getComment());
                bw.write("\tpublic "+ fieldInfo.getJavaType() +" get"+ tempField+"() {");
                bw.newLine();
                bw.write("\t\treturn this."+fieldInfo.getPropertyName()+";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
            }

//            重写toString

            StringBuffer fieldToString = new StringBuffer();
            Integer index = 0;
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {

                String propName = fieldInfo.getPropertyName();
                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,fieldInfo.getSqlType())){
                    propName = "DateUtils.format(" + fieldInfo.getPropertyName() + ", DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())";
                }
                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,fieldInfo.getSqlType())){
                    propName = "DateUtils.format(" + fieldInfo.getPropertyName() + ", DateTimePatternEnum.YYYY_MM_DD.getPattern())";
                }

                fieldToString.append("\"" + fieldInfo.getComment() + " : \" + (" + fieldInfo.getPropertyName() + " == null ? \"空\" : " + propName + ")");
                if(index!=tableInfo.getFieldInfoList().size()-1) {
                    fieldToString.append(" + \",\" + ");
                }
                else{
                    fieldToString.append(" + ");
                }
                index++;
            }
            fieldToString.substring(0,fieldToString.lastIndexOf(",")-1);

            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            bw.write("\t\treturn \""+tableInfo.getBeanName()+" {\" + ");
            bw.write(String.valueOf(fieldToString));
            bw.write("\"}\";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.write("}");
            bw.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(outw!=null){
                try {
                    outw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
