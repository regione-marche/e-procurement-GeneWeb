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

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<fmt:setBundle basename="AliceResources" />
<c:set var="nomeEntitaParametrizzata">
	<fmt:message key="label.tags.uffint.multiplo" />
</c:set>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="mioAccount" value="${requestScope.mioAccountForm}"/>

<html:form action="/SalvaMioAccount">
	<input type="hidden" name="metodo" value="${metodo}"/>
	<html:hidden property="idAccount" name="mioAccountForm"/>
	<html:hidden property="login" name="mioAccountForm"/>
	<table  class="dettaglio-tab">
		<tr>
			<td colspan="2">
				<b>Dati generali</b>
			</td>
		</tr>
		<tr id="rowDescrizioneUtente" >
			<td class="etichetta-dato">Descrizione (*)</td>
     		<td class="valore-dato">
					<html:text property="nome" size="40" maxlength="161" name="mioAccountForm"/>
			</td>
   		</tr>
		<tr id="rowNomeUtente" >
			<td class="etichetta-dato">Nome utente</td>
     		<td class="valore-dato"> 
     			<c:out value="${mioAccount.login}"/>
			</td>
   		</tr>

   		<tr id="rowEmail" >
			<td class="etichetta-dato">E-Mail</td>
   			<td class="valore-dato">
	 			<html:text property="email" name="mioAccountForm" maxlength="50" size="50"/>
			</td>
   		</tr>
   		<tr id="rowCodfisc" >
			<td class="etichetta-dato">Codice fiscale</td>
   			<td class="valore-dato">
	 			<html:text property="codfisc" name="mioAccountForm" maxlength="18" size="18"/>
			</td>
   		</tr>
  		<jsp:include page="/WEB-INF/pages/gene/admin/sottoSezioniMioAccount.jsp" />
		<tr>
   			<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Conferma delle modifiche" onClick="javascript:schedaSalva();" />
					<Input type="button" class="bottone-azione" value="Annulla" onClick="javascript:schedaAnnulla();">
		        &nbsp;
	      	</td>
	    </tr>
	</table>
</html:form>
<script type="text/javascript">
<!--

-->
</script>