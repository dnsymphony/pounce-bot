package com.symphony.bots.pounce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/**
 * Bot that allows one user ("pouncer") to be notified when another user ("pouncee") becomes available on
 * Symphony, based on presence.  The pouncer communicates with the bot in a Symphony chat (in an IM or in any
 * room in which the bot is present).  When the pouncee's presence changes to "Available", the bot will send a IM
 * to the pouncer letting them know.  The message sent may optionally contain a chime.
 *
 * "pounce on @Dan Nathanson"
 * "pounce on @Dan Nathanson with chime"  - this chimes the pouncer, not the pouncee.
 *
 * This bot relies on Spring Boot and the Symphony Java Client
 *
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dtruststore.file=
 * -Dtruststore.password=password
 * -Dsessionauth.url=https://(hostname)/sessionauth
 * -Dkeyauth.url=https://(hostname)/keyauth
 * -Duser.call.home=frank.tarsillo@markit.com
 * -Duser.cert.password=password
 * -Duser.cert.file=bot.user2.p12
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email=bot.user2@markit.com or bot user email
 */
@SpringBootApplication
public class PounceBotApplication {


	public static void main(String[] args) {
		SpringApplication.run(PounceBotApplication.class, args);
	}

}
