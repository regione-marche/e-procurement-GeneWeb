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

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript" src="${contextPath}/js/forms.js"></script>
<script type="text/javascript" src="${contextPath}/js/general.js"></script>
<c:set var="account" value="${requestScope.accountForm}"/>


<script type="text/javascript">
<!--
	
	<elda:jsFunctionOpzioniListaRecord contextPath="${pageContext.request.contextPath}"/>
	<% // Variabili globali JS della pagina %>
	var elemOpzGenRic = null;
	var elemOpzGenMod = null;
	
	function initVarGlobali(){
		if(elemOpzGenRic == null)
			elemOpzGenRic = document.getElementById("opzioniGenric");

		if(elemOpzGenMod == null)
			elemOpzGenMod = document.getElementById("opzioniGenmod");
	}
	<% // Fine variabili globali della pagina %>
	
	function listaGruppiAccount(id){
		document.location.href='ListaGruppiAccount.do?'+csrfToken+'&idAccount=' + id;
	}
  
 	function obbligatorio() {
 		if (!controllaCampoInputObbligatorio(document.accountForm.nome, 'Descrizione') ||
 			!controllaCampoInputObbligatorio(document.accountForm.login, 'Login')
 			<c:choose>
 				<c:when test='${(not empty account.dn) && (account.dn != "")}'>
 				|| !controllaCampoInputObbligatorio(document.accountForm.dn, 'Nome univoco utente per LDAP')
 				</c:when>
 				<c:otherwise>
		 			<c:if test='${"1" ne passwordNullable}'>||
		 				!controllaCampoInputObbligatorio(document.accountForm.password, 'Password') ||
		 				!controllaCampoInputObbligatorio(document.accountForm.confPassword, 'Conferma Password')
		 			</c:if>
 				</c:otherwise>
 			</c:choose>
 			
			<c:if test='${"1" eq requestScope.uffAppObbligatorio}'>
 				|| !controllaCampoInputObbligatorio(document.accountForm.ufficioAppartenenza, 'Ufficio Appartenenza')
 			</c:if>
 			) {
 			return false;
 		} else {
  		return true;
 		}
 	}
	
	function passwordOk() {
		var result = isPasswordOk();
	}
	
	function isPasswordOk() {
		if (document.accountForm.password.value != document.accountForm.confPassword.value) {
			alert('La password non è stata confermata correttamente. Assicurarsi di confermare correttamente la password');
			document.accountForm.confPassword.value = '';
			//document.accountForm.confPassword.focus();
			return false;
		}
		return true;
	}

	function esisteAlmenoUnCheckReportSePersonali() {
		initVarGlobali();
	  // solo per il pacchetto evoluto con OP98, non solo con OP2
	  if (${fn:contains(listaOpzioniDisponibili, "OP98#")} &&
	      (elemOpzGenRic.options[elemOpzGenRic.selectedIndex].value == "ou49") &&
	      (!document.accountForm.opzioniGenricBase.checked &&
	       !document.accountForm.opzioniGenricAvanzati.checked &&
	       !document.accountForm.opzioniGenricProspetto.checked)
	     ) {
			alert('La gestione di report personali deve essere configurata con almeno una famiglia di report');
		    return false;
		   }
	  else 
	    return true;
	}

	function schedaSalva(){
		if(document.accountForm.metodo.value == '')
			document.accountForm.metodo.value = "inserisci";
		
		if (document.accountForm.email.value != "" && !controllaEmail(document.accountForm.email,'document.accountForm.email')) {
			return ; //false;
		}
							
		if (!eseguiControlliSezioneCustomSalva()) {
			return ; //false;
		}
					
		if (document.accountForm.metodo.value == "inserisci") {
		
			if (obbligatorio() 
			<c:if test='${(empty account.dn)}'> && isPasswordOk() </c:if> && esisteAlmenoUnCheckReportSePersonali() ) {
				bloccaRichiesteServer();
				document.accountForm.submit();
			}	else {
				return ; //false;
			}
		}
		
		if (document.accountForm.metodo.value=='carica' || document.accountForm.metodo.value=='modifica') {
			
			if (!controllaCampoInputObbligatorio(document.accountForm.nome, 'Descrizione') ||
    			!controllaCampoInputObbligatorio(document.accountForm.login, 'Login')
    			
    			<c:if test='${"1" eq requestScope.uffAppObbligatorio}'>
						|| !controllaCampoInputObbligatorio(document.accountForm.ufficioAppartenenza, 'Ufficio Appartenenza')
					</c:if>
    			
					)
    		return ; //false;

	    if (esisteAlmenoUnCheckReportSePersonali()) {
				document.accountForm.metodo.value="modifica";
				bloccaRichiesteServer();
				document.accountForm.submit();
			}
		}
	}
		
	function controllaEmail(unCampoDiInput,stringaCampo) {
		var esito = false;
		
		if (unCampoDiInput.value != "" && !isFormatoEmailValido(unCampoDiInput.value)) {
			alert("L'indirizzo email non e' sintatticamente valido.");
			if(ie4){
	  			unCampoDiInput.select();
  				unCampoDiInput.focus();
  			} else {
  			<% // Si e' dovuto differenziare il javascript per un bug
  			   // presente in Firefox 2.0 relativo all'esecuzione delle funzioni 
  			   // focus() e select() su un oggetto dopo all'evento onblur	del 
  			   // oggetto stesso %>
	 				setTimeout(stringaCampo + ".select()",125);
 					setTimeout(stringaCampo + ".focus()",125);
  			}
  		} else
  			esito = true;
  			
  		return esito;
	}
	
	function controllaCodfisc(unCampoDiInput,stringaCampo) {
				
		if (unCampoDiInput.value != "" && !checkCodFis(unCampoDiInput.value)) {
			if( !confirm("Il valore del codice fiscale specificato non è valido.\nConfermi ugualmente ?")){
				unCampoDiInput.value = "${accountForm.codfisc}";
				if(ie4){
		  			unCampoDiInput.select();
	  				unCampoDiInput.focus();
	  			} else {
	  			<% // Si e' dovuto differenziare il javascript per un bug
	  			   // presente in Firefox 2.0 relativo all'esecuzione delle funzioni 
	  			   // focus() e select() su un oggetto dopo all'evento onblur	del 
	  			   // oggetto stesso %>
		 				setTimeout(stringaCampo + ".select()",125);
	 					setTimeout(stringaCampo + ".focus()",125);
	  			}	
			}
			
			
			
  		}
  			
  		
	}
	
	function controllaCampoNome(unCampoDiInput, descrizioneCampo){
		var valore = new String(unCampoDiInput.value);
<c:choose>
	<c:when test="${account.flagLdap ne 1}">
		var	caratteriAmmessi = " ~#$%&'()*+,-.\\/0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_abcdefghijklmnopqrstuvwxyz";
	</c:when>
	<c:otherwise>
		var	caratteriAmmessi = "~#$%&'()*+,-.\\/0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_abcdefghijklmnopqrstuvwxyz ";
	</c:otherwise>	
</c:choose>
		var result = true;
		var index = 0;
		while(index < valore.length && result){
	  	if(caratteriAmmessi.indexOf(valore.charAt(index)) < 0){
		  	result = false;
	  		alert('Attenzione: "'+ valore.charAt(index) + '" carattere non ammesso nel campo '+ descrizioneCampo +'.');
  			if(ie4){
	  			unCampoDiInput.select();
  				unCampoDiInput.focus();
  			} else {
  			<% // Si e' dovuto differenziare il javascript per un bug
  			   // presente in Firefox 2.0 relativo all'esecuzione delle funzioni 
  			   // focus() e select() su un oggetto dopo all'evento onblur	del 
  			   // oggetto stesso %>
	 				setTimeout("document.accountForm.login.select()",125);
 					setTimeout("document.accountForm.login.focus()",125);
  			}
	  	}	else {
			 	index = index+1;
			}
		}
	}

