# Multi-stage Dockerfile for AMHS UA Test Tool
# This Dockerfile builds and runs the AMHS UA Test Tool in a container

# =============================================================================
# Stage 1: Build Stage
# =============================================================================
FROM maven:3.8.7-eclipse-temurin-8 AS builder

WORKDIR /build

# Copy pom.xml first for better caching
COPY pom.xml .

# Copy source code
COPY src ./src

# Copy local libraries that need to be installed
COPY lib ./lib

# Copy install script if it exists
COPY install-isode-libs.sh ./install-isode-libs.sh 2>/dev/null || true

# Build the application
RUN mvn clean package -DskipTests -B

# =============================================================================
# Stage 2: Runtime Stage
# =============================================================================
FROM eclipse-temurin:8-jre

LABEL maintainer="AMHS UA Test Tool"
LABEL description="Docker container for AMHS X.400 UA Test Tool"
LABEL version="1.0.0"

# Install any required system packages for native libraries
# Uncomment if your native libraries require specific packages
# RUN apt-get update && apt-get install -y \
#     libfreetype6 \
#     libxrender1 \
#     libxtst6 \
#     libxi6 \
#     && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Create non-root user for security (optional, comment out if native libs need root)
# RUN groupadd -r amhs && useradd -r -g amhs amhs

# Copy the built JAR from builder stage
COPY --from=builder /build/target/ua-test-tool-1.0.0-jar-with-dependencies.jar ./ua-test-tool.jar

# Copy the lib directory with native libraries from builder stage
COPY --from=builder /build/lib ./lib

# Copy connection properties if exists
COPY connection.properties ./connection.properties 2>/dev/null || true

# Set environment variables
ENV ISODE_BINDIR=/app/lib
ENV JAVA_TOOL_OPTIONS="-Disode.bindir=/app/lib -Djava.library.path=/app/lib"

# Set working directory
WORKDIR /app

# Change to non-root user (if created above)
# USER amhs

# Default command to run the application
# For GUI applications, you may need X11 forwarding or use headless mode
ENTRYPOINT ["java"]
CMD ["-Disode.bindir=/app/lib", "-Djava.library.path=/app/lib", "-jar", "ua-test-tool.jar"]

# =============================================================================
# Usage Instructions:
# =============================================================================
# 
# Build the Docker image:
#   docker build -t amhs-ua-test-tool .
#
# Run in headless mode (for CLI operations):
#   docker run --rm amhs-ua-test-tool
#
# Run with GUI support (requires X server):
#   On Linux:
#     xhost +local:docker
#     docker run --rm -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix amhs-ua-test-tool
#
#   On Windows with Xming/VcXsrv:
#     docker run --rm -e DISPLAY=host.docker.internal:0 amhs-ua-test-tool
#
# Run with mounted configuration:
#   docker run --rm -v $(pwd)/connection.properties:/app/connection.properties amhs-ua-test-tool
#
# Interactive shell for debugging:
#   docker run --rm -it --entrypoint /bin/bash amhs-ua-test-tool
#
# =============================================================================
