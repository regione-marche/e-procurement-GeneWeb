<%/*
       * Created on 07-Nov-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
%>
<% //Inserisco la Tag Library %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<!-- Dati anagrafica utente -->
<gene:template file="scheda-nomenu-template.jsp">
<gene:javaScript>
	function gestioneAction() {
		var nuovaAction = contextPath + "/SchedaNoSessione.do";
		document.forms[0].action = nuovaAction;
		nuovaAction = "commons/redirect.jsp";
		document.forms[0].jspPathTo.value = nuovaAction;
	}
	
</gene:javaScript>
	
	<% //Settaggio delle stringhe utilizate nel template %>
	<gene:setString name="titoloMaschera" value='Registrazione Professionista'/>
	
	<gene:redefineInsert name="corpo">

										
<gene:formScheda entita="UTENT" gestore="it.eldasoft.gene.web.struts.registrazione.GestoreUtentRegistrazione" >
	<gene:campoScheda title="Codice dell'anagrafico" campo="CODUTE"  
		modificabile='${modoAperturaScheda eq "NUOVO"}' visibile="false" />
	<gene:campoScheda campo="TPPERS" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoPersona" obbligatorio="true"/>
	<gene:campoScheda campo="COGUTE" title="Cognome"/>
	<gene:campoScheda campo="NOMEUTE" title="Nome"/>
		
	<gene:campoScheda campo="NOMUTE" obbligatorio="true">
		<gene:calcoloCampoScheda 
			funzione='( ( "#UTENT_NOMUTE#"=="" || "#UTENT_NOMUTE#"==" #UTENT_NOMEUTE#" || "#UTENT_NOMUTE#"=="#UTENT_COGUTE#" || "#UTENT_NOMUTE#"=="#UTENT_COGUTE# ") ? "#UTENT_COGUTE# #UTENT_NOMEUTE#" : "#UTENT_NOMUTE#")' 
			elencocampi="UTENT_COGUTE;UTENT_NOMEUTE"/>
	</gene:campoScheda>
	
	<gene:campoScheda campo="CFUTE" title="Codice fiscale (**)">
		<gene:checkCampoScheda funzione='"##".length==16 || "##".length==0' obbligatorio="false" messaggio='Il codice fiscale deve essere di 16 caratteri !' />
	</gene:campoScheda>
	<gene:campoScheda campo="PIVAUTE" title="Partita I.V.A. (**)" >
		<gene:checkCampoScheda funzione='"##".length==11 || "##".length==0' obbligatorio="false" messaggio='La partita IVA  deve essere di 11 caratteri !' />
	</gene:campoScheda>
	<gene:campoScheda campo="NAZUTE"/>
	<gene:campoScheda campo="INDUTE" obbligatorio="true"/>
	<gene:campoScheda campo="NCIUTE" obbligatorio="true"/>
	<gene:campoScheda campo="CAPUTE" obbligatorio="true"/>
	<gene:campoScheda campo="LOCUTE" obbligatorio="true"/>
	<gene:campoScheda campo="PROUTE" obbligatorio="true"/>
	<gene:campoScheda campo="TELUTE" />
	<gene:campoScheda campo="FAXUTE" />
	<gene:campoScheda campo="EMAIL" obbligatorio="true"/>
	<gene:campoScheda campo="TELCEL"/>
	<gene:campoScheda campo="LEGRAP" title="Legale Rappresentante (*)"/>
	<gene:campoScheda campo="NOT_UTE" title="Note" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
	<gene:campoScheda campo="SYSCON" visibile="false"/>
	<gene:campoScheda campo="SYSLOGIN" obbligatorio="true" title="Login" entita="USRSYS" where="usrsys.syscon = utent.syscon"/>
	<gene:campoScheda campo="SYSPWD" visibile="false" entita="USRSYS" where="usrsys.syscon = utent.syscon"/>
	<gene:campoScheda>	
		<td class="etichetta-dato" >Password (*)</td>
		<td class="valore-dato">
			<input type="password" id="password" name="password" styleClass="testo" maxlength="10" onBlur="javascript:passaAConferma();" />
		</td>
	</gene:campoScheda>
	<gene:campoScheda>
		<td class="etichetta-dato" >Conferma Password (*)</td>
		<td class="valore-dato"> 
			<input type="password" id="confPassword" name="confPassword" styleClass="testo" maxlength="10" onBlur="javascript:passwordOk();" />
		</td>
	</gene:campoScheda>
	<gene:campoScheda>	
				<td class="valore-dato" colSpan="2">
					<center>
						(*) Campi obbligatori  (**) Campi obbligatori in alternativa
					</center>
				</td>
			</gene:campoScheda>
			<gene:campoScheda>	
				<td class="comandi-dettaglio" colSpan="2">
						<gene:insert name="pulsanteSalva">
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:eseguiSubmit()">
						</gene:insert>
						<gene:insert name="pulsanteAnnulla">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annullaScheda()">
						</gene:insert>
						&nbsp;
					</td>
				
			</gene:campoScheda>
	<%/* Aggiungo il richiamo di funzioni alla modifica del tipo di persona */%>
	<gene:fnJavaScriptScheda funzione="visualizzaTPPERS('#UTENT_TPPERS#')" elencocampi="UTENT_TPPERS" esegui="true" />
	<gene:fnJavaScriptScheda funzione="changeTPPERS('#UTENT_TPPERS#')" elencocampi="UTENT_TPPERS" esegui="false" />
	<gene:fnJavaScriptScheda funzione="emailValida()" elencocampi="UTENT_EMAIL" esegui="false" />
