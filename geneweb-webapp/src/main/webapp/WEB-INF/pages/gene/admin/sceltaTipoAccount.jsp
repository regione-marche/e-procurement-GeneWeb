<%/*
   * Created on 02-mag-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // PAGINA CHE CONTIENE LA PAGINA CON LA DOMANDA DI DEFINIZIONE FILTRI PER IL WIZARD
 // DI CREAZIONE DI UNA RICERCA BASE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<table class="dettaglio-notab">
	<tr>
		 	<td width="8%" align="right" valign="top">
				<input type="radio" id="accountLdap" name="domandaAccount">
				
			</td>
			<td class="valore-dato">
				<p>
					<b>Seleziona un utente dall'elenco utenti che accedono alla intranet aziendale</b>
				</p>
				<p>
					Questa opzione permette di selezionare un utente dall'elenco di utenti che possono utilizzare 
					le macchine della rete aziendale. L'utente selezionato viene inserito nell'applicativo e ne vengono 
					configurate le abilitazioni. L'utente potrà accedere all'applicativo utilizzando le proprie 
					credenziali (login/password) utilizzate abitualmente per accedere alle macchine nella rete.
					<br>
				</p>
				
			</td>
		</tr>
  <tr>
  <tr>
		 	<td width="8%" align="right" valign="top">
				<input type="radio" id="account" name="domandaAccount" checked="checked">
				
			</td>
			<td class="valore-dato">
				<p>
					<b>Definisci un nuovo utente interno all'applicativo</b>
				</p>
				<p>
					Questa opzione permette di definire un nuovo utente direttamente all'interno dell'applicativo, 
					specificandone il nome, la login e la password. L'utente selezionato viene inserito nell'applicativo 
					e ne vengono configurate le abilitazioni.
				</p>
			</td>
		</tr>

  <tr>
  	<td class="comandi-dettaglio" colSpan="2">
		<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
		<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
	</td>
  </tr>
</table>