<%
/*
 * Created on 04-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA TROVA RICERCA 
 // CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<script type="text/javascript">
<!--

<elda:jsFunctionOpzioniListaRecord contextPath="${pageContext.request.contextPath}"/>

	function apriTrovaModelli(){
		document.location.href='InitTrovaModelli.do?'+csrfToken;
  }
	function listaModelli(){
		document.location.href='TrovaModelli.do?'+csrfToken+'&metodo=trovaModelli';
	}

  function creaNuovoModello(){
		document.location.href='Modello.do?'+csrfToken+'&metodo=creaModello';
  }
	
	function visualizza(idModello){
		document.location.href='Modello.do?'+csrfToken+'&metodo=dettaglioModello&idModello='+idModello;
	}
	function modifica(idModello){
	  document.location.href='Modello.do?'+csrfToken+'&metodo=modificaModello&idModello='+idModello;
	}
	function elimina(idModello){
	  if(confirm("Procedere con l'eliminazione del modello ?","Eliminazione modello")){
	  	bloccaRichiesteServer();
	 		document.location.href='Modello.do?'+csrfToken+'&metodo=deleteModello&idModello='+idModello;
		}
	}

	function eliminaSelez(){
		var numeroOggetti = contaCheckSelezionati(document.listaModelli.id);
	  if (numeroOggetti == 0) {
	    alert("Nessun elemento selezionato nella lista");
	  } else {
   	  if (confirm("Sono stati selezionati " + numeroOggetti + " record. Procedere con l'eliminazione?")){
      	bloccaRichiesteServer();
      	//document.listaModelli.metodo.value="eliminaSelez";
	      document.listaModelli.submit();
	    }
		}
	}
-->
</script>