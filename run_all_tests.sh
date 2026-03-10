#!/bin/bash

# Default mode is local if not specified
MODE="local"

if [ "$1" == "--docker" ]; then
    MODE="docker"
elif [ "$1" == "--local" ]; then
    MODE="local"
elif [ -n "$1" ]; then
    echo "Uso: ./run_all_tests.sh [--local | --docker]"
    echo "  --local : Roda usando 'php artisan serve' (HTTP, porta 8555)"
    echo "  --docker: Roda usando 'docker compose' (HTTPS, porta 443 / porta mapeada)"
    exit 1
fi

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
if [ "$MODE" == "docker" ]; then
    # Para o docker, precisamos subir os containers primeiro para rodar as migrations dentro deles
    docker compose up -d
    echo "Aguardando serviços do Docker subirem..."
    sleep 5
    docker compose exec -T laravel php artisan migrate:fresh --seed --force
else
    php artisan migrate:fresh --seed --force
fi
echo "✅ Database seeded with integration test data."

echo ""
echo "=================================================="
echo "3. Starting services and configuring desktop .env..."
echo "=================================================="

# Configurar o enviroment do desktop (Java) dinamicamente
DESKTOP_ENV_FILE="../modulo_desktop/.env"

if [ "$MODE" == "docker" ]; then
    # Configuracao HTTPS para Docker (Localhost porta publica)
    echo "API_BASE_URL=https://localhost/api" > $DESKTOP_ENV_FILE
    echo "API_WORKSTATION_HASH=WS-XTYBQRG6" >> $DESKTOP_ENV_FILE
    echo "SSL_TRUST_ALL=true" >> $DESKTOP_ENV_FILE
    
    echo "Ambiente Docker configurado (HTTPS: https://localhost/api)."
else
    # Configuracao HTTP para porta especifica do Artisan Serve
    echo "API_BASE_URL=http://127.0.0.1:8555/api" > $DESKTOP_ENV_FILE
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
echo "5. Tearing down services..."
echo "=================================================="
if [ "$MODE" == "docker" ]; then
    cd ../modulo_web
    # Mantém o docker rodando ou derruba? Geralmente derrubamos pos teste
    # docker compose down
    echo "Manteve o docker rodando para debug. Use 'docker compose down' manualmente se quiser desligar."
else
    kill $SERVER_PID 2>/dev/null
    echo "Processo 'php artisan serve' encerrado."
fi

echo ""
if [ $JAVA_TEST_RESULT -ne 0 ]; then
  echo "❌ Java tests failed!"
  exit 1
fi

echo "✅ All tests completed successfully!"
