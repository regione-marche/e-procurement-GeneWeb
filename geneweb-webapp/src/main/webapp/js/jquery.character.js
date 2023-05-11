/* 
* Gestione dei controlli sui caratteri ammessi
*/
	
$(window).on("load", function (){
	
	$("textarea, input").not('[name^="def"],[id^="def"]').alphanum({
		allow               : '!@#$%^&*()+=[]\\\';,/{}|":<>?~`.-_°§אטילעש£',    // Allow extra characters
	    disallow            : '',    // Disallow extra characters
	    allowSpace          : true,  // Allow the space character
	    allowNewline        : true,  // Allow the newline character \n ascii 10
	    allowNumeric        : true,  // Allow digits 0-9
	    allowUpper          : true,  // Allow upper case characters
	    allowLower          : true,  // Allow lower case characters
	    allowCaseless       : true,  // Allow characters that do not have both upper & lower variants eg Arabic or Chinese
	    allowLatin          : true,  // a-z A-Z
	    allowOtherCharSets  : false, // eg י, ֱ, Arabic, Chinese etc
	    forceUpper          : false, // Convert lower case characters to upper case
	    forceLower          : false, // Convert upper case characters to lower case
	    allowPlus           : true,  // Allow the + sign
	    allowMinus          : true,  // Allow the - sign
	    allowThouSep        : true,  // Allow the thousands separator, default is the comma eg 12,000
	    allowDecSep         : true,  // Allow the decimal separator, default is the fullstop eg 3.141
	    allowLeadingSpaces  : true
	});
	
});	