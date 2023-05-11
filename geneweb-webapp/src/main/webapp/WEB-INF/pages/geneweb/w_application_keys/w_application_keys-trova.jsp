<%/* 
       * Created on 26-Feb-2010
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="W_APPLICATION_KEYS-trova">
	<gene:setString name="titoloMaschera" value="Ricerca credenziali servizi di pubblicazione"/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">

  	<% // Creo la form di trova con i campi dell'entita' peri %>
  	<gene:formTrova entita="W_APPLICATION_KEYS"  gestisciProtezioni="true" >
		<gene:gruppoCampi idProtezioni="GEN">
			<tr><td colspan="3"><b>Parametri di ricerca</b></td></tr>
			<gene:campoTrova campo="CLIENTID"/>
			<gene:campoTrova campo="NOTE"/>
		</gene:gruppoCampi>
    </gene:formTrova>    
  </gene:redefineInsert>
</gene:template>