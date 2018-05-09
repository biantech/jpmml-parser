# jpmml-parser
# pmml-parser
## JPMML解析pmml 模型的例子
- 打包方式
  mvn package 
  会在target 目录下生成 pmmlParser-1.jar.<br/>
  把jar copy 到 根目录下,就可以运行命令进行执行了.
- 调用方式：
  java -jar pmml-parser-1.jar [pmml file] [model input args] <br/>
- example：
  java -jar pmml-parser-1.jar iris_rf.pmml irisv2.csv
