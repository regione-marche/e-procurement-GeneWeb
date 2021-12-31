<%
/*
 * Created on 21-set-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // AREA SOPRA I PULSANTI DELLA PAGINA DI TROVA E CONTENENTE ALCUNE OPZIONI SPECIALI
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

    <tr>
   		<td class="etichetta-dato">Opzioni</td>
		<td class="flag-sensitive" colspan="2">
    		<div class="opzioniTrova">
    		<html:select property="risPerPagina">
	      		<html:options name="listaRisPerPagina" labelName="listaRisPerPagina" />
			</html:select>&nbsp;Risultati per pagina
			</div>
			<c:choose>
				<c:when test='${applicationScope.attivaCaseSensitive}'>
				<div class="opzioniTrova">
		    	  <html:checkbox property="noCaseSensitive" value="true" />&nbsp;Ignora maiuscole/minuscole
				</div>
				</c:when>
				<c:otherwise>
					<html:hidden property="noCaseSensitive" />
				</c:otherwise>
			</c:choose>
			<div class="opzioniTrova">
			<html:checkbox property="visualizzazioneAvanzata" onclick="gestisciVisualizzazioneAvanzata()" styleId="visualizzazioneAvanzata"/>&nbsp;Opzioni avanzate
			</div>
		</td>
	</tr>
