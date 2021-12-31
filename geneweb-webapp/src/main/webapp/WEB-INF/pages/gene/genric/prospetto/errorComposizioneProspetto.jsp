<%/*
   * Created on 21-mar-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA DI ERRORE DURANTE L'ELABORAZIONE DEL MODELLO IN CORSO PER LA
  // GENERAZIONE DEL PROSPETTO ASSOCIATO AD UNA RICERCA CON PROSPETTO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<table class="lista">
	<tr>
		<td align="center">
		<br>
			Operazione interrotta
		</td>
	</tr>
	<tr>
   	<td class="comandi-dettaglio">
			<INPUT type="button" class="bottone-azione"	value="Indietro" title="Indietro" onclick="javascript:historyVaiIndietroDi(0);">
		&nbsp;
		</td>
 	</tr>

</table>