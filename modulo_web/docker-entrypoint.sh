#!/bin/bash
set -e

# ──────────────────────────────────────────────
# CattleRFID – Docker Entrypoint
# ──────────────────────────────────────────────

echo "🐄 [CattleRFID] Starting entrypoint..."

APP_DIR="/var/www/html"
PUBLIC_VOLUME="/var/www/public_volume"
CERTS_DIR="/etc/nginx/certs"

# ── 1. Certificados SSL ────────────────────────
# Se não houver fullchain.pem no volume de certs, gera um self-signed.
# Em produção real, substitua por certs reais (Let's Encrypt, etc).
if [ ! -f "${CERTS_DIR}/fullchain.pem" ] || [ ! -f "${CERTS_DIR}/privkey.pem" ]; then
    echo "🔒 No SSL certs found — generating self-signed certificate..."
    mkdir -p "${CERTS_DIR}"
    openssl req -x509 \
        -nodes \
        -days 365 \
        -newkey rsa:2048 \
        -keyout "${CERTS_DIR}/privkey.pem" \
        -out    "${CERTS_DIR}/fullchain.pem" \
        -subj "/C=BR/ST=SP/L=SaoPaulo/O=CattleRFID/CN=localhost" \
        -extensions v3_ca \
        -addext "subjectAltName=DNS:localhost,IP:127.0.0.1" \
        2>/dev/null
    echo "✅ Self-signed certificate generated (valid 365 days)."
    echo "   ⚠  Replace with real certs in nginx/certs/ for production."
else
    echo "🔒 SSL certs found — skipping generation."
fi

# ── 2. Garantir que o banco SQLite existe ─────
DB_PATH="${APP_DIR}/database/database.sqlite"
if [ ! -f "$DB_PATH" ]; then
    echo "📀 Creating SQLite database..."
    mkdir -p "$(dirname "$DB_PATH")"
    touch "$DB_PATH"
fi

# ── 3. Permissões ─────────────────────────────
echo "🔐 Setting permissions..."
chown -R www-data:www-data \
    "${APP_DIR}/storage" \
    "${APP_DIR}/bootstrap/cache" \
    "${APP_DIR}/database"
chmod -R 775 \
    "${APP_DIR}/storage" \
    "${APP_DIR}/bootstrap/cache"

# ── 4. Gerar APP_KEY se ausente ───────────────
if ! grep -qE "^APP_KEY=base64:.{40,}" "${APP_DIR}/.env" 2>/dev/null; then
    echo "🔑 Generating APP_KEY..."
    php "${APP_DIR}/artisan" key:generate --force
fi

# ── 5. Migrations ─────────────────────────────
echo "🗄️  Running migrations..."
php "${APP_DIR}/artisan" migrate --force

# ── 6. Cache de configuração/rotas/views ──────
echo "⚡ Optimizing application..."
php "${APP_DIR}/artisan" config:cache
php "${APP_DIR}/artisan" route:cache
php "${APP_DIR}/artisan" view:cache

# ── 7. Sincronizar public/ para volume compartilhado com nginx ─────────
# Resolve o problema de nginx não conseguir servir os arquivos de public/.
# rsync copia apenas diff (eficiente em restarts).
if [ -d "$PUBLIC_VOLUME" ]; then
    echo "📂 Syncing public/ → shared volume..."
    rsync -a --delete \
        "${APP_DIR}/public/" \
        "${PUBLIC_VOLUME}/"
    chown -R www-data:www-data "$PUBLIC_VOLUME"
    echo "✅ public/ synced."
fi

echo "🚀 [CattleRFID] Ready. Starting PHP-FPM..."
exec "$@"
