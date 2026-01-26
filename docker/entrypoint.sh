#!/bin/sh

# Create necessary directories
mkdir -p /tmp
mkdir -p /var/log/nginx
mkdir -p /var/run/nginx
mkdir -p /var/cache/nginx

# Set proper permissions
chown -R www-data:www-data /var/log/nginx
chown -R www-data:www-data /var/run/nginx
chown -R www-data:www-data /var/cache/nginx

# Test nginx configuration
echo "Testing nginx configuration..."
nginx -t

if [ $? -ne 0 ]; then
    echo "ERROR: Nginx configuration test failed!"
    exit 1
fi

echo "Nginx configuration is valid!"

# Execute the main command (usually nginx -g 'daemon off;')
exec "$@"
