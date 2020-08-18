  在使用SQL*LOADER装载数据时，由于平面文件的多样化和数据格式问题总会遇到形形色色的一些小问题，下面是工作中累积、整理记录的遇到的一些形形色色错误。希望能对大家有些用处。（今天突然看到自己以前整理的这些资料，于是稍稍整理、归纳成这篇博客，后面如果碰到其他案例，会陆陆续续补充在此篇文章。）

 



**ERROR 1**：SQL*LOADER装载数据成功，但是发现某些字段的中文为乱码，这个是因为编码缘故造成乱码。可在控制文件中添加字符集编码参数解决问题，

例如：CHARACTERSET 'ZHS16GBK' 或 CHARACTERSET 'UTF8'，根据数据库实际情况设置数据库字符集。

```
LOAD DATA
CHARACTERSET 'ZHS16GBK' 
INFILE '/oradata/impdata/test.txt' 
APPEND INTO TABLE ETL.TEST
FIELDS TERMINATED BY '@#$' TRAILING NULLCOLS
(
MON_CD          ,  
CITY_ID         ,
CELL_ID         ,
GPRS_USER_CNT   ,
TERM_BRAND      ,
BRAND_ID        ,
FLUX            ,
CELL_NAM       
)
```

 

**ERROR 2****：**装载数据时，报ORA-01722: invalid number错误（不是数据类型错误造成的一般错误。而是最后一个字段为NUMBER类型时，会报上述错误）因为换行符的问题，如果NUMBER类型的列位于表的最后，最后其实会有换行符（如果为\n，不会出错， 如果为\r\n，则会报错），在用SQLLDR导入时会把换行符也算作那个数字的一部分，从而出错。解决办法加INTEGER或者加“TERMINATED BY WHITESPACE”。

Record 1: Rejected - Error on table DM.TM_WGGHF_CELL_USER_DAY, column TYPE_ID.

ORA-01722: invalid number

注意：如果数据字段类型是NUMBER类型，则用INTEGER会导致装载的数据异常，99.875000 会变成一个很大的整数。

 

**ERROR 3****：**装载数据时，由于里面有日期字段，需要添加日期函数处理特定格式的数据类型。否则会出现错乱格式





```
LOAD APPEND INTO TABLE ODS.TO_ALARM_LOG
FIELDS TERMINATED BY  '@#$' TRAILING NULLCOLS
(
COLLECT_DT                        ,
DATE_CD                           ,
HR_CD                             ,
DISPH_LOST_REASON                 ,
COLLET_TYPE_ID                    ,
ALM_TM                "TO_DATE(:ALM_TM,'DD-MM-YYYY HH24:MI:SS', 'NLS_DATE_LANGUAGE=American')"             ,
ALM_DISCOVER_TM       "TO_DATE(:ALM_DISCOVER_TM, 'DD-MM-YYYY HH24:MI:SS', 'NLS_DATE_LANGUAGE=American')"   ,
ALARM_DELSTA_ID                   ,
ALM_RESUME_TM         "TO_DATE(:ALM_RESUME_TM, 'DD-MM-YYYY HH24:MI:SS', 'NLS_DATE_LANGUAGE=American')"     ,
FP_ALM_SER                        ,
FP3_ALM_FINGMARK                  ,
AREA_NAM                          ,
VSEQUIP_NAM                       ,
VSEQUIP_STATUS_ID                 ,
VSEQUIP_SUBSTATUS_ID              ,
PLAN_DISPH_TM         "TO_DATE(:PLAN_DISPH_TM, 'DD-MM-YYYY HH24:MI:SS', 'NLS_DATE_LANGUAGE=American')"      ,
AUTO_PRETREAT_STATUS_ID           ,
EMOS_RECORD_CNT                   ,
CONT_TIME                         ,
ALM_CNT                
)
```

 

**ERROR 4** 如果数据文件里面缺少某些字段，可以在控制文件中添加常量参数，例如下面缺少COLLECT_DT这个字段的数据（其实是根据数据文件以及某些参数生成的），可以通过CONSTANT常量参数解决。



```
LOAD DATA
CHARACTERSET 'UTF8'
INFILE 'DEVICE_WIRELESS_GSMCELL_F_20120130190002.CSV' "str '\r\n'"
APPEND INTO TABLE STAGE.TS_RSRC_IRMS_GSMCELL
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '\'' TRAILING NULLCOLS
(
        COLLECT_DT  CONSTANT     '20120214',             
        CELL_ID                         ,
        CELL_NAM                        ,
        FULL_NAM                        ,
        OTHER_NAM                       ,
        OMC_CELL_NAM                    ,
        GROUP_NAM                       ,
        GROUP_NAM_EN                    
)
```

 

**ERROR 5** 如果换行是WINDOW平台的格式（即\r\n, LINUX平台是以\n)，如下图所示(用vi编辑器查看)

```
LOAD DATA
INFILE 'EDS.TW_BUSS_GN_CELLFLUX_HR4.csv'  "str '\r\n'"
APPEND INTO TABLE EDS.TW_BUSS_GN_CELLFLUX_HR_TEST                 
FIELDS TERMINATED BY  ','  TRAILING NULLCOLS
 (
       DATE_CD          ,
       HR_CD            ,
       LAC_ID           ,
       CELL_ID          ,
       BUSI_TYP1_CD     ,
       BUSI_TYP2_CD     ,
       CITY_ID          ,
       CELL_NAM         ,
       UP_FLUX          ,
       DOWN_FLUX        ,
       VSD_CNT          ,
       CI           
)
```

 

 

**ERROR 6** **数据文件请见附件TEST.csv，数据文件中某个字段的值内部有换行符，加载数据时，想保持数据原样，即数据入库后，数据里面保存有换行。此时可以通过**"str '\r\n'"**解决问题。**

如下所示，数据文件TEST.csv只有两行数据，每一条记录中第二个字段都存在换行。

```
12,"这仅仅

是测试"

14,"数据

有换行"
LOAD DATA
INFILE 'TEST.csv' "str '\r\n'"
APPEND INTO TABLE TEST
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"' TRAILING NULLCOLS
(
ID   ,
NAME 
)

 
```

**ERROR 7 :** **字段长度超过255**

SQLLDR默认输入的字段长度为255个字符。如果某个字段的字符长度超过255，而且你在控制文件里面，没有指定其字符长度，则会报下面错误：SQL Loader- Field in data file exceeds maximum length

**ERROR 8 :** SQL*Loader-510 & SQL*Loader-2026

这个是我遇到的一个特殊例子，SQLLDR装载日志错误如下：

```
SQL*Loader-510: Physical record in data file (/jkfile/DAD_CDR/TEMP/201207/EDS.TW_CUST_COSTCELL_HR_07.dat) is longer than the maximum

(20971520)

SQL*Loader-2026: the load was aborted because SQL Loader cannot continue.
```

**因为这个EDS.TW_CUST_COSTCELL_HR_07.dat文件37G，刚开始搜索了很多资料，也没有解决问题，其实最后发现时数据文件FTP拷贝传送时损坏的缘故。**

 

**ERROR 9****：SQL\*Loader-605 & ORA-01653**

```
ORA-01653: unable to extend table tablename by 128 in tablespace xxxx

SQL*Loader-605: Non-data dependent ORACLE error occurred -- load discontinued.
```

出现这个错误是因为表所在表空间无法扩展，导致SQL*LOADER装载数据无法插入。要给用户对应的表空间扩展空间