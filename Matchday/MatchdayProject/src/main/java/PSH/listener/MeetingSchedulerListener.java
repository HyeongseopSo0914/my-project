package PSH.listener;

import PSH.service.MeetingScheduler;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

@WebListener
public class MeetingSchedulerListener implements ServletContextListener {
    private MeetingScheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = new MeetingScheduler();
        scheduler.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}