import com.qoppa.pdfWriter.PDFPrinterJob;

import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.Properties;

class Email {

    private Properties emailProperties;
    private Session mailSession;
    private MimeMessage emailMessage;
    private Multipart multipart;
    private MimeBodyPart messageBodyPart;
    private MimeBodyPart attachPart1;
    private String recipientEmail;
    private String emailHost = "smtp.gmail.com";
    private String fromUser = "todolistjava2";
    private String fromUserEmailPassword = "toDoListJava2";

    public Email(JFrame frame, String filename, String attachmentType) {
        recipientEmail = JOptionPane.showInputDialog("Enter the email address to send to.");
        setMailServerProperties();
        createEmailMessage(frame, filename, attachmentType);
        sendEmail();
    }

    public void setMailServerProperties() {
        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.starttls.enable", "true");
        emailProperties.put("mail.smtp.host", emailHost);
        emailProperties.put("mail.smtp.user", fromUser);
        emailProperties.put("mail.smtp.password", fromUserEmailPassword);
        emailProperties.put("mail.smtp.port", "587");
        emailProperties.put("mail.smtp.auth", "true");
    }

    public void createEmailMessage(JFrame frame, String filename, String attachmentType) {
        multipart = new MimeMultipart();
        messageBodyPart = new MimeBodyPart();
        attachPart1 = new MimeBodyPart();

        String emailSubject = "To Do List";

        mailSession = Session.getDefaultInstance(emailProperties);
        emailMessage = new MimeMessage(mailSession);

        try {
            emailMessage.setFrom(new InternetAddress("todolistjava2"));
            emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            emailMessage.setSubject(emailSubject);

            switch (attachmentType) {
                case "pdf":
                    pdf(frame, filename);
                    break;
                case "object":
                    object(filename);
                    break;
            }
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachPart1);
            emailMessage.setContent(multipart);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendEmail() {

        try {
            Transport transport = mailSession.getTransport("smtp");
            transport.connect(emailHost, fromUser, fromUserEmailPassword);
            transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void pdf(JFrame frame, String fileName) {
        try {
            messageBodyPart.setContent("Enjoy","text/html");
            PDFPrinterJob printer = (PDFPrinterJob)PDFPrinterJob.getPrinterJob ();
            printer.setPrintable (new PrintableToDoList(frame));
            printer.setCopies (1);
            printer.print("C:\\Users\\Eitan\\IdeaProjects\\to do list\\"+fileName+".pdf");
            attachPart1.attachFile(fileName+".pdf");
        } catch (MessagingException | IOException | PrinterException e) {
            e.printStackTrace();
        }
    }

    private void object(String fileName) {
        MimeBodyPart attachPart2 = new MimeBodyPart();
        String instructions = "You have been emailed a synchronized object. To use just save the files in your to do " +
                "list directory and add the name to your Names of lists file. Then start your program and open the new list.";
        try {
            messageBodyPart.setContent(instructions, "text/html");
            attachPart1.attachFile(fileName+".ListDisplay");
            attachPart2.attachFile(fileName+".ToDoList");
            multipart.addBodyPart(attachPart2);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
}