<c:choose>
	<c:when test='${account.idAccount == 0 || (empty account.idAccount)}'>
		function schedaAnnulla(){
			document.accountForm.metodo.value="annulla";
			bloccaRichiesteServer();
			document.accountForm.submit();
		}
	</c:when>
	<c:otherwise>
		function schedaAnnulla(){
			document.location.href = 'DettaglioAccount.do?'+csrfToken+'&idAccount=${account.idAccount}&metodo=visualizza';
		}	
	</c:otherwise>
</c:choose>

	function schedaNuovo(){
		bloccaRichiesteServer();
		document.location.href = 'DettaglioAccount.do?'+csrfToken+'&metodo=nuovo';
	}
			
	function avanzatoAbilitato(){
		initVarGlobali();
		if (${fn:contains(listaOpzioniDisponibili, "OP2#")} && (
		   (elemOpzGenRic.options[elemOpzGenRic.selectedIndex].value == "ou48")|| (elemOpzGenRic.options[elemOpzGenRic.selectedIndex].value == "ou49")))
			return true;
		else
			return false;
	}
			
	function baseAbilitato(){
		initVarGlobali();
		if (${fn:contains(listaOpzioniDisponibili, "OP98#")} && (
		   (elemOpzGenRic.options[elemOpzGenRic.selectedIndex].value == "ou48") || (elemOpzGenRic.options[elemOpzGenRic.selectedIndex].value == "ou49")))
			return true;
		else
			return false;
	}
			
	function prospettoAbilitato(){
		initVarGlobali();
		if ((${fn:contains(listaOpzioniDisponibili, "OP1#")} && 
  		 ((elemOpzGenMod.options[elemOpzGenMod.selectedIndex].value == "ou50") || (elemOpzGenMod.options[elemOpzGenMod.selectedIndex].value == "ou51"))) && 
		   (${fn:contains(listaOpzioniDisponibili, "OP98#")} && 
		   ((elemOpzGenRic.options[elemOpzGenRic.selectedIndex].value == "ou48") || (elemOpzGenRic.options[elemOpzGenRic.selectedIndex].value == "ou49"))))
			return true;
		else
			return false;
	}
			
	function settaReport() {
		if (${!fn:contains(listaOpzioniDisponibili, "OP2#")})
			return;

		initVarGlobali();
		if (elemOpzGenRic.options[elemOpzGenRic.selectedIndex].value == "") {
			document.getElementById("rGenric").style.display = "none";
			document.getElementById("opzioniGenricBase").checked = false;
			document.getElementById("opzioniGenricAvanzati").checked = false;
			document.getElementById("opzioniGenricProspetto").checked = false;
		}
		
		if (elemOpzGenRic.options[elemOpzGenRic.selectedIndex].value == "ou48"){
			document.getElementById("rGenric").style.display = "none";
			if (baseAbilitato())
				document.getElementById("opzioniGenricBase").checked = true;
			else
				document.getElementById("opzioniGenricBase").checked = false;
				
			if (avanzatoAbilitato())
				document.getElementById("opzioniGenricAvanzati").checked = true;
			else
				document.getElementById("opzioniGenricAvanzati").checked = false;
				
			if (prospettoAbilitato())
				document.getElementById("opzioniGenricProspetto").checked = true;
			else
				document.getElementById("opzioniGenricProspetto").checked = false;
		}
		
		if (elemOpzGenRic.options[elemOpzGenRic.selectedIndex].value == "ou49") {
	    if (${!fn:contains(listaOpzioniDisponibili, "OP98#")}) {
	      document.getElementById("rGenric").style.display = "none";
	      document.getElementById("opzioniGenricBase").checked = false;
	      document.getElementById("opzioniGenricAvanzati").checked = true;
	      document.getElementById("opzioniGenricProspetto").checked = false;
	    } else {
	      document.getElementById("rGenric").style.display = "";
	    }
		}
	}

	function nascondiModelliReport() {
		initVarGlobali();
		if (document.getElementById("opzioniUtente") != null && document.getElementById("opzioniUtente").checked) {
			if (document.getElementById("rModelli")){
				document.getElementById("rModelli").style.display = "none";
				elemOpzGenMod.selectedIndex = 0;
			}

			if (document.getElementById("rReport")) {
				document.getElementById("rReport").style.display = "none";
				elemOpzGenRic.selectedIndex = 0;
				document.getElementById("opzioniGenricBase").checked = false;
	      document.getElementById("opzioniGenricAvanzati").checked = false;
	      document.getElementById("opzioniGenricProspetto").checked = false;
			}
		}	else {
			if (document.getElementById("rModelli"))
				document.getElementById("rModelli").style.display = "";

			if (document.getElementById("rReport"))
				document.getElementById("rReport").style.display = "";
		}
		nascondiProspetto();
	}
			
	function nascondiProspetto() {
		initVarGlobali();
		if (${fn:contains(listaOpzioniDisponibili, "OP1#")}) {
		  if(((elemOpzGenRic != null && elemOpzGenMod.options[elemOpzGenMod.selectedIndex].value == "ou50") ||
		    (elemOpzGenRic != null && elemOpzGenMod.options[elemOpzGenMod.selectedIndex].value == "ou51"))) {
 			  //document.getElementById("opzioniGenricProspetto").checked = true;
			  document.getElementById("rProspetto").style.display = "";
		  }	else {
				if(document.getElementById("opzioniGenricProspetto")) {
					document.getElementById("opzioniGenricProspetto").checked = false;
					document.getElementById("rProspetto").style.display = "none";
				}
		  }
		}
	}
	
	function controllaInputData(unCampoDiInput) {
	  return isData(unCampoDiInput)
	}
	
	function gestioneCampoDataScadenza() {
		var objDataScadenza = document.accountForm.selectScadenzaAccount;
		if (objDataScadenza) {
			if(objDataScadenza.selectedIndex == 0 ) {
				document.getElementById("spanScadenzaAccount").style.display = "none";
				document.accountForm.scadenzaAccount.value='';
			} else {
				document.getElementById("spanScadenzaAccount").style.display = "";
				if(document.accountForm.scadenzaAccount.value == '') {
					d = new Date();
					giorno = d.getDate() >= 10 ? d.getDate() : '0'+d.getDate();
					mese = (d.getMonth() + 1) >= 10 ? (d.getMonth() + 1) : '0'+ (d.getMonth() + 1);
					data = giorno + '/' + mese + '/' + (d.getFullYear()+1);
					document.accountForm.scadenzaAccount.value=data;
				}
			}
		}
	}
	
-->
</script>