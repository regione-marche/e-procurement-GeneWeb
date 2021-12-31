<%
/*
 * Created on: 21-05-2013
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
		Finestra per la funzione di salva previsione
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<div style="width:97%;">
<gene:template file="popup-template.jsp">
<gene:setString name="titoloMaschera" value='Salva previsione' />

<c:choose>
	<c:when test='${not empty param.chiave}'>
		<c:set var="chiave" value="${param.chiave}" />
	</c:when>
	<c:otherwise>
		<c:set var="chiave" value="${chiave}" />
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

<c:set var="modo" value="NUOVO" scope="request" />
	
	<c:set var="bloccoEliminazione" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.ControlliSalvaPrevisioneFunction", pageContext,entitaPartenza,chiave)}' />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="G_SCADENZ" gestisciProtezioni="false" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestorePopupSalvaPrevisione">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${requestScope.salvataggioPrevisoneEseguito eq "1"}'>
					Previsione salvata con successo
					<br>
					<br>
				</c:when>
				<c:otherwise>
					<c:choose>
					<c:when test='${empty requestScope.conteggioPrevisione or requestScope.conteggioPrevisione eq "0"}' >
						Confermi l'inserimento della previsione a partire dai dati correnti?
					</c:when>
					<c:otherwise>
						E' già presente una previsione, vuoi procedere con l'eliminazione della previsione esistente e 
						la creazione di una nuova a partire dai dati correnti?
					</c:otherwise>
					</c:choose>
					<br>
					<br>
					<c:if test='${not empty requestScope.conteggioConsuntivo and requestScope.conteggioConsuntivo > 0}' >
						Son presenti dati di consuntivo che non verranno salvati nella previsione.
					</c:if>
					<br>&nbsp;
				</c:otherwise>
			</c:choose>
		</td>
	</gene:campoScheda>
			
	<gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${empty requestScope.salvataggioPrevisoneEseguito or requestScope.salvataggioPrevisoneEseguito ne "1"}' >
					<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
				</c:otherwise>
			</c:choose>
			
			
		</td>
	</gene:campoScheda>
	<input type="hidden" name="conteggioPrevisione" id="conteggioPrevisione" value="${requestScope.conteggioPrevisione}">
	<input type="hidden" name="chiave" id="chiave" value="${chiave}">
	<input type="hidden" name="entitaPartenza" id="entitaPartenza" value="${entitaPartenza}">
	</gene:formScheda>
  </gene:redefineInsert>


	
	<gene:javaScript>
				
		function conferma() {
			document.forms[0].jspPathTo.value="gene/g_scadenz/popup-salva-previsione.jsp";
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



	