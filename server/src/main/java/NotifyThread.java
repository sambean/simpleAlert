import email.Email;
import email.EmailService;
import email.EmailServiceImpl;

import java.util.*;

/**
 * User: Sam.Yu
 * Date: 14-2-8
 * Time: 下午12:26
 */
public class NotifyThread  implements  Runnable{

    private EmailService emailService = new EmailServiceImpl();

    private Map<String,Integer> timeoutCountMap = new HashMap<String, Integer>(100);

    private Map<String,Boolean> sendMailFlagMap = new HashMap<String,Boolean>(100);

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Set<Map.Entry<String,Stack<CallRecord>>> set = Server.recordMap.entrySet();
            Iterator<Map.Entry<String,Stack<CallRecord>>> it = set.iterator();
            while ( it.hasNext() ){
                Map.Entry<String,Stack<CallRecord>> entry = it.next();
                Stack<CallRecord> callRecordStack = entry.getValue();
                try {
                    if ( !callRecordStack.isEmpty() ){
                        CallRecord callRecord = callRecordStack.peek();
                        checkCallRecord( callRecord );
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkCallRecord(CallRecord callRecord) {
        long timeOutSeconds =  callRecord.getTimeOutSeconds();
        long intervalSeconds = callRecord.getIntervalSeconds();
        long callTimeMills = callRecord.getCallTimeMillis();
        String emailAddress = callRecord.getEmailAddress();
        String key = callRecord.getKey();
        if ( isTimeOut( timeOutSeconds, intervalSeconds,callTimeMills) ){
            increaseTimeOutCount(key);
            if (isNeedSendEmail(key)){
                Boolean flag = sendMailFlagMap.get(key);
                if ( flag == null ){
                    sendEmailAndSetFlag(key, emailAddress);
                }else{
                    if (isResendEmail(intervalSeconds, callTimeMills)){
                        sendEmailAndSetFlag(key, emailAddress);
                    }
                }
                timeoutCountMap.put(key,null);
            }
        }  else{
            resetTimeOutCountAndSendEmailFlag(key);
        }
    }

    private boolean isNeedSendEmail(String key) {
        return timeoutCountMap.get(key) > 10;
    }

    private boolean isResendEmail(long intervalSeconds, long callTimeMills) {
        return System.currentTimeMillis() > callTimeMills + intervalSeconds * 1000;
    }

    private void resetTimeOutCountAndSendEmailFlag(String key) {
        timeoutCountMap.put(key,null);
        sendMailFlagMap.put(key,null);
    }

    private void sendEmailAndSetFlag(String key, String emailAddress) {
        Email email = new Email();
        email.setMailAddress(emailAddress);
        email.setTitle("服务超时警告,key="+key);
        email.setContent("服务超时警告");
        emailService.sendEmail(email);
        sendMailFlagMap.put(key,true);
    }

    private boolean isTimeOut( long timeOutSeconds, long intervalSeconds,long callTimeMills) {
        long currentTimeMills = System.currentTimeMillis();
        return (callTimeMills + timeOutSeconds * 1000 +  intervalSeconds * 1000 ) < currentTimeMills;
    }

    private void increaseTimeOutCount(String key) {
        Integer timeoutCount = timeoutCountMap.get(key);
        if (  timeoutCount == null){
            timeoutCount = 1;
        }else{
            timeoutCount ++;
        }

         timeoutCountMap.put(key, timeoutCount);
    }
}
