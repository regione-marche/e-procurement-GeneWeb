<%/*
       * Created on 16-gen-2008
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI CONFERMA
      // DELLA CANCELLAZIONE DI UN DOCUMENTO ASSOCIATO
    %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<c:set var="numeroDocAss" value="${param.numDoc}"/>

<table>
	<tr>
		<td>
<div class="contenitore-dettaglio">
<table class="lista">
    <tr>
    	<td>
    <c:choose>
    	<c:when test='${not empty param.id}'>
   		<c:if test='${not param.shared}'>
				Attenzione. Il file relativo al documento selezionato si trova all'esterno del percorso standard d'archiviazione.
				<br><br>
			</c:if>
				Procedere con l'eliminazione?
        <br><br>
        <input type="checkbox" id="cancellaFile" name="cancellaFile" checked="checked" >&nbsp;Eliminare il file relativo al documento associato
        <br>&nbsp;
      </c:when>
    	<c:when test='${empty param.id and param.numDoc eq 1}'>
   		<c:if test='${not param.shared}'>
				Attenzione. Il file relativo al documento selezionato si trova all'esterno del percorso standard d'archiviazione.
				<br><br>
			</c:if>
        Procedere con l'eliminazione?
        <br><br>
        <input type="checkbox" id="cancellaFile" name="cancellaFile" checked="checked" >&nbsp;Eliminare il file relativo al documento associato
        <br>&nbsp;
      </c:when>
      <c:otherwise>
   		<c:if test='${not param.shared}'>
      	Attenzione. Almeno uno dei file relativi ai documenti selezionati si trova all'esterno del percorso standard d'archiviazione.
      	<br><br>
			</c:if>
      	Procedere con l'eliminazione?
        <br><br>
        <input type="checkbox" id="cancellaFile" name="cancellaFile" checked="checked">&nbsp;Eliminare i file relativi ai documenti associati
        <br>&nbsp;
      </c:otherwise>
    </c:choose>
      </td>
    </tr>
    <tr class="comandi-dettaglio">
      <td >
				<input type="button" onclick="javascript:eliminaFile();" title="Conferma eliminazione" value="Conferma" class="bottone-azione"/>&nbsp;
				<input type="button" onclick="javascript:annulla();" title="Annulla eliminazione" value="Annulla" class="bottone-azione"/>&nbsp;
      </td>
    </tr>
</table>
</div>
		</td>
		<td width="10px">&nbsp;</td>
    </tr>
</table>
<script type="text/javascript">
<!--

	function annulla(){
		window.opener.document.getElementById("cancellazioneFile").value=1;
		window.close();
	}

	function eliminaFile(){
		setCancellazioneFile();
<c:choose>
	<c:when test='${not empty param.id}'>
		window.opener.eseguiCancellazione(${param.id});
  </c:when>
  <c:otherwise>
		window.opener.eseguiCancellazioneMultipla();
  </c:otherwise>
</c:choose>
		window.close();
	}
	
	function setCancellazioneFile(){
		if(document.getElementById("cancellaFile").checked){
			window.opener.document.getElementById("cancellazioneFile").value=1;
		} else {
			window.opener.document.getElementById("cancellazioneFile").value=0;
		}
	}
	
-->
</script>