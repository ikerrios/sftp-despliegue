package es.hellin.stfp_app.service;

import com.jcraft.jsch.*;
import es.hellin.stfp_app.config.SftpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;

@Service
public class SftpService {

    private static final Logger logger = LoggerFactory.getLogger(SftpService.class);
    private final SftpConfig config;

    public SftpService(SftpConfig config) {
        this.config = config;
    }

    public void uploadFile(String localFilePath) throws Exception {
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            // 1. Crear sesión SSH
            JSch jsch = new JSch();
            session = jsch.getSession(config.getUser(), config.getHost(), config.getPort());
            session.setPassword(config.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");

            logger.info("Conectando a {}@{}:{}", config.getUser(), config.getHost(), config.getPort());
            session.connect();
            logger.info("Conexión establecida");

            // 2. Abrir canal SFTP
            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;

            // 3. Cambiar a directorio remoto
            channelSftp.cd(config.getRemoteDirectory());

            // 4. Subir archivo
            File localFile = new File(localFilePath);
            logger.info("Subiendo: {} ({} bytes)", localFile.getName(), localFile.length());

            FileInputStream inputStream = new FileInputStream(localFile);
            channelSftp.put(inputStream, localFile.getName());
            inputStream.close();

            logger.info("Archivo subido: {}", localFile.getName());

        } finally {
            // 5. Cerrar conexiones
            if (channelSftp != null) channelSftp.disconnect();
            if (session != null) session.disconnect();
        }
    }
}