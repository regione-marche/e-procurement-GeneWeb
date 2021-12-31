<%
/*
 * Created on 21-ago-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL 
 // FORM PER L'UPLOAD DEL FILE DI IMPORT
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/UploadDefinizioneRicerca"  method="post" enctype="multipart/form-data" >
  <html:hidden property="pageFrom" value="${pageFrom}"/>
	<table class="dettaglio-notab">
		<tr>
			<td colspan="2">
				<b>Selezione file</b>
				<span class="info-wizard">
		   		Selezionare il file XML contenente la definizione del report da importare.<br>
		   		Premere "Avanti &gt;" per proseguire nell'importazione guidata, "Annulla" per annullare l'operazione e tornare nella pagina iniziale dell'applicativo (homepage).
	   		</span>
			</td>
		</tr>
	  <tr>
      <td class="etichetta-dato">File (*)</td>
      <td class="valore-dato">
      	<html:file property="selezioneFile"  styleClass="file" size="50" onkeydown="return bloccaCaratteriDaTastiera(event);"/>
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;
      	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;
      	<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
      </td>
    </tr>
	</table>
</html:form>