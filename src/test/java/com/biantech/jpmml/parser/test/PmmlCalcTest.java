package com.biantech.jpmml.parser.test;

import com.biantech.jpmml.parser.PmmlCalculator;
import com.biantech.jpmml.parser.PmmlInvoker;
import com.biantech.jpmml.parser.PmmlParserUtils;
import org.apache.commons.lang3.StringUtils;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.ModelEvaluator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class PmmlCalcTest {
	 Logger logger = LoggerFactory.getLogger(PmmlCalcTest.class);
	 String utf8="utf-8";
	 @Test
	 public void  test(){
		 PmmlCalculator pmmlCalator =  new PmmlCalculator();
		 String pmmlName = "iris_rf";
		 String pmmlpath = "iris_rf.pmml";
		 HashMap<String, String> map  =  new HashMap<String, String>();
		 map.put( "Petal.Length", new Double(1.4).toString());
		 map.put("level1" ,Integer.toString(1));
		 map.put("Petal.Width",Integer.toString(5));
		 map.put( "Sepal.Width" ,Integer.toString(6));
		 map.put("Sepal.Length",Integer.toString(1));
		 map.put("Species", Integer.toString(0));
		pmmlCalator.pmmlEvaluator(pmmlName, pmmlpath, map);
	 }

	 
	 @Test
	 public void  test1(){
		 PmmlCalculator pmmlCalator =  new PmmlCalculator();
		 String pmmlName = "Audit";
		 String pmmlpath = "Audit.xml";
		 HashMap<String, String> map  =  new HashMap<String, String>();
		 map.put( "Age", Integer.toString(38));
		 map.put("Employment" , "Private");
		 map.put("Education","College");
		 map.put( "Marital" ,"Unmarried");
		 map.put("Occupation", "Service");
		 map.put("Income", Integer.toString(81838));
		 map.put("Gender", "Female");
		 map.put("Deductions", Integer.toString(0));
		 map.put("Hours", Integer.toString(72));
		 map.put("IGNORE_Accounts", "UnitedStates");
		 pmmlCalator.pmmlEvaluator(pmmlName, pmmlpath, map);
	 }


	@Test
	public void testPmmlSEG6V1Invoker() throws Exception {
	 	//String keyIdName = "id";
	 	String keyIdName="APPLICATION_NO";
	 	//String originalKeyName = "prob_1";//"PROB1";
	 	String originalKeyName="PROB1";
		//String resultKeyName = "probability(1.0)"; //
		String resultKeyName = "PROB_1";
		//文件生成路径;
		//String pmmlModelFile = "doc\\申请评分SEG6-PMMLv1-Modified-F.xml";   // pmml文件路径
		//String modelDataFile = "doc\\申请评分一键上线_1000条样例v1空值赋值2.csv";
		//String pmmlModelFile = "doc\\申请评分SEG6-PMMLv2.xml";
		//String modelDataFile = "doc\\申请评分一键上线_1000条样例v2.csv";
		String pmmlModelFile = "doc\\ascore_pbc_pm_seg1.xml";
		String modelDataFile ="doc\\ascore_pbc_pm_seg1.csv";
		PmmlInvoker invoker = new PmmlInvoker();
		ModelEvaluator evaluator = invoker.initModelEvaluator(pmmlModelFile,true);
		List<HashMap<FieldName, String>> paramList = PmmlParserUtils.readInParams(modelDataFile,utf8,evaluator);
		int lineNum = 0;  //当前处理行数
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder errors = new StringBuilder();
		logger.info("pmmlModel={}",pmmlModelFile);
		errors.append("APPLICATION_NO,origianlProb1,evaluatorProb1\n");
		Exception exception = null;
		for (Map<FieldName, String> paramMap : paramList) {
			try {
				lineNum++;
				logger.info("-------pmmlModel={},当前行={} -------",pmmlModelFile,lineNum);
				logger.info("parameters=" + paramMap);
				Map<FieldName, ?> result = evaluator.evaluate(paramMap);
				for (Map.Entry<FieldName, ?> entry : result.entrySet()) {
					FieldName fieldName = entry.getKey();
					Object fieldValue = entry.getValue();
					logger.info(fieldName + ":" + fieldValue);
				}
				String originalProb1 = paramMap.get(FieldName.create(originalKeyName));
				Object resultProb1 = result.get(FieldName.create(resultKeyName));
				if (StringUtils.isNotBlank(originalProb1) && resultProb1 != null) {
					double tempOriginal = Double.parseDouble(originalProb1);
					double tempResult = Double.parseDouble(resultProb1.toString());
					if (Math.abs(tempOriginal - tempResult) > 0.001) {
						//logger.info("evaluate result errors, origianlProb1=" + originalProb1 + ",resultProb1="+resultProb1 );
						errors.append(paramMap.get( FieldName.create(keyIdName)) + "," + originalProb1 + "," + resultProb1 + "\n");
					}
				}
				stringBuilder.append(paramMap.get(FieldName.create(keyIdName)) + "," + resultProb1 + "\n");
			}catch(Exception ex){
				logger.error("evaluate exception="+ex.toString());
				exception = ex;
			}
		}
		logger.info("{}, evaluate result errors\n {}",pmmlModelFile,errors);
		logger.info("{}, map Result\n {}",pmmlModelFile,stringBuilder);
		if(exception!=null){
			throw exception;
		}
	}
}
