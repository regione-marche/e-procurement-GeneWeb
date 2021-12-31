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

  // PAGINA CHE CONTIENE LA MODIFICA DELLA CONFIGURAZIONE DEI PARAMETRI DI POSTA
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
	if(continua && !controllaCampoInputObbligatorio(document.cfgMailForm.idcfg, 'Configurazione')){
		continua = false;
	}
	if(continua && !controllaCampoInputObbligatorio(document.cfgMailForm.server, 'Server')){
		continua = false;
	}
	if(continua && !isIntero(document.cfgMailForm.porta.value, false)){
		alert("Il campo 'Porta' prevede un valore intero");
		document.cfgMailForm.porta.focus();
	    continua = false;
	}
	if(continua && !isIntero(document.cfgMailForm.timeout.value, true, false)){
		alert("Il campo 'Timeout attesa server (in millisecondi)' prevede un valore intero");
		document.cfgMailForm.timeout.focus();
	    continua = false;
	}
	if(continua && !isIntero(document.cfgMailForm.delay.value, true, false)){
		alert("Il campo 'Delay tra invii ripetuti di email (in millisecondi)' prevede un valore intero");
		document.cfgMailForm.delay.focus();
	    continua = false;
	}
	if(continua && !controllaCampoInputObbligatorio(document.cfgMailForm.mail, 'Mail mittente')){
		continua = false;
	}
	if(continua && !isEmailFormatoValido(document.cfgMailForm.mail)){
		continua = false;
	}
	if(continua && !isIntero(document.cfgMailForm.maxMb.value,false)){
		alert("Il campo 'Limite dimensione totale allegati' prevede un valore intero");
		document.cfgMailForm.maxMb.focus();
		continua = false;
	}
		
	if(continua)
		document.cfgMailForm.submit();
}

function annulla(){
	if(document.getElementById('idcfgPar')!=null && document.getElementById('idcfgPar').value != ''){
		document.location.href='ConfigurazioneMail.do?'+csrfToken+'&metodo=visualizza&idcfg=${param.idcfg}';
	}else{
		document.location.href='ListaConfigurazioniMail.do?'+csrfToken+'&metodo=lista';
	}
	
	
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
		<tr>
			<td>
				&nbsp;
			</td>
		</tr>
    <jsp:include page="/WEB-INF/pages/commons/torna.jsp" />	
	</tiles:put>

	<tiles:put name="titoloMaschera" type="string" value="Configurazione server di posta" />
	 
	<tiles:put name="dettaglio" type="string">
		<html:form action="/SalvaConfigurazioneMail">
		<html:hidden property="codapp" value = "${cfgMailForm.codapp}"/>
		<table class="dettaglio-notab">
		  <tr>
		    <td class="etichetta-dato">Configurazione (*)</td>
			<td class="valore-dato">
		    	<c:choose>
			    	<c:when test='${!empty cfgMailForm.idcfg}'>
			    		<html:hidden property="idcfg" value = "${cfgMailForm.idcfg}"/>
			    		${cfgMailForm.nomein}
			    	</c:when>
			    	<c:otherwise>
				      	<html:select property="idcfg" >
			    	  		<html:option value="">&nbsp;</html:option>
				    	  	<html:options collection="listaStazioniAppaltanti" property="tipoTabellato" labelProperty="descTabellato" />
			      		</html:select>
			    	</c:otherwise>
		    	</c:choose>
		  	</td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Server (*)</td>
		    <td class="valore-dato">
		    	<html:text property="server" size="20" maxlength="40"/>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Porta</td>
		    <td class="valore-dato">
		    	<html:text property="porta" size="10" maxlength="10"/> (vale 25 se non valorizzato)
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Protocollo</td>
		    <td class="valore-dato">
				<select name="protocollo">
					<option value="0" <c:if test="${cfgMailForm.protocollo eq '0'}">selected="selected"</c:if>>SMTP</option>
					<option value="1" <c:if test="${cfgMailForm.protocollo eq '1'}">selected="selected"</c:if>>SMTP + SSL (SMTPS)</option>
					<option value="2" <c:if test="${cfgMailForm.protocollo eq '2'}">selected="selected"</c:if>>SMTP + STARTTLS</option>
				</select>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Timeout attesa server (in millisecondi)</td>
		    <td class="valore-dato">
		    	<html:text property="timeout" size="10" maxlength="6"/> (vale 5000 se non valorizzato, inserire un valore negativo per disabilitarlo)
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Delay tra invii ripetuti di email (in millisecondi)</td>
		    <td class="valore-dato">
		    	<html:text property="delay" size="10" maxlength="6"/>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Attiva tracciatura messaggi nel log</td>
		    <td class="valore-dato">
		    	<input type="checkbox" name="debug" value="1" <c:if test='${cfgMailForm.debug}'>checked="true"</c:if> />
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Mail mittente (*)</td>
		    <td class="valore-dato">
		    	<html:text property="mail" size="60" maxlength="100"/>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Password</td>
		    <td class="valore-dato">
				<c:choose>
					<c:when test='${!empty cfgMailForm.password}'>
						<c:out value="Impostata"/>
					</c:when>
					<c:otherwise>
						<c:out value="Non impostata"/>
					</c:otherwise>
				</c:choose>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Id di autenticazione (se diverso dalla mail mittente)</td>
		    <td class="valore-dato">
		    	<html:text property="userId" size="60" maxlength="100"/>
		    </td>
		  </tr>
		  <tr>
		    <td class="etichetta-dato">Limite dimensione totale allegati (in MB)</td>
		    <td class="valore-dato">
		    	<html:text property="maxMb" size="10" maxlength="10"/>
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
		<input type="hidden" name="idcfgPar" id="idcfgPar" value="${param.idcfg}">
		</html:form>
	</tiles:put>

</tiles:insert>
