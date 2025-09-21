package lk.ijse.backend.service.imple;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MailSendServiceImpl {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    private String loginAlert(String userName){
        return String.format("""
        <html>
          <body style="font-family: sans-serif;">
            <h2>Login Alert!</h2>
            <p><strong>%s</strong>, you just logged into your account.</p>
            <hr>
            <p style="font-size: 12px; color: gray;">This is an automated email. Please don't reply.</p>
          </body>
        </html>
        """, userName);
    }

    private String registeredAlert(String userName){
        String dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return String.format("""
                <html>
                  <body style="font-family: Arial, sans-serif; margin:0; padding:0; background-color:#f5f5f5;">
                    <div style="max-width:600px; margin:20px auto; background-color:#ffffff; border-radius:10px; overflow:hidden; box-shadow:0 4px 8px rgba(0,0,0,0.1);">
                
                      <!-- Header -->
                      <div style="background: linear-gradient(90deg, #43cea2, #185a9d); padding: 20px; text-align:center;">
                        <h1 style="color:white; font-size:28px; margin:0;">Welcome to Home Chef!</h1>
                      </div>
                
                      <!-- Body -->
                      <div style="padding:20px; color:#333;">
                        <p style="font-size:16px;">Hi <strong style="color:#43cea2;">%s</strong>,</p>
                        <p style="font-size:16px;">Thank you for registering with us. Your account has been successfully created.</p>
                        <p style="font-size:16px;"><strong style="color:#43cea2;">Registration Date & Time:</strong> %s</p>
                        <hr style="border:none; border-top:1px solid #eee; margin:20px 0;">
                        <p style="font-size:12px; color:gray;">This is an automated message. Please do not reply to this email.</p>
                      </div>
                
                    </div>
                  </body>
                </html>
                
        """, userName, dateTime);
    }

    public void sendLoggedInEmail(String userName, String toEmail, String subject){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(loginAlert(userName), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRegisteredEmail(String name,String email,String subject){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(registeredAlert(name), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
