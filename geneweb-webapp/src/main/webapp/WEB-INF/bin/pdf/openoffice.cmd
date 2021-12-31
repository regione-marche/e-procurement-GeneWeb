@ECHO OFF

"C:\Programmi\OpenOffice.org 3\program\soffice" -writer -invisible "macro:///Eldasoft.Export.ConvertToPDF(%1)" 
exit %errorlevel%