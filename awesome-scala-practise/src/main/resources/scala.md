## 启动第一个项目

1. 下载scala、并配置path，所有国内镜像会快一点

   ```
   http://downloads.typesafe.com/scala/2.12.12/scala-2.12.12.msi
   ```

   

2. idea下载插件

3. idea配置

   * Project structure》Project setting》libraries
   * Project structure》Platform settings》SDKS
   * Project structure》Platform settings》Global Libraries

4. pom添加依赖

   ```xml
   <dependency>
       <groupId>org.scala-lang</groupId>
       <artifactId>scala-library</artifactId>
       <version>${scala.detail.version}</version>
   </dependency>
   ```

   

5. maven 添加插件依赖

   ```xml
    <!--声明主类位置-->
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-jar-plugin</artifactId>
       <configuration>
           <archive>
               <manifest>
                   <addClasspath>true</addClasspath>
                   <classpathPrefix>lib/</classpathPrefix><!--声明主类使用类库的相对目录名称-->
                   <mainClass>com.froggengo.springcloud.HelloWorld</mainClass>
               </manifest>
           </archive>
       </configuration>
   </plugin>
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-compiler-plugin</artifactId>
       <version>2.0.2</version>
       <executions>
           <execution>
               <phase>compile</phase>
               <goals>
                   <goal>compile</goal>
               </goals>
           </execution>
       </executions>
   </plugin>
    <!--scala编译插件-->
   <plugin>
       <groupId>org.scala-tools</groupId>
       <artifactId>maven-scala-plugin</artifactId>
       <version>2.15.2</version>
       <executions>
           <execution>
               <id>scala-compile-first</id>
               <goals>
                   <goal>compile</goal>
               </goals>
               <configuration>
                   <includes>
                       <include>**/*.scala</include>
                   </includes>
               </configuration>
           </execution>
           <execution>
               <id>scala-test-compile</id>
               <goals>
                   <goal>testCompile</goal>
               </goals>
           </execution>
       </executions>
   </plugin>
   <!--依赖类库复制插件：-->
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-dependency-plugin</artifactId>
       <executions>
           <execution>
               <id>copy-dependencies</id>
               <phase>prepare-package</phase>
               <goals>
                   <goal>copy-dependencies</goal>
               </goals>
               <configuration>
                   <!--${project.build.directory}是项目构建完成后的根目录，可执行的Jar在此目录-->
                   <outputDirectory>${project.build.directory}/lib</outputDirectory>
                   <overWriteReleases>false</overWriteReleases>
                   <overWriteSnapshots>false</overWriteSnapshots>
                   <overWriteIfNewer>true</overWriteIfNewer>
               </configuration>
           </execution>
       </executions>
   </plugin>
   <!--添加拷贝依赖到可执行Jar的插件：-->
   <plugin>
       <artifactId>maven-assembly-plugin</artifactId>
       <configuration>
           <appendAssemblyId>false</appendAssemblyId>
           <descriptorRefs>
               <descriptorRef>jar-with-dependencies</descriptorRef>
           </descriptorRefs>
           <archive>
               <manifest>
                    <!--声明主类位置-->
                   <mainClass>com.froggengo.springcloud.HelloWorld</mainClass>
               </manifest>
           </archive>
       </configuration>
       <executions>
           <execution>
               <id>make-assembly</id>
               <phase>package</phase>
               <goals>
                   <goal>single</goal>
               </goals>
           </execution>
       </executions>
   </plugin>
   ```

   

6. 打包命令

   ```shell
   clean package -T 1C -Dmaven.test.skip=true -Dmaven.compile.fork=true
   ```

   

7. 报错：错误: 找不到或无法加载主类

   * 可能scala对应的jdk版本不一致，重新下载jdk后，重新编译打包，运行成功

8. 报错：

   ```
   Exception in thread "main" java.lang.NoClassDefFoundError: scala/Predef$
   ```

   原因是：pom里面为添加scala依赖，导致打包是没有吧scala包打进去