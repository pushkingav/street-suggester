1. Build all jars locally
2. scp jars to EC2 instance (change instance ip accordingly)
   - sudo scp -i "/mnt/c/Distrib/ap-ec2-es.pem" "/mnt/c/ec2/search-0.2.zip" ec2-user@ec2-3-239-150-206.compute-1.amazonaws.com:/home/ec2-user/compose/search
3. scp frontend to EC2 instance
   - sudo scp -i "/mnt/c/Distrib/ap-ec2-es.pem" "/mnt/c/ec2/App.js" ec2-user@ec2-3-239-150-206.compute-1.amazonaws.com:/home/ec2-user/compose/frontend/src
4. Put startup.sh /var/lib/cloud/scripts/per-boot/ folder of the EC2 instance
5. sudo chmod +x /var/lib/cloud/scripts/per-boot/startup.sh (first deploy only)
6. Stop current version: docker-compose --profile main stop
7. Delete old versions of containers and images, except elastic (docker rm, docker rmi)
8. Build new versions of containers: docker-compose --profile main up
9. Make sure all containers started.
10. Gracefully stop docker compose (Ctrl+C)
11. Reboot EC2 Instance. Startup script will make necessary changes to public ip