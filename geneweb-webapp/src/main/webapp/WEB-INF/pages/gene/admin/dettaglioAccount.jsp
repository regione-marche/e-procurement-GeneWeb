<%/*
       * Created on 20-Ott-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE LA DEFINIZIONE DELLE VOCI DEI MENU COMUNI A TUTTE LE APPLICAZIONI
      %>
<!-- Inserisco la Tag Library -->

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="AliceResources" />
<c:set var="nomeEntitaParametrizzata">
	<fmt:message key="label.tags.uffint.multiplo" />
</c:set>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<c:set var="account" value="${accountForm}"/>
<c:set var="listaOpzioniUtenteSys" value="${fn:join(account.opzioniUtenteSys,'#')}#" />

	<table  class="dettaglio-tab">
		<tr>
			<td colspan="2">
				<b>Dati generali</b>
			</td>
		</tr>
		<tr id="rowCodiceUtente" >
			<td class="etichetta-dato">Codice</td>
			<td class="valore-dato">
				<c:out value="${account.idAccount}"/>
			</td>
		</tr>
		<tr id="rowDescrizioneUtente" >
			<td class="etichetta-dato" >Descrizione</td>
    	<td class="valore-dato"> 
				<c:out value="${account.nome}"/>
			</td>
		</tr>
		<tr id="rowNomeUtente" >
			<td class="etichetta-dato" >Nome utente</td>
    	<td class="valore-dato"> 
				<c:out value="${account.login}"/>
		</td>
        </tr>
	<c:if test='${fn:contains(listaOpzioniDisponibili, "OP100#")}'>
		<tr id="rowUtenteLDAP">
			<td class="etichetta-dato">Utente LDAP</td>
			<td class="valore-dato"> 
				<c:choose>
					<c:when test='${accountForm.flagLdap == "1" }'>
						Si
					</c:when>
					<c:otherwise>
						No
					</c:otherwise>
				</c:choose>		
			</td>
 		</tr>
	</c:if>
		<c:if test='${account.flagLdap == 1}'>
		<tr id="rowNomeUtenteLDAP" >
			<td class="etichetta-dato" >Nome univoco utente per LDAP</td>
      		<td class="valore-dato"> 
				<c:out value="${account.dn}"/>
			</td>
		</tr>	
		</c:if>
		<c:if test="${! empty listaUffAppartenenza }">
			<tr id="rowUfficioAppartenenza" >
				<td class="etichetta-dato" >Ufficio Appartenenza</td>
	      		<td class="valore-dato"> 
		      	<c:forEach items="${listaUffAppartenenza}" var="Uff">
	 	     		<c:if test="${Uff.tipoTabellato eq account.ufficioAppartenenza}">
			      	${Uff.descTabellato}
	      			</c:if>
	    	  	</c:forEach>
				</td>
			</tr>	
		</c:if>
		<c:if test="${! empty listaCategorie }">
			<tr id="rowCategoria" >
				<td class="etichetta-dato" >Categoria</td>
				<td class="valore-dato"> 
				<c:forEach items="${listaCategorie}" var="cat">
					<c:if test="${cat.tipoTabellato eq account.categoria}">
					${cat.descTabellato}
					</c:if>
				</c:forEach>
				</td>
			</tr>	
		</c:if>
		<tr id="rowEmail" >
			<td class="etichetta-dato" >E-Mail</td>
      		<td class="valore-dato"> 
				<c:out value="${account.email}"/>
			</td>
	  	</tr>
	  	<tr id="rowCF" >
			<td class="etichetta-dato" >Codice fiscale</td>
      		<td class="valore-dato"> 
				<c:out value="${account.codfisc}"/>
			</td>
	  	</tr>
		
        <tr>
		<td colspan="2">
			<b>Opzioni</b>
		</td>
	  	</tr>
	 	<tr id="rowUtentiApplicativo" >
			<td class="etichetta-dato">Gestione utenti</td>
	 		<td class="valore-dato"> 
	 			<c:choose>
	 				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou11#") and not (fn:contains(listaOpzioniUtenteSys, "ou12#"))}'>
	 					${account.listaTextPrivilegi[2]}
	 				</c:when>
	 				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou12#")}'>
	 					${account.listaTextPrivilegi[1]}
	 				</c:when>
	 				<c:otherwise>
	 					${account.listaTextPrivilegi[0]}
	 				</c:otherwise>
	 			</c:choose>
			</td>
	  </tr>
	 	<tr id="rowParametriSistema" >
			<td class="etichetta-dato">Amministrazione di sistema</td>
	 		<td class="valore-dato">
	 			<c:choose>
	 				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou89#")}'>
	 					Si
	 				</c:when>
	 				<c:otherwise>
	 				  No
	 				</c:otherwise>
	 			</c:choose>
			</td>
	  </tr>
	 	<tr id="rowAbilitaFunzioniAvanzate" >
			<td class="etichetta-dato">Abilita funzioni avanzate men&ugrave; "Utilit&agrave;"</td>
	 		<td class="valore-dato">
	 			<c:choose>
	 				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou56#")}'>
	 					Si
	 				</c:when>
	 				<c:otherwise>
	 				  No
	 				</c:otherwise>
	 			</c:choose>
			</td>
	  </tr>
	  <% // si inserisce la configurazione solo se l'applicativo è abilitato ad almeno una delle sottovoci del menu' %>
	  <c:if test='${fn:contains(listaOpzioniDisponibili, "OP1#") ||fn:contains(listaOpzioniDisponibili, "OP2#")}'>
		<tr id="rowNascondiMenuStrumenti">
			<td class="etichetta-dato">Nascondi men&ugrave; "Strumenti"</td>
	   	<td class="valore-dato">
	   		<c:choose>
	   			<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou30#")}'>
						<c:out value="Si"/>									
					</c:when>
					<c:otherwise>
						<c:out value="No"/>									
					</c:otherwise>
				</c:choose>
			</td>
	  </tr>
	  </c:if>


<% // F.D. se non è abilitato il menù strumenti non si vedono le due opzioni %>
<c:if test='${!fn:contains(listaOpzioniUtenteSys, "ou30#")}'>		
	<c:if test='${fn:contains(listaOpzioniDisponibili, "OP1#")}'>
 		<tr id="rModelli">
			<td class="etichetta-dato" >Gestione modelli</td>
   		<td class="valore-dato">
	 			<c:choose>
	 				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou50#")}'>
	 					${account.listaTextGenmod[2]}
	 				</c:when>
	 				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou51#")}'>
	 					${account.listaTextGenmod[1]}
	 				</c:when>
	 				<c:otherwise>
	 					${account.listaTextGenmod[0]}
	 				</c:otherwise>
	 			</c:choose>
			</td>
   	</tr>
  </c:if>
 	<c:if test='${fn:contains(listaOpzioniDisponibili, "OP2#")}'>
   	<tr id="rReport">
			<td class="etichetta-dato" >Gestione report</td>
   		<td class="valore-dato"> 
	 			<c:choose>
	 				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou48#")}'>
	 					${account.listaTextGenric[2]}
	 				</c:when>
	 				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou49#")}'>
	 					${account.listaTextGenric[1]}<c:if test='${fn:contains(listaOpzioniUtenteSys, "ou53#") || fn:contains(listaOpzioniUtenteSys, "ou54#") || fn:contains(listaOpzioniUtenteSys, "ou55#")}'>:&nbsp;</c:if>
 						<c:if test='${fn:contains(listaOpzioniUtenteSys, "ou53#")}'>Base</c:if><c:if test='${fn:contains(listaOpzioniUtenteSys, "ou53#") && fn:contains(listaOpzioniUtenteSys, "ou54#")}'>, </c:if><c:if test='${fn:contains(listaOpzioniUtenteSys, "ou54#")}'>Avanzato</c:if><c:if test='${(fn:contains(listaOpzioniUtenteSys, "ou53#") || fn:contains(listaOpzioniUtenteSys, "ou54#")) && fn:contains(listaOpzioniUtenteSys, "ou55#")}'>, </c:if><c:if test='${fn:contains(listaOpzioniUtenteSys, "ou55#")}'>Modello</c:if>
	 				</c:when>
	 				<c:otherwise>
	 					${account.listaTextGenric[0]}
	 				</c:otherwise>
	 			</c:choose>
			</td>
   	</tr>	  
 	</c:if>	    	
</c:if>
<c:if test='${fn:contains(listaOpzioniDisponibili, "OP2#")}'>
	<tr id="rowSchedulazioni">
		<td class="etichetta-dato">Amministrazione schedulazioni di report</td>
		<td class="valore-dato">
			<c:choose>
 				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou62#")}'>
 					Si
 				</c:when>
 				<c:otherwise>
 				  No
 				</c:otherwise>
 			</c:choose>
		</td>
	</tr>
	</c:if>
	<%-- si inserisce la configurazione solo se l'applicativo è abilitato --%>
	<c:if test='${fn:contains(listaOpzioniDisponibili, "OP128#")}'>
	<tr id="rowNascondiMenuStrumenti">
		<td class="etichetta-dato">Amministrazione modelli di scadenzario</td>
		<td class="valore-dato">
			<c:choose>
				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou212#")}'>
					<c:out value="Si"/>									
				</c:when>
				<c:otherwise>
					<c:out value="No"/>									
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	</c:if>
 		<tr id="rowAbilitaInserimentoNote" >
			<td class="etichetta-dato">Abilita utente all'inserimento note</td>
			<td class="valore-dato">
   			<c:choose>
   				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou59#")}'>
						<c:out value="Si"/>
					</c:when>
					<c:otherwise>
						<c:out value="No"/>
					</c:otherwise>
				</c:choose>
			</td>
  	</tr>
  	<tr id="rowBloccaEditUffint" >
			<td class="etichetta-dato">Blocca utente nella modifica ${fn:toLowerCase(nomeEntitaParametrizzata)}</td>
			<td class="valore-dato">
   			<c:choose>
   				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou214#")}'>
						<c:out value="Si"/>
					</c:when>
					<c:otherwise>
						<c:out value="No"/>
					</c:otherwise>
				</c:choose>
			</td>
  	</tr>
  	<tr id="rowBloccaEliminazioneEntitaPrincipale" >
			<td class="etichetta-dato">Blocca eliminazione su entita principale</td>
			<td class="valore-dato">
   			<c:choose>
   				<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou215#")}'>
						<c:out value="Si"/>
					</c:when>
					<c:otherwise>
						<c:out value="No"/>
					</c:otherwise>
				</c:choose>
			</td>
  	</tr>
	  <tr>
		<td colspan="3">
			<b>Sicurezza</b>
		</td>
	  </tr>
	  <c:if test='${account.flagLdap == 0}'>
	  <tr id="rowScadenzaAccount" >
			<td class="etichetta-dato" >Scadenza Account</td>
      		<td class="valore-dato">
      		  <c:choose>
			  	<c:when test='${empty account.scadenzaAccount}'>
      		     Mai 
      		    </c:when>
	 			<c:otherwise>
				 Alla fine di: <c:out value="${account.scadenzaAccount}"/>
				</c:otherwise>	
			  </c:choose>
			</td>
	  </tr>
	  </c:if>
  	  <tr id="rowUtenteDisabilitato" >
			<td class="etichetta-dato">Utente disabilitato</td>
	 		<td class="valore-dato">
	 			<c:choose>
	 				<c:when test='${accountForm.utenteDisabilitato == "1"}'>
	 					Si
	 				</c:when>
	 				<c:otherwise>
	 				  	No
	 				</c:otherwise>
	 			</c:choose>
			</td>
	  </tr>
	  <c:if test='${account.flagLdap == 0}'>
	   	  <tr id="rSicurezza" >
				<td class="etichetta-dato" >Applica i controlli di sicurezza previsti dalle norme GDPR e AGID</td>
				<td class="valore-dato">
					<c:choose>
						<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou39#")}'>
							<c:out value="Si"/>
						</c:when>
						<c:otherwise>
							<c:out value="No"/>
		  					<div class="info-wizard">ATTENZIONE: rimuovere i controlli di sicurezza implica una non conformit&agrave; alle norme (GDPR, AGID, ...).</div>
						</c:otherwise>
					</c:choose>
				</td>
		  </tr>
	  </c:if>
	  
	  <c:set var="art80wsurl" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "art80.ws.url")}'/>
	  <c:if test="${!empty art80wsurl}">
		  <tr>
			<td colspan="3">
				<b>Verifiche Art.80</b>
			</td>
		  </tr>
		  <tr id="rowCreateArt80" >
				<td class="etichetta-dato" >Abilitato all'invio dei dati per richiedere una verifica</td>
	      		<td class="valore-dato">
	      			<c:choose>
						<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou226#")}'>
							<c:out value="Si"/>
						</c:when>
						<c:otherwise>
							<c:out value="No"/>
						</c:otherwise>
					</c:choose>
				</td>
		  </tr>		  
		  <tr id="rowReadArt80" >
				<td class="etichetta-dato" >Abilitato alla consultazione dei documenti</td>
	      		<td class="valore-dato">
	      			<c:choose>
						<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou225#")}'>
							<c:out value="Si"/>
						</c:when>
						<c:otherwise>
							<c:out value="No"/>
						</c:otherwise>
					</c:choose>
				</td>
		  </tr>
		</c:if>
		
		<c:if test='${fn:contains(listaOpzioniDisponibili, "OP133#")}'>
		  	<tr>
				<td colspan="3">
					<b>Verifiche interne Art.80</b>
				</td>
		  	</tr>
		  	<tr id="rowIntArt80" >
				<td class="etichetta-dato">Privilegi dell'utente sulle verifiche</td>
	      		<td class="valore-dato">
	      			<c:choose>
						<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou228#")}'>
							<c:out value="Accesso ai contenuti e operazioni sui documenti di ciascuna verifica."/>
						</c:when>
						<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou227#")}'>
	 						<c:out value="Accesso anche ai contenuti dei documenti di ciascuna verifica."/>
	 					</c:when>
						<c:otherwise>
							<c:out value="Accesso (in sola lettura) per verificare esiti delle verifiche e scadenze dei documenti"/>
						</c:otherwise>
					</c:choose>
				</td>
		  	</tr>
	 	</c:if>
		
	  
  	<jsp:include page="/WEB-INF/pages/gene/admin/sottoSezioniDatiGen.jsp" />
<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>
 	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Scheda.DATIGEN.SCHEDAMOD")}' >
		<tr>
     	<td class="comandi-dettaglio" colSpan="2">
     		<INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica Utente" onclick="javascript:schedaModifica('<c:out value='${account.idAccount}' />')">
        &nbsp;
			</td>
    </tr>
	</c:if>	    
</c:if>		    
	</table>