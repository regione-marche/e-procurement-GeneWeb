<%
/*
 * Created on 04-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE LA LISTA DEGLI ORDINAMENTI DI UNA RICERCA BASE PER IL
 // WIZARD
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="testata" value="${sessionScope.recordDettRicerca.testata}" scope="request" />
<c:set var="elencoOrdinamenti" value="${sessionScope.recordDettRicerca.elencoOrdinamenti}" scope="request" />
<table class="lista">
	<tr>
		<td>
			<b>Riepilogo ordinamenti</b>
			<span class="info-wizard">
				Il presente elenco è costituito dai criteri di ordinamento da applicare all'estrazione del risultato.<br>
				Selezionare il nome di un campo per modificarne il criterio di ordinamento, selezionare <img src='${pageContext.request.contextPath}/img/trash.gif' height='15' width='15' alt='Elimina'> per eliminare il criterio stesso.<br>
				Premere "&lt; Indietro" per tornare all'aggiunta ordinamenti, "Avanti &gt;" per proseguire la creazione guidata o ripartire con l'inserimento ordinamenti da una situazione pulita.<br>
				Premere "Fine" per passare al salvataggio del report previo inserimento del nome e tipo di report,  "Annulla" per annullare la creazione guidata.
			</span>
		</td>
	</tr>
	<tr>
		<td>
			<display:table name="${elencoOrdinamenti}" class="datilista" id="ordinamento">
				<display:column title="Campo" href="OrdinamentiBase.do?metodo=modifica&" paramId="id" paramProperty="progressivo">${fn:substringAfter(ordinamento.mnemonicoCampo, "_")}</display:column>
				<display:column property="ordinamento" title="Ordinamento" decorator="it.eldasoft.gene.commons.web.displaytag.OrdinamentoDecorator">  </display:column>
				<display:column style="width:18px">
					<a href='javascript:elimina(${ordinamento.progressivo});' Title='Elimina'> <img src='${pageContext.request.contextPath}/img/trash.gif' height='15' width='15' alt='Elimina'></a>
				</display:column>
			</display:table>
		</td>
	</tr>
	<tr>
		<td>Confermi l'inserimento dei criteri di ordinamento indicati?
	  		<br>
	  		<input type="radio" id="SiConfermaListaOrdinamenti" name="confermaListaOrdinamenti" checked="checked">&nbsp;Si
	  		<br>
	  		<input type="radio" id="NoConfermaListaOrdinamenti" name="confermaListaOrdinamenti">&nbsp;No, annullo l'inserimento dei criteri di ordinamento finora eseguiti
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