@echo off
REM SUPPORTED PARAMS
REM TODO: Make command line arg parsing cleaner
set MEM=64
set FLAVOR=
set FILENAME=

REM SCAN FIRST PARAM
if not "%2"=="" goto %2
goto default
goto end
:memory
set MEM=%3
goto end
:flavor
set FLAVOR=%3
goto end
:filename
set FILENAME=%3
goto end
:default
goto end
:end

REM SCAN SECOND PARAM
if not "%4"=="" goto %4
goto default
goto end
:memory
set MEM=%5
goto end
:flavor
set FLAVOR=%5
goto end
:filename
set FILENAME=%5
goto end
:default
goto end
:end

REM SCAN THIRD PARAM
if not "%6"=="" goto %6
goto default
goto end
:memory
set MEM=%7
goto end
:flavor
set FLAVOR=%7
goto end
:filename
set FILENAME=%7
goto end
:default
goto end
:end

echo on
java -Xmx%MEM%m -Xbootclasspath/p:%XMLBEANS_PERFROOT%\3rdparty\xerces\xerces-2_7_1\xml-apis.jar:%XMLBEANS_PERFROOT%\3rdparty\xerces\xerces-2_7_1\xercesImpl.jar -classpath %XMLBEANS_PERFROOT%\build;%XMLBEANS_PERFROOT%\schema_build\jaxb-purchase-order.jar;%XMLBEANS_PERFROOT%\schema_build\jaxb-primitives.jar;%XMLBEANS_PERFROOT%\schema_build\jaxb-non-primitives.jar;%JAXB_LIBDIR%\jaxb\lib\jaxb-libs.jar;%JAXB_LIBDIR%\jaxb\lib\jaxb-impl.jar;%JAXB_LIBDIR%\jaxb\lib\jaxb-api.jar;%JAXB_LIBDIR%\jwsdp-shared\lib\relaxngDatatype.jar;%JAXB_LIBDIR%\jwsdp-shared\lib\xsdlib.jar -DPERF_ROOT=%XMLBEANS_PERFROOT% org.apache.xmlbeans.test.performance.jaxb.%1 %FLAVOR% %FILENAME% 
