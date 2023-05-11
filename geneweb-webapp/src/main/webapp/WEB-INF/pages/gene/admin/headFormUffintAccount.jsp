<%
/*
 * Created on 02-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA UFFICI
 // ACCOUNT CONTENENTE LA SEZIONE JAVASCRIPT
%>


<script type="text/javascript">


  function salvaModifiche(id){
		bloccaRichiesteServer();
  	document.listaForm.submit();
  }

  function annullaModifiche(id){
    bloccaRichiesteServer();
		document.location.href='ListaUfficiIntestatariAccount.do?'+csrfToken+'&metodo=visualizza&idAccount=' + id;
  }
  
  function schedaVisualizza(id){
		document.location.href = 'DettaglioAccount.do?'+csrfToken+'&metodo=carica&idAccount=' + id + '&modo=visualizza';
	}

</script>