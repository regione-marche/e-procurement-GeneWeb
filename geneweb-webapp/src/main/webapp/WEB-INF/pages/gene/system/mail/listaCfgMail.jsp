<%
/*
 * Created on 02-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA PROFILI 
 // CONTENENTE LA EFFETTIVA LISTA DEI PROFILI 
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<tiles:insert  definition=".listaDef" flush="true">

<tiles:put name="head" type="string">

	<script type="text/javascript">
<!--
	
<!-- elda:jsFunctionOpzioniListaRecord contextPath="${pageContext.request.contextPath}"/-->
	function generaPopupListaOpzioniRecord(id) {
		<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
			<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
			<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
			<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
		</elda:jsBodyPopup>
		return linkset;
	}


	function nuovo(){
		bloccaRichiesteServer();
		document.location.href = 'ConfigurazioneMail.do?'+csrfToken+'&metodo=nuovo';
	}
	
	function modifica(id){
		document.location.href = 'ConfigurazioneMail.do?'+csrfToken+'&metodo=modifica&idcfg=' + id;
  	}

	
	function visualizza(id){
		document.location.href = 'ConfigurazioneMail.do?'+csrfToken+'&metodo=visualizza&idcfg=' + id;
	}
	
	function elimina(id){
		if (confirm("Confermi l'eliminazione della configurazione?")){
			bloccaRichiesteServer();
			document.location.href = 'ListaConfigurazioniMail.do?'+csrfToken+'&metodo=elimina&idcfg=' + id;
		}
	}
	
	function eliminaSelez(){
		  var numeroOggetti = contaCheckSelezionati(document.listaConfigurazioniMail.id);
		  if (numeroOggetti == 0) {
		    alert("Nessun elemento selezionato nella lista");
		  } else {
	   	  	if (confirm("Sono stati selezionati " + numeroOggetti + " record. Procedere con l'eliminazione?")) {
	      		bloccaRichiesteServer();
	    	  	document.listaConfigurazioniMail.metodo.value="eliminaSelez";
		      	document.listaConfigurazioniMail.submit();
		   }
		 }
	}


-->
</script>

</tiles:put>

<tiles:put name="azioniContesto" type="string">
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<gene:template file="menuAzioni-template.jsp">
	<gene:insert name="addHistory">
		<gene:historyAdd titolo='Lista Configurazioni Mail' id="lista" />
	</gene:insert>
</gene:template>

		<tr>
			<td class="titolomenulaterale">Lista: Azioni</td>
		</tr>
		<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.DEL.GENEWEB.W_MAIL-Lista.LISTADELSEL")}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:eliminaSelez();" title="Elimina dati selezionati" tabindex="1500">Elimina dati selez.</a></td>
			</tr>
			</c:if>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td class="titolomenulaterale">Configurazioni Mail</td>
			</tr>
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.INS.GENEWEB.W_MAIL-Lista.LISTANUOVO")}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:nuovo();" title="Crea nuova configurazione" tabindex="1501">Nuovo</a></td>
			</tr>
			</c:if>
		</c:if>		
		<tr>
			<td>&nbsp;</td>
		</tr>
		<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />		

		
</tiles:put>

<tiles:put name="titoloMaschera" type="string" value="Lista delle configurazioni di posta" />

<tiles:put name="dettaglio" type="string">
	<form name="listaConfigurazioniMail" action="ListaConfigurazioniMail.do">
		<table class="lista">
			<tr>
				<td>
					<display:table name="listaConfigurazioniMailForm" defaultsort="3" id="listaConfigurazioniMailForm" class="datilista" sort="list" >
					<display:column class="associadati" title="Opzioni<br><center>
					<a href='javascript:selezionaTutti(document.listaConfigurazioniMail.id);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;
					<a href='javascript:deselezionaTutti(document.listaConfigurazioniMail.id);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a>
					</center>" style="width:50px">
					<A id="l${listaConfigurazioniMailForm.idcfg}" href="javascript:showMenuPopup('l${listaConfigurazioniMailForm.idcfg}',generaPopupListaOpzioniRecord('${listaConfigurazioniMailForm.idcfg}'));"><IMG src="${contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
					<input type="checkbox" name="id" value="${listaConfigurazioniMailForm.idcfg}"/>
					</display:column>
					<display:column property="nomein" title="Configurazione" href="ConfigurazioneMail.do?metodo=visualizza&" paramId="idcfg" paramProperty="idcfg"></display:column>
					<display:column property="server" title="Server" ></display:column>
					<display:column property="mailMitt" title="Mittente" ></display:column>
					</display:table>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}'
					title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:nuovo()">&nbsp;
					<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}'
					title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:eliminaSelez()">&nbsp;
				 </td>
		    </tr>
		</table>
		<input type="hidden" name="metodo" id="metodo" value="eliminaSelez">
		<input type="hidden" name="keys" id="keys" value="">
	</form>

	</tiles:put>
</tiles:insert>
