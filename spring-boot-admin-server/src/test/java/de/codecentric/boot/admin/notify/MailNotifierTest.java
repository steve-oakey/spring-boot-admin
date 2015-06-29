package de.codecentric.boot.admin.notify;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import de.codecentric.boot.admin.event.ClientApplicationStatusChangedEvent;
import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.model.StatusInfo;

public class MailNotifierTest {

	@Mock
	private MailSender sender;

	@Mock
	private MailNotifierProperties properties;

	@InjectMocks
	private MailNotifier notifier;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		when(properties.getTo()).thenReturn(new String[] { "foo@bar.com" });
		when(properties.getCc()).thenReturn(new String[] { "bar@foo.com" });
		when(properties.getFrom()).thenReturn("SBA <no-reply@example.com>");
		when(properties.getIgnoreChanges()).thenReturn(new String[0]);
	}

	@Test
	public void test_onApplicationEvent() {
		SpelExpressionParser parser = new SpelExpressionParser();
		when(properties.getText()).thenReturn(parser.parseExpression("Text", ParserContext.TEMPLATE_EXPRESSION));
		when(properties.getSubject()).thenReturn(parser.parseExpression("Subject", ParserContext.TEMPLATE_EXPRESSION));
		notifier.onApplicationEvent(new ClientApplicationStatusChangedEvent(new Object(),
				Application.create("App").withId("-id-").withHealthUrl("http://health").build(), StatusInfo.ofDown(),
				StatusInfo.ofUp()));

		SimpleMailMessage expected = new SimpleMailMessage();
		expected.setTo(new String[] { "foo@bar.com" });
		expected.setCc(new String[] { "bar@foo.com" });
		expected.setFrom("SBA <no-reply@example.com>");
		expected.setText("Text");
		expected.setSubject("Subject");

		verify(sender).send(eq(expected));
	}

	@Test
	public void test_onApplicationEvent_disbaled() {
		when(properties.isEnabled()).thenReturn(false);
		notifier.onApplicationEvent(new ClientApplicationStatusChangedEvent(new Object(),
				Application.create("App").withId("-id-").withHealthUrl("http://health").build(), StatusInfo.ofDown(),
				StatusInfo.ofUp()));

		verify(sender, never()).send(isA(SimpleMailMessage.class));
	}

	@Test
	public void test_onApplicationEvent_noSend() {
		notifier.onApplicationEvent(new ClientApplicationStatusChangedEvent(new Object(),
				Application.create("App").withId("-id-").withHealthUrl("http://health").build(), StatusInfo.ofUnknown(),
				StatusInfo.ofUp()));

		verify(sender, never()).send(isA(SimpleMailMessage.class));
	}

	@Test
	public void test_onApplicationEvent_noSend_wildcard() {
		when(properties.getIgnoreChanges()).thenReturn(new String[] { "*:UP" });

		notifier.onApplicationEvent(new ClientApplicationStatusChangedEvent(new Object(),
				Application.create("App").withId("-id-").withHealthUrl("http://health").build(), StatusInfo.ofOffline(),
				StatusInfo.ofUp()));

		verify(sender, never()).send(isA(SimpleMailMessage.class));
	}
}
