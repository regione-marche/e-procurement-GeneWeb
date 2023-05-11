package it.eldasoft.gene.web.struts.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Classe che serve a rimappare la risposta del servizio di Maggioli 
 * per la verifica della firma elettronica.
 * <pre>"timestamps": [{
            "valid": true,
            "timestamp": "2017-05-03T09:07:41Z",
            "tsaCommonName": "ICEDTS01201703",
            "serial": "56ECF51",
            "policyId": "1.3.76.36.1.1.40",
            "tsa": "InfoCert Qualified Time Stamping Authority 2"
        }
    ]</pre>
 * @author gabriele.nencini
 *
 */
public class Timestamp {
	private boolean valid;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date timestamp;
	private String tsaCommonName;
	private String serial;
	private String policyId;
	private String tsa;
	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}
	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the tsaCommonName
	 */
	public String getTsaCommonName() {
		return tsaCommonName;
	}
	/**
	 * @param tsaCommonName the tsaCommonName to set
	 */
	public void setTsaCommonName(String tsaCommonName) {
		this.tsaCommonName = tsaCommonName;
	}
	/**
	 * @return the serial
	 */
	public String getSerial() {
		return serial;
	}
	/**
	 * @param serial the serial to set
	 */
	public void setSerial(String serial) {
		this.serial = serial;
	}
	/**
	 * @return the policyId
	 */
	public String getPolicyId() {
		return policyId;
	}
	/**
	 * @param policyId the policyId to set
	 */
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}
	/**
	 * @return the tsa
	 */
	public String getTsa() {
		return tsa;
	}
	/**
	 * @param tsa the tsa to set
	 */
	public void setTsa(String tsa) {
		this.tsa = tsa;
	}
	
	public String getValidItaliano() {
		return this.valid?"Si":"No";
	}
	
}
