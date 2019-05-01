# jpmml-parser
## JPMML解析pmml 模型的例子
- 打包方式
  mvn package 
  当前目录下生成 jpmml-parser-1-jar-with-dependencies.jar.<br/>
  把jar copy 到 根目录下,就可以运行命令进行执行了.
- 调用方式：
  java -jar jpmml-parser-1-jar-with-dependencies.jar [pmml file] [model input args] <br/>
- example：
  java -jar jpmml-parser-1-jar-with-dependencies.jar audit.xml audit.csv
  java -jar jpmml-parser-1-jar-with-dependencies.jar demo-model.pmml demo-data.csv

![如果觉得有帮助,请支持一下](https://github.com/biantech/jpmml-parser/blob/master/image/alipay-pic.jpg)
