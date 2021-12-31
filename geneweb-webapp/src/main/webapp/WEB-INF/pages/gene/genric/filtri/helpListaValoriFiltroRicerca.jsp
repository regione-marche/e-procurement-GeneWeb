<%/*
       * Created on 02-mar-2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA L'HELP PER
      // LA VALORIZZAZIONE DEL CAMPO VALORE NEL CASO DI OPERATORE "IN"
    %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<c:set var="operatore" value="${param.operatore}"/>
<c:choose>
	<c:when test='${operatore eq "IN"}'>
		<c:set var="valoreOperatore" value="IN"/>
	</c:when>
	<c:otherwise>
		<c:set var="valoreOperatore" value="NOT IN"/>
		<c:set var="str1" value="non "/>
	</c:otherwise>
</c:choose>

<table>
	<tr>
		<td>
<div class="contenitore-dettaglio">
<table class="lista">
    <tr>
    	<td>
    		<p><b>Valorizzazione di 'Valore Confronto' con operatore '${valoreOperatore} - Appartiene':</b></p>
        L'operatore '${valoreOperatore}' permette di definire un filtro sull'elemento 'Campo' specificando la lista dei valori che ${str1}può assumere.
        <br>
        L'elemento 'Valore Confronto' deve essere popolato con una lista di valori separati da virgola, tenendo conto del 
        tipo di dato rappresentato dall'elemento 'Campo'. In particolare:
        <ul type="disc">
          <li>se il campo selezionato è di tipo numerico o di tipo data gli elementi della lista devono essere separati da virgola.
              Ad esempio:
            <ul type="circle">
              <li><i>22, 14, 76, 213</i>
              <li><i>76.00, 324.76, 1021, 1423.43</i>
              <li><i>11/03/2002, 30/06/2003, 30/07/2005, 04/10/2006 </i>
            </ul>
           <br>
          <li>se il campo selezionato è di tipo stringa gli elementi della lista devono essere racchiusi fra apice e separati da virgola.
              Ad esempio:
              <ul type="circle">
              	<li><i>'casa', 'cosa', 'caso'</i>
              </ul>
        </ul>
      </td>
    	<!--td width="2%">&nbsp;</td-->
    </tr>
    <tr class="comandi-dettaglio">
      <td align="center"> <!--  colspan="3" -->
				<input type="button" onclick="javascript:window.close();" title="Chiudi" value="Chiudi" class="bottone-azione"/>
      </td>
    </tr>
</table>
</div>
		</td>
		<td width="10px">&nbsp;</td>
    </tr>
</table>