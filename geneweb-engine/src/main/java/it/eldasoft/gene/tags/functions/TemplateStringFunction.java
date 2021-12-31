package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.tags.templates.JspTemplateTag;

import javax.servlet.jsp.PageContext;

public class TemplateStringFunction {
	public static String getString(PageContext context, String nome,
			String defaultVal) {
		JspTemplateTag template = JspTemplateTag
				.getLastTemplateTag(context);
		if (template != null) {
			// Aggiungo la stringa all'elenco delle stringhe
			if (template.getStrings().get(nome) != null) {
				return (String) template.getStrings().get(
						nome);
			} else {
				return defaultVal;
			}
		} else {
			throw new RuntimeException(
					"La funzione getString deve essere all'interno di un templateTag !");
		}
	}
}
