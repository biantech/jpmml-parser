package com.biantech.jpmml.parser;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.ModelEvaluator;

import java.io.*;
import java.util.*;

public class PmmlParserUtils {
    /**
     * 读取参数文件
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public static List<HashMap<FieldName,String>> readInParams(String filePath, String encoding, ModelEvaluator modelEvaluator) throws IOException {
        InputStream is;
        is = PmmlParserUtils.class.getClassLoader().getResourceAsStream(filePath);
        if(is==null){
            is = new FileInputStream(filePath);
        }
        ArrayList<HashMap<FieldName,String>> list ;
        list=readInParams(is,encoding);
        is.close();
        return list;
    }

    public static ArrayList<HashMap<FieldName,String>> readInParams(InputStream inputStream, String encoding) throws IOException {
        InputStreamReader isreader = new InputStreamReader(inputStream,encoding);
        BufferedReader br = new BufferedReader(isreader);
        String[] nameArr = br.readLine().split(",");  //读取表头的名字
        ArrayList<HashMap<FieldName,String>> list = new ArrayList<HashMap<FieldName,String>>();
        String paramLine;  //一行参数
        //循环读取  每次读取一行数据
        while((paramLine = br.readLine()) != null){
            HashMap<FieldName,String> map = new LinkedHashMap<FieldName,String>();
            String[] paramLineArr = paramLine.split(",");
            for(int i=0; i<paramLineArr.length; i++){//一次循环处理一行数据
                String tempValue=paramLineArr[i];
                FieldName fieldName=new FieldName(nameArr[i].toUpperCase().trim());
                map.put(fieldName, tempValue); //将表头和值组成map 加入list中
            }
            if(map.size()>1) {
                list.add(map);
            }
        }
        return list;
    }
}
