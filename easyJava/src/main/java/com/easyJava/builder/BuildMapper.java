package com.easyJava.builder;

import com.easyJava.bean.Constants;
import com.easyJava.bean.FieldInfo;
import com.easyJava.bean.TableInfo;
import com.easyJava.utils.DateUtils;
import com.easyJava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BuildMapper {
    public static void execute(TableInfo tableInfo){

        File folder = new File(Constants.PATH_MAPPERS);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;

        File poFile = new File(folder,className +".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try{
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out,"utf-8");
            bw = new BufferedWriter(outw);
            bw.write("package " + Constants.PACKAGE_MAPPERS + ";");
            bw.newLine();
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

            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            bw.newLine();

//            生成类注释
            BuildComment.createClassComment(bw,tableInfo.getComment());
            bw.write("public interface "+ className +"<T,P> extends BaseMapper {");
            bw.newLine();

            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();

//            生成查询
            for(Map.Entry<String,List<FieldInfo>> entry : keyIndexMap.entrySet()){
                List<FieldInfo> keyIndexInfoList = entry.getValue();
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                Integer index = 0;
                for (FieldInfo fieldInfo : keyIndexInfoList) {
                    index++;
                    methodParams.append("@Param(\""+fieldInfo.getPropertyName()+"\") "+fieldInfo.getJavaType()+" "+fieldInfo.getPropertyName());
                    methodName.append(StringUtils.UpperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if(index<keyIndexInfoList.size()){
                        methodName.append("And");
                        methodParams.append(", ");
                    }
                }

                BuildComment.createFieldComment(bw,"根据"+methodName+"查询");
                bw.write("\tT selectBy"+methodName + "("+methodParams+");");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw,"根据"+methodName+"删除");
                bw.write("\tint deleteBy"+methodName + "("+methodParams+");");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw,"根据"+methodName+"更新");
                bw.write("\tint updateBy"+methodName + "("+"@Param(\"bean\") T t, "+methodParams+");");
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
