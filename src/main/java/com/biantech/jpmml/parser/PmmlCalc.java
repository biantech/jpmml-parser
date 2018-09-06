package com.biantech.jpmml.parser;

import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.ModelEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;

/**
 * 使用模型
 * @author biantech
 *
 */
public class PmmlCalc {
    static Logger logger = LoggerFactory.getLogger(PmmlCalc.class);
    final static String utf8="utf-8";
    public static void main(String[] args) throws IOException, JAXBException, SAXException {
        if(args.length < 2){
            System.out.println("参数个数不匹配");
        }
        //文件生成路径
        String pmmlPath = args[0];
        String modelArgsFilePath = args[1];
        PmmlInvoker invoker = new PmmlInvoker();
        //invoker.initModelEvaluator(pmmlPath,true);
        ModelEvaluator evaluator = invoker.initModelEvaluator(pmmlPath,true);
        List<HashMap<FieldName, String>> paramList = PmmlParserUtils.readInParams(modelArgsFilePath,utf8,evaluator);
        int lineNum = 0;  //当前处理行数
        //File file = new File("result.txt");
        for(Map<FieldName, String> param : paramList){
            lineNum++;
            //System.out.println("======当前行： " + lineNum + "=======");
            //Files.append("======当前行： " + lineNum + "=======",file,Charset.forName(utf8));
            logger.info("======当前行： " + lineNum + "=======");
            Map<FieldName, ?> result = evaluator.evaluate(param);
            Set<FieldName> keySet = result.keySet();  //获取结果的keySet
            for(FieldName fn : keySet){
                String tempString = result.get(fn).toString();
                logger.info(tempString);
            }
        }
        //logger.info("resultFile="+file.getAbsolutePath());
    }
}