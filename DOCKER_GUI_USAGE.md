# AMHS UA Test Tool - Docker GUI Usage Guide / Hướng dẫn sử dụng Docker GUI

[English](#english-guide) | [Tiếng Việt](#huong-dan-tieng-viet)

---

<a name="english-guide"></a>
## 🇬🇧 English Guide: Running the AMHS UA Test Tool with GUI on Docker

### Prerequisites
1. **Docker**: Ensure Docker is installed and running on your system.
2. **X Server (Display Server)**: Since this is a GUI application running inside a Docker container, it needs an X server to render its windows on your host machine.

### Step 1: Build the Docker Image
First, build the Docker image from the source code.
```bash
docker build -t amhs-ua-test-tool .
```

### Step 2: Set up X Server and Run the Container

**For Windows:**
1. Install an X server like [VcXsrv](https://sourceforge.net/projects/vcxsrv/) or [Xming](https://sourceforge.net/projects/xming/).
2. Run VcXsrv/Xming. When configuring VcXsrv (XLaunch), make sure to check **"Disable access control"** so Docker can communicate with it.
3. Run the container:
```bash
docker run --rm -it -e DISPLAY=host.docker.internal:0 amhs-ua-test-tool
```
*(If `host.docker.internal` doesn't work, replace it with your Windows machine's IPv4 address).*

**For Linux:**
1. Allow local connections to the X server:
```bash
xhost +local:docker
```
2. Run the container, mounting the X11 socket and passing your DISPLAY variable:
```bash
docker run --rm -it -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix amhs-ua-test-tool
```

**For macOS:**
1. Install [XQuartz](https://www.xquartz.org/).
2. Open XQuartz preferences, go to the "Security" tab, and check **"Allow connections from network clients"**. Restart XQuartz.
3. Add your local IP to xhost:
```bash
xhost +localhost
```
4. Run the container:
```bash
docker run --rm -it -e DISPLAY=host.docker.internal:0 amhs-ua-test-tool
```

### Step 3: Verify Application
The Java GUI (Swing) application should now open on your host machine. Any files/logs created by the application inside the container will be lost when the container is stopped unless you map a volume (e.g., `-v /your/local/dir:/app/output`).

---

<a name="huong-dan-tieng-viet"></a>
## 🇻🇳 Hướng dẫn Tiếng Việt: Chạy AMHS UA Test Tool (có giao diện) trên Docker

### Yêu cầu hệ thống
1. **Docker**: Đảm bảo Docker đã được cài đặt và đang chạy.
2. **X Server (Máy chủ hiển thị)**: Vì công cụ này là một ứng dụng có giao diện (GUI) chạy trong container, nó cần một X Server để có thể hiển thị cửa sổ ứng dụng lên màn hình máy thật của bạn.

### Bước 1: Build (Tạo) Docker Image
Đầu tiên, hãy tạo Docker image từ mã nguồn.
```bash
docker build -t amhs-ua-test-tool .
```

### Bước 2: Cài đặt X Server và Chạy Container

**Dành cho Windows:**
1. Cài đặt một X server như [VcXsrv](https://sourceforge.net/projects/vcxsrv/) hoặc [Xming](https://sourceforge.net/projects/xming/).
2. Chạy VcXsrv/Xming (thông qua XLaunch). Ở bước cấu hình, hãy đảm bảo chọn mục **"Disable access control"** để Docker có quyền truy cập và hiển thị giao diện.
3. Chạy lệnh sau để khởi động container:
```bash
docker run --rm -it -e DISPLAY=host.docker.internal:0 amhs-ua-test-tool
```
*(Nếu `host.docker.internal` không hoạt động, hãy thay bằng địa chỉ IP của máy tính Windows của bạn).*

**Dành cho Linux:**
1. Cấp quyền truy cập cho máy chủ X:
```bash
xhost +local:docker
```
2. Chạy container bằng cách gán biến môi trường DISPLAY và thư mục socket của X11:
```bash
docker run --rm -it -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix amhs-ua-test-tool
```

**Dành cho macOS:**
1. Cài đặt [XQuartz](https://www.xquartz.org/).
2. Mở phần Cài đặt (Preferences) của XQuartz, chuyển đến tab "Security" và chọn **"Allow connections from network clients"**. Khởi động lại XQuartz.
3. Cho phép kết nối:
```bash
xhost +localhost
```
4. Chạy container:
```bash
docker run --rm -it -e DISPLAY=host.docker.internal:0 amhs-ua-test-tool
```

### Bước 3: Xác nhận
Giao diện ứng dụng Java sẽ hiển thị trên máy tính của bạn. Lưu ý rằng mọi dữ liệu do ứng dụng tạo ra trong container sẽ bị mất khi đóng container, trừ khi bạn mount (gắn) một thư mục từ máy thật vào container (ví dụ thêm: `-v /thu/muc/cua/ban:/app/output`).
