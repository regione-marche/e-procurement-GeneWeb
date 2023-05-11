<%/*
       * Created on 11-ott-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE LA DEFINIZIONE DELLA POPUP CONTENENTE I DATI RISULTATO DA STAMPARE
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />


<HTML lang="it">
<HEAD>
	<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
	<style type="text/css" media="print">
	#bottone-stampa {
		visibility: hidden;
	}
	</style>
	<script type="text/javascript">
		function stampa(){
			window.print();
			//window.close();
		}
	</script>
</HEAD>

<BODY> <!-- onload='setTimeout("stampa()",10)'-->
	
	<table class="print-testata">
		<tr>
			<td class="icone">
				<img alt="Stampa Report" src="${contextPath}/img/logo_stampa.gif">
			</td>
			<td class="testo">
					${risultatoRicerca.titoloRicerca}
			</td>
		</tr>
	</table>
	
	<div class="contenitore-errori-arealavoro">
		<jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
	</div>
	
	<jsp:include page="/WEB-INF/pages/gene/genric/risultato/risultatoRicercaStampa.jsp" />
	
	<div align="center" id="bottone-stampa">
	<input type="button" class="bottone-azione" name="Stampa" value="Stampa" onclick="javascript:stampa();" title="Invia la pagina alla stampante"/>
	</div>

</BODY>
</HTML>

