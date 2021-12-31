<%
/*
 * Created on 22-ago-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI ERRORE
 // DURANTE L'IMPORTAZIONE DI UN REPORT
%>

<table class="lista">
  <tr>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>Errore durante l'operazione di importazione di un report. Premere "Annulla" per interrompere la procedura guidata di importazione</td>
  </tr>
  <tr>
    <td class="comandi-dettaglio">
      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;
    </td>
  </tr>
</table>