<%/*
   * Created on 20-nov-2014
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI EDIT
  // DEL DETTAGLIO DI UN DOCUMENTO ASSOCIATO RELATIVA AI DATI EFFETTIVI
%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>


<script type="text/javascript">
<!--

	arrayProprieta = [  ];

	tipoProprieta = [  ];
	
-->
</script>

			<% // Valorizzare con il nome del gestore predisposto per il salvataggio delle proprieta' nella W_CONFIG. %>
			<% // (indicare package e classe). Il gestore deve estendere la classe %>
			<% // it.eldasoft.gene.web.struts.w_config.AbstractGestoreProprieta    %>
			<input type="hidden" name="gestoreProprieta" value="" />

			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp1" >Descrizione della proprietà 1</span>
				</td>
				<td class="valore-dato">
					<!--input type="hidden" id="codapp1" name="codapp" value="" />
					<input type="hidden" id="chiave1" name="chiave" value="" maxlength="60" />
					<input type="hidden" id="prop1" name="valore" size="50" maxlength="500" /-->
					<span id="prop1"></span> 
					<span id="link1" style="margin-left: 100px; visibility: none;">
						<a href="javascript=''">Modifica password</a>
					</span>
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato" >
					<span id="titoloProp2" >Descrizione della proprietà 2</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp2" name="codapp" value="" />
					<input type="hidden" id="chiave2" name="chiave" value="" maxlength="60" />
					<input type="text" id="prop2" name="valore" size="50" maxlength="500" />
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato"  >
					<span id="titoloProp3" >Descrizione della proprietà 3</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp3" name="codapp" value="" />
					<input type="hidden" id="chiave3" name="chiave" value="" maxlength="60" />
					<input type="text" id="prop3" name="valore" size="50" maxlength="500" />
				</td>
			</tr>

			<tr id="">
				<td class="etichetta-dato"  >
					<span id="titoloProp4" >Descrizione della proprietà 4</span>
				</td>

				<td class="valore-dato">
					<input type="hidden" id="codapp4" name="codapp" value="" />
					<input type="hidden" id="chiave4" name="chiave" value="" maxlength="60" />
					<input type="text" id="prop4" name="valore" size="50" maxlength="500" />
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato"  >
					<span id="titoloProp5" >Descrizione della proprietà 5</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp5" name="codapp" value="" />
					<input type="hidden" id="chiave5" name="chiave" value="" maxlength="60" />
					<input type="text" id="prop5" name="valore" size="50" maxlength="500" />
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato"  >
					<span id="titoloProp6" >Descrizione della proprietà 6</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp6" name="codapp" value="" />
					<input type="hidden" id="chiave6" name="chiave" value="" maxlength="60" />
					<input type="text" id="prop6" name="valore" size="50" maxlength="500" />
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato"  >
					<span id="titoloProp7" >
					<p>
					Descrizione della proprietà 7
					<br>
					Descrizione della proprietà 7
					<br>
					Descrizione della proprietà 7
					</p></span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp7" name="codapp" value="" />
					<input type="hidden" id="chiave7" name="chiave" value="" maxlength="60" />
					<input type="text" id="prop7" name="valore" size="50" maxlength="500" />
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato"  >
					<span id="titoloProp8" >Descrizione della proprietà 8</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp8" name="codapp" value="" />
					<input type="hidden" id="chiave8" name="chiave" value="" maxlength="60" />
					<input type="text" id="prop8" name="valore" size="50" maxlength="500" />
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato"  >
					<span id="titoloProp9" >Descrizione della proprietà 9</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp9" name="codapp" value="" />
					<input type="hidden" id="chiave9" name="chiave" value="" maxlength="60" />
					<input type="text" id="prop9" name="valore" size="50" maxlength="500" />
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato"  >
					<span id="titoloProp10" >Descrizione della proprietà 10</span>
				</td>
				<td class="valore-dato" >
					<!--input type="hidden" id="codapp10" name="codapp" value="" />
					<input type="hidden" id="chiave10" name="chiave" value="" maxlength="60" />
					<input type="password" id="prop10" name="valore" size="50" maxlength="500" /-->
					
					<span id="prop10"></span>
					<span id="link10" style="margin-left: 100px; visibility: none;">
						<a href="javascript=''">Modifica password</a>
					</span>
					
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato"  >
					<span id="titoloProp11" >Descrizione della proprietà 11</span>
				</td>
				<td class="valore-dato" >
					<input type="hidden" id="codapp11" name="codapp" value="" />
					<input type="hidden" id="chiave11" name="chiave" value="" />
					<input type="text" id="prop11" name="valore" size="50" maxlength="500" />
				</td>
			</tr>

			<tr id="">
				<td class="etichetta-dato"  >
					<span id="titoloProp12" >Descrizione della proprietà 12</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp12" name="codapp" value="" />
					<input type="hidden" id="chiave12" name="chiave" value="" maxlength="60" />
					<input type="text" id="prop12" name="valore" size="50" maxlength="500" />
				</td>
			</tr>
			<tr id="">
				<td class="etichetta-dato"  >
					<span id="titoloProp13" >Descrizione della proprietà 13</span>
				</td>
				<td class="valore-dato">
					<input type="hidden" id="codapp13" name="codapp" value="" />
					<input type="hidden" id="chiave13" name="chiave" value="" maxlength="60" />
					<input type="text" id="prop13" name="valore" size="50" maxlength="500" />
				</td>
			</tr>