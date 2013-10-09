REM create KB from FNA and FoC collections.

SET javacom=C:\Program Files\MyEclipse 6.0\jre\bin\java.exe
REM SET javacom=java
SET FoCdir=C:\Documents and Settings\hongcui\Desktop\iConference\ApplicationPrototype\FOC\descriptions2-standardized-markedup-level2
SET FNAdir=C:\Documents and Settings\hongcui\Desktop\iConference\ApplicationPrototype\FNA\descriptionsWithoutHTML-markedup-level2
SET xml=C:\Documents and Settings\hongcui\Desktop\iConference\crimson-1.1.3\crimson.jar

"%javacom%" -Xms512m -Xmx512m -classpath .\bin;"%xml%" knowledgebase.KnowledgeBase create "%FoCdir%" FoC-KB 0.8 0.001 2 flase
"%javacom%" -Xms512m -Xmx512m -classpath .\bin;"%xml%" knowledgebase.KnowledgeBase create "%FNAdir%" FNA-KB 0.8 0.001 2 false
"%javacom%" -Xms512m -Xmx512m -classpath .\bin;"%xml%" knowledgebase.KnowledgeBase add FoC-KB FNA-KB FoCFNA-KB