package es.hellin.stfp_app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sftp")
@Data
public class SftpConfig {
    private String host;
    private int port;
    private String user;
    private String password;
    private String remoteDirectory;
    private String privateKeyPath;
    private String privateKeyPass;
}