<%
/*
 * Created on: 03-feb-2020
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Lista dei tecnici progettisti */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%/*Imposto il menu nel titolo*/%>
<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="modo" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GestioneVerificheFunction", pageContext, "")}'/>
<c:set var="codiceImpresa" value='${gene:getValCampo(key, "IMPR.CODIMP")}' scope="request"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:choose>
	<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou228#")}'>
		<c:set var="opzVerifiche" value="ou228" />
	</c:when>
	<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou227#")}'>
		<c:set var="opzVerifiche" value="ou227" />
	</c:when>
	<c:otherwise>
		<c:set var="opzVerifiche" value="" />
	</c:otherwise>
</c:choose>

<c:if test="${! empty sessionScope.uffint}">
	<c:set var="codfiscUffint" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetCodfiscUffintFunction", pageContext, sessionScope.uffint)}'/>
</c:if>

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<table class="dettaglio-tab-lista" >
	<tr>
		<td>
			<gene:formLista entita="VERIFICHE" where="VERIFICHE.CODIMP = #IMPR.CODIMP# AND VERIFICHE.CFEIN = '${codfiscUffint}'"  sortColumn="0" pagesize="20" tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreVerificheImpresa">
				<gene:redefineInsert name="listaNuovo" /> 
				<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu" onClick="chiaveRiga='${chiaveRigaJava}'"/>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && ("ou227" eq opzVerifiche || "ou228" eq opzVerifiche)}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>
				</gene:campoLista>
				<% // Campi veri e propri %>
				<gene:campoLista campo="ID" visibile="false" edit="${updateLista eq 1}"/>
				<c:set var="isdoc"	value='${gene:callFunction2("it.eldasoft.gene.tags.functions.IsDocumentiVerificheFunction", pageContext,datiRiga.VERIFICHE_ID)}' />
				<gene:campoLista campo="CODIMP" visibile="false" />
				<gene:campoLista campo="CFEIN"  visibile="false" />
				<gene:campoLista campo="CONTESTO_VERIFICA" visibile="false" />
				<gene:campoLista campo="TIPO_VERIFICA" headerClass="sortable" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
				<gene:campoLista campo="GG_VALIDITA" edit="${updateLista eq 1 && (isdoc ne 1)}"/>
				<gene:campoLista campo="ESITO_VERIFICA" visibile="true" edit="${updateLista eq 1}"/>
				<gene:campoLista campo="DATA_ULTIMA_RICHIESTA" visibile="true" />
				<gene:campoLista campo="DATA_SILENZIO_ASSENSO" visibile="true" />
				<gene:campoLista campo="DATA_ULTIMA_CERTIFICAZIONE" visibile="true" />
				<gene:campoLista campo="DATA_SCADENZA" visibile="true" />
				<gene:campoLista campo="STATO_VERIFICA" visibile="false" />
				<gene:campoLista title="" width="20">
					<c:choose>
					<c:when test='${datiRiga.VERIFICHE_STATO_VERIFICA eq "3"}'>
						<span id="INFO_TOOLTIP${currentRow }" title="Scaduta">
							<IMG SRC="${contextPath}/img/controllo_e.gif"> </span>
					</c:when>
					<c:when test='${datiRiga.VERIFICHE_STATO_VERIFICA eq "2"}'>
						<span id="INFO_TOOLTIP${currentRow }" title="In scadenza">
							<IMG SRC="${contextPath}/img/controllo_w.gif"> </span>
					</c:when>
					<c:otherwise>
						<span id="INFO_TOOLTIP${currentRow }" title=" In corso">
						    <IMG SRC="${contextPath}/img/controllo_i.gif"> </span>
					</c:otherwise>
					</c:choose>
				</gene:campoLista>
				
				<input type="hidden" name="numeroVerifiche" id="numeroVerifiche" value="" />
				<input type="hidden" name="archiviati" id="archiviati" value="" />
				
			</gene:formLista>

			<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.visualizza" title="Visualizza verifica"/>
			<c:if test='${gene:checkProtFunz(pageContext, "MOD", "MOD") && "ou228" eq opzVerifiche}' >
				<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.modifica" title="Modifica verifica" />
			</c:if>
			<c:if test='${gene:checkProtFunz(pageContext, "DEL", "DEL") && "ou228" eq opzVerifiche}' >
				<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.elimina" title="Elimina verifica" />
			</c:if>
			<c:if test='${"ou228" eq opzVerifiche}' >
				<gene:PopUpItem variableJs="rigaPopUpMenu" title="Visualizza documenti archiviati" href="visDocArchiviati()" />
			</c:if>
		</td>
	</tr>
	

	<gene:redefineInsert name="addToAzioni" >
		<c:choose>
			<c:when test='${updateLista eq 1 }'>
				<c:if test='${"ou228" eq opzVerifiche}'>
				<tr>
					<td class="vocemenulaterale" >
						<a href="javascript:listaConferma();" title="Salva modifiche" >Salva</a>
					</td>
				</tr>
				<tr>
					<td class="vocemenulaterale" >
						<a href="javascript:listaAnnullaModifica();" title="Annulla modifiche" >Annulla</a>
					</td>
				</tr>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:if test='${"ou228" eq opzVerifiche}'>
				<tr>
					<td class="vocemenulaterale" >
						<a href="javascript:listaApriInModifica();" title="Modifica" tabindex="1501">Modifica</a>
					</td>
				</tr>	
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:apriPopupInsertPredefiniti('${codiceImpresa}')" title="Inserisci verifiche predefinite" tabindex="1502">
						</c:if>
							Inserisci verifiche predefinite
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
				</c:if>
				<c:if test="${gene:checkProtFunz(pageContext,'ALT','EsportaDocumentiVeriche') and ('ou228' eq opzVerifiche or 'ou227' eq opzVerifiche)}">
				<tr>
					<td class="vocemenulaterale">
						<a href='javascript:openModalDownloadDoc("${codiceImpresa}","${pageContext.request.contextPath}" );' title='Esporta su file zip' tabindex="1504">
							Esporta su file zip
						</a>
					</td>
				</tr>
				</c:if>
			</c:otherwise>
		</c:choose>

	</gene:redefineInsert>
		
	<jsp:include page="/WEB-INF/pages/gene/verifiche/modalPopupDownloadAllegatiVerificheImpr.jsp" />
	
	<c:if test='${"ou228" eq opzVerifiche}'>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<c:choose>
					<c:when test='${updateLista eq 1}'>
						<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="listaConferma();">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
					</c:when>
					<c:otherwise>
						<c:if test='${gene:checkProtFunz(pageContext, "MOD", "MOD")}'>
							<INPUT type="button"  class="bottone-azione" value="Modifica" title="Modifica" onclick="javascript:listaApriInModifica();">&nbsp;
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
						</c:if>
					</c:otherwise>
				</c:choose>
				&nbsp;
			</td>
		</tr>
	</c:if>


	<c:if test='${"ou228" ne opzVerifiche || updateLista eq 1}'>
		<gene:redefineInsert name="listaEliminaSelezione" />
	</c:if>

