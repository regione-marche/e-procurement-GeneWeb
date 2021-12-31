
<%
	/*
	 * Created on 06-ago-2008
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<%/* Chiamo la funzione che estrae l'elenco dei campi della tabella dinamica 1:N da utilizzare nel dettaglio 
     ed altre informazioni aggiuntive a supporto della generazione della pagina 
   */%>
<gene:callFunction obj="it.eldasoft.gene.tags.functions.GetMetadatiScheda1NFunction" />

<gene:template file="scheda-template.jsp" gestisciProtezioni="true"
	idMaschera="${requestScope.entitaParent}.EXT" schema="${requestScope.schema}">
	
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="${requestScope.entita}" gestisciProtezioni="true">
				
			<input type="hidden" name="modificabile" value="${param.modificabile}" />

			<c:forEach items="${campi}" var="campo">
				<gene:campoScheda campo="${campo.DYNCAM_NAME}" title="${campo.DYNCAM_DESC}" visibile="${campo.DYNCAM_SCH eq 1}" modificabile="${campo.DYNCAM_SCH_B ne 1}" defaultValue="${gene:if(campo.DYNCAM_PK eq 1, gene:getValCampo(keyParent,campo.DYNCAM_NAME_P), '')}" gestisciProtezioni="false"/>		
			</c:forEach>
					
			<c:if test="${(empty param.modificabile) || param.modificabile}">
			<gene:campoScheda>
				<gene:redefineInsert name="pulsanteSalva">
						<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConfermaFiglia1N()">
				</gene:redefineInsert>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		<gene:redefineInsert name="schedaConferma">
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:schedaConfermaFiglia1N();" title="Salva modifiche" tabindex="1501">
						${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a></td>
			</tr>
		</gene:redefineInsert>
			</c:if>

			<c:if test="${!((empty param.modificabile) || param.modificabile)}">
			<gene:campoScheda>
				<gene:redefineInsert name="pulsanteModifica" />
				<gene:redefineInsert name="pulsanteNuovo" />
				<gene:redefineInsert name="schedaModifica" />
				<gene:redefineInsert name="schedaNuovo" />
				<gene:redefineInsert name="schedaConferma" />
			</gene:campoScheda>
			</c:if>
		</gene:formScheda>
	</gene:redefineInsert>
	
	<gene:javaScript>
	function schedaConfermaFiglia1N() {
		document.forms[0].metodo.value="updateFiglia1N";
		schedaConfermaPopUp();
	}
	</gene:javaScript>		
</gene:template>

