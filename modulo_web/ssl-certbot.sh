#!/bin/bash
set -euo pipefail

# ══════════════════════════════════════════════════════════════════
#  CattleRFID – SSL Certbot Manager
#  Cria ou renova certificados Let's Encrypt via certbot standalone
#
#  Uso:
#    sudo ./ssl-certbot.sh -d example.com -e admin@example.com
#    sudo ./ssl-certbot.sh -d example.com -e admin@example.com --renew
# ══════════════════════════════════════════════════════════════════

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'
log()  { echo -e "${CYAN}[SSL]${NC} $*"; }
ok()   { echo -e "${GREEN}[ ✓ ]${NC} $*"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $*"; }
die()  { echo -e "${RED}[ERR]${NC} $*" >&2; exit 1; }

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VOLUME_NAME="modulo_web_ssl_certs"
CERTS_MOUNT="/etc/nginx/certs"
DOMAIN=""
EMAIL=""
RENEW_ONLY=false

# ── Argumentos ───────────────────────────────────────────────────
while [[ $# -gt 0 ]]; do
    case "$1" in
        -d|--domain) DOMAIN="$2"; shift 2 ;;
        -e|--email)  EMAIL="$2";  shift 2 ;;
        --renew)     RENEW_ONLY=true; shift ;;
        *) die "Argumento inválido: $1  |  Uso: $0 -d DOMINIO -e EMAIL [--renew]" ;;
    esac
done

# ── Modo interativo se não passado como arg ───────────────────────
[[ -z "$DOMAIN" ]] && read -rp "Domínio: " DOMAIN
[[ -z "$EMAIL"  ]] && read -rp "E-mail:  " EMAIL
[[ -z "$DOMAIN" || -z "$EMAIL" ]] && die "Domínio e e-mail são obrigatórios."

command -v certbot &>/dev/null || die "certbot não encontrado. Instale com: sudo apt install certbot"

echo -e "\n${BOLD}${CYAN}🔒 CattleRFID – Certbot SSL${NC}  (domínio: ${DOMAIN})\n"

# ── Parar nginx para liberar porta 80 ────────────────────────────
cd "$SCRIPT_DIR"
log "Parando nginx (porta 80 necessária para desafio)..."
docker compose stop nginx 2>/dev/null || warn "nginx não estava ativo."

# ── Certbot ──────────────────────────────────────────────────────
if [[ "$RENEW_ONLY" == true ]]; then
    log "Renovando certificado para ${DOMAIN}..."
    certbot renew --cert-name "$DOMAIN" --non-interactive
else
    log "Solicitando certificado para ${DOMAIN}..."
    certbot certonly \
        --standalone \
        --non-interactive \
        --agree-tos \
        --email  "$EMAIL" \
        --domain "$DOMAIN" \
        --keep-until-expiring
fi

ok "Certificado obtido/renovado."

# ── Injetar certs no volume Docker ───────────────────────────────
log "Copiando certs para o volume Docker (${VOLUME_NAME})..."
docker run --rm \
    -v "/etc/letsencrypt/live/${DOMAIN}:/src:ro" \
    -v "${VOLUME_NAME}:${CERTS_MOUNT}" \
    alpine:latest sh -c "
        cp /src/fullchain.pem ${CERTS_MOUNT}/fullchain.pem &&
        cp /src/privkey.pem   ${CERTS_MOUNT}/privkey.pem   &&
        chmod 644 ${CERTS_MOUNT}/fullchain.pem              &&
        chmod 600 ${CERTS_MOUNT}/privkey.pem
    "
ok "Certs injetados no volume."

# ── Subir nginx ───────────────────────────────────────────────────
log "Reiniciando nginx..."
docker compose up -d nginx
ok "nginx no ar com certificado válido."

# ── Resumo ───────────────────────────────────────────────────────
EXPIRY=$(openssl x509 -enddate -noout \
         -in "/etc/letsencrypt/live/${DOMAIN}/fullchain.pem" | cut -d= -f2)

echo ""
echo -e "${BOLD}${GREEN}✅  https://${DOMAIN}${NC}"
echo -e "   Válido até: ${BOLD}${EXPIRY}${NC}"
echo ""
echo -e "${YELLOW}Renovação automática (cron):${NC}"
echo -e "  0 3 * * * sudo ${SCRIPT_DIR}/ssl-certbot.sh -d ${DOMAIN} -e ${EMAIL} --renew"
echo ""
