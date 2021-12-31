<%
/*
 * Created on 06-set-2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
 // PER LA MODIFICA DI UN ORDINAMENTO APPARTENENTE AD UNA RICERCA
%>

<%/*
   * Created on 03-mag-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // PAGINA CHE CONTIENE IL FORM DELLA PAGINA DI DETTAGLIO PER
 // L'INSERIMENTO MULTIPLO DEI TITOLI CAMPI IN UNA RICERCA BASE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="elencoCampi" value="${sessionScope.recordDettRicerca.elencoCampi}" scope="request" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(sessionScope.profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<html:form action="/SalvaTitoliCampiRicerca">
	<table class="dettaglio-tab-lista">
	  <tr>
	   	<td>
				<display:table name="elencoCampi" class="datilista" id="campo">
					<display:column title="Tabella" >${campo.aliasTabella}</display:column>
					<display:column title="Campo" >${campo.mnemonicoCampo}</display:column>
					<display:column title="Titolo colonna"><input class="testo" type="text" name="id" value="${campo.titoloColonna}" size="60" maxlength="60"></display:column>
				</display:table>   	
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio">
	      <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit()">
	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">
	      &nbsp;
	    </td>
	  </tr>
	</table>
</html:form>