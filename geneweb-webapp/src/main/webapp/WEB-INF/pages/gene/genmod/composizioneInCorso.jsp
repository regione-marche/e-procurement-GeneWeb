<%/*
       * Created on 04-dic-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA TEMPORANEA CHE INDICA L'ELABORAZIONE DEL MODELLO IN CORSO
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />

<script type="text/javascript">
	<!--
  /**************************************************************
	Funzione che esegue la composizione di un testo tipo
  @param idModello codice del modello da comporre
  @param nome Nome del modello che viene messo nella descrizione

  @author Marco Franceschin
  @changelog
  19.09.2006: M.F. Prima Versione
  ***************************************************************/
  function componiModello(idModello, nome){
    document.componiModelloForm.idModello.value=idModello;
    document.componiModelloForm.nomeModello.value=nome;
    document.componiModelloForm.submit();
  }
  -->
</script>
</HEAD>

<BODY>


<!-- Parte durante la composizione -->
<table style="border: thin; position: absolute; top: 15%;left: 5%;"
	align="left" width="90%">
	<tr>
		<TD align="center">
		<div class="titolomaschera">Composizione modelli</div>
		<div class="contenitore-dettaglio">

		<table class="lista">
			<tr>
				<td align="center">
				<img id="progressImage" src="${contextPath}/img/${applicationScope.pathImg}progressbar.gif" alt=""/>
				<br>
				<br>
				Composizione '${componiModelloForm.nomeModelloPerJs}' in corso...
				</td>
			</tr>
		</table>
		</div>
		</TD>
	</tr>
</table>

<html:form action="/ComponiModello">
	<input type="hidden" name="metodo" value="componiModello"/> 
	<jsp:include page="/WEB-INF/pages/commons/csrf.jsp"></jsp:include>
	<html:hidden property="idSessione"/>
	<html:hidden property="idModello"/>
	<html:hidden property="nomeModello"/> 
	<html:hidden property="entita"/> 
	<html:hidden property="nomeChiavi"/> 
	<html:hidden property="valori"/>
	<html:hidden property="fileComposto"/>
	<html:hidden property="noFiltroEntitaPrincipale"/>
	<html:hidden property="paginaSorgente"/>
	<html:hidden property="riepilogativo"/>
	<c:forEach items="${componiModelloForm.valChiavi}" var="chiave">
	<input type="hidden" name="valChiavi" value="${chiave}"/>
	</c:forEach>
</html:form>

<script type="text/javascript">
  // aggiunge la gestione in primo piano della popup anzichè definire
  // l'evento onblur sul body, dato che non è standard W3C
  if (navigator.appName == "Microsoft Internet Explorer") {
  	window.onblur = new Function("window.focus()");
  }
  
  // inoltra la richiesta di composizione dopo aver caricato la pagina e
  // dopo aver reso visibile la gif animata della progressbar, il cui
  // comportamento dipende dal browser
  if (navigator.appName == "Microsoft Internet Explorer") {
    document.componiModelloForm.submit();
    document.getElementById('progressImage').src = "${contextPath}/img/${applicationScope.pathImg}progressbar.gif";

  } else {
    window.setTimeout('document.componiModelloForm.submit()', 5);
  }
</script>

</BODY>
</HTML>
