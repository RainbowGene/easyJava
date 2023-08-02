package com.easyJava.builder;

import com.easyJava.bean.Constants;
import com.easyJava.bean.FieldInfo;
import com.easyJava.bean.TableInfo;
import com.easyJava.utils.DateUtils;
import com.easyJava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BuildQuery {
    public static void execute(TableInfo tableInfo){
        File folder = new File(Constants.PATH_QUERY);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
        File poFile = new File(folder,className + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try{
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out,"utf-8");
            bw = new BufferedWriter(outw);
            bw.write("package " + Constants.PACKAGE_QUERY + ";");
            bw.newLine();
            bw.newLine();


//            包名，import
            if(tableInfo.isHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            if(tableInfo.isHaveDate()||tableInfo.isHaveDateTime()){
                bw.write("import java.util.Date;");
                bw.newLine();
            }

            bw.newLine();

//            生成类注释
            BuildComment.createClassComment(bw,tableInfo.getComment()+"查询对象");
            bw.write("public class "+ className +" extends BaseQuery {");
            bw.newLine();

//            属性
            for (FieldInfo fieldInfo : tableInfo.getFieldInfoList()) {
                BuildComment.createFieldComment(bw,fieldInfo.getComment());
                bw.write("\t" + Constants.PRIVATE + " " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();

//                String类型参数,
                if(ArrayUtils.contains(Constants.SQL_STRING_TYPE,fieldInfo.getSqlType())){
                    bw.write("\t" + Constants.PRIVATE + " " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY + ";");
                    bw.newLine();
                }
//                日期
                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,fieldInfo.getSqlType())||ArrayUtils.contains(Constants.SQL_DATE_TYPES,fieldInfo.getSqlType())){
                    bw.write("\t" + Constants.PRIVATE + " " + "String" + " " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START + ";");
                    bw.newLine();
                    bw.write("\t" + Constants.PRIVATE + " " + "String" + " " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END + ";");
                    bw.newLine();
                }
            }
//            get，set方法
            for (FieldInfo fieldInfo :tableInfo.getFieldInfoList()) {
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
            for (FieldInfo fieldInfo :tableInfo.getExtendFieldInfoList()) {
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
