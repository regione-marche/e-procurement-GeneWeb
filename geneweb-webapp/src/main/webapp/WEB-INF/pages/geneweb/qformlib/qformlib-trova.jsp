
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="TrovaQform" >

	<gene:setString name="titoloMaschera" value="Ricerca modelli Q-form"/>
	
	<gene:redefineInsert name="corpo">
  	<gene:formTrova entita="QFORMLIB" gestisciProtezioni="true" >
			<gene:gruppoCampi idProtezioni="Gen">
				<gene:campoTrova campo="CODMODELLO"/>
				<gene:campoTrova campo="TITOLO"/>
				<gene:campoTrova campo="TIPOLOGIA"/>
				<gene:campoTrova campo="MODINTERNO"/>
				<gene:campoTrova campo="STATO"/>
			</gene:gruppoCampi>
			
		</gene:formTrova>

		
		
	</gene:redefineInsert>
</gene:template>
