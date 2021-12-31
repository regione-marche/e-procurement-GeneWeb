

$(window).on("load", function (){
	
	var _tabellaC0CAMPI = null;
	
	_popolaTabellaC0CAMPI();
	
	/*
	 * Funzione di attesa
	 */
	function _wait() {
		document.getElementById('bloccaScreen').style.visibility='visible';
		$('#bloccaScreen').css("width",$(document).width());
		$('#bloccaScreen').css("height",$(document).height());
		document.getElementById('wait').style.visibility='visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	}

	/*
	 * Nasconde l'immagine di attesa
	 */
	function _nowait() {
		document.getElementById('bloccaScreen').style.visibility='hidden';
		document.getElementById('wait').style.visibility='hidden';
	}
	
	/*
	 * Crea tabella contenitore
	 */
	function _creaTabellaC0CAMPI() {
		
		var _table = $('<table/>', {"id": "tabellaC0CAMPI", "class": "scheda", "cellspacing": "0", "width" : "100%"});
		var _thead = $('<thead/>');
		var _tr1 = $('<tr/>', {"class": "intestazione"});
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_thead.append(_tr1);
		_table.append(_thead);
		
		$("#tabellaC0CAMPIcontainer").append(_table);
	}
	
	/*
	 * Popola tabella
	 */
	function _popolaTabellaC0CAMPI() {
		
		_wait();
		
		if (_tabellaC0CAMPI != null) {
			_tabellaC0CAMPI.destroy(true);
		}
		
		_creaTabellaC0CAMPI();
		_tabellaC0CAMPI = $('#tabellaC0CAMPI').DataTable( {
			"ajax": {
				url: "GetListaC0CAMPI.do",
				async: true,
				dataType: "json",
				complete: function(e){
					_nowait();
				}
			},
			"columnDefs": [
				{	
					"data": "C0C_TIP.value",
					"visible": true,
					"searchable": false,
					"targets": [ 0 ],
					"sTitle": "T",
					"sWidth": "40px"
				},
				{	
					"data": "C0C_CHI.value",
					"visible": true,
					"searchable": false,
					"targets": [ 1 ],
					"sTitle": "C",
					"sWidth": "40px",
					"align": "center"
				},
				{	
					"data": "COC_MNE_UNI.value",
					"visible": true,
					"searchable": true,
					"targets": [ 2 ],
					"sTitle": "Entita'",
					"sWidth": "70px",
					"render": function ( data, type, full, meta ) {
						var _val = full.COC_MNE_UNI.value
						return _val.substring(_val.search("\\.") + 1);
					}
				},
				{	
					"data": "COC_MNE_UNI.value",
					"visible": true,
					"searchable": true,
					"targets": [ 3 ],
					"sTitle": "Campo",
					"sWidth": "60px",
					"render": function ( data, type, full, meta ) {
						var _val = full.COC_MNE_UNI.value
						return _val.substring(0,_val.search("\\."));
					}
				},
				{	
					"data": "C0C_MNE_BER.value",
					"visible": true,
					"searchable": true,
					"targets": [ 4 ],
					"sTitle": "Mnemonico",
					"sWidth": "80px"
				},
				{	
					"data": "COC_DES.value",
					"visible": true,
					"searchable": true,
					"targets": [ 5 ],
					"sTitle": "Descrizione",
					"sWidth": "300px"
				},
				{	
					"data": "C0C_FS.value",
					"visible": true,
					"searchable": true,
					"targets": [ 6 ],
					"sTitle": "Form.",
					"sWidth": "70px"
				},
				{	
					"data": "C0C_TAB1.value",
					"visible": true,
					"searchable": true,
					"targets": [ 7 ],
					"sTitle": "Tab.",
					"sWidth": "70px"
				},
				{	
					"data": "COC_DOM.value",
					"visible": true,
					"searchable": true,
					"targets": [ 8 ],
					"sTitle": "Dom.",
					"sWidth": "70px"
				}
	        ],
	        "language": {
				"sEmptyTable":     "Nessuna riga trovata",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ righe",
				"sInfoEmpty":      "Nessuna riga trovata",
				"sInfoFiltered":   "(su _MAX_ righe totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca",
				"sZeroRecords":    "Nessuna riga trovata",
				"oPaginate": {
					"sFirst":      "Prima",
					"sPrevious":   "Precedente",
					"sNext":       "Successiva",
					"sLast":       "Ultima"
				}
			},
			"lengthMenu": [[20, 50, 100, 200, 500], ["20 righe", "50 righe", "100 righe", "200 righe", "500 righe"]],
	        "pagingType": "full_numbers",
	        "bLengthChange" : true,
	        "order": [[ 2, "asc" ], [3, "asc"] ],
	        "aoColumns": [
			     { "bSortable": true, "bSearchable": false },
			     { "bSortable": true, "bSearchable": false },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true }
			   ]
	    });
		
		
		$('#tabellaC0CAMPI thead th').eq(0).attr("title","Tipo campo");
		$('#tabellaC0CAMPI thead th').eq(1).attr("title","Chiave ?");
		$('#tabellaC0CAMPI thead th').eq(6).attr("title","Formato");
		$('#tabellaC0CAMPI thead th').eq(7).attr("title","Tabellato di riferimento");
		$('#tabellaC0CAMPI thead th').eq(8).attr("title","Dominio");
	
	}
	
	
	$("body").delegate('#tabellaC0CAMPI tr td:nth-child(3)', "mouseover",
		function(event) {
			var _c0e_nom = _tabellaC0CAMPI.cell( this ).data();
			_c0e_nom = _c0e_nom.substring(_c0e_nom.search("\\.") + 1);
			var _html = _getDescrizioneC0Entit(_c0e_nom);
			if (_html != "" && _html != null) {
				var _position = $(this).position();
				var _top = _position.top;
				var _left = _position.left + 140;
				var _div = $("<div/>", {"id": "div_descrizione",  "class": "tooltip ui-corner-all", "html": _html});
				var _height = _div.height();
				_div.css('left', _left);
				_div.css('top', _top);
				$(this).append(_div)
				_div.show(400);
			}

		}
	);
		
	$("body").delegate('#tabellaC0CAMPI tr', "mouseout",
		function() {
			$("#div_descrizione").remove();
		}
	);
	
	/*
	 * Lettura descrizione dell'entita'
	 */
	function _getDescrizioneC0Entit(c0e_nom) {
		var _c0e_des;
		$.ajax({
			type: "POST",
			dataType: "json",
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "GetDescrizioneC0Entit.do",
			data: "c0e_nom=" + c0e_nom, 
			success: function(data){
				if (data) {
					$.map( data, function( item ) {
						_c0e_des = item[0]
					});
	        	}
			}			
		});
		return _c0e_des;
	}
	
	
});
