// middleware.js or middleware.ts
import { NextRequest, NextResponse } from 'next/server';

export function middleware(req: NextRequest) {
  const origin = req.headers.get("origin") || "";
  const allowed = (process.env.ALLOWED_ORIGINS || "")
    .split(",")
    .map(o => o.trim());

  const res = NextResponse.next();

  // Allow only the matched origin
  if (allowed.includes(origin)) {
    res.headers.set("Access-Control-Allow-Origin", origin);
  }

  res.headers.set('Access-Control-Allow-Methods', 'GET,POST,PUT,DELETE,OPTIONS');
  res.headers.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');
  return res;
}

export const config = {
  matcher: ['/api/playlists', '/api/recent-song-played'], 
};
