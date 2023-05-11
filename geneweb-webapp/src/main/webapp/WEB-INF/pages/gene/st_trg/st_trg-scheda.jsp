<%
/*
 * Created on: 08-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="SttrgScheda" >
	<gene:setString name="titoloMaschera" value='Dettaglio tracciatura modifiche su DB' />
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="ST_TRG" gestisciProtezioni="true" plugin="it.eldasoft.gene.tags.gestori.plugin.GestoreDettaglioStoriaModifiche" >
		
			<gene:redefineInsert name="schedaModifica" />
			<gene:redefineInsert name="schedaNuovo" />
			<gene:redefineInsert name="documentiAzioni" />
			
			<gene:gruppoCampi idProtezioni="GEN" >
				<gene:campoScheda  nome="GEN">
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
		
				<gene:campoScheda campo="ST_SEQ" />
				<gene:campoScheda campo="ST_DATE" title="Data / ora"/>
				<gene:campoScheda campo="ST_OPERATION" title="Tipo operazione" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoTipoOperazione" />
				<gene:campoScheda campo="ST_TABLE" title="Entit&agrave;" />
			</gene:gruppoCampi>
			
			<gene:gruppoCampi idProtezioni="UTENTE_HOST" >
				<gene:campoScheda nome="UTENTE_HOST">
					<td colspan="2"><b>Dettagli utente / terminale</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="ST_SYSCON" title="Codice utente" />
				<gene:campoScheda campo="ST_SYSUTE" title="Nome utente" />
				<gene:campoScheda campo="ST_SESSION_USER" title="Nome utente sessione Oracle" />
				<gene:campoScheda campo="ST_HOST" title="Host" />
				<gene:campoScheda campo="ST_OSUSER" title="Utente sistema operativo" />
				<gene:campoScheda campo="ST_IP_ADDRESS" title="Indirizzo di rete del client" />
		
			</gene:gruppoCampi>
			
			<gene:gruppoCampi idProtezioni="CAMPICHIAVE" >
				<gene:campoScheda nome="CAMPICHIAVE">
					<td colspan="2"><b>Campi Chiave</b></td>
				</gene:campoScheda>		
				<gene:campoScheda campo="ST_KEY1" />
				<gene:campoScheda campo="ST_VAL1" />
				<gene:campoScheda campo="ST_KEY2" visibile="${not empty datiRigiga.ST_TRG_ST_KEY2}" />
				<gene:campoScheda campo="ST_VAL2" visibile="${not empty datiRigiga.ST_TRG_ST_VAL2}" />
				<gene:campoScheda campo="ST_KEY3" visibile="${not empty datiRigiga.ST_TRG_ST_KEY3}" />
				<gene:campoScheda campo="ST_VAL3" visibile="${not empty datiRigiga.ST_TRG_ST_VAL3}" />
				<gene:campoScheda campo="ST_KEY4" visibile="${not empty datiRigiga.ST_TRG_ST_KEY4}" />
				<gene:campoScheda campo="ST_VAL4" visibile="${not empty datiRigiga.ST_TRG_ST_VAL4}" />
				<gene:campoScheda campo="ST_KEY5" visibile="${not empty datiRigiga.ST_TRG_ST_KEY5}" />
				<gene:campoScheda campo="ST_VAL5" visibile="${not empty datiRigiga.ST_TRG_ST_VAL5}" />
				<gene:campoScheda campo="ST_KEY6" visibile="${not empty datiRigiga.ST_TRG_ST_KEY6}" />
				<gene:campoScheda campo="ST_VAL6" visibile="${not empty datiRigiga.ST_TRG_ST_VAL6}" />
				<gene:campoScheda campo="ST_KEY7" visibile="${not empty datiRigiga.ST_TRG_ST_KEY7}" />
				<gene:campoScheda campo="ST_VAL7" visibile="${not empty datiRigiga.ST_TRG_ST_VAL7}" />
			</gene:gruppoCampi>
			
			<gene:gruppoCampi idProtezioni="" >
				<gene:campoScheda addTr="false">
					<tr>
						<td colspan="2"><b>Campi modificati</b></td>
					</tr>
				</gene:campoScheda>
				
				<gene:campoScheda addTr="false">
					<tr>
						<td colspan="2">
							<table class="griglia" >
								<tbody>
									<thead>
										<tr>
											<td class="etichetta-dato" style=" border-right: #A0AABA 1px solid"><center><b>Nome campo</b></center></td>
											<td class="etichetta-dato" style=" border-right: #A0AABA 1px solid"><center><b>Valore nuovo</b></center></td>
											<td class="etichetta-dato"><center><b>Valore vecchio</b></center></td>
										</tr>
									</thead>
								</tbody>
								
							<c:forEach items="${listaCampiModificati}" var="campo" >
										<tr>
											<td class="valore-dato" >${campo.ST_CNAME}</td>
											<td class="valore-dato" >${campo.ST_C_NEWVALUE}</td>
											<c:choose>
												<c:when test='${not empty campo.ST_C_OLDVALUE}' >
													<td class="valore-dato" >${campo.ST_C_OLDVALUE}</td>
												</c:when>
												<c:otherwise>
													<td class="valore-dato" ><i>null</i></td>
												</c:otherwise>
											</c:choose>
										</tr>
							</c:forEach>
								
							</table>
						</td>
					</tr>
				</gene:campoScheda>
			</gene:gruppoCampi>
		
		</gene:formScheda>

	</gene:redefineInsert>
</gene:template>