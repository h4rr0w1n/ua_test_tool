# Docker README for AMHS UA Test Tool

This document provides instructions for building and running the AMHS UA Test Tool as a Docker container.

## Prerequisites

- Docker (version 20.10 or higher recommended)
- Docker Compose (version 2.0 or higher recommended)
- Sufficient disk space (~500MB for the image)

## Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# Build and start the container
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the container
docker-compose down
```

### Option 2: Using Docker Directly

```bash
# Build the Docker image
docker build -t amhs-ua-test-tool .

# Run the container
docker run --rm amhs-ua-test-tool
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `ISODE_BINDIR` | Path to Isode native libraries | `/app/lib` |
| `JAVA_TOOL_OPTIONS` | JVM options | `-Disode.bindir=/app/lib -Djava.library.path=/app/lib` |
| `DISPLAY` | X11 display for GUI (Linux) | Not set |

### Volume Mounts

The following volumes are configured in `docker-compose.yml`:

- `./connection.properties:/app/connection.properties:ro` - Mount your connection configuration
- `./docker-output:/app/output` - Mount a directory for output files

Create the output directory if needed:
```bash
mkdir -p docker-output
```

## GUI Support

This application has a graphical user interface. To run it with GUI support:

### On Linux

```bash
# Allow Docker to access X server
xhost +local:docker

# Set DISPLAY environment variable
export DISPLAY=$DISPLAY

# Run with Docker Compose
docker-compose up -d

# Or run directly with Docker
docker run --rm \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  amhs-ua-test-tool
```

### On Windows

Install an X server like [VcXsrv](https://sourceforge.net/projects/vcxsrv/) or [Xming](https://sourceforge.net/projects/xming/).

```bash
# Run with Docker (replace HOST_IP with your host machine's IP)
docker run --rm \
  -e DISPLAY=host.docker.internal:0 \
  amhs-ua-test-tool
```

### On macOS

Install [XQuartz](https://www.xquartz.org/) and configure it to accept network connections.

```bash
# Run with Docker
docker run --rm \
  -e DISPLAY=host.docker.internal:0 \
  amhs-ua-test-tool
```

## Headless Mode

For automated testing or server environments without a display:

```bash
# Run in headless mode
docker run --rm \
  -Djava.awt.headless=true \
  amhs-ua-test-tool
```

Note: Some features may not work in headless mode if they require GUI components.

## Building from Source

The Dockerfile uses a multi-stage build:

1. **Builder Stage**: Uses Maven to compile and package the application
2. **Runtime Stage**: Uses a minimal JRE image to run the application

To rebuild after making code changes:

```bash
# Force rebuild without cache
docker build --no-cache -t amhs-ua-test-tool .

# Or with Docker Compose
docker-compose up -d --build
```

## Troubleshooting

### Container exits immediately

Check the logs:
```bash
docker-compose logs
```

Common issues:
- Missing native libraries in the `lib` directory
- Incorrect Java version requirements
- Missing configuration files

### GUI doesn't display

1. Ensure X server is running and accepting connections
2. Check DISPLAY environment variable is set correctly
3. Verify Docker has permission to access X server

### Native library errors

If you see errors about native libraries:
```
UnsatisfiedLinkError: no x400mt in java.library.path
```

Ensure the `lib` directory contains all required native libraries (.dll, .so, or .dylib files).

## Security Considerations

- The container runs as root by default. For production, consider creating a non-root user.
- Mount sensitive configuration files as read-only when possible.
- Don't expose unnecessary ports.

## Customization

### Adding System Dependencies

If your native libraries require additional system packages, modify the Dockerfile:

```dockerfile
RUN apt-get update && apt-get install -y \
    libfreetype6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    && rm -rf /var/lib/apt/lists/*
```

### Changing Java Version

To use a different Java version, update both stages in the Dockerfile:

```dockerfile
# Builder stage
FROM maven:3.8.7-eclipse-temurin-11 AS builder

# Runtime stage
FROM eclipse-temurin:11-jre
```

## Advanced Usage

### Interactive Shell

```bash
# Access container shell for debugging
docker run --rm -it --entrypoint /bin/bash amhs-ua-test-tool
```

### Port Forwarding

If your application needs network access:

```yaml
# In docker-compose.yml
ports:
  - "8080:8080"
```

### Resource Limits

```yaml
# In docker-compose.yml
deploy:
  resources:
    limits:
      cpus: '2'
      memory: 2G
    reservations:
      cpus: '1'
      memory: 1G
```

## Cleanup

```bash
# Remove stopped containers
docker-compose down

# Remove images
docker rmi amhs-ua-test-tool:latest

# Remove all Docker artifacts (use with caution)
docker system prune -a
```

## Support

For issues related to the AMHS UA Test Tool itself, refer to the main README.md.
For Docker-specific issues, check:
- Docker documentation: https://docs.docker.com/
- Docker Compose documentation: https://docs.docker.com/compose/
