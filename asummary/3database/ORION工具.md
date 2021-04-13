## 一. ORION 概述

### 1.1 ORION 说明

​	ORION (Oracle I/O Calibration Tool) 是校准用于 Oracle 数据库的存储系统 I/O 性能的独立工具。校准结果对于了解存储系统的性能有很大帮助，不仅可以找出影响 Oracle 数据库性能的问题，还能测量新数据库安装的大小。由于 ORION 是一个独立工具，用户不需要创建和运行 Oracle 数据库。



Orion的下载地址为:
http://www.oracle.com/technology/global/cn/software/tech/orion/index.html

 

​	为了尽可能地模拟 Oracle 数据库，ORION 使用和 Oracle 相同的 I/O 软件集生成一个合成的 I/O 负载。可以将 ORION 配置为生成很多 I/O 负载，包括一个模拟 OLTP 和数据仓库负载的 I/O 负载。



​	ORION 支持多种平台。将来还会增加对更多平台的支持。用户指南提供详细的使用文档，包括“入门”部分和疑难解答提示。在 ORION 中调用“-help”选项可以直接获取关于如何使用 ORION 的摘要。

### 优势：

​	1. 不需要运行load runner以及配置大量的clinet

2. 不需要运行oracle数据库，以及准备大量的测试数据

	3.  测试结果更具有代表性，如随机IO测试中，该软件可以让存储的命中率接近为0，而更仿真出了磁盘的真实的IOPS，而load runner很难做到这些，最终的磁盘IOPS需要换算得到。
 	4.  可以根据需要定制一定比例的写操作（默认没有写操作），但是需要注意，如果磁盘上有数据，需要小心数据被覆盖掉。

### 	缺点

​	1. 到现在为止，无法指定自定义的总体的运行时间以及加压的幅度，这里完全是自动的.

​	2. 无法进行一些自定义的操作类型，如表的扫描操作，装载测试等等，不过可以与oracle数据库结合起来达到这个效果.

### 1.2 ORION 帮助

C:\Program Files(x86)\Oracle\Orion>orion -help

> ORION: ORacle IO Numbers -- Version10.2.0.1.0
>
> ORION runs IO performance tests that modelOracle RDBMS IO workloads.
>
> It measures the performance of small(2-32K) IOs and large (128K+) IOs at various load levels. Each Orion data point is done at a specific mixof small and large IO loads sustained for a duration. Anywhere from a single data point to atwo-dimensional array of data points can be tested by setting the rightoptions.
>
>  
>
> An Orion test consists of data points atvarious small and large IO load levels. These points can be represented as a two-dimensional matrix: Each columnin the matrix represents a fixed small IO load. Each row represents a fixedlarge IO load. The first row is with no largeIO load and the first column is with no small IO load. An Orion test can be a single point, a row, acolumn or the whole matrix.
>
>  
>
> Parameters 'run','testname', and 'num_disks' are mandatory. Defaults are indicated for all otherparameters.  For additional information on the user interface, see the Orion User Guide.
>
> **--运行ORAION必须包含run,testname,num_disks 三个参数。**
>
>  
>
> <testname>.lun should contain acarriage-return-separated list of LUNs The output files for a test run areprefixed by <testname>_.
>
>  
>
> The output files are:
>
> <testname>_summary.txt - Summary ofthe input parameters along with min. small latency, max large MBPS and/or max.small IOPS.
>
> <testname>_mbps.csv - Performanceresults of large IOs in MBPS**(吞吐量)**
>
> <testname>_iops.csv - Performanceresults of small IOs in IOPS**（每秒IO次数）**
>
> <testname>_lat.csv - Latency of smallIOs**（响应时间）**
>
> <testname>_tradeoff.csv - Shows largeMBPS / small IOPS combinations that can be achieved at certain small latencies
>
> <testname>_trace.txt - Extended,unprocessed output(跟踪信息，最详细的数据)
>
> --ORION 执行结束会输出5个文件，每个文件的不同作用参考上面的说明。
>
>  
>
> WARNING: IF YOU ARE PERFORMING WRITE TESTS,BE PREPARED TO LOSE ANY DATA STORED ON THE LUNS.
>
>  
>
> Mandatory parameters(强制的参数说明):
>
> run           Type of workload to run(simple, normal, advanced)
>
> ​           		simple - tests random 8K small IOs at various loads,then random 1M largeIOs at various loads.
>
> ​             normal - tests combinations of random8K small IOs and random 1M large IOs
>
> ​             advanced - run theworkload specified by the user using optional parameters
>
> testname        Name of the test run
>
> num_disks        Number of disks (physicalspindles)
>
>  
>
> Optional parameters（可选的参数说明，注意默认值）:
>
> size_small       Size of small IOs (in KB) -default 8
>
> size_large       Size of large IOs (in KB) -default 1024
>
> type           Type of large IOs (rand,seq) - default rand
>
> ​             	rand - Random largeIOs
>
> ​              seq - Sequential streams of large IOs
>
> num_streamIO      Number of concurrent IOs per stream(only if type is seq) - default 4
>
> simulate        Orion tests on a virtual volumeformed by combining the provided volumes in one of these ways (default concat):
>
> ​             concat - A serialconcatenation of the volumes
>
> ​             raid0 - A RAID-0mapping across the volumes
>
> write         Percentage of writes (SEEWARNING ABOVE) - default 0
>
> cache_size       Size *IN MEGABYTES* of thearray's cache.
>
> ​             Unless this option is set to 0, Orion does anumber of (unmeasured) random IO before each large sequential data point. This is done in order to fill up the array cachewith random data. This way, the blocksfrom one data point do not result in cache hits for the next data point. Read tests are preceded with junk reads andwrite tests are preceded with junk writes. If specified, this 'cache warming' is done until cache_size worth of IOhas been read or written.
>
> ​            Default behavior: fillup cache for 2 minutes before each data point.
>
> duration        Duration of each data point (inseconds) - default 60
>
> num_small        Number of outstanding small IOs(only if matrix is point, col, or max) - no default
>
> num_large        For random, number ofoutstanding large IOs.
>
> ​            For sequential, numberof streams (only if matrix is point, row, or max) - nodefault
>
> matrix         An Orion test consists ofdata points at various small and large IO load levels. These points can be represented as atwo-dimensional matrix: Each column in the matrix represents a fixed small IOload. Each row represents a fixed largeIO load. The first row is with no largeIO load and the first column is with no small IO load. An Orion test can be a single point, a row, acolumn or the whole matrix, depending on the matrix option setting below (default basic):
>
> ​             basic - test thefirst row and the first column
>
> ​             detailed - test theentire matrix
>
> ​             point - test at loadlevel num_small, num_large
>
> ​             col - varying largeIO load with num_small small IOs
>
> ​             row - varying smallIO load with num_large large IOs
>
> ​             max - test varyingloads up to num_small, num_large
>
> verbose         Prints tracing information tostandard output if set.
>
> ​            Default -- not set
>
>  
>
> ### Examples
>
> For a preliminary set of data
>
> ​    -run simple -testname <name> -num_disks <#>
>
> For a basic set of data
>
> ​    -run normal -testname <name> -num_disks <#>
>
> To generate combinations of 32KB and 1MBreads to random locations:
>
> ​    -run advanced -testname <name> -num_disks <#>
>
> ​    -size_small 32 -size_large 1024 -type rand   -matrix detailed
>
> To generate multiple sequential 1MB writestreams, simulating 1MB RAID0 stripes
>
> ​    -run advanced -testname <name> -num_disks <#>
>
> ​    -simulate RAID0 -stripe 1024 -write 100 -type seq
>
> ​    -matrix col -num_small 0
>
>  
>
> C:\Program Files (x86)\Oracle\Orion>

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210322094329.png)

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210322094330.png)

