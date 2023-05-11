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

  // PAGINA CHE CONTIENE IL DETTAGLIO DELLA CONFIGURAZIONE DEI PARAMETRI DI POSTA
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
	function visualizza(type){
		document.location.href='ConfigurazioneMail.do?'+csrfToken+'&metodo=visualizza&idcfg='+idcfg;
	}

	function verifica(idcfg){
		document.location.href='ConfigurazioneMail.do?'+csrfToken+'&metodo=apriVerificaConfigurazione&idcfg='+idcfg;
	}

	function modifica(idcfg){
		document.location.href='ConfigurazioneMail.do?'+csrfToken+'&metodo=modifica&idcfg='+idcfg;
	}

	function modificaPassword(idcfg){
		document.location.href='ConfigurazioneMail.do?'+csrfToken+'&metodo=modificaPassword&idcfg='+idcfg;
	}

	function sincronizza(idcfg){
		if (confirm("Vuoi procedere con la sincronizzazione della presente configurazione sul portale?"))
			document.location.href='SincronizzaConfigurazioneMailPortale.do?'+csrfToken+'&idcfg='+idcfg;
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
				<!--gene-:-historyClear/-->
				<gene:historyAdd titolo='Dettaglio configurazione mail' id="scheda" />
			</gene:insert>
		</gene:template>
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:modifica('${cfgMailForm.idcfg}');" tabindex="1501" title="Modifica la configurazione">Modifica</a>
			</td>
		</tr>	
	<c:if test='${!empty cfgMailForm.server}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:modificaPassword('${cfgMailForm.idcfg}');" tabindex="1502" title="Modifica la password">Imposta password</a>
			</td>
		</tr>	
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:verifica('${cfgMailForm.idcfg}');" tabindex="1503" title="Prova l'invio di una mail ad un destinatario">Invia mail di prova</a>
			</td>
		</tr>
	</c:if>
	<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") && !empty cfgMailForm.server && cfgMailForm.idcfg eq "STD"}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:sincronizza('${cfgMailForm.idcfg}');" tabindex="1504" title="PAggiorna la configurazione di posta sul portale con la presente">Sincronizza sul portale</a>
			</td>
		</tr>
	</c:if>
  
		<tr>
			<td>
				&nbsp;
			</td>
		</tr>
    <jsp:include page="/WEB-INF/pages/commons/torna.jsp" />	

	</tiles:put>

	<tiles:put name="titoloMaschera" type="string" value="Configurazione server di posta" />
	 
	<tiles:put name="dettaglio" type="string">
		<table class="dettaglio-notab">
		  <tr>
		    <td class="etichetta-dato">Configurazione</td>
			<td class="valore-dato">${cfgMailForm.nomein}</td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Server</td>
		    <td class="valore-dato">${cfgMailForm.server}
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Porta</td>
		    <td class="valore-dato">
		    	<c:if test='${!empty cfgMailForm.server}'>
			    	<c:choose>
				    	<c:when test='${!empty cfgMailForm.porta}'>
				    	${cfgMailForm.porta}
				    	</c:when>
				    	<c:otherwise>
				    	25 (default)
				    	</c:otherwise>
			    	</c:choose>
			    </c:if>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Protocollo</td>
		    <td class="valore-dato">
				<c:choose>
					<c:when test='${cfgMailForm.protocollo eq 0}'>SMTP</c:when>
					<c:when test='${cfgMailForm.protocollo eq 1}'>SMTP + SSL (SMTPS)</c:when>
					<c:when test='${cfgMailForm.protocollo eq 2}'>SMTP + STARTTLS</c:when>
				</c:choose>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Server IMAP</td>
		    <td class="valore-dato">${cfgMailForm.serverIMAP}
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Porta IMAP</td>
		    <td class="valore-dato">${cfgMailForm.portaIMAP}</td>
		  </tr>
		  <% /*
          <tr>
		  	<td class="etichetta-dato">Protocollo IMAP</td>
		  	<td class="valore-dato">
		  		<c:choose>
		  			<c:when test='${cfgMailForm.protocolloIMAP eq 0}'>Standard</c:when>
		  			<c:when test='${cfgMailForm.protocolloIMAP eq 1}'>SSL</c:when>
		  		</c:choose>
		  	</td>
          </tr>
          */ %>
		  <tr>
		    <td class="etichetta-dato">Timeout attesa server (in millisecondi)</td>
		    <td class="valore-dato">
			    <c:if test='${!empty cfgMailForm.server}'>
					<c:choose>
						<c:when test='${empty cfgMailForm.timeout}'>5000 (default)</c:when>
						<c:when test='${cfgMailForm.timeout <= 0}'>Disabilitato</c:when>
						<c:otherwise>${cfgMailForm.timeout}</c:otherwise>
					</c:choose>
				</c:if>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Delay tra invii ripetuti di email (in millisecondi)</td>
		    <td class="valore-dato">
			    <c:if test='${!empty cfgMailForm.server}'>
			    	<c:out value="${cfgMailForm.delay}" />
				</c:if>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Attiva tracciatura messaggi nel log</td>
		    <td class="valore-dato">
			    <c:if test='${!empty cfgMailForm.server}'>
					<c:choose>
						<c:when test='${cfgMailForm.debug}'>SI</c:when>
						<c:otherwise>NO</c:otherwise>
					</c:choose>
				</c:if>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Mail mittente</td>
		    <td class="valore-dato">${cfgMailForm.mail}</td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Password</td>
		    <td class="valore-dato">
		    	<c:if test='${!empty cfgMailForm.server}'>
					<c:choose>
						<c:when test='${!empty cfgMailForm.password}'>
							<c:out value="Impostata"/>
						</c:when>
						<c:otherwise>
							<c:out value="Non impostata"/>
						</c:otherwise>
					</c:choose>
			    </c:if>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Id di autenticazione (se diverso dalla mail mittente)</td>
		    <td class="valore-dato">${cfgMailForm.userId}</td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Limite dimensione totale allegati (in MB)</td>
		    <td class="valore-dato">${cfgMailForm.maxMb}</td>
		  </tr>
		  <tr>
		    <td class="comandi-dettaglio" colSpan="2">
		      	<INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica la configurazione della posta" onclick="javascript:modifica('${cfgMailForm.idcfg}');">
				<c:if test='${!empty cfgMailForm.server}'>
		      	<INPUT type="button" class="bottone-azione" value="Imposta password" title="Imposta o sbianca la password" onclick="javascript:modificaPassword('${cfgMailForm.idcfg}');">
		    	<INPUT type="button" class="bottone-azione" value="Invia mail di prova" title="Prova l'invio di una mail ad un destinatario" onclick="javascript:verifica('${cfgMailForm.idcfg}');">
		    	</c:if>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") && !empty cfgMailForm.server && cfgMailForm.idcfg eq "STD"}'>
		    	<INPUT type="button" class="bottone-azione" value="Sincronizza sul portale" title="Aggiorna la configurazione di posta sul portale con la presente" onclick="javascript:sincronizza('${cfgMailForm.idcfg}');">
		    	</c:if>
		      &nbsp;
		    </td>
		  </tr>
		</table>
	</tiles:put>

</tiles:insert>
