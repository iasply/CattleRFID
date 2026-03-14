#!/bin/bash

# Default mode is local if not specified
MODE="local"
KEEP_DOCKER=false

# Simple argument parsing
for arg in "$@"; do
    case $arg in
        --docker)
            MODE="docker"
            ;;
        --local)
            MODE="local"
            ;;
        --keep-docker)
            KEEP_DOCKER=true
            ;;
        --no-cache)
            NO_CACHE=true
            ;;
        *)
            echo "Uso: ./run_all_tests.sh [--local | --docker] [--keep-docker] [--no-cache]"
            echo "  --local       : Roda usando 'php artisan serve' (HTTP, porta 8555)"
            echo "  --docker      : Roda usando 'docker compose' (HTTPS, porta 443 / porta mapeada)"
            echo "  --keep-docker : Mantém o docker rodando após os testes (apenas modo --docker)"
            echo "  --no-cache    : Limpa imagens e volumes do docker antes de iniciar (apenas modo --docker)"
            exit 1
            ;;
    esac
done

echo "Starting test suite in $MODE mode..."
echo "=================================================="
echo "1. Running PHP web module tests..."
echo "=================================================="
cd modulo_web || exit 1
php artisan test
if [ $? -ne 0 ]; then
  echo "PHP tests failed! Aborting."
  exit 1
fi

echo ""
echo "=================================================="
echo "2. Preparing database for integration tests..."
echo "=================================================="
# Garantir que o diretório e o arquivo sqlite existam
mkdir -p database
touch database/database.sqlite

if [ "$MODE" == "docker" ]; then
    if [ "$NO_CACHE" = true ]; then
        echo "Limpando cache do Docker (imagens, volumes, orphans)..."
        docker compose down -v --rmi all --remove-orphans
    fi
    docker compose up -d
    echo "Aguardando serviços do Docker subirem..."
    
    # Check if we can communicate with the container/service
    MAX_RETRIES=10
    COUNT=0
    echo -n "Checking Docker services health"
    while [ $COUNT -lt $MAX_RETRIES ]; do
        if docker compose exec -T laravel php artisan --version > /dev/null 2>&1; then
            echo " ✅"
            break
        fi
        echo -n "."
        sleep 2
        COUNT=$((COUNT + 1))
    done

    if [ $COUNT -eq $MAX_RETRIES ]; then
        echo " ❌"
        echo "Erro: Não foi possível comunicar com os serviços do Docker após $MAX_RETRIES tentativas. Abortando."
        docker compose down -v
        exit 1
    fi

    # Usamos o banco de dados persistente mapeado no volume
    docker compose exec -T laravel php artisan migrate:fresh --seed --force
else
    php artisan migrate:fresh --seed --force
fi
echo "✅ Database seeded with integration test data."


echo ""
echo "=================================================="
echo "3. Starting services and configuring desktop .env..."
echo "=================================================="

# Generate certificates if running with Docker
if [ "$MODE" == "docker" ]; then
    ../generate_dev_ssl.sh
fi

# Configurar o enviroment do desktop (Java) dinamicamente
DESKTOP_ENV_FILE="../modulo_desktop/.env.test"

if [ "$MODE" == "docker" ]; then
    echo "API_BASE_URL=https://localhost/api/desktop" > $DESKTOP_ENV_FILE
    echo "API_WORKSTATION_HASH=WS-XTYBQRG6" >> $DESKTOP_ENV_FILE
    echo "SSL_TRUST_ALL=true" >> $DESKTOP_ENV_FILE
    
    # Path absoluto para o certificado dev gerado
    CERT_PATH=$(readlink -f "../modulo_web/nginx/certs/dev.crt")
    echo "SSL_DEV_CERT_PATH=$CERT_PATH" >> $DESKTOP_ENV_FILE
    
    echo "Ambiente Docker configurado (HTTPS: https://localhost/api)."
else
    # Configuracao HTTP para porta especifica do Artisan Serve
    echo "API_BASE_URL=http://127.0.0.1:8555/api/desktop" > $DESKTOP_ENV_FILE
    echo "API_WORKSTATION_HASH=WS-XTYBQRG6" >> $DESKTOP_ENV_FILE
    echo "SSL_TRUST_ALL=false" >> $DESKTOP_ENV_FILE

    pkill -f "artisan serve" || true
    sleep 1
    php artisan serve --port=8555 > /dev/null 2>&1 &
    SERVER_PID=$!
    echo "Aguardando artisan serve subir (porta 8555)..."
    sleep 3
fi

echo ""
echo "=================================================="
echo "4. Running Java desktop module tests..."
echo "=================================================="
if [ "$MODE" == "docker" ]; then
    cd ../modulo_desktop || exit 1
else
    cd ../modulo_desktop || { kill $SERVER_PID; exit 1; }
fi

mvn test -DexcludedGroups=""
JAVA_TEST_RESULT=$?

echo ""
echo "=================================================="
echo "5. Tearing down services and cleaning up..."
echo "=================================================="

# Remover o .env temporário do desktop
rm -f "$DESKTOP_ENV_FILE"
echo "✅ Arquivo $DESKTOP_ENV_FILE removido."

if [ "$MODE" == "docker" ]; then
    cd ../modulo_web
    if [ "$KEEP_DOCKER" = true ]; then
        echo "Manteve o docker rodando para debug (--keep-docker ativo)."
    else
        echo "Limpando ambiente docker (containers, volumes, orphans)..."
        docker compose down -v --remove-orphans
        echo "✅ Ambiente Docker removido."
    fi
else
    if [ -n "$SERVER_PID" ]; then
        kill $SERVER_PID 2>/dev/null
        echo "✅ Processo 'php artisan serve' encerrado."
    fi
fi

echo ""
if [ $JAVA_TEST_RESULT -ne 0 ]; then
  echo "❌ Java tests failed!"
  exit 1
fi

echo "✅ All tests completed successfully!"
