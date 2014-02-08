package email;

/**
 * User: Sam.Yu
 * Date: 14-2-8
 * Time: 上午11:38
 */
public class Email {
    private String mailAddress;
    private String title;
    private String content;

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
