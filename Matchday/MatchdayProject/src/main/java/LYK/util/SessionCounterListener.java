package LYK.util;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class SessionCounterListener implements HttpSessionListener {
	private static final AtomicInteger activeSessions = new AtomicInteger(0);


    public void sessionCreated(HttpSessionEvent se)  {
    	activeSessions.incrementAndGet();
    }


    public void sessionDestroyed(HttpSessionEvent se)  { 
        // 0 미만으로 떨어지지 않도록 max() 사용
        activeSessions.updateAndGet(current -> Math.max(0, current - 1));
    }
    
    public static int getActiveSessions() {
    	return activeSessions.get();
    }
	
}
