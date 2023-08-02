package com.easyJava.builder;

import com.easyJava.bean.Constants;
import com.easyJava.bean.FieldInfo;
import com.easyJava.bean.TableInfo;
import com.easyJava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BuildServiceImpl {
    public static void execute(TableInfo tableInfo){

        File folder = new File(Constants.PATH_SERVICE);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + "ServiceImpl";
        String interfaceName = tableInfo.getBeanName() + "Service";
        String mapperClass = tableInfo.getBeanName() + "Mapper";
        String mapperBeanName = StringUtils.LowerCaseFirstLetter(mapperClass);
        String queryClass = tableInfo.getBeanName() + "Query";

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
            bw.newLine();

            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVO;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + ".SimplePage;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_MAPPERS + "." + mapperClass + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_ENUMS + ".PageSize;");
            bw.newLine();
            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();
            bw.newLine();
            bw.write("import javax.annotation.Resource;");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw,tableInfo.getComment() + "Service");
            bw.write("@Service(\"" + StringUtils.LowerCaseFirstLetter(tableInfo.getBeanName()) + "Service" + "\")");
            bw.newLine();
            bw.write("public class " + className + " implements " + interfaceName + " {");
            bw.newLine();
            bw.newLine();

            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate " + mapperClass + "<" + tableInfo.getBeanName() + "," + queryClass + "> " + StringUtils.LowerCaseFirstLetter(mapperClass) + ";");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"根据条件查询列表");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic List<"+tableInfo.getBeanName()+"> findListByParam(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".selectList(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"根据条件查询数量");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic int findCountByParam(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".selectCount(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"分页查询");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic PaginationResultVO<"+tableInfo.getBeanName()+"> findListByPage(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\tint count = this.findCountByParam(query);");
            bw.newLine();
            bw.write("\t\tint pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();");
            bw.newLine();
            bw.write("\t\tSimplePage page = new SimplePage(query.getPageNo(),count,pageSize);");
            bw.newLine();
            bw.write("\t\tquery.setSimplePage(page);");
            bw.newLine();
            bw.write("\t\tList<"+tableInfo.getBeanName()+"> list = this.findListByParam(query);");
            bw.newLine();
            bw.write("\t\tPaginationResultVO<"+tableInfo.getBeanName()+"> result = new PaginationResultVO<>(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),list);");
            bw.newLine();
            bw.write("\t\treturn result;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"新增");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic int add(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insert(bean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"新增或更新");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic int addOrUpdate(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insertOrUpdate(bean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"批量新增");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic int addBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tif(listBean == null || listBean.isEmpty()){");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insertBatch(listBean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw,"批量新增或更新");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic int addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tif(listBean == null || listBean.isEmpty()){");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insertOrUpdateBatch(listBean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
//            生成查询
            for(Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()){
                List<FieldInfo> keyIndexInfoList = entry.getValue();
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                StringBuilder params = new StringBuilder();
                Integer index = 0;
                for (FieldInfo fieldInfo : keyIndexInfoList) {
                    index++;
                    methodParams.append(fieldInfo.getJavaType()+" "+fieldInfo.getPropertyName());
                    methodName.append(StringUtils.UpperCaseFirstLetter(fieldInfo.getPropertyName()));
                    params.append(fieldInfo.getPropertyName());
                    if(index<keyIndexInfoList.size()){
                        methodName.append("And");
                        methodParams.append(", ");
                        params.append(", ");
                    }
                }

                BuildComment.createFieldComment(bw,"根据"+methodName+"查询");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic " + tableInfo.getBeanName() + " getBy"+methodName + "("+methodParams+") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".selectBy" + methodName + "(" + params + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw,"根据"+methodName+"删除");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic int deleteBy"+methodName + "("+methodParams+") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".deleteBy" + methodName + "(" + params + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw,"根据"+methodName+"更新");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic int updateBy"+methodName + "("+ tableInfo.getBeanName() + " bean, "+methodParams+") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".updateBy" + methodName + "(bean, " + params + ");");
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
