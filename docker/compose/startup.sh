#!/bin/bash
export REACT_APP_PUBLIC_IP=$(TOKEN=`curl -X PUT "http://169.254.169.254/latest/api/token" -H "X-aws-ec2-metadata-token-ttl-seconds: 21600"` \
&& curl -H "X-aws-ec2-metadata-token: $TOKEN" -v http://169.254.169.254/latest/meta-data/public-ipv4)

echo "Public ip = ${REACT_APP_PUBLIC_IP}"

#put this script to /var/lib/cloud/scripts/per-boot/ folder of the EC2 instance