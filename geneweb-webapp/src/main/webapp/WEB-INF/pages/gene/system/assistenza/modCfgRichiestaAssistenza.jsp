<%/*
   * Created on 2-feb-2012
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE LA MODIFICA DELLA CONFIGURAZIONE DEI PARAMETRI DI POSTA
%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<tiles:insert definition=".dettaglioNoTabDef" flush="true">

	<tiles:put name="head" type="string">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>
		<script type="text/javascript">
			<!--
			function gestisciSubmit() {
				var continua = true;
				var modo = document.cfgRichiestaAssistenzaForm.modo.value;
				var oldModo = document.cfgRichiestaAssistenzaForm.oldModo.value;
				if (modo == "1" || modo == "2") {
					if (continua && !controllaCampoInputObbligatorio(document.cfgRichiestaAssistenzaForm.oggetto, 'Oggetto')) {
						continua = false;
					}
				}
				if (modo == "2" && continua && !controllaCampoInputObbligatorio(document.cfgRichiestaAssistenzaForm.mail, 'Mail')) {
						continua = false;
				}
				if (modo == "2" && continua && !isEmailFormatoValido(document.cfgRichiestaAssistenzaForm.mail)) {
					continua = false;
				}
				if (modo == "1" && continua && !controllaCampoInputObbligatorio(document.cfgRichiestaAssistenzaForm.servizioUrl, 'Url')) {
					continua = false;
				}
				if (modo == "1" && continua && !controllaCampoInputObbligatorio(document.cfgRichiestaAssistenzaForm.servizioUsr, 'Username')) {
					continua = false;
				}
				if(continua && (modo == "1" || modo == "2") && !isIntero(document.cfgRichiestaAssistenzaForm.servizioFileSize.value, false)){
					alert("Il campo 'Dimensione massima allegato' prevede un valore intero");
					document.cfgRichiestaAssistenzaForm.servizioFileSize.focus();
					continua = false;
				}
				
				if (continua && modo != oldModo) {
					continua = confirm('Le precedenti impostazioni verranno cancellate. Sei sicuro di voler procedere?');
				}
				
				if (continua) {
					document.cfgRichiestaAssistenzaForm.submit();
				}
			}

			function modifica(modo) {
				document.location.href = 'ConfigurazioneRichiestaAssistenza.do?'+csrfToken+'&metodo=modifica&modo=' + modo;
			}

			function annulla() {
				document.location.href = 'ConfigurazioneRichiestaAssistenza.do?'+csrfToken+'&metodo=visualizza';
			}
-->
		</script>
	</tiles:put>

	<tiles:put name="azioniContesto" type="string">
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:gestisciSubmit();" tabindex="1501" title="Salva">Salva</a>
			</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annulla();" tabindex="1502" title="Annulla">Annulla</a>
			</td>
		</tr>
		<tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
	</tiles:put>

	<tiles:put name="titoloMaschera" type="string" value="Configurazione richiesta assistenza" />

	<tiles:put name="dettaglio" type="string">
		<html:form action="/SalvaConfigurazioneRichiestaAssistenza">
			<c:choose>
				<c:when test="${empty param.modo}">
					<html:hidden property="modo" />
				</c:when>
				<c:otherwise>
					<input type="hidden" name="modo" value="${param.modo}"/>
				</c:otherwise>
			</c:choose>
			<html:hidden property="oldModo" />
			<html:hidden property="codapp" />
			<table class="dettaglio-notab">
				<tr>
					<td colspan="2">
						<input type="radio" name="radioCfg" onclick="javascript:modifica(0);" <c:if test='${empty cfgRichiestaAssistenzaForm.modo || cfgRichiestaAssistenzaForm.modo eq "0"}'>checked="checked"</c:if>>Disabilitata</input>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="radio" name="radioCfg" onclick="javascript:modifica(1);" <c:if test='${cfgRichiestaAssistenzaForm.modo eq "1"}'>checked="checked"</c:if>>Richiesta di assistenza inviata mediante servizio</input>
						</td>
					</tr>
				<c:if test='${cfgRichiestaAssistenzaForm.modo eq "1" }'>
					<tr>
						<td class="etichetta-dato">Oggetto (*)</td>
						<td class="valore-dato">
							<html:text property="oggetto" size="50" maxlength="255"/>
						</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Url (*)</td>
						<td class="valore-dato">
							<html:text property="servizioUrl" size="50" maxlength="255"/>
						</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Username (*)</td>
						<td class="valore-dato">
							<html:text property="servizioUsr" size="50" maxlength="255"/>
						</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Password (*)</td>
						<td class="valore-dato">
							<c:choose>
								<c:when test='${!empty cfgRichiestaAssistenzaForm.servizioPwd}'>
									<c:out value="Impostata"/>
								</c:when>
								<c:otherwise>
									<c:out value="Non impostata"/>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Dimensione massima allegato (in MB)</td>
						<td class="valore-dato">
							<html:text property="servizioFileSize" size="10" maxlength="10"/>
						</td>
					</tr>
				</c:if>
				<tr>
					<td colspan="2">
						<input type="radio" name="radioCfg" onclick="javascript:modifica(2);" <c:if test='${cfgRichiestaAssistenzaForm.modo eq "2"}'>checked="checked"</c:if>>Richiesta di assistenza inviata mediante mail</input>
						</td>
					</tr>
				<c:if test='${cfgRichiestaAssistenzaForm.modo eq "2" }'>
					<tr>
						<td class="etichetta-dato">Oggetto (*)</td>
						<td class="valore-dato">
							<html:text property="oggetto" size="50" maxlength="255"/>
						</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Mail (*)</td>
						<td class="valore-dato">
							<html:text property="mail" size="60" maxlength="100"/>
						</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Dimensione massima allegato (in MB)</td>
						<td class="valore-dato">
							<html:text property="servizioFileSize" size="10" maxlength="10"/>
						</td>
					</tr>
				</c:if>
				<tr>
					<td class="comandi-dettaglio" colSpan="2">
						<INPUT type="button" class="bottone-azione" value="Salva" title="Salva" onclick="javascript:gestisciSubmit();">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
						&nbsp;
					</td>
				</tr>
			</table>
		</html:form>
	</tiles:put>

</tiles:insert>
