package com.easyJava.builder;

import com.easyJava.bean.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BuildBase {
    public static void execute() {
        List<String> headerInfo  = new ArrayList<>();

        headerInfo.clear();
        headerInfo.add("package " + Constants.PACKAGE_ENUMS + ";");
//        生成date枚举
        build(headerInfo, "DateTimePatternEnum", Constants.PATH_ENUMS);

//        生成ResponseCodeEnum枚举
        build(headerInfo, "ResponseCodeEnum", Constants.PATH_ENUMS);

        headerInfo.clear();
        headerInfo.add("package " + Constants.PACKAGE_UTILS + ";");
//        生成date工具类
        build(headerInfo, "DateUtils", Constants.PATH_UTILS);

        headerInfo.clear();
        headerInfo.add("package " + Constants.PACKAGE_MAPPERS + ";");
//        生成baseMapper
        build(headerInfo, "BaseMapper", Constants.PATH_MAPPERS);

        headerInfo.clear();
        headerInfo.add("package " + Constants.PACKAGE_EXCEPTION + ";");
        headerInfo.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum;");
//        生成businessException
        build(headerInfo, "BusinessException", Constants.PATH_EXCEPTION);

        headerInfo.clear();
        headerInfo.add("package " + Constants.PACKAGE_ENUMS + ";");
//        生成pagesize枚举
        build(headerInfo, "PageSize", Constants.PATH_ENUMS);

        headerInfo.clear();
        headerInfo.add("package " + Constants.PACKAGE_QUERY + ";");
        headerInfo.add("import " + Constants.PACKAGE_ENUMS + ".PageSize;");
//        生成分页信息
        build(headerInfo, "SimplePage", Constants.PATH_QUERY);

        headerInfo.clear();
        headerInfo.add("package " + Constants.PACKAGE_CONTROLLER + ";");
        headerInfo.add("import " + Constants.PACKAGE_VO + ".ResponseVO;");
        headerInfo.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum;");
//        生成ABaseController
        build(headerInfo, "ABaseController", Constants.PATH_CONTROLLER);

        headerInfo.clear();
        headerInfo.add("package " + Constants.PACKAGE_CONTROLLER + ";");
        headerInfo.add("import " + Constants.PACKAGE_VO + ".ResponseVO;");
        headerInfo.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum;");
        headerInfo.add("import " + Constants.PACKAGE_EXCEPTION + ".BusinessException;");
//        生成AGlobalExceptionHandlerController
        build(headerInfo, "AGlobalExceptionHandlerController", Constants.PATH_CONTROLLER);

        headerInfo.clear();
        headerInfo.add("package " + Constants.PACKAGE_QUERY + ";");
//        生成BaseQuery
        build(headerInfo , "BaseQuery", Constants.PATH_QUERY);

        headerInfo.clear();
        headerInfo.add("package " + Constants.PACKAGE_VO + ";");
//        生成PaginationResultVO
        build(headerInfo , "PaginationResultVO", Constants.PATH_VO);
//        生成ResponseVO
        build(headerInfo , "ResponseVO", Constants.PATH_VO);
    }

    private static void build(List<String> headerInfo, String fileName, String outputPath) {
        File folder = new File(outputPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File javaFile = new File(outputPath, fileName + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {

            os = new FileOutputStream(javaFile);
            osw = new OutputStreamWriter(os, "utf-8");
            bw = new BufferedWriter(osw);

            String templatePath = BuildBase.class.getClassLoader().getResource("template/" + fileName + ".txt").getPath();

            is = new FileInputStream(templatePath);
            isr = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isr);

            for (String s : headerInfo) {
                bw.write(s);
                bw.newLine();
                bw.newLine();
            }


            String lineInfo = null;
            while ((lineInfo = br.readLine()) != null) {
                bw.write(lineInfo);
                bw.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

}
