<%/*
       * Created on 30-ago-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
      // PAGINA CHE CONTIENE IL TEMPLATE DELLA PAGINA DI DETTAGLIO TIPICA (CON TAB)
    %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<script type="text/javascript">
/*
$(document).ready(function(){
	$('#link-info-tecniche').click(function(){
	    if ($('#info-tecniche').attr('class') == 'nascosto') {
	    	$('#info-tecniche').attr('class', '');
	    	$(this).attr('title', 'Nascondi le informazioni di registrazione');
	    	$(this).text('Informazioni tecniche:');
	    }
	    else {
	    	$('#info-tecniche').attr('class', 'nascosto');
	    	$(this).attr('title', 'Visualizza le informazioni di registrazione');
	    	$(this).text('Visualizza le informazioni tecniche');
	    }
	    //$('tr[id^="cat_'+this.id+'"]').slideToggle(0.1);
	  });	
});
*/
</script>
</HEAD>
<BODY>
<div class="margin10px">
<div align="center"><img src="${pageContext.request.contextPath}/img/logo.png"
				alt="Logo"/></div>

<p>
				<jsp:include page="/WEB-INF/pages/gene/aboutInfoCustom.jsp"/>
</p>
<div>
			<!-- <a id="link-info-tecniche" href="#" class="link-generico" title="Visualizza le informazioni di registrazione"> -->Informazioni tecniche:<!-- </a> -->
			<div id="info-tecniche">
			Nome prodotto: ${applicationScope.appTitle}<br/>
			Versione: ${sessionScope.versioneModuloAttivo}<br/>
			Registrato da: ${requestScope.acquirente}<br/>
		<c:if test='${(!empty codiceProdotto) and (!empty chiaveAccesso)}'>
			Configurazione installata: ${codiceProdotto}<br/>
			Chiave di accesso: ${chiaveAccesso}<br/>
		</c:if>
			</div>
</div>
</div>
</BODY>
</HTML>
