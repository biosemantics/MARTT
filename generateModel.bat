REM create model (elementComposite) from FNA and FoC collections.

SET javacom=C:\Program Files\MyEclipse 6.0\jre\bin\java.exe
REM SET javacom=java
SET FoCdir=C:\Documents and Settings\hongcui\Desktop\iConference\ApplicationPrototype\Exp\level2\trainingdata\trainingdir-foc500-merged-bysent-level2-standardized
SET FNAdir=C:\Documents and Settings\hongcui\Desktop\iConference\ApplicationPrototype\Exp\level2\trainingdata\trainingdir-fna630-merged-bysent-level2-stratified

SET xml=C:\Documents and Settings\hongcui\Desktop\iConference\crimson-1.1.3\crimson.jar

"%javacom%" -Xms512m -Xmx512m -classpath .\bin;"%xml%" visitor.ElementComposite "%FoCdir%" FoC-Model SCCP
"%javacom%" -Xms512m -Xmx512m -classpath .\bin;"%xml%" visitor.ElementComposite "%FNAdir%" FNA-Model SCCP
