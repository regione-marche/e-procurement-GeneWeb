<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<tiles:insert  definition=".listaNoAzioniDef" flush="true">

	<tiles:put name="head" type="string">
	</tiles:put>

	<tiles:put name="titoloMaschera" type="string" value="Esegui aggiornamenti SQL" />
	 
	<tiles:put name="dettaglio" type="string">
	<form name="query" method="post" action="${pageContext.request.contextPath}/CRUD.do">
		<table class="ricerca">
			<tr>
				<td class="etichetta-dato">Inserire query di INSERT, UPDATE o DELETE</td>
				<td class="valore-dato">
					<textarea rows="10" cols="80" name="sql" id="sql">${param.sql}</textarea>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Inserire il numero di righe da aggiornare</td>
				<td class="valore-dato">
					<input type="text" name="numrow" id="numrow" value="1" size="7" maxlength="6"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Inserire il codice PIN</td>
				<td class="valore-dato">
					<input type="password" name="pin" size="10" />
				</td>
			</tr>
			<tr class="comandi-dettaglio">
				<td colspan="2">
					<input type="submit" value="Aggiorna" class="bottone-azione"/>
					<input type="button" value="Cancella" class="bottone-azione" onclick="document.getElementById('sql').value = '';document.getElementById('numrow').value = '1';"/>&nbsp;
				</td>
			</tr>
		</table>
	</form>
	</tiles:put>

</tiles:insert>
