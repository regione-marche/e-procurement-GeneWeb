<%
/*
 * Created on 16-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE LA VISUALIZZAZIONE DELL'ELENCO PARAMETRI DEFINITI PER 
 // UNA RICERCA CON PROSPETTO: la pagina e' un adattamento della pagina
 // \WEB-INF\pages\gene\genmod\listaParametriModello.jsp per i parametri
 // associati ad una ricerca con prospetto
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
								
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
								
<form name="listaParametri" action="">
<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<display:table name="listaParametriModello" id="modelloForm" class="datilista">
					<display:column title="Opzioni" style="width:50px">
						<elda:linkPopupRecord idRecord="${modelloForm.progressivo}" contextPath="${contextPath}" />
						<input type="checkbox" name="progressivo" value="${modelloForm.progressivo}" />
					</display:column>

				<display:column property="codice" title="Codice" headerClass="sortable">  </display:column>
				<display:column property="obbligatorio" title="Obbligatorio" decorator="it.eldasoft.gene.commons.web.displaytag.BooleanDecorator"> </display:column> 
				<display:column property="nome" title="Descrizione per inserimento" headerClass="sortable">  </display:column>
				<display:column property="descrizione" title="Descrizione" headerClass="sortable"> </display:column>
				<display:column property="descrizioneTipo" title="Tipo" headerClass="sortable">  </display:column>
				<display:column property="menu" title="Voci menu" headerClass="sortable"> </display:column>
			</display:table>
		</td>
	</tr>	
	<tr>
    <td class="comandi-dettaglio" colSpan="2">
      <INPUT type="button" class="bottone-azione" value="Aggiungi parametro" title="Aggiungi parametro al modello" onclick="javascript:creaParametro(${idModello})">
			<INPUT type="button" class="bottone-azione" value="Esegui report" title="Esegui estrazione report" onclick="javascript:eseguiRicerca();" >
      &nbsp;
    </td>
  </tr>
</table>
</form>