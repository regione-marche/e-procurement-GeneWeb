<%
/*
 * Created on 03-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA 
 // ARGOMENTI CONTENENTE LA EFFETTIVA LISTA DEI FILTRI DI UNA RICERCA BASE PER IL
 // WIZARD
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="testata" value="${sessionScope.recordDettRicerca.testata}" scope="request" />
<c:set var="elencoFiltri" value="${sessionScope.recordDettRicerca.elencoFiltri}" scope="request" />
<table class="lista">
	<tr>
		<td>
			<b>Riepilogo filtri</b>
			<span class="info-wizard">
				Il presente elenco è costituito dalle condizioni di filtro da applicare all'estrazione del risultato.<br>
				Selezionare il nome di un campo per modificarne la condizione di filtro, selezionare <img src='${pageContext.request.contextPath}/img/trash.gif' height='15' width='15' alt='Elimina'> per eliminare la condizione stessa.<br>
				Premere "&lt; Indietro" per tornare all'aggiunta filtri, "Avanti &gt;" per proseguire la creazione guidata o ripartire con l'inserimento filtri da una situazione pulita.<br>
				Premere "Fine" per passare al salvataggio del report previo inserimento del nome e tipo di report,  "Annulla" per annullare la creazione guidata.
			</span>
		</td>
	</tr>
	<tr>
		<td>
			<display:table name="${elencoFiltri}" class="datilista" id="filtro">
				<display:column title="Campo" href="FiltriBase.do?metodo=modifica&" paramId="id" paramProperty="progressivo">${fn:substringAfter(filtro.mnemonicoCampo, "_")}</display:column>
				<display:column property="descrizioneOperatore" title="Operatore">  </display:column>
				<display:column property="valoreConfronto" title="Valore confronto" decorator="it.eldasoft.gene.commons.web.displaytag.SpaziHTMLDecorator">  </display:column>
				<display:column style="width:18px">
					<a href='javascript:eliminaFiltro(${filtro.progressivo});' Title='Elimina'> <img src='${pageContext.request.contextPath}/img/trash.gif' height='15' width='15' alt='Elimina'></a>
				</display:column>
			</display:table>
		</td>
	</tr>
	<tr>
		<td>Confermi l'inserimento delle condizioni di filtro indicate?
	  		<br>
	  		<input type="radio" id="SiConfermaListaFiltri" name="confermaListaFiltri" checked="checked">&nbsp;Si
	  		<br>
	  		<input type="radio" id="NoConfermaListaFiltri" name="confermaListaFiltri">&nbsp;No, annullo l'inserimento delle condizioni di filtro finora eseguite
		</td>
	</tr>
	<tr>
    <td class="comandi-dettaglio">
      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
    	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;&nbsp;&nbsp;&nbsp;
    	<INPUT type="button" class="bottone-azione" value="Fine" title="Fine" onclick="javascript:fineWizard();">&nbsp;
    </td>
  </tr>
</table>