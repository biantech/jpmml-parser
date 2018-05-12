package com.biantech.jpmml.parser.test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dmg.pmml.FieldName;


import com.biantech.jpmml.parser.PmmlCalc;
import com.biantech.jpmml.parser.PmmlInvoker;
import org.junit.Test;

public class PmmlCalcTest {

	@Test
	public void name() {

	}

	@Test
	public void test() throws IOException {
		 //文件生成路径   
        //PrintStream ps=new PrintStream("result.txt");
        //System.setOut(ps);
		String pmmlPath = "model_rf.pmml";   // pmml文件路径
		String modelArgsFilePath = "ir-data.csv";
		
		PmmlInvoker invoker = new PmmlInvoker(pmmlPath);
		 List<Map<FieldName, String>> paramList = PmmlCalc.readInParams(modelArgsFilePath);
		 int lineNum = 0;  //当前处理行数
		 for(Map<FieldName, String> param : paramList){
			 lineNum++;
			 System.out.println("======当前行： " + lineNum + "=======");
			 Map<FieldName, ?> result = invoker.invoke(param);
			 Set<FieldName> keySet = result.keySet();  //获取结果的keySet
			 for(FieldName fn : keySet){
				 System.out.println(result.get(fn).toString());
			 }
		 }
	}
}
