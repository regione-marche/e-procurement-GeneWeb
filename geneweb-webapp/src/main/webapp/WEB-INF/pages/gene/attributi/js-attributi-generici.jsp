<%
			/*
       * Created on: 15.54 14/01/2008
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
      /*
				Descrizione:
					Javascript utilizzati dagli attributi generici
				Creato da:
					Marco Franceschin
					Stefano Sabbadin
			*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<gene:javaScript>
function checkCondAttrib(valCampo,condizione,valNum, valStr, nomeCampo){	
	var visibile = isAttributoVisibile(valCampo,condizione,valNum,valStr);
	showObj("row"+nomeCampo,visibile);
}

// ritorna true se la condizione da verificare è soddisfatta, false altrimenti
function isAttributoVisibile(valCampo,condizione,valNum, valStr) {
	var visibile=false;
	
	// Se il campo utilizza una regola di visualizzazione, e il valore del campo
	// discriminante è vuoto, allora il campo non è certamente visibile.
	// Altrimenti, se il campo discriminante è non vuoto, si verifica se la regola
	// viene soddisfatta o meno 
	if (valCampo != "") {
		// controllo se la regola è di confronto con un valore numerico
		if(valStr==""){
			// Valore numerico
			try{
				valCampo=eval(valCampo);
				valNum=eval(valNum);
				if(condizione=="="){
					visibile=valCampo==valNum;
				}else if(condizione=="<>"){
					visibile=valCampo!=valNum;
				}else if(condizione=="<"){
					visibile=valCampo<valNum;
				}else if(condizione=="<="){
					visibile=valCampo<=valNum;
				}else if(condizione==">"){
					visibile=valCampo>valNum;
				}else if(condizione==">="){
					visibile=valCampo>=valNum;
				}
			}catch(e){
			}
		}else{
			if (condizione == "IN" || condizione == "NOT IN") {
				// Caso di confronto con un set di valori di un tabellato numerico
				var setValido=valStr.split(",");
				var pos=-1;
				for (var i=0; i<setValido.length; i++) {
					if (setValido[i]==valCampo) {
						pos = i;
						break;
					}
				}
				if(condizione=="IN"){
					visibile=pos>=0;
				}else{
					visibile=pos<0;
				}
			} else {
				// Valore stringa: si cerca per posizione in modo da cercare anche elenchi di valori separati da ","
				var pos=valStr.indexOf(valCampo);
				if(condizione=="="){
					visibile=pos>=0;
				}else{
					visibile=pos<0;
				}
			}
		}
	}
	return visibile;
}
</gene:javaScript>
