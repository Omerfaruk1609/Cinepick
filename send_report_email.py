import smtplib
import os
import argparse
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from email.mime.application import MIMEApplication

def send_email(sender_email, sender_password, recipient_email, report_file_path):
    smtp_server = os.environ.get("SMTP_SERVER", "smtp.gmail.com")
    smtp_port = int(os.environ.get("SMTP_PORT", 587))
    
    if not os.path.exists(report_file_path):
        print(f"Hata: Rapor dosyasi bulunamadi: {report_file_path}")
        return False
        
    with open(report_file_path, "r", encoding="utf-8") as f:
        report_content = f.read()
        
    msg = MIMEMultipart()
    msg['From'] = sender_email
    msg['To'] = recipient_email
    msg['Subject'] = "CinePick Projesi Kapsamli Analiz ve Yol Haritasi Raporu"
    
    body = f"Merhaba,\n\nCinePick projesine ait yapılanlar ve yapılacaklar (Docker dahil) analiz raporu ekte ve asagida sunulmustur.\n\n---\n\n{report_content}"
    msg.attach(MIMEText(body, 'plain', 'utf-8'))
    
    # Attach markdown file as well
    with open(report_file_path, "rb") as f:
        part = MIMEApplication(f.read(), Name=os.path.basename(report_file_path))
        part['Content-Disposition'] = f'attachment; filename="{os.path.basename(report_file_path)}"'
        msg.attach(part)

    try:
        print(f"SMTP sunucusuna baglaniliyor ({smtp_server}:{smtp_port})...")
        server = smtplib.SMTP(smtp_server, smtp_port)
        server.starttls()
        server.login(sender_email, sender_password)
        server.sendmail(sender_email, recipient_email, msg.as_string())
        server.quit()
        print(f"E-posta basariyla {recipient_email} adresine gonderildi!")
        return True
    except Exception as e:
        print(f"E-posta gonderim hatasi: {e}")
        return False

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="CinePick Rapor E-Posta Gonderici")
    parser.add_argument("--sender", help="Gonderici E-Posta adresi", default=os.environ.get("SMTP_USER"))
    parser.add_argument("--password", help="Gonderici E-Posta sifresi / App Password", default=os.environ.get("SMTP_PASS"))
    parser.add_argument("--recipient", help="Alici E-Posta adresi", default="hacicirak10@gmail.com")
    parser.add_argument("--report", help="Rapor dosya yolu", default="cinepick_project_analysis_report.md")
    
    args = parser.parse_args()
    
    if not args.sender or not args.password:
        print("Uyarı: Gonderici e-posta veya sifre belirtilmedi.")
        print("Kullanim: python send_report_email.py --sender SIZIN_MAILINIZ@gmail.com --password UYGULAMA_SIFRESI --recipient hacicirak10@gmail.com")
    else:
        send_email(args.sender, args.password, args.recipient, args.report)
