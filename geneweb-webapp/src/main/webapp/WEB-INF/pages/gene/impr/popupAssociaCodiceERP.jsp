<%
/*
 * Created on: 22-04-2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra per l'aggiornamento codice ERP 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.associaERPSuccess and requestScope.associaERPSuccess eq "1"}' >
		<script type="text/javascript">
			window.opener.bloccaRichiesteServer();
			window.opener.selezionaPagina(0);
			window.close();
		</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Associa codice ERP' />

<c:choose>
	<c:when test='${not empty param.codice}'>
		<c:set var="codice" value="${param.codice}" />
	</c:when>
	<c:otherwise>
		<c:set var="codice" value="${codice}" />
	</c:otherwise>
</c:choose>

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	
	<gene:formScheda entita="IMPR" gestisciProtezioni="false" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestorePopupAssociaCodiceERP">
		<gene:campoScheda>
			<td colSpan="2">
				<br>
				<c:choose>
					<c:when test='${!empty requestScope.messageError}'>
						Si sono verificati degli errori durante l'operazione: <br><br>
						${requestScope.messageError}
						<br><br>
					</c:when>
				</c:choose>
			</td>
		</gene:campoScheda>
		<gene:campoScheda campo="CODIMP" visibile="false" defaultValue="${codice}"/>
		<gene:campoScheda campo="CGENIMP" title="Codice ERP" obbligatorio="true"/>
		
		<input type="hidden" name="codice" id="codice" value="${codice}" />
	</gene:formScheda>
	
  </gene:redefineInsert>


	<gene:redefineInsert name="buttons">
		<c:if test='${empty requestScope.messageError}'>
			<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
		</c:if>
		<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>


	
	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gene/impr/popupAssociaCodiceERP.jsp";
			var cgenimp = getValue("CGENIMP");
			if(cgenimp!=null){
				schedaConferma();
			} else {
				alert("Codice ERP obbligatorio per proseguire");
				document.getElementById("CGENIMP").focus();
			}
		}
		
		function annulla(){
			window.close();
		}
		
	

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>