<%
/*
 * Created on: 17-dic-2008
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
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>



	<c:if test='${fn:containsIgnoreCase(key, "DOCUMENTI_VERIFICHE")}'>
		<c:set var="entita" value="DOCUMENTI_VERIFICHE"/>
		<c:set var="titolo" value="Archiviazione del documento di verifica"/>
		<c:set var="msgConferma" value="Confermi l'archiviazione dell documento selezionato?"/>
		<c:set var="msgOk" value="Il documento risulta archiviato."/>
	</c:if>


<div style="width:97%;">
	
	<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value='${titolo }' />
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="DOCUMENTI_VERIFICHE" gestisciProtezioni="false" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestorePopupArchiviaDocumento">
			<c:choose>
				<c:when test='${RISULTATO eq "OPERAZIONEESEGUITA"}'>
					<gene:campoScheda>
						<td colspan="2">
						<br>
						${msgOk }
						<br><br>
						</td>
					</gene:campoScheda>
				</c:when>
				
				<c:otherwise>
					<gene:campoScheda>
						<td colspan="2">
						<br>
						${msgConferma }
						<br><br>
						</td>
					</gene:campoScheda>
				</c:otherwise>
			</c:choose>
			<gene:campoScheda campo="ID" visibile="false" />	
			<gene:campoScheda campo="ID_VERIFICA" visibile="false" />
			<gene:campoScheda campo="ISARCHI" visibile="false" />
					
			<c:choose>
				<c:when test='${!empty RISULTATO}'>
					<gene:campoScheda>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</gene:campoScheda>
				</c:when>
				
				<c:otherwise>
					<gene:campoScheda>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</gene:campoScheda>
				</c:otherwise>
			</c:choose>
			<input type="hidden" id="isCatalogo" value="isCatalogo"/>
		</gene:formScheda>
  	</gene:redefineInsert>

	<gene:javaScript>
	
	
		
		
		<c:if test='${RISULTATO eq "OPERAZIONEESEGUITA"}'>
			window.opener.historyReload();
			window.close();
		</c:if>
		
		function conferma() {
			document.forms[0].jspPathTo.value="gene/verifiche/popup-archivia-docverifica.jsp";
			document.forms[0].DOCUMENTI_VERIFICHE_ISARCHI.value=1;
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}

	</gene:javaScript>
	
	</gene:template>
</div>
