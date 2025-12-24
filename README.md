# springboot-uploadservice
This project delves into handling the upload of image for a popular app
# Start MySQL
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=imagedb mysql:8

# Start Kafka
docker run -d -p 9092:9092 apache/kafka:latest

# Update application.yml with your AWS credentials

# Run the application
mvn spring-boot:run

Architecture Flow
1. Client Requests Upload (Presigned URL)
POST /api/images/upload/initiate
→ Generates presigned S3 URL
→ Saves metadata to MySQL (status: PENDING)
→ Returns presigned URL to client
2. Client Uploads Directly to S3
Client uses presigned URL → Uploads to S3 directly
(No traffic through your backend!)
3. Client Confirms Upload
POST /api/images/upload/confirm/{imageId}
→ Sends event to Kafka
→ Returns 202 Accepted immediately
4. Async Processing via Kafka Consumer
Kafka Consumer receives event
→ Updates status to PROCESSING
→ Generates thumbnails
→ Applies filters/compression
→ Updates status to COMPLETED
