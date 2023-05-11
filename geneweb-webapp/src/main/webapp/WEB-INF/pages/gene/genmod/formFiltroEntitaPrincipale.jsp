<%/*
       * Created on 28-lug-2009
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE 
      // IL FORM PER LA DEFINIZIONE DEL FILTRO SULL'ENTITA PRINCIPALE
    %>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<div class="contenitore-popup">

<table class="ricerca">
	<tbody>
		<tr>
      <td class="valore-dato">
      	<input type="hidden" name="condizioneParte1" id="condizioneParte1" value="${sqlSelect}" />
      	<p>
      		<c:out value="${sqlSelect}" escapeXml="false"/>
      		<br>
      	</p>
				<textarea name="condizioneAggiuntiva" id="condizioneAggiuntiva" rows="4" cols="75"></textarea>
			</td>
		</tr>
		<tr>
			<TD class="comandi-dettaglio" >
			<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">&nbsp; 
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:window.close();">
			&nbsp;</TD>
		</tr>
	</tbody>
</table>
</div>

			<!-- PARTE NECESSARIA PER VISUALIZZARE I POPUP MENU DI OPZIONI PER CAMPO -->
			<IFRAME class="gene" id="iframepopmenu"></iframe>
			<div id="popmenu" class="popupmenuskin"
				onMouseover="highlightMenuPopup(event,'on');"
				onMouseout="highlightMenuPopup(event,'off');"></div>
