<%
/*
 * Created on: 14-10-2010
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
		Finestra per l'attivazione della funzione 'Registra su portale'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.registrazioneEseguita and requestScope.registrazioneEseguita eq "1"}' >
		<script type="text/javascript">
			window.opener.bloccaRichiesteServer();
			window.opener.selezionaPagina(0);
			window.close();
		</script>
	</c:when>
	<c:otherwise>
<c:set var="controlloSuperato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlloRegistrazioneIscrizionePortaleFunction", pageContext, null)}' /> 
<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Registra su portale Appalti' />

<c:choose>
	<c:when test='${not empty param.codice}'>
		<c:set var="codice" value="${param.codice}" />
	</c:when>
	<c:otherwise>
		<c:set var="codice" value="${codice}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ragsoc}'>
		<c:set var="ragsoc" value="${param.ragsoc}" />
	</c:when>
	<c:otherwise>
		<c:set var="ragsoc" value="${ragsoc}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.email}'>
		<c:set var="email" value="${param.email}" />
	</c:when>
	<c:otherwise>
		<c:set var="email" value="${email}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.pec}'>
		<c:set var="pec" value="${param.pec}" />
	</c:when>
	<c:otherwise>
		<c:set var="pec" value="${pec}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codfisc}'>
		<c:set var="codfisc" value="${param.codfisc}" />
	</c:when>
	<c:otherwise>
		<c:set var="codfisc" value="${codfisc}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.piva}'>
		<c:set var="piva" value="${param.piva}" />
	</c:when>
	<c:otherwise>
		<c:set var="piva" value="${piva}" />
	</c:otherwise>
</c:choose>

<c:set var="impresaRegistrata" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.ImpresaRegistrataSuPortaleFunction", pageContext,codice)}' />

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	
			<c:choose>	
			<c:when test="${controlloSuperato eq 'SI' || empty controlloSuperato}">
	
	
	<gene:formScheda entita="IMPR" gestisciProtezioni="false" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestorePopupRegistraSuPortale">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${requestScope.userPresente eq "1"}'>
					Il nome utente <b>${requestScope.codiceUser}</b> è già utilizzato da un'altra impresa registrata su portale.<br>  
					Inserire un nome utente differente.
					<c:set var="isVisible" value="true"/>
				</c:when>
				<c:when test='${requestScope.mailNonValorizzata eq "1"}'>
					Non è possibile procedere con la registrazione sul portale Appalti.<br>
					L'indirizzo mail <b>"${requestScope.indirizzoMail}"</b> o pec <b>"${requestScope.indirizzoPec}"</b> non è valorizzato.<br>
					<c:set var="isVisible" value="false"/> 
				</c:when>
				<c:when test='${requestScope.mailPresente eq "1"}'>
					Non è possibile procedere con la registrazione sul portale Appalti.<br>
					<c:if test="${not empty requestScope.indirizzoMail}">
						<c:set var="msgMail" value=" mail <b>${requestScope.indirizzoMail}</b>"/>
					</c:if>
					<c:if test="${not empty requestScope.indirizzoPec}">
						<c:choose>
							<c:when test="${not empty requestScope.indirizzoMail}">
								<c:set var="msgMail" value=" mail <b>${requestScope.indirizzoMail}</b> o pec <b>${requestScope.indirizzoPec}</b>"/>
							</c:when>
							<c:otherwise>
								<c:set var="msgMail" value=" pec <b>${requestScope.indirizzoPec}</b>"/>
							</c:otherwise>
						</c:choose>
					</c:if>
					
					L'indirizzo ${msgMail} è già utilizzato da un'altra impresa registrata su portale.<br>
					<c:set var="isVisible" value="false"/> 
				</c:when>
				<c:when test='${requestScope.mailNonValida eq "1"}'>
					Non è possibile procedere con la registrazione sul portale Appalti.<br>
					L'indirizzo mail <b>"${requestScope.indirizzoMail}"</b> o pec <b>"${requestScope.indirizzoPec}"</b> non è valido.<br>
					<c:set var="isVisible" value="false"/> 
				</c:when>
				<c:when test='${requestScope.codfiscPresente eq "1"}'>
					Non è possibile procedere con la registrazione sul portale Appalti.<br>
					Il codice fiscale <b>${requestScope.codfisc}</b> è già utilizzato da un'altra impresa registrata su portale.<br>
					<c:set var="isVisible" value="false"/> 
				</c:when>
				<c:when test='${requestScope.pivaPresente eq "1"}'>
					Non è possibile procedere con la registrazione sul portale Appalti.<br>
					La partita I.V.A. <b>${requestScope.piva}</b> è già utilizzata da un'altra impresa registrata su portale.<br>
					<c:set var="isVisible" value="false"/> 
				</c:when>
				<c:when test='${requestScope.erroreServizioPortale eq "1"}'>
					Non è possibile procedere con la registrazione sul portale Appalti.<br>
					<b>Si è verificato un errore.<b>
					<c:set var="isVisible" value="false"/>
				</c:when>
				<c:when test='${impresaRegistrata eq "SI"}'>
					L'impresa è già registrata.
					<c:set var="isVisible" value="false"/>
				</c:when>
				<c:otherwise>
					Confermi la registrazione sul portale Appalti?
					<c:set var="isVisible" value="true"/>
				</c:otherwise>
			</c:choose>
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>

		<c:if test="${impresaRegistrata ne 'SI' }">
			<gene:campoScheda campo="USER" title="Nome utente" campoFittizio="true" definizione="T20" visibile="${isVisible}" obbligatorio="true"/>
			<gene:campoScheda campo="CODIMP" campoFittizio="true" defaultValue="${codice}" 	visibile="false" definizione="T10;0;;;CODIMP"/>
			<gene:campoScheda campo="RAGIONESOC" campoFittizio="true" defaultValue="${ragsoc}" visibile="false" definizione="T120;0;;;NOMIMP"/>
		</c:if>
		<input type="hidden" name="codice" id="codice" value="${codice}" />
		<input type="hidden" name="ragsoc" id="ragsoc" value="${ragsoc}" />
		<input type="hidden" name="email" id="email" value="${email}" />
		<input type="hidden" name="pec" id="pec" value="${pec}" />
		<input type="hidden" name="codfisc" id="codfisc" value="${codfisc}" />
		<input type="hidden" name="piva" id="piva" value="${piva}" />
	</gene:formScheda>
	
			</c:when>
			<c:otherwise>
				<br>
				${requestScope.msg }
				<br>
				<br>
				<gene:redefineInsert name="buttons">
		
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
						
				</gene:redefineInsert>
			</c:otherwise>
		</c:choose>
	
  </gene:redefineInsert>

<c:if test='${impresaRegistrata eq "SI" || requestScope.erroreServizioPortale eq "1" || requestScope.mailPresente eq "1"|| requestScope.mailNonValida eq "1" || requestScope.codfiscPresente eq "1" || requestScope.pivaPresente eq "1"}' >
	<gene:redefineInsert name="buttons">
		
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			
	</gene:redefineInsert>
</c:if>

	
	<gene:javaScript>
		function conferma() {
			//Controllo sui caratteri ammessi. (Stesso criterio di quello usato dal portale)
			var nomeUtente= getValue("USER");
			var espressione =/^[a-z0-9\.]+$/i;
			if (!espressione.test(nomeUtente)){
			  alert("I caratteri ammessi per il Nome utente sono lettere, numeri ed il punto."); 
			}else{
				document.forms[0].jspPathTo.value="gene/impr/popupRegistraSuPortale.jsp";
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