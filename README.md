# Dsequence分布式序列服务

### 快速介绍

Dsequence 是一个基于java 的**轻量级的分布式序列服务**，为业务系统提供序列号生成服务。具有分布式，高可用，高性能，低侵入等特点。这是一个纯java 编写的序列服务，类似数据库的自增长主键，但其并不是直接使用了mysql 的自增长ID而实现，只是使用MySQL作为持久，且其对数据库的压力几乎可以忽略（亿级序列）。我之所以设计该项目最主要的原因是因为在一个核心的分布式系统中使用MySQL数据库做批量插入（主键为自增长ID）时，造成锁表等待。使用该服务后数据库批量插入再无锁等待,同时该项目也可适用到其它业务系统中。

### 核心原理


业务系统（客户端）每次来获取序列时并不是每次获取一个而是一批，然后放到本地缓冲区队列（队列长度10000），同时业务系统（客户端）会创建一个异步线程扫描队列长度，当本地缓冲区队列中的序列数小于本地缓冲区队列长度的80%时，会启动序列饥饿机制（异步填充本地缓冲区队列直至本地缓冲区队列值满）。

服务中的队列是持久化的，每个序列都持久化在底层数据库（MySQL）中，每个序列服务节点启动时会**事先准备一个批次的序列放在缓冲区**，这个批次的大小等于本地缓冲区队列的十倍（100000）。同样当服务端缓冲区的序列少于队列的80%时，服务端会启动饥饿模式填充缓冲区队列中的序列。



### 数据结构
应用名称  	| 序列名称		| 上次使用	| 下次使用 | 最大序列 |
-------- | ------------|-------- |-------- |-------- |
App-01  | Seq-01| 1		| 10001   | 99999999999999999 |
App-01  | Seq-02| 2000		| 30001   | 99999999999999999 |
App-02  | Seq-01| 1		| 10001   | 99999999999999999 |
App-02  | Seq-02| 1001		| 20001   | 99999999999999999 |



### 界面
#### 管理界面
![Reset 界面](https://raw.githubusercontent.com/chinazhen/images/master/jiemian.png)

* 创建序列
* 删除序列
* 获取序列
* 查询序列

#### 创建序列

![Reset 界面](https://raw.githubusercontent.com/chinazhen/images/master/create.png)


* 可直接在业务系统中使用获取序列方式创建序列（后面介绍）。
* 也可提前在服务端界面创建序列。


#### 删除序列


![Reset 界面](https://raw.githubusercontent.com/chinazhen/images/master/delete.png)

* 删除已创建序列（不可恢复）

#### 获取序列
![Reset 界面](https://raw.githubusercontent.com/chinazhen/images/master/get.png)

* 获取指定序列（批次方式）


#### 获取序列
![Reset 界面](https://raw.githubusercontent.com/chinazhen/images/master/query.png)

* 查询/获取某个业务系统中的所有/指定序列信息


### 项目部署

* 下载源码 git clone [https://github.com/chinazhen/dsequence.git](https://github.com/chinazhen/dsequence.git) 
* 创建数据库及表修改数据库配置。数据库配置在dsequence/dsequence-server/src/main/resources/properties/（各个环境 maven profile选择） ，MySQL数据库建表脚本在 dsequence/dsequence-server/src/main/resources/scripts/database.sql，
* 编译源码,进入项目目录 mvn clean package
* 运行项目 进入 desequence-server/target目录，执行 java -jar dsequence.jar 或 找到源码 com.github.dsequence.server.Application 运行main方法。
* 打开浏览器访问 [http://127.0.0.1:9099/dsequence/swagger/index.html](http://127.0.0.1:9099/dsequence/swagger/index.html)

PS : 项目jdk 版本为1.8，项目使用lombok插件，idea源码编译时请 [安装lombok插件](http://blog.csdn.net/zw235345721/article/details/50737549)。


### Java Client Example

* Maven pom

		<dependency>
		    <groupId>com.github.dsequence</groupId>
		    <artifactId>dsequence-client</artifactId>
		    <version>1.1-SNAPSHOT</version>
		</dependency>


* Java code

		package com.baofu.account.manager.sequence;
		
		import ...
		
		import com.github.dsequence.client.SequenceFactory;
		import lombok.extern.slf4j.Slf4j;
		import java.util.Objects;
		
		/**
		* 收支明细ID序列号生成
		*
		* @author talentshu@163.com
		* @version 1.0.0 createTime: 17/4/23
		*/
		@Slf4j
		public final class BalDetailIDProducer {
		
			private final static String appName = "ACCOUNT";
			
			private final static String seqName = "BAL_DETAIL_ID";
			
			public static Long getSequence() {
			    long startTime = System.currentTimeMillis();
			    Long sequence = SequenceFactory.getInstance(
			            Configuration.getString("sequence.server")).getSequence(appName,seqName);
			    startTime = System.currentTimeMillis() - startTime;
			    if (startTime >= 200) {
			        log.warn("收支明细ID序列号生成, sequence:{}, 耗时:{}", sequence, startTime);
			    }
			    if (Objects.isNull(sequence)) {
			        throw new AccountException(BizErrorCode.GET_SEQUENCE_ERROR,"获取余额明细ID为空");
			    }
			    return sequence;
			}
	
		}
		
### 讨论及建议

* 邮箱：talentshu@163.com

### 支持与鼓励

![微信](https://raw.githubusercontent.com/chinazhen/images/master/IMG_0217.JPG)