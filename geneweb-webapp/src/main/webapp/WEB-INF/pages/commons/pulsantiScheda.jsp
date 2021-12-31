<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<%// Aggiunto il controllo dulle protezioni %>
<td class="comandi-dettaglio" colSpan="2">
	<gene:insert name="addPulsanti"/>
	<c:choose>
	<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
		<gene:insert name="pulsanteSalva">
			<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
		</gene:insert>
		<gene:insert name="pulsanteAnnulla">
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
		</gene:insert>

	</c:when>
	<c:otherwise>
		<gene:insert name="pulsanteModifica">
			<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
				<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
			</c:if>
		</gene:insert>
		<gene:insert name="pulsanteNuovo">
			<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
				<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
			</c:if>
		</gene:insert>
	</c:otherwise>
	</c:choose>
	&nbsp;
</td>