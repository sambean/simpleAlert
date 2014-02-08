import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Sam.Yu
 * Date: 14-2-8
 * Time: 上午11:40
 */
public class Server extends HttpServlet {

    public final static Map<String,Stack<CallRecord>> recordMap = new ConcurrentHashMap<String,Stack<CallRecord>>(100);

   public final static List<ClientConfig> clientConfigs = new ArrayList<ClientConfig>(100);

    public final static List<String> keys = new ArrayList<String>(100);



    static {
        new Thread(new NotifyThread()).start();
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String isShowStatus = request.getParameter("showStatus");
        if ( isShowStatus != null ){
            showMapStatus(response);
            return ;
        }
        String key = request.getParameter("key");
        if  ( key == null ){
            response.getWriter().write("key cann't be null");
            return ;
        }
        String emailAddress = request.getParameter("emailAddress");
        if  ( emailAddress == null ){
            response.getWriter().write("emailAddress cann't be null");
            return ;
        }
        if ( !keys.contains(key)){
            registerKey(key,emailAddress);
        }
        String intervalSecondsStr = request.getParameter("intervalSeconds");
        String timeOutSecondsStr = request.getParameter("timeOutSeconds");
        long   intervalSeconds ;
        long   executionSeconds;
        long   callTimeMills  = System.currentTimeMillis();
        try{
            intervalSeconds    = Long.parseLong(intervalSecondsStr);
        }   catch (Exception e){
            response.getWriter().write("intervalSeconds is not   number");
            return ;
        }
        try{
            if ( timeOutSecondsStr != null ){
                executionSeconds =   Long.parseLong(timeOutSecondsStr);
            }  else{
                executionSeconds  =  (long) (intervalSeconds * 0.2) ;
            }
        }   catch (Exception e){
            response.getWriter().write("executionSeconds is not number");
            return ;
        }
        CallRecord callRecord = new CallRecord();
        callRecord.setKey(key);
        callRecord.setCallTimeMillis(callTimeMills);
        callRecord.setTimeOutSeconds(executionSeconds);
        callRecord.setIntervalSeconds(intervalSeconds);
        callRecord.setEmailAddress(emailAddress);
        saveRecord(callRecord);
        response.getWriter().write("ok");
    }

    private void registerKey(String key, String emailAddress) {
        keys.add(key);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setKey(key);
        clientConfig.setEmailAddress(emailAddress);
        clientConfigs.add(clientConfig);
    }

    private void showMapStatus(HttpServletResponse response) throws IOException {
         response.setContentType("text/html");
         Set<Map.Entry<String,Stack<CallRecord>>> set = recordMap.entrySet();
         Iterator<Map.Entry<String,Stack<CallRecord>>> it = set.iterator();
         while ( it.hasNext() ){
             Map.Entry<String,Stack<CallRecord>> entry =  it.next();
             response.getWriter().write("====== key  ===== "+ entry.getKey() +" <br/>");
             response.getWriter().write("====== callRecord  =====   <br/>");
             Stack<CallRecord> stack = entry.getValue();
             CallRecord[] callRecords = new CallRecord[stack.size()];
             stack.copyInto(callRecords);
             for ( CallRecord callRecord : callRecords){
                 Date t = new Date( callRecord.getCallTimeMillis());
                 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                 response.getWriter().write( "callTime " + sdf.format(t) +" <br/>");
             }
         }

    }


    private void saveRecord(CallRecord callRecord) {
        String key = callRecord.getKey();
        Stack records = recordMap.get(key);
        if ( records == null ){
            records = new Stack();
            records.push(callRecord);
            recordMap.put(key,records);
        }else{
            if ( records.size() > 10 ){
                records.removeAllElements();
                records.push(callRecord);
            }else{
                records.push(callRecord);
            }

        }
    }



}
