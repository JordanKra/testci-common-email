package org.apache.commons.mail;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.After;

public class EmailTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	private static final String[] TEST_EMAILS = {"ab@bc.com", "a.b@c.org", "abcdefghijklmnop@abcdefghijklmnop.com.bd"};
	
	private EmailConcrete email;
	
	@Before
	public void setUpTestEmail() throws Exception {
		email = new EmailConcrete();
	}
	
	/*
	 * AddBcc Tests
	 * 
	 * */
	
	@Test
	public void testAddBcc() throws Exception {
		//Test that list of bcc's is the same as the number of emails we passed in
		email.addBcc(TEST_EMAILS);
		
		assertEquals(3, email.getBccAddresses().size());
	}

	@Test(expected = EmailException.class)
	public void testAddBccException() throws Exception {
		//Test that addBcc throws when null emails passed in
		String[] emails = null;
		email.addBcc(emails);
	}
	
	/*
	 * AddCc Tests
	 * 
	 * */
	
	@Test
	public void testAddCc() throws Exception {
		//Test that adding an email results in correct list size and correct entry
		email.addCc("abc@def.com");
		assertEquals(1, email.getCcAddresses().size());
		assertEquals("abc@def.com", email.getCcAddresses().get(0).toString());
	}
	
	@Test
	public void testAddMultipleCc() throws Exception {
		//Test that adding multiple cc's results in a list size thats the same as number we passed in
		email.addCc(TEST_EMAILS);
		
		assertEquals(3, email.getCcAddresses().size());
	}
	
	/*
	 * AddHeader Tests
	 * 
	 * */
	
	@Test
	public void testAddHeader() throws Exception {
		//Test that add header is the same as what we speicfy
		String name = "John";
		String value = "Doe";
		email.addHeader(name, value);
		
		assertEquals("Doe", email.headers.get(name).toString());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testHeaderNameException() {
		//Test that addHeader throws when name is null
		String value = "Doe";
		email.addHeader(null, value);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testHeaderValueException() {
		//Test that addHeader throws when email passed in is null
		String name = "John";
		email.addHeader(name, null);
	}
	
	/*
	 * AddReplyTo Tests
	 * 
	 * */
	
	@Test
	public void testAddReply() throws Exception{
		//Test that number of email addresses in reply list matches amount given when email and name given
		String valid_email = "abc@def.org";
		String name = "John";
		email.addReplyTo(valid_email, name);
		assertEquals(1, email.getReplyToAddresses().size());
	}
	
	@Test
	public void testAddOnlyEmailReply() throws Exception{
		//Test that number of email addresses in reply list matches amount given when only email given
		String valid_email = "abc@def.org";
		email.addReplyTo(valid_email);
		assertEquals(1, email.getReplyToAddresses().size());
	}
	
	/*
	 * GetHostName Tests
	 * 
	 * */
	
	@Test
	public void testGetHostNameNull() throws Exception{
		//Assert that getHostName returns null when no hostname specified
		assertEquals(null, email.getHostName());
	}
	
	@Test
	public void testGetHostName() throws Exception{
		//Test that getHostName returns same host name
		email.hostName = "localhost";
		assertEquals("localhost", email.getHostName().toString());
	}
	
	@Test
	public void testGetHostNameWithSession() throws Exception{
		//Test that setMailSession returns same hostname
		Properties prop = new Properties();
		prop.setProperty(email.MAIL_HOST, "localhost");
		Session scn = Session.getInstance(prop);
		email.setMailSession(scn);
		assertEquals("localhost", email.getHostName().toString());
	}
	
	/*
	 * BuildMimeMessage Tests
	 * 
	 * */
	
	@Test
	public void testBuildMimeMessageWithContent() throws Exception {
		//Test that content matches our specified content
		Properties prop = new Properties();
		prop.setProperty(email.MAIL_HOST, "localhost");
		Session scn = Session.getInstance(prop);
		
		email.setContent(scn, "Test");
		email.setMailSession(scn);
		email.setFrom("abc@def.com");
		email.addTo("def@ghi.com");
		email.addBcc("def@gh.edu");
		email.addReplyTo("xyz@g.com");
		email.addHeader("test", "Header");
		
		MimeMessage msg = new MimeMessage(scn);
		msg.addHeader("test", "Header");
		msg.setFrom();
		msg.setContent(scn, "Test");
		msg.setFrom();
		
		email.buildMimeMessage();
		assertEquals(email.getMimeMessage().getContent(), msg.getContent());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testBuildExistingMimeMessage() throws Exception{
		//Test that buildMimeMessage throws when message is already created
		Properties prop = new Properties();
		prop.setProperty(email.MAIL_HOST, "localhost");
		Session scn = Session.getInstance(prop);
		MimeMessage msg = new MimeMessage(scn);
		email.message = msg;
		email.buildMimeMessage();
	}
	
	@Test
	public void testBuildMimeMessageWithExistingSubjectAndCharset() throws Exception{
		//Test that the message created has the same subject as our specified one
		String valid_email = "abc@def.com";
		Properties prop = new Properties();
		prop.setProperty(email.MAIL_HOST, "localhost");
		prop.setProperty(email.MAIL_SMTP_FROM, "localhost");
		Session scn = Session.getInstance(prop);
		
		MimeMessage msg = new MimeMessage(scn);
		msg.setSubject("Test");
		email.setMailSession(scn);
		email.addCc(valid_email);
		email.setSubject("Test");
		email.setCharset("ASCII");
		email.buildMimeMessage();
		
		assertEquals(email.getMimeMessage().getSubject().toString(), msg.getSubject());
	}
	
	
	/*
	 * SetFrom Tests
	 * 
	 * */
	
	
	@Test
	public void testSetFrom() throws Exception{
		//Test that sender email matches
		String valid_email = "abc@def.com";
		email.setFrom(valid_email);
		assertEquals(valid_email, email.getFromAddress().toString());
	}
	
	/*
	 * GetMailSession Tests
	 * 
	 * */
	
	@Test
	public void testGetMailSession() throws Exception{
		//Ensure that when getMailSession is called with an existing session, that that is returned
		Properties prop = new Properties();
		prop.setProperty(email.MAIL_HOST, "localhost");
		Session scn = Session.getInstance(prop);
		email.setMailSession(scn);
		assertEquals(scn, email.getMailSession());
	}
	
	@Test
	public void testCreateMailSession() throws Exception{
		//Test that the mail session created matches our specified session
		Properties prop = new Properties();
		prop.setProperty(email.MAIL_HOST, "localhost");
        prop.setProperty(email.MAIL_TRANSPORT_PROTOCOL, email.SMTP);
		prop.setProperty(email.MAIL_PORT, "22");
		prop.setProperty(email.MAIL_SMTP_SOCKET_FACTORY_PORT, "22");
		prop.setProperty(email.MAIL_SMTP_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
		prop.setProperty(email.MAIL_SMTP_SOCKET_FACTORY_FALLBACK, "false");
		prop.setProperty(EmailConstants.MAIL_SMTP_SSL_CHECKSERVERIDENTITY, "true");
		prop.setProperty(email.MAIL_SMTP_FROM, "abc@def.com");
		prop.setProperty(email.MAIL_SMTP_TIMEOUT, Integer.toString(10));
		prop.setProperty(email.MAIL_SMTP_CONNECTIONTIMEOUT, Integer.toString(10));
		prop.setProperty(email.MAIL_SMTP_AUTH, "true");
        prop.setProperty(email.MAIL_DEBUG, "true");
        prop.setProperty(EmailConstants.MAIL_TRANSPORT_STARTTLS_ENABLE, "true");
        prop.setProperty(EmailConstants.MAIL_SMTP_SEND_PARTIAL, "true");
        prop.setProperty(EmailConstants.MAIL_SMTPS_SEND_PARTIAL, "true");
        Authenticator auth = new DefaultAuthenticator("test", "password");
        
		email.setSSLOnConnect(true);
		email.setHostName("localhost");
		email.setAuthentication("test", "password");
		email.setSocketTimeout(10);
		email.setSocketConnectionTimeout(10);
		email.setSSLCheckServerIdentity(true);
		email.setBounceAddress("abc@def.com");
		email.setSslSmtpPort("22");
		email.setDebug(true);
		email.setStartTLSEnabled(true);
		email.setStartTLSRequired(true);
		email.setSendPartial(true);
		
		Session scn = Session.getInstance(prop, auth);
		Session email_scn = email.getMailSession();
	
		assertEquals(scn.getProperty(email.MAIL_HOST), email_scn.getProperty(email.MAIL_HOST));
		assertEquals(scn.getProperty(email.MAIL_TRANSPORT_PROTOCOL), email_scn.getProperty(email.MAIL_TRANSPORT_PROTOCOL));
		assertEquals(scn.getProperty(email.MAIL_PORT), email_scn.getProperty(email.MAIL_PORT));
		assertEquals(scn.getProperty(email.MAIL_SMTP_SOCKET_FACTORY_PORT), email_scn.getProperty(email.MAIL_SMTP_SOCKET_FACTORY_PORT));
		assertEquals(scn.getProperty(email.MAIL_SMTP_SOCKET_FACTORY_CLASS), email_scn.getProperty(email.MAIL_SMTP_SOCKET_FACTORY_CLASS));
		assertEquals(scn.getProperty(email.MAIL_SMTP_SOCKET_FACTORY_FALLBACK), email_scn.getProperty(email.MAIL_SMTP_SOCKET_FACTORY_FALLBACK));
		assertEquals(scn.getProperty(EmailConstants.MAIL_SMTP_SSL_CHECKSERVERIDENTITY), email_scn.getProperty(EmailConstants.MAIL_SMTP_SSL_CHECKSERVERIDENTITY));
		assertEquals(scn.getProperty(email.MAIL_SMTP_FROM), email_scn.getProperty(email.MAIL_SMTP_FROM));
		assertEquals(scn.getProperty(email.MAIL_SMTP_TIMEOUT), email_scn.getProperty(email.MAIL_SMTP_TIMEOUT));
		assertEquals(scn.getProperty(email.MAIL_SMTP_CONNECTIONTIMEOUT), email_scn.getProperty(email.MAIL_SMTP_CONNECTIONTIMEOUT));
		assertEquals(scn.getProperty(email.MAIL_SMTP_AUTH), email_scn.getProperty(email.MAIL_SMTP_AUTH));
		assertEquals(scn.getProperty(email.MAIL_DEBUG), email_scn.getProperty(email.MAIL_DEBUG));
		assertEquals(scn.getProperty(email.MAIL_DEBUG), email_scn.getProperty(email.MAIL_DEBUG));

		assertEquals(scn.getProperty(EmailConstants.MAIL_TRANSPORT_STARTTLS_ENABLE), email_scn.getProperty(EmailConstants.MAIL_TRANSPORT_STARTTLS_ENABLE));

		assertEquals(scn.getProperty(EmailConstants.MAIL_SMTP_SEND_PARTIAL), email_scn.getProperty(EmailConstants.MAIL_SMTP_SEND_PARTIAL));

		assertEquals(scn.getProperty(EmailConstants.MAIL_SMTPS_SEND_PARTIAL), email_scn.getProperty(EmailConstants.MAIL_SMTPS_SEND_PARTIAL));
	}
	
	@Test(expected = EmailException.class)
	public void testCreateMailSessionException() throws Exception{
		//Test that if hostname isnt set, that an EmailException is thrown
		email.getMailSession();
		
	}
	
	/*
	 * GetDate Test
	 * 
	 * */
	
	@Test
	public void testGetDate() throws Exception{
		//Test that date returned is the same as specified
		Date d = new Date();
		email.sentDate = d;
		assertEquals(d, email.getSentDate());
	}
	
	/*
	 * GetSocketConnectionTimeout Test
	 * 
	 * */
	
	@Test
	public void testGetSocketConnectionTimeout() throws Exception{
		//Test that timeout returned is the same as specified
		int timeout = 10;
		email.setSocketConnectionTimeout(timeout);
		assertEquals(timeout, email.getSocketConnectionTimeout());
	}
	
}
