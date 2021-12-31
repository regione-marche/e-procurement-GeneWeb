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

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI EDIT
  // DEL DETTAGLIO DI UN DOCUMENTO ASSOCIATO RELATIVA AI DATI EFFETTIVI
%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>


<tiles:insert definition=".dettaglioNoTabDef" flush="true">

	<tiles:put name="head" type="string">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">
<!--

	// Azioni di pagina

	function gestisciSubmit(){
		var continua = true;
		//Controllo del titolo del documento associato
		if(continua && !controllaCampoInputObbligatorio(document.cfgLdapForm.server, 'Nome server')){
			continua = false;
		}
	
		if(continua && !controllaCampoInputObbligatorio(document.cfgLdapForm.porta, 'Porta')){
			continua = false;
		}

		if(continua && !controllaCampoInputObbligatorio(document.cfgLdapForm.base, 'Percorso base di ricerca utenti')){
			continua = false;
		}

		if(continua && !controllaCampoInputObbligatorio(document.cfgLdapForm.dn, 'DN utente accesso LDAP')){
			continua = false;
		}

		if(continua && !controllaCampoInputObbligatorio(document.cfgLdapForm.filtroUtenti, 'Filtro utenti')){
			continua = false;
		}
		
		if(continua && !controllaCampoInputObbligatorio(document.cfgLdapForm.attributoNome, 'Attributo nome')){
			continua = false;
		}
		
		if(continua && !controllaCampoInputObbligatorio(document.cfgLdapForm.attributoLogin, 'Attributo login')){
			continua = false;
		}
		
		if(continua)
			document.cfgLdapForm.submit();
	}

function annulla(){
	document.location.href='ConfigurazioneLdap.do?'+csrfToken+'&metodo=visualizza';
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
</gene:template>
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:gestisciSubmit();" tabindex="1502" title="Salva">Salva</a>
			</td>
		</tr>		
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annulla();" tabindex="1503" title="Annulla">Annulla</a>
			</td>
		</tr>
	  <tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
	</tiles:put>

	<tiles:put name="titoloMaschera" type="string" value="Configurazione accesso al server LDAP" />
	 
	<tiles:put name="dettaglio" type="string">
<html:form action="/SalvaConfigurazioneLdap">
	<html:hidden property="metodo" value="salva" name="cfgLdapForm"/>
	<table class="dettaglio-notab">
	  <tr>
	    <td class="etichetta-dato">Nome server (*)</td>
	    <td class="valore-dato">
	    	<html:text property="server" size="20" maxlength="500"/>
	    </td>
	  </tr>
	  <tr>
	    <td class="etichetta-dato">Porta (*)</td>
	    <td class="valore-dato">
	    	<html:text property="porta" size="10" maxlength="10"/></td>
	  </tr>
	
	  <tr>
	    <td class="etichetta-dato">Percorso base di ricerca utenti (*)</td>
	    <td class="valore-dato">
	    	<html:text property="base" size="60" maxlength="500"/></td>
	  </tr>
	
	  <tr>
	    <td class="etichetta-dato">DN utente accesso LDAP (*)</td>
	    <td class="valore-dato">
	    	<html:text property="dn" size="60" maxlength="500"/></td>
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
	    <td class="etichetta-dato">Filtro utenti (*)</td>
	    <td class="valore-dato">
	    	<html:text property="filtroUtenti" size="80" maxlength="500"/></td>
	  </tr>
	  
	  <tr>
	    <td class="etichetta-dato">Filtro unit&agrave; organizzative</td>
	    <td class="valore-dato">
	    	<html:text property="filtroOU" size="80" maxlength="500"/></td>
	  </tr>

	  <tr>
	    <td class="etichetta-dato">Attributo login (*)</td>
	    <td class="valore-dato">
	    	<html:text property="attributoLogin" size="20" maxlength="100"/></td>
	  </tr>
	  
	  <tr>
	    <td class="etichetta-dato">Attributo nome (*)</td>
	    <td class="valore-dato">
	    	<html:text property="attributoNome" size="20" maxlength="100"/></td>
	  </tr>
	  	  
	  <tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      	<INPUT type="button" class="bottone-azione" value="Salva" title="Salva" onclick="javascript:gestisciSubmit();">
        	<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
        &nbsp;
	    </td>
	  </tr>
	</table>
</html:form>
	</tiles:put>

</tiles:insert>
