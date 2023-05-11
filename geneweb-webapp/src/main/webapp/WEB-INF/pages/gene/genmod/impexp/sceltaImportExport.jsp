<%/*
   * Created on 12-nov-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // PAGINA CHE CONTIENE LA PAGINA CON LA DOMANDA SCELTA DI QUALE OPERAZIONE
 // ESEGUIRE: IMPORT O EXPORT DEFINIZIONE REPORT
%>

<table class="dettaglio-notab">
  <tr>
   	<td>
   		<span class="info-wizard">
   			Selezionare l'operazione da avviare e premere "Avanti &gt;" o premere "Annulla" per tornare alla pagina iniziale dell'applicativo (homepage).
   		</span>
  		<p>
	  		<input type="radio" id="import" name="domanda">&nbsp;Importa definizione
  			<br>
	  		<input type="radio" id="export" name="domanda">&nbsp;Esporta definizione
	  		<br>
	  		&nbsp;
  		</p>
  	</td>
  </tr>
  <tr>
  	<td class="comandi-dettaglio">
      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
			<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
		</td>
  </tr>
</table>