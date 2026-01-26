# Nginx Configuration for TreadX API

This directory contains the nginx configuration for proxying requests to the TreadX Spring Boot API.

## Configuration Overview

The nginx configuration is set up to:
- Listen on port 80
- Serve the subdomain: `api.treadx.nexussolutions.tech`
- Proxy all requests to the Spring Boot application running on port 9003 (exposed by Docker)
- Include security headers
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

## SSL/HTTPS Setup (Optional but Recommended)

To enable HTTPS, you'll need to:

1. Obtain an SSL certificate (using Let's Encrypt/Certbot):
   ```bash
   sudo certbot --nginx -d api.treadx.nexussolutions.tech
   ```

2. Update the nginx configuration to include SSL settings:
   - Add `listen 443 ssl;`
   - Add SSL certificate paths
   - Redirect HTTP to HTTPS

## Testing

After setup, test the API:

```bash
curl http://api.treadx.nexussolutions.tech/actuator/health
```

Or visit in your browser:
```
http://api.treadx.nexussolutions.tech
```

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
