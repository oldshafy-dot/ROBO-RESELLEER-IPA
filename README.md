# ROBO Android

Separate Android implementation of the existing ROBO reseller portal. The iOS Swift source is not used as Android code; this project is a new Kotlin + Jetpack Compose codebase.

## Same backend
Base API: `https://deploy-straight-up.lovable.app/api/public/`

Routes: `POST /auth/login`, `GET /me`, `GET /products`, `POST /get-key`, `GET /my-keys`, `GET /updates`, `GET /health`.

The Android app stores the same bearer token locally and uses the same backend/database, so iOS and Android accounts, balance, products and keys stay server-synchronized.

## Build on GitHub
Push this folder to the APK Build repository. GitHub Actions -> **Build APK** -> Run workflow. The generated debug APK is uploaded as an artifact.
