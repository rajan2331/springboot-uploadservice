# springboot-uploadservice
This project delves into handling the upload of image for a popular app
# Start MySQL
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=imagedb mysql:8

# Start Kafka
docker run -d -p 9092:9092 apache/kafka:latest

# Update application.yml with your AWS credentials

# Run the application
mvn spring-boot:run
