<%
/*
 * Created on: 24-05-2016
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
		Finestra per l'attivazione della funzione 'Soggetto con delega in Portale Appalti'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.delegaPortaleEseguita and requestScope.delegaPortaleEseguita eq "1"}' >
		<script type="text/javascript">
			window.opener.bloccaRichiesteServer();
			window.opener.selezionaPagina(0);
			window.close();
		</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Soggetto con delega su portale' />

<c:choose>
	<c:when test='${not empty param.codice}'>
		<c:set var="codice" value="${param.codice}" />
	</c:when>
	<c:otherwise>
		<c:set var="codice" value="${codice}" />
	</c:otherwise>
</c:choose>

<c:set var="codfisc" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetUtenteDelegatotFunction", pageContext, codice)}' />

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	
	<gene:formScheda entita="IMPR" gestisciProtezioni="false" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestorePopupSoggettoDelegaPortale">
	
		<gene:campoScheda>
			<td colSpan="2">
				<br>
				<c:choose>
					<c:when test="${requestScope.utenteNonDefinito eq '1' || requestScope.delegatoNonDefinito eq '1' || requestScope.erroreNonDefinito eq '1'}">
						Non è stato possibile procedere con l'operazione poichè:<br>
						<c:if test="${requestScope.utenteNonDefinito eq '1'  }">
							<b>Username dell'impresa non definito</b>
						</c:if>
						<c:if test="${requestScope.delegatoNonDefinito eq '1'  }">
							<b>Soggetto delegato non definito</b>
						</c:if>
						<c:if test="${requestScope.erroreNonDefinito eq '1'  }">
							<b>Si è presentato un errore </b>
						</c:if>
						<br>
						<br>
					</c:when>
					<c:otherwise>
						L'operatività nel Portale Appalti per conto dell'impresa può essere demandata all'unico soggetto sotto indicato, individuato dal codice fiscale. 
						Tale soggetto dovrà utilizzare il sistema di autenticazione previsto dall'Ente per accedere al Portale. 
						Per modificare il soggetto delegato basta riportare il codice fiscale del nuovo soggetto subentrante.
						<br>
						<br>
						<c:if test="${empty codfisc}">
							<b>Soggetto delegato non definito</b>
							<br>
							<br>
						</c:if>
					</c:otherwise>
				</c:choose>
				
			</td>
		</gene:campoScheda>

		<gene:campoScheda campo="CODIMP" visibile="false" defaultValue="${codice}"/>
		<c:if test="${!empty codfisc && requestScope.utenteNonDefinito ne '1' && requestScope.delegatoNonDefinito ne '1' && requestScope.erroreNonDefinito ne '1'}">
			<gene:campoScheda campo="CODFISC" campoFittizio="true" title="Codice Fiscale del soggetto delegato" defaultValue="${codfisc}" obbligatorio="true" definizione="T40;0;;;"/>
			<gene:campoScheda campo="USERNAME" campoFittizio="true" title="Username" defaultValue="${requestScope.username}" definizione="T20;0;;;" visibile="false"/>
		</c:if>
		
		<input type="hidden" name="codice" id="codice" value="${codice}" />
	</gene:formScheda>
	
  </gene:redefineInsert>


	<gene:redefineInsert name="buttons">
		<c:if test='${!empty codfisc && requestScope.utenteNonDefinito ne "1" && requestScope.delegatoNonDefinito ne "1" && requestScope.erroreNonDefinito ne "1"}' >
			<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
		</c:if>	
		<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>


	
	<gene:javaScript>
		function conferma() {
			var eseguiSubmit = false;
			document.forms[0].jspPathTo.value="gene/impr/popupSoggettoDelegaPortale.jsp";
			var codfisc = getValue("CODFISC");
			if(codfisc!=null){
				if(!checkCodFis(codfisc)){
					if (confirm("Il codice fiscale specificato non è valido! Prosegui ugualmente?")) {
						eseguiSubmit = true;
					} else {
						document.getElementById("CODFISC").focus();
						return;
					}
				} else {
					eseguiSubmit = true;
				}
			}
			if (eseguiSubmit) {
				schedaConferma();
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