<%/*
       * Created on 22-dic-2014
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
	}
</gene:javaScript>

	<% //Settaggio delle stringhe utilizate nel template %>
	<gene:setString name="titoloMaschera" value='Recupero credenziali'/>
	
	<gene:redefineInsert name="corpo">

		<table class="dettaglio-home">
			<tr>
				<td class="sotto-voce">
					<p>Ti è stata inviata una email con le credenziali di accesso all'indirizzo ${email}</p>
					<p>Per accedere all'applicativo attendere una e-mail con i dati di connessione.</p>
					<p>Se non ricevi l'email, contatta l'assistenza.</p>
					<p><a href="InitLogin.do" class="link-generico">Vai alla home page</a></p>
				</td>
			</tr>
		</table>

</gene:redefineInsert>
</gene:template>