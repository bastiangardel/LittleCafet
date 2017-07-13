package ch.bastiangardel.LittleCafet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by bastiangardel on 13.07.17.
 */
@Component
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private DataSource dataSource;

    private static final Logger log = LoggerFactory.
            getLogger(ApplicationStartup.class);

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        try {

            log.info("----- Test database connexion ------");
            dataSource.getConnection();
        } catch (SQLException e) {
            //e.printStackTrace();
            //log.error("!!! There is no connexion with the database !!!");
            log.error("\n" +
                    "\n" +
                    " __   __   __     .__   __.   ______        ______   ______   .__   __. .__   __.  __________   ___  __    ______   .__   __.                                     \n" +
                    "|  | |  | |  |    |  \\ |  |  /  __  \\      /      | /  __  \\  |  \\ |  | |  \\ |  | |   ____\\  \\ /  / |  |  /  __  \\  |  \\ |  |                                     \n" +
                    "|  | |  | |  |    |   \\|  | |  |  |  |    |  ,----'|  |  |  | |   \\|  | |   \\|  | |  |__   \\  V  /  |  | |  |  |  | |   \\|  |                                     \n" +
                    "|  | |  | |  |    |  . `  | |  |  |  |    |  |     |  |  |  | |  . `  | |  . `  | |   __|   >   <   |  | |  |  |  | |  . `  |                                     \n" +
                    "|__| |__| |__|    |  |\\   | |  `--'  |    |  `----.|  `--'  | |  |\\   | |  |\\   | |  |____ /  .  \\  |  | |  `--'  | |  |\\   |                                     \n" +
                    "(__) (__) (__)    |__| \\__|  \\______/      \\______| \\______/  |__| \\__| |__| \\__| |_______/__/ \\__\\ |__|  \\______/  |__| \\__|                                     \n" +
                    "                                                                                                                                                                  \n" +
                    "____    __    ____  __  .___________. __    __      _______       ___   .___________.    ___      .______        ___           _______. _______     __   __   __  \n" +
                    "\\   \\  /  \\  /   / |  | |           ||  |  |  |    |       \\     /   \\  |           |   /   \\     |   _  \\      /   \\         /       ||   ____|   |  | |  | |  | \n" +
                    " \\   \\/    \\/   /  |  | `---|  |----`|  |__|  |    |  .--.  |   /  ^  \\ `---|  |----`  /  ^  \\    |  |_)  |    /  ^  \\       |   (----`|  |__      |  | |  | |  | \n" +
                    "  \\            /   |  |     |  |     |   __   |    |  |  |  |  /  /_\\  \\    |  |      /  /_\\  \\   |   _  <    /  /_\\  \\       \\   \\    |   __|     |  | |  | |  | \n" +
                    "   \\    /\\    /    |  |     |  |     |  |  |  |    |  '--'  | /  _____  \\   |  |     /  _____  \\  |  |_)  |  /  _____  \\  .----)   |   |  |____    |__| |__| |__| \n" +
                    "    \\__/  \\__/     |__|     |__|     |__|  |__|    |_______/ /__/     \\__\\  |__|    /__/     \\__\\ |______/  /__/     \\__\\ |_______/    |_______|   (__) (__) (__) \n" +
                    "                                                                                                                                                                  \n" +
                    "\n");
            System.exit(1);
        }


        log.info("----- Database connexion UP and Running------");
    }

}
