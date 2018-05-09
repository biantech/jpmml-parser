package com.biantech.jpmml.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.model.PMMLUtil;
import org.xml.sax.SAXException;

/**
 * 读取pmml 获取模型
 * 
 * @author biantech
 *
 */
public class PmmlInvoker {
	private ModelEvaluator modelEvaluator;

	// 通过文件读取模型
	public PmmlInvoker(String pmmlFileName) {
		PMML pmml = null;
		InputStream is = null;
		try {
			if (pmmlFileName != null) {
				is = PmmlInvoker.class.getClassLoader().getResourceAsStream(pmmlFileName);
				pmml = PMMLUtil.unmarshal(is);
			}
			try {
				is.close();
			} catch (IOException localIOException) {
			}
			this.modelEvaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);
		} catch (SAXException e) {
			pmml = null;
		} catch (JAXBException e) {
			pmml = null;
		} finally {
			try {
				is.close();
			} catch (IOException localIOException3) {
			}
		}
		this.modelEvaluator.verify();
		System.out.println("模型读取成功");
	}

	// 通过输入流读取模型
	public PmmlInvoker(InputStream is) {
		PMML pmml = null;
		try {
			pmml = PMMLUtil.unmarshal(is);
			try {
				is.close();
			} catch (IOException localIOException) {
			}
			this.modelEvaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);
		} catch (SAXException e) {
			pmml = null;
		} catch (JAXBException e) {
			pmml = null;
		} finally {
			try {
				is.close();
			} catch (IOException localIOException3) {
			}
		}
		this.modelEvaluator.verify();
	}
	
	public Map<FieldName, ?> invoke(Map<FieldName, Object> paramsMap) {
		return this.modelEvaluator.evaluate(paramsMap);
	}
}