</table>

<gene:javaScript>
	document.getElementById("numeroVerifiche").value = ${currentRow}+1;

	function apriPopupInsertPredefiniti(codiceImpresa) {
		var href = "href=gene/verifiche/conferma-ins-verifiche-predefinite.jsp?codimp="+codiceImpresa;
		openPopUpCustom(href, "insVerifichePredefinite", 600, 350, "no", "yes");
	}
	function visDocArchiviati() {
	 document.forms[0].archiviati.value = "1";
	 listaVisualizza();
	}
	
	function listaElimina(){
		var message = "Attenzione: eliminando la verifica saranno eliminati anche tutti i documenti contenuti al suo interno e le relative scadenze. Si è sicuri di voler procedere? ";
		if(confirm(message)){
			document.forms[0].key.value = chiaveRiga;
			document.forms[0].metodo.value = "elimina";
			document.forms[0].submit();
		}
	}
	
	function listaEliminaSelezione(){
		var message = "Attenzione: eliminando le verifiche selezionate saranno eliminati anche tutti i documenti contenuti al loro interno e le relative scadenze. Si è sicuri di voler procedere? ";
		
		var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
		if (numeroOggetti == 0) {
		  alert("Nessun elemento selezionato nella lista");
		} else {
		// Verifico se esiste ina classe per i popup di conferma d'eliminazione
			if(classePopUpElimina==null){
			
			  if (confirm("Sono stati selezionati " + numeroOggetti + " elementi." + message)) {
						listaEliminaSelezionePopUp();
					}
				}else{
					showConfermaPopUp("elimina",classePopUpElimina,"keys","listaEliminaSelezionePopUp");
				}
			}
	}
	
	
	
</gene:javaScript>