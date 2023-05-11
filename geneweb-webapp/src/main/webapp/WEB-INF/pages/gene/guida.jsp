<%/*
       * Created on 1-ott-2007
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

<gene:template file="generic-template.jsp" >
	<gene:historyClear/>
	<gene:insert name="addHistory">
		<gene:historyAdd titolo='Documenti e manuali' id="ricerca" />
	</gene:insert>

		<gene:redefineInsert name="corpo" >
			<c:import url="/doc/manuali.jsp" />
		</gene:redefineInsert>
		<gene:redefineInsert name="debugDefault"></gene:redefineInsert>
</gene:template>