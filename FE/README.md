# FE Setup (Local + Vercel)

This frontend is configured to work with environment-based API URL so it can run locally and deploy cleanly to Vercel.

## Local development

1. Install dependencies

	npm install

2. Ensure local env file exists at `.env.local`

	VITE_API_URL=http://localhost:8080/api

3. Start dev server

	npm run dev

## Production on Vercel

Set this variable in Vercel Project Settings -> Environment Variables:

- `VITE_API_URL=https://uis-sale-management-production.up.railway.app/api`

Then redeploy.

## API base behavior

- If `VITE_API_URL` is set, frontend uses it.
- If `VITE_API_URL` is missing and app runs in dev mode, it falls back to `http://localhost:8080/api`.
- If `VITE_API_URL` is missing in production build, fallback is `/api`.

Recommended: always set `VITE_API_URL` on Vercel production to Railway public URL.
