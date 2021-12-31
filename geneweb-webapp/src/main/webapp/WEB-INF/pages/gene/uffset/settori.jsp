<%
/*
 * Created on: 18-05-2017
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Sezione settori dell'ufficio intestatario */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="codiceUfficio" value='${param.chiave}' />

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda  entita="UFFSET" where="UFFINT.CODEIN = UFFSET.CODEIN" campo="ID_${param.contatore}" visibile="false" campoFittizio="true" definizione="N10;1;;;G1IDRET" value="${item[0]}" />
		<gene:campoScheda  entita="UFFSET" where="UFFINT.CODEIN = UFFSET.CODEIN" campo="CODEIN_${param.contatore}" visibile="false" campoFittizio="true" definizione="T16;;;;G_CODEINSE" value="${item[1]}" />
		<gene:campoScheda  entita="UFFSET" where="UFFINT.CODEIN = UFFSET.CODEIN" campo="NOMSET_${param.contatore}" campoFittizio="true" definizione="N2;;A1092;;G_NOMSET" value="${item[2]}" >
			<c:if test='${modo ne "VISUALIZZA"}'>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</c:if>
			<c:choose>
				<c:when test='${empty item[3] or item[3] =="" }'>
					<c:set var="valoreStato" value="1"/>
				</c:when>
				<c:otherwise>
					<c:set var="valoreStato" value="2"/>
				</c:otherwise>
			</c:choose>
			
			<span style="BACKGROUND-COLOR: #EFEFEF;">&nbsp;&nbsp;&nbsp;Stato&nbsp;</span>
			&nbsp;
			<c:choose>
				<c:when test='${(modo eq "MODIFICA" or modo eq "NUOVO")}'>
					
					<select id="STATO_${param.contatore}" onChange="attivazione(this,'${param.contatore}')">
							<option value='1' <c:if test="${valoreStato eq '1' }">selected </c:if>>Attivo</option>
							<option value='2'  <c:if test="${valoreStato eq '2' }">selected </c:if>>Disattivo</option>
					</select>
				</c:when>
				<c:otherwise>
					${gene:if(valoreStato eq '1', 'Attivo' , 'Disattivo') }
				</c:otherwise>
			</c:choose>
			
		</gene:campoScheda>
		<gene:campoScheda  entita="UFFSET" where="UFFINT.CODEIN = UFFSET.CODEIN" campo="DATFIN_${param.contatore}" visibile="false" campoFittizio="true" definizione="D;;;DATA_ELDA;G_DATFINSE" value="${item[3]}" />
		
	</c:when>
	<c:otherwise>
		
		<gene:campoScheda  entita="UFFSET" where="UFFINT.CODEIN = UFFSET.CODEIN" campo="ID_${param.contatore}" visibile="false" campoFittizio="true" definizione="N10;1;;;G1IDRET"  />
		<gene:campoScheda  entita="UFFSET" where="UFFINT.CODEIN = UFFSET.CODEIN" campo="CODEIN_${param.contatore}" visibile="false" campoFittizio="true" definizione="T16;;;;G_CODEINSE" value="${codiceUfficio}" />
		<gene:campoScheda  entita="UFFSET" where="UFFINT.CODEIN = UFFSET.CODEIN" campo="NOMSET_${param.contatore}" campoFittizio="true" definizione="N2;;A1092;;G_NOMSET"  >
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<span style="BACKGROUND-COLOR: #EFEFEF;">&nbsp;&nbsp;&nbsp;Stato&nbsp;</span>
			&nbsp;
			<select id="STATO_${param.contatore}" onChange="attivazione(this,'${param.contatore}')">
					<option value='1' selected >Attivo</option>
					<option value='2'>Disattivo</option>
			</select>
		</gene:campoScheda>
		<gene:campoScheda  entita="UFFSET" where="UFFINT.CODEIN = UFFSET.CODEIN" campo="DATFIN_${param.contatore}" visibile="false" campoFittizio="true" definizione="D;;;DATA_ELDA;G_DATFINSE"  />
		
	</c:otherwise>
</c:choose>

<gene:javaScript>
	function attivazione(oggetto,indice){
		var azione = oggetto.value;
		var msg = "Confermi";
		if(azione=="1")
			msg+=" l'attivazione ";
		else
			msg+=" la disattivazione ";
		msg+="del settore?";
		var risposta=confirm(msg);
		if(risposta){
			var valore = null;
			if(azione!="1"){
				var dataOggi = new Date();
				var giorno = dataOggi.getDate();
				var mese = dataOggi.getMonth() + 1;
				var anno = dataOggi.getFullYear();
				valore= giorno + "/" + mese + "/" + anno;
			}
			setValue("UFFSET_DATFIN_" + indice,valore); 
		}else{
			//Si deve ripristinare il valore precedente
            var valoreOriginario = 0;
			if(azione=="1")
				valoreOriginario = 1;
			document.getElementById("STATO_" +indice).selectedIndex=valoreOriginario;
		}
	}
	
	
</gene:javaScript>

