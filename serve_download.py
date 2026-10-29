import os
import socket
import sys
import http.server
import socketserver

PORT = 8080
APK_NAME = "ShubDeep-Calculator-Pro.apk"
APK_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), APK_NAME)

def get_local_ips():
    ips = set()
    try:
        # Connect to an external address to get default outbound IP
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ips.add(s.getsockname()[0])
        s.close()
    except Exception:
        pass

    try:
        hostname = socket.gethostname()
        for ip in socket.gethostbyname_ex(hostname)[2]:
            if not ip.startswith("127."):
                ips.add(ip)
    except Exception:
        pass

    return sorted(list(ips))

HTML_TEMPLATE = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ShubDeep Calculator Pro - Mobile Download</title>
    <style>
        :root {{
            --primary: #2563EB;
            --primary-dark: #1D4ED8;
            --bg: #0B0F19;
            --card-bg: #151D2F;
            --card-border: #24304A;
            --text-main: #FFFFFF;
            --text-sub: #94A3B8;
            --accent: #10B981;
            --warning: #F59E0B;
        }}
        * {{
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
        }}
        body {{
            background: var(--bg);
            color: var(--text-main);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }}
        .card {{
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 24px;
            max-width: 480px;
            width: 100%;
            padding: 32px 24px;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
            text-align: center;
        }}
        .app-icon {{
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, #2563EB, #7C3AED);
            border-radius: 20px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 38px;
            margin-bottom: 20px;
            box-shadow: 0 10px 20px rgba(37, 99, 235, 0.35);
        }}
        h1 {{
            font-size: 24px;
            font-weight: 700;
            margin-bottom: 6px;
            letter-spacing: -0.5px;
        }}
        .badge {{
            display: inline-block;
            background: rgba(16, 185, 129, 0.15);
            color: #34D399;
            font-size: 13px;
            font-weight: 600;
            padding: 4px 12px;
            border-radius: 9999px;
            margin-bottom: 20px;
            border: 1px solid rgba(16, 185, 129, 0.3);
        }}
        p.subtitle {{
            color: var(--text-sub);
            font-size: 15px;
            line-height: 1.5;
            margin-bottom: 28px;
        }}
        .download-btn {{
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            background: linear-gradient(135deg, #2563EB, #1D4ED8);
            color: #FFFFFF;
            text-decoration: none;
            font-size: 17px;
            font-weight: 700;
            padding: 16px 24px;
            border-radius: 16px;
            box-shadow: 0 12px 24px rgba(37, 99, 235, 0.4);
            transition: transform 0.15s, background 0.15s;
        }}
        .download-btn:active {{
            transform: scale(0.97);
        }}
        .file-info {{
            margin-top: 10px;
            font-size: 13px;
            color: var(--text-sub);
        }}
        .instructions {{
            text-align: left;
            background: rgba(11, 15, 25, 0.6);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 18px 20px;
            margin-top: 28px;
        }}
        .instructions h3 {{
            font-size: 14px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            color: #E2E8F0;
            margin-bottom: 12px;
            display: flex;
            align-items: center;
            gap: 6px;
        }}
        .instructions ol {{
            padding-left: 20px;
            color: var(--text-sub);
            font-size: 14px;
            line-height: 1.8;
        }}
        .instructions strong {{
            color: #FFFFFF;
        }}
    </style>
</head>
<body>
    <div class="card">
        <div class="app-icon">🧮</div>
        <h1>ShubDeep Calculator Pro</h1>
        <div class="badge">v1.0.0 Ready &bull; Samsung One UI Optimized</div>
        <p class="subtitle">Scientific Calculator &bull; Live Currency &amp; INR &bull; 24 Unit Converters &bull; Indian GST &amp; Loan EMI</p>

        <a href="/ShubDeep-Calculator-Pro.apk" class="download-btn" download>
            <span>📥</span> Download APK ({size_mb:.1f} MB)
        </a>
        <div class="file-info">File: ShubDeep-Calculator-Pro.apk</div>

        <div class="instructions">
            <h3>📱 Samsung Galaxy Quick Install:</h3>
            <ol>
                <li>Tap <strong>Download APK</strong> above.</li>
                <li>When Samsung Internet / Chrome asks <em>"File might be harmful"</em>, tap <strong>Download anyway</strong>.</li>
                <li>Tap <strong>Open</strong> once downloaded, then tap <strong>Install</strong>.</li>
                <li>If prompted, allow <em>"Install unknown apps"</em> in Samsung Settings.</li>
            </ol>
        </div>
    </div>
</body>
</html>
"""

class CustomHTTPHandler(http.server.SimpleHTTPRequestHandler):
    def end_headers(self):
        self.send_header('Cache-Control', 'no-cache, no-store, must-revalidate')
        self.send_header('Pragma', 'no-cache')
        self.send_header('Expires', '0')
        super().end_headers()

    def do_GET(self):
        if self.path in ('/', '/index.html'):
            if os.path.exists(APK_PATH):
                size_mb = os.path.getsize(APK_PATH) / (1024 * 1024)
            else:
                size_mb = 0.0
            content = HTML_TEMPLATE.format(size_mb=size_mb).encode('utf-8')
            self.send_response(200)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.send_header("Content-Length", str(len(content)))
            self.end_headers()
            self.wfile.write(content)
            return

        if self.path == f"/{APK_NAME}":
            if not os.path.exists(APK_PATH):
                self.send_error(404, "APK file not found")
                return
            file_size = os.path.getsize(APK_PATH)
            self.send_response(200)
            self.send_header("Content-Type", "application/vnd.android.package-archive")
            self.send_header("Content-Length", str(file_size))
            self.send_header("Content-Disposition", f'attachment; filename="{APK_NAME}"')
            self.end_headers()
            with open(APK_PATH, "rb") as f:
                while chunk := f.read(64 * 1024):
                    self.wfile.write(chunk)
            print(f"\n[+] APK successfully transferred to mobile device!")
            return

        return super().do_GET()

def main():
    if not os.path.exists(APK_PATH):
        print(f"[!] Warning: {APK_NAME} not found at {APK_PATH}!")
        return

    try:
        sys.stdout.reconfigure(encoding='utf-8')
    except Exception:
        pass

    ips = get_local_ips()

    print("=" * 70)
    print("   SHUBDEEP CALCULATOR PRO - MOBILE DIRECT DOWNLOAD SERVER")
    print("=" * 70)
    print("\n   Connect your phone to the same Wi-Fi network and open:")
    for ip in ips:
        print(f"      ->  http://{ip}:{PORT}")
    print("\n   Or direct APK download link:")
    for ip in ips:
        print(f"      ->  http://{ip}:{PORT}/{APK_NAME}")
    print("\n" + "=" * 70)
    print(f"   Server listening on port {PORT}... Press Ctrl+C to stop.")
    print("=" * 70 + "\n")

    class ThreadedTCPServer(socketserver.ThreadingTCPServer):
        allow_reuse_address = True

    try:
        with ThreadedTCPServer(("", PORT), CustomHTTPHandler) as httpd:
            httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n[+] Server stopped.")

if __name__ == "__main__":
    main()
