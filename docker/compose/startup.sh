#!/bin/bash
export PUBLIC_IP=$(TOKEN=`curl -X PUT "http://169.254.169.254/latest/api/token" -H "X-aws-ec2-metadata-token-ttl-seconds: 21600"` \
&& curl -H "X-aws-ec2-metadata-token: $TOKEN" -v http://169.254.169.254/latest/meta-data/public-ipv4)

echo "Public ip = ${PUBLIC_IP}"

# Set the path to the application.properties file
FILEPATH="/compose/application.properties"

# Find the line that starts with "public.ip.address=" and replace the IP address
sed -i "s/^public\.ip\.address=.*/public.ip.address=$PUBLIC_IP/g" $FILEPATH

echo "IP address updated successfully"

#also we need to follow this guide to put it properly into the frontend container:
#https://create-react-app.dev/docs/adding-custom-environment-variables/