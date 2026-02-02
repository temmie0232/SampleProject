package com.example.simplelibrary.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Jwt jwt = new Jwt();
    private Upload upload = new Upload();
    private Cors cors = new Cors();
    private Bootstrap bootstrap = new Bootstrap();

    public Jwt getJwt() {
        return jwt;
    }

    public Upload getUpload() {
        return upload;
    }

    public Cors getCors() {
        return cors;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public static class Jwt {
        private String secret;
        private long expiresSeconds;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpiresSeconds() {
            return expiresSeconds;
        }

        public void setExpiresSeconds(long expiresSeconds) {
            this.expiresSeconds = expiresSeconds;
        }
    }

    public static class Upload {
        private String dir;
        private long maxSizeMb;

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public long getMaxSizeMb() {
            return maxSizeMb;
        }

        public void setMaxSizeMb(long maxSizeMb) {
            this.maxSizeMb = maxSizeMb;
        }
    }

    public static class Cors {
        private String[] allowedOrigins = new String[0];

        public String[] getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String[] allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }

    public static class Bootstrap {
        private boolean enabled = true;
        private String adminEmail;
        private String adminPassword;
        private String adminDisplayName;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getAdminEmail() {
            return adminEmail;
        }

        public void setAdminEmail(String adminEmail) {
            this.adminEmail = adminEmail;
        }

        public String getAdminPassword() {
            return adminPassword;
        }

        public void setAdminPassword(String adminPassword) {
            this.adminPassword = adminPassword;
        }

        public String getAdminDisplayName() {
            return adminDisplayName;
        }

        public void setAdminDisplayName(String adminDisplayName) {
            this.adminDisplayName = adminDisplayName;
        }
    }
}
