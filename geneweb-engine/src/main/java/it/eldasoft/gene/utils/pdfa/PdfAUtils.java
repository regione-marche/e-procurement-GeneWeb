package it.eldasoft.gene.utils.pdfa;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.verapdf.gf.foundry.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.profiles.Profiles;



/**
 * Classe per la validazione del formato PDF-A, PDF-UA
 */
public class PdfAUtils {

	private static final Logger logger = Logger.getLogger(PdfAUtils.class);

	/**
	 * verifica la validita' di un documento in formato .PDF ed addizionalmente anche se e' in formato PDF-A 
	 */
	public static boolean checkIsPdfACompliant(byte[] stream, String fileName) {
		logger.debug("checkIsPdfACompliant BEGIN");
		boolean isPdfA;

		isPdfA = checkPdfComplianceVeraGreenfield(stream, fileName);
		//isPdfA = checkPdfCompliancePdfbox(stream, fileName, event, action);
		
    	// se il documento non rispetta il formato PDF-A 
    	// scrivi l'evento e segnala un messaggio di warning alla action chiamante 
		if( !isPdfA ) {
		  logger.debug("Il file " + fileName + " non e' un PDF-A valido");
		}
		
		logger.debug("checkIsPdfACompliant END (isPdfA=" + isPdfA + ", " + fileName + ")");
		return isPdfA;
	}

	/**
	 * verifica la validita' di un documento in formato .PDF ed addizionalmente anche se e' in formato PDF-A 
	 * @throws IOException 
	 */
	public static boolean checkIsPdfACompliant(File document, String fileName) throws IOException 
	{
		return checkIsPdfACompliant(FileUtils.readFileToByteArray(document), fileName);
	}
	

	/** 
	 ********************************************************************************
	 ********************************************************************************
	 * VeraGreenfield validator
	 ********************************************************************************
	 ******************************************************************************** 
	 */
	// elenco dei formati PDF/A, PDF/UA accettati PDF-A1, PDF-A2, PDF-A3, PDF-A4, PDF-UA1  
	private static final PDFAFlavour[] FLAVOURS = { 
			PDFAFlavour.PDFA_1_A, PDFAFlavour.PDFA_1_B,
			PDFAFlavour.PDFA_2_A, PDFAFlavour.PDFA_2_B, PDFAFlavour.PDFA_2_U,			
			PDFAFlavour.PDFA_3_A, PDFAFlavour.PDFA_3_B, PDFAFlavour.PDFA_3_U,			
			PDFAFlavour.PDFA_4, PDFAFlavour.PDFA_4_E , PDFAFlavour.PDFA_4_F,				
			PDFAFlavour.PDFUA_1
	};
	
	// profili di validazione per i vari formati di pdf-a, pdf-ua...
	private static PdfAValidationProfile[] PROFILES;
	
	/**
	 * verifica se un documento e' nel formato PDF-A con il provider VeraGreenfield  
	 */
	private static boolean checkPdfComplianceVeraGreenfield(byte[] stream, String fileName) {
		boolean isPdfA = false;		
		try {
			logger.debug("checkPdfComplianceVeraGreenfield init provider");
			VeraGreenfieldFoundryProvider.initialise();		// <= VeraGreenfield provider
			Profiles.getVeraProfileDirectory();						
	    	
			logger.debug("checkPdfComplianceVeraGreenfield init profiles");
			PROFILES = new PdfAValidationProfile[FLAVOURS.length];
	    	for (int i = 0; i < PROFILES.length; i++) {	    
	    		PROFILES[i] = new PdfAValidationProfile(FLAVOURS[i]);
		    }				

        	logger.debug("checkPdfComplianceVeraGreenfield checking profiles");
	    	InputStream is = null;
        	PDFAParser parser = null;
        	PdfAValidator validator = null; 
        	try {
        		// valida il file...
        		is = new ByteArrayInputStream(stream);
        		parser = Foundries.defaultInstance().createParser(is);            		
        		validator = new PdfAValidator(PROFILES); 
        		org.verapdf.pdfa.results.ValidationResult result = validator.validate(parser);        		
                if (result.isCompliant()) {
                	isPdfA = true;
                	logger.debug("checkPdfComplianceVeraGreenfield " + fileName + 
                				 " is " + validator.getProfile().getPDFAFlavour().name() + " compliant");
                }
        	} catch (Throwable t) {
 	        	logger.error("checkPdfComplianceVeraGreenfield", t);
            } finally {
            	if(validator != null)
    				try{ validator.close(); } catch(Exception e) {}
    			if(parser != null) 
    				try{ parser.close(); } catch(Exception e) {}
    			if(is != null) 
    	        	try{ is.close(); } catch(Exception e) {}
            }
	    		    	
		} catch (Throwable t) {
			logger.error("checkPdfComplianceVeraGreenfield", t);
		}	
		return isPdfA;
	}

}
