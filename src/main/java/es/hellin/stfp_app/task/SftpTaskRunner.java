package es.hellin.stfp_app.task;

import es.hellin.stfp_app.service.SftpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SftpTaskRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SftpTaskRunner.class);

    private final SftpService sftpService;

    public SftpTaskRunner(SftpService sftpService) {
        this.sftpService = sftpService;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== INICIANDO SUBIDA SFTP ===");
        try {
            sftpService.uploadFile("src/main/resources/archivos/prueba.txt");
            logger.info("=== COMPLETADO ===");
        } catch (Exception e) {
            logger.error("=== ERROR ===", e);
            System.exit(1);
        }
    }
}