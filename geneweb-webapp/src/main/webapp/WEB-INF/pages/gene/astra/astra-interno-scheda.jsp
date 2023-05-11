<%
			/*
       * Created on: 11.58 01/06/2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
      /*
				Descrizione:
					Interno delle scheda di astra
				Creato da:
					Marco Franceschin
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<gene:formScheda entita="ASTRA" gestisciProtezioni="true" >
	<gene:campoScheda campo="CODVIA" 
		obbligatorio="true" modificabile='${modoAperturaScheda eq "NUOVO"}' keyCheck="yes" />
	<gene:campoScheda campo="VIAPIA" />
	<gene:campoScheda campo="STRTIP" />
	<gene:campoScheda campo="STRCIN" />
	<gene:campoScheda campo="STR_EST_UF" />
	<gene:campoScheda campo="STATO" />
	<gene:campoScheda campo="DATVAL" />
	<gene:campoScheda campo="STDESC" />
	<gene:campoScheda campo="IDLOCA" />
	<gene:campoScheda campo="STR_NOTE" />
	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
</gene:formScheda>