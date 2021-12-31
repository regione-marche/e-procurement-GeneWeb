<%/*
   * Created on 24-lug-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI VISUALIZZAZIONE
  // DEL DETTAGLIO DI UN DOCUMENTO ASSOCIATO RELATIVA AI DATI EFFETTIVI
%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<tiles:insert definition=".dettaglioNoTabDef" flush="true">

	<tiles:put name="head" type="string">
<script type="text/javascript">
<!--

// Azioni di pagina

function modifica(){
	document.location.href='ConfigurazioneLdap.do?'+csrfToken+'&metodo=modifica';
}

function modificaPassword(){
	document.location.href='ConfigurazioneLdap.do?'+csrfToken+'&metodo=modificaPassword';
}

function verifica(){
	document.location.href='ConfigurazioneLdap.do?'+csrfToken+'&metodo=verificaConnessione';
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
		<gene:historyAdd titolo='Dettaglio server ldap' id="scheda" />
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
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:modificaPassword();" tabindex="1502" title="Modifica la password">Imposta password</a>
			</td>
		</tr>	
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:verifica();" tabindex="1503" title="Verifica la configurazione tentando la connessione al server">Test connessione</a>
			</td>
		</tr>
		<tr>
			<td>
				&nbsp;
			</td>
		</tr>
		<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />	
	</tiles:put>

	<tiles:put name="titoloMaschera" type="string" value="Configurazione accesso al server LDAP" />
	 
	<tiles:put name="dettaglio" type="string">

<table class="dettaglio-notab">
  <tr>
    <td class="etichetta-dato">Nome server</td>
    <td class="valore-dato">${cfgLdapForm.server}
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Porta</td>
    <td class="valore-dato">${cfgLdapForm.porta}</td>
  </tr>
  <tr>
    <td class="etichetta-dato">Percorso base di ricerca utenti</td>
    <td class="valore-dato">${cfgLdapForm.base}</td>
  </tr>
  <tr>
    <td class="etichetta-dato">DN utente di accesso LDAP</td>
    <td class="valore-dato">${cfgLdapForm.dn}</td>
  </tr>
  <tr>
    <td class="etichetta-dato">Password</td>
    <td class="valore-dato">
		<c:choose>
			<c:when test='${!empty cfgLdapForm.password}'>
				<c:out value="Impostata"/>
			</c:when>
			<c:otherwise>
				<c:out value="Non impostata"/>
			</c:otherwise>
		</c:choose>
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Filtro utenti</td>
    <td class="valore-dato">${cfgLdapForm.filtroUtenti}</td>
  </tr>
  <tr>
    <td class="etichetta-dato">Filtro unit&agrave; organizzative</td>
    <td class="valore-dato">${cfgLdapForm.filtroOU}</td>
  </tr>
  <tr>
    <td class="etichetta-dato">Attributo login</td>
    <td class="valore-dato">${cfgLdapForm.attributoLogin}</td>
  </tr>
  <tr>
    <td class="etichetta-dato">Attributo nome</td>
    <td class="valore-dato">${cfgLdapForm.attributoNome}</td>
  </tr>
  <tr>
    <td class="comandi-dettaglio" colSpan="2">
      	<INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica la configurazione" onclick="javascript:modifica();">
      	<INPUT type="button" class="bottone-azione" value="Imposta password" title="Imposta la password" onclick="javascript:modificaPassword();">
    	<INPUT type="button" class="bottone-azione" value="Test connessione" title="Verifica la configurazione tentando la connessione al server" onclick="javascript:verifica();">
      &nbsp;
    </td>
  </tr>
</table>
	</tiles:put>

</tiles:insert>
