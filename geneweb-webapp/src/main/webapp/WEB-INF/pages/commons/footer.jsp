<%
/*
 * Created on 18-set-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // FONDO PAGINA
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>


	<div class="superiore">
		<div class="left"></div>
		<div class="right"></div>
	</div>
	<div class="inferiore">
		<div class="left">
			<c:if test='${!empty moduloAttivo}'>
				<span <c:if test="${empty nomeUffint || !empty param.paginaChiamante}">class="ultimolink"</c:if> title="${descProfiloAttivo}   (${profiloAttivo})">${nomeProfiloAttivo}</span>
			</c:if>
			<c:if test='${!empty nomeUffint && empty param.paginaChiamante}'>
				<span class="ultimolink" title="${nomeUffint}   (${uffint})">${nomeUffint}</span>
			</c:if>
		</div>
		
		<div class="right">
			<c:if test='${!empty profiloAttivo && empty param.paginaChiamante && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.Riferimenti-Informazioni")}' >
				<span>
					<a href="javascript:about('${pageContext.request.contextPath}');" title="visualizza informazioni sul software">info</a>
				</span>
			</c:if>
		</div>			
	</div>
	
	<br>
	<br>

