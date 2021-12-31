<%
/*
 * Created on 28-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI TROVA RICERCHE
 // CONTENENTE IL FORM PER IMPOSTARE I DATI DELLA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<form action="${contextPath}/geneGenric/SalvaSqlRicerca.do" name="sqlForm" method="post" >
	<table class="dettaglio-tab">
	  <tr>
      <td class="etichetta-dato">Istruzione SQL da eseguire (*)</td>
      <td class="valore-dato">
      	<textarea name="defSql" id="defSql" cols="80" rows="10" >${requestScope.defSql}</textarea>
      </td>
    </tr>
    <tr>
    	<td class="etichetta-dato">Scrivere la query secondo le seguenti regole:</td>
    	<td class="etichetta-dato" style="text-align: left" >
    		<br>
    		<ul>
    			<li>
    				se ai campi estratti non si applicano funzioni SQL con pi&ugrave; di un argomento, l'elenco dei campi della select pu&ograve; essere scritto liberamente in un'unica riga oppure in pi&ugrave; righe (vedi 
    				<a href="javascript:showObj('trEsempio1')" >Esempio 1</a>);
    			</li>
    			<li>
    				se ai campi estratti si applicano funzioni SQL con pi&ugrave; di un argomento, l'elenco dei campi della select deve essere scritto specificando un campo per riga (vedi 
    				<a href="javascript:showObj('trEsempio2')" >Esempio 2</a>);
    			</li>
				</ul>
				<br>
				Dopo il from queste regole possono essere ignorate e la parte successiva della query pu&ograve; essere scritta come meglio si crede.
    	</td>
    </tr>
		<tr id="trEsempio1">
			<td class="etichetta-dato">Esempio 1:</td>
			<td class="etichetta-dato" style="text-align: left">
			<br>
			<div style="padding-left: 10px;">
			-- Riga di commento ...
			<br>
			select CODGAR, DATTOT, DFPUBA, TIPTOR @@SN@@, IMPTOR @@MONEY@@, ISTAUT @@MONEY@@ 
			<br>
			from TORN
			<br>
			where DATTOT > #PARAM1#
			<br>
			and DATTOT< #PARAM2#
			<br>
			-- Riga di commento ...
			<br>
			and DESTOR like #PARAM3#
			<br>
			and DFPUBA > #PARAM2#
			<br>
			-- Riga di commento<br>
			<br><br>
			</div>
			</td>
		</tr>

		<tr id="trEsempio2">
			<td class="etichetta-dato">Esempio 2:</td>
			<td class="etichetta-dato" style="text-align: left">
			<br>
			<div style="padding-left: 10px;">
			-- Riga di commento ...
			<br>
			select UPPER(not_gar),
			<br> 
			impgar @@MONEY@@,
			<br> 
			CAST(COALESCE(DAATTO, CURRENT_TIMESTAMP) as TIMESTAMP) as DataAtto @@DATA_ELDA@@
			<br>from 
			<br>gare 
			<br>where 
			<br>impgar is not null 
			<br>-- Riga di commento
			<br><br>
			</div>
			</td>
		</tr>
		
		<tr>
			<td class="etichetta-dato">Note:</td>
			<td class="etichetta-dato" style="text-align: left">
				<br>
				E' possibile indicare mediante tag @@ la modalit&agrave; di formattazione di un campo estratto mediante select.<br>
				I formati supportati sono i seguenti:
				<dl>
				  <dt><b>@@SN@@</b></dt>
				  <dd>per i campi Si/No, conversione dei valori 1 e 2 rispettivamente in Si e No</dd>
				  <dt><b>@@MONEY@@</b></dt>
				  <dd>per i campi importo, visualizzazione del valore con il punto come separatore delle migliaia, la virgola come separatore decimali e con 2 cifre decimali</dd>
				  <dt><b>@@MONEY5@@</b></dt>
				  <dd>per i campi importo, visualizzazione del valore con il punto come separatore delle migliaia, la virgola come separatore decimali e con 5 cifre decimali</dd>
				  <dt><b>@@DATA_ELDA@@</b></dt>
				  <dd>per i campi date o timestamp, visualizzazione del valore in formato GG/MM/AAAA</dd>
				  <dt><b>@@TIMESTAMP@@</b></dt>
				  <dd>per i campi timestamp, visualizzazione del valore in formato GG/MM/AAAA HH:MM:SS</dd>
				</dl>
			</td>
		</tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
		<c:choose>
       <c:when test="${!empty sessionScope.recordDettRicerca.testata.nome}">
 	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:cambiaTab('SQL');">
       </c:when>
       <c:otherwise>
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaModifiche();">
       </c:otherwise>
     </c:choose>
        &nbsp;
      </td>
    </tr>
	</table>
</form>