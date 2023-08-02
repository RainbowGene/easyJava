package com.easyJava.builder;

import com.easyJava.bean.Constants;
import com.easyJava.utils.DateUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;

public class BuildComment {
    public static void createClassComment(BufferedWriter bw,String comment) throws IOException {
        bw.write("/**");
        bw.newLine();
        bw.write(" * @Description: "+comment);
        bw.newLine();
        bw.write(" * @author: "+ Constants.AUTHOR_COMMENT);
        bw.newLine();
        bw.write(" * @date: "+ DateUtils.format(new Date(),DateUtils._YYYYMMDD));
        bw.newLine();
        bw.write(" */");
        bw.newLine();
    }
    public static void createFieldComment(BufferedWriter bw,String comment) throws IOException {
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * "+ (comment==null?"":comment) );
        bw.newLine();
        bw.write("\t */");
        bw.newLine();
    }
    public static void createMethodComment(BufferedWriter bw,String comment) throws IOException {
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * "+ (comment==null?"":comment) );
        bw.newLine();
        bw.write("\t */");
        bw.newLine();
    }
}
