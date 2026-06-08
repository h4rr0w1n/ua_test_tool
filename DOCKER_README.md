# Docker README for AMHS UA Test Tool

This document provides instructions for building and running the AMHS UA Test Tool in Docker.

## Prerequisites

- Docker (version 20.10 or higher recommended)
- Sufficient disk space (~500MB for the image)
- Optional: X server for GUI display on the host

> Note: This repository does not include a `docker-compose.yml` file. Use `docker build` and `docker run` directly.

## Quick Start

### Build the Docker image

```bash
docker build -t amhs-ua-test-tool .
```

### Run the container

```bash
docker run --rm amhs-ua-test-tool
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `ISODE_BINDIR` | Path to Isode native libraries | `/app/lib` |
| `JAVA_TOOL_OPTIONS` | JVM options | `-Disode.bindir=/app/lib -Djava.library.path=/app/lib` |
| `DISPLAY` | X11 display for GUI | Not set |

### Volume Mounts

Mount the configuration file and any output directory as needed:

```bash
docker run --rm -v ./connection.properties:/app/connection.properties:ro amhs-ua-test-tool
```

If you want persistent output storage:

```bash
docker run --rm -v ./docker-output:/app/output amhs-ua-test-tool
```

## GUI Support

This application uses a Swing GUI. To display the window on your host, connect the container to an X server.

### On Linux

```bash
xhost +local:docker

docker run --rm -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix amhs-ua-test-tool
```

### On Windows

Install an X server such as [VcXsrv](https://sourceforge.net/projects/vcxsrv/) or [Xming](https://sourceforge.net/projects/xming/).

```bash
docker run --rm -e DISPLAY=host.docker.internal:0 amhs-ua-test-tool
```

If `host.docker.internal` does not work, use your host's local IP address instead.

### On macOS

Install [XQuartz](https://www.xquartz.org/) and enable network client connections.

```bash
docker run --rm -e DISPLAY=host.docker.internal:0 amhs-ua-test-tool
```

## Headless Mode

Headless mode is available, but GUI functionality may be limited:

```bash
docker run --rm -e JAVA_TOOL_OPTIONS='-Djava.awt.headless=true' amhs-ua-test-tool
```

## Building from Source

The Dockerfile uses a multi-stage build:

1. Builder stage: Maven compiles and packages the application
2. Runtime stage: Minimal JRE image runs the application

Rebuild after code changes:

```bash
docker build --no-cache -t amhs-ua-test-tool .
```

## Troubleshooting

### Container exits immediately

Inspect the container logs:

```bash
docker logs <container-id>
```

Common issues:
- Missing native libraries in `lib/`
- Incorrect Java version
- Missing configuration files

### GUI does not display

- Ensure the X server is running
- Verify `DISPLAY` is set correctly
- Confirm Docker can access the X server

### Native library errors

If you see native library errors such as `UnsatisfiedLinkError`, ensure the required Isode native files are available in `lib/` and properly mounted.

## Security

- The container runs as root by default. For production, consider running as a non-root user.
- Mount configuration files as read-only when possible.
- Avoid exposing unnecessary ports.

## Cleanup

```bash
docker rmi amhs-ua-test-tool:latest
```

## Support

For application issues, see `README.md`.

For Docker help, see:
- https://docs.docker.com/