</gene:formScheda>
<c:if test="${param.metodo == 'nuovo'}">
	<gene:javaScript>
		document.forms[0].UTENT_TPPERS.value="F";
	</gene:javaScript>
</c:if>
<gene:javaScript>		
	function visualizzaTPPERS(tpers){
		document.getElementById("rowUTENT_NOMEUTE").style.display = (tpers=='G' ? 'none':'');
		document.getElementById("rowUTENT_COGUTE").style.display = (tpers=='G' ? 'none':'');
		document.getElementById("rowUTENT_LEGRAP").style.display = (tpers=='G' ? '':'none');
		
	}
	function changeTPPERS(tpers){
		if(tpers=='G'){
			document.forms[0].UTENT_NOMEUTE.value='';
			document.forms[0].UTENT_COGUTE.value='';
			
		} else
			document.forms[0].UTENT_LEGRAP.value='';
	}
	
	function passwordOk(){
	 	var passwordOk = true;
		if (document.forms[0].password.value != document.forms[0].confPassword.value) {
			alert('La password non è stata confermata correttamente. Assicurarsi di confermare correttamente la password');
			document.forms[0].confPassword.value = '';
			passwordOk =  false;
			}
		if (passwordOk) {
			document.forms[0].USRSYS_SYSPWD.value = document.forms[0].password.value;
			if (!controllaCampoPassword(document.forms[0].password,8,true))
				passwordOk = false;
			}
		return passwordOk;
	}	
				
	function annullaScheda(){
		window.location.href="InitLogin.do";
	}
	
	function emailValida() {
		var esito = false;
		if (!isFormatoEmailValido(document.forms[0].UTENT_EMAIL.value)) {
			alert("L'indirizzo email non e' sintatticamente valido.");
			if(ie4){
	  			document.forms[0].UTENT_EMAIL.select();
  				document.forms[0].UTENT_EMAIL.focus();
  			} else {
  			<% // Si e' dovuto differenziare il javascript per un bug
  			   // presente in Firefox 2.0 relativo all'esecuzione delle funzioni 
  			   // focus() e select() su un oggetto dopo all'evento onblur	del 
  			   // oggetto stesso %>
	 				setTimeout("document.forms[0].UTENT_EMAIL.select()",125);
 					setTimeout("document.forms[0].UTENT_EMAIL.focus()",125);
  			}
  		} else
  			esito = true;
  			
  		return esito;
	}
	
	function passaAConferma(){
		document.forms[0].confPassword.focus();
	}									
	
	function eseguiSubmit(){
		
		if (document.forms[0].UTENT_EMAIL.value!= "" && !emailValida())
			return false;
		if (passwordOk()) {
			document.forms[0].metodo.value="update";
			// Eseguo il submith con il controllo dei campi obbligatori
			if(activeForm.onsubmit()){
				bloccaRichiesteServer();
				document.forms[0].submit();
			}
		}
	}
	
	activeForm.addCheck("UTENT_CFUTE","\"##\".length>0 || \"#UTENT_PIVAUTE#\".length>0", "Uno fra \"Codice Fiscale\" e \"Partita IVA\" è obbligatorio",true);
	activeForm.addCheck("UTENT_PIVUTE","\"##\".length>0 || \"#UTENT_CFUTE#\".length>0", "Uno fra \"Codice Fiscale\" e \"Partita IVA\" è obbligatorio",true);
	activeForm.addCheck("UTENT_LEGRAP","\"##\".length>0 || \"#UTENT_TPPERS#\" == \"F\"", "Il Campo \"Legale rappresentante\" è obbligatorio",true);
	activeForm.addCheck("USRSYS_SYSPWD","\"##\".length>0", "Il Campo \"Password\" è obbligatorio",true);
</gene:javaScript>
</gene:redefineInsert>
</gene:template>