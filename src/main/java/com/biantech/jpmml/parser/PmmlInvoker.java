package com.biantech.jpmml.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.model.JAXBUtil;
import org.jpmml.model.filters.ImportFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 读取pmml 获取模型
 * @author biantech
 *
 */
public class PmmlInvoker {
	private static Logger logger = LoggerFactory.getLogger(PmmlInvoker.class);
	@SuppressWarnings("rawtypes")
	private ModelEvaluator modelEvaluator;
	@SuppressWarnings("rawtypes")
	HashMap<String, ModelEvaluator> map = new HashMap<String, ModelEvaluator>();

	// 通过文件读取模型
	@SuppressWarnings("rawtypes")
	public ModelEvaluator initModelEvaluator(String pmmlFileName,boolean validate) throws IOException, JAXBException, SAXException {
		PMML pmml = null;
		InputStream is = null;
		ModelEvaluator modelEvaluator=null;
		try {
			if (pmmlFileName != null) {
				is = PmmlInvoker.class.getClassLoader().getResourceAsStream(pmmlFileName);
				if (is == null) {
					is = new FileInputStream(pmmlFileName);
				}
				modelEvaluator=initModelEvaluator(is,validate);
				//pmml = PMMLUtil.unmarshal(is);
			}
			//this.modelEvaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);
			//this.modelEvaluator.verify();
			//logger.info("模型读取成功,pmmlFile=" + pmmlFileName);
			return modelEvaluator;
		} finally {
			try {
				if(is!=null)
					is.close();
			} catch (IOException localIOException3) {
				//localIOException3.printStackTrace();
			}
		}
	}

	public ModelEvaluator initModelEvaluator(File file, boolean validate) throws IOException, JAXBException, SAXException {
		InputStream ins = new FileInputStream(file);
		ModelEvaluator modelEvaluator=initModelEvaluator(ins,validate);
		ins.close();
		return modelEvaluator;
	}

	public ModelEvaluator initModelEvaluator(InputStream ins, boolean validate) throws IOException, JAXBException, SAXException {
		PMML pmml = null;
		pmml = unmarshal(ins, validate);
		this.modelEvaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);
		this.modelEvaluator.verify();
		return modelEvaluator;
	}

	private  PMML unmarshal(InputStream is, boolean validate) throws IOException, SAXException, JAXBException {
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		ImportFilter filter = new ImportFilter(reader);
		Source source = new SAXSource(filter, new InputSource(is));
		Unmarshaller unmarshaller = JAXBUtil.createUnmarshaller();
		unmarshaller.setEventHandler(new SimpleValidationEventHandler());
		if(validate){
			Schema schema = JAXBUtil.getSchema();
			unmarshaller.setSchema(schema);
		}
		return (PMML)unmarshaller.unmarshal(source);
	}
	// 初始化得到评估模型（如果存在直接获取，不存在则新建并将模型放到map中）
	public ModelEvaluator getEvaluator(String modelName, String modelFilePath) throws JAXBException, IOException, SAXException {
		if (map.keySet().contains(modelName)) {
			logger.info("获取模型成功");
			return map.get(modelName);
		} else {
			initModelEvaluator(modelFilePath,true);
			logger.info("模型初始化成功");
			map.put(modelName, modelEvaluator);
			return modelEvaluator;
		}
	}
	
	/**
	 * 获取要获得的评分
	 * @param result 传入的结果集
	 * @return 评分分数
	 */
	public String scoreResult(Map<FieldName, ?> result) {
		String score = null;
		Set<FieldName> keySet = result.keySet();
		for (FieldName fn : keySet) {
			if (fn.getValue().equalsIgnoreCase("PROB_1") || "probability(1.0)".equalsIgnoreCase(fn.getValue())) {
				score = result.get(fn).toString();
				break;
			}
		}
		return score;
	}

	/*
	 * 对传入的map进行格式转换为key为FieldName类型的map
	 */
	public HashMap<FieldName, Object> dataType(HashMap<String, String> dataParameters) {
		HashMap<FieldName, Object> map = new HashMap<FieldName, Object>();
		Set<String> keySet = dataParameters.keySet();//
		for (String string : keySet) {
			map.put(new FieldName(string), dataParameters.get(string));
		}
		return map;
	}

	static private class SimpleValidationEventHandler implements ValidationEventHandler {
		@Override
		public boolean handleEvent(ValidationEvent event){
			int severity = event.getSeverity();

			switch(severity){
				case ValidationEvent.ERROR:
				case ValidationEvent.FATAL_ERROR:
					return false;
				default:
					return true;
			}
		}
	}
}
