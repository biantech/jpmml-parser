package com.biantech.jpmml.ui;

import com.biantech.jpmml.parser.PmmlInvoker;
import com.biantech.jpmml.parser.PmmlParserUtils;
import org.apache.commons.lang3.StringUtils;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.ModelEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PmmlCalcSwing implements ActionListener {
  	private Logger logger = LoggerFactory.getLogger(PmmlCalcSwing.class);
	private String utf8 = "utf-8";
	private JFrame frame = new JFrame("Pmml模型预测");

	private JPanel controlPanel = new JPanel();
	private Container con = new Container();// 布局1
	private JLabel label1 = new JLabel("Pmml模型");
	private JLabel label2 = new JLabel("数据csv");
	private JTextField text1 = new JTextField();
	private JTextField text2 = new JTextField();
	private JButton button1 = new JButton("选择文件");
	private JButton button2 = new JButton("选择文件");
	private JButton b1 = new JButton("开始解析");
	private JButton b2 = new JButton("取消解析");
	private JFileChooser jfc = new JFileChooser();// 文件选择器
	private String pmmlPath = null;
	private String dataPath = null;
	JTabbedPane tabPane = new JTabbedPane();// 选项卡布局

	PmmlCalcSwing(){}

	public void runFunc() {
		jfc.setCurrentDirectory(new File("c:\\"));// 文件选择器的初始目录定为d盘
		// 下面两行是取得屏幕的高度和宽度
		double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		controlPanel.setLayout(new FlowLayout());
		frame.setLocation(new Point((int) (lx / 2) - 150, (int) (ly / 2) - 150));// 设定窗口出现位置
		frame.setSize(1300, 800);// 设定窗口大小
		frame.setContentPane(tabPane);// 设置布局
		frame.add("结果面板", controlPanel);
		// 下面设定标签等的出现位置和高宽
		label1.setBounds(30, 10, 130, 20);
		label2.setBounds(30, 30, 130, 20);
		text1.setBounds(160, 10, 340, 20);
		text2.setBounds(160, 30, 340, 20);
		button1.setBounds(520, 10, 100, 20);
		button2.setBounds(520, 30, 100, 20);
		b1.setBounds(100, 80, 100, 20);
		b2.setBounds(250, 80, 100, 20);
		button1.addActionListener(this);// 添加事件处理
		button2.addActionListener(this);// 添加事件处理
		b1.addActionListener(this);
		b2.addActionListener(this);
		con.add(label1);
		con.add(label2);
		con.add(text1);
		con.add(text2);
		con.add(button1);
		con.add(button2);
		con.add(b1);
		con.add(b2);
		con.add(jfc);
		controlPanel.setBorder(BorderFactory.createTitledBorder("解析结果"));
		tabPane.add("目录/文件选择", con);// 添加布局1
		frame.setVisible(true);// 窗口可见
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 使能关闭窗口，结束程序
	}

	public void actionPerformed(ActionEvent e) {// 事件处理
		if (e.getSource().equals(button1)) {// 判断触发方法的按钮是哪个
			jfc.setFileSelectionMode(0);// 设定只能选择到文件夹
			int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
			if (state == 1) {
				return;// 撤销则返回
			} else {
				File f = jfc.getSelectedFile();// f为选择到的目录
				text1.setText(f.getAbsolutePath());
				pmmlPath = text1.getText();
			}
		}
		if (e.getSource().equals(button2)) {
			jfc.setFileSelectionMode(0);// 设定只能选择到文件
			int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
			if (state == 1) {
				return;// 撤销则返回
			} else {
				File f = jfc.getSelectedFile();// f为选择到的文件
				text2.setText(f.getAbsolutePath());
				dataPath = text2.getText();
			}
		}

		if (e.getSource().equals(b1)) {
			try {

				List<Map<FieldName, ?>> result = pmmlEvaluateInvoker(pmmlPath, dataPath);
				showTextAreaDemo(result);

			} catch (Exception e1) {
				logger.info("errors",e1);
				showTextException(e1);
			} finally {
			}
		}
	}

	boolean wirteToModelLog = true;

	public List<Map<FieldName, ?>> pmmlEvaluateInvoker(String pmmlModelFile, String dataPath) throws Exception {
		if(wirteToModelLog)
			initLogFile();
		String keyIdName="APPLICATION_NO";
		//String originalKeyName = "prob_1";//"PROB1";
		String originalKeyName="PROB1";
		//String resultKeyName = "probability(1.0)"; //
		String resultKeyName = "PROB_1";
		List<Map<FieldName, ?>> list = new ArrayList<Map<FieldName, ?>>();
		PmmlInvoker invoker = new PmmlInvoker();
		ModelEvaluator evaluator = invoker.initModelEvaluator(pmmlModelFile, true);
		List<HashMap<FieldName, String>> paramList = PmmlParserUtils.readInParams(dataPath, utf8, evaluator);
		int lineNum = 0; // 当前处理行数
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder errors = new StringBuilder();
		logInfo("pmmlModel="+pmmlModelFile);
		//int index= StringUtils.lastIndexOf(pmmlModelFile,"\\");
		String modelName = StringUtils.substringAfterLast(dataPath,"\\");
		errors.append("APPLICATION_NO,origianlProb1,evaluatorProb1\n");
		Exception exception = null;
		for (Map<FieldName, String> paramMap : paramList) {
			lineNum++;
			logInfo("--------"+modelName+" , 行 " + lineNum + " -------");
			logInfo("parameters=" + paramMap);
			Map<FieldName, ?> result = evaluator.evaluate(paramMap);
			list.add(result);
			logInfo(String.format("result=%s",result));
			String originalProb1 = paramMap.get(new FieldName(originalKeyName));
			Object resultProb1 = result.get(new FieldName(resultKeyName));
			if (StringUtils.isNotBlank(originalProb1) && resultProb1 != null) {
				double tempOriginal = Double.parseDouble(originalProb1);
				double tempResult = Double.parseDouble(resultProb1.toString());
				if (Math.abs(tempOriginal - tempResult) > 0.001) {
					errors.append(paramMap.get(new FieldName(keyIdName)) + "," + originalProb1 + "," + resultProb1 + "\n");
				}
			}
			stringBuilder.append(paramMap.get(new FieldName(keyIdName)) + "," + resultProb1 + "\n");
		}
		logInfo(String.format("%s, evaluate result errors\n%s",pmmlModelFile,errors));
		logInfo(String.format("%s, map Result\n%s,%s\n%s",pmmlModelFile,keyIdName,"resultScore",stringBuilder));
		return list;
	}

	private void initLogFile(){
		try {
			String modelName = StringUtils.substringAfterLast(dataPath, "\\") + ".log";
			FileOutputStream outputStream = new FileOutputStream(modelName, false);
			//outputStream.write(message.getBytes("utf-8"));
			outputStream.close();
		}catch(Exception ex){
			logger.info(ex.toString(),ex);
		}
	}
	private void logInfo(String message,Object ...appends){
		logger.info(message,appends);
		if(wirteToModelLog){
			try {
				String modelName = StringUtils.substringAfterLast(dataPath, "\\")+".log";
				FileOutputStream outputStream = new FileOutputStream(modelName,true);
				outputStream.write(message.getBytes("utf-8"));
				outputStream.write("\n".getBytes("utf-8"));
				outputStream.close();
			}catch(Exception ex){
				logger.info(ex.toString(),ex);
			}
		}
	}

	public void showTextAreaDemo(List<Map<FieldName, ?>> list) {
		JTextArea commentTextArea = new JTextArea(38, 115);
		for (int i = 0; i < list.size(); i++) {
			commentTextArea.append("第" + (i + 1) + "行数据结果：" + list.get(i) + "\n");
		}
		JScrollPane scrollPane = new JScrollPane(commentTextArea);
		ScrollPaneLayout flowLayout=new ScrollPaneLayout();
		flowLayout.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		flowLayout.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setLayout(flowLayout);
		scrollPane.setWheelScrollingEnabled(true);
		controlPanel.removeAll();
		controlPanel.add(scrollPane);
		tabPane.add("目录/文件选择", con);
		frame.setVisible(true);
	}

	public void showTextException(Exception e) {
		JTextArea commentTextArea = new JTextArea(38, 115);
		commentTextArea.append("异常信息" + e + "\n");
		JScrollPane scrollPane = new JScrollPane(commentTextArea);
		controlPanel.removeAll();
		controlPanel.add(scrollPane);
		tabPane.add("目录/文件选择", con);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		PmmlCalcSwing pmmlCalcSwing=new  PmmlCalcSwing();
		pmmlCalcSwing.runFunc();
		//List<Map<FieldName, ?>> list = new ArrayList<>();
		//pmmlCalcSwing.showTextAreaDemo(list);
	}
}
