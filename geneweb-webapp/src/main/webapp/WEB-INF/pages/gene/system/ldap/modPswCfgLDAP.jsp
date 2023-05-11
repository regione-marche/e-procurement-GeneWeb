<%/*
   * Created on 3-feb-2012
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE IL DETTAGLIO DELLA CONFIGURAZIONE DEI PARAMETRI LDAP
%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<tiles:insert definition=".dettaglioNoTabDef" flush="true">

	<tiles:put name="head" type="string">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">
<!--
function gestisciSubmit(){
	var continua = true;
	if(continua && document.cfgLDAPForm.password.value != document.cfgLDAPForm.newPassword.value) {
		alert("Nuova password e conferma nuova password non coincidono");
		continua = false;
	}
	if(continua && document.cfgLDAPForm.password.value == document.cfgLDAPForm.oldPassword.value) {
		alert("Nuova password e vecchia password non possono essere uguali");
		continua = false;
	}
	if(continua)
		document.cfgLDAPForm.submit();
}

function annulla(){
	document.location.href='ConfigurazioneLdap.do?'+csrfToken+'&metodo=visualizza';
}

-->
</script>	
	</tiles:put>

	<tiles:put name="azioniContesto" type="string">
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:gestisciSubmit();" tabindex="1501" title="Salva">Salva</a>
			</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annulla();" tabindex="1502" title="Annulla">Annulla</a>
			</td>
		</tr>	
	</tiles:put>

	<tiles:put name="titoloMaschera" type="string" value="Configurazione accesso al server LDAP: impostazione password" />
	 
	<tiles:put name="dettaglio" type="string">
		<form name="cfgLDAPForm" action="${pageContext.request.contextPath}/ImpostaPasswordConfigurazioneLdap.do" method="post">
		<table class="dettaglio-notab">
		  <tr>
		    <td class="etichetta-dato">Vecchia password</td>
		    <td class="valore-dato">
		    	<input type="password" name="oldPassword" size="20" maxlength="40"/>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Nuova password</td>
		    <td class="valore-dato">
		    	<input type="password" name="password" size="20" maxlength="40"/>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Conferma nuova password</td>
		    <td class="valore-dato">
		    	<input type="password" name="newPassword" size="20" maxlength="40"/>
		    </td>
		  </tr>
		  <tr>
		    <td class="comandi-dettaglio" colSpan="2">
		      	<INPUT type="button" class="bottone-azione" value="Salva" title="Salva" onclick="javascript:gestisciSubmit();">
	        	<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
		      &nbsp;
		    </td>
		  </tr>
		</table>
		</form>
	</tiles:put>

</tiles:insert>
