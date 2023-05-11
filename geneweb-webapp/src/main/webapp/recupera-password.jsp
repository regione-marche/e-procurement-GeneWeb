<%/*
       * Created on 17-Dic-2014
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="scheda-nomenu-template.jsp">
<gene:javaScript>
	function gestioneAction() {	
	}
</gene:javaScript>

	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.realperson.min.js"></script>
		<link rel="STYLESHEET" type="text/css" href="${contextPath}/css/jquery/jquery.realperson.css">
		<script type="text/javascript">
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />

  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    //hideSubmenuNavbar();
    hideMenuPopup();
    hideSubmenuNavbar();
  }
  
	function annullaScheda(){
		window.location.href = "${pageContext.request.contextPath}/InitLogin.do";
	}
	
  function invia() {
    var esito = true;

    if (esito && !controllaCampoInputObbligatorio(recuperaPasswordForm.username, 'Username')){
      esito = false;
    }

    if (esito && !controllaCampoInputObbligatorio(recuperaPasswordForm.captcha, 'Codice di controllo')){
        esito = false;
    }
	
    if (esito){
      bloccaRichiesteServer();
      document.recuperaPasswordForm.submit();
    }
  }
  
	
	
	// connette il plugin per la gestione captcha al campo effettivo
	$(document).ready(function(){
		$("#captcha").realperson({regenerate: 'Cambia immagine'});
		$("#captcha").css("text-transform","uppercase");
	});
  
-->
</script>

	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value='Hai dimenticato la password?'/>
	
	<gene:redefineInsert name="corpo">
		<html:form  action="/RecuperaPassword">
		<table class="dettaglio-notab">
			<tr>
            	<td colspan="2"><br><b>Inserisci qui sotto il tuo username</b></td>
            </tr>
			<tr>
            	<td class="etichetta-dato">Username (*)</td>
                <td align="valore-dato"><html:text property="username" styleClass="testo" size="15" maxlength="60"/></td>
            </tr>
            <tr>
            	<td colspan="2"><br><b>Dimostra di non essere un robot</b></td>
            </tr>

			<tr>
				<td class="etichetta-dato">Codice di controllo (*)</td>
				<td class="valore-dato">
					<html:text property="captcha" size="40" styleClass="testo" styleId="captcha" />
				</td>
			</tr>
			
			<tr>
				<td colSpan="2">
					<i>
					<br>(*) Campi obbligatori
					</i>
				</td>
			</tr>
			
			<tr>
                <td class="comandi-dettaglio" colSpan="2">
                	  <INPUT type="button" class="bottone-azione" value="Inoltra richiesta" title="Inoltra richiesta" onClick="javascript:invia();" />
					  <INPUT type="button" class="bottone-azione" value="Annulla richiesta" title="Annulla richiesta" onclick="javascript:annullaScheda()">
                </td>
            </tr>
				
		</table>
		</html:form>

	</gene:redefineInsert>
</gene:template>