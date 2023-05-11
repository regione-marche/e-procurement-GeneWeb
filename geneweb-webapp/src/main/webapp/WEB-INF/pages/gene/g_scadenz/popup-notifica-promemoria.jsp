<%
/*
 * Created on: 10-04-2013
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
		Finestra per la funzione di notifica promemoria
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<div style="width:97%;">
<gene:template file="popup-template.jsp">
<gene:setString name="titoloMaschera" value='Notifica promemoria' />

<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.entitaPartenza}'>
		<c:set var="entitaPartenza" value="${param.entitaPartenza}" />
	</c:when>
	<c:otherwise>
		<c:set var="entitaPartenza" value="${entitaPartenza}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.discriminante}'>
		<c:set var="discriminante" value="${param.discriminante}" />
	</c:when>
	<c:otherwise>
		<c:set var="discriminante" value="${discriminante}" />
	</c:otherwise>
</c:choose>

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="G_SCADENZ" gestisciProtezioni="false" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestorePopupNotificaPromemoria">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${not empty requestScope.aggiornamentoNotificaEseguita and requestScope.aggiornamentoNotificaEseguita eq "1"}' >
					Aggiornamento dati promemoria eseguito con successo
				</c:when>
				<c:otherwise>
					Inserire i dati relativi al promemoria
				</c:otherwise>
			</c:choose>
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	<c:if test='${empty requestScope.aggiornamentoNotificaEseguita or requestScope.aggiornamentoNotificaEseguita ne "1"}' >
		<gene:campoScheda campo="GGPROMEM" title='Invia promemoria N giorni prima della scadenza' campoFittizio="true" definizione="N3;0;;;G_SCADGGPROMEM" value='0'/>
		<gene:campoScheda campo="REFPROMEM" title='Destinatari promemoria' campoFittizio="true" definizione="T30;0;;;G_DMODSCADREF" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoRefpromem"/>
		<gene:campoScheda campo="DESTPROMEM" title='Altri destinatari promemoria' campoFittizio="true" definizione="T200;0;;NOTE;G_SCADDEST" />
	</c:if>
	
	
		<input type="hidden" name="id" id="id" value="${id}" />
		
		<gene:campoScheda>
			<td class="comandi-dettaglio" colSpan="2">
				<c:choose>
					<c:when test='${empty requestScope.aggiornamentoNotificaEseguita or requestScope.aggiornamentoNotificaEseguita ne "1"}' >
						<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</c:when>
					<c:otherwise>
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</c:otherwise>
				</c:choose>
				
				
			</td>
		</gene:campoScheda>
		
	</gene:formScheda>
  </gene:redefineInsert>


	
	<gene:javaScript>
				
		function conferma() {
			var destpromem = getValue("DESTPROMEM");
				if(destpromem!=null && destpromem!=""){
					var listaMail = trimStringa(destpromem).split(',');
					var esitoMail1 = true;
					for(var i = 0; i < listaMail.length; i++){
					  	listaMail[i] = trimStringa(listaMail[i]);
					  	esitoMail1 = isFormatoEmailValido(trimStringa(listaMail[i]));
					  	if(!esitoMail1){
					  		if(listaMail.length > 1)
					  			alert("La lista degli indirizzi email nel campo 'Invio promemoria a' presenta degli errori di sintassi.\nVerificare la sintassi di tutti gli indirizzi.");
					  		else
					  			alert("L'indirizzo email nel campo 'Invio promemoria a' non e' sintatticamente valido.");
					  		return;
					  		
					  	}
					  }
				}
				
				
				var promem = getValue("GGPROMEM");
				if(promem==null || promem=="")
					setValue("GGPROMEM","0");
			
			document.forms[0].jspPathTo.value="gene/g_scadenz/popup-notifica-promemoria.jsp";
			schedaConferma();
		}
		
		function chiudi(){
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		}
		
		function annulla(){
			window.close();
		}
		
		
		
	

	</gene:javaScript>
</gene:template>
</div>

	