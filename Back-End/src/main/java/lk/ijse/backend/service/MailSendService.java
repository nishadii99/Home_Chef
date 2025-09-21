package lk.ijse.backend.service;


public interface MailSendService {

    void sendRegisteredEmail(String name, String email, String subject);
    void sendLoggedInEmail(String userName, String toEmail, String subject);


}
