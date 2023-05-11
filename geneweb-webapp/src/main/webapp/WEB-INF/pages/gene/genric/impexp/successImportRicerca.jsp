<%
/*
 * Created on 15-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI SUCCESSO
 // DELL'IMPORTAZIONE DI UN REPORT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<table class="lista">
	<tr>
		<td>
			<br>
			Vuoi eseguire nuovamente l'importazione di un report?
			<br>
			<input type="radio" name="continua" id="importa"/>&nbsp;Si
			<br>
			<input type="radio" name="continua" id="annullaImporta" checked="checked"/>&nbsp;No
		</td>
	</tr>
  <tr>
    <td class="comandi-dettaglio">
			<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
    </td>
  </tr>
</table>