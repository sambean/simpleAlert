/**
 * User: Sam.Yu
 * Date: 14-2-8
 * Time: 上午11:42
 */
public class CallRecord {

    private String key;

    private long callTimeMillis ;

    private long timeOutSeconds;

    private long intervalSeconds ;

    private String emailAddress;

    public long getCallTimeMillis() {
        return callTimeMillis;
    }

    public void setCallTimeMillis(long callTimeMillis) {
        this.callTimeMillis = callTimeMillis;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTimeOutSeconds() {
        return timeOutSeconds;
    }

    public void setTimeOutSeconds(long timeOutSeconds) {
        this.timeOutSeconds = timeOutSeconds;
    }

    public long getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(long intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
