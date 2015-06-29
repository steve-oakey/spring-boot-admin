package de.codecentric.boot.admin.notify;

import java.util.Arrays;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@ConfigurationProperties("spring.boot.admin.notify")
public class MailNotifierProperties {

	private final String DEFAULT_SUBJECT = "#{application.name} (#{application.id}) is #{to.status}";
	private final String DEFAULT_TEXT = "#{application.name} (#{application.id})\nstatus changed from #{from.status} to #{to.status}\n\n#{application.healthUrl}";

	private final SpelExpressionParser parser = new SpelExpressionParser();

	/**
	 * recipients of the mail
	 */
	private String to[] = { "root@localhost" };

	/**
	 * cc-recipients of the mail
	 */
	private String cc[];

	/**
	 * sender of the change
	 */
	private String from = null;

	/**
	 * Mail Text. SpEL template using event as root;
	 */
	private Expression text;

	/**
	 * Mail Subject. SpEL template using event as root;
	 */
	private Expression subject;

	/**
	 * List of changes to ignore. Must be in Format OLD:NEW, for any status use
	 * * as wildcard, e.g. *:UP or OFFLINE:*
	 */
	private String[] ignoreChanges = { "UNKNOWN:UP" };

	/**
	 * Enables the mail notification.
	 */
	private boolean enabled = true;

	public MailNotifierProperties() {
		this.subject = parser.parseExpression(DEFAULT_SUBJECT, ParserContext.TEMPLATE_EXPRESSION);
		this.text = parser.parseExpression(DEFAULT_TEXT, ParserContext.TEMPLATE_EXPRESSION);
	}

	public String[] getTo() {
		return to;
	}

	public String getFrom() {
		return from;
	}

	public String[] getCc() {
		return cc;
	}

	public Expression getSubject() {
		return subject;
	}

	public Expression getText() {
		return text;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setSubject(String subject) {
		this.subject = parser.parseExpression(subject, ParserContext.TEMPLATE_EXPRESSION);
	}

	public void setText(String text) {
		this.text = parser.parseExpression(text, ParserContext.TEMPLATE_EXPRESSION);
	}

	public void setIgnoreChanges(String[] ignoreChanges) {
		String[] copy = Arrays.copyOf(ignoreChanges, ignoreChanges.length);
		Arrays.sort(copy);
		this.ignoreChanges = copy;
	}

	public String[] getIgnoreChanges() {
		return Arrays.copyOf(ignoreChanges, ignoreChanges.length);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
