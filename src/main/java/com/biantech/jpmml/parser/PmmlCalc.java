package com.biantech.jpmml.parser;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.io.Files;
import org.dmg.pmml.FieldName;

/**
 * 使用模型
 * @author biantech
 *
 */
public class PmmlCalc {
	final static String utf8="utf-8";
	public static void main(String[] args) throws IOException {
		if(args.length < 2){
			System.out.println("参数个数不匹配");
		}
		 //文件生成路径   
        //PrintStream ps=new PrintStream("result.txt");
        //System.setOut(ps);
		String pmmlPath = args[0];  
		String modelArgsFilePath = args[1];
		PmmlInvoker invoker = new PmmlInvoker(pmmlPath);
		List<Map<FieldName, String>> paramList = readInParams(modelArgsFilePath);
		int lineNum = 0;  //当前处理行数
		File file = new File("result.txt");
		for(Map<FieldName, String> param : paramList){
		 lineNum++;
		 //System.out.println("======当前行： " + lineNum + "=======");
		 Files.append("======当前行： " + lineNum + "=======",file,Charset.forName(utf8));
		 Map<FieldName, ?> result = invoker.invoke(param);
		 Set<FieldName> keySet = result.keySet();  //获取结果的keySet
		 for(FieldName fn : keySet){
		 	String tempString = result.get(fn).toString()+"\n";
			Files.append(tempString,file,Charset.forName(utf8));
		 	//System.out.println(result.get(fn).toString());
		 }
		 //Files.append(""
		}
	}
	
	/**
	 * 读取参数文件
	 * @param filePath 文件路径
	 * @return
	 * @throws IOException 
	 */
	public static List<Map<FieldName,String>> readInParams(String filePath) throws IOException{
		InputStream is;
		is = PmmlCalc.class.getClassLoader().getResourceAsStream(filePath);
		if(is==null){
			is = new FileInputStream(filePath);
		}
		InputStreamReader isreader = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isreader);
		//BufferedReader br = new BufferedReader(new FileReader(filePath));
		String[] nameArr = br.readLine().split(",");  //读取表头的名字
		ArrayList<Map<FieldName,String>> list = new ArrayList<>();
		String paramLine;  //一行参数
		//循环读取  每次读取一行数据
		while((paramLine = br.readLine()) != null){
			Map<FieldName,String> map = new HashMap<>();
			String[] paramLineArr = paramLine.split(",");
			for(int i=0; i<paramLineArr.length; i++){//一次循环处理一行数据
				map.put(new FieldName(nameArr[i]), paramLineArr[i]); //将表头和值组成map 加入list中
			}
			list.add(map);
		}
		is.close();
		return list;
	}
	
}
