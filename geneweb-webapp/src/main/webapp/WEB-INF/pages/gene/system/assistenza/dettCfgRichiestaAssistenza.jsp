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

  // PAGINA CHE CONTIENE IL DETTAGLIO DELLA CONFIGURAZIONE DELLA RICHIESTA DI ASSISTENZA
%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />

<tiles:insert definition=".dettaglioNoTabDef" flush="true">

	<tiles:put name="head" type="string">
		<script type="text/javascript">
			<!--
			function visualizza(modo) {
				document.location.href = 'ConfigurazioneRichiestaAssistenza.do?'+csrfToken+'&metodo=visualizza&modo='+modo;
			}

			function modifica() {
				document.location.href = 'ConfigurazioneRichiestaAssistenza.do?'+csrfToken+'&metodo=modifica';
			}

			function modificaPassword() {
				document.location.href = 'ConfigurazioneRichiestaAssistenza.do?'+csrfToken+'&metodo=modificaPassword';
			}
			-->
		</script>
	</tiles:put>

	<tiles:put name="azioniContesto" type="string">
		<gene:template file="menuAzioni-template.jsp">
			<%
				/* Inseriti i tag per la gestione dell' history:
				 * il template 'menuAzioni-template.jsp' e' un file vuoto, ma e' stato definito 
				 * solo perche' i tag <gene:insert>, <gene:historyAdd> richiedono di essere 
				 * definiti all'interno del tag <gene:template>
				 */
			%>
			<gene:insert name="addHistory">
				<gene:historyAdd titolo='Dettaglio configurazione richiesta assistenza' id="scheda" />
			</gene:insert>
		</gene:template>
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:modifica();" tabindex="1501" title="Modifica la configurazione">Modifica</a>
			</td>
		</tr>
		<c:if test='${!empty cfgRichiestaAssistenzaForm.servizioUrl}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:modificaPassword();" tabindex="1502" title="Modifica la password">Imposta password</a>
				</td>
			</tr>
		</c:if>
		<tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
	</tiles:put>

	<tiles:put name="titoloMaschera" type="string" value="Configurazione richiesta assistenza" />

	<tiles:put name="dettaglio" type="string">
		<table class="dettaglio-notab">
			<tr>
				<td colspan="2">
					<input type="radio" name="radioCfg" <c:if test='${empty cfgRichiestaAssistenzaForm.modo || cfgRichiestaAssistenzaForm.modo eq "0"}'>checked="checked"</c:if> <c:if test='${!empty cfgRichiestaAssistenzaForm.modo && cfgRichiestaAssistenzaForm.modo ne "0"}'>disabled="disabled"</c:if>>Disabilitata</input>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="radio" name="radioCfg" <c:if test='${cfgRichiestaAssistenzaForm.modo eq "1"}'>checked="checked"</c:if> <c:if test='${cfgRichiestaAssistenzaForm.modo ne "1"}'>disabled="disabled"</c:if>>Richiesta di assistenza inviata mediante servizio</input>
				</td>
			</tr>
			<c:if test='${cfgRichiestaAssistenzaForm.modo eq "1" }'>
				<tr>
					<td class="etichetta-dato">Oggetto</td>
					<td class="valore-dato">
						${cfgRichiestaAssistenzaForm.oggetto}
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Url</td>
					<td class="valore-dato">
						${cfgRichiestaAssistenzaForm.servizioUrl}
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Username</td>
					<td class="valore-dato">
						${cfgRichiestaAssistenzaForm.servizioUsr}
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Password</td>
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
						${cfgRichiestaAssistenzaForm.servizioFileSize}
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="2">
					<input type="radio" name="radioCfg" <c:if test='${cfgRichiestaAssistenzaForm.modo eq "2"}'>checked="checked"</c:if> <c:if test='${cfgRichiestaAssistenzaForm.modo ne "2"}'>disabled="disabled"</c:if>>Richiesta di assistenza inviata mediante mail</input>
				</td>
			</tr>
			<c:if test='${cfgRichiestaAssistenzaForm.modo eq "2" }'>
				<tr>
					<td class="etichetta-dato">Oggetto</td>
					<td class="valore-dato">
						${cfgRichiestaAssistenzaForm.oggetto}
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Mail</td>
					<td class="valore-dato">
						${cfgRichiestaAssistenzaForm.mail}
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Dimensione massima allegato (in MB)</td>
					<td class="valore-dato">
						${cfgRichiestaAssistenzaForm.servizioFileSize}
					</td>
				</tr>
			</c:if>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica la configurazione della richiesta di assistenza" onclick="javascript:modifica();">
					<c:if test='${!empty cfgRichiestaAssistenzaForm.servizioUrl}'>
						<INPUT type="button" class="bottone-azione" value="Imposta password" title="Imposta o sbianca la password" onclick="javascript:modificaPassword();">
					</c:if>
					&nbsp;
				</td>
			</tr>
		</table>
	</tiles:put>

</tiles:insert>
