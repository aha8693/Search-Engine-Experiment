# Search-Engine-Experiment

This is my implementation of hashmap using to different methods of hashing -- open adressing and and chaining -- and profiling with given text files. 

Details: 

• Organize a list of urls from Aa given input (text files) into implemented hashmaps and measure the time and capacity costs for searching the
given list of urls given unique keywords. 

• During the experiment, two different hashmaps are compared with a given, already implemented hashmap -- JdkHashMap -- in terms of their time (ms/op) and 
capacity scores (bytes). The following is the score with JdkHashMap. 

Benchmark                           (fileName)      Mode  Cnt        Score   Error   Units
JmhRuntimeTest.buildSearchEngine    apache.txt      avgt    2        350.423         ms/op
JmhRuntimeTest.buildSearchEngine    jhu.txt         avgt    2        0.433           ms/op
JmhRuntimeTest.buildSearchEngine    joanne.txt      avgt    2        0.190           ms/op
JmhRuntimeTest.buildSearchEngine    newegg.txt      avgt    2        149.994         ms/op
JmhRuntimeTest.buildSearchEngine    random164.txt   avgt    2        1382.812        ms/op
JmhRuntimeTest.buildSearchEngine    urls.txt        avgt    2        0.045           ms/op

+c2k.gc.maximumUsedAfterGc              apache.txt  avgt    2   110840784.000           bytes
+c2k.gc.maximumUsedAfterGc                 jhu.txt  avgt    2    15863820.000           bytes
+c2k.gc.maximumUsedAfterGc              joanne.txt  avgt    2    15634948.000           bytes
+c2k.gc.maximumUsedAfterGc              newegg.txt  avgt    2    76865824.000           bytes
+c2k.gc.maximumUsedAfterGc           random164.txt  avgt    2  1032442952.000           bytes
+c2k.gc.maximumUsedAfterGc                urls.txt  avgt    2    15408460.000           bytes
