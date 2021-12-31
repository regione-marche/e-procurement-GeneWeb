' Convert2PDF.vbs script
' Part of PDFCreator
' License: GPL
' Homepage: http://www.pdfforge.org/products/pdfcreator
' Windows Scripting Host version: 5.1
' Version: 1.1.0.0
' Date: December, 24. 2007
' Author: Frank Heindörfer
' Comments: This script convert a printable file in a pdf-file using 
'           the com interface of PDFCreator.

' ULTERIORI MODIFICHE APPORTATE DA ELDASOFT S.p.A. PER:
' - GESTIRE L'OUTPUT SU STANDARD OUTPUT ANZICHE' MESSAGEBOX
' - CREARE UN OGGETTO PDFCreator SOLO SE NON E' GIA' STATO CREATO, ATTENDENDO 
' EVENTUALMENTE UN TEMPO MASSIMO PER EFFETTUARE LA CREAZIONE
' - TERMINARE CON UN EXIT STATUS (0=OK, 1=ERRORI)
' - MIGLIORARE LA MESSAGGISTICA E FORNIRLA IN ITALIANO

Option Explicit

Const maxTime = 30    ' in seconds
Const sleepTime = 350 ' in milliseconds

Dim objArgs, ifname, fso, PDFCreator, DefaultPrinter, ReadyState, _
 i, c, AppTitle, Scriptname, ScriptBasename

Dim ms, status, colProcess, strComputer, strProcessToKill, count, objWMIService
status = 0
strComputer = "."
strProcessToKill = "PDFCreator.exe"


Set fso = CreateObject("Scripting.FileSystemObject")

Scriptname = fso.GetFileName(Wscript.ScriptFullname)
ScriptBasename = fso.GetFileName(Wscript.ScriptFullname)

AppTitle = "PDFCreator - " & ScriptBaseName

If CDbl(Replace(WScript.Version,".",",")) < 5.1 then
 Wscript.Echo "Richiesto ""Windows Scripting Host"" versione 5.1 o superiore"
 Wscript.Quit 1
End if

Set objArgs = WScript.Arguments

If objArgs.Count = 0 Then
 Wscript.Echo "Sintassi: " & Scriptname & " <Filename>"
 WScript.Quit 1
End If

c = 0
ms = 0
Do
 WScript.Sleep ms
 c = c + ms
 Set objWMIService = GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\cimv2")
 Set colProcess = objWMIService.ExecQuery ("Select * from Win32_Process Where Name = '" & strProcessToKill & "'")
 count = 0
 For Each colProcess in colProcess
  count = count + 1
 Next
 Randomize
 ms = int( sleepTime * 3 * Rnd)
Loop while (count > 0) and (c < (maxTime * 1000))
If (count > 0) then
 Wscript.Echo "PDFCreator in esecuzione per altri task, riprovare in un secondo momento"
 Wscript.Quit 1
End If

Set PDFCreator = Wscript.CreateObject("PDFCreator.clsPDFCreator", "PDFCreator_")
PDFCreator.cStart "/NoProcessingAtStartup"
With PDFCreator
 .cOption("UseAutosave") = 1
 .cOption("UseAutosaveDirectory") = 1
 .cOption("AutosaveFormat") = 0                              ' 0 = PDF
 DefaultPrinter = .cDefaultprinter
 .cDefaultprinter = "PDFCreator"
 .cClearcache
 .cPrinterStop = false
End With

For i = 0 to objArgs.Count - 1
 With PDFCreator
  ifname = objArgs(i)
  If Not fso.FileExists(ifname) Then
   Wscript.Echo "Impossibile trovare il file " & ifname
   status = 1
   Exit For
  End If
  if Not .cIsPrintable(CStr(ifname)) Then
   Wscript.Echo "Impossibile generare il PDF a causa di problemi rilevati durante la conversione del file " & ifname
   status = 1
   Exit For
  End if

  ReadyState = 0
  .cOption("AutosaveDirectory") = fso.GetParentFolderName(ifname)
  .cOption("AutosaveFilename") = fso.GetBaseName(ifname)
  .cPrintfile cStr(ifname)

  c = 0
  Do While (ReadyState = 0) and (c < (maxTime * 1000 / sleepTime))
   c = c + 1
   Wscript.Sleep sleepTime
  Loop
  If ReadyState = 0 then
   Wscript.Echo "Tempo scaduto per la conversione in PDF del file " & ifname
   status = 1
   Exit For
  End If
 End With
Next

With PDFCreator
 .cDefaultprinter = DefaultPrinter
 .cClearcache
 WScript.Sleep 200
 .cClose
End With

WScript.Quit status

'--- PDFCreator events ---

Public Sub PDFCreator_eReady()
 ReadyState = 1
End Sub

Public Sub PDFCreator_eError()
 Wscript.Echo "[" & PDFCreator.cErrorDetail("Number") & "]: " & PDFcreator.cErrorDetail("Description")
 status = 1
 Wscript.Quit status
End Sub
