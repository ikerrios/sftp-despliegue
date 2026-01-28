package es.hellin.stfp_app.service;

import com.jcraft.jsch.*;
import es.hellin.stfp_app.config.SftpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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

            jsch.addIdentity(config.getPrivateKeyPath(),config.getPrivateKeyPass());
            session = jsch.getSession(config.getUser(), config.getHost(), config.getPort());

            //session.setPassword(config.getPassword());

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


    // NUEVO: Método para subir archivos desde formulario web
    public void uploadFile(MultipartFile file) throws Exception {
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            logger.info("Cargando clave privada: {}", config.getPrivateKeyPath());
            jsch.addIdentity(config.getPrivateKeyPath(), config.getPrivateKeyPass());

            session = jsch.getSession(config.getUser(), config.getHost(), config.getPort());
            session.setConfig("StrictHostKeyChecking", "no");

            logger.info("Conectando con autenticación SSH...");
            session.connect();
            logger.info("Conexión SSH establecida");

            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            logger.info("Canal SFTP abierto");

            channelSftp.cd(config.getRemoteDirectory());
            logger.info("Directorio remoto: {}", config.getRemoteDirectory());

            logger.info("Subiendo: {} ({} bytes)", file.getOriginalFilename(), file.getSize());

            InputStream inputStream = file.getInputStream();
            channelSftp.put(inputStream, file.getOriginalFilename());
            inputStream.close();

            logger.info("Archivo subido exitosamente: {}", file.getOriginalFilename());

        } catch (JSchException e) {
            logger.error("Error SSH: {}", e.getMessage());
            throw new Exception("Error de autenticación SSH", e);
        } catch (SftpException e) {
            logger.error("Error SFTP: {}", e.getMessage());
            throw new Exception("Error al subir archivo", e);
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}