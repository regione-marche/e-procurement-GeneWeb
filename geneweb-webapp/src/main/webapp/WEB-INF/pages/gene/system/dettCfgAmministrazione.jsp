<%/*
   * Created on 27-ott-2014
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA PAGINA DI AMMINISTRAZIONE
%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld" prefix="csrf" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<%-- Definizione di variabili per la visualizzazione dei singolo elementi --%>

<c:set var="isConfigMailAbilitato" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-MAIL")}' scope="page" />
<c:set var="isConfigLdapAbilitato" value='${fn:contains(listaOpzioniDisponibili, "OP100#") and gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-LDAP")}' scope="page" />
<c:set var="isRichAssistenzaAbilitato" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-RichAssistenza")}' scope="page" />
<c:set var="isScadenzarioAbilitato" value='${fn:contains(listaOpzioniDisponibili, "OP128#") and gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.Configurazione-EventiScadenz")}' scope="page" />

<c:set var="isStoriaModificheAbilitato" value='${gene:isTable(pageContext, "ST_TRG") and gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.StoriaModifiche")}' scope="page" /> 
<c:set var="IsTracciaturaEventiAbilitata" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "eventi.log")}' scope="request"/>
<c:set var="isTracciaturaEventiAbilitata" value='${fn:contains(listaOpzioniUtenteAbilitate, "ou89#") and IsTracciaturaEventiAbilitata eq 1 and gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.LogEventi")}' scope="page" />

<c:set var="isConfigCodificaAutomatica" value='${gene:checkProt(pageContext,"SUBMENU.VIS.AMMINISTRAZIONE.CodificaAutomatica")}' scope="page" />

<tiles:insert definition=".listaNoAzioniDef" flush="true">

	<tiles:put name="head" type="string">
		<script type="text/javascript">
			<jsp:include page="dettCfgAmminCustom.jsp"/>
			$(function() {
				<%-- si rimuovono tutte le sezioni che non contengono voci --%>
				$("div.sez-accordion").each(function( index ) {
					if ($(this).children("p").length == 0) {
						var id = $(this).attr("id");
						var selIdTitolo = "#tit-"+id;
						$(this).remove();
						$(selIdTitolo).remove();
					}
				});

				$( "#accordion" ).accordion({
					heightStyle: "content",
					collapsible: true
				});
			});
			
			$(document).ready(function() {
				$(document).on('keydown', 'return', function(e) {
					if (e.shiftKey && e.altKey && e.which == 53) {
						document.location.href="ApriPagina.do?<csrf:token/>&href=commons/lista-c0campi.jsp";
					}
				});
			});
			
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
			<gene:historyClear/>
			<gene:insert name="addHistory">
				<gene:historyAdd titolo='Amministrazione' id="trova" />
			</gene:insert>
		</gene:template>
	</tiles:put>

	<tiles:put name="titoloMaschera" type="string" value="" />

	<tiles:put name="dettaglio" type="string">

	<div id="accordion">
		
		<%-- Inizio prima sezione --%>
		<h2 id="tit-provider" style="height: 25px; padding-top: 10px; padding-left: 30px;">Provider e servizi</h2>
		<div id="provider" class="sez-accordion">
			<%-- Inizio riga per la configurazione server di posta --%>
			<c:if test='${isConfigMailAbilitato}' >
				<p>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ListaConfigurazioniMail.do?<csrf:token/>&metodo=lista';">
					<img alt="Configurazione server di posta" src="${contextPath}/img/Communication-41.png"></a>
					&nbsp;&nbsp;&nbsp;
					<b>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ListaConfigurazioniMail.do?<csrf:token/>&metodo=lista';">Server di posta</a>
					</b>
				</p>
			</c:if>
			<%-- Fine riga per la configurazione server di posta--%>

			<%-- Inizio riga per la configurazione del server LDAP --%>
			<c:if test='${isConfigLdapAbilitato}' >
				<p>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ConfigurazioneLdap.do?<csrf:token/>&metodo=visualizza';">
					<img alt="Configurazione server LDAP" src="${contextPath}/img/Hardware-5.png"></a>
					&nbsp;&nbsp;&nbsp;
					<b>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ConfigurazioneLdap.do?<csrf:token/>&metodo=visualizza';">Server LDAP</a>
					</b>
				</p>
			</c:if>
			<%-- Fine riga per la configurazione del server LDAP --%>
			
			<%-- Inizio riga per la configurazione della richiesta di assistenza --%>
			<c:if test='${isRichAssistenzaAbilitato}'>
				<p>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ConfigurazioneRichiestaAssistenza.do?<csrf:token/>&metodo=visualizza';">
					<img alt="Configurazione della richiesta di assistenza" src="${contextPath}/img/Communication-23.png" ></a>
					&nbsp;&nbsp;&nbsp;
					<b>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ConfigurazioneRichiestaAssistenza.do?<csrf:token/>&metodo=visualizza';">Richiesta assistenza</a>
					</b>
				</p>
			</c:if>
			<%-- Fine riga per la configurazione della richiesta di assistenza --%>	
		</div>
		<%-- Fine prima sezione --%>

		<%-- Inizio seconda sezione --%>
		<h2 id="tit-monitoraggio" style="height: 25px; padding-top: 10px; padding-left: 30px;">Monitoraggio</h2>
		<div id="monitoraggio" class="sez-accordion">
			<c:if test='${isTracciaturaEventiAbilitata}' >
				<p>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=geneweb/w_logeventi/w_logeventi-trova.jsp';">
					<img alt="Tracciatura eventi" src="${contextPath}/img/Programing-56.png"></a>
					&nbsp;&nbsp;&nbsp;
					<b>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=geneweb/w_logeventi/w_logeventi-trova.jsp';">Visualizzazione eventi</a>
					</b>
				</p>
			</c:if>

			<c:if test='${isStoriaModificheAbilitato}' >
				<p>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/st_trg/st_trg-trova.jsp';">
					<img alt="Tracciatura delle modifiche" src="${contextPath}/img/Programing-56.png"></a>
					&nbsp;&nbsp;&nbsp;
					<b>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/st_trg/st_trg-trova.jsp';">Visualizzazione modifiche</a>
					</b>
				</p>
			</c:if>
			
		</div>
		<%-- Fine seconda sezione --%>

		<%-- Inizio terza sezione --%>
		<h2 id="tit-configurazione" style="height: 25px; padding-top: 10px; padding-left: 30px;">Dati</h2>
		<div id="configurazione" class="sez-accordion">
			<%-- Inizio riga per la configurazione eventi scadenzario --%>
			<c:if test='${isScadenzarioAbilitato}'>
				<p>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/g_eventiscadenz/g_eventiscadenz-trova.jsp';">
					<img alt="Eventi scadenzario" src="${contextPath}/img/Time-3.png" ></a>
					&nbsp;&nbsp;&nbsp;
					<b>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/g_eventiscadenz/g_eventiscadenz-trova.jsp';">Eventi scadenzario</a>
					</b>
				</p>
			</c:if>
			<%-- Fine riga per la configurazione eventi scadenzario --%>
			
			<p>
				<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=commons/lista-c0campi.jsp';">
				<img alt="Gestione dati tabellati" src="${contextPath}/img/Files-62.png"></a>
				&nbsp;&nbsp;&nbsp;
				<b>
				<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=commons/lista-c0campi.jsp';">Lista mnemonici</a>
				</b>
			</p>
			
		</div>
		<%-- Fine terza sezione --%>
		
		<%-- Inizio quarta sezione --%>
		<h2 id="tit-parametri" style="height: 25px; padding-top: 10px; padding-left: 30px;">Configurazioni</h2>
		<div id="parametri" class="sez-accordion">
			<p>
				<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/v_tab4_tab6/v_tab4_tab6-trova.jsp';">
				<img alt="Gestione dati tabellati" src="${contextPath}/img/Files-62.png"></a>
				&nbsp;&nbsp;&nbsp;
				<b>
				<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/v_tab4_tab6/v_tab4_tab6-trova.jsp';">Gestione dati tabellati</a>
				</b>
			</p>
			<p>
				<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/w_config/w_config-trova.jsp';">
				<img alt="Gestione configurazioni" src="${contextPath}/img/Status-30.png"></a>
				&nbsp;&nbsp;&nbsp;
				<b>
				<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/w_config/w_config-trova.jsp';">Gestione configurazioni</a>
				</b>
			</p>
			<p>
				<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=geneweb/w_quartz/w_quartz-lista.jsp';">
				<img alt="Gestione pianificazioni" src="${contextPath}/img/Time-3.png"></a>
				&nbsp;&nbsp;&nbsp;
				<b>
				<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=geneweb/w_quartz/w_quartz-lista.jsp';">Gestione pianificazioni</a>
				</b>
			</p>
			<p>
				<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=geneweb/w_tags/w_tags-lista.jsp';">
				<img alt="Configurazione etichette per le integrazioni" src="${contextPath}/img/Vote-7.png"></a>
				&nbsp;&nbsp;&nbsp;
				<b>
				<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=geneweb/w_tags/w_tags-lista.jsp';">Configurazione etichette per le integrazioni</a>
				</b>
			</p>
			
			<%-- Inizio riga per la configurazione codifica automatica --%>
			<c:if test='${isConfigCodificaAutomatica}'>
				<p>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/g_confcod/g_confcod-lista.jsp';">
					<img alt="Codifica automatica" src="${contextPath}/img/ConfigCodificaAutomatica.png" ></a>
					&nbsp;&nbsp;&nbsp;
					<b>
					<a class="link-generico" href="javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/g_confcod/g_confcod-lista.jsp';">Codifica automatica</a>
					</b>
				</p>
			</c:if>
			<%-- Fine riga per la configurazione codifica automatica --%>
			
		</div>
		<%-- Fine quarta sezione --%>
		
	</div>
		
	</tiles:put>

</tiles:insert>
