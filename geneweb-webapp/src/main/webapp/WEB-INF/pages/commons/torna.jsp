<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

		<tr>
			<td class="titolomenulaterale">${gene:resource("label.tags.template.tornaa.titolo")}</td>
		</tr>
		<tr>
		<c:choose>
			<c:when test="${isNavigazioneDisabilitata eq '1'}">
			<td>Indietro</td>
			</c:when>
			<c:otherwise>
			<td class="vocemenulaterale">
				<a href="javascript:historyVaiIndietroDi(1);" title="Torna indietro alla pagina precedente" tabindex="1600" id="alinkIndietro">
					Indietro</a>
			</td>
			</c:otherwise>
		</c:choose>
		</tr>
