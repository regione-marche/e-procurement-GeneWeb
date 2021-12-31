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

<html:form action="/LayoutBase">
	<table class="lista">
	  <tr>
	   	<td>
	   		<b>Layout report</b>
	   		<span class="info-wizard">
	   			Il presente elenco è costituito dai campi estratti; è possibile modificare i titoli corrispondenti nel report.<br>
	   			Premere "&lt; Indietro" per tornare all'inserimento degli ordinamenti, "Avanti &gt;" per proseguire la creazione guidata.<br>
	   			Premere "Fine" per passare al salvataggio del report previo inserimento del nome e tipo di report, "Annulla" per annullare la creazione guidata.
	   		</span>
	   	</td>
	  </tr>
	  <tr>
	   	<td>
				<display:table name="elencoCampi" class="datilista" id="campo">
					<display:column title="Campo" >${fn:substringAfter(campo.mnemonicoCampo, "_")}</display:column>
					<display:column title="Titolo colonna"><input class="testo" type="text" name="id" value="${campo.titoloColonna}" size="60" maxlength="60"></display:column>
				</display:table>   	
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio">
	    	<html:hidden property="metodo" value="salvaTitoli" />
	      <html:hidden property="pageFrom" styleId="pageFrom" value="" />
	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
	    	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();"><c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>&nbsp;<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();"></c:if>&nbsp;&nbsp;&nbsp;&nbsp;
	    	<INPUT type="button" class="bottone-azione" value="Fine" title="Fine" onclick="javascript:fineWizard();">&nbsp;
	    </td>
	  </tr>
	</table>
</html:form>