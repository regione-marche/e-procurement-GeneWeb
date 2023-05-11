package it.eldasoft.gene.web.struts.model;

import java.util.List;

/**
 * Classe che serve a rimappare la risposta del servizio di Maggioli 
 * per la verifica della firma elettronica.
 * <pre><code>{"verified":true,"signatures":[{"index":1,"valid":true,"verifyStatus":4,"signingTime":"23/03/2022 16:14:03 UTC","signingTimeISO":"2022-03-23T16:14:03Z","signerDigestAlgDes":"SHA-256","subjectCommonName":"URBANETTO PAOLO","subjectSerialNumber":"RBNPLA74A02F770B","issuerCommonName":"ArubaPEC S.p.A. NG CA 3","issuerOrganization":"ArubaPEC S.p.A.","validityIni":"2021-03-25T00:00:00Z","validityFin":"2024-03-24T23:59:59Z","desValidityIni":"25/03/2021 00:00:00","desValidityFin":"24/03/2024 23:59:59","serialNumber":"346EE056DD6034BBBC21ACCBBC27744","cns":false}]}</code></pre>
 * @author gabriele.nencini
 *
 */
public class FirmaCheck {
	private boolean verified;
	private List<Signature> signatures;
	private List<Timestamp> timestamps;
	List<String> listOfInvalidSignatures;
	List<String> listOfInvalidTimestamps;

	public boolean getVerified() {
		return verified;
	}

	public void setVerified(boolean value) {
		this.verified = value;
	}

	public List<Signature> getSignatures() {
		return signatures;
	}

	public void setSignatures(List<Signature> value) {
		this.signatures = value;
	}

	/**
	 * @return the timestamps
	 */
	public List<Timestamp> getTimestamps() {
		return timestamps;
	}

	/**
	 * @param timestamps the timestamps to set
	 */
	public void setTimestamps(List<Timestamp> timestamps) {
		this.timestamps = timestamps;
	}

	/**
	 * @return the listOfInvalidSignatures
	 */
	public List<String> getListOfInvalidSignatures() {
		return listOfInvalidSignatures;
	}

	/**
	 * @param listOfInvalidSignatures the listOfInvalidSignatures to set
	 */
	public void setListOfInvalidSignatures(List<String> listOfInvalidSignatures) {
		this.listOfInvalidSignatures = listOfInvalidSignatures;
	}

	/**
	 * @return the listOfInvalidTimestamps
	 */
	public List<String> getListOfInvalidTimestamps() {
		return listOfInvalidTimestamps;
	}

	/**
	 * @param listOfInvalidTimestamps the listOfInvalidTimestamps to set
	 */
	public void setListOfInvalidTimestamps(List<String> listOfInvalidTimestamps) {
		this.listOfInvalidTimestamps = listOfInvalidTimestamps;
	}

	@Override
	public String toString() {
		return "FirmaCheck [verified=" + verified + ", "
				+ (signatures != null ? "signatures=" + signatures : "") + "]";
	}
	
	
}
