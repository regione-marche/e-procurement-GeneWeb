<%/*
       * Created on 14-set-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE IL TEMPLATE DELLE PAGINE DI POPUP
    %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<HTML>
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<tiles:insert attribute="head" />
</HEAD>

<BODY <tiles:getAsString name="eventiDiPagina" /> >
<jsp:include page="/WEB-INF/pages/commons/bloccaRichieste.jsp" />
<div id="titolomaschera" class="titolomaschera"><tiles:getAsString name="titoloMaschera" /></div>
			<div class="contenitore-errori-arealavoro">
				<jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
			</div>
<tiles:insert attribute="dettaglio" />
</BODY>
</HTML>
