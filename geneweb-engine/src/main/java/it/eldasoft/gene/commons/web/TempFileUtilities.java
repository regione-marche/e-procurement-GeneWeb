/*
 * Created on 08-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;

/**
 * Classe di utilita' per la gestione dei file temporanei
 *
 * @author Luca.Giacomazzo
 */
public class TempFileUtilities {

	/**
	 * @return Ritorna la directory temporanea dell'application server
	 */
	private static File getTempDir() {

		String tempDirName = System.getProperty("java.io.tmpdir");
		if (tempDirName != null) {
			// Creazione della directory temporanea se non esiste
			File tempDir = new File(tempDirName);
			if (tempDir.exists()) {
				return tempDir;
			} else {
				tempDir.mkdirs();
				return new File(tempDirName);
			}
		} else {
			throw new RuntimeException("La property di sistema relativa alla "
							+ "directory temporanea (java.io.tmpdir) non e' definita.");
		}
	}

	/**
	 * Creazione di un file temporaneo nella cartella temp e memorizzazione del
	 * nome del file nell'oggetto presente in sessione, predisposto alla rimozione
	 * dei file temporanei allo scadere della sessione. Il nome del file e' cosi'
	 * costruito: prefisso + numero random + suffisso, dove prefisso =
	 * nomeFile.substring(0, nomeFile.indexOf(".")); suffisso =
	 * nomeFile.substring(nomeFile.indexOf(".")+1);
	 *
	 * @param nomeFile
	 * @param session
	 * @return ritorna il file temporaneo nella cartella temp
	 *
	 * @throws IOException
	 */
	public static File getTempFile(String nomeFile, HttpSession session)
					throws IOException {

		File tempFile = File.createTempFile(FilenameUtils.getBaseName(nomeFile),
						".".concat(FilenameUtils.getExtension(nomeFile)),
						TempFileUtilities.getTempDir());
		// Se il file esiste, viene cancellato
		if (tempFile.exists()) {
			tempFile.delete();
		}
		// Salvataggio del nome del file temporaneo nella lista dei file
		// temporanei da cancellare allo scadere/invalidazione della sessione
		TempFileUtilities.registerTempFileForDeletion(tempFile.getName(), session);
		return tempFile;
	}

	/**
	 * Aggiunta nell'oggetto {@link TempFileDeleter} presente in sessione (con il
	 * nome <code>TEMP_FILES_NAME_SESSION</code> del nome del file nella lista dei
	 * file da cancellare. Se in sessione l'oggetto {@link TempFileDeleter} non
	 * esiste, viene prima creato.
	 *
	 * @param nomeTempFile il file che deve essere cancellato.
	 * @param session l'oggetto HttpSession associata all'utente.
	 */
	public static void registerTempFileForDeletion(String nomeTempFile,
					HttpSession session) {

		// Add chart to deletion list in session
		if (session != null) {
			TempFileDeleter tempFileDeleter = (TempFileDeleter) session.getAttribute(
							CostantiGenerali.TEMP_FILES_NAME_SESSION);
			if (tempFileDeleter == null) {
				tempFileDeleter = new TempFileDeleter();
				session.setAttribute(CostantiGenerali.TEMP_FILES_NAME_SESSION,
								tempFileDeleter);
			}
			tempFileDeleter.addTempFile(nomeTempFile);
		}
	}

	/**
	 * Ritorna il file temporaneo precedentemente creato.
	 *
	 * @param nomeFile il nome del file (comprensivo di estensione)
	 * @return ritorna il byte array del file
	 *
	 * @throws IOException
	 */
	public static byte[] getTempFile(String nomeFile) throws IOException {

		String pathTempDir = System.getProperty("java.io.tmpdir");
		File tempFile = new File(pathTempDir, nomeFile);
		byte[] fileInBytes = new byte[(int) tempFile.length()];
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(tempFile);
			inputStream.read(fileInBytes);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return fileInBytes;
	}


	/**
     * Creazione di un file temporaneo nella cartella temp e memorizzazione del
     * nome del file nell'oggetto presente in sessione, predisposto alla rimozione
     * dei file temporanei allo scadere della sessione.
     *
     * @param nomeFile
     * @param session
     * @return ritorna il file temporaneo nella cartella temp
     *
     * @throws IOException
     */
    public static File getTempFileSenzaNumeoRandom(String nomeFile, HttpSession session)
                    throws IOException {

        File tempFile = new File(TempFileUtilities.getTempDir(),nomeFile);
        // Se il file esiste, viene cancellato
        if (tempFile.exists()) {
            tempFile.delete();
        }
        // Salvataggio del nome del file temporaneo nella lista dei file
        // temporanei da cancellare allo scadere/invalidazione della sessione
        TempFileUtilities.registerTempFileForDeletion(tempFile.getName(), session);
        return tempFile;
    }
}
