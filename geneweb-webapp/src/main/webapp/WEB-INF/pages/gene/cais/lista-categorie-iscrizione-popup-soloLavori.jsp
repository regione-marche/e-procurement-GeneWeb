<%
	/*
   * Created on: 15:30 19/03/2008
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */
   
   /*
		Descrizione: Lista delle categorie degli appalti
		Creato da:   Luca Giacomazzo
	  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<gene:template file="popup-template.jsp">

<jsp:include page="lista-categorie-iscrizione-interno-popup.jsp" />

<gene:javaScript>
		showObj("spanLavori150", false);
		showObj("spanForniture", false);
		showObj("spanServizi", false);
		showObj("spanServiziProfessionali", false);
</gene:javaScript>

</gene:template>