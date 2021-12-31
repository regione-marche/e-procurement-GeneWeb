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

      // *******************     ATTENZIONE     *********************************************
		  // Questa pagina jsp e' stata copiata e modificata nel progetto CSDomande-OnLine,
		  // per personalizzare le funzioni di cancellazione utente singola e multipla.
		  // Pertanto ogni modifica della presente pagina jsp deve essere riportata
		  // nella pagina copiata
      // ************************************************************************************
      %>
<!-- Inserisco la Tag Library -->

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<script type="text/javascript">
<!-- 
		function generaPopupListaOpzioniRecord(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Lista.LISTAMOD")}' >
						<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.DEL.GENE.USRSYS-Lista.LISTADEL")}' >
						<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Cambia-password")}' >
						<elda:jsVocePopup functionJS="cambiaPassword('\"+id+\"')" descrizione="Cambia password"/>	
					</c:if>
				</c:if>
			</elda:jsBodyPopup>
			return linkset;
		}
		
		function generaPopupListaOpzioniRecordDisattiva(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Lista.LISTAMOD")}' >
						<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.DEL.GENE.USRSYS-Lista.LISTADEL")}' >					
							<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>

					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Cambia-password")}' >
						<elda:jsVocePopup functionJS="cambiaPassword('\"+id+\"')" descrizione="Cambia password"/>	
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Disabilita-utente")}' >
						<elda:jsVocePopup functionJS="disattiva('\"+id+\"')" descrizione="Disabilita utente"/>	
					</c:if>
				</c:if>
			</elda:jsBodyPopup>
			return linkset;
		}
		
		function generaPopupListaOpzioniRecordAttiva(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Lista.LISTAMOD")}' >
						<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.DEL.GENE.USRSYS-Lista.LISTADEL")}' >
						<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Cambia-password")}' >
						<elda:jsVocePopup functionJS="cambiaPassword('\"+id+\"')" descrizione="Cambia password"/>	
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Abilita-utente")}' >
						<elda:jsVocePopup functionJS="attiva('\"+id+\"')" descrizione="Abilita utente"/>
					</c:if>
				</c:if>
			</elda:jsBodyPopup>
			return linkset;
		}
		
		function generaPopupListaOpzioniRecordLdap(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Lista.LISTAMOD")}' >
						<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.DEL.GENE.USRSYS-Lista.LISTADEL")}' >
						<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
					</c:if>
				</c:if>
			</elda:jsBodyPopup>
			return linkset;
		}
		
		function generaPopupListaOpzioniRecordLdapAttiva(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Lista.LISTAMOD")}' >
						<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.DEL.GENE.USRSYS-Lista.LISTADEL")}' >
						<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
					</c:if>
				</c:if>
				<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Abilita-utente")}' >
					<elda:jsVocePopup functionJS="attiva('\"+id+\"')" descrizione="Abilita utente"/>
				</c:if>
			</elda:jsBodyPopup>
			return linkset;
		}
		
		function generaPopupListaOpzioniRecordLdapDisattiva(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Lista.LISTAMOD")}' >
						<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.DEL.GENE.USRSYS-Lista.LISTADEL")}' >
						<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
					</c:if>
				</c:if>
				<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Disabilita-utente")}' >
					<elda:jsVocePopup functionJS="disattiva('\"+id+\"')" descrizione="Disabilita utente"/>
				</c:if>
			</elda:jsBodyPopup>
			return linkset;
		}
		
		function generaPopupListaOpzioniRecordDisattivaSenzaEliminazione(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Lista.LISTAMOD")}' >
						<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Cambia-password")}' >
						<elda:jsVocePopup functionJS="cambiaPassword('\"+id+\"')" descrizione="Cambia password"/>	
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Disabilita-utente")}' >
						<elda:jsVocePopup functionJS="disattiva('\"+id+\"')" descrizione="Disabilita utente"/>	
					</c:if>
				</c:if>
			</elda:jsBodyPopup>
			return linkset;
		}
		
		function generaPopupListaOpzioniRecordAttivaSenzaEliminazione(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Lista.LISTAMOD")}' >
						<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Cambia-password")}' >
						<elda:jsVocePopup functionJS="cambiaPassword('\"+id+\"')" descrizione="Cambia password"/>	
					</c:if>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Abilita-utente")}' >
						<elda:jsVocePopup functionJS="attiva('\"+id+\"')" descrizione="Abilita utente"/>
					</c:if>
				</c:if>
			</elda:jsBodyPopup>
			return linkset;
		}
		
		function generaPopupListaOpzioniRecordLdapAttivaSenzaEliminazione(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Lista.LISTAMOD")}' >
						<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
					</c:if>
				</c:if>
				<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Abilita-utente")}' >
					<elda:jsVocePopup functionJS="attiva('\"+id+\"')" descrizione="Abilita utente"/>
				</c:if>
			</elda:jsBodyPopup>
			return linkset;
		}
		
		function generaPopupListaOpzioniRecordLdapDisattivaSenzaEliminazione(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Lista.LISTAMOD")}' >
						<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
					</c:if>
				</c:if>
				<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.USRSYS-Lista.Disabilita-utente")}' >
					<elda:jsVocePopup functionJS="disattiva('\"+id+\"')" descrizione="Disabilita utente"/>
				</c:if>
			</elda:jsBodyPopup>
			return linkset;
		}
<%
	//function modifica(id){
		//document.location.href='DettaglioAccount.do?metodo=carica&modo=modifica&idAccount=' + id;
  	//}

	
	//function visualizza(id){
		//document.location.href = 'DettaglioAccount.do?metodo=carica&idAccount=' + id + '&modo=visualizza';
	//}
%>
	
	function modifica(id){
		document.location.href='DettaglioAccount.do?'+csrfToken+'&metodo=modifica&idAccount=' + id;
  }
  	
	function visualizza(id){
		document.location.href = 'DettaglioAccount.do?metodo=visualizza&idAccount=' + id;
		<% //Questa action viene utilizzata direttamente nel file listaAccount.jsp %>
	}
	
	function schedaNuovo(){
		document.location.href = 'InitCreaAccount.do?'+csrfToken;
	}

	function elimina(id){
		if (confirm("Confermi l'eliminazione dell'utente?")){
			bloccaRichiesteServer();
			document.location.href = 'ListaAccount.do?'+csrfToken+'&metodo=elimina&idAccount=' + id;
		}
	}
	
	function eliminaSelez(){
		var numeroOggetti = contaCheckSelezionati(document.listaAccount.id);
	  if (numeroOggetti == 0) {
	    alert("Nessun elemento selezionato nella lista");
	  } else {
   	  if (confirm("Sono stati selezionati " + numeroOggetti + " record. Procedere con l'eliminazione?")) {
      	bloccaRichiesteServer();
      	document.listaAccount.metodo.value="eliminaSelez";
	      document.listaAccount.submit();
	    }
		}
	}
	
	function cambiaPassword(id) {
		document.location.href = 'InitCambiaPasswordAdmin.do?'+csrfToken+'&metodo=cambioAdmin&idAccount=' + id;
	}

	function attiva(id){
		document.location.href='DettaglioAccount.do?'+csrfToken+'&metodo=abilita&idAccount=' + id;
  	}
	
	function disattiva(id){
		document.location.href='DettaglioAccount.do?'+csrfToken+'&metodo=disabilita&idAccount=' + id;
  	}
-->
</script>