> num_streamIO: 增加这个参数来仿真并行执行操作。指定计划的并行度。一个好的开始点是CPU数目*一个CPU的线程数。
>
>  -num_small
>  指定测试small IO时，进程个数
>
>  -num_large
>  指定测试large IO时，进程个数
>
>  matrix: 使用point来仿真单个连续工作量，或者使用col来仿真不断增加的大的连续的工作量。
>  使用非simple/normal模式时，希望得到的测试结果形式
>   basic:等同于run -simple的输出，只是可以更多定制
>   detailed:等同于run -normal的输出，只是可以更多定制
>   point:只测给定num_small，num_large这个点的性能
>   col:只测给定num_small值的那一列的large IO
>   row:只测给定num_large值的那一行的small IO
>   max:以num_small，num_large做为横竖坐标的最大值，测试n*m矩阵内所有组合的值

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210322094331.png)

### 1.3使用

```shell
#在oracle安装目录可以找到orion.exe
##D:\app1\mh\product\11.2.0\dbhome_1\BIN\orion.exe
##在任意位置的文件夹创建   filename.lun  文件
##文件内容为
\\.\E:
##如果想指定盘下某个文件夹，比如为\\.\E:\1orion，则会报错
###执行命令，其中oracleio为 lun文件的文件名及绝对路径
orion.exe -run simple -testname D:\app1\oracleio -num_disks 1
```

**执行结果**

![image-20200519120211803](https://gitee.com/froggengo/cloudimage/raw/master/img/20210322094332.png)

![image-20200519120253553](https://gitee.com/froggengo/cloudimage/raw/master/img/20210322094333.png)

```properties
Maximum Large MBPS=32.78 @ Small=0 and Large=2
Maximum Small IOPS=84 @ Small=5 and Large=0
Minimum Small Latency=20349.84 usecs @ Small=1 and Large=0
##吞吐量为32.78M 
##IOPS每秒读写为84
```

其他类型

```
4.1、数据库OLTP类型，假定IO类型全部是8K随机操作，压力类型，自动加压，从小到大，一直到存储压力极限

#./orion -run advanced -testname dave-num_disks 1 -size_small 8 -size_large 8 -type rand &

4.2、数据库吞吐量测试，假定IO全部是1M的序列性IO

#./orion -run advanced -testname mytest-num_disks 96 -size_small 1024 -size_large 1024 -type seq &

4.3、指定特定的IO类型与IO压力，如指定小IO为压力500的时候，不同大IO下的压力情况

#./orion -run advanced -testname mytest-num_disks 96 -size_small 8 -size_large 128 -matrix col -num_small 500 -typerand &

4.4、结合不同的IO类型，测试压力矩阵

#./orion -run advanced -testname mytest-num_disks 96 -size_small 8 -size_large 128 -matrix detailed -type rand &

原文链接：https://blog.csdn.net/tianlesoftware/article/details/5965331
```

