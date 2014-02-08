package email;

/**
 * User: Sam.Yu
 * Date: 14-2-8
 * Time: 上午11:39
 */
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendEmail(Email email) {
        System.out.println("send email : " + email.getMailAddress()+"  " + email.getTitle() +"  " + email.getContent()) ;

    }
}
