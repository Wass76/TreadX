# Nginx Configuration for TreadX API

This directory contains the nginx configuration for proxying requests to the TreadX Spring Boot API.

## Configuration Overview

The nginx configuration is set up to:
- Listen on port 80 (HTTP) and port 443 (HTTPS)
- Serve the subdomain: `api.treadx.nexussolutions.tech`
- Redirect HTTP to HTTPS
- Proxy all requests to the Spring Boot application running on port 9003 (exposed by Docker)
- Include security headers and SSL/TLS configuration
- Support large file uploads (up to 100MB)

## Files

- `api.treadx.nexussolutions.tech.conf` - Site-specific nginx configuration (recommended)
- `nginx.conf` - Full nginx configuration (alternative option)

## Setup Instructions

### Recommended: Using Site-Specific Configuration

1. Copy the site configuration file to nginx sites-available:
   ```bash
   sudo cp docker/api.treadx.nexussolutions.tech.conf /etc/nginx/sites-available/api.treadx.nexussolutions.tech.conf
   ```

2. Create a symbolic link to enable the site:
   ```bash
   sudo ln -s /etc/nginx/sites-available/api.treadx.nexussolutions.tech.conf /etc/nginx/sites-enabled/
   ```

3. Test the nginx configuration:
   ```bash
   sudo nginx -t
   ```

4. If the test is successful, reload nginx:
   ```bash
   sudo systemctl reload nginx
   ```

### Alternative: Using Full nginx.conf

If you prefer to use the full nginx.conf file (not recommended if you have other sites):

1. **Backup your existing nginx.conf:**
   ```bash
   sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup
   ```

2. Copy the nginx configuration:
   ```bash
   sudo cp docker/nginx.conf /etc/nginx/nginx.conf
   ```

3. Test the configuration:
   ```bash
   sudo nginx -t
   ```

4. Reload nginx:
   ```bash
   sudo systemctl reload nginx
   ```

### Important Notes

- The Spring Boot application must be running and accessible on `localhost:9003`
- Make sure your Docker container is exposing port 9003 (which it does in docker-compose.yml)
- The upstream points to `localhost:9003` because nginx on the host connects to the exposed Docker port

## DNS Configuration

Make sure your DNS is configured to point `api.treadx.nexussolutions.tech` to your VPS IP address:

```
A Record: api.treadx.nexussolutions.tech -> YOUR_VPS_IP
```

## SSL/HTTPS Setup (Required)

The nginx configuration already includes SSL support. You need to obtain an SSL certificate for the subdomain.

### Step 1: Install Certbot (if not already installed)

```bash
sudo apt update
sudo apt install certbot python3-certbot-nginx
```

### Step 2: Ensure nginx is running with HTTP configuration

Make sure your nginx configuration is deployed and nginx is running:

```bash
# Copy and enable the configuration (if not done already)
sudo cp docker/api.treadx.nexussolutions.tech.conf /etc/nginx/sites-available/api.treadx.nexussolutions.tech.conf
sudo ln -s /etc/nginx/sites-available/api.treadx.nexussolutions.tech.conf /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### Step 3: Obtain SSL Certificate

**Option A: Using Certbot with nginx plugin (Recommended - Auto-configures nginx):**

```bash
sudo certbot --nginx -d api.treadx.nexussolutions.tech
```

This will:
- Automatically obtain the certificate
- Update your nginx configuration with SSL settings
- Set up automatic renewal

**Option B: Using Certbot standalone (if nginx plugin doesn't work):**

```bash
# Stop nginx temporarily
sudo systemctl stop nginx

# Obtain certificate
sudo certbot certonly --standalone -d api.treadx.nexussolutions.tech

# Start nginx
sudo systemctl start nginx
```

After obtaining the certificate, update the nginx configuration file with the correct certificate paths (they should already be correct if you used Option A).

### Step 4: Verify SSL Configuration

After running certbot, test your configuration:

```bash
sudo nginx -t
sudo systemctl reload nginx
```

### Step 5: Test HTTPS

```bash
curl https://api.treadx.nexussolutions.tech/actuator/health
```

Or visit in your browser:
```
https://api.treadx.nexussolutions.tech
```

### Step 6: Set Up Auto-Renewal

Certbot certificates expire after 90 days. Set up auto-renewal:

```bash
# Test renewal
sudo certbot renew --dry-run

# Certbot should already have a systemd timer, but verify:
sudo systemctl status certbot.timer
```

### Troubleshooting SSL Issues

1. **Certificate doesn't match domain (ERR_CERT_COMMON_NAME_INVALID):**
   - Make sure you obtained the certificate for the exact subdomain: `api.treadx.nexussolutions.tech`
   - Check certificate details: `sudo certbot certificates`
   - Verify DNS is pointing correctly: `dig api.treadx.nexussolutions.tech`

2. **Certificate paths are incorrect:**
   - Check certificate location: `sudo ls -la /etc/letsencrypt/live/api.treadx.nexussolutions.tech/`
   - Update paths in nginx config if needed

3. **Port 443 not accessible:**
   - Check firewall: `sudo ufw status`
   - Allow HTTPS: `sudo ufw allow 443/tcp`

4. **Let's Encrypt rate limits:**
   - You can only request a limited number of certificates per week
   - Use `--dry-run` to test without counting against limits

## Testing

After setup, test the API:

**HTTP (should redirect to HTTPS):**
```bash
curl -I http://api.treadx.nexussolutions.tech/actuator/health
```

**HTTPS:**
```bash
curl https://api.treadx.nexussolutions.tech/actuator/health
```

Or visit in your browser:
```
https://api.treadx.nexussolutions.tech
```

Note: HTTP requests will automatically redirect to HTTPS.

## Troubleshooting

1. **Check nginx logs:**
   ```bash
   sudo tail -f /var/log/nginx/error.log
   sudo tail -f /var/log/nginx/access.log
   ```

2. **Test nginx configuration:**
   ```bash
   sudo nginx -t
   ```

3. **Check if Spring Boot app is accessible from host:**
   ```bash
   curl http://localhost:9003/actuator/health
   # Or test a specific endpoint
   curl http://localhost:9003/api/your-endpoint
   ```

4. **Verify Docker container is running and port is exposed:**
   ```bash
   docker ps
   # Should show treadx-backend container with port 9003:9003
   ```

5. **Check if port 9003 is listening:**
   ```bash
   sudo netstat -tlnp | grep 9003
   # Or
   sudo ss -tlnp | grep 9003
   ```

6. **Test the subdomain from your VPS:**
   ```bash
   curl -H "Host: api.treadx.nexussolutions.tech" http://localhost
   ```

7. **If you get 502 Bad Gateway:**
   - Verify the Spring Boot container is running: `docker ps`
   - Check if the app is listening: `curl http://localhost:9003/actuator/health`
   - Check nginx error logs: `sudo tail -f /var/log/nginx/error.log`
