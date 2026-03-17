import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "미리 알림",
  description: "Apple Reminders Clone",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body className="antialiased">
        {children}
      </body>
    </html>
  );
}
