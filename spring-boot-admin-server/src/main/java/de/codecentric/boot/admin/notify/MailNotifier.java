/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.boot.admin.notify;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import de.codecentric.boot.admin.event.ClientApplicationStatusChangedEvent;

public class MailNotifier implements ApplicationListener<ClientApplicationStatusChangedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailNotifier.class);

	@Autowired
	private MailNotifierProperties mailNotifierProperties;

	@Autowired
	private MailSender sender;

	@Override
	public void onApplicationEvent(ClientApplicationStatusChangedEvent event) {
		if (shouldSendMail(event.getFrom().getStatus(), event.getTo().getStatus())) {
			try {
				SimpleMailMessage message = buildMessage(event);
				sender.send(message);
			} catch (Exception ex) {
				LOGGER.error("Couldn't send mail for Statuschange {} ", event, ex);
			}
		}
	}

	private SimpleMailMessage buildMessage(ClientApplicationStatusChangedEvent event) {
		EvaluationContext context = new StandardEvaluationContext(event);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(mailNotifierProperties.getTo());
		message.setFrom(mailNotifierProperties.getFrom());
		message.setSubject(mailNotifierProperties.getSubject().getValue(context, String.class));
		message.setText(mailNotifierProperties.getText().getValue(context, String.class));
		message.setCc(mailNotifierProperties.getCc());

		return message;
	}

	private boolean shouldSendMail(String fromStatus, String toStatus) {
		String[] ignoreChanges = mailNotifierProperties.getIgnoreChanges();
		return Arrays.binarySearch(ignoreChanges, (fromStatus + ":" + toStatus)) < 0
				&& Arrays.binarySearch(ignoreChanges, ("*:" + toStatus)) < 0
				&& Arrays.binarySearch(ignoreChanges, (fromStatus + ":*")) < 0;
	}

}
