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

public class BuildService {
    public static void execute(TableInfo tableInfo){

        File folder = new File(Constants.PATH_SERVICE);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + "Service";

        File poFile = new File(folder,className + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try{
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out,"utf-8");
            bw = new BufferedWriter(outw);
            bw.write("package " + Constants.PACKAGE_SERVICE + ";");
            bw.newLine();

            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVO;");
            bw.newLine();

            BuildComment.createClassComment(bw,tableInfo.getComment() + "Service");
            bw.write("public interface " + className + "{");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"根据条件查询列表");
            bw.write("\tList<"+tableInfo.getBeanName()+"> findListByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"根据条件查询数量");
            bw.write("\tint findCountByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"分页查询");
            bw.write("\tPaginationResultVO<"+tableInfo.getBeanName()+"> findListByPage(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"新增");
            bw.write("\tint add(" + tableInfo.getBeanName() + " bean);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"新增或更新");
            bw.write("\tint addOrUpdate(" + tableInfo.getBeanName() + " bean);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"批量新增");
            bw.write("\tint addBatch(List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"批量新增或更新");
            bw.write("\tint addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();
            bw.newLine();

            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
//            生成查询
            for(Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()){
                List<FieldInfo> keyIndexInfoList = entry.getValue();
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                Integer index = 0;
                for (FieldInfo fieldInfo : keyIndexInfoList) {
                    index++;
                    methodParams.append(fieldInfo.getJavaType()+" "+fieldInfo.getPropertyName());
                    methodName.append(StringUtils.UpperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if(index<keyIndexInfoList.size()){
                        methodName.append("And");
                        methodParams.append(", ");
                    }
                }

                BuildComment.createFieldComment(bw,"根据"+methodName+"查询");
                bw.write("\t" + tableInfo.getBeanName() + " getBy"+methodName + "("+methodParams+");");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw,"根据"+methodName+"删除");
                bw.write("\tint deleteBy"+methodName + "("+methodParams+");");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw,"根据"+methodName+"更新");
                bw.write("\tint updateBy"+methodName + "("+ tableInfo.getBeanName() + " bean, "+methodParams+");");
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
