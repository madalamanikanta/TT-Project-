# Frontend API Setup

## Frontend Environment

Set this in Vercel and in your local frontend env file:

```env
VITE_API_BASE_URL=https://smart-internship-skill-matching-portal.onrender.com
VITE_API_TIMEOUT=30000
```

## Notes

- `VITE_API_BASE_URL` must not include `/api`
- The frontend appends `/api` automatically
- The frontend sends the JWT in the `Authorization` header
- The shared API client lives in `src/services/api.ts`

## Deployment Checks

- Add `VITE_API_BASE_URL` in the Vercel project settings
- Redeploy after changing the env var
- Confirm the browser console prints the expected API base URL
- Confirm Network requests go to `https://smart-internship-skill-matching-portal.onrender.com/api/...`

## Common Problems

- Wrong env var name: must be exactly `VITE_API_BASE_URL`
- Missing Vercel env var: requests may fall back to a relative `/api` path
- Backend CORS not allowing the Vercel frontend origin
