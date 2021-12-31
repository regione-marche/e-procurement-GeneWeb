<%
/*
 * Created on 21-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA 
 // DEI MODELLI ASSOCIATI AL GRUPPO IN ANALISI 
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
								
								
<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<display:table name="listaGruppiModello" defaultsort="1" id="modelloForm" class="datilista" sort="list" requestURI="GruppiModello.do">
				<display:column property="nomeGruppo" title="Nome" sortable="true" headerClass="sortable">  </display:column>
				<display:column property="descrGruppo" title="Descrizione" sortable="true" headerClass="sortable"> </display:column>
			</display:table>
		</td>
	</tr>	
	<tr>
    <td class="comandi-dettaglio" colSpan="2">
    	<!--<INPUT type="button" class="bottone-azione" value="Crea" title="Crea nuovo gruppo" onclick="javascript:creaNuovoModello();">-->
      <INPUT type="button" class="bottone-azione" value="Modifica associazioni" title="Modifica associazioni dei gruppi al modello" onclick="javascript:modificaAssModelliGruppo(${idModello})">
        &nbsp;
    </td>
  </tr>
</table>