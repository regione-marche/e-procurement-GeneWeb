<%/*
       * Created on 23/09/2014
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
<c:set var="metodo" value="${metodo}" />

<gene:template file="scheda-nomenu-template.jsp">
	<gene:redefineInsert name="head">
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.realperson.min.js"></script>

	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/ui/std/jquery-ui.css" />
	
	<script type="text/javascript">
	<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />
	function gestioneAction() {	
	}
  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    //hideSubmenuNavbar();
    hideMenuPopup();
    hideSubmenuNavbar();
  }
  
	function annullaScheda(){
		window.location.href = "${pageContext.request.contextPath}/Logout.do";
	}
	
	function gestisciSubmit(){
		var esito = true;
		
		if(document.getElementById("autenticazione_mtoken").checked){
			document.adminAccessForm.metodo.value = "mtoken";
			if (esito && !controllaCampoInputObbligatorio(adminAccessForm.motivazione, 'motivazione')){
			  esito = false;
			}
			if (esito &&  (trim(adminAccessForm.certificatoText) == "" && trim(adminAccessForm.selezioneFile) == "")){
				alert("Inserisci il certificato");
				esito = false;
			}
		}else{
			document.adminAccessForm.metodo.value = "portoken";
			if (esito && !controllaCampoInputObbligatorio(adminAccessForm.motivazione, 'motivazione')){
				esito = false;
			}
			if (esito &&  !controllaCampoInputObbligatorio(adminAccessForm.emailDominio, 'email dominio')){
				esito = false;
			}
			if (esito &&  !controllaCampoInputObbligatorio(adminAccessForm.emailDominio, 'password dominio')){
				esito = false;
			}
		}
		if(esito){
			bloccaRichiesteServer();
			document.adminAccessForm.submit();
		}
	}
  
	function cambiaTipoAutenticazione(index){
		if(index == 2){
			$("#portokencontainer").show();
			$("#mtokencontainer").hide();
		}else{
			$("#portokencontainer").hide();
			$("#mtokencontainer").show();
		}
		
	}
	<c:if test="${metodo eq 'portoken'}">
	$(window).load(function(){
		cambiaTipoAutenticazione(2);
	}
		
	);
	
	</c:if>
  
-->
</script>
		
		<style type="text/css">
			
			.required-field {
				color: red;
			}
			#portokencontainer{
				display: none;
			}
		</style>
		
	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value='Accesso utente Admin'/>

	<gene:redefineInsert name="corpo">

		<html:form  action="/AdminAccess" method="post" enctype="multipart/form-data" >
		
			<TABLE class="dettaglio-notab">
				<legend>
					<span class="noscreen"></span>
				</legend>
				
				<TR>
					<TD colspan="2">
						<input type="radio" value="1" name="autenticazione" id="autenticazione_mtoken" <c:if test="${metodo eq null or metodo eq 'mtoken'}"> checked="checked"</c:if> onclick="javascript:cambiaTipoAutenticazione(1);" />
						 Autenticazione M-token
						&nbsp;&nbsp;
						<input type="radio" value="2" name="autenticazione" id="autenticazione_portoken" <c:if test="${metodo eq 'portoken'}"> checked="checked"</c:if> onclick="javascript:cambiaTipoAutenticazione(2);" />
						 Autenticazione Portoken
						 &nbsp;&nbsp;
						<!--
						<input type="radio" value="3" name="allegato_${param.contatore}" id="doc_${param.contatore}" onclick="javascript:cambiaTipoAutenticazione(3);" />
						 Autenticazione SPID-->
					</TD>
				</TR>
				<tbody id="mtokencontainer">
					<TR>
						<TD colspan="2">
						<br>
							<b>Autenticazione M-token</b>
							<br>
						</TD>
					</TR>
					<TR>
						<TD class="etichetta-dato">
							Carica il file del certificato : 
						</TD>
						<TD class="valore-dato">
							<html:file property="selezioneFile"  styleClass="file" size="50" onkeydown="return bloccaCaratteriDaTastiera(event);"/>
						</TD>
					</TR>
					<TR>
						<TD class="etichetta-dato">
							Contenuto del certificato :
						</TD>
						<TD class="valore-dato">
							
							<html:textarea property="certificatoText" styleClass="testo" cols="60" rows="20"/>
						</TD>
					</TR>
					</tbody>
					
					<tbody id="portokencontainer" >
						<TR>
							<TD colspan="2">
							<br>
								<b>Autenticazione Portoken</b>
								<br>
							</TD>
						</TR>
						<TR>
							<TD class="etichetta-dato">
								 Email di dominio : <span class="required-field">*</span>
							</TD>
							<TD class="valore-dato">
								<html:text property="emailDominio" styleClass="testo" size="25" maxlength="60" value=""/> @maggioli.it
							</TD>
						</TR>
						<TR>
							<TD class="etichetta-dato">
								 Password di dominio : <span class="required-field">*</span>
							</TD>
							<TD class="valore-dato">
								<html:password property="passwordDominio" styleClass="testo" size="25" maxlength="60"/>
							</TD>
						</TR>
					</tbody>
					
					<TR>
						<TD class="etichetta-dato">
							 Ticket HDA / Motivazione : <span class="required-field">*</span>
						</TD>
						<TD class="valore-dato">
							<html:text property="motivazione" styleClass="testo" size="25" maxlength="60"/>
						</TD>
					</TR>
				<html:hidden property="metodo" styleClass="testo" value="1"/>

				<TR>
					<TD class="comandi-dettaglio" colSpan="2">
						<INPUT type="button" class="bottone-azione" value="Conferma" onclick="javascript:gestisciSubmit();">
						&nbsp;
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaScheda();" >
					&nbsp;
					</TD>
				</TR>
			</TABLE>
			
			
			</html:form>
	
	</gene:redefineInsert>
</gene:template>