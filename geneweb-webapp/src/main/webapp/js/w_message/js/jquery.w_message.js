/*
 * Gestione messaggistica interna
 */

 	var _tabellaMessaggiIn = null;
	var _tabellaMessaggiOut = null;
	var _enabledSend = false;
	var _offsetLeft = -400;
	var _offsetTop = 0;
	var _ctx;
	
	var _indexstep = 50;
	
	var _indexstartmessagein = 0;
	var _indexendmessagein = 0;
	var _totalmessagein = 0;

	var _indexstartmessageout = 0;
	var _indexendmessageout = 0;
	var _totalmessageout = 0;
	
	/*
	 * init: inizializza il contesto applicativo
	 * 
	 * creamc: crea l'oggetto base con il contatore messaggi non letti
	 * 
	 * Ha bisogno dei seguenti parametri (nell'ordine):
	 * 1. anchor: oggetto di riferimento
	 * 2. position: posizione rispetto all'oggetto di riferimento (PREPEND o APPEND)
	 * 3. send: definisce se e' possibile creare nuovi messaggi (TRUE o FALSE)
	 * 4. offsetLeft: offset, da sinistra, della finestra messaggi rispetto all'oggetto di riferimento
	 * 5. offsetTop: offset, dall'alto, della finestra messaggi rispetto all'oggetto di riferimento
	 * 
	 * Esempio
	 * mymessage.creamc($(".info-utente"), "PREPEND", true, -400, 0);
	 * 
	 */
	var mymessage = mymessage || (function(){
		var _args = {};
		return {
			init : function(Args) {
				_args = Args;
				_ctx = _args[0];
			},
			creamc: function(anchor, position, send, offsetLeft, offsetTop) {
				_creamcTimeout(anchor, position, send, offsetLeft, offsetTop);
			}
		};
	}());
 	
	
	function _creamcTimeout(anchor, position, send, offsetLeft, offsetTop) {
		setTimeout(function(){
			
			_enabledSend = send;
			if (offsetLeft != null) {
				_offsetLeft = offsetLeft;
			}
			if (offsetTop != null) {
				_offsetTop = offsetTop;
			}
			
			var _spanm = $("<span/>");
			var _startmessage = $("<span/>",{"id": "startmessage", "class": "startmessage", "title": "Leggi i messaggi"});
			_spanm.append(_startmessage);
			
			var _divmc = $("<div/>", {"id": "mc", "class": "mc"});
			var _divmcinout = $("<div/>", {"id": "mcinout"});
			_divmc.append(_divmcinout);
			_spanm.append(_divmc);
			
			_divmwait = $("<div/>",{"id": "divmwait", "class": "mwait"});
			_spanmwait = $("<span/>",{"id": "divmspan", "class": "mwait", "text": "elaborazione in corso..."});
			_spanmwait.css('margin-left', "200px");
			_spanmwait.css('margin-top', "200px");
			_divmwait.append(_spanmwait);
			_divmwait.css('left', 10);
			_divmwait.css('top', 10);
			_divmc.append(_divmwait);
	
			var _mcontatore = $("<span/>", {"class": "mcontatore"});
			_mcontatore.hide();
			_startmessage.append(_mcontatore);
			
			if (position == "PREPEND") {
				anchor.prepend(_spanm);
				_aggiornaContatore();
			} else if (position == "APPEND") {
				anchor.append(_spanm);
				_aggiornaContatore();
			}
			
			_creaeventimc();
		
		}, 300);
	}
	
	
	function _remove() {
		$("#mcin").remove();
		$("#mcout").remove();
		$("#mcnew").remove();
		$("#divmwait").hide();
		if (_tabellaMessaggiIn != null) {
			_tabellaMessaggiIn.destroy(true);
		}
		if (_tabellaMessaggiOut != null) {
			_tabellaMessaggiOut.destroy(true);
		}
	}
	
	
	function _aggiornaContatore() {
		$.ajax({
            type: "GET",
            dataType: "json",
            async: true,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: _ctx + "/GetContaNonLettiW_MESSAGE.do",
            success: function(data){
            	if (data.numeromessagginonletti > 0) {
            		$(".mcontatore").text(data.numeromessagginonletti).show(400);
            	} else {
            		$(".mcontatore").hide(400);
            	}
            }
        });
	}
	
	
	function _popolaTabellaMessaggiIn() {
		
		var _divmcin = $("<div/>", {"id": "mcin"});
		$("#mcinout").append(_divmcin);
		
		var _table = $('<table/>', {"id": "tabellamessaggiin", "class": "messaggi"});
		var _thead = $('<thead/>');
		var _tr = $('<tr/>');
		_tr.append($('<th/>'));
		_thead.append(_tr);
		_table.append(_thead);
		var _tbody = $('<tbody/>');
		_table.append(_tbody);
		_divmcin.append(_table);
		
		var _searchicon = '<span class="msearch"></span>';
		
		_tabellaMessaggiIn = $('#tabellamessaggiin').DataTable({
			"columnDefs": [
				{
					"data": "message_id",
					"visible": true,
					"targets": [ 0 ],
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						
						var _markasnew = $("<span/>",{"class": "mmarkasnew", "text": "Non letto"});
						_div.append(_markasnew);
						if (full.message_recipient_read == "1") {
							_markasnew.hide();
						}
						
						var _m = $("<span/>",{"text": full.message_sender, "class": "message_sender"});
						_div.append(_m);
						var _d = $("<span/>",{"text": full.message_date, "class": "message_date"});
						_div.append(_d);
						var _sb = $("<div/>",{"class": "blank"});
						_div.append(_sb);
						var _o = $("<span/>",{"class": "message_subject"});
						if (full.message_subject != null) {
							var msg_sub  = full.message_subject.replace(/(\r\n|\n|\r)/g,"<br/>");
							_o.append(msg_sub);
						}
						_div.append(_o);
						var _delete = $("<span/>",{"class":"mindelete", "title" : "Elimina"});
						_div.append(_delete);
						return _div.html();
					}					
				}			
			],
			"language": {
				"sEmptyTable":     "Nessun messaggio trovato",
				"sInfo":           "da _START_ a _END_ di _TOTAL_ messaggi",
				"sInfoEmpty":      "Nessun messaggio trovato",
				"sInfoFiltered":   "(su _MAX_ messaggi)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_ messaggi",
				"sLoadingRecords": "",
				"sProcessing":     "",
				"sSearch":         _searchicon,
				"sZeroRecords":    "",
				"oPaginate": {
					"sFirst":      "<<",
					"sPrevious":   "<",
					"sNext":       ">",
					"sLast":       ">>"
				}
			},
			"lengthMenu": [[10, 25, 50, 100, 200], ["10", "25", "50", "100", "200"]],
			"pagingType": "simple_numbers",
			"bLengthChange" : false,
			"bProcessing": true,
			"bZeroRecords": false,
			"scrollY": "330px",
			"paging": false,
			"ordering": false,
			"info": false	
		});
		
		_indexstartmessagein = 0;
		_indexendmessagein = _indexstep;
		_popolaTabellaMessaggiInAdd();
		
		$("#tabellamessaggiin_wrapper > div > div > div > table thead").hide();
		$("#tabellamessaggiin_wrapper > div > div.dataTables_scrollBody").css("border-bottom","0px");
		
		$("#tabellamessaggiin_wrapper > div > div.dataTables_scrollBody").on('scroll', function() {
			if (_totalmessagein > _indexendmessagein) {
				if($(this).scrollTop() + $(this).innerHeight() >= this.scrollHeight) {
					_indexstartmessagein = _indexstartmessagein + _indexstep;
					_indexendmessagein = _indexendmessagein + _indexstep;
					_popolaTabellaMessaggiInAdd();
				}
			}
		});
		
		$("#tabellamessaggiin_filter").show();
		
		var _title = $("<span/>", {"class": "mtitle", "text": "Messaggi in arrivo"});
		$("#mcin").prepend(_title);

		if (_enabledSend == true) {
			var _moutbutton = $("<span/>", {"id": "moutbutton", "class": "moutbutton", "title" : "Lista dei messaggi inviati"});
			$("#mcin").append(_moutbutton);
			var _mnewbutton = $("<span/>", {"id": "mnewbutton", "class": "mnewbutton", "title" : "Nuovo messaggio"});
			_mnewbutton.css("left","50px");
			$("#mcin").append(_mnewbutton);
		}
		
		$("#tabellamessaggiin tbody").on("click","tr", function () {
			var _new = $(this).find(".mmarkasnew");
			if (_new.length > 0) {
				var _message_id = _tabellaMessaggiIn.row($(this)).data().message_id;
				
				if (_new.is(":visible")) {
					$.ajax({
			    		type: "POST",
			    		async: true,
			    		dataType: "json",
			    		url: _ctx + "/SetW_MESSAGE.do",
			    		data : {
			    			type: "IN",
			    			operation: "READ",
			    			id: _message_id,
			    			recipient: "",
			    			subject: "",
			    			body: "",
			    			read: "1"
						},
			    		complete: function() {
			    			_aggiornaContatore();
			    			_new.hide();		
			    		}
			    	});
				} else {
					$.ajax({
			    		type: "POST",
			    		async: true,
			    		dataType: "json",
			    		url: _ctx + "/SetW_MESSAGE.do",
			    		data : {
			    			type: "IN",
			    			operation: "READ",
			    			id: _message_id,
			    			recipient: "",
			    			subject: "",
			    			body: "",
			    			read: "0"
						},
			    		complete: function() {
			    			_aggiornaContatore();
			    			_new.show();		
			    		}
			    	});
				}

	        } 
	    });
	}

	
	function _popolaTabellaMessaggiInAdd() {
		
		$("#divmwait").show();
		
		$.ajax({
    		type: "POST",
    		async: true,
    		dataType: "json",
    		"url": _ctx + "/GetListaW_MESSAGE.do?type=IN&start=" + _indexstartmessagein + "&end=" + _indexendmessagein,
			"success" : function(res) {
				_totalmessagein = res.iTotalRecords;
				_tabellaMessaggiIn.rows.add(res.data).draw(false);
			},
			"error": function() {
				_totalmessagein = 0;
				$("#divmwait").hide();
			},
    		"complete": function() {
    			$("#divmwait").hide();
    		}
    	});
	}
	

	function _popolaTabellaMessaggiOut() {
		
		var _divmcout = $("<div/>", {"id": "mcout"});
		$("#mcinout").append(_divmcout);
		
		var _table = $('<table/>', {"id": "tabellamessaggiout", "class": "messaggi"});
		var _thead = $('<thead/>');
		var _tr = $('<tr/>');
		_tr.append($('<th/>'));
		_thead.append(_tr);
		_table.append(_thead);
		var _tbody = $('<tbody/>');
		_table.append(_tbody);
		_divmcout.append(_table);
		
		var _searchicon = '<span class="msearch"></span>';
		
		_tabellaMessaggiOut = $('#tabellamessaggiout').DataTable({
			"columnDefs": [
				{
					"data": "message_id",
					"visible": true,
					"targets": [ 0 ],
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						var _m = $("<span/>",{"text": full.recipientlist, "class": "message_recipient"});
						_div.append(_m);
						var _d = $("<span/>",{"text": full.message_date, "class": "message_date"});
						_div.append(_d);
						var _sb = $("<div/>",{"class": "blank"});
						_div.append(_sb);
						var _o = $("<span/>",{"class": "message_subject"});
						if (full.message_subject != null) {
							var msg_sub  = full.message_subject.replace(/(\r\n|\n|\r)/g,"<br/>");
							_o.append(msg_sub);
						}
						_div.append(_o);
						var _delete = $("<span/>",{"class":"moutdelete", "title" : "Elimina"});
						_div.append(_delete);
						return _div.html();
					}					
				}			
			],
			"language": {
				"sEmptyTable":     "Nessun messaggio trovato",
				"sInfo":           "da _START_ a _END_ di _TOTAL_ messaggi",
				"sInfoEmpty":      "Nessun messaggio trovato",
				"sInfoFiltered":   "(su _MAX_ messaggi)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_ messaggi",
				"sLoadingRecords": "",
				"sProcessing":     "",
				"sSearch":         _searchicon,
				"sZeroRecords":    "",
				"oPaginate": {
					"sFirst":      "<<",
					"sPrevious":   "<",
					"sNext":       ">",
					"sLast":       ">>"
				}
			},
			"lengthMenu": [[10, 25, 50, 100, 200], ["10", "25", "50", "100", "200"]],
			"pagingType": "simple_numbers",
			"bLengthChange" : false,
			"bProcessing": true,
			"scrollY": "330px",
			"paging": false,
			"ordering": false,
			"info": false	
		});
		
		_indexstartmessageout = 0;
		_indexendmessageout = _indexstep;
		_popolaTabellaMessaggiOutAdd();
		
		$("#tabellamessaggiout_wrapper > div > div > div > table thead").hide();
		$("#tabellamessaggiout_wrapper > div > div.dataTables_scrollBody").css("border-bottom","0px");
		
		$("#tabellamessaggiout_wrapper > div > div.dataTables_scrollBody").on('scroll', function() {
			if (_totalmessageout > _indexendmessageout) {
				if($(this).scrollTop() + $(this).innerHeight() >= this.scrollHeight) {
					_indexstartmessageout = _indexstartmessageout + _indexstep;
					_indexendmessageout = _indexendmessageout + _indexstep;
					_popolaTabellaMessaggiOutAdd();
				}
			}
		});
			
		$("#tabellamessaggiout_filter").show();
		
		var _title = $("<span/>", {"class": "mtitle", "text": "Messaggi inviati"});
		$("#mcout").prepend(_title);
		
		var _minbutton = $("<span/>", {"id": "minbutton", "class": "minbutton", "title" : "Lista dei messaggi in arrivo"});
		$("#mcout").append(_minbutton);
		
		if (_enabledSend == true) {
			var _mnewbutton = $("<span/>", {"id": "mnewbutton", "class": "mnewbutton", "title" : "Nuovo messaggio"});
			_mnewbutton.css("left","50px");
			$("#mcout").append(_mnewbutton);
		}
		
	}
	
	
	function _popolaTabellaMessaggiOutAdd() {
		
		$("#divmwait").show();
		
		$.ajax({
    		type: "POST",
    		async: true,
    		dataType: "json",
    		"url": _ctx + "/GetListaW_MESSAGE.do?type=OUT&start=" + _indexstartmessageout + "&end=" + _indexendmessageout,
			"success" : function(res) {
				_totalmessageout = res.iTotalRecords;
				_tabellaMessaggiOut.rows.add(res.data).draw(false);
			},
			"error": function() {
				_totalmessageout = 0;
				$("#divmwait").hide();
			},
    		"complete": function() {
    			$("#divmwait").hide();
    		}
    	});
	}
	
	
	function _delete(_type, _message_id) {
		$.ajax({
    		type: "POST",
    		async: true,
    		dataType: "json",
    		url: _ctx + "/SetW_MESSAGE.do",
    		data : {
    			type: _type,
    			operation: "DELETE",
    			id: _message_id,
    			recipient: "",
    			subject: "",
    			body: ""
			},
    		error: function(e) {
    			if (e.status != 200)
					alert("Errore nella cancellazione del messaggio");
    		},
    		complete: function() {
    			_aggiornaContatore();
    		}
    	});
	}
	
	
	function _nuovoMessaggio() {
		var _divmcnew = $("<div/>", {"id": "mcnew"});
		$("#mcinout").append(_divmcnew);

		var _title = $("<span/>", {"class": "mtitle", "text": "Nuovo messaggio"});
		_divmcnew.append(_title);

		var _table = $('<table/>', {"id": "newmessage", "class": "newmessagge"});
		var _tbody = $('<tbody/>');

		var _tr = $('<tr/>');
		_tr.append($('<td/>',{"text":"Agli utenti", "id": "etichettarecipientusr", "class": "etichetta"}));
		var _inputrecipientusr = $("<input/>",{"id":"inputrecipientusr","class":"new"});
		var _td = $('<td/>');
		_td.append(_inputrecipientusr);
		_tr.append(_td);
		_tbody.append(_tr);
		
		var _tr = $('<tr/>');
		_tr.append($('<td/>',{"text":"Messaggio", "id": "etichettasubject", "class": "etichetta"}));
		var _inputsubject = $("<textarea/>",{"id":"inputsubject", "class":"new", "rows": "10",  "cols": "50"});
		var _td = $('<td/>');
		_td.append(_inputsubject);
		_tr.append(_td);
		_tbody.append(_tr);
		
		_table.append(_tbody);
		_divmcnew.append(_table);
		
		var _minbutton = $("<span/>", {"id": "minbutton", "class": "minbutton", "title" : "Lista dei messaggi in arrivo"});
		_divmcnew.append(_minbutton);
		
		var _moutbutton = $("<span/>", {"id": "moutbutton", "class": "moutbutton", "title" : "Lista dei messaggi inviati"});
		_moutbutton.css("left","50px");
		_divmcnew.append(_moutbutton);
		
		var _msendbutton = $("<span/>", {"id": "msendbutton", "class": "msendbutton", "title" : "Invia"});
		_msendbutton.css("left","90px");
		_divmcnew.append(_msendbutton);
		
		var _recipientusr;
		
		$.ajax({
			type: "POST",
			dataType: "json",
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: _ctx + "/GetRecipientList.do",
			success: function(json){
				_recipientusr = json.usr;
			}
		});
		
		$('#inputrecipientusr').textext({
		    plugins: 'autocomplete suggestions tags filter arrow',
		    suggestions: _recipientusr,
		    html: {
                tag: '<div class="text-tag"><div class="text-button"><span class="text-label text-label-usr"/><a class="text-remove"/></div></div>'
            },
		    ext: {
		        itemManager: {
		            stringToItem: function(str)
		            {
		                var thisindex = -1;
		                var thisvalue = "";
		                var thistype = "";
		                for (index = 0; index < _recipientusr.length; index++) {
		                    if (_recipientusr[index].descr == str) {
		                       thisindex = index;
		                    }
		                }
		                if (thisindex>-1) {
		                	thistype = _recipientusr[thisindex].type;
		                	thisvalue = _recipientusr[thisindex].val;
		                }
		                return { type: thistype, descr: str, val: thisvalue }; 
		            },
		            itemToString: function(item)
		            {
	            		return item.descr;
		            },
		            compareItems: function(item1, item2)
		            {
		                return item1.descr == item2.descr;
		            }		            
		        }
		    }
		});
	}
	
	
	function _inviaMessaggio() {
		var _inputrecipientusr = $('#inputrecipientusr').textext()[0].hiddenInput().val();
		var _recipientusrJSON = $.parseJSON(_inputrecipientusr);
		var _recipientusr = "";
		
		$.map( _recipientusrJSON, function( item ) {
			if (_recipientusr != "") _recipientusr = _recipientusr + ",";
			_recipientusr = _recipientusr + item.val;
		});
		
		var _subject = $("#inputsubject").val();
		
		if ((_recipientusr == null || _recipientusr == "")) {
			$("#etichettarecipientusr").addClass("etichettared");
		} else {
			$("#etichettarecipientusr").removeClass("etichettared");
		}

		if (_subject == null || _subject == "") {
			$("#etichettasubject").addClass("etichettared");
		} else {
			$("#etichettasubject").removeClass("etichettared");
		}
		
		if (((_recipientusr != "" && _recipientusr != null)) && _subject != "" && _subject != null) {
						
			$("#divmwait").show();
			
	    	$.ajax({
	    		type: "POST",
	    		async: true,
	    		dataType: "json",
	    		url: _ctx + "/SetW_MESSAGE.do",
	    		data : {
	    			type: "OUT",
	    			operation: "INSERT",
	    			id: "",
	    			recipientuff: "",
	    			recipientusr: _recipientusr,
	    			subject: _subject,
	    			body: ""
				},
	    		error: function(e) {
	    			$("#divmwait")
	    			alert("Errore nell'invio del messaggio");
	    		},
	    		complete: function() {
	    			if ($("#mc").is(":visible")) {
	    				_remove();
	    				_popolaTabellaMessaggiOut();
	    			}
	    			_aggiornaContatore();
	    			$("#divmwait").hide();
	    		}
	    	});
		}
	}
	
	
	function _creaeventimc() {
		$("body").mouseup(
			function(e) {
				var subject = $("#mc"); 
				if(e.target.id != subject.attr('id') && !subject.has(e.target).length) {
					if ($("#mc").is(":visible")) {
						if ($("#mcnew").is(":visible")) {
						
						} else {
							$("#mc").hide(400);
							_remove();
						}
					}
				}
			}
		);
		
		$("body").on("click","#startmessage", 
			function() {
				if ($("#mc").is(":visible")) {
					$("#mc").hide(400);
					_remove();
				} else {
					$("#mc").css('margin-left', _offsetLeft);
					$("#mc").css('margin-top', _offsetTop);
					$("#mc").show(400);
					_popolaTabellaMessaggiIn();
				}
			}
		);

		$("body").on("click",".moutdelete", 
			function() {
				var _message_id = _tabellaMessaggiOut.row($(this).parents('tr')).data().message_id;
				_delete('OUT',_message_id);
				_tabellaMessaggiOut.row($(this).parents('tr')).remove().draw(false);
				
			}
		);
		
		$("body").on("click",".mindelete", 
			function() {
				var _message_id = _tabellaMessaggiIn.row($(this).parents('tr')).data().message_id;
				_delete('IN',_message_id);
				_tabellaMessaggiIn.row($(this).parents('tr')).remove().draw(false);
			}
		);
		
		$("body").on("click","#minbutton", 
			function() {
				_remove();
				_popolaTabellaMessaggiIn();
			}
		);
		
		$("body").on("click","#moutbutton", 
			function() {
				_remove();
				_popolaTabellaMessaggiOut();
			}
		);
		
		$("body").on("click","#msendbutton", 
			function() {
				_inviaMessaggio();
			}
		);

		$("body").on("click","#mnewbutton", 
			function() {
				_remove();
				_nuovoMessaggio();
			}
		);
	}
	

