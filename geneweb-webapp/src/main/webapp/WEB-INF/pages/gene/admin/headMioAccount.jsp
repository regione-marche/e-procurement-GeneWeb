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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript" src="${contextPath}/js/forms.js"></script>
<script type="text/javascript" src="${contextPath}/js/general.js"></script>
<c:set var="account" value="${requestScope.mioAccountForm}"/>


<script type="text/javascript">
<!--


	function schedaSalva(){
		
		if (document.mioAccountForm.email.value != "" && !controllaEmail(document.mioAccountForm.email,'document.mioAccountForm.email')) {
			return ; //false;
		}
		
		if (document.mioAccountForm.codfisc.value != "" && !controllaCodfisc(document.mioAccountForm.codfisc,'document.mioAccountForm.codfisc')) {
			return ; //false;
		}

		if (document.mioAccountForm.metodo.value=='modifica') {
			
			if (!controllaCampoInputObbligatorio(document.mioAccountForm.nome, 'Descrizione') ||
    			!controllaCampoInputObbligatorio(document.mioAccountForm.login, 'Login'))
    		return ; //false;

		}
		
		document.mioAccountForm.metodo.value="modifica";
		bloccaRichiesteServer();
		document.mioAccountForm.submit();
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
		var esito = false;		
		if (unCampoDiInput.value != "" && !checkCodFis(unCampoDiInput.value)) {
			if( !confirm("Il valore del codice fiscale specificato non è valido.\nConfermi ugualmente ?")){
				unCampoDiInput.value = "${mioAccountForm.codfisc}";
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
				esito = false;
			} else {
	  			esito = true;
	  		}
  		} else {
  			esito = true;
  		}
  			
		return esito;
	}
	
	function controllaCampoNome(unCampoDiInput, descrizioneCampo){
		var valore = new String(unCampoDiInput.value);
		var	caratteriAmmessi = " ~#$%&'()*+,-.\\/0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_abcdefghijklmnopqrstuvwxyz";
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
	 				setTimeout("document.mioAccountForm.login.select()",125);
 					setTimeout("document.mioAccountForm.login.focus()",125);
  			}
	  	}	else {
			 	index = index+1;
			}
		}
	}

	function schedaAnnulla(){
		bloccaRichiesteServer();
		document.location.href='SalvaMioAccount.do?'+csrfToken+'&metodo=annulla';
	}

-->
</